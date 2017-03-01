package flyway.asdi;

import fi.nls.oskari.util.FlywayHelper;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;

import java.sql.Connection;
import java.util.List;

/**
 * Add featuredata2 bundle to views that don't have it.
 */
public class V1_6_0__Add_featuredata_to_geoportal implements JdbcMigration {
    private static final String BUNDLE_ID = "featuredata2";

    public void migrate(Connection connection)
            throws Exception {
        final List<Long> views = FlywayHelper.getUserAndDefaultViewIds(connection);
        for (Long viewId : views) {
            if (FlywayHelper.viewContainsBundle(connection, BUNDLE_ID, viewId)) {
                continue;
            }
            FlywayHelper.addBundleWithDefaults(connection, viewId, BUNDLE_ID);
        }
    }
}
