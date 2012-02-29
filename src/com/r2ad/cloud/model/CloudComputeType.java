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

public class CloudComputeType extends CloudLinkType {
	
    /** Property names fired when a class property is changed (via set) */
    public static final String HOSTNAME = "Hostname";
    public static final String MEMORY = "Memory";
    public static final String SPEED = "Speed";
    public static final String STATUS = "Status";
    public static final String ARCHITECTURE = "Architecture";
    public static final String CORES = "Cores";
    public static final String CATEGORY = "Category";

    public static enum Status {ACTIVE, INACTIVE, SUSPENDED}
    public static String[] StatusString = {"Active", "Inactive", "Suspended"};
    public static enum Architecture {x86, x86_32, x86_64}
    public static String[] ArchString = {"X86", "x86_32", "x86_64"};

    private String hostname;    
    private float memory = 1024;     
    private float speed = 2;      
    private Status status = Status.ACTIVE;     
    private Architecture architecture = Architecture.x86_32;
    private float cores = 1;

    // ***************************
    // Public Class Constructor Methods
    // ***************************	
    
    public CloudComputeType() {
        super();
    }
   
    // ***************************
    // Implement the OCCIType Interface
    // ***************************
    
    public TYPE getType() {
    	return TYPE.COMPUTE;
    }
    
    // ***************************
    // Class Public Methods
    // ***************************

    public Architecture getArchitecture() {
        return architecture;
    }

    public String getArchitectureAsString() {
        switch (architecture) {
            case x86: return ArchString[0];
            case x86_32: return ArchString[1];
        }
        return ArchString[2];
    }    
    
    public float getCores() {
        return cores;
    }

    public String getHostname() {
        return hostname;
    }

    public float getMemory() {
        return memory;
    }

    public float getSpeed() {
        return speed;
    }

    public Status getStatus() {
        return status;
    }

    public String getStatusAsString() {
        switch (status) {
            case ACTIVE: return StatusString[0];
            case INACTIVE: return StatusString[1];
        }
        return StatusString[2];
    }    
    
    public void setArchitecture(int arch) {
        if (arch == 0) {
            setArchitecture(Architecture.x86);
        } else if (arch == 1) {
            setArchitecture(Architecture.x86_32);
        } else if (arch == 2) {
            setArchitecture(Architecture.x86_64);
        }
    }

    public void setArchitecture(Architecture architecture) {
        Architecture temp = this.architecture;
        this.architecture = architecture;
        firePropertyChange(new PropertyChangeEvent(this,
            ARCHITECTURE, temp, architecture));
    }

    public void setCores(float cores) {
        float temp = this.cores;
        this.cores = cores;
        firePropertyChange(new PropertyChangeEvent(this,
            CORES, Float.valueOf(temp), Float.valueOf(cores)));
    }

    public void setHostname(String hostname) {
    	String temp = this.hostname;
        this.hostname = hostname;
        firePropertyChange(new PropertyChangeEvent(this,
            HOSTNAME, temp, this.hostname));
    }

    public void setMemory(float memory) {
    	float temp = this.memory;
        this.memory = memory;
        firePropertyChange(new PropertyChangeEvent(this,
            MEMORY, Float.valueOf(temp), Float.valueOf(memory)));
    }

    public void setSpeed(float speed) {
    	float temp = this.speed;
        this.speed = speed;
        firePropertyChange(new PropertyChangeEvent(this,
            SPEED, Float.valueOf(temp), Float.valueOf(speed)));
    }

    public void setStatus(int status) {
        if (status == 0) {
            setStatus(Status.ACTIVE);
        } else if (status == 1) {
            setStatus(Status.INACTIVE);
        } else if (status == 2) {
            setStatus(Status.SUSPENDED);
        }
    }

    public void setStatus(Status stat) {
    	Status temp = this.status;
    	this.status = stat;        
        firePropertyChange(new PropertyChangeEvent(this,
            STATUS, temp, stat));
    }

    public String getString() {
        return "host:["+hostname +"]/nID:"+ this.getUID() +", Title:"+ this.getTitle() +", summary:"+ this.getSummary() +", Mem:"+ memory +", Speed:"+ speed +", Status:"+ status +", Arch:"+ architecture +", Core:"+ cores + "]";
    }    
    

}
