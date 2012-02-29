package com.r2ad.cloud.occi;

import com.r2ad.cloud.actions.ActionObserver;
import com.r2ad.cloud.actions.CloudAction;

public class OCCIAction implements CloudAction {

	private String name;
	private String details;
	private int iconID;
	private boolean enabled;
	
	public OCCIAction(String name) {
		this.name = name;
		this.details = name + " details TBD";
		iconID = 0;
		enabled = true;
	}
	
	public String getName() {
		return name;
	}
	public String getDetails() {
		return details;
	}
	public int getIconId() {
		return iconID;
	}
	public boolean isEnabled() {
		return enabled;
	}
	
	public void invokeAction(ActionObserver observer) {		
	}
}
