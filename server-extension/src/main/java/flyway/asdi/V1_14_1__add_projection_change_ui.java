package flyway.asdi;

import fi.nls.oskari.db.BundleHelper;
import fi.nls.oskari.domain.map.view.Bundle;
import fi.nls.oskari.domain.map.view.View;
import fi.nls.oskari.map.view.ViewService;
import fi.nls.oskari.map.view.ViewServiceIbatisImpl;
import fi.nls.oskari.util.FlywayHelper;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;
import org.json.JSONObject;

import java.sql.Connection;
import java.util.List;

/**
 * Created by MMLDEV on 16.1.2018.
 */
public class V1_14_1__add_projection_change_ui implements JdbcMigration {

    private static final String BUNDLE_ID = "asdi-projection-change";

    public void migrate(Connection connection) throws Exception {
        // register
        Bundle bundle = new Bundle();
        bundle.setConfig("{}");
        bundle.setState("{}");
        bundle.setName(BUNDLE_ID);
        bundle.setStartup(BundleHelper.getDefaultBundleStartup("asdi", BUNDLE_ID, "Projection change UI"));
        BundleHelper.registerBundle(bundle);

        // add to appsetups
        final List<Long> views = FlywayHelper.getUserAndDefaultViewIds(connection);
        for (Long viewId : views) {
            if (FlywayHelper.viewContainsBundle(connection, BUNDLE_ID, viewId)) {
                continue;
            }
            FlywayHelper.addBundleWithDefaults(connection, viewId, BUNDLE_ID);
        }
    }
}
