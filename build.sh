#!/bin/bash

mvn clean compile assembly:single 
stat=$?
if [ $stat -ne 0 ]; then
  exit $stat
fi
