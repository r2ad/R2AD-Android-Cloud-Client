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

import java.util.ArrayList;

public class CloudServiceRegistry {
	
	private static ArrayList<String> serviceNames;
	private static ArrayList<String> serviceClasses;
	
	static {
		serviceClasses = new ArrayList<String>();
		serviceNames = new ArrayList<String>();
		serviceNames.add("Amazon");
		serviceClasses.add("com.r2ad.cloud.amazon.AmazonService");		
		serviceNames.add("CDMI");
		serviceClasses.add("com.r2ad.cloud.cdmi.CDMIService");
		serviceNames.add("CloudStack");
		serviceClasses.add("com.r2ad.cloud.cloudstack.CloudStackService");		
		serviceNames.add("OCCI");
		serviceClasses.add("com.r2ad.cloud.occi.OCCIService");
		serviceNames.add("OpenShift");
		serviceClasses.add("com.r2ad.cloud.openshift.OpenShiftService");		
		serviceNames.add("Rackspace");
		serviceClasses.add("com.r2ad.cloud.rackspace.RackspaceService");		
	}
	// ****************************************************************
	// CloudServiceRegistry Methods
	// ****************************************************************
	
	public static void registerCloudService(String name, String serviceclass) {	
		serviceNames.add(name);
		serviceClasses.add(serviceclass);
	}
	
	public static String[] getRegisteredCloudServices() {
		String[] result = new String[serviceNames.size()];
		serviceNames.toArray(result);
		return result;
	}
	
	public static CloudService generateCloudServiceByIndex(int index) {
		return generateCloudServiceByClass(serviceClasses.get(index));
	}	
	
	public static CloudService generateCloudServiceByName(String serviceName) {
		return generateCloudServiceByClass(serviceClasses.get(serviceNames.indexOf(serviceName)));
	}	
	
	public static CloudService generateCloudServiceByClass(String classname) {
		CloudService result = null;
    	try {
    	    result = (CloudService)Class.forName(classname).newInstance();
    	} catch (ClassNotFoundException oops) {	        		
    	} catch (InstantiationException nogood) {
    	} catch (IllegalAccessException  sucks) {
    	}		
		return result;
	}			

}
