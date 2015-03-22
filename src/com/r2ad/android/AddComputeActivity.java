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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.r2ad.cloud.http.CloudServiceManager;
import com.r2ad.cloud.model.CloudComputeType;
import com.r2ad.cloud.model.CloudTypeMap;

public class AddComputeActivity extends Activity implements SeekBar.OnSeekBarChangeListener, OnItemSelectedListener, OnClickListener {

	//private static final String TAG = "CloudServiceAddComputeActivity";
	private int m_Architecture;
	private int m_Cores;
	private float m_Memory;
	private int m_State;
	private Spinner architectureSpinner;
	private Spinner coresSpinner;
	private Spinner stateSpinner;
	private SeekBar memorySeekBar;
	private CloudComputeType m_Compute;
	private boolean UPDATE;
	String TAG="AddComputeActivity-->";

	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createcomputer);
        ((Button) findViewById(R.id.save_button)).setOnClickListener(this);
        m_Compute = (CloudComputeType)CloudTypeMap.getSelectedType();
        if (m_Compute != null) {
        	((EditText)findViewById(R.id.server_name)).setText(m_Compute.getTitle());
        	((Button) findViewById(R.id.save_button)).setText("Save");
        	UPDATE = true;
        } else {
        	((Button) findViewById(R.id.save_button)).setText("Create");
        	UPDATE = false;
        }
        loadArchitectureSpinner();
        loadCoresSpinner();
        loadMemorySeekBar();   
        loadStateSpinner();
    }

    
    private void loadArchitectureSpinner() {
    	architectureSpinner = (Spinner) findViewById(R.id.archspinner);
    	architectureSpinner.setOnItemSelectedListener(this);
		ArrayAdapter<String> architectureAdapter = new ArrayAdapter<String>(this, 
		    android.R.layout.simple_spinner_item, CloudComputeType.ArchString);
		architectureAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		architectureSpinner.setAdapter(architectureAdapter);
		int index = 0;
		if (m_Compute != null) {
			if (m_Compute.getArchitecture() == CloudComputeType.Architecture.x86_32) {
				index = 1;
			} else if (m_Compute.getArchitecture() == CloudComputeType.Architecture.x86_64) {
				index = 2;
			}
		}
		architectureSpinner.setSelection(index);
    }
    
    private void loadCoresSpinner() {
    	coresSpinner = (Spinner) findViewById(R.id.corespinner);
    	coresSpinner.setOnItemSelectedListener(this);
		String imageNames[] = new String[16];
		for (int i = 0; i < 12; i++) {
			imageNames[i] = new String(Integer.toString(i+1));
		}		
		ArrayAdapter<String> coreAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, imageNames);
		coreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		coresSpinner.setAdapter(coreAdapter);
		int cores = 0;
		if (m_Compute != null) {	
			cores = new Float(m_Compute.getCores()).intValue();
			if (cores > 0) {
				cores--; //account for array index
			}
		}	
		coresSpinner.setSelection(cores);
    }
    
    private void loadStateSpinner() {
    	stateSpinner = (Spinner) findViewById(R.id.statespinner);
    	stateSpinner.setOnItemSelectedListener(this);    	
		ArrayAdapter<String> stateAdapter = new ArrayAdapter<String>(this, 
		    android.R.layout.simple_spinner_item, CloudComputeType.StatusString);
		stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		stateSpinner.setAdapter(stateAdapter);
		int state = 0;
		if (m_Compute != null) {	
			if (m_Compute.getStatus() == CloudComputeType.Status.INACTIVE) {
				state = 1;
			} else if (m_Compute.getStatus() == CloudComputeType.Status.SUSPENDED) {
				state = 2;
			}
		}	
		stateSpinner.setSelection(state);		
    }    
    
    private void loadMemorySeekBar() {
    	memorySeekBar = (SeekBar)findViewById(R.id.memorySeek);
    	memorySeekBar.setOnSeekBarChangeListener(this); 
    	int memory = 4;
    	if (m_Compute != null) {
    		memory = (int)m_Compute.getMemory();
    	}
    	memorySeekBar.setProgress(memory);
    }
    
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		if (parent == architectureSpinner) {
			m_Architecture = position;
		} else if (parent == coresSpinner) {
			m_Cores = position+1;
		} else if (parent == stateSpinner) {
			m_State = position;
		}
	}

	public void onNothingSelected(AdapterView<?> parent) {
	}

    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
    	m_Memory = progress;
    	((TextView)findViewById(R.id.memoryText)).setText("Memory " + m_Memory + " GB");
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
    	//NOTHING TO DO
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
    	//NOTHING TO DO
    }
    
	public void onClick(View arg0) {
		String name = ((EditText)findViewById(R.id.server_name)).getText().toString();
		if ("".equals(name)) {
			showAlert("Required Field Missing", "Computer name is required.");
		} else {
			showActivityIndicators();
			if (m_Compute == null) {
				m_Compute = new CloudComputeType();
			} 
			m_Compute.setTitle(name);
			if (m_Architecture == 0) { 
				m_Compute.setArchitecture(CloudComputeType.Architecture.x86);
			} else if (m_Architecture == 1) {
				m_Compute.setArchitecture(CloudComputeType.Architecture.x86_32);
			} else {
				m_Compute.setArchitecture(CloudComputeType.Architecture.x86_64);
			}
			m_Compute.setCores(m_Cores);
			m_Compute.setMemory(m_Memory);
			if (m_State == 0) {
				m_Compute.setStatus(CloudComputeType.Status.ACTIVE);
			} else if (m_State == 1) {
				m_Compute.setStatus(CloudComputeType.Status.INACTIVE);
			} else {
				m_Compute.setStatus(CloudComputeType.Status.SUSPENDED);
			}
			new SaveServerTask().execute((Void[]) null);
		}
	}
	
    private void showAlert(String title, String message) {
		AlertDialog alert = new AlertDialog.Builder(this).create();
		alert.setTitle(title);
		alert.setMessage(message);
		alert.setButton("OK",  new DialogInterface.OnClickListener()  {
	      public void onClick(DialogInterface dialog, int which) {
	        return;
	    } }); 
		alert.show();
		hideActivityIndicators();
    }
	
    private void setActivityIndicatorsVisibility(int visibility) {
        ProgressBar pb = (ProgressBar) findViewById(R.id.save_server_progress_bar);
    	TextView tv = (TextView) findViewById(R.id.saving_server_label);
        pb.setVisibility(visibility);
        tv.setVisibility(visibility);
    }

    private void showActivityIndicators() {
    	setActivityIndicatorsVisibility(View.VISIBLE);
    }
    
    private void hideActivityIndicators() {
    	setActivityIndicatorsVisibility(View.INVISIBLE);
    }
        
    private class SaveServerTask extends AsyncTask<Void, Void, CloudComputeType> {    	
    	
    	private Throwable t; 
    	
		@Override
		protected CloudComputeType doInBackground(Void... arg0) {
			try {
				if (UPDATE) {
				    //CloudListingManager.updateWrapper(CloudServiceManager.getSelectedService(), m_Compute);
				} else {
					CloudTypeMap.add(CloudServiceManager.getSelectedService(), m_Compute);
		            //Log.i(TAG,"PUT new computer:"+ OCCITypeMap.getSelectedType());
		            Log.i(TAG,"m_Compute: Name is "+ m_Compute.getTitle());
		            Log.i(TAG,"m_Compute: Memory is "+ m_Compute.getMemory());
		            Log.i(TAG,"m_Compute: Cores is "+ m_Compute.getCores());
		            //CloudTypeMap.add(this, m_Compute);
		            //m_Compute = new CloudComputeType(); 
		            //CloudComputer cc = new CloudComputer(CloudServiceManager.getSelectedService().getURL(), m_Compute);
		            //cc.createComputer();

				}
			} catch (Throwable e) {
				t = e;
			}
			return m_Compute;
		}
    	
		@Override
		protected void onPostExecute(CloudComputeType result) {
			if (t != null) {
				showAlert("Error", "There was a problem creating your server: " + t.getMessage());
			} else {
				hideActivityIndicators();
				setResult(Activity.RESULT_OK);
				finish();
			}
		}
    }
	
}
