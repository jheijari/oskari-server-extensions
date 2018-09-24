package flyway.asdistats;

import fi.nls.oskari.util.FlywayHelper;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;

import java.sql.Connection;
import java.util.List;

/**
 * Add statsgrid bundle to views that don't have it.
 */
public class V1_0__add_statsgrid_to_geoportal implements JdbcMigration {
    private static final String BUNDLE_ID = "statsgrid";

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
