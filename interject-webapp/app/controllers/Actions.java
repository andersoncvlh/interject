/**
 * 
 */
package controllers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import models.ActionObject;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.IOUtils;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.ToTextContentHandler;
import org.xml.sax.SAXException;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import play.*;
import play.mvc.*;
import uk.bl.wa.access.qaop.QaopShot;

/**
 * @author Andrew Jackson <Andrew.Jackson@bl.uk>
 *
 */
public class Actions extends Controller {

    public static Result index() {
        return ok("It works!");
    }
    
    // Should cache properly: http://www.playframework.com/documentation/2.2.1/JavaCache
    // Should async properly: http://www.playframework.com/documentation/2.2.1/JavaAsync
    
    public static Result qaop( String urlparam ) throws IOException { 
    	Logger.warn("URL Param: "+urlparam);
    	URL url = new URL(urlparam);
    	String filename = FilenameUtils.getName(urlparam);
    	File input = File.createTempFile("input", filename);
    	Logger.info("Writing to temp file: "+input.getAbsolutePath());
    	IOUtils.copy(url.openStream(), new FileOutputStream(input));
    	File tmp = File.createTempFile("spectrum", ".png");
    	QaopShot.takeScreenshot(input.getAbsolutePath(), tmp.getAbsolutePath(), 5);
    	Logger.info("Setting headers...");
    	response().setHeader("Content-Disposition", "inline;");
    	response().setContentType("image/png");
    	return ok(tmp);
    }

    
	/* ---- */
	
    /* Static code for managing types and actions */
    
    /* ---- */
    
	static MimeTypes mimeTypes = TikaConfig.getDefaultConfig().getMimeRepository();
	
	public static List<ActionObject> loadActions( String contentType, String prefix ) {
		HashMap<String,ActionObject> actions = new HashMap<String,ActionObject>();
		Config conf = ConfigFactory.load();
		
		MediaType type = MediaType.parse(contentType);
		Logger.info("Looking for actions that match type: "+type);

		for( Config a : conf.getConfigList("actions") ) {
			ActionObject ao = new ActionObject(a, prefix);
			//Logger.debug("Looking at action: "+ao.action);
			
			// Look for a match:
			for( String in : a.getStringList("types.in")) {
				MediaType inType = MediaType.parse(in);
				
				if( type.equals(inType) || hasMatchingSupertype(type,inType) ) {
					// Add to the list of options:
					actions.put( ao.getAction(), ao);
				}
			}
		}
		List<ActionObject> actionList = new ArrayList<ActionObject>( actions.values() );
		Collections.sort(actionList);
		return actionList;
	}
	
	private static boolean hasMatchingSupertype(MediaType type, MediaType inType) {
		// Get the Tika type tree:
		MediaType superType = mimeTypes.getMediaTypeRegistry().getSupertype(type);
		
		// IF there's a super-type, see it if matches:
		if( superType != null ) {
			if( superType.equals(inType) ) return true;
			// Otherwise, look for next supertype.
			return hasMatchingSupertype(superType, inType);
		}
		
		return false;
	}
	
	public static MimeType lookupMimeType( String mimeType ) {
		MimeType mt = null;
		try {
			mt = mimeTypes.getRegisteredMimeType(mimeType);
		} catch ( MimeTypeException e ) {
			Logger.warn("Could not find registered type: "+mimeType);
		}
		try {
			mt = mimeTypes.forName(mimeType);
		} catch ( MimeTypeException e ) {
			Logger.warn("Could not create type: "+mimeType);
		}
		
		Logger.info("Got mimeType: "+mt);
		if( mt == null ) return null;
		
 		Logger.info("Got mimeType: "+mt.getName());
		Logger.info("Got mimeType: "+mt.getType());
		Logger.info("Got mimeType: "+mt.getAcronym());
		Logger.info("Got mimeType: "+mt.getDescription());
		Logger.info("Got mimeType: "+mt.getExtension());
		Logger.info("Got mimeType: "+mt.getExtensions());
		Logger.info("Got mimeType: "+mt.getUniformTypeIdentifier());
		Logger.info("Got mimeType: "+mt.getLinks());
		
		return mt;
	}

	private static Tika tika = new Tika();
	
	/**
	 * TODO Currently opens the connection twice. In the future, this should probably shift to the same core as the warc-discovery indexer, as that would enhance the metadata and prevent multiple downloads.
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 * @throws SAXException
	 * @throws TikaException
	 */
	public static Metadata parseURL( String url, Writer content ) throws IOException, SAXException, TikaException {
		ParseContext context = new ParseContext();
		
		Metadata md = new Metadata();
		md.add(Metadata.RESOURCE_NAME_KEY, url);
		URL urlResource = new URL(url);
		URLConnection urlConnection = urlResource.openConnection();
		md.add("Server-Content-Type", urlConnection.getContentType());
		
		// Detect:
	    byte[] bytes = IOUtils.toByteArray(urlConnection.getInputStream());
		String tikaType = tika.detect(bytes, url);
		Logger.info("Dectected input type: "+tikaType);
		Logger.info("Head: "+Base64.encodeBase64String(Arrays.copyOf(bytes,100)));
		Logger.info("Tail: "+Base64.encodeBase64String(Arrays.copyOfRange(bytes,bytes.length-10, bytes.length)));
		
		// Parse it:
		tika.getParser().parse( new ByteArrayInputStream(bytes), new ToTextContentHandler(content), md, context );
		
		// Repair content type!
		md.set("Content-Type", tikaType);
		
		return md;
		
	}
	
}

