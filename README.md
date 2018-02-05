# oskari-server-extensions

The geoportal for http://arctic-sdi.org/

Builds the on top of the oskariorg/oskari-server project:
- http://oskari.org/
- https://github.com/oskariorg/oskari-server

## To setup Jetty 8

Download the Jetty-Oskari bundle from http://oskari.org/download.
You can delete everything under {JETTY_HOME}/webapps and replace the oskari-map.war with the version build from this repository.

Add/modify  {JETTY_HOME}/resources/oskari-ext.properties with:

    ##################################
    # Flyway config
    ##################################
    
    # initialized the layer srs (also updated by setup.war if used to generate GeoServer config)
    oskari.native.srs=EPSG:3575
    
    # skip geoserver setting as its by default on the same server -> geoserver is not running when migrations are run
    flyway.1_45_0.skip=true


## To build the webapp for map

Run 'mvn clean install' in this directory. 
A deployable WAR-file will be compiled to ./webapp-map/target/oskari-map.war
