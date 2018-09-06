INSERT INTO oskari_maplayer(type, url,
                    name, dataprovider_id,
                    locale,
                    attributes, internal)
VALUES(
    'statslayer', 'http://localhost:8080/geoserver/asdi/wms',
    'asdi:countries', (SELECT MAX(id) FROM oskari_dataprovider),
    '{ "en" : {
        "name":"Countries"
    }}',
    '{
        "statistics" : {
            "featuresUrl":"http://localhost:8080/geoserver/asdi/wfs",
            "regionIdTag":"ISO_A2",
            "nameIdTag":"NAME_EN"
        }
    }', true);


-- datasource for "omat indikaattorit"
INSERT INTO oskari_statistical_datasource (locale, config, plugin)
VALUES ('{"fi":{"name":"Omat indikaattorit"},"sv":{"name":"Dina indikatorer"},"en":{"name":"Your indicators"}}',
'{}', 'UserStats');


INSERT INTO 
    oskari_statistical_layer(datasource_id, layer_id, config)
VALUES(
    (SELECT id FROM oskari_statistical_datasource 
        WHERE locale like '%Your indicators%'),
    (SELECT id FROM oskari_maplayer WHERE type='statslayer' AND name = 'asdi:countries'),
    '{}');
