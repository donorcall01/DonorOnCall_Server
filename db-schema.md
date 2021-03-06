
# MYSQL DATABASE SCHEMA 

Update your mysql database to Run the Application

```
CREATE DATABASE donor_on_call;

USE donor_on_call;

CREATE TABLE users
  (
     userId             BIGINT NOT NULL auto_increment,
     username           VARCHAR(255) NOT NULL UNIQUE,
     password_hash      VARCHAR(128),
     name               VARCHAR(255),
     email              VARCHAR(255) UNIQUE,
     phoneno            VARCHAR(20),
     dob                VARCHAR(20),
     address_1          VARCHAR(255),
     address_2          VARCHAR(255),
     locality           VARCHAR(255),
     city               VARCHAR(255),
     blood_group        VARCHAR(20),
     account_status     VARCHAR(20),
     latitude           DOUBLE,
     longitude          DOUBLE,
     health_information VARCHAR(1024),
     is_donor           BOOLEAN NOT NULL DEFAULT 0,
     is_recipient       BOOLEAN NOT NULL DEFAULT 0,
     is_admin_approved  BOOLEAN NOT NULL DEFAULT 0,
     join_date          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
     zipcode            INT DEFAULT 0,
     request_count      INT DEFAULT 0,
     fulfilled_count    INT DEFAULT 0,
     donation_count     INT DEFAULT 0,
     PRIMARY KEY (userId)
  );

CREATE TABLE blood_request
  (
     requestId        BIGINT NOT NULL auto_increment,
     userId    BIGINT NOT NULL,
     contact_number   VARCHAR(20),
     hospital_name    VARCHAR(255),
     hospital_address VARCHAR(255),
     patient_name     VARCHAR(255),
     required_units   INT,
     promised_units   INT,
     fulfilled_units  INT,
     required_within  BIGINT,
     blood_group      VARCHAR(20),
     purpose          VARCHAR(255),
     status           INT,
     comment          VARCHAR(255),
     lat              DOUBLE,
     lon              DOUBLE,
     PRIMARY KEY (requestId)
  );

CREATE TABLE donation_record
  (
     donationId BIGINT NOT NULL auto_increment,
     user_id    BIGINT NOT NULL,
     request_id BIGINT NOT NULL,
     status     INT,
     PRIMARY KEY (donationId)
  );

CREATE TABLE registered_devices
  (
     deviceId VARCHAR(255),
     userId   BIGINT,
     PRIMARY KEY (deviceId),
     FOREIGN KEY (userId) REFERENCES users(userId)
  );


```
