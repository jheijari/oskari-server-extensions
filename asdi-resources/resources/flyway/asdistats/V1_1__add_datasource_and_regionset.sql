INSERT INTO oskari_maplayer(type, url,
                    name, dataprovider_id,
                    locale,
                    attributes, internal, srs_name)
VALUES(
    'statslayer', 'resources://regionsets/countries.json',
    'countries', (SELECT MAX(id) FROM oskari_dataprovider),
    '{ "en" : {
        "name":"Countries"
    }}',
    '{
        "statistics" : {
            "featuresUrl":"resources://regionsets/countries.json",
            "regionIdTag":"ISO2",
            "nameIdTag":"NAME"
        }
    }', true, 'EPSG:3575');


-- datasource for "omat indikaattorit"
INSERT INTO oskari_statistical_datasource (locale, config, plugin)
VALUES ('{"fi":{"name":"Omat indikaattorit"},"sv":{"name":"Dina indikatorer"},"en":{"name":"Your indicators"}}',
'{}', 'UserStats');


INSERT INTO 
    oskari_statistical_layer(datasource_id, layer_id, config)
VALUES(
    (SELECT id FROM oskari_statistical_datasource 
        WHERE locale like '%Your indicators%'),
    (SELECT id FROM oskari_maplayer WHERE type='statslayer' AND name = 'countries'),
    '{}');
