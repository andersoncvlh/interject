package models;

import java.util.List;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MimeType;

import controllers.Actions;

public class FileObject {
	
	private final String filename;
	private Inspection inspection;
	private final List<ActionObject> actions;
	
	public FileObject(String filename, Inspection inspection, List<ActionObject> actions) {
		this.filename = filename;
		this.inspection = inspection;
		this.actions = actions;
	}

	public String getFilename() {
		return filename;
	}
	
	public Inspection getInspection() {
		return this.inspection;
	}
 	
	public String getContentType() {
		return inspection.getContentType();
	}
	
	public MimeType getContentTypeDetails() {
		return Actions.lookupMimeType(inspection.getContentType());
	}
	
	public String getServedContentType() {
		return inspection.getServerContentType();
	}
	
	public MimeType getServedContentTypeDetails() {
		return Actions.lookupMimeType(inspection.getServerContentType());
	}
	
	public Metadata getMetadata() {
		return inspection.getMetadata();
	}
	
	public List<ActionObject> getActions() {
	  return actions;
	}

	public int getNumberOfOptions() {
		if (actions == null)
			return 0;
		return actions.size();
	}
}