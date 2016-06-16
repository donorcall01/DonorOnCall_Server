package com.donoroncall.server.rest.undertow.handlers.authentication

import com.donoroncall.server.rest.controllers.authentication.AuthenticationController
import com.donoroncall.server.rest.controllers.authentication.session.SessionHandler
import com.donoroncall.server.utils.STATUS_CODES
import com.google.inject.Inject
import io.undertow.server.{HttpHandler, HttpServerExchange}
import io.undertow.util.HttpString
import org.apache.commons.io.IOUtils
import org.slf4j.{Logger, LoggerFactory}
import spray.json._

/**
  * Created by vishnu on 20/1/16.
  */
class LogoutApiHandler @Inject()(authenticationController: AuthenticationController, sessionHandler: SessionHandler) extends HttpHandler {
  val LOG: Logger = LoggerFactory.getLogger(this.getClass)

  override def handleRequest(exchange: HttpServerExchange): Unit = {

    if (exchange.isInIoThread) {
      exchange.dispatch(this)
    } else {
      try {

        exchange.startBlocking()
        val requestJson = new String(IOUtils.toByteArray(exchange.getInputStream)).parseJson.asJsObject

        // Get the auth Token for Authenticating the User
        val authToken = requestJson.getFields("token").head.asInstanceOf[JsString].value

        // Removes SessionToken
        sessionHandler.clearSessionToken(authToken)

        exchange.getResponseSender.send(JsObject(
          "status" -> JsString("ok"),
          "message" -> JsString("Successfully Logged Out")
        ).prettyPrint)

      } catch {
        case e: Exception =>
          exchange.setStatusCode(STATUS_CODES.SERVER_ERROR)
          exchange.getResponseSender.send(JsObject(
            "status" -> JsString("failed"),
            "message" -> JsString("Server Exception")
          ).prettyPrint)
      }
    }

  }
}
