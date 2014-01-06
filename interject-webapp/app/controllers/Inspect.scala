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

  def inspect(url: String) = Action {
    // http://www.webarchive.org.uk/interject/action/inspect/http://web.archive.org/web/19991001051504/http://wkweb1.cableinet.co.uk:80/malkc/Wheelie.tap
    // 1. identify contents using Apache Tika
    val tika = new Tika();
    val mimeType = tika.detect(url);
    // TODO: get file name from url and store it
    val filename = FilenameUtils.getName(url);
    println("mimeType : " + mimeType)

    
    try {
	    val config = ConfigFactory.load()
	
	    // 2. look up problem types
	    val interjection = InterjectionFactory.INSTANCE.findProblemType(mimeType);
	    if (interjection != null) {
	      var redirectUrl = new StringBuilder(interjection.getRedirectUrl());
	      redirectUrl.append("?url=").append(url);
	      redirectUrl.append("&sourceContentType=");
	      redirectUrl.append(mimeType);
	      println("Redirecting: " + redirectUrl.toString());
	      Redirect(redirectUrl.toString());
	    } else {
		    // 3. Get list of actions based on mime type	  
		    // A scala list of SimpleConfigObjects
		    // just a test for speccy
		    val options = "." + mimeType
		    println("Getting actions for mime type : " + options);
		    var actions = config.getConfigList("mime.type.actions" + options).map(new ActionObject(_))
		    println(actions.getClass.getName)
		
		    var fileObject = new FileObject(filename, mimeType, null, actions);
		    
//		    actions.foreach(e => {
//		      println(mimeType + " " + e.getAction() + " " + e.getDescription())
//		    })
		
		    println("forwarding url : " + url);
		    Ok(views.html.inspect(url, fileObject));
	    }
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