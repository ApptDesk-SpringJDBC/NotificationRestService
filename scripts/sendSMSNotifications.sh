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


# call lockDialer rest api
status=$(curl -X GET --silent "${restserviceURL}/getDialerLockStatus?clientCode=${clientCode}&device=sms")
echo "getDialerLockStatus JSON Response:$status"
responseStatus=$(echo ${status}|jq .data.responseStatus)
responseMsg=$(echo $status|jq .data.responseMessage)
echo "ResponseStatus of lockDailer call: ${responseStatus}"
echo "ResponseMessage of lockDailer call: ${responseMsg}"

# sms notifications send.
if [[ "$responseStatus" == "true" ]] 
then
 # send sms notifications.
 status=$(curl -X GET --silent "${restserviceURL}/sendSMSNotifications?clientCode=${clientCode}&device=sms")
 echo
 echo "sendSMSNotification JSON Response: $status"
 responseStatus=$(echo ${status}|jq .data.responseStatus)
 if [[ "$responseStatus" == "true" ]]
 then
  successCount=$(echo ${status}|jq .data.noOfNotificationsSentSuccess)
  failureCount=$(echo ${status}|jq .data.noOfNotificationsSentFailed)
 
  echo "Total success sms notifications: ${successCount}"
  echo "Total failure sms notifications: ${failureCount}"
 else 
  echo
  echo "send sms notifications failed."
 fi
else 
 echo "getDialerLockStatus failed, response message: ${responseMsg}"
fi

#sh sendSMSNotifications.sh DEMODOC2 http://localhost:8089/notificationRestService/notifications

