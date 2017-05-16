package controllers

import play.api.mvc.Results._

object Responses {
  def success = Ok("Success")
  def notFound(content: String) = NotFound(s"Not found ${content}")
}
