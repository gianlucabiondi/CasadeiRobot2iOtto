#!/bin/bash

./build.sh
stat=$?
if [ $stat -ne 0 ]; then
  exit $stat
fi

echo === Stop HubSimulator ===
~/scripts/HubSimulator-stop.sh

echo === Start HubSimulator ===
~/scripts/HubSimulator-start.sh

#JMX_ARGS="-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false"
#JMX_HOST="-Djava.rmi.server.hostname=ec2_appdev.turingsense.com"
#JMX_PORT="-Dcom.sun.management.jmxremote.port=1099 -Dcom.sun.management.jmxremote.rmi.port=1099"

##nohup java $JMX_ARGS $JMX_HOST $JMX_PORT -Xmx2000m -Xms2000m -jar ../start.jar STOP.PORT=8282 STOP.KEY=secret > jetty.log 2>&1 &
#nohup java -jar target/HubSimulator-0.0.1-jar-with-dependencies.jar HubSimulator.properties  > hubSim.log 2>&1 &



