package com.r2ad.android;

import com.r2ad.cloud.http.CloudService;
import com.r2ad.cloud.http.CloudServiceManager;
import com.r2ad.cloud.model.CloudStorageType;
import com.r2ad.cloud.model.CloudTypeMap;

import android.app.Activity;
import android.util.Log;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StorageDetailActivity extends Activity implements OnClickListener {
	
	private CloudService m_CloudService;
	private CloudStorageType m_Storage;
	private static final String TAG = "StorageDetail";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.storagedetail);
        m_CloudService = CloudServiceManager.getSelectedService();
    	if (m_CloudService != null) {
    		setTitle(m_CloudService.getName() + " Storage");
    	}        
    	m_Storage = (CloudStorageType)CloudTypeMap.getSelectedType();
        if (m_Storage != null) {
            loadServerData();
        } 
        ((Button) findViewById(R.id.stopbutton)).setOnClickListener(this);
    }       
   
    private void loadServerData() {
	    Log.d(TAG, "Loading Server Data for Storage");
    	
    	TextView name = (TextView) findViewById(R.id.view_storage_name);
    	if (name != null) name.setText(m_Storage.getTitle());
    	
    	TextView summary = (TextView) findViewById(R.id.storagesummary);
    	if (summary != null) summary.setText(m_Storage.getSummary());
    	
    	TextView size = (TextView) findViewById(R.id.storagesize);
    	if (size != null) size.setText((int)m_Storage.getSize() + " MB");
    	
    	TextView state = (TextView) findViewById(R.id.storagestate);
    	if (state != null) {
    		state.setText(m_Storage.getStatusAsString());
    		if (m_Storage.getStatus() == CloudStorageType.Status.ONLINE) {
    			state.setTextColor(Color.GREEN);
    		} else if (m_Storage.getStatus() == CloudStorageType.Status.OFFLINE) {
    			state.setTextColor(Color.YELLOW);
    		} else {
    			state.setTextColor(Color.RED);
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
			startActivityForResult(new Intent(this, AddComputeActivity.class), 234); 
			return true;
		case R.id.deletecomputer:
			CloudTypeMap.remove(m_CloudService, m_Storage);
			m_Storage = null;
			startActivityForResult(new Intent(this, ComputeListActivity.class), 238); 
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

	@Override
	public void onClick(View v) {
		startActivityForResult(new Intent(this, LinkListActivity.class), 2381);
	}	    
   
    
}

