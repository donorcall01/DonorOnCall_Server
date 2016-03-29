package com.donoroncall.server.rest.undertow.handlers.admin

import com.donoroncall.server.rest.controllers.authentication.AuthenticationController
import com.google.inject.Inject
import io.undertow.server.{HttpHandler, HttpServerExchange}
import org.apache.commons.io.IOUtils
import spray.json._

/**
 * Created by Anmol on 9/3/16.
 */
class AdminsApprovalReqAPI @Inject()(authenticationController: AuthenticationController) extends HttpHandler {
  override def handleRequest(exchange: HttpServerExchange): Unit = {
    if (exchange.isInIoThread) {
      exchange.dispatch(this)
    } else {
      try {
        exchange.startBlocking()
        val request = new String(IOUtils.toByteArray(exchange.getInputStream))

        val requestJson = request.parseJson.asJsObject

        val blood_group = requestJson.getFields("blood_group").head.asInstanceOf[JsString].value
        val admin_response = requestJson.getFields("admin_response").head.asInstanceOf[JsString].value
        val latitude = requestJson.getFields("latitude").head.asInstanceOf[JsString].value
        val longitude = requestJson.getFields("longitude").head.asInstanceOf[JsString].value
        val username = requestJson.getFields("username").head.asInstanceOf[JsString].value

        val userId = authenticationController.addNewRecipientTable(blood_group, latitude, longitude, username)
        if (userId) {

          if(admin_response==true){
//TO
            exchange.getResponseSender.send(JsObject(
              "status" -> JsString("ok"),
              "message" -> JsString(" List of donors according to distance is ready ")
              // list is ready in the table userName_recipient
              // to do delete that table is created but not required anymore
            ).prettyPrint)

          } else {
            exchange.getResponseSender.send(JsObject(
              "status" -> JsString("failed"),
              "message" -> JsString("Admin did not approve the request")
            ).prettyPrint)
          }}else {
          //TODO add logic for Failed Registration
          exchange.getResponseSender.send(JsObject(
            "status" -> JsString("failed"),
            "message" -> JsString("Request for blood Failed")
          ).prettyPrint)
        }


      } catch {
        case e: Exception => {
          exchange.getResponseSender.send(JsObject(
            "status" -> JsString("failed"),
            "message" -> JsString("Request for blood Failed")
          ).prettyPrint)
        }
      }
    }

  }
}
