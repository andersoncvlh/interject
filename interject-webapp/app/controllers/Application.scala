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

import org.apache.commons.imaging.ImagingConstants
import org.apache.commons.imaging.Imaging
import org.apache.commons.imaging.ImageFormat
import org.apache.commons.imaging.formats.png.PngConstants
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.tika.Tika
import uk.bl.wa.interject.converter.CommonsImageStrategy
import uk.bl.wa.interject.converter.ImageIOStrategy
import uk.bl.wa.interject.converter.ImageConverter
import org.apache.commons.io.FilenameUtils


object Application extends Controller {

  def index = Action {
    Ok(views.html.index())
  }

  def passthrough(url: String) = Action.async {
    WS.url(url).get().map { response =>
      val asStream: InputStream = response.ahcResponse.getResponseBodyAsStream
      Ok.chunked(Enumerator.fromStream(asStream)).as("application/octet-stream").withHeaders(CONTENT_TRANSFER_ENCODING -> "binary")
    }
  }

  def jsspeccy(url: String) = Action {
    // call passthrough
	val filename = FilenameUtils.getName(url);
    Ok(views.html.jsspeccy("/action/passthrough/" + url, filename));
  }
  
  def commonsImagingConversion(url: String, sourceContentType: String) = Action {
    println("Attempting to convert: "+url+ " from: " + sourceContentType);
    
	val imageConverter = new ImageConverter(CommonsImageStrategy.INSTANCE);
	val imageBytes = imageConverter.convertFromUrlToPng(url, sourceContentType);
	
	SimpleResult(
	    header = ResponseHeader(200, Map(CONTENT_TYPE -> "image/png")),
	    body = Enumerator(imageBytes)
	)
  }
  
  def imageIOConversion(url: String, sourceContentType: String) = Action {
    println("Attempting to convert: "+url+" from "+sourceContentType);
    
	val imageConverter = new ImageConverter(ImageIOStrategy.INSTANCE);
	val imageBytes = imageConverter.convertFromUrlToPng(url, sourceContentType);
	
	SimpleResult(
	    header = ResponseHeader(200, Map(CONTENT_TYPE -> "image/png")),
	    body = Enumerator(imageBytes)
	)
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
    val path = System.getProperty("user.dir") + "/tmp/feedback.txt";
    val file = new File(path);
    var fileContents = Files.readFile(file);
    fileContents += entry + "\n"; 
    Files.writeFile(file, fileContents);
  }  
}