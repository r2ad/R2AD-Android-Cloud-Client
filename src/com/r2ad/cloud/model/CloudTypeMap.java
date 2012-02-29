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
package com.r2ad.cloud.model;

import java.util.ArrayList;
import java.util.HashMap;

public class CloudTypeMap {

	//Used for Log reporting
	private static final String TAG = "CloudTypeMap";
	//For application use
	private static CloudBaseType selectedType;
	//Internal HashMap 
	private static HashMap<Object,ArrayList<CloudBaseType>> 
        cloudMap = new HashMap<Object,ArrayList<CloudBaseType>>();	
		
	// Methods to manage the static instance of the OCCILinkType
	
	public static void clearSelectedType() {
		CloudTypeMap.selectedType = null;
	}
	
	public static CloudBaseType getSelectedType() {
		return CloudTypeMap.selectedType;
	}
	
	public static void setSelectedType(CloudBaseType selected) {
		CloudTypeMap.selectedType = selected;
	}		
	
	// Methods to manage OCCLinkTypes mapped to a key Object
	
	public static void add(Object key, CloudBaseType cloudBase) {
		ArrayList<CloudBaseType> target = cloudMap.get(key);
		if (target == null) {
			target = new ArrayList<CloudBaseType>();
		}
		target.add(cloudBase);
		cloudMap.put(key, target);
	}
	
	public static void clearAll() {
		cloudMap.clear();
	}	
	
	public static void clear(Object key) {
		cloudMap.put(key, null);
	}			
	
	public static CloudBaseType[] get(Object key) {
		CloudBaseType[] result = new CloudBaseType[0];
		ArrayList<CloudBaseType> target = cloudMap.get(key);
		if (target != null && target.size() > 0) {
			result = new CloudBaseType[target.size()];
			target.toArray(result);
		}
		return result;
	}		
	
	public static CloudBaseType[] get(Object key, CloudType.TYPE type) {
		CloudBaseType[] result = new CloudBaseType[0];
		ArrayList<CloudBaseType> target = cloudMap.get(key);
		if (target != null && target.size() > 0) {
			ArrayList<CloudBaseType> matching = new ArrayList<CloudBaseType>();
			for (int i = 0; i < target.size(); i++) {
				CloudBaseType temp = target.get(i);
				if (temp.getType() == type) {
				    matching.add(temp);
				}
			}
			result = new CloudBaseType[matching.size()];
			matching.toArray(result);
		}
		return result;
	}		
	
	public static CloudBaseType get(Object key, CloudType.TYPE type, int index) {
		CloudBaseType result = null;
		ArrayList<CloudBaseType> target = cloudMap.get(key);
		if (target != null && target.size() > 0) {
			int typeIndex = 0;
			for (int i = 0; result == null && i < target.size(); i++) {
				CloudBaseType temp = target.get(i);
				if (temp.getType() == type) {
					if (typeIndex == index) {
				        result = temp;
					} 
					typeIndex++;
				}
			}
		}
		return result;
	}			
			
	public static int count(Object key) {
		ArrayList<CloudBaseType> target = cloudMap.get(key);
		return (target == null) ? 0 : target.size();
	}		
	
	public static int count(Object key, CloudType.TYPE type) {
		int result = 0;
		ArrayList<CloudBaseType> target = cloudMap.get(key);
		if (target != null && target.size() > 0) {
			for (int i = 0; i < target.size(); i++) {
				CloudBaseType temp = target.get(i);
				if (temp.getType() == type) {
					result++;
				}
			}
		}
		return result;
	}			
	
	public static void remove(Object key, CloudBaseType occi) {
		ArrayList<CloudBaseType> target = cloudMap.get(key);
		if (target != null) {
			target.remove(occi);
		}
	}
	
	public static void remove(Object key, int index) {
		ArrayList<CloudBaseType> target = cloudMap.get(key);
		if (target != null) {
			target.remove(index);
		}
	}		
		
}
