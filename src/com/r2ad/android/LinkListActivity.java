package com.r2ad.android;

import java.net.URI;

import com.r2ad.cloud.model.CloudLinkType;
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

public class LinkListActivity extends ListActivity {
	
	private URI[] links = new URI[0];
	private CloudLinkType m_LinkType;
    //private static final int ADD_ID = Menu.FIRST + 1;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_LinkType = (CloudLinkType)CloudTypeMap.getSelectedType();
        loadLinks();
    	//registerForContextMenu(getListView());
    }	
    
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	/*
    	if (wrappers != null && wrappers.length > 0) {
	    	Intent viewIntent = new Intent(this, ComputeDetailActivity.class);
	    	CloudListingManager.setSelectedWrapper(wrappers[position]);
			startActivityForResult(viewIntent, 55); // arbitrary number; never used again
    	} else {
			CloudListingManager.clearSelectedWrapper();
			startActivityForResult(new Intent(this, AddComputeActivity.class), 34); // arbitrary number; never used again
    	}
    	*/
    }
    
    /*
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle(m_LinkType.getTitle() + " Links");
        menu.add(0, ADD_ID, 0, "Add Link"); 
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	int listIndex = ((AdapterContextMenuInfo)item.getMenuInfo()).position;
        switch(item.getItemId()) {
            case ADD_ID:
                //CloudListingManager.removeWrapper(m_cloudService, wrappers[listIndex]);
                //loadComputers();
                return true;
        }
        return super.onContextItemSelected(item);
    }    
    */
    
    private void loadLinks() {
    	displayLoadingCell();
    	new LoadLinksTask().execute((Void[]) null);
    }
    
    private void setLinks(URI[] linkArray) {
    	links = linkArray;
    	if (links == null) {
    		links = new URI[0];
    	}
		if (links.length == 0) {
			displayNoLinksCell();
		} else {
			getListView().setDividerHeight(3); // restore divider lines 
			setListAdapter(new LinksAdapter());
		}
    }
    
    private void displayLoadingCell() {
    	String a[] = new String[1];
    	a[0] = "Loading Storage Actions...";
        setListAdapter(new ArrayAdapter<String>(this, R.layout.loadingcell, R.id.loading_label, a));
        getListView().setTextFilterEnabled(true);
        getListView().setDividerHeight(0); // hide the dividers so it won't look like a list row
        getListView().setItemsCanFocus(false);
    }
    
    private void displayNoLinksCell() {
    	String a[] = new String[1];
    	a[0] = "No Actions Defined";
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
    
    
    private class LoadLinksTask extends AsyncTask<Void, Void, URI[]> {    	  	
		@Override
		protected URI[] doInBackground(Void... arg0) {
			URI[] result = new URI[0];
			if (m_LinkType != null) {
				result = m_LinkType.getLinks();
			}
			return result;
		}
    	
		@Override
		protected void onPostExecute(URI[] linkArray) {
			setLinks(linkArray);
		}
    }
    
    /*
    @Override 
    public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.compute_list_menu, menu);
		return true;
	} 
	*/
    
    /*
    @Override 
    public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add_computer:
			OCCITypeMap.clearSelectedType();
			startActivityForResult(new Intent(this, AddComputeActivity.class), 34); // arbitrary number; never used again
			return true;
		case R.id.refresh:
			loadLinks();
	        return true;
		}
		return false;
	} 
	*/
    
	class LinksAdapter extends ArrayAdapter<URI> {
		LinksAdapter() {
			super(LinkListActivity.this, R.layout.listcomputecell, links);
		}
		
		public View getView(int position, View convertView, ViewGroup parent) {
			
			LayoutInflater inflater = getLayoutInflater();
			View row = inflater.inflate(R.layout.listcomputecell, parent, false);

			TextView label = (TextView) row.findViewById(R.id.label);
			String url = links[position].toString();
    		int start = url.indexOf("?action=");
    		try {
			    label.setText(url.substring(start+8, url.length()));
    		} catch (Throwable t) {
    			label.setText("Action");
    		}
			
			TextView sublabel = (TextView) row.findViewById(R.id.sublabel);
			sublabel.setText(url.substring(0, start));
			
			ImageView icon = (ImageView) row.findViewById(R.id.icon);
			icon.setImageResource(R.drawable.arch_icon);

			return(row);
		}
	}
    
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);
	  loadLinks();
	}	
}
