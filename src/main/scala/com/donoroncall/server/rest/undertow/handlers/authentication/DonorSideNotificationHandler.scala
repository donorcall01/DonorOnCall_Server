package com.donoroncall.server.rest.undertow.handlers.authentication

/**
 * Created by anmol on 23/3/16.
 */

import com.donoroncall.server.rest.controllers.authentication.NotificationController
import com.donoroncall.server.utils.STATUS_CODES
import com.google.inject.Inject
import io.undertow.server.{HttpHandler, HttpServerExchange}
import org.apache.commons.io.IOUtils
import spray.json._


class DonorSideNotificationHandler @Inject()(notificationController:NotificationController ) extends HttpHandler {
  override def handleRequest(exchange: HttpServerExchange): Unit = {
    if (exchange.isInIoThread) {
      exchange.dispatch(this)
    } else {
      try {
        exchange.startBlocking()
        val request = new String(IOUtils.toByteArray(exchange.getInputStream))


        val requestJson = request.parseJson.asJsObject

        val userName = requestJson.getFields("username").head.asInstanceOf[JsString].value
        // Recipient username


        val bloodGroup = notificationController.getBloodGroup(userName)
        val patientName = notificationController.getName(userName)
        val phoneNo = notificationController.getPhoneNo(userName)
        val hospitalName = notificationController.getHospitalName(userName)
        val latitude = notificationController.getLatitude(userName)
        val longitude = notificationController.getLongitude(userName)




        if (userName != null) {
          // to verify if this is the correct way
          exchange.getResponseSender.send(JsObject(
            "status" -> JsString("ok"),
            "patientName" -> JsString(patientName),
            "bloodGroup" -> JsString(bloodGroup),
            "phoneNo" -> JsString(phoneNo),
            "hospitalName" -> JsString(hospitalName),
            "latitude" -> JsString(latitude),
            "langitude" -> JsString(longitude),


            "message" -> JsString("Blood Required for this patient")
          ).prettyPrint)

        } else {
          //TODO add logic for Failed Registration
          exchange.setStatusCode(STATUS_CODES.BAD_REQUEST)
          exchange.getResponseSender.send(JsObject(
            "status" -> JsString("failed"),
            "message" -> JsString("Could not load recipient details")
          ).prettyPrint)
        }


      } catch {
        case e: Exception => {
          exchange.setStatusCode(STATUS_CODES.BAD_REQUEST)
          exchange.getResponseSender.send(JsObject(
            "status" -> JsString("failed"),
            "message" -> JsString("Could not load recipient details")
          ).prettyPrint)
        }
      }
    }

  }
}

