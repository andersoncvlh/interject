/**
 * 
 */
package models;

import java.util.List;
import java.util.SortedSet;

import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeType;

import uk.gov.nationalarchives.droid.core.signature.FileFormat;

/**
 * @author Andrew Jackson <Andrew.Jackson@bl.uk>
 *
 */
public class ContentType {

	private MediaType type;
	private MimeType mimeType;
	private MediaType parentType;
	private List<MediaType> childTypes;
	private SortedSet<MediaType> aliases;
	private List<FileFormat> droidFormats;
	private List<ActionObject> actions;

	public ContentType(MediaType fulltype, MimeType mt, MediaType parentType,
			SortedSet<MediaType> aliases, List<MediaType> childTypes, List<FileFormat> dffs, List<ActionObject> actions) {
		this.type = fulltype;
		this.mimeType = mt;
		this.parentType = parentType;
		this.aliases = aliases;
		this.childTypes = childTypes;
		this.droidFormats = dffs;
		this.actions = actions;
	}
	
	/**
	 * @return the type
	 */
	public MediaType getType() {
		return type;
	}

	/**
	 * @return the mimeType
	 */
	public MimeType getMimeType() {
		return mimeType;
	}

	/**
	 * @return the parentType
	 */
	public MediaType getParentType() {
		return parentType;
	}

	/**
	 * @return the aliases
	 */
	public SortedSet<MediaType> getAliases() {
		return aliases;
	}

	/**
	 * @return the childTypes
	 */
	public List<MediaType> getChildTypes() {
		return childTypes;
	}

	/**
	 * @return the droidFormats
	 */
	public List<FileFormat> getDroidFormats() {
		return droidFormats;
	}

	/**
	 * @return the actions
	 */
	public List<ActionObject> getActions() {
		return actions;
	}
	
}
