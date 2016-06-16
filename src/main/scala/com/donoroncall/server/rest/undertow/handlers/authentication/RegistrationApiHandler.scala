package com.donoroncall.server.rest.undertow.handlers.authentication

import com.donoroncall.server.models.User
import com.donoroncall.server.rest.controllers.authentication.AuthenticationController
import com.donoroncall.server.utils.STATUS_CODES
import com.google.inject.Inject
import io.undertow.server.{HttpHandler, HttpServerExchange}
import org.apache.commons.io.IOUtils
import spray.json._

/**
  * Created by vishnu on 20/1/16.
  */
class RegistrationApiHandler @Inject()(authenticationController: AuthenticationController) extends HttpHandler {
  override def handleRequest(exchange: HttpServerExchange): Unit = {
    if (exchange.isInIoThread) {
      exchange.dispatch(this)
    } else {
      try {
        exchange.startBlocking()
        val request = new String(IOUtils.toByteArray(exchange.getInputStream))

        val requestJson = request.parseJson.asJsObject

        val registrationTuple = User.registerUser(requestJson)

        if (registrationTuple._1 != null) {
          //TODO add logic for Successful Registration
          exchange.getResponseSender.send(JsObject(
            "status" -> JsString("ok"),
            "message" -> JsString("Registration successful")
          ).prettyPrint)
        } else {
          //TODO add logic for Failed Registration
          exchange.setStatusCode(STATUS_CODES.BAD_REQUEST)
          exchange.getResponseSender.send(JsObject(
            "status" -> JsString("failed"),
            "message" -> JsString("Registration Failed"),
            "comments" -> JsArray(registrationTuple._2.map(JsString(_)).toVector)
          ).prettyPrint)
        }


      } catch {
        case e: Exception => {
          exchange.setStatusCode(STATUS_CODES.BAD_REQUEST)
          exchange.getResponseSender.send(JsObject(
            "status" -> JsString("failed"),
            "message" -> JsString("Registration Failed")
          ).prettyPrint)
        }
      }
    }

  }
}
