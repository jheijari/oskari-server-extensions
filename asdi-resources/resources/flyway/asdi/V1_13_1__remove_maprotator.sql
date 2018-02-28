-- Removes maprotator bundle from all user/default views

DELETE FROM portti_view_bundle_seq s
USING portti_view v
WHERE s.view_id = v.id AND
v.type IN ('USER', 'DEFAULT') AND
s.bundle_id = (SELECT id from portti_bundle WHERE name = 'maprotator');
