package models;

import java.util.List;

public class FileObject {
	
	private final String filename;
	private final String mimeType;
	private final String contentType;
	private final List<ActionObject> actions;
	
	public FileObject(String filename, String mimeType, String contentType, List<ActionObject> actions) {
		this.filename = filename;
		this.mimeType = mimeType;
		this.contentType = contentType;
		this.actions = actions;
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
	
	public List<ActionObject> getActions() {
	  return actions;
	}
}