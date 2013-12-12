package controllers

import play.api._
import play.api.mvc._
import play.api.libs.ws.WS
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.iteratee._
import scala.concurrent._
import java.io.InputStream
import org.apache.tika.Tika
import uk.bl.wa.interject.factory.InterjectionFactory

object Inspect extends Controller {
  
  def inspect(url: String) = Action {
	  // TODO: the link should be passed to the interject-webapp: 
	  // http://www.webarchive.org.uk/interject/action/inspect/http://web.archive.org/web/19991001051504/http://wkweb1.cableinet.co.uk:80/malkc/Wheelie.tap
	  // 1. identify contents using Apache Tika
	  val tika = new Tika();
	  val mimeType = tika.detect(url);
	  val interjection = InterjectionFactory.INSTANCE.findProblemType(mimeType);
	  // 2. look up list of actions based on type
	  // 3.
	  Logger.debug("mimeType: " + mimeType + " " + interjection.getRedirectUrl() + " " + interjection.getMimeType())
	  Ok(views.html.jsspeccy("/action/jsspeccy/"+url));
  }  
}