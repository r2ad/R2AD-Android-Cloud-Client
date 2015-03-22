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

import com.r2ad.cloud.http.CloudAccount;
import com.r2ad.cloud.http.CloudService;
import com.r2ad.cloud.http.CloudServiceManager;
import com.r2ad.cloud.http.CloudServiceRegistry;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class SettingsActivity extends Activity implements OnItemSelectedListener, OnClickListener {
      
    private Spinner serviceSpinner;
    private boolean isNewService;
    private int  servTypeIndex;
	private static final String TAG = "SettingsActivity";

    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CloudService cs = CloudServiceManager.getSelectedService();
		if (cs == null) {
			isNewService = true;
			servTypeIndex = 1;
			setContentView(R.layout.servicelogin);      
	        ((CheckBox) findViewById(R.id.servicePassBox)).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					EditText textKey = (EditText) findViewById(R.id.serviceAPIKey);  
			        if (((CheckBox) v).isChecked()) {
			        	textKey.setTransformationMethod(new SingleLineTransformationMethod());
			        } else {
			        	textKey.setTransformationMethod(new PasswordTransformationMethod());	
			        }
			        textKey.requestFocus();
			    }	
			});     
	        // Set Default:
	        ((TextView) findViewById(R.id.servicetexturl)).setText("http://globule.scality.com/dewpoint/"); // End in /
	        ((TextView) findViewById(R.id.servicetexturl)).setText("http://ec2-107-20-16-71.compute-1.amazonaws.com/campSrv/Platform/"); // End in /
	        ((TextView) findViewById(R.id.serviceUserId)).setText("plugfest2013"); // i.e.: plugfest2013
	        ((TextView)  findViewById(R.id.serviceAPIKey)).setText("plugfest2013");	        
	        ((Button) findViewById(R.id.buttonservicelogin)).setOnClickListener(this); 
	        loadServiceSpinner();
	        hideActivityIndicators();	        
		} else {
			isNewService = false;
			setContentView(R.layout.servicesettings);
			((EditText) findViewById(R.id.serviceNameField)).setText(cs.getName());
			((TextView) findViewById(R.id.serviceurl)).setText(cs.getURL()); // or use "Local Cloud Host" for demos
			((TextView) findViewById(R.id.serviceType)).setText(cs.getType());
			((Button) findViewById(R.id.serviceactionbutton)).setOnClickListener(this); 
			((Button) findViewById(R.id.servicedeletebutton)).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) { 
	        		showConfirm();	
				}
					
			});  
		}
	} 
	
    private void loadServiceSpinner() {
	    Log.d(TAG, "loadServiceSpinner");

    	serviceSpinner = (Spinner) findViewById(R.id.serviceTypeSpinner);
    	serviceSpinner.setOnItemSelectedListener(this);
		ArrayAdapter<String> serviceAdapter = new ArrayAdapter<String>(this, 
		    android.R.layout.simple_spinner_item, CloudServiceRegistry.getRegisteredCloudServices()) {
			public View getDropDownView (int position, View convertView, ViewGroup parent) {
	            View v = convertView;
	            if (v == null) {
	               Context mContext = this.getContext();
	               LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	               v = vi.inflate(R.layout.spinnerrow, null);
	            } 
	            TextView tv=(TextView) v.findViewById(R.id.spinnerTarget);
	    	    Log.d(TAG, "spinnerTarget poisition is: " + position);

	            CloudService cs = CloudServiceRegistry.generateCloudServiceByIndex(position);
	    	    Log.d(TAG, "CloudService is: " + cs);

	            tv.setText(cs.getType());
	            tv.setTextColor(cs.isAvailable() ? Color.BLACK : Color.LTGRAY);
	            return v;  
			}
			public boolean isEnabled(int position) {
				CloudService cs = CloudServiceRegistry.generateCloudServiceByIndex(position);
				return cs.isAvailable();
		    }
		    public boolean areAllItemsEnabled() {
		        return false;
		    } 			
		};
		serviceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		serviceSpinner.setAdapter(serviceAdapter);
		serviceSpinner.setSelection(servTypeIndex);
    }	
   
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		if (parent == serviceSpinner) {	
			servTypeIndex = position;
		}
	}

	public void onNothingSelected(AdapterView<?> parent) {
	}
	
    public void onClick(View view) {
    	login();
    }    
	
    private boolean hasValidInput() {
    	if (isNewService) {
    	    String urlStr = ((EditText) findViewById(R.id.servicetexturl)).getText().toString();
    	    return !"".equals(urlStr);
    	}
    	return true;
    }
    
    private void setActivityIndicatorsVisibility(int visibility) {
    	if (isNewService) {
	        ProgressBar pb = (ProgressBar) findViewById(R.id.serviceLoginProgressbar);
	    	TextView tv = (TextView) findViewById(R.id.serviceLoginAuthenticatingLabel);
	        pb.setVisibility(visibility);
	        tv.setVisibility(visibility);
    	}
    }
    
    private void showActivityIndicators() {
    	if (isNewService) {
    	    setActivityIndicatorsVisibility(View.VISIBLE);
    	    ((CheckBox)findViewById(R.id.servicePassBox)).setVisibility(View.INVISIBLE);
    	}
    }
    
    private void hideActivityIndicators() {
    	if (isNewService) {
    	    setActivityIndicatorsVisibility(View.INVISIBLE);
    	    ((CheckBox)findViewById(R.id.servicePassBox)).setVisibility(View.VISIBLE);
    	}
    }
    
    public void login() {
    	if (isNewService) {
    		CloudService srv = CloudServiceRegistry.generateCloudServiceByIndex(servTypeIndex);
    		CloudServiceManager.setSelectedService(srv);
	    	if (hasValidInput()) {
	        	showActivityIndicators();
	        	setLoginPreferences();
	        	new AuthenticateTask().execute((Void[]) null);
	    	} else {
	    		showAlert("Field Missing", "Target URL is required.");
	    	}
    	} else {
    		CloudService srv = CloudServiceManager.getSelectedService();
    		srv.setName(((EditText) findViewById(R.id.serviceNameField)).getText().toString());
    		setResult(Activity.RESULT_OK);
    		finish();
    	}
    }	
    
    private void showConfirm() {
    	CloudService cs = CloudServiceManager.getSelectedService();
    	if (cs != null) {
	    	AlertDialog.Builder alert = new AlertDialog.Builder(this);
	    	alert.setTitle("Delete Service");
	    	String servName = cs.getName();
	    	if (servName == null || servName.length() < 1) {
	    		servName = "Service";
	    	}
	    	alert.setMessage("Confirm Deletion of " + servName);
	    	alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
	    	    public void onClick(DialogInterface dialog, int whichButton) {
	            	CloudService service = CloudServiceManager.getSelectedService();
	                if (service != null) {
	                	CloudServiceManager.removeService(service); 
	                	finish();
	                }	    	    	
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
    
    private void showAlert(String title, String message) {
		AlertDialog alert = new AlertDialog.Builder(this).create();
		alert.setTitle(title);
		alert.setMessage(message);
		alert.setButton("OK", new DialogInterface.OnClickListener() {
	      public void onClick(DialogInterface dialog, int which) {
	        return;
	    } }); 
		alert.show();
		hideActivityIndicators();
    }    
    
    private class AuthenticateTask extends AsyncTask<Void, Void, Boolean> {    	
		@Override
		protected Boolean doInBackground(Void... arg0) {
			boolean result = false;
			if (isNewService) {
				CloudService srv = CloudServiceManager.getSelectedService();
				if (srv.authenticate()) {
					result = true;
				}				
			} else {
				result = true;
			}
			return new Boolean(result);
		}    	
		@Override
		protected void onPostExecute(Boolean result) {
			if (isNewService) {
				hideActivityIndicators();
				if (result.booleanValue()) {
					finishServiceConnection();
				} else {
					showAlert("Login Failure", "Authentication failed.  Please check your login settings.");
				}
			}
		}
    }
    
    private void finishServiceConnection() {    
	    Intent viewIntent = new Intent(this, ServiceFinalizeActivity.class);
	    startActivityForResult(viewIntent, 22);          
		setResult(Activity.RESULT_OK);
		finish();								    	
    }
    
    private void setLoginPreferences() {  
    	CloudService srv = CloudServiceManager.getSelectedService();
	    Log.d(TAG, "setLoginPreferences, CloudService: " + srv);
    	
    	String username = ((EditText) findViewById(R.id.serviceUserId)).getText().toString().trim();
		String userKey = ((EditText) findViewById(R.id.serviceAPIKey)).getText().toString().trim();
	    Log.d(TAG, "setLoginPreferences, username: " + username);
	    Log.d(TAG, "setLoginPreferences, userKey: " + userKey);
	    Log.d(TAG, "setLoginPreferences, servicetexturl: " + userKey);

    	srv.setURL(((EditText) findViewById(R.id.servicetexturl)).getText().toString().trim());
    	srv.setAccount(new CloudAccount(username, userKey));
    }    
	
}
