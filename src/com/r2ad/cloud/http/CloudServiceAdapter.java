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

import android.util.Log;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.r2ad.cloud.actions.CloudAction;
import com.r2ad.security.utils.Encoder;

public abstract class CloudServiceAdapter implements CloudService {
	
	protected static final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	
	private long					m_uid = CloudService.NO_ID;     
	private String 					m_name;	
	private String 					m_url;
	private CloudAccount			m_account;
	private Date					m_accessed;		
	private ArrayList<CloudAction> 	m_actions;
	
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
		if (url != null && !url.startsWith(HTTP)) {
			url = HTTP + url;
		}		
		this.m_url = url;
	}
	
	@Override
	public boolean isAvailable() {
		return false;
	}	
	
	@Override
	public String toString() {
		return getType() + " " + getName() + " " + getURL();
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
	
	protected HttpResponse getResponse(String url) {
		HttpResponse result = null;
		try {			
			DefaultHttpClient httpclient = new DefaultHttpClient();
			String username = getAccount().getUsername();			
			if (username != null && username.length() > 0) {
			    httpclient.getCredentialsProvider().setCredentials(new AuthScope(null, -1),
                    new UsernamePasswordCredentials(username, getAccount().getUserToken()));
			    Log.d(toString(), "Using Credentials " + username + " " + getAccount().getUserToken());
			} 
			HttpGet get = generateHeader(url);
			HttpResponse httpResp = httpclient.execute(get);			
			int response = httpResp.getStatusLine().getStatusCode();
			Log.d(toString(), url + " HttpResponse is " + response);
		    if (response == 200 || response == 204) {
		    	result = httpResp;
		    } 
		} catch (Throwable t) {
		} 
		return result;		
	}	
	
	protected HttpResponse getAuthResponse(String url) {
		HttpResponse result = null;
	    //var digestedPassPhrase = Encoder.sign("{connection.credentials}");
	    //var credentials64 = Base64.encode("{connection.user}:{digestedPassPhrase}".getBytes());
	    //var rubyAuthHeader = HttpHeader {
	    //       name: "Authorization",
	    //       value:"Basic {credentials64}"
	    //   };	 		
		try {			
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpGet get = generateHeader(url);
			String username = getAccount().getUsername();			
			if (username != null && username.length() > 0) {
				// We might need to digest (hash) the password:
			    String digestedCredentials=username + ":" + Encoder.sign(getAccount().getUserToken());
			    String credentials=username + ":" + getAccount().getUserToken();
	            String credentials64 = Base64.encodeBase64String(credentials.getBytes());
	            // Add the encoded credentials to the header:
	            get.addHeader("Authorization", "Basic " + credentials64);
	            Log.d(toString(), "Authorization header is: " + credentials64);
			    httpclient.getCredentialsProvider().setCredentials(new AuthScope(null, -1),
                    new UsernamePasswordCredentials(username, getAccount().getUserToken()));
			    Log.d(toString(), "Using Credentials " + username + " " + getAccount().getUserToken());
			} 
			HttpResponse httpResp = httpclient.execute(get);			
			int response = httpResp.getStatusLine().getStatusCode();
			Log.d(toString(), url + " HttpResponse is " + response);
		    if (response == 200 || response == 204) {
		    	result = httpResp;
		    } 
		} catch (Throwable t) {
		} 
		return result;		
	}		

}
