package flyway.asdi;

import java.sql.Connection;
import java.util.List;

import org.flywaydb.core.api.migration.jdbc.JdbcMigration;

import fi.nls.oskari.db.BundleHelper;
import fi.nls.oskari.domain.map.view.Bundle;
import fi.nls.oskari.util.FlywayHelper;

public class V1_19_2__add_guided_tour_extension implements JdbcMigration {

	private static final String BUNDLE_ID = "guided-tour-extension";

	@Override
	public void migrate(Connection connection) throws Exception {

		// BundleHelper checks if these bundles are already registered
		Bundle bundle = new Bundle();
		bundle.setName(BUNDLE_ID);
		BundleHelper.registerBundle(bundle, connection);

		final List<Long> views = FlywayHelper.getUserAndDefaultViewIds(connection);
		for (Long viewId : views) {
			if (FlywayHelper.viewContainsBundle(connection, BUNDLE_ID, viewId)) {
				continue;
			}
			FlywayHelper.addBundleWithDefaults(connection, viewId, BUNDLE_ID);
		}
	}
}
