package controllers;

import com.typesafe.config.Config;

public class ActionObject {
	
	private final String action;
	private final String description;
	private final String imageUrl;
	private final String filename;
	private final String mimeType;
	private final String contentType;
	
	public ActionObject(final Config params, final String filename, String mimeType, String contentType) {
		this.action = params.getString("action");
		this.description = params.getString("description");
		this.imageUrl = params.getString("imageUrl");
		this.filename = filename;
		this.mimeType = mimeType;
		this.contentType = contentType;
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
	
	public String getFilename() {
		return filename;
	}
	
	public String getMimeType() {
		return mimeType;
	}
	
	public String getContentType() {
		return contentType;
	}
}