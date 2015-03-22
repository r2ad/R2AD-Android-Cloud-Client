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
package com.r2ad.cloud.cdmi;

// Codehaus dependency: http://wiki.fasterxml.com/JacksonDownload

import java.io.InputStream;
import java.io.IOException;

import java.util.ArrayList;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.ObjectMapper;

import android.util.Log;

public class ParseCDMIContainer {

	private static final String TAG = "ParseCDMIContainer";
    static ObjectMapper mapper; // can reuse, share globally
    static JsonFactory factory = new JsonFactory();

    public static ObjectMapper getMapper() {
    	if ( mapper == null ) {
		    Log.d(TAG, "Creating JSON ObjectMapper ");
    		mapper = new ObjectMapper(); // can reuse, share globally;
    	}
    	return mapper;
    }
    

    public static JsonFactory getJSONFactory() {
    	if ( factory == null ) {
    		factory = new JsonFactory();
    	}
    	return factory;
    }
    
    /* 
     * Given the results of a query, determine the children of this container.
     * These will then be displayed later.
     */
    public static String[] parseStorage(InputStream instream, String url) {
		ArrayList<String> items = new ArrayList<String>();
		//
		// Note: Null is return in conditions of errors or if nothing is found
		//
		String[] result = null;
		
		if (instream != null) {
		    Log.d(TAG, "parseStorage ready, url is " + url);
		} else {
		    Log.d(TAG, "parseStorage instream is null, url is " + url);
		}

	   	try {
            JsonParser jp = null;
            jp = getJSONFactory().createJsonParser(instream);

            JsonToken token;
            token = jp.nextToken(); // move to value, or START_OBJECT/START_ARRAY

            String fieldname = null;
            boolean children = false;
            while (jp.hasCurrentToken()) {
            	fieldname = jp.getCurrentName();
				 //
				 // Extra debug line if needed:
				 //System.out.println("token=" + token + " Name=" + fieldname + " Text=" + jp.getText());
				 //
				 if (token.toString().equals("FIELD_NAME") && fieldname.equals("metadata") ) {
					 Log.d(TAG, "Process any Metadata here");
				     children=false;
				 } else if ( token.toString().equals("FIELD_NAME") && fieldname.equals("children") ) {
				     children=true;
				     Log.d(TAG, "Processing child elements");
				 }
				 if ( token.toString().equals("VALUE_STRING") && children ) {
					 String line = jp.getText();
					 Log.d(TAG, "   Item: " + line);
					 items.add(line);
					 //
					 // TBD - Query metadata for this on a separate thread
					 // Store metadata with the internal CloudStorage Type and make it accessible via a button.
				}					
				token = jp.nextToken(); // move to value, or START_OBJECT/START_ARRAY
            }
            
            // Now process the collected items and return an array:
		    Log.d(TAG, "Found this many items: " + items.size());	            
		    Log.d(TAG, "Process Children?  " + children);	            
			result = new String[items.size()];
			items.toArray(result);
	    } catch (IOException e) {
	         System.err.println("Fatal transport error: " + e.getMessage());
	    } catch (java.lang.IllegalStateException e) {
	         System.err.println("BAD URL, try again: " + e.getMessage());
	    } finally {
	      // Release the connection.
	    }
	    
		return result;	            

	}			
   
}
