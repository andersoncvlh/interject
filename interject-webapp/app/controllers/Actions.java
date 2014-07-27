/**
 * 
 */
package controllers;

import iicm.vrml.vrml2x3d.vrml2x3d;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.concurrent.Callable;

import models.ActionObject;
import models.ContentType;
import models.Inspection;

import org.apache.commons.io.FilenameUtils;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.io.IOUtils;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;

import play.Logger;
import play.cache.Cache;
import play.mvc.Controller;
import play.mvc.Result;
import uk.bl.wa.access.qaop.QaopShot;
import uk.gov.nationalarchives.droid.core.signature.FileFormat;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * @author Andrew Jackson <Andrew.Jackson@bl.uk>
 *
 */
public class Actions extends Controller {

	public static final String URL_PREFIX_KEY = "application.urlprefix";

    private static final int DURATION = 3600*24; // 24 hour cache.

	private static Config conf = ConfigFactory.load();

	public static Result view(String actionStr, String url) {
		String absolutePrefix = routes.Application.index().absoluteURL(
				request());
		if (conf.hasPath(URL_PREFIX_KEY)) {
			absolutePrefix = conf.getString(URL_PREFIX_KEY);
		}
        Logger.info("Got absolutePrefix: " + absolutePrefix);

		ActionObject action = loadAction(actionStr, absolutePrefix);
		Logger.info("Matched action: " + action.getAction() + " + "
				+ action.getActionURL());
		return ok(views.html.view.render(action, url));
	}

	public static Result act( String action, String url ) {
        return ok("ACT: "+action+" ON "+url);
	}
	
	public static Result index() {
        return ok("It works!");
    }
    
    public static Result qaop( final String urlparam ) throws Exception { 
    	Logger.warn("URL Param: "+urlparam);
    	File tmp = Cache.getOrElse("qaop-" + urlparam, new Callable<File>() {
            @Override
            public File call() throws Exception {
            	URL url = new URL(urlparam);
            	String filename = FilenameUtils.getName(urlparam);
            	File input = File.createTempFile("input", filename);
				input.deleteOnExit();
            	Logger.info("Writing to temp file: "+input.getAbsolutePath());
            	IOUtils.copy(url.openStream(), new FileOutputStream(input));
            	File tmp = File.createTempFile("spectrum", ".png");
				tmp.deleteOnExit();
            	QaopShot.takeScreenshot(input.getAbsolutePath(), tmp.getAbsolutePath(), 5);
            	Logger.info("Setting headers...");
            	return tmp;
            }
        }, DURATION );
    	// Caching the whole Result 'ok(tmp)' doesn't work due to stream-reuse failing I think.
    	response().setHeader("Content-Disposition", "inline;");
    	response().setContentType("image/png");
    	return ok(tmp);
    }
    
	/**
	 * 
	 * @param urlparam
	 * @return
	 * @throws Exception
	 */
	public static Result vrml1to97(final String urlparam) throws Exception {
		Logger.warn("VRML1to97 URL Param: " + urlparam);
		File tmp = Cache.getOrElse("vrml1to97-" + urlparam,
				new Callable<File>() {
					@Override
					public File call() throws Exception {
						URL url = new URL(urlparam);
						return vrml1to97(url);
					}
				}, DURATION);
		response().setHeader("Content-Disposition", "inline;");
		response().setContentType("model/vrml; version=2.0");
		return ok(tmp);
	}

	/**
	 * 
	 * @param urlparam
	 * @return
	 * @throws Exception
	 */
	public static Result vrml97toX3D(final String urlparam) throws Exception {
		Logger.warn("VRML97toX3D URL Param: " + urlparam);
		final URL url = new URL(urlparam);
		File tmp = Cache.getOrElse("vrml97toX3D-" + urlparam,
				new Callable<File>() {
					@Override
					public File call() throws Exception {
						return vrml97toX3D(url);
					}
				}, DURATION);
		// TODO Fix up the name correctly, adding extension to existing
		// filename:
		response().setHeader("Content-Disposition",
				"inline;filename=converted.x3d");
		response().setContentType("model/x3d+xml");
		return ok(tmp);
	}

	/**
	 * Code to invoke the command on some temp files.
	 * 
	 * @param url
	 * @return
	 */
	public static File vrml1to97(final URL url) {
		try {
			//
			File tempIn = File.createTempFile("to97in", ".wrl");
			tempIn.deleteOnExit();
			IOUtils.copy(url.openStream(), new FileOutputStream(tempIn));
			//
			File tempOut = File.createTempFile("to97out", ".wrl");
			tempOut.deleteOnExit();
			// Assemble the command:
			String[] command = new String[] {
					"wine",
					"../interject-access-external-tools/ivTools-3.0/ivvrml.exe",
					"-2", tempIn.getAbsolutePath(), "-o",
					tempOut.getCanonicalPath() };
			// Execute:
			ProcessBuilder pb = new ProcessBuilder(command);
			pb.inheritIO();
			Logger.warn("EXECUTING: " + pb.command());
			Process p = pb.start();
			int rc = p.waitFor();
			// Return:
			tempIn.delete();
			return tempOut;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static File vrml97toX3D(final URL url) {
		try {
			//
			File tempIn = File.createTempFile("toX3Din", ".wrl");
			tempIn.deleteOnExit();
			IOUtils.copy(url.openStream(), new FileOutputStream(tempIn));
			//
			File tempOut = File.createTempFile("toX3Dout", ".x3d");
			tempOut.deleteOnExit();
			// Perform the transformation:
			vrml2x3d.main(new String[] { tempIn.getAbsolutePath(),
					tempOut.getAbsolutePath() });
			// Return:
			tempIn.delete();
			return tempOut;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Result view3dsceneToPNG(final String urlparam)
			throws Exception {
		Logger.warn("view3dsceneToPNG URL Param: " + urlparam);
		final URL url = new URL(urlparam);
		File tmp = Cache.getOrElse("view3dsceneToPNG-" + urlparam,
				new Callable<File>() {
					@Override
					public File call() throws Exception {
						return view3dsceneToPNG(url);
					}
				}, DURATION);
		// TODO Fix up the name correctly, adding extension to existing
		// filename:
		response().setHeader("Content-Disposition",
				"inline;filename=converted.png");
		response().setContentType("image/png");
		return ok(tmp);
	}

	/**
	 * 
	 * @param url
	 * @return
	 */
	public static File view3dsceneToPNG(final URL url) {
		try {
			// Need the right extension to work, which is not always given.
			File tempIn = File.createTempFile("to97in",
					"." +
					FilenameUtils.getExtension(url.getPath()));
			tempIn.deleteOnExit();
			IOUtils.copy(url.openStream(), new FileOutputStream(tempIn));
			//
			File tempOut = File.createTempFile("to97out", ".png");
			tempOut.deleteOnExit();
			// Assemble the command:
			String[] command = new String[] {
					"../interject-access-external-tools/view3dscene",
					tempIn.getAbsolutePath(), "--screenshot", "0",
					tempOut.getCanonicalPath() };
			// Execute:
			ProcessBuilder pb = new ProcessBuilder(command);
			pb.inheritIO();
			Logger.warn("EXECUTING: " + pb.command());
			Process p = pb.start();
			int rc = p.waitFor();
			// Return:
			tempIn.delete();
			return tempOut;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	/* -------- -------- -------- -------- -------- -------- -------- */

    public static Result allTypes() {
    	return types( "application/octet-stream" );
    }
    
	public static Result allActions() {
		List<ActionObject> actions = loadAllActions("");
		return ok(views.html.actions.render(actions));
	}

    public static Result types( String type ) {
    	// No type supplied:
//    	if( type == null ) {
//    		Logger.info("Should return list of all types.");
//    		ok();
//    	}
    	// Only type supplied:
//    	if (subtype == null ) {
//    		Logger.info("Should return list of all subtypes.");
//    		ok();
//    	}
    	// The ';' is actually valid to interpret as '&', so change it back:
    	type = type.replaceFirst("&",";");
    	type = type.replaceFirst("%20"," ");
    	Logger.info("Got type string "+type);
    	MediaType fulltype = MediaType.parse(type);
    	Logger.info("Got fulltype "+fulltype);
    	Logger.info("Looking up "+fulltype.getBaseType());
    	MimeType mt = null;
    	try {
			mt = mimeTypes.getRegisteredMimeType(fulltype.getBaseType().toString());
		} catch (MimeTypeException e) {
			Logger.error("Unknown type.");
			e.printStackTrace();
		}
    	
    	MediaType parent = mimeTypes.getMediaTypeRegistry().getSupertype(fulltype);
    	SortedSet<MediaType> aliases = mimeTypes.getMediaTypeRegistry().getAliases(fulltype);
		
		List<MediaType> childTypes = new ArrayList<MediaType>();
		for( MediaType at : mimeTypes.getMediaTypeRegistry().getTypes() ) {
			if( fulltype.equals(mimeTypes.getMediaTypeRegistry().getSupertype(at)) ) {
				childTypes.add(at);
			}
			// Also build alias back-links:
			for( MediaType alias : mimeTypes.getMediaTypeRegistry().getAliases(at) ) {
				if( fulltype.equals(alias) ) {
					aliases.add(at);
				}
			}
		}
		
		// DROID info for this type:
		List<FileFormat> dffs = Inspection.getDroidFormatsForMediaType(fulltype);
		
		// Actions
		List<ActionObject> actions = loadActions(type,"");
		
		// Build the page:
    	return ok( views.html.types.render( new ContentType(fulltype, mt, parent, aliases, childTypes, dffs, actions) ) );
    }

    
	/* ---- */
	
    /* Static code for managing types and actions */
    
    /* ---- */
    
	static MimeTypes mimeTypes = TikaConfig.getDefaultConfig().getMimeRepository();
	
	public static ActionObject loadAction(String actionId, String prefix) {

		for (Config a : conf.getConfigList("actions")) {
			ActionObject ao = new ActionObject(a, prefix);
			if (actionId.equals(ao.getAction()))
				return ao;
		}
		return null;
	}

	public static List<ActionObject> loadActions( String contentType, String prefix ) {
		HashMap<String,ActionObject> actions = new HashMap<String,ActionObject>();
		
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
	
	public static List<ActionObject> loadAllActions(String prefix) {
		HashMap<String, ActionObject> actions = new HashMap<String, ActionObject>();

		for (Config a : conf.getConfigList("actions")) {
			ActionObject ao = new ActionObject(a, prefix);
			actions.put(ao.getAction(), ao);
		}
		List<ActionObject> actionList = new ArrayList<ActionObject>(
				actions.values());
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
		
		return mt;
	}

}

