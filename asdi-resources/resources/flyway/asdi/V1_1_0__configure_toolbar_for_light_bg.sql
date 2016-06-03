-- update all non-publish(ed) views to have toolbar config;
update portti_view_bundle_seq set config='{
  "viewtools": {"link":false},
  "colours": {
     "hover": "#009FE3",
     "background": "#ffffff"
  }
 }' where bundle_id = (select id from portti_bundle where name = 'toolbar') and view_id NOT IN (select id from portti_view where type = 'PUBLISHED' OR type = 'PUBLISH');