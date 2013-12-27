package controllers

import play.api._
import play.api.mvc._
import play.api.libs.ws.WS
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.iteratee._
import scala.concurrent._
import java.io._
import play.api.libs.Files
import com.typesafe.config.ConfigFactory
import com.typesafe.config.Config

import java.awt.image.BufferedImage
import java.util.HashMap
import java.lang.Boolean
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

import org.apache.commons.imaging.ImagingConstants;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.ImageFormat;
import org.apache.commons.imaging.formats.png.PngConstants;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.tika.Tika

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def passthrough(url: String) = Action.async {
    WS.url(url).get().map { response =>
      val asStream: InputStream = response.ahcResponse.getResponseBodyAsStream
      Ok.chunked(Enumerator.fromStream(asStream)).as("application/octet-stream").withHeaders(CONTENT_TRANSFER_ENCODING -> "binary")
    }
  }

  def jsspeccy(url: String) = Action {
    // call passthrough
    Ok(views.html.jsspeccy("/action/passthrough/" + url));
  }
  
  def commonsImagingConversion(url: String) = Action {
	val tika = new Tika();
    val sourceContentType = tika.detect(url);
    println("Attempting to convert: "+url+ " from: " + sourceContentType);		
	val httpclient = HttpClients.createDefault();
	val httpGet = new HttpGet(url);
	if(sourceContentType != null) {
		httpGet.addHeader("Accept", sourceContentType);
	}
	val res = httpclient.execute(httpGet);
	val is = res.getEntity().getContent();
    // read image
	val readParams = new HashMap[String, Object];
	readParams.put(ImagingConstants.PARAM_KEY_FILENAME, url);
	// Note that current version assumes TIFFs are not transparent.
    val image = Imaging.getBufferedImage(is,readParams);
    
	// Now convert and respond:
	var writeParams = new HashMap[String, Object];
	writeParams.put(PngConstants.PARAM_KEY_PNG_FORCE_TRUE_COLOR, Boolean.TRUE);
	
    val baos = new ByteArrayOutputStream();
    Imaging.writeImage(image, baos, ImageFormat.IMAGE_FORMAT_PNG, writeParams);
    baos.flush();
    var imageBytes = baos.toByteArray();
    println("imageBytes: " + imageBytes);
	httpclient.close();
	SimpleResult(
	    header = ResponseHeader(200, Map(CONTENT_TYPE -> "image/png")),
	    body = Enumerator(imageBytes)
	)
  }
  
  def imageIOConversion(url: String) = Action {
    Ok(views.html.jsspeccy("/action/passthrough/" + url));
  }
  
  // -- Javascript routing
  def javascriptRoutes = Action { implicit request =>
  	import routes.javascript._
  	Ok(
  	  Routes.javascriptRouter("jsRoutes")(
  	    routes.javascript.Application.rate,
  	    routes.javascript.Application.sendFeedback
  	  )
  	).as("text/javascript")
  }
  
  def rate(entry: String) = Action { implicit request =>
    writeToFile(entry);
	Ok("false")
  }

  def sendFeedback(entry: String) = Action { implicit request =>
    writeToFile(entry);
	Ok("false")
  }
  
  def writeToFile(entry: String) = {
    println(entry);
    val config = ConfigFactory.load()
    val path = config.getString("feedback.path");
    val file = new File(path);
    var fileContents = Files.readFile(file);
    fileContents += entry + "\n"; 
    Files.writeFile(file, fileContents);
  }
}