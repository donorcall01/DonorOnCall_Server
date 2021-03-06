package com.donoroncall.server.models

import java.sql.ResultSet

import com.donoroncall.server.BootStrapServer._
import com.donoroncall.server.utils.SqlUtils
import org.slf4j.{LoggerFactory, Logger}
import spray.json.{JsString, JsNumber, JsObject}

import scala.collection.mutable.ArrayBuffer

/**
  * Created by vishnu on 14/4/16.
  */

/**
  *
  * @param donationId The current Records Id 0 for insert
  * @param userId     The Donors UserId
  * @param requestId  The BloodRequest record id
  * @param status
  *                   -2 => Deleted Record
  *                   -1 => Request Completed
  *                   0 => Recipient Canceled
  *                   1 => Pending
  *                   2 => Donor Accepted
  *                   3 => Donor Canceled
  *                   4 => Successfully Completed
  */
class DonationRecord(
                      val donationId: Long = 0,
                      val userId: Long,
                      val requestId: Long,
                      val status: Int = 0
                    ) {

  def getStatusString: String = status match {
    case -2 => "Deleted Record"
    case -1 => "Request Completed"
    case 0 => "Recipient Canceled"
    case 1 => "Pending"
    case 2 => "Donor Accepted"
    case 3 => "Donor Canceled"
    case 4 => "Successfully Completed"
  }

  def toJson: JsObject = JsObject(
    "donationId" -> JsNumber(donationId),
    "userId" -> JsNumber(userId),
    "requestId" -> JsNumber(requestId),
    "status" -> JsString(DonationRecord.statusToString(status))
  )
}

object DonationRecord {
  private val LOG: Logger = LoggerFactory.getLogger(this.getClass)

  def statusToString(i: Int): String = i match {
    case -2 => "deleted record"
    case -1 => "request completed"
    case 0 => "recipient canceled"
    case 1 => "pending"
    case 2 => "donor accepted"
    case 3 => "donor canceled"
    case 4 => "successfully completed"
    case _ => "invalid status"
  }

  def statusToInt(s: String): Int = s.toLowerCase match {
    case "deleted record" => -2
    case "request completed" => -1
    case "recipient canceled" => 0
    case "pending" => 1
    case "donor accepted" => 2
    case "donor canceled" => 3
    case "successfully completed" => 4
    case _ => -99
  }

  def getDonationRecord(donationId: Long) = try {
    val resultSet = mysqlClient.getResultSet("SELECT * from donation_record where donationId=" + donationId)

    if (resultSet.next()) {
      getDonationRecordFromResultSet(resultSet)
    } else null
  } catch {
    case e: Exception => LOG.debug("exception in Getting Donation Record " + donationId, e)
      null
  }

  /**
    *
    * @param requestJson
    * {
                          "requestId":120,
                          "status":"donor accepted",
                         }
    * @return
    */
  def registerDonationRecord(requestJson: JsObject, userId: Long): (DonationRecord, Array[String]) = {
    var donationRecord: DonationRecord = null
    val messages: scala.collection.mutable.ArrayBuffer[String] = ArrayBuffer.empty[String]
    try {
      val requestId = requestJson.getFields("requestId").head.asInstanceOf[JsNumber].value.toLong
      val status = requestJson.getFields("status").head.asInstanceOf[JsString].value

      if (-99 != statusToInt(status)) {
        val donationId = SqlUtils.insert("donation_record", Map(
          "user_id" -> userId,
          "request_id" -> requestId,
          "status" -> statusToInt(status)
        ))
        donationRecord = getDonationRecord(donationId)
        messages += "Donation Record Created Successfully with Id " + donationId
      } else {
        messages += "Invalid Status Variable"
      }


    } catch {
      case u: UnsupportedOperationException => {
        messages += "Invalid Request Json"
        LOG.debug("Error While Creating Donation Record", u)
      }
      case e: Exception => {
        messages += e.getLocalizedMessage
        LOG.debug("Error While Creating Donation Record", e)
      }
    }
    (donationRecord, messages.toArray)
  }

  def getDonationRecordFromResultSet(resultSet: ResultSet): DonationRecord = {
    val id = resultSet.getLong("donationId")
    val userId = resultSet.getLong("user_id")
    val requestId = resultSet.getLong("request_id")
    val status = resultSet.getInt("status")

    new DonationRecord(
      donationId = id,
      userId = userId,
      requestId = requestId,
      status = status
    )
  }

  def getDonationRecordsFor(bloodRequest: BloodRequest, statusAbove: Int = 2): Array[(DonationRecord, User)] = {

    val donationRecords: scala.collection.mutable.ArrayBuffer[(DonationRecord, User)] = ArrayBuffer.empty[(DonationRecord, User)]

    val resultSet = mysqlClient.getResultSet("SELECT * from donation_record where request_id=" + bloodRequest.requestId + " and status>=" + statusAbove)

    while (resultSet.next()) {

      val donationRecord = getDonationRecordFromResultSet(resultSet)
      donationRecords += donationRecord -> User.getUser(donationRecord.userId)
    }
    donationRecords.toArray
  }

}