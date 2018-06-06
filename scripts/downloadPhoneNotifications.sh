#!/bin/bash
# usage check
usage(){
        if [ $args -lt 3  -o $args -gt 3 ]; then
        echo "Usage: Please pass following arguments - sh downloadPhoneNotifications.sh <CLIENT_CODE> <CSV_DEST_FULL_PATH> <REST_SERVICE_URL>"
        echo
        exit 1
        fi
}


args=$#
usage
clientCode=$1 
csvdestPath=$2
restserviceURL=$3


# call lockDialer rest api
status=$(curl -X GET --silent "${restserviceURL}/getDialerLockStatus?clientCode=${clientCode}&device=phone")
echo "getDialerLockStatus JSON Response:$status"
responseStatus=$(echo ${status}|jq .data.responseStatus)
responseMsg=$(echo $status|jq .data.responseMessage)
echo "ResponseStatus of lockDailer call: ${responseStatus}"
echo "ResponseMessage of lockDailer call: ${responseMsg}"

# downloading Notifications.
if [[ "$responseStatus" == "true" ]] 
then
 # fetch phone notification list from restservice.
 status=$(curl -H "Accept:application/octet-stream" -X GET --write-out %{http_code} --silent --output ${csvdestPath} "${restserviceURL}/getNotifyPhoneList?clientCode=${clientCode}&device=phone")
 if [[ $status != *'200'* ]]
 then
  echo 
  echo "Failed to download phone notifications from backend service url: ${restserviceURL}";
  exit 1
 else 
  echo
  echo "Phone notifications downloaded successfully from backend service."
 fi
else 
 echo "getDialerLockStatus failed, response message: ${responseMsg}"
fi

#sh downloadPhoneNotifications.sh DEMODOC2 ./download.csv http://localhost:8084/notificationRestService/notifications

