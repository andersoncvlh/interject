package controllers

import play.api._
import play.api.mvc._
import play.api.libs.ws.WS
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.iteratee._
import scala.concurrent._
import java.io.InputStream

object Application extends Controller {
  
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }
  
  def passthrough(url: String) = Action.async {
      WS.url(url).get().map { response =>
	      val asStream: InputStream = response.ahcResponse.getResponseBodyAsStream
	      Ok.chunked(Enumerator.fromStream(asStream)).as("application/octet-stream").withHeaders( CONTENT_TRANSFER_ENCODING -> "binary" )
    }
  }
  
  def jsspeccy(url: String) = Action {
    Ok(views.html.jsspeccy(url));
  }
}