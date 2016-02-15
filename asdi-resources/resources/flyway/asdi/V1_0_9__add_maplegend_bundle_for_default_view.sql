-- Add maplegend
INSERT
INTO portti_view_bundle_seq
(
	view_id,
	bundle_id,
	seqno,
	config,
	state,
	startup,
	bundleinstance
)
VALUES (
	(SELECT id FROM portti_view WHERE application='asdi_guest' AND type='DEFAULT'),
	(SELECT id FROM portti_bundle WHERE name='maplegend'),
	(SELECT max(seqno)+1 FROM portti_view_bundle_seq WHERE view_id=(SELECT id FROM portti_view WHERE application='asdi_guest' AND type='DEFAULT')),
	(SELECT config FROM portti_bundle WHERE name='maplegend'),
	(SELECT state FROM portti_bundle WHERE name='maplegend'),
	(SELECT startup FROM portti_bundle WHERE name='maplegend'),
	'maplegend'
);