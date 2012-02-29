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
package com.r2ad.android;


import com.r2ad.cloud.http.CloudService;
import com.r2ad.cloud.http.CloudServiceManager;
import com.r2ad.cloud.model.CloudComputeType;
import com.r2ad.cloud.model.CloudTypeMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ComputeDetailActivity extends Activity {

	private CloudService m_CloudService;
	private CloudComputeType m_Compute;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.computerdetail);
        m_CloudService = CloudServiceManager.getSelectedService();
    	if (m_CloudService != null) {
    		setTitle(m_CloudService.getName() + " Computer");
    	}        
        m_Compute = (CloudComputeType)CloudTypeMap.getSelectedType();
        if (m_Compute != null) {
            loadServerData();
        } 
        ((Button) findViewById(R.id.editcomputerbutton)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                editClicked();
            }
        });
        ((Button) findViewById(R.id.deletecomputerbutton)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	showDeleteConfirm();
            }
        });        
    }       
   
    private void loadServerData() {
    	TextView name = (TextView) findViewById(R.id.view_server_name);
    	if (name != null) name.setText(m_Compute.getTitle());
    	
    	TextView os = (TextView) findViewById(R.id.viewarch);
    	if (os != null) os.setText(m_Compute.getArchitectureAsString());
    	
    	TextView memory = (TextView) findViewById(R.id.viewmemory);
    	if (memory != null) memory.setText(m_Compute.getMemory() + " GB");
    	
    	TextView cores = (TextView) findViewById(R.id.corestate);
    	if (cores != null) cores.setText(Float.toString(m_Compute.getCores()));
    	
    	TextView status = (TextView) findViewById(R.id.viewstatus);
    	if (status != null) {
    		status.setText(m_Compute.getStatusAsString());
    		if (m_Compute.getStatus() == CloudComputeType.Status.ACTIVE) {
    			status.setTextColor(Color.GREEN);
    		} else if (m_Compute.getStatus() == CloudComputeType.Status.INACTIVE) {
    			status.setTextColor(Color.YELLOW);
    		} else {
    			status.setTextColor(Color.RED);
    		}
    	}  
    }   
    
    @Override 
    public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.computer_detail_menu, menu);
		return true;
	} 
    
    @Override 
    public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.editcomputer:
			editClicked();
			return true;
		case R.id.deletecomputer:
			showDeleteConfirm();
	        return true;
		}
		return false;
	} 
    
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);	  
	  if (resultCode == RESULT_OK) {	  
		  // a sub-activity kicked back, so we want to refresh the server list
		  loadServerData();
	  }
	}

    private void showDeleteConfirm() {
    	if (m_Compute != null) {
	    	AlertDialog.Builder alert = new AlertDialog.Builder(this);
	    	alert.setTitle("Delete Computer Verification");
	    	String servName = (m_Compute.getTitle());
	    	alert.setMessage("Confirm Deletion of " + servName);
	    	alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
	    	    public void onClick(DialogInterface dialog, int whichButton) {
	    	    	deleteClicked();    	    	
	    	    }
	    	});
	    	alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	    	    public void onClick(DialogInterface dialog, int whichButton) {
	    	      return;
	    	    }
	    	});
	    	alert.show();   
    	}
    }	
    
	private void deleteClicked() {
		CloudTypeMap.remove(m_CloudService, m_Compute);
		m_Compute = null;
		startActivityForResult(new Intent(this, ComputeListActivity.class), 2339); 	
	}	  
	
	private void editClicked() {
    	Intent viewIntent = new Intent(this, AddComputeActivity.class);
    	CloudTypeMap.setSelectedType(m_Compute);
		startActivityForResult(viewIntent, 3511);  		
	}	    	
   
    
}
