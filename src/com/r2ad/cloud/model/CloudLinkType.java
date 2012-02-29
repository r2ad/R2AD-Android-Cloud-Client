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

import java.beans.PropertyChangeEvent;
import java.net.URI;
import java.util.ArrayList;

public class CloudLinkType extends CloudBaseType {
	
    /** Class properties not related to OCCI specification */
    public static final String LINK_ADDED = "Link Added";
    public static final String LINK_REMOVED = "Link Removed";

    /** OPTIONAL by OCCI Schema */
    private ArrayList<URI> linkArray = new ArrayList<URI>();
	
    public CloudLinkType() {
        super();
    }
    
    // ***************************
    // Implement the CloudType Interface
    // ***************************
    
    public TYPE getType() {
    	return TYPE.LINK;
    }
    
    // ***************************
    // Class Public Methods
    // ***************************

    public void addLink(URI link) {
        if (link != null) {
        	linkArray.add(link);
            firePropertyChange(new PropertyChangeEvent(this,
                LINK_ADDED, null, link));
        }
    }

    public void addLink(URI[] link) {
        if (link != null) {
            for (int i = 0; i < link.length; i++) {
                addLink(link[i]);
            }
        }
    }

    public URI[] getLinks() {
        URI[] result = new URI[linkArray.size()];
        linkArray.toArray(result);
        return result;
    }

    public URI removeLink(URI link) {
        URI result = null;
        int pos = linkArray.indexOf(link);
        if (pos >= 0) {
            result = linkArray.remove(pos);
            firePropertyChange(new PropertyChangeEvent(this,
                LINK_REMOVED, null, result));
        }
        return result;
    }    

}
