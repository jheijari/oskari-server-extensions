INSERT INTO portti_view_bundle_seq(view_id, bundle_id, seqno, bundleinstance) VALUES (
	1,
	(SELECT id FROM portti_bundle WHERE name = 'language-selector'),
	(SELECT max(seqno) FROM portti_view_bundle_seq WHERE view_id = 1) + 1,
	'language-selector'
);
