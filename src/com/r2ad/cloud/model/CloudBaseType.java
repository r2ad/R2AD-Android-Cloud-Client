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
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.text.format.DateFormat;

import com.r2ad.cloud.actions.CloudAction;

/**
 * The CloudBaseType class is a Java implementation of the most basic cloud node
 * HTTP/REST response type. Vendor specific cloud service modules should extend this
 * base class in order to be used in the R2AD Cloud Client.
 * 
 * @author David K. Moolenaar, R2AD LLC
 */
public class CloudBaseType implements CloudType {
	
    /** Used when firing PropertyChangeEvents to any registered listeners. */
    public static final String ICON = "ICON";
    public static final String ID = "ID";
    public static final String RELATED = "Related";
    public static final String SUMMARY = "Summary";
    public static final String TITLE = "Title";
    public static final String URI = "URI";
    private PropertyChangeSupport propertySupport;
    protected static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    /** Private class properties. */
    private String 	uid; 
    private URI 	uri; 
    private String 	related; 
    private String 	summary;
    private String 	title;  
    private int		iconid;
    private ArrayList<CloudAction> actions;
    
    public CloudBaseType() {
    	actions = new ArrayList<CloudAction>();
    }
       
    // ***************************
    // Implement the CloudType Interface
    // ***************************
    
    public TYPE getType() {
    	return TYPE.BASE;
    }
    
    public CloudAction[] getActions() {
    	CloudAction[] result = new CloudAction[actions.size()];
    	actions.toArray(result);
    	return result;
    }    
    
    // ***************************
    // Class Public Methods
    // ***************************

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        if (propertySupport == null) {
            propertySupport = new PropertyChangeSupport(this);
        }
        propertySupport.addPropertyChangeListener(pcl);
    }

    protected void firePropertyChange(PropertyChangeEvent pce) {
        if (propertySupport != null) {
            propertySupport.firePropertyChange(pce);
        }
    }

    public void addAction(CloudAction action) {
    	actions.add(action);
    }
    
    public void removeAction(CloudAction action) {
    	actions.remove(action);
    }   
    
    public int getIconId() {
        return iconid;
    }
    
    public String getUID() {
        return uid;
    }

    public String getRelated() {
        return related;
    }

    public String getSummary() {
        return summary;
    }

    public String getTitle() {
        return title;
    }

    public URI getURI() {
        return uri;
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        if (propertySupport != null) {
            propertySupport.removePropertyChangeListener(pcl);
        }
    }

    public void setIconId(int iconid) {
        int temp = this.iconid;
        this.iconid = iconid;
        firePropertyChange(new PropertyChangeEvent(this,
            ICON, temp, iconid));
    }
    
    public void setUID(String uid) {
        String temp = this.uid;
        this.uid = uid;
        firePropertyChange(new PropertyChangeEvent(this,
            ID, temp, uid));
    }

    public void setRelated(String related) {
        String temp = this.related;
        this.related = related;
        firePropertyChange(new PropertyChangeEvent(this,
            RELATED, temp, related));
    }

    public void setSummary(String summary) {
        String temp = this.summary;
        this.summary = summary;
        firePropertyChange(new PropertyChangeEvent(this,
            SUMMARY, temp, summary));
    }

    public void setTitle(String title) {
        String temp = this.title;
        this.title = title;
        firePropertyChange(new PropertyChangeEvent(this,
            TITLE, temp, title));
    }

    public void setURI(URI newURI) {
        URI temp = this.uri;
        this.uri = newURI;
        firePropertyChange(new PropertyChangeEvent(this,
            URI, temp, newURI));
    }

    public String toString() {
        return getTitle();
    }    
	

}
