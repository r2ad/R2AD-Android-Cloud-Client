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

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ServiceFinalizeActivity extends Activity {
          
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.servicename);
		CloudService cs = CloudServiceManager.getSelectedService();
		if (cs != null) {
			String type = cs.getType();
			((TextView) findViewById(R.id.finalservicenameview)).setText("Name your New " + type + " Connection");
			String name = cs.getName();
			if (name == null || name.length() < 1) {
				name = "New " + type + " Service";
			}
			((EditText) findViewById(R.id.finalservicenamefield)).setText(name);			
			((TextView) findViewById(R.id.finalserviceurl)).setText(cs.getURL());
			((TextView) findViewById(R.id.finalservicetype)).setText(type);
		}
		((Button) findViewById(R.id.finalservicebutton)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				saveService();
		    }	
		});  
		((EditText) findViewById(R.id.finalservicenamefield)).requestFocus();
	} 
	
	
    public void saveService() {
    	CloudService srv = CloudServiceManager.getSelectedService();
    	String name = ((EditText) findViewById(R.id.finalservicenamefield)).getText().toString();
    	srv.setName(name);
    	CloudServiceManager.addService(srv);
		setResult(Activity.RESULT_OK);
		finish();
    }    
	
}

