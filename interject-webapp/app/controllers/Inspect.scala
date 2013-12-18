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

object Inspect extends Controller {
  
  def inspect(url: String) = Action {
	  // http://www.webarchive.org.uk/interject/action/inspect/http://web.archive.org/web/19991001051504/http://wkweb1.cableinet.co.uk:80/malkc/Wheelie.tap
	  // 1. identify contents using Apache Tika
	  val tika = new Tika();
	  var mimeType = tika.detect(url);
	  
	  println("mimeType : " + mimeType)

	  // 2. look up list of actions based on type - DO WE NEED THIS?
	  val interjection = InterjectionFactory.INSTANCE.findProblemType(mimeType);
	  if (interjection != null) {
	    println("interjection : " + interjection)
	  }
	  
	  // 3. Get list of actions based on mime type
	  
	  val config = ConfigFactory.load()
	  // A scala list of SimpleConfigObjects
	  // just a test for speccy
	  val options = "." + mimeType
	  println("Getting actions for mime type : " + options);
	  var actions = config.getConfigList("mime.type.actions" + options).map(new ActionObject(_))
	  println(actions.getClass.getName)

	  actions.foreach(e => {
		  println(mimeType + " " + e.getAction() + " " + e.getDescription())
	  })

//	  println("mimeType: " + mimeType + " " + interjection.getRedirectUrl() + " " + interjection.getMimeType())
//	  Redirect("/action/" + action + "/" + url)
	  
	  println("forwarding url : " + url);
	  Ok(views.html.inspect(url, actions));

	  
  }  
}