package com.donoroncall.server.rest.undertow.handlers.user

import com.donoroncall.server.rest.controllers.authentication.session.SessionHandler
import com.donoroncall.server.utils.STATUS_CODES
import com.google.inject.Inject
import io.undertow.server.{HttpServerExchange, HttpHandler}
import org.apache.commons.io.IOUtils
import spray.json._


/**
  * Created by vishnu on 30/1/16.
  */
class GetUserHandler @Inject()(sessionHandler: SessionHandler) extends HttpHandler {

  override def handleRequest(exchange: HttpServerExchange): Unit = {

    if (exchange.isInIoThread) {
      exchange.dispatch(this)
    } else {
      try {

        exchange.startBlocking()
        val requestJson = new String(IOUtils.toByteArray(exchange.getInputStream)).parseJson.asJsObject

        // Get the auth Token for Authenticating the User
        val authToken = requestJson.getFields("token").head.asInstanceOf[JsString].value

        // Get the authenticated User
        val user = sessionHandler.getUserForSession(authToken)

        if (user != null) {
          // If the user is logged in
          exchange.getResponseSender.send(JsObject(
            "status" -> JsString("ok"),
            "user" -> user.toJson
          ).prettyPrint)
        } else {
          // If the token is invalid prepare and response
          exchange.setStatusCode(STATUS_CODES.BAD_REQUEST)
          exchange.getResponseSender.send(JsObject(
            "status" -> JsString("failed"),
            "message" -> JsString("auth Failed")
          ).prettyPrint)

        }

      } catch {
        case e: Exception =>
          exchange.setStatusCode(STATUS_CODES.BAD_REQUEST)
          exchange.getResponseSender.send(JsObject(
            "status" -> JsString("failed"),
            "message" -> JsString("Server Exception")
          ).prettyPrint)
      }
    }

  }
}
