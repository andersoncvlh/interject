/**
 * 
 */
package controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import models.ActionObject;

import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.IOUtils;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
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
	
	public static List<ActionObject> loadActions( String contentType ) {
		HashMap<String,ActionObject> actions = new HashMap<String,ActionObject>();
		Config conf = ConfigFactory.load();
		
		MediaType type = MediaType.parse(contentType);
		Logger.info("Looking for actions that match type: "+type);

		for( Config a : conf.getConfigList("actions") ) {
			ActionObject ao = new ActionObject(a);
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

	/**
	 * TODO In the future, this should probably shift to the same core as the warc-discovery indexer.
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 * @throws SAXException
	 * @throws TikaException
	 */
	public static Metadata parseURL( String url ) throws IOException, SAXException, TikaException {
		ParseContext context = new ParseContext();
		StringWriter content = new StringWriter();
		
		Metadata md = new Metadata();
		md.add(Metadata.RESOURCE_NAME_KEY, url);
		URL urlResource = new URL(url);
		
		// Parse it:
		Tika tika = new Tika();
		tika.getParser().parse( urlResource.openStream(), new ToTextContentHandler(content), md, context );
		
		md.add("TEXT", content.toString());
		
		return md;
		
	}
	
}

