package controllers

import play.api._
import play.api.mvc._
import play.api.libs.ws.WS
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.iteratee._
import scala.concurrent._
import java.io.InputStream
import java.io.StringWriter
import java.net.URL
import org.apache.tika.Tika
import org.apache.tika.metadata.Metadata
import org.apache.tika.sax.BodyContentHandler
import com.typesafe.config.ConfigFactory
import com.typesafe.config.Config
import scala.collection.Map
import scala.collection.JavaConversions._
import scala.collection.mutable.ArrayBuffer
import uk.bl.wa.interject.factory.InterjectionFactory
import org.apache.commons.io.FilenameUtils
import com.typesafe.config.ConfigException
import org.apache.commons.io.IOUtils
import models.ActionObject
import models.FileObject
import models.Inspection

object Inspect extends Controller {
  
  def inspect(url: String) = Action { implicit request =>
    
    // get file name from url and store it
    val filename = FilenameUtils.getName(url)
    
    // 1. identify contents using Apache Tika
    Logger.info("Running Tika on "+filename);
    val metadata = new Inspection(url);
    val mimeType = metadata.getContentType();
    Logger.info("Got mimeType : " + mimeType)
    
    try {
	
	    // 2. look up problem types
//	    val interjection = InterjectionFactory.INSTANCE.findProblemType(mimeType);
//	    if (interjection != null) {
//	      var redirectUrl = new StringBuilder(interjection.getRedirectUrl());
//	      redirectUrl.append("?url=").append(url);
//	      redirectUrl.append("&sourceContentType=");
//	      redirectUrl.append(mimeType);
//	      println("Redirecting: " + redirectUrl.toString());
//	      Redirect(routes.Application.index()+redirectUrl.toString().substring(1));
//	    } else {
      
		    val absolutePrefix = routes.Application.index().absoluteURL();
		    Logger.info("Got absolutePrefix: "+absolutePrefix);
		    
		    var actions = Actions.loadActions(mimeType, absolutePrefix);
		    var fileObject = new FileObject(filename, metadata, actions);
		    
		    Ok(views.html.inspect(url, fileObject));
		    
    } catch {
      
      case e: ConfigException => println("do something else " + e)
      NotFound("")
    }
  }
}