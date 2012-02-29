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
import java.util.ArrayList;

import com.r2ad.cloud.model.CloudType.TYPE;

public class CloudKindType extends CloudBaseType {

    /** Class properties not related to OCCI specification */
    public static final String COMPUTE_ADDED = "Compute Added";
    public static final String COMPUTE_REMOVED = "Compute Removed";
    public static final String NETWORK_ADDED = "Network Added";
    public static final String NETWORK_REMOVED = "Network Removed";
    public static final String STORAGE_ADDED = "Storage Added";
    public static final String STORAGE_REMOVED = "Storage Removed";

    /** OPTIONAL by OCCI Schema */
    private ArrayList<CloudComputeType> computeArray;
    private ArrayList<CloudNetworkType> networkArray;
    private ArrayList<CloudStorageType> storageArray;

    public CloudKindType() {
        super();
        computeArray = new ArrayList<CloudComputeType>();
        networkArray = new ArrayList<CloudNetworkType>();
        storageArray = new ArrayList<CloudStorageType>();
    }

    // ***************************
    // Implement the OCCIType Interface
    // ***************************
    
    public TYPE getType() {
    	return TYPE.KIND;
    }
    
    // ***************************
    // Class Public Methods
    // ***************************

    public void addType(Object target) {
        if (target != null) {
            if (target instanceof CloudComputeType) {
                computeArray.add((CloudComputeType)target);
                firePropertyChange(new PropertyChangeEvent(this,
                    COMPUTE_ADDED, null, target));
            } else if (target instanceof CloudNetworkType) {
                networkArray.add((CloudNetworkType)target);
                firePropertyChange(new PropertyChangeEvent(this,
                    NETWORK_ADDED, null, target));
            } else if (target instanceof CloudStorageType) {
                storageArray.add((CloudStorageType)target);
                firePropertyChange(new PropertyChangeEvent(this,
                    STORAGE_ADDED, null, target));
            }
        }
    }

    public void addType(Object[] target) {
        if (target != null) {
            for (int i = 0; i < target.length; i++) {
                addType(target[i]);
            }
        }
    }

    public CloudComputeType[] getCompute() {
    	CloudComputeType[] result = new CloudComputeType[computeArray.size()];
        computeArray.toArray(result);
        return result;
    }

    public CloudNetworkType[] getNetwork() {
    	CloudNetworkType[] result = new CloudNetworkType[networkArray.size()];
        networkArray.toArray(result);
        return result;
    }

    public CloudStorageType[] getStorage() {
    	CloudStorageType[] result = new CloudStorageType[storageArray.size()];
        storageArray.toArray(result);
        return result;
    }

    public Object removeType(Object target) {
        Object result = null;
        if (target != null) {
            if (target instanceof CloudComputeType) {
                int pos = computeArray.indexOf(target);
                if (pos >= 0) {
                    result = computeArray.remove(pos);
                    firePropertyChange(new PropertyChangeEvent(this,
                        COMPUTE_REMOVED, null, result));
                }
            } else if (target instanceof CloudNetworkType) {
                int pos = networkArray.indexOf(target);
                if (pos >= 0) {
                    result = networkArray.remove(pos);
                    firePropertyChange(new PropertyChangeEvent(this,
                        NETWORK_REMOVED, null, result));
                }
            } else if (target instanceof CloudStorageType) {
                int pos = storageArray.indexOf(target);
                if (pos >= 0) {
                    result = storageArray.remove(pos);
                    firePropertyChange(new PropertyChangeEvent(this,
                        STORAGE_REMOVED, null, result));
                }
            }
        }
        return result;
    }
	
}
