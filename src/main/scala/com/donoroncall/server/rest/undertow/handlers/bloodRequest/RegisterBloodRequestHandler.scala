package com.donoroncall.server.rest.undertow.handlers.bloodRequest

import com.donoroncall.server.models.{User, BloodRequest}
import com.donoroncall.server.rest.controllers.authentication.session.SessionHandler
import com.donoroncall.server.utils.STATUS_CODES
import com.google.inject.Inject
import io.undertow.server.{HttpServerExchange, HttpHandler}
import org.apache.commons.io.IOUtils
import spray.json.{JsObject, JsString}
import spray.json._

/**
  * Created by vishnu on 16/4/16.
  */
class RegisterBloodRequestHandler @Inject()(sessionHandler: SessionHandler) extends HttpHandler {

  /**
    *
    * @param exchange
    * request: body
                      {
                    "token": "8acbbd80e7ce457e8cd5b816fa01302b0fe60c4f276acf349aa39021bf4e98a2",
                    "data": {
                      "contactNumber": "13245645",
                      "hospitalName": "hospital name",
                      "hospitalAddress": " hospital address1, Hospital address 2",
                      "patientName": "Patient Name",
                      "bloodGroup": "0+ve",
                      "purpose": "reason for blood Request will be displayed to the donors",
                      "comment": "",
                      "requiredUnits": 3,
                      "lat": 0.0,
                      "lon": 0.0,
                      "requiredWithin": 1462022163000
                    }
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
  },
  "messages": ["Blood Request Created Successfully with Id 3"]
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

          val data = requestJson.getFields("data").head.asJsObject

          val (bloodRequest, messages) = BloodRequest.registerRequest(data, user.userId)

          if (bloodRequest != null) {
            exchange.getResponseSender.send(JsObject(
              "status" -> JsString("ok"),
              "bloodRequest" -> bloodRequest.toJson,
              "messages" -> JsArray(messages.map(JsString(_)).toVector)
            ).prettyPrint)
          } else {
            exchange.setStatusCode(STATUS_CODES.BAD_REQUEST)
            exchange.getResponseSender.send(JsObject(
              "status" -> JsString("failed"),
              "messages" -> JsArray(messages.map(JsString(_)).toVector)
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
