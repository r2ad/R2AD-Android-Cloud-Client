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
import java.util.Date;

import android.util.Log;

import com.r2ad.android.R;

public class CAMPResourceType extends CloudLinkType {
	
    /** Property names fired when a class property is changed (via set) */
    public static final String TYPE = "Type";
    public static final String STATUS = "Status";
    public static final String SIZE = "Size";
    public static final String ACCESS_TIME = "Access_Time";
    public static final String CREATE_TIME = "Create_Time";
    public static final String MODIFIED_TIME = "Modified_Time";

    public static enum Status {RUNNING, STOPPED, UNKNOWN}
    public static String[] StatusString = {"Running", "Stopped", "Unknown"};
    public static enum Type {APPLICATION, ASSEMBLYTEMPLATE, TEMPLATE}
    public static String[] TypeString = {"Application", "AssemblyTemplate","Template"};
    private float size;
    private Status status;
    private Type itemType;
    private Date createTime;
    private Date modifiedTime;
    private Date accessTime;
    private boolean m_leaf;


    // ***************************
    // Public Class Constructor Methods
    // ***************************

    public CAMPResourceType() {
        super();
        this.accessTime = new Date();
        this.createTime = new Date();
        this.modifiedTime = new Date();
        this.status = Status.UNKNOWN;
        this.itemType = Type.APPLICATION; 
    }

    // ***************************
    // Implement the OCCIType Interface
    // ***************************
    
    public TYPE getType() {
    	return com.r2ad.cloud.model.CloudType.TYPE.BASE;
    }
    
    // ***************************
    // Class Public Methods
    // ***************************

    public Date getAccessTime() {
        return accessTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public Date getModifiedTime() {
        return modifiedTime;
    }

    public float getSize() {
        return size;
    }

    public Status getStatus() {
        return status;
    }

    public Type getContainerType() {
        return itemType;
    }
    
    public String getStatusAsString() {
        switch (status) {
            case RUNNING: return StatusString[0];
            case STOPPED: return StatusString[1];
            case UNKNOWN: return StatusString[2];
        }
        return StatusString[2];
    }    
    
    public boolean isLeaf() {
        return m_leaf;
    }
    
    @Override
    public void setTitle(String title) {
    	super.setTitle(title);
    	determineObjectType(title);
    }
        
    public void setAccessTime(Date time) {
        Date temp = this.accessTime;
        this.accessTime = time;
        firePropertyChange(new PropertyChangeEvent(this,
            ACCESS_TIME, temp, time));
    }

    public void setCreateTime(Date time) {
        Date temp = this.createTime;
        this.createTime = time;
        firePropertyChange(new PropertyChangeEvent(this,
            CREATE_TIME, temp, time));
    }

    public void setModifiedTime(Date time) {
        Date temp = this.modifiedTime;
        this.modifiedTime = time;
        firePropertyChange(new PropertyChangeEvent(this,
            MODIFIED_TIME, temp, time));
    }

    public void setSize(float size) {
        float temp = this.size;
        this.size = size;
        firePropertyChange(new PropertyChangeEvent(this,
            SIZE, Float.valueOf(temp), Float.valueOf(size)));
    }

    public void setStatus(Status status) {
        Status temp = this.status;
        this.status = status;
        firePropertyChange(new PropertyChangeEvent(this,
            STATUS, temp, status));
    }

    public void setContainerType(Type itemType) {
        Type temp = this.itemType;
        this.itemType = itemType;
        firePropertyChange(new PropertyChangeEvent(this,
            TYPE, temp, itemType));
    }

    /*
     * Adds an object to this storage container.
    public void addObject(StoredObject SO) {
        if (contianedObject == null) {
            contianedObject = SO;
        }
    }
    */

    /*
     * Returns the objects stored in this cotnainer.  These are typically
     * files, either textual or binary.
    public StoredObject getObject() {
        return contianedObject;
    }
     */

	private void determineObjectType(String title) {
		if (title != null) {			
			// 
			// Set container type based on ending slash
			if (title.endsWith("/")) {
				setContainerType(Type.APPLICATION);
				setIconId(R.drawable.folder);		
				setSummary(dateFormat.format(getModifiedTime()));
			} else {
				setContainerType(Type.TEMPLATE);
				int index = getTitle().lastIndexOf("."); 				
				if (index > 1 && index < title.length()) {
					m_leaf = true;
					String temp = title.substring(index+1, title.length());
					setIconId(R.drawable.richtext);
					if (temp.equalsIgnoreCase("html") || temp.endsWith("htm")) {
						setIconId(R.drawable.html);
					} else if (temp.equalsIgnoreCase("gif") || temp.endsWith("ico") || 
					    temp.endsWith("jpg") || temp.endsWith("png") || temp.endsWith("jpeg") || temp.endsWith("img")) {
						setIconId(R.drawable.image);
					} else if (temp.equalsIgnoreCase("pdf")) {
						setIconId(R.drawable.pdf);
					} 
				} else {
					// Can't figure out what type of file this is. Use a generic file icon.
					setIconId(R.drawable.generic);
				}				
				// Regardless, show file size:
				setSummary("Size " + getSize() + "KB");
			}			
			Log.d("CST", "Item: " + getString());
		}
	}    
	
    public String getString() {
         return "ID:"+ this.getUID() +", Title:"+ this.getTitle()+", Type:"+ this.getContainerType() +", URI:"+ this.getURI();
    }	

}
