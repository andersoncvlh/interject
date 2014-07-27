package models;

import java.util.ArrayList;
import java.util.List;

import com.typesafe.config.Config;

public class ActionObject implements Comparable<ActionObject> {
	
	private final String action;
	private String name;
	private final String description;
	private final String imageUrl;
	private String prefix;
	private List<String> formatsIn;
	private List<String> formatsOut;
	
	public ActionObject(final Config params, final String prefix) {
		this.action = params.getString("action");
		this.name = params.getString("name");
		this.description = params.getString("description");
		this.imageUrl = params.getString("imageUrl");
		this.prefix = prefix;
		this.formatsIn = new ArrayList<String>();
		for (String in : params.getStringList("types.in")) {
			this.formatsIn.add(in);
		}
		this.formatsOut = new ArrayList<String>();
		for (String out : params.getStringList("types.out")) {
			this.formatsOut.add(out);
		}
	}
	// getters, hashCode, equals, etc.
	
	public String getAction() {
		return action;
	}
	
	public String getName() {
		return name;
	}
	
	public String getActionURL() {
		if( action.startsWith("http://") || action.startsWith("https://")) 
			return action+"/";
		return prefix+action+"/";
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getImageUrl() {
		return imageUrl;
	}
	
	public List<String> getFormatsIn() {
		return this.formatsIn;
	}

	public List<String> getFormatsOut() {
		return this.formatsOut;
	}
	
	@Override
	public int compareTo(ActionObject o) {
		return action.compareTo(o.action);
	}

}