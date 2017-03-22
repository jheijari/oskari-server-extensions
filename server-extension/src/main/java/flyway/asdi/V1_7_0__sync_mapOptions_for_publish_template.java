package flyway.asdi;

import fi.nls.oskari.domain.map.view.Bundle;
import fi.nls.oskari.domain.map.view.View;
import fi.nls.oskari.map.view.ViewService;
import fi.nls.oskari.map.view.ViewServiceIbatisImpl;
import fi.nls.oskari.util.PropertyUtil;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;
import org.json.JSONObject;

import java.sql.Connection;

/**
 * The publish template has different mapOptions than the geoportal view.
 * This migration loads the default view mapfull bundle config,
 * takes the mapOptions and overwrites the mapOptions in publish template.
 * Style is removed from mapOptions in publish template since publisher uses the default style.
 */
public class V1_7_0__sync_mapOptions_for_publish_template implements JdbcMigration {
    private static final String BUNDLE_ID = "mapfull";
    private static final String KEY_OPTS = "mapOptions";

    public void migrate(Connection connection)
            throws Exception {
        ViewService service = new ViewServiceIbatisImpl();
        View defaultView = service.getViewWithConf(PropertyUtil.getOptional("view.default", -1));
        if(defaultView == null) {
            throw new RuntimeException("Couldn't load default view");
        }
        View templateView = service.getViewWithConf(PropertyUtil.getOptional("view.template.publish", -1));
        if(templateView == null) {
            throw new RuntimeException("Couldn't load publish template");
        }
        JSONObject mapOptions = getMapOptions(defaultView);
        mapOptions.remove("style");
        Bundle templateMapfull = templateView.getBundleByName(BUNDLE_ID);
        templateMapfull.getConfigJSON().put(KEY_OPTS, mapOptions);
        service.updateBundleSettingsForView(templateView.getId(), templateMapfull);
    }

    private JSONObject getMapOptions(View view) {
        Bundle bundle = view.getBundleByName(BUNDLE_ID);
        JSONObject conf = bundle.getConfigJSON();
        return conf.optJSONObject(KEY_OPTS);
    }
}
