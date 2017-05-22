package flyway.asdi;

import fi.nls.oskari.util.FlywayHelper;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;

import java.sql.Connection;
import java.util.List;


/**
 * Adds maprotator bundle to all user/default views
 */
public class V1_8_0__add_maprotator_to_views implements JdbcMigration {
    private static final String BUNDLE_ID = "maprotator";

    public void migrate(Connection connection) throws Exception {
        final List<Long> views = FlywayHelper.getUserAndDefaultViewIds(connection);
        for (Long viewId : views) {
            if (FlywayHelper.viewContainsBundle(connection, BUNDLE_ID, viewId)) {
                continue;
            }
            FlywayHelper.addBundleWithDefaults(connection, viewId, BUNDLE_ID);
        }
    }
}
