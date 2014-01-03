package models;

import com.typesafe.config.Config;

public class ActionObject {
	
	private final String action;
	private final String description;
	private final String imageUrl;
	
	public ActionObject(final Config params) {
		this.action = params.getString("action");
		this.description = params.getString("description");
		this.imageUrl = params.getString("imageUrl");
	}
	// getters, hashCode, equals, etc.
	
	public String getAction() {
		return action;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getImageUrl() {
		return imageUrl;
	}
}