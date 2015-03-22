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
package com.r2ad.cloud.occi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.KeyStore;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
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

import android.content.Context;
import android.util.Log;

import com.r2ad.security.utils.MySSLSocketFactory;
import com.r2ad.cloud.model.CloudComputeType;

/**
 * CloudComputer represents an OCCI compute resource.  This class provide methods
 * to create and update and compute resource.
 * @author behrens, moolenaar
 *
 */
public class CloudComputer {

	public static enum TYPE { CDMI, OCCI };
	private String 			m_url;
	private CloudComputeType m_Compute;
	private boolean			parsedComputers;
	static String TAG = "CloudComputer-->";
	
	// Extra values that may or may not be needed:
	static final String keystoreFilename = "keystore.android";
	KeyStore trustStore = null;
	SSLSocketFactory socketFactory = null;
	Context context = null;
	String  urlEncodedAccount = "YWNjb3JkczpwbGF0Zm9ybQ=="; // previous to plugfest 2013


	/**
	 * Create a CloudComputer instance with minimum required arguments to be valid.
	 * @param url the URL for this service
	 * @param wrapper the compute resource wrapper object
	 */
	public CloudComputer(String url, CloudComputeType wrapper) {
		this.m_Compute = wrapper;
		this.m_url = url;
	}	
	
    public HttpClient getNewHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));
            registry.register(new Scheme("https", sf, 8094));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }	
	
	/** 
	 * This sends an OCCI compliant POST command to create a resource.
	 */
	private HttpResponse sendCreateComputer() {
		HttpResponse result = null;
        Log.i(TAG,"sendCreateComputer to:  "+ m_url);
		
		try {
			//
			// Use this client instead of default one! June 2013
			//
			HttpClient httpclient = getNewHttpClient();
			HttpPost verb = new HttpPost(m_url);
			verb.addHeader("User-Agent", "occi-client/1.0");
			verb.addHeader("Content-Type", "text/occi");
			verb.addHeader("Accept", "text/occi");
			
			// TODO: Replace with input values
			String urlEncodedAccount2 = "cGx1Z2Zlc3QyMDEzOnBsdWdmZXN0MjAxMw=="; // plugfest2013
			verb.addHeader("Authorization", "Basic " + urlEncodedAccount2);
	        Log.e(TAG,"Authorization: Basic "+ urlEncodedAccount2);

			
	        //
	        // Note: For this demo, put OCCI message in header.
	        //StringBuffer payload = new StringBuffer();
	        //
	        verb.addHeader("Category", "compute");
	        verb.addHeader("Category", "compute; scheme=\"http://schemas.ogf.org/occi/infrastructure#\"; class=\"kind\"");
	        verb.addHeader("X-OCCI-Attribute", "occi.compute.hostname="+m_Compute.getTitle());
	        verb.addHeader("X-OCCI-Attribute", "occi.compute.memory="+ m_Compute.getMemory());
	        verb.addHeader("X-OCCI-Attribute", "occi.compute.cores="+ m_Compute.getCores());
	        verb.addHeader("X-OCCI-Attribute", "occi.compute.state="+ m_Compute.getStatusAsString());						


			HttpResponse httpResp = httpclient.execute(verb);			
			int response = httpResp.getStatusLine().getStatusCode();
		    if (response == 200 || response == 204 || response == 201) {
		    	result = httpResp;
		    } else {
		        Log.e(TAG,"Bad Repsonse, code:  "+ response);
		    }
		} catch (Throwable t) {
		} 
		return result;		
	}
	
	
	public void createComputer() {
		if (!parsedComputers) {
			parsedComputers = true;
			HttpResponse httpResp = sendCreateComputer();
			if (httpResp != null) {
			   BufferedReader reader = null;
			   try {
				   reader = new BufferedReader(new InputStreamReader(httpResp.getEntity().getContent(), "UTF-8"));
			   } catch (Throwable e) {
			   } 
			   
			   if (reader != null) {	
			      // TODO.
				  // Output should be checked and THEN the new computer should be 
				  // flagged as validated, vice pending.
				  // A pending computer could be re-sent potentially, or canceled if
				  // a response never comes back.				
				  //
				  // For now, just debug out the returned block:
				  //
				  try {
					  String line;
				      while((line = reader.readLine()) != null) {
					      Log.d(TAG, "response: " + line);
				      }
				    } catch (Exception e) {
				        Log.d(TAG, e.getMessage());
				    }					
				}			
			}
		}
	}
	
	
}
