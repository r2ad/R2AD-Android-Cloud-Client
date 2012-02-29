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

import org.apache.http.HttpResponse;

import com.r2ad.cloud.actions.ActionObserver;
import com.r2ad.cloud.actions.CloudAction;
import com.r2ad.cloud.http.CloudServiceAdapter;
import com.r2ad.cloud.model.CloudComputeType;
import com.r2ad.cloud.model.CloudStorageType;
import com.r2ad.cloud.model.CloudTypeMap;

public class OCCIService extends CloudServiceAdapter {
	
	public static final String[] OCCI_HEADERS = {"User-Agent", "occi-client/1.0", 
		"Content-Type", "text/occi", "Accept", "*/*"};		
	public static final String TAG = "OCCIService";
	private static final String TYPE = "OCCI";
	private boolean m_retrieved = false;
	
	public OCCIService() {
		init();
	}
	
	// *******************************************************
	// Implement abstract methods from CloudServiceAdapter
	// *******************************************************
	
	@Override
	public boolean authenticate() {	
		return getResponse(getURL()) != null;
	}			
		
	@Override
	public void disposeService() {	
		//TODO - Remove all service items from CloudTypeMap
	}			
	
	@Override
	public String getType() {
		return TYPE;
	}				
	
	@Override
	public boolean isAvailable() {
		return true;
	}
	
	@Override
	public void refresh() {	
		CloudTypeMap.clear(this);
		m_retrieved = false;
		retrieve();
	}
	
	@Override
	public void retrieve() {
		if (!m_retrieved) {
		    m_retrieved = true;
		    retrieveComputers();
		    //retrieveStorage();
		}
	}
	
	@Override
	protected String[] getHeaderInfo() {
		return OCCI_HEADERS;		
	}
	
	// *******************************************************
	// OCCI Service Specific Methods
	// *******************************************************
	
	private void init() {	
	}
	
	private void retrieveComputers() {
		HttpResponse httpResp = getResponse(getURL());
		if (httpResp != null) {
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(httpResp.getEntity().getContent(), UTF8));
			} catch (Throwable e) {
			} 
			if (reader != null) {	
				String[] compURLS = ParseOcciTop.parseComputers(reader, getURL());
				if (compURLS != null) {
					for (int i = 0; i < compURLS.length; i++) {
						retrieveComputer(compURLS[i]);
					}
				}
			}			
		}
	}	
	
	private void retrieveComputer(String url) {
		HttpResponse httpResp = getResponse(url);
		if (httpResp != null) {
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(httpResp.getEntity().getContent(), UTF8));
			} catch (Throwable e) {
			} 
			if (reader != null) {	
				CloudComputeType comp = ParseOcciTop.parseComputer(url, reader);
				if (comp != null) CloudTypeMap.add(this, comp);
			}
		}
	}	
	
	private void retrieveStorage() {	
		HttpResponse httpResp = getResponse(getURL());
		if (httpResp != null) {
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(httpResp.getEntity().getContent(), UTF8));
			} catch (Throwable e) {
			} 
			if (reader != null) {	
				String[] storageURLS = ParseOcciTop.parseStorage(reader, getURL());
				if (storageURLS != null) {
					for (int i = 0; i < storageURLS.length; i++) {
						retrieveStorage(storageURLS[i]);
					}
				}
			}			
		}		
	}	
	
	private void retrieveStorage(String url) {
		HttpResponse httpResp = getResponse(url);
		if (httpResp != null) {
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(httpResp.getEntity().getContent(), UTF8));
			} catch (Throwable e) {
			} 
			if (reader != null) {	
				CloudStorageType store = ParseOcciTop.parseStorage(url, reader);
				if (store != null) CloudTypeMap.add(this, store);
			}
		}
	}		
	

}
