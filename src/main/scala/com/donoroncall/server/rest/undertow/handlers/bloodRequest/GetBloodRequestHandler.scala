package com.donoroncall.server.rest.undertow.handlers.bloodRequest

import com.donoroncall.server.rest.controllers.authentication.session.SessionHandler
import com.donoroncall.server.models.{DonationRecord, BloodRequest, User}
import com.donoroncall.server.utils.STATUS_CODES
import com.google.inject.Inject
import io.undertow.server.{HttpHandler, HttpServerExchange}
import org.apache.commons.io.IOUtils
import spray.json.{JsObject, JsString, _}

/**
  * Created by vishnu on 16/4/16.
  */
class GetBloodRequestHandler @Inject()(sessionHandler: SessionHandler) extends HttpHandler {

  /**
    *
    * @param exchange
    * request: body
                      {
                    "token": "8acbbd80e7ce457e8cd5b816fa01302b0fe60c4f276acf349aa39021bf4e98a2",
                    "requestId":1
                  }

    * @return
  {
  "status": "ok",
  "bloodRequest": {
    "bloodGroup": "0+ve",
    "patientName": "Patient Name",
    "requiredWithin": 1462022163000,
    "purpose": "reason for blood Request will be displayed to the donors",
    "status": 0,
    "comment": "",
    "createdUserId": 1,
    "contactNumber": "13245645",
    "lon": 0.0,
    "requestId": 3,
    "hospitalAddress": " hospital address1, Hospital address 2",
    "requiredUnits": 3,
    "hospitalName": "hospital name",
    "fulfilledUnits": 0,
    "lat": 0.0,
    "promisedUnits": 0
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

          val requestId = requestJson.getFields("requestId").head.asInstanceOf[JsNumber].value.toLong

          val bloodRequest = BloodRequest.getBloodRequest(requestId)

          if (bloodRequest != null) {
            if (bloodRequest.createdUserId == user.userId) {

              exchange.getResponseSender.send(JsObject(
                "status" -> JsString("ok"),
                "bloodRequest" -> bloodRequest.toJson,
                //TODO give more information about the user
                "donorDetails" -> JsArray(DonationRecord.getDonationRecordsFor(bloodRequest, 2).map(i => {
                  JsObject(
                    "status" -> JsString(i._1.getStatusString),
                    "name" -> JsString(i._2.name),
                    "phone" -> JsString(i._2.phone),
                    "age" -> JsNumber(i._2.age),
                    "zipCode" -> JsString(i._2.zipCode.toString)
                  )
                }).toVector)
              ).prettyPrint)
            } else {
              exchange.getResponseSender.send(JsObject(
                "status" -> JsString("ok"),
                "bloodRequest" -> bloodRequest.toJson
              ).prettyPrint)
            }
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
