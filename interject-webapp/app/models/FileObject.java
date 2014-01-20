package models;

import java.util.List;

import org.apache.tika.metadata.Metadata;

import play.Logger;

public class FileObject {
	
	private final String filename;
	private final String mimeType;
	private final String contentType;
	private final List<ActionObject> actions;
	private Metadata metadata;
	
	public FileObject(String filename, String mimeType, String contentType, Metadata metadata, List<ActionObject> actions) {
		this.filename = filename;
		this.mimeType = mimeType;
		this.contentType = contentType;
		this.metadata = metadata;
		this.actions = actions;
		for( String name : metadata.names() ) {
			Logger.info("Metadata: "+ name + " => "+ metadata.get(name));
		}
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
	
	public Metadata getMetadata() {
		return metadata;
	}
	
	public List<ActionObject> getActions() {
	  return actions;
	}
}