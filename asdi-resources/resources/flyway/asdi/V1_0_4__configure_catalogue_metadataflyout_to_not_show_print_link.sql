UPDATE portti_view_bundle_seq
   SET config='{"hideMetaDataPrintLink":true}'
 WHERE bundle_id=(SELECT id FROM portti_bundle WHERE name='metadataflyout');