package controllers

import play.api.data.Form
import play.api.mvc.Result
import play.api.mvc.Results._

object Responses {
  def success = Ok("Success")
  def notFound(content: String) = NotFound(s"Not found ${content}")
  def badRequest[A](form: Form[_]): Result = BadRequest(form.errors.mkString("\n"))
}
