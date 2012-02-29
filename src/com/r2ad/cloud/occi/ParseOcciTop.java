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
import java.io.IOException;
import java.util.ArrayList;
import java.net.URI;

import android.util.Log;

import com.r2ad.cloud.model.CloudComputeType;
import com.r2ad.cloud.model.CloudLinkType;
import com.r2ad.cloud.model.CloudStorageType;

public class ParseOcciTop {
	
	private static final String TAG = "ParseOcciTop";
	private static final String[] COMPUTERS = { "/vms/", "/compute/" };
	private static final String[] STORAGE = { "/storage/" };
	
	public static String[] parseComputers(BufferedReader reader, String url) {
		ArrayList<String> temp = new ArrayList<String>();
		if (reader != null) {
			try {
				for (String line = null; (line = reader.readLine()) != null;) {
					String compURL = find(line, COMPUTERS[0], url);
					if (compURL == null) {
						compURL = find(line, COMPUTERS[1], url);
					}
					if (compURL != null) {
						temp.add(compURL);
					}
					if (temp.size() > 5) break;
				}
			} catch (IOException e) {				
			}
		}
		String[] result = new String[temp.size()];
		temp.toArray(result);
		return result;
	}		
	
	public static String[] parseStorage(BufferedReader reader, String url) {
		ArrayList<String> temp = new ArrayList<String>();
		if (reader != null) {
			try {
				for (String line = null; (line = reader.readLine()) != null;) {
					String storageURL = find(line, STORAGE[0], url);
					if (storageURL != null) {
						temp.add(storageURL);
					}
				}
			} catch (IOException e) {				
			}
		}
		String[] result = new String[temp.size()];
		temp.toArray(result);
		return result;
	}		
	
	private static String find(String line, String key, String url) {		
        String result = null;
		if (line != null && line.length() > 0) {
			int index = line.indexOf(key);
			if (index >= 0) {
				result = url + line.substring(index+1, line.length());
			}
		}
		return result;
	}
	
    public static CloudComputeType parseComputer(String url, BufferedReader reader) {
        String line = null;
        CloudComputeType result = new CloudComputeType(); 
        try {
            result.setURI(URI.create(url));
            while((line = reader.readLine()) != null) {
                //Log.d(TAG, line);
                if (line.startsWith("Category:")) {
                    String category = line.substring(10, line.indexOf(";"));
                    if (category.equalsIgnoreCase("compute")) {
                       processComputeCategory(result, line);
                    }
                    //
                    // TBD: Add other categories, network, etc.
                    //
                } else if (line.startsWith("Link:")) {
            	    processLink(result, line);
                } else if (line.startsWith("X-OCCI-Attribute:")) {
                    processComputeAttribute(result, line);
                } else {
            	    Log.d(TAG, "Skipping " + line);
                }
            }
        } catch (Exception e) {
            //Log.d(TAG, e.getMessage());
        }
        return result;
    }
    
    public static CloudStorageType parseStorage(String url, BufferedReader reader) {
        String line = null;
        CloudStorageType result = new CloudStorageType(); 
        try {
            result.setURI(URI.create(url));
            while((line = reader.readLine()) != null) {
                //Log.d(TAG, line);
                if (line.startsWith("Category:")) {
                    String category = line.substring(10, line.indexOf(";"));
                    if (category.equalsIgnoreCase("storage")) {
                    //   processComputeCategory(result, line);
                    }
                } else if (line.startsWith("Link:")) {
            	    processLink(result, line);
                } else if (line.startsWith("X-OCCI-Attribute:")) {
                    processStorageAttribute(result, line);
                } else {
            	    Log.d(TAG, "Skipping " + line);
                }
            }
        } catch (Exception e) {
            //Log.d(TAG, e.getMessage());
        }
        return result;
    }    

    private static void processComputeCategory(CloudComputeType comp, String input) {
        String title="Unknown";
        int idEnd;
        int idStart = input.indexOf("title=");
        if (idStart > 0) {
            idEnd = input.indexOf('"', idStart+7);
            title=input.substring(idStart+7, idEnd);
        }
        //Log.d(TAG, "Title " + title);
        comp.setTitle(title); 
    }
    
    private static void processLink(CloudLinkType comp, String input) {  
    	try {
    		int start = input.indexOf("?action=");
    		int end = input.indexOf(">", start);
    		//URI linkU = URI.create(comp.getURI().toString()+input.substring(start, end));
    		//comp.addLink(linkU);
    		OCCIAction startA = new OCCIAction("Start");
    		OCCIAction stopA = new OCCIAction("Stop");
    		OCCIAction susA = new OCCIAction("Suspend");
    		//OCCIAction delA = new OCCIAction("Delete");
    		comp.addAction(startA);
    		comp.addAction(stopA);
    		comp.addAction(susA);
    		//comp.addAction(delA);
    		//Log.d(TAG, "Link " + linkU.toString());
    	} catch (Exception e) {    
    	}
    }
    
    private static void processStorageAttribute(CloudStorageType comp, String input) {

        int catStart=input.indexOf("X-OCCI-Attribute: ");
        int catEnd=input.indexOf("=");

        String OCCIattribute = input.substring(catStart+18, catEnd);

        catStart=input.indexOf("=");
        String attributeValue = input.substring(catStart+1);

        // If value starts and ends with quotes, remove them:
        catStart = attributeValue.indexOf("\"");
        catEnd = attributeValue.lastIndexOf("\"");
        if (catStart >= 0 ) {
            attributeValue = attributeValue.substring(catStart+1, catEnd);
        }
        //Log.d(TAG, "[" + OCCIattribute + "] value [" + attributeValue + "]");
        if (OCCIattribute.equals("occi.core.title")) {
        	if (attributeValue.length() > 0) {
        	    comp.setTitle(attributeValue);
        	}
            return;
        }  
        if (OCCIattribute.equals("occi.core.summary")) {
        	comp.setSummary(attributeValue);
            return;
        }    
        if (OCCIattribute.equals("occi.core.id")) {
        	comp.setUID(attributeValue);
            return;
        }     
        if (OCCIattribute.equals("occi.storage.state")) {
            if (attributeValue.equalsIgnoreCase("online")) {
            	comp.setStatus(CloudStorageType.Status.ONLINE);
            } else if (attributeValue.equalsIgnoreCase("offline")) {
            	comp.setStatus(CloudStorageType.Status.OFFLINE);
            } else {
            	comp.setStatus(CloudStorageType.Status.DEGRADED);
            }
            return;
        }                    
        if (OCCIattribute.equals("occi.storage.size")) {
            try {
            	comp.setSize(Float.parseFloat(attributeValue));
            } catch (NumberFormatException ne) {
            }
            return;
        }
    }	    
    
    
    private static void processComputeAttribute(CloudComputeType comp, String input) {

        int catStart=input.indexOf("X-OCCI-Attribute: ");
        int catEnd=input.indexOf("=");

        String OCCIattribute = input.substring(catStart+18, catEnd);

        catStart=input.indexOf("=");
        String attributeValue = input.substring(catStart+1);

        // If value starts and ends with quotes, remove them:
        catStart = attributeValue.indexOf("\"");
        catEnd = attributeValue.lastIndexOf("\"");
        if (catStart >= 0 ) {
            attributeValue = attributeValue.substring(catStart+1, catEnd);
        }
        //Log.d(TAG, "[" + OCCIattribute + "] value [" + attributeValue + "]");
        if (OCCIattribute.equals("occi.core.title")) {
        	if (attributeValue.length() > 0) {
        	    comp.setTitle(attributeValue);
        	}
            return;
        }  
        if (OCCIattribute.equals("occi.core.summary")) {
        	comp.setSummary(attributeValue);
            return;
        }    
        if (OCCIattribute.equals("occi.core.id")) {
        	comp.setUID(attributeValue);
            return;
        }     
        if (OCCIattribute.equals("occi.compute.hostname")) {
        	comp.setHostname(attributeValue);
            return;
        }                    
        if (OCCIattribute.equals("occi.compute.architecture")) {
           if (attributeValue.equals("x86_64")) { //Defaults to x86_32
        	   comp.setArchitecture(CloudComputeType.Architecture.x86_64);
           } 
           return;
        }
        if (OCCIattribute.equals("occi.compute.state")) {
            if (attributeValue.equalsIgnoreCase("active")) {
            	comp.setStatus(CloudComputeType.Status.ACTIVE);
            } else if (attributeValue.equalsIgnoreCase("inactive")) {
            	comp.setStatus(CloudComputeType.Status.INACTIVE);
            } else {
            	comp.setStatus(CloudComputeType.Status.SUSPENDED);
            }
            return;
        }
        if (OCCIattribute.equals("occi.compute.memory")) {
            try {
            	comp.setMemory(Float.parseFloat(attributeValue));
            } catch (NumberFormatException ne) {
            }
            return;
        }
        if (OCCIattribute.equals("occi.compute.speed")) {
            try {
            	comp.setSpeed(Float.parseFloat(attributeValue));
            } catch (NumberFormatException ne) {
            }
            return;
        }
        if (OCCIattribute.equals("occi.compute.cores")) {
            try {
            	comp.setCores(Float.parseFloat(attributeValue));
            } catch (NumberFormatException ne) {
            }
        }        

    }	
	

}
