/**
 * 
 */
package models;

import java.util.List;
import java.util.SortedSet;

import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeType;

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

	public ContentType(MediaType fulltype, MimeType mt, MediaType parentType,
			SortedSet<MediaType> aliases, List<MediaType> childTypes) {
		this.type = fulltype;
		this.mimeType = mt;
		this.parentType = parentType;
		this.aliases = aliases;
		this.childTypes = childTypes;
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

}
