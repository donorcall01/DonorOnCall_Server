package com.donoroncall.server.rest.undertow.handlers.authentication

import com.donoroncall.server.rest.controllers.authentication.AuthenticationController
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
class LoginApiHandler @Inject()(authenticationController: AuthenticationController) extends HttpHandler {
  val LOG: Logger = LoggerFactory.getLogger(this.getClass)

  override def handleRequest(exchange: HttpServerExchange): Unit = {
    if (exchange.isInIoThread) {
      exchange.dispatch(this)
    } else {
      try {

        exchange.startBlocking()
        val request = new String(IOUtils.toByteArray(exchange.getInputStream))

        val requestJson = request.parseJson.asJsObject
        val userName = requestJson.getFields("userName").head.asInstanceOf[JsString].value
        val password = requestJson.getFields("password").head.asInstanceOf[JsString].value

        val authToken = authenticationController.login(userName, password)

        if (authToken != "") {
          exchange.getResponseHeaders.add(new HttpString("auth-token"), authToken)
          exchange.getResponseSender.send(JsObject(
            "token" -> JsString(authToken),
            "userName"->JsString(userName)
          ).prettyPrint)

        } else {
          //TODO add logic for Login failure
          exchange.setStatusCode(STATUS_CODES.UNAUTHORIZED)
          exchange.getResponseSender.send(JsObject(
            "error" -> JsString("Wrong username and password combination")
          ).prettyPrint)
        }
      } catch {
        case u: UnsupportedOperationException => {
          u.printStackTrace()
          exchange.setStatusCode(STATUS_CODES.BAD_REQUEST)
          exchange.getResponseSender.send(JsObject(
            "status" -> JsString("failed"),
            "message" -> JsString("Invalid Request Format")
          ).prettyPrint)
        }
        case e: Exception => {
          e.printStackTrace()
          exchange.setStatusCode(STATUS_CODES.BAD_REQUEST)
          exchange.getResponseSender.send(JsObject(
            "status" -> JsString("failed"),
            "message" -> JsString("Server Exception")
          ).prettyPrint)
        }
      }
    }

  }
}
