/**
 * 
 */
package controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FilenameUtils;
import org.apache.tika.io.IOUtils;

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

}

