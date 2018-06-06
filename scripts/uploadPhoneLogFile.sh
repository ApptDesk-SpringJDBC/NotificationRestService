#!/bin/bash
# usage check
usage(){
        if [ $args -lt 3  -o $args -gt 3 ]; then
        echo "Usage: Please pass following arguments - sh uploadPhoneLogFile.sh <CLIENT_CODE> <FILE_PATH> <REST_SERVICE_URL>"
        echo
        exit 1
        fi
}


args=$#
usage
clientCode=$1 
filePath=$2
restserviceURL=$3

# uploadPhoneLogFile to restservice
 status=$(curl -H 'Content-type: multipart/form-data' -H 'Transfer-Encoding: chunked' -F 'file=@"'"${filePath}"'"' -F clientCode=${clientCode} -X POST --silent "${restserviceURL}/uploadPhoneLogFile")
 echo "UploadLogResponse: $status"
 if [[ $status != *'responseStatus":true'* ]]
 then
  echo 
  echo "Failed to uploadPhoneLogFile to backend service url: ${restserviceURL}";
  exit 1
 else 
  echo
  echo "Success response has been received from backend."
  totalRecords=$(echo ${status}|jq -r '.data.totalRecords')
  successRecords=$(echo ${status}|jq -r '.data.successRecords')
  failureRecords=$(echo ${status}|jq -r '.data.failureRecords')
  invalidRecords=$(echo ${status}|jq -r '.data.invalidRecords')
  invalidRecordsList=$(echo ${status}|jq -r '.data.invalidRecordsList')
  echo "TotalRecords: ${totalRecords}"
  echo "SuccessRecords: ${successRecords}"
  echo "FailureRecords: ${failureRecords}"
  echo "InvalidRecords: ${invalidRecords}"
  echo "invalidRecordsList: ${invalidRecordsList}" 
 fi
 
# sh uploadPhoneLogFile.sh DEMODOC2 "C:/Users/nanba03/Downloads/nbiservice-acspage-31082016-225305.zip" http://localhost:8089/notificationRestService/notifications
