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
    Ok(views.html.jsspeccy("/action/passthrough/" + url));
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