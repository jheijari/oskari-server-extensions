package flyway.asdi;

import fi.nls.oskari.db.BundleHelper;
import fi.nls.oskari.domain.map.view.Bundle;
import fi.nls.oskari.util.FlywayHelper;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;

import java.sql.Connection;
import java.util.List;

/**
 * Registers guided tour bundle for ASDI and adds it to default and user views
 */
public class V1_5_2__Add_guidedtour implements JdbcMigration {

    private static final String BUNDLE_ID = "asdi-guided-tour";

    public void migrate(Connection connection)
            throws Exception {

        // BundleHelper checks if these bundles are already registered
        Bundle bundle = new Bundle();
        bundle.setName(BUNDLE_ID);
        bundle.setStartup(BundleHelper.getDefaultBundleStartup("asdi", BUNDLE_ID, "Guided tour"));
        BundleHelper.registerBundle(bundle, connection);

        final List<Long> views = FlywayHelper.getUserAndDefaultViewIds(connection);
        for(Long viewId : views){
            if (FlywayHelper.viewContainsBundle(connection, BUNDLE_ID, viewId)) {
                continue;
            }
            FlywayHelper.addBundleWithDefaults(connection, viewId, BUNDLE_ID);
        }
    }
}
