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
import com.r2ad.cloud.model.CloudStorageType;
import com.r2ad.cloud.model.CloudType;
import com.r2ad.cloud.model.CloudTypeMap;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class StorageListActivity extends ListActivity {

	private CloudStorageType[] storage;
	private CloudService m_cloudService;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	m_cloudService = CloudServiceManager.getSelectedService();
    	if (m_cloudService != null) {
    		setTitle(m_cloudService.getName() + " Storage Listing");
    	}    	
    	loadStorage();
    }	
    
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	if (storage != null && storage.length > 0) {
	    	Intent viewIntent = new Intent(this, StorageDetailActivity.class);
    		CloudTypeMap.setSelectedType(storage[position]);
			startActivityForResult(viewIntent, 97); // arbitrary number; never used again
    	} else {
    		//CloudTypeMap.clearSelectedType();
			//startActivityForResult(new Intent(this, AddComputeActivity.class), 98); // arbitrary number; never used again
    	}
    }    	
    
    private void loadStorage() {
    	displayLoadingCell();
    	new LoadStorageTask().execute((Void[]) null);
    }
    
    private void setStorage(CloudStorageType[] sArray) {
    	storage = sArray;
    	if (storage == null) {
    		storage = new CloudStorageType[0];
    	}
		if (storage.length == 0) {
			displayNoServersCell();
		} else {
			getListView().setDividerHeight(3); // restore divider lines 
			setListAdapter(new StorageAdapter());
		}    	
    }
    
    private void displayLoadingCell() {
    	String a[] = new String[1];
    	a[0] = "Loading Storage Content...";
        setListAdapter(new ArrayAdapter<String>(this, R.layout.loadingcell, R.id.loading_label, a));
        getListView().setTextFilterEnabled(true);
        getListView().setDividerHeight(0); // hide the dividers so it won't look like a list row
        getListView().setItemsCanFocus(false);
    }
    
    private void displayNoServersCell() {
    	String a[] = new String[1];
    	a[0] = "No Storage Content";
        setListAdapter(new ArrayAdapter<String>(this, R.layout.nocontentcell, R.id.no_content_label, a));
        getListView().setTextFilterEnabled(true);
        getListView().setDividerHeight(0); // hide the dividers so it won't look like a list row
        getListView().setItemsCanFocus(false);
    }
    
    /*
    private void showAlert(String title, String message) {
    	//Can't create handler inside thread that has not called Looper.prepare()
    	//Looper.prepare();
    	try {
		AlertDialog alert = new AlertDialog.Builder(this).create();
		alert.setTitle(title);
		alert.setMessage(message);
		alert.setButton("OK", new DialogInterface.OnClickListener() {
	      public void onClick(DialogInterface dialog, int which) {
	        return;
	    } }); 
		alert.show();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    */
    
    
    private class LoadStorageTask extends AsyncTask<Void, Void, CloudStorageType[]> {
    	  	
		@Override
		protected CloudStorageType[] doInBackground(Void... arg0) {
			CloudStorageType[] result = null;
			if (m_cloudService != null) {
				m_cloudService.retrieve();
				Object[] temp = CloudTypeMap.get(m_cloudService, CloudType.TYPE.STORAGE);
				if (temp != null) {
					result = new CloudStorageType[temp.length];
					System.arraycopy(temp, 0, result, 0, temp.length);					                            
				}				
			}
			return result;
		}
    	
		@Override
		protected void onPostExecute(CloudStorageType[] result) {
			setStorage(result);
		}
    }
    
    @Override 
    public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.storage_list_menu, menu);
		return true;
	} 
    
    @Override 
    public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add_storage:
			//startActivityForResult(new Intent(this, AddServerActivity.class), 56); // arbitrary number; never used again
			return true;
		case R.id.refresh:
			loadStorage();
	        return true;
		}
		return false;
	} 
	//*/
    
	class StorageAdapter extends ArrayAdapter<CloudStorageType> {
		StorageAdapter() {
			super(StorageListActivity.this, R.layout.liststorage, storage);
		}
		
		public View getView(int position, View convertView, ViewGroup parent) {
			
			LayoutInflater inflater = getLayoutInflater();
			View row = inflater.inflate(R.layout.liststorage, parent, false);

			TextView label = (TextView) row.findViewById(R.id.liststorage_label);
			label.setText(storage[position].getTitle());
			
			TextView sublabel = (TextView) row.findViewById(R.id.liststorage_sublabel);
			sublabel.setText(storage[position].getSummary());
			
			ImageView icon = (ImageView) row.findViewById(R.id.liststorage_icon);
			icon.setImageResource(storage[position].getIconId());

			return(row);
		}
	}
    
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);
	  
	  if (resultCode == RESULT_OK) {	  
		  // a sub-activity kicked back, so we want to refresh the server list
		  loadStorage();
	  }
	}	
}
