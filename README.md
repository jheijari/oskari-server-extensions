# oskari-server-extensions

The geoportal for http://arctic-sdi.org/

Builds the on top of the nls-oskari/oskari-server project:
- http://oskari.org/
- https://github.com/nls-oskari/oskari-server

## To setup Jetty 8

Download the Jetty-Oskari bundle from http://oskari.org/download.
You can delete everything under {JETTY_HOME}/webapps and replace the oskari-map.war with the version build from this repository.

## To build the webapp for map

Run 'mvn clean install' in this directory. 
A deployable WAR-file will be compiled to ./webapp-map/target/oskari-map.war
