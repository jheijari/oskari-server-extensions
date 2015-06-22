# oskari-server-extensions

http://locationframework.eu/

Builds the on top of the nls-oskari/oskari-server project:
- http://oskari.org/
- https://github.com/nls-oskari/oskari-server

## To setup Jetty 8

See README.md under jetty-home-instructions

## To build the webapp for map

Run 'mvn clean install' in this directory. 
A deployable WAR-file will be compiled to ./webapp-map/target/oskari-map.war

## To build the WFS-support webapp for map

Run 'mvn clean install' in this directory. 
A deployable WAR-file will be compiled to ./webapp-transport/target/transport.war

##### Note! The runnable JAR packaging is under development and shouldn't be used for now