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

import java.util.ArrayList;

import com.r2ad.android.db.CloudServicesDbAdapter;
import com.r2ad.cloud.http.CloudAccount;
import com.r2ad.cloud.http.CloudService;
import com.r2ad.cloud.http.CloudServiceManager;
import com.r2ad.cloud.http.CloudServiceRegistry;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class ServicesListActivity extends ListActivity {

// TODO: Add an Edit URL feature.
	
	private static final String TAG = "ServicesListActivity";
    private static final int DELETE_ID = Menu.FIRST + 1;
    private static final int SETTINGS_ID = Menu.FIRST + 2;
    private static final int ADD_ID = Menu.FIRST + 3;
    
	private CloudServicesDbAdapter m_cloudServerDb;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	m_cloudServerDb = new CloudServicesDbAdapter(this);
    	m_cloudServerDb.open(); 
        loadServers(true);
        setTitle("Cloud Services Client");
        registerForContextMenu(getListView());
    }
	   
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
        CloudService service = CloudServiceManager.getService(info.position);
        if (service != null) {
            menu.setHeaderTitle(service.getName() + " Options");
            menu.add(0, DELETE_ID, 0, "Delete"); 
            menu.add(0, SETTINGS_ID, 0, "Settings");    
            menu.add(0, ADD_ID, 0, "Add Service");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {    	
        switch(item.getItemId()) {
            case DELETE_ID:
            	int listIndex = ((AdapterContextMenuInfo)item.getMenuInfo()).position;
            	CloudService service = CloudServiceManager.getService(listIndex);
                if (service != null) {
                	CloudServiceManager.setSelectedService(service);
                	showDeleteConfirm();
                }
                return true;
            case SETTINGS_ID:
            	listIndex = ((AdapterContextMenuInfo)item.getMenuInfo()).position;
            	service = CloudServiceManager.getService(listIndex);
            	if (service != null) {
            	    Intent viewIntent = new Intent(this, SettingsActivity.class);
            	    //viewIntent.putExtra(CloudServiceManager.SERVICE_INDEX, position);
            	    CloudServiceManager.setSelectedService(service);
        		    startActivityForResult(viewIntent, 55);             	
                    return true;   
            	}
            	return true;   
            case ADD_ID:
            	addServiceRequest();
            	return true;                       	
        }
        return super.onContextItemSelected(item);
    }
    
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	if (CloudServiceManager.getServiceCount() > 0) {
    		CloudService service = CloudServiceManager.getService(position);
    	    if (service != null) {
	    	    CloudServiceManager.setSelectedService(service);
	    	    Intent viewIntent = new Intent(this, ServiceListActivity.class);
	    	    startActivityForResult(viewIntent, 55);
	    	    /*
	    	    if (service.getType() == CloudServiceOld.TYPE.OCCI) {
	    	    	Intent viewIntent = new Intent(this, ServiceListActivity.class);
	    	    	startActivityForResult(viewIntent, 55);
	    	    } else {
	    	    	Intent viewIntent = new Intent(this, ChildListActivity.class);
	    	    	startActivityForResult(viewIntent, 98);
	    	    }
	    	    */
    	    }
    	} else {
    		addServiceRequest();
    	}
    }
       
    private void addServiceRequest() {
    	CloudServiceManager.clearSelectedService();
    	startActivityForResult(new Intent(this, SettingsActivity.class), 56); 
    }
    
    private void loadServers(boolean useDB) { 	
    	displayLoadingCell();
    	if (useDB) {
    		new LoadServersTask().execute((Void[]) null);
    	} else {
    		setServerList();    		
    	}
    }
    
    private void showDeleteConfirm() {
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
	                	m_cloudServerDb.deleteEntry(service.getUID()); 
	                	CloudServiceManager.removeService(service); 
	                	loadServers(false);
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
    
    private void setServerList() {
		if (CloudServiceManager.getServiceCount() == 0) {
			displayNoServersCell();
		} else {
			getListView().setDividerHeight(3); // restore divider lines 
			setListAdapter(new ServerAdapter());
			performServerDBMaintenence();
		}
    }
    
    private void performServerDBMaintenence() {
    	Log.d(TAG, " Performing System Cleanup");
    	CloudService[] temp = CloudServiceManager.processDeletedServices();
    	for (int i = 0; i < temp.length; i++) {
    		if (temp[i] != null) {
    			Log.d(TAG, "Deleting Server " + temp[i].getName());
    			m_cloudServerDb.deleteEntry(temp[i].getUID());
    		}
    	}
    	temp = CloudServiceManager.getServices();
    	for (int i = 0; i < temp.length; i++) {
            if (temp[i].getUID() == CloudService.NO_ID) {
    			//Remember this user added Service with DB
    			String title = temp[i].getName();
    			String url = temp[i].getURL();
    			String user = "";
    			String key = "";
    			CloudAccount acct = temp[i].getAccount();
    			if (acct != null) {
    				user = acct.getUsername();
    				key = acct.getUserToken();
    			}
    			String type = temp[i].getClass().getName(); //getTypeAsString();
    			long index = m_cloudServerDb.createEntry(
    				m_cloudServerDb.buildContent(title, url, user, key, type));
    			temp[i].setUID(index);
    			Log.d(TAG, "Registering Server " + temp[i].getName() + " type " + type);
    		}
    	}
    }
        
    private void displayLoadingCell() {
    	String a[] = new String[1];
    	a[0] = "Loading Connections...";
        setListAdapter(new ArrayAdapter<String>(this, R.layout.loadingcell, R.id.loading_label, a));
        getListView().setTextFilterEnabled(true);
        getListView().setDividerHeight(0); // hide the dividers so it won't look like a list row
        getListView().setItemsCanFocus(false);
    }
    
    private void displayNoServersCell() {
    	String a[] = new String[1];
    	a[0] = "No Cloud Connections";
        setListAdapter(new ArrayAdapter<String>(this, R.layout.noserverscell, R.id.no_servers_label, a));
        getListView().setTextFilterEnabled(true);
        getListView().setDividerHeight(0); // hide the dividers so it won't look like a list row
        getListView().setItemsCanFocus(false);
    }   
        
    private class LoadServersTask extends AsyncTask<Void, Void, ArrayList<CloudService>> {    	
    	
		@Override
		protected ArrayList<CloudService> doInBackground(Void... arg0) {
			ArrayList<CloudService> servers = new ArrayList<CloudService>();
			
	        Cursor sCur = m_cloudServerDb.fetchAllEntries();
	        startManagingCursor(sCur);
	        sCur.moveToFirst();
	        while (sCur.isAfterLast() == false) {
	        	String tName = sCur.getString(1);
	        	String tURL = sCur.getString(2);	        	
	        	String tUser = sCur.getString(3);
	        	String tToken = sCur.getString(4);
	        	String type = sCur.getString(5);
	        	CloudService service = CloudServiceRegistry.generateCloudServiceByClass(type);
	        	if (service != null) {
	        		service.setName(tName);
	        		service.setURL(tURL);
	        		service.setAccount(new CloudAccount(tUser, tToken));
	        		service.setUID(sCur.getInt(0));
		        	servers.add(service);	        			        		
	        	}
	            sCur.moveToNext();
	        }
			return servers;
		}
    	
		@Override
		protected void onPostExecute(ArrayList<CloudService> result) {
			CloudServiceManager.clearServices();
			for (int i = 0; i < result.size(); i++) {
				CloudServiceManager.addService(result.get(i));
			}
			setServerList();
		}
    }
    
    @Override 
    public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.servers_list_menu, menu);
		return true;
	} 
       
    @Override 
    public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add_service:	
			addServiceRequest();
			return true;
		}
		return false;
	} 
    
	class ServerAdapter extends ArrayAdapter<CloudService> {
		ServerAdapter() {
			super(ServicesListActivity.this, 
			    R.layout.listservicescell, CloudServiceManager.getServices());
		}
		
		public View getView(int position, View convertView, ViewGroup parent) {			
			CloudService service = CloudServiceManager.getService(position); 
			LayoutInflater inflater = getLayoutInflater();
			View row = inflater.inflate(R.layout.listservicescell, parent, false);
			TextView label = (TextView) row.findViewById(R.id.label);
			label.setText(service.getName());			
			TextView sublabel = (TextView) row.findViewById(R.id.sublabel);
			sublabel.setText(service.getURL());
			return(row);
		}
	}
    
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);	
	  if (resultCode == RESULT_OK) {	  
		  loadServers(false);
	  }
	}		
}
