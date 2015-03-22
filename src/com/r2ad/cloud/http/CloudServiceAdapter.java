/**
 DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 Copyright (c) 2010, R2AD, LLC
 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
    * Neither the name of the R2AD, LLC nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.r2ad.cloud.http;

import android.content.Context;
import android.util.Log;

import java.security.KeyStore;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import com.r2ad.cloud.actions.CloudAction;
import com.r2ad.security.utils.Encoder;
import com.r2ad.security.utils.MySSLSocketFactory;

public abstract class CloudServiceAdapter implements CloudService {
	
	protected static final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	
	private long					m_uid = CloudService.NO_ID;     
	private String 					m_name;	
	private String 					m_url;
	private CloudAccount			m_account;
	private Date					m_accessed;		
	private ArrayList<CloudAction> 	m_actions;
	private HttpClient httpclient = null;
	
	// Extra values that may or may not be needed:
	static final String keystoreFilename = "keystore.android";
	KeyStore trustStore = null;
	SSLSocketFactory socketFactory = null;
	Context context = null;
	static final String urlEncodedAccount = "YWNjb3JkczpwbGF0Zm9ybQ=="; // accords:platform
	
	
	// *******************************************************
	// Implement the CloudService Interface
	// *******************************************************	
	
    @Override
    public CloudAction[] getActions() {
    	CloudAction[] result = new CloudAction[m_actions.size()];
    	m_actions.toArray(result);
    	return result;
    }
    
	@Override
	public CloudAccount getAccount() {
		return m_account;
	}
	
	@Override
	public void setAccount(CloudAccount account) {
		this.m_account = account;
	}		
	
	@Override
	public Date getLastAccessed() {
		return m_accessed;
	}
		
	@Override
	public String getName() {
		return m_name;
	}
	
	@Override
	public void setName(String name) {
		this.m_name = name;
	}		
	
	@Override
	public long getUID() {
		return m_uid;
	}
	
	@Override
	public void setUID(long uid) {
		this.m_uid = uid;
	}	
	
	@Override
	public String getURL() {
		return m_url;
	}
	
	@Override
	public void setURL(String url) {
		if (url != null) {
			if (!url.toLowerCase().startsWith("http")) {
				url = HTTPPrefix + url;				
		    }
		}		
		this.m_url = url;
	}
	
	@Override
	public boolean isAvailable() {
		return false;
	}	
	
	@Override
	public String toString() {
		return "CSA:" + getType() + " " + getName(); // add if verbose on:  + " " + getURL()
	}	
	
	// *******************************************************
	// Abstract Class Method
	// *******************************************************	
	
	protected abstract String[] getHeaderInfo();
	
	// *******************************************************
	// Convenience Methods
	// *******************************************************	
	
    protected void addAction(CloudAction action) {
    	m_actions.add(action);
    }
    
    protected void removeAction(CloudAction action) {
    	m_actions.remove(action);
    }
    	
	protected HttpGet generateHeader(String url) {
		HttpGet result = new HttpGet(url);
		String[] info = getHeaderInfo();
		for (int i = 0; i < info.length; i+=2) {
		    result.addHeader(info[i], info[i+1]);
		}
		return result;		
	}

    public HttpClient getNewHttpClient() {
    	//
    	//TODO: consider a singleton
    	// http://stackoverflow.com/questions/10174004/how-to-use-one-httpclient-per-application
    	//
        //if ( httpclient != null) {
	        try {
	            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
	            trustStore.load(null, null);
	
	            SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
	            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
	
	            HttpParams params = new BasicHttpParams();
	            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
	            HttpProtocolParams.setContentCharset(params, org.apache.http.protocol.HTTP.UTF_8);
	
	            SchemeRegistry registry = new SchemeRegistry();
	            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
	            registry.register(new Scheme("https", sf, 443));
	            registry.register(new Scheme("https", sf, 8094));

	            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
	
	            httpclient = new DefaultHttpClient(ccm, params);
	        } catch (Exception e) {
	        	httpclient =  new DefaultHttpClient();
	        }
        //};
        return httpclient;
    }
    
	protected HttpResponse getResponse(String url) {
		HttpResponse result = null;
	    Log.d(toString(), "getResponse:  " + url );
		
		try {
			// Old way:
			//DefaultHttpClient httpclient = new DefaultHttpClient();
			// New way to support CO test site:
			//
			HttpClient httpclient = getNewHttpClient();
			HttpGet get = generateHeader(url);
			
			String username = getAccount().getUsername();			
			if (username != null && username.length() > 0) {
				// old way:
			    //httpclient.getCredentialsProvider().setCredentials(new AuthScope(null, -1),
                //    new UsernamePasswordCredentials(username, getAccount().getUserToken()));
			    //
				// consider THIS TODO: http://hc.apache.org/httpclient-3.x/authentication.html
				//UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, getAccount().getUserToken());
				String urlEncodedAccount2 = "cGx1Z2Zlc3QyMDEzOnBsdWdmZXN0MjAxMw=="; // plugfest2013:plugfest2013

				get.addHeader("Authorization", "Basic " + urlEncodedAccount2);
				get.addHeader("Accept", "occi/text");

			    Log.d(toString(), "Username:  " + getAccount().getUsername() );
			    Log.d(toString(), "Password:  " + getAccount().getUserToken() );
			    urlEncodedAccount2 = Encoder.Base64Encode(username + ":" + getAccount().getUserToken());
			    Log.d(toString(), "encoded value (test using on-line decoder) : " + urlEncodedAccount2);
			} 
			HttpResponse httpResp = httpclient.execute(get);			
			int response = httpResp.getStatusLine().getStatusCode();
			Log.d(toString(), "getResponse: HttpResponse is " + response + " for URL:" + url);
		    if (response == 200 || response == 204) {
		    	result = httpResp;
		    } 
		} catch (Throwable t) {
		} 
		return result;		
	}	
	
	protected HttpResponse getAuthResponse(String url) {
		HttpResponse result = null;
		Log.d(toString(), "getAuthResponse testing URL: " + url);
	    //var digestedPassPhrase = Encoder.sign("{connection.credentials}");
	    //var credentials64 = Base64.encode("{connection.user}:{digestedPassPhrase}".getBytes());
	    //var rubyAuthHeader = HttpHeader {
	    //       name: "Authorization",
	    //       value:"Basic {credentials64}"
	    //   };	 		
		try {			
			//OLD: DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpClient httpclient = getNewHttpClient();
			
			HttpGet get = generateHeader(url);
			String username = getAccount().getUsername();			
			if (username != null && username.length() > 0) {
			    String urlEncodedAccount2 = Encoder.Base64Encode(username + ":" + getAccount().getUserToken());

				get.addHeader("Authorization", "Basic " + urlEncodedAccount2);

	            //
			    // httpclient.getCredentialsProvider().setCredentials(new AuthScope(null, -1),
                //    new UsernamePasswordCredentials(username, getAccount().getUserToken()));
			    Log.d(toString(), "Using Credentials " + username + " " + getAccount().getUserToken());
			    Log.d(toString(), "Basic Auth encoding: " + urlEncodedAccount2);
			    
			    
			} 
			HttpResponse httpResp = httpclient.execute(get);			
			int response = httpResp.getStatusLine().getStatusCode();
			Log.d(toString(), "URL is: " + url);
			Log.d(toString(), url + " HttpResponse code: " + response);
		    if (response == 200 || response == 204) {
		    	result = httpResp;
		    } 
		} catch (Throwable t) {
		} 
		return result;		
	}		

}
