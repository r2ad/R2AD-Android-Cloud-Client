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
import com.r2ad.cloud.model.CloudType;
import com.r2ad.cloud.model.CloudTypeMap;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class ContainerListActivity extends ListActivity {
	
	private CloudComputeType[] computers;
	private CloudService m_cloudService;
	private static final String TAG = "ContainerListActivity";
    private static final int DELETE_ID = Menu.FIRST + 1;
    private static final int SETTINGS_ID = Menu.FIRST + 2;	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	m_cloudService = CloudServiceManager.getSelectedService();
    	if (m_cloudService != null) {
    		setTitle(m_cloudService.getName() + " Container Listing");
    	}    	
    	loadComputers();
    	registerForContextMenu(getListView());
    }	
    
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	if (computers != null && computers.length > 0) {
	    	Intent viewIntent = new Intent(this, ComputeDetailActivity.class);
	    	CloudTypeMap.setSelectedType(computers[position]);
			startActivityForResult(viewIntent, 55); // arbitrary number; never used again
    	} else {
    		CloudTypeMap.clearSelectedType();
			startActivityForResult(new Intent(this, AddComputeActivity.class), 34); // arbitrary number; never used again
    	}
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
        menu.setHeaderTitle(computers[info.position].getTitle() + " Options");
        menu.add(0, DELETE_ID, 0, "Delete"); 
        menu.add(0, SETTINGS_ID, 0, "Settings");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	int listIndex = ((AdapterContextMenuInfo)item.getMenuInfo()).position;
        switch(item.getItemId()) {
            case DELETE_ID:
            	CloudTypeMap.remove(m_cloudService, computers[listIndex]);
                loadComputers();
                return true;
            case SETTINGS_ID:
            	Intent viewIntent = new Intent(this, AddComputeActivity.class);
            	CloudTypeMap.setSelectedType(computers[listIndex]);
        		startActivityForResult(viewIntent, 55);  
                return true;   
        }
        return super.onContextItemSelected(item);
    }    
    
    private void loadComputers() {
    	displayLoadingCell();
    	new LoadComputersTask().execute((Void[]) null);
    }
    
    private void setComputers(CloudComputeType[] compArray) {
    	computers = compArray;
    	if (computers == null) {
    		computers = new CloudComputeType[0];
    	}
		if (computers.length == 0) {
			displayNoServersCell();
		} else {
			getListView().setDividerHeight(3); // restore divider lines 
			setListAdapter(new ComputerAdapter());
		}
    }
    
    private void displayLoadingCell() {
    	String a[] = new String[1];
    	a[0] = "Loading Computer Content...";
        setListAdapter(new ArrayAdapter<String>(this, R.layout.loadingcell, R.id.loading_label, a));
        getListView().setTextFilterEnabled(true);
        getListView().setDividerHeight(0); // hide the dividers so it won't look like a list row
        getListView().setItemsCanFocus(false);
    }
    
    private void displayNoServersCell() {
    	String a[] = new String[1];
    	a[0] = "No Computer Content";
        setListAdapter(new ArrayAdapter<String>(this, R.layout.nocontentcell, R.id.no_content_label, a));
        getListView().setTextFilterEnabled(true);
        getListView().setDividerHeight(0); // hide the dividers so it won't look like a list row
        getListView().setItemsCanFocus(false);
    }
    
    private class LoadComputersTask extends AsyncTask<Void, Void, CloudComputeType[]> {
    	  	
		@Override
		protected CloudComputeType[] doInBackground(Void... arg0) {
			CloudComputeType[] result = null;
			if (m_cloudService != null) {
				m_cloudService.retrieve();
				Object[] temp = CloudTypeMap.get(m_cloudService, CloudType.TYPE.COMPUTE);
				if (temp != null) {
					result = new CloudComputeType[temp.length];
					System.arraycopy(temp, 0, result, 0, temp.length);					                            
				}
			}
			return result;
		}
    	
		@Override
		protected void onPostExecute(CloudComputeType[] result) {
			setComputers(result);
		}
    }
    
    @Override 
    public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.compute_list_menu, menu);
		return true;
	} 
    
    @Override 
    public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add_computer:
			CloudTypeMap.clearSelectedType();
			startActivityForResult(new Intent(this, AddComputeActivity.class), 34); // arbitrary number; never used again
			return true;
		case R.id.refresh:
			loadComputers();
	        return true;
		}
		return false;
	} 
	//*/
    
	class ComputerAdapter extends ArrayAdapter<CloudComputeType> {
		ComputerAdapter() {
			super(ContainerListActivity.this, R.layout.listcomputecell, computers);
		}
		
		public View getView(int position, View convertView, ViewGroup parent) {
			
			LayoutInflater inflater = getLayoutInflater();
			View row = inflater.inflate(R.layout.listcomputecell, parent, false);

			TextView label = (TextView) row.findViewById(R.id.label);
			label.setText(computers[position].getTitle());
			
			TextView sublabel = (TextView) row.findViewById(R.id.sublabel);
			String temp = "Cores " + computers[position].getCores();
			temp += " Memory " + computers[position].getMemory();
			sublabel.setText(temp);
			
			ImageView icon = (ImageView) row.findViewById(R.id.icon);
			if (computers[position].getStatus() == CloudComputeType.Status.ACTIVE) {
				icon.setImageResource(R.drawable.vmg);
			} else if (computers[position].getStatus() == CloudComputeType.Status.INACTIVE) {
				icon.setImageResource(R.drawable.vmy);
			} else {
				icon.setImageResource(R.drawable.vmr);
			}
			return(row);
		}
	}
    
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);
	  loadComputers();
	}	
}
