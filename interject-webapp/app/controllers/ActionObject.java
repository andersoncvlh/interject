package controllers;

import com.typesafe.config.Config;

public class ActionObject {
	
	private final String action;
	private final String description;

	public ActionObject(final Config params) {
		action = params.getString("action");
		description = params.getString("description");
	}
	// getters, hashCode, equals, etc.
	
	public String getAction() {
		return action;
	}
	
	public String getDescription() {
		return description;
	}
	
}