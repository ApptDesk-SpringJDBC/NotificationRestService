#!/bin/bash
# usage check
usage(){
        if [ $args -lt 2  -o $args -gt 2 ]; then
        echo "Usage: Please pass following arguments - sh sendEmailNotifications.sh <CLIENT_CODE> <REST_SERVICE_URL>"
        echo
        exit 1
        fi
}


args=$#
usage
clientCode=$1 
restserviceURL=$2


# call lockDialer rest api - here not locking, it will check whether dialer is locked or not.
status=$(curl -X GET --silent "${restserviceURL}/getDialerLockStatus?clientCode=${clientCode}&device=email")
echo "getDialerLockStatus JSON Response:$status"
responseStatus=$(echo ${status}|jq .data.responseStatus)
responseMsg=$(echo $status|jq .data.responseMessage)
echo "ResponseStatus of lockDailer call: ${responseStatus}"
echo "ResponseMessage of lockDailer call: ${responseMsg}"

# email notifications send.
if [[ "$responseStatus" == "true" ]] 
then
 # send email notification. in this api.
 status=$(curl -X GET --silent "${restserviceURL}/sendEmailNotifications?clientCode=${clientCode}&device=email")
 echo 
 echo "sendEmail Notification JSON Response: $status"
 responseStatus=$(echo ${status}|jq .data.responseStatus)
 if [[ "$responseStatus" == "true" ]]
 then
  successCount=$(echo ${status}|jq .data.noOfNotificationsSentSuccess)
  failureCount=$(echo ${status}|jq .data.noOfNotificationsSentFailed) 
  echo "Total success email notifications: ${successCount}"
  echo "Total failure email notifications: ${failureCount}"
 else 
  echo
  echo "send email notifications failed."
 fi
else 
 echo "getDialerLockStatus failed, response message: ${responseMsg}"
fi

#sh sendEmailNotifications.sh DEMODOC2 http://localhost:8089/notificationRestService/notifications

