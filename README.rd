 1. ModuleName: NotificationRestService
 2. Schema update: NotificationRestService/database/create-table-template.sql
 3. Shell Scripts: NotificationRestService/scripts
 
Installation Steps:

1.copy velocity-*.jar to sharedLib.
2.copy notificationRestService.properties, notificationdb.properties from notificationRestService/config/ to /apps/properties
3. install jq in linux machine.  
   $ wget http://stedolan.github.io/jq/download/linux32/jq (32-bit system)
   $ wget http://stedolan.github.io/jq/download/linux64/jq (64-bit system)
   $ chmod +x ./jq
   $ sudo cp jq /usr/bin


Steps to run:
   1. downloadPhoneNotifications.sh
       sh downloadPhoneNotifications.sh DEMODOC2 ./download.csv http://localhost:8084/notificationRestService/notifications
   2. sendEmailNotifications.sh
       sh sendEmailNotifications.sh DEMODOC2 http://localhost:8084/notificationRestService/notifications
   3. sendSMSNotifications.sh
       sh sendSMSNotifications.sh DEMODOC2 http://localhost:8084/notificationRestService/notifications
   4. sh uploadPhoneLogFile.sh DEMODOC2 "C:/Users/<fileName>.zip" http://localhost:8089/notificationRestService/notifications

  CronJob:
  NotificationRestService/scripts/notification-crontab.txt


Shell script to upload the phone log.
$ sh uploadPhoneLogFile.sh DEMODOC2 "D:/phoneuploadlog.csv" http://localhost:8089/notificationRestService/notifications
UploadLogResponse: {"data":{"responseStatus":true,"responseMessage":null,"errorStatus":false,"errorCode":null,"internalErrorMessage":null,"userErrorMessage":null,"totalRecords":"1","successRecords":"1","failureRecords":"0","invalidRecords":"","fileUploadResponse":null,"invalidRecordsList":[],"errorResponse":null}}

Success response has been received from backend.
TotalRecords: 1
SuccessRecords: 1
FailureRecords: 0
InvalidRecords:
invalidRecordsList: []