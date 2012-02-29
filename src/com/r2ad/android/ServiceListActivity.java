package com.r2ad.android;

import com.r2ad.cloud.http.CloudService;
import com.r2ad.cloud.http.CloudServiceManager;
import com.r2ad.cloud.model.CloudType;
import com.r2ad.cloud.model.CloudTypeMap;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ServiceListActivity extends ListActivity {

	private ServiceContent[] content;
	private CloudService m_cloudService;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	m_cloudService = CloudServiceManager.getSelectedService();
    	if (m_cloudService != null) {
    		setTitle(m_cloudService.getName() + " Service");
    	}
    	loadService();
    }	
    
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	Intent viewIntent = new Intent(this, SettingsActivity.class);
    	if (position == 0) {
    		viewIntent = new Intent(this, ComputeListActivity.class);
    		startActivityForResult(viewIntent, 431); 
    	} else if (position == 1) {
    		viewIntent = new Intent(this, StorageListActivity.class);
    	} 
    	startActivityForResult(viewIntent, 433); 
    }    
    
    private void loadService() {
    	displayLoadingCell();
    	new LoadWrappersTask().execute((Void[]) null);
    }
    
    private void serviceLoaded() {
    	int computerCount = CloudTypeMap.count(m_cloudService, CloudType.TYPE.COMPUTE);
    	int storageCount = CloudTypeMap.count(m_cloudService, CloudType.TYPE.STORAGE);
    	content = new ServiceContent[3];
    	content[0] = new ServiceContent("Computers", computerCount, R.drawable.ic_tab_computers);
    	content[1] = new ServiceContent("Storage", storageCount, R.drawable.ic_tab_storage);
    	content[2] = new ServiceContent("Settings", -1, R.drawable.settings_button);
    	getListView().setDividerHeight(3); // restore divider lines 
    	setListAdapter(new WrapperAdapter());
    }
    
    private void displayLoadingCell() {
    	String a[] = new String[1];
    	a[0] = "Loading Service Content...";
        setListAdapter(new ArrayAdapter<String>(this, R.layout.loadingcell, R.id.loading_label, a));
        getListView().setTextFilterEnabled(true);
        getListView().setDividerHeight(0); // hide the dividers so it won't look like a list row
        getListView().setItemsCanFocus(false);
    }
       
    private class ServiceContent {
    	private String name;
    	private int count;
    	private int iconID;
    	public ServiceContent(String name, int count, int iconID) {
    		this.name = name;
    		this.count = count;
    		this.iconID = iconID;
    	}
    	
    	public int getCount() {
    		return count;
    	}
    	
    	public String getName() {
    		return name;
    	}
    	
    	public int getIconID() {
    		return iconID;
    	}
    }
    
    private class LoadWrappersTask extends AsyncTask<Void, Void, Boolean> {
	  	
		@Override
		protected Boolean doInBackground(Void... arg0) {
			if (m_cloudService != null) {
				m_cloudService.retrieve();
			}
			return new Boolean(true);
		}
    	
		@Override
		protected void onPostExecute(Boolean result) {
			serviceLoaded();
		}
    }
      
	class WrapperAdapter extends ArrayAdapter<ServiceContent> {
		WrapperAdapter() {
			super(ServiceListActivity.this, R.layout.listservice, content);
		}
		
		public View getView(int position, View convertView, ViewGroup parent) {
			
			ServiceContent wrapper = content[position];
			LayoutInflater inflater = getLayoutInflater();
			View row = inflater.inflate(R.layout.listservice, parent, false);

			TextView label = (TextView) row.findViewById(R.id.option);
			int count = wrapper.getCount();
			if (count >= 0) {
				label.setText(wrapper.getName() + " (" + count + ")");
			} else {
				label.setText(wrapper.getName());
			}			
		
			ImageView icon = (ImageView) row.findViewById(R.id.icon);
			icon.setImageResource(wrapper.getIconID());

			return(row);
		}
	}
    
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);
	  loadService();
	}	
}
