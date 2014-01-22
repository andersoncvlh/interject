package models;

import com.typesafe.config.Config;

public class ActionObject implements Comparable<ActionObject> {
	
	private final String action;
	private String name;
	private final String description;
	private final String imageUrl;
	private String prefix;
	
	public ActionObject(final Config params, final String prefix) {
		this.action = params.getString("action");
		this.name = params.getString("name");
		this.description = params.getString("description");
		this.imageUrl = params.getString("imageUrl");
		this.prefix = prefix;
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
	
	
	@Override
	public int compareTo(ActionObject o) {
		return action.compareTo(o.action);
	}

}