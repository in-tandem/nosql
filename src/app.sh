#!/bin/sh
echo 'somak'
printenv
echo ${wiremock_profile}
java -jar /app/application.jar
#echo ${wiremock_profile}
