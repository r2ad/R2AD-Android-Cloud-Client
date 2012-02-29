package com.r2ad.cloud.rackspace;

import android.util.Log;

import java.io.InputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;

import com.r2ad.cloud.cdmi.ParseCDMIContainer;
import com.r2ad.cloud.http.CloudServiceAdapter;
import com.r2ad.cloud.model.CloudStorageType;
import com.r2ad.cloud.model.CloudTypeMap;

public class RackspaceService extends CloudServiceAdapter {
	public static final String[] CAPABILITES_HDR = {"Accept", "application/cdmi-capability", "X-CDMI-Specification-Version", "1.0.1"};	
	public static final String[] CONTAINER_HDR = {"Accept", "application/cdmi-container", "X-CDMI-Specification-Version", "1.0.1"};	
	public static final String[] OBJECT_HDR = {"Accept", "application/cdmi-object", "X-CDMI-Specification-Version", "1.0.1"};
		
	private static final String TAG = "RackspaceService";
	private static final String TYPE = "Rackspace";
	private String[] HEADERS = CAPABILITES_HDR;
	private boolean m_retrieved = false;
	
	// *******************************************************
	// Implement abstract methods from CloudServiceAdapter
	// *******************************************************
	
	@Override
	public boolean authenticate() {	
		return false;
	}			
		
	@Override
	public void disposeService() {	
		m_retrieved = true;
		CloudTypeMap.clear(this);
	}			
		
	@Override
	public String getType() {
		return TYPE;
	}				
	
	@Override
	public boolean isAvailable() {
		return false;
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
		}
	}
	
	protected String[] getHeaderInfo() {
		return HEADERS;		
	}
	
	// *******************************************************
	// Rackspace Service Specific Methods
	// *******************************************************		
	

}
