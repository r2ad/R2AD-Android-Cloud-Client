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

public class CloudServiceManager {
	
	private static CloudService selectedService;
	public static final String SERVICE_INDEX = "service-index";
	
	private static ArrayList<CloudService> serviceArray;
	private static ArrayList<CloudService> deletedServices;
	
	static {
		serviceArray = new ArrayList<CloudService>();
		deletedServices = new ArrayList<CloudService>();
	}
	
	// ****************************************************************
	// CloudService Selection Methods
	// ****************************************************************
		
	public static void clearSelectedService() {
		CloudServiceManager.selectedService = null;
	}
	
	public static CloudService getSelectedService() {
		return CloudServiceManager.selectedService;
	}
	
	public static void setSelectedService(CloudService service) {
		CloudServiceManager.selectedService = service;
	}
	
	// ****************************************************************
	// CloudService Methods
	// ****************************************************************
	
	public static void addService(CloudService service) {
		serviceArray.add(service);
	}
	
	public static void clearServices() {
		while (getServiceCount() > 0) {
			removeService(0);
		}
	}		
	
	public static CloudService getService(int index) {
		return serviceArray.get(index);
	}	
	
	public static CloudService[] processDeletedServices() {
		CloudService[] result = new CloudService[deletedServices.size()];
		deletedServices.toArray(result);
		deletedServices.clear();
		return result;
	}	
	
	public static CloudService[] getServices() {
		CloudService[] result = new CloudService[getServiceCount()];
		serviceArray.toArray(result);
		return result;
	}		
	
	public static int getServiceCount() {
		return serviceArray.size();
	}	
	
	public static void removeService(CloudService service) {
		if (service != null) {
		    service.disposeService();
		    serviceArray.remove(service);
		    deletedServices.add(service);
		}
	}
	
	public static void removeService(int index) {
		removeService(getService(index));
	}			

}
