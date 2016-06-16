package com.donoroncall.server.rest.undertow.handlers.donationRecord

import com.donoroncall.server.models.{DonationRecord, BloodRequest, User}
import com.donoroncall.server.rest.controllers.authentication.session.SessionHandler
import com.donoroncall.server.utils.STATUS_CODES
import com.google.inject.Inject
import io.undertow.server.{HttpHandler, HttpServerExchange}
import org.apache.commons.io.IOUtils
import spray.json.{JsObject, JsString, _}

/**
  * Created by vishnu on 16/4/16.
  */
class GetDonationRecordHandler @Inject()(sessionHandler: SessionHandler) extends HttpHandler {

  /**
    *
    * @param exchange
    * request: body
                      {
                    "token": "8acbbd80e7ce457e8cd5b816fa01302b0fe60c4f276acf349aa39021bf4e98a2",
                    "donationId":1
                  }

    * @return
  {
  "status": "ok",
  "donationRecord": {
    "donationId": 1,
    "userId": 1,
    "requestId": 120,
    "status": "donor accepted"
  }
}
    */
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
        val user: User = sessionHandler.getUserForSession(authToken)

        if (user != null) {
          // If the user is logged in

          val donationId = requestJson.getFields("donationId").head.asInstanceOf[JsNumber].value.toLong

          val donationRecord = DonationRecord.getDonationRecord(donationId)

          if (donationRecord != null) {
            exchange.getResponseSender.send(JsObject(
              "status" -> JsString("ok"),
              "donationRecord" -> donationRecord.toJson
            ).prettyPrint)
          } else {
            exchange.setStatusCode(STATUS_CODES.BAD_REQUEST)
            exchange.getResponseSender.send(JsObject(
              "status" -> JsString("failed"),
              "messages" -> JsArray(Vector(JsString("Invalid RequestId")))
            ).prettyPrint)
          }

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
