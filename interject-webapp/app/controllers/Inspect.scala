package controllers

import play.api._
import play.api.mvc._
import play.api.libs.ws.WS
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.iteratee._
import scala.concurrent._
import java.io.InputStream
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

object Inspect extends Controller {
  
  val tika = new Tika();

  def inspect(url: String) = Action { implicit request =>
    
    // get file name from url and store it
    val filename = FilenameUtils.getName(url)
    val urlResource = new URL(url);
    val urlConnection = urlResource.openConnection();
    val serverContentType = urlConnection.getContentType();
    
    // 1. identify contents using Apache Tika
    Logger.info("Running Tika on "+filename);
    val mimeType = tika.detect(urlConnection.getInputStream(), filename);
    Logger.info("Got mimeType : " + mimeType)
    val metadata = Actions.parseURL(url);
    
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
		    var fileObject = new FileObject(filename, mimeType, serverContentType, metadata, actions);
		    
		    Ok(views.html.inspect(url, fileObject));
		    
    } catch {
      
      case e: ConfigException => println("do something else " + e)
      val inputStream = new URL(url).openStream();
      val bytes = IOUtils.toByteArray(inputStream)
      // #VRML V1.0 ascii
      // #VRML V2.0 utf8

      println(new String(bytes, "UTF-8"))
      NotFound("")
    }
  }
}