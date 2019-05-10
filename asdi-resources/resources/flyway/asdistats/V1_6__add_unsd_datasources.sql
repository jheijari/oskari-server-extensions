-- Goal 1
INSERT INTO oskari_statistical_datasource (locale, config, plugin)
VALUES ('{"en":{"name":"UN SDG goal 1: No poverty"}}', '{}', 'UNSD');

INSERT INTO oskari_statistical_layer(datasource_id, layer_id, config)
VALUES(
    (SELECT id FROM oskari_statistical_datasource
        WHERE locale like '%UN SDG goal 1: No poverty%'),
    (SELECT id FROM oskari_maplayer WHERE type='statslayer' AND name = 'ne_110m_countries-3575'),
    '{}');

-- Goal 2
INSERT INTO oskari_statistical_datasource (locale, config, plugin)
VALUES ('{"en":{"name":"UN SDG goal 2: Zero hunger"}}', '{}', 'UNSD');

INSERT INTO oskari_statistical_layer(datasource_id, layer_id, config)
VALUES(
    (SELECT id FROM oskari_statistical_datasource
        WHERE locale like '%UN SDG goal 2: Zero hunger%'),
    (SELECT id FROM oskari_maplayer WHERE type='statslayer' AND name = 'ne_110m_countries-3575'),
    '{}');

-- Goal 7
INSERT INTO oskari_statistical_datasource (locale, config, plugin)
VALUES ('{"en":{"name":"UN SDG goal 7: Affordable and clean energy"}}', '{}', 'UNSD');

INSERT INTO oskari_statistical_layer(datasource_id, layer_id, config)
VALUES(
    (SELECT id FROM oskari_statistical_datasource
        WHERE locale like '%UN SDG goal 7: Affordable and clean energy%'),
    (SELECT id FROM oskari_maplayer WHERE type='statslayer' AND name = 'ne_110m_countries-3575'),
    '{}');



update oskari_statistical_layer l
	set config = '{}'
	from oskari_statistical_datasource ds
	where l.datasource_id = ds.id and ds.plugin = 'UNSD';