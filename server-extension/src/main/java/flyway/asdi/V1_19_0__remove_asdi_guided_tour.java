package flyway.asdi;

import java.sql.Connection;
import java.util.List;

import org.flywaydb.core.api.migration.jdbc.JdbcMigration;

import fi.nls.oskari.db.BundleHelper;
import fi.nls.oskari.util.FlywayHelper;

public class V1_19_0__remove_asdi_guided_tour implements JdbcMigration {

	private static final String BUNDLE_NAME = "asdi-guided-tour";

	@Override
	public void migrate(Connection connection) throws Exception {

		final List<Long> views = FlywayHelper.getUserAndDefaultViewIds(connection);
		for (Long viewId : views) {
			if (FlywayHelper.viewContainsBundle(connection, BUNDLE_NAME, viewId)) {
				FlywayHelper.removeBundleFromView(connection, BUNDLE_NAME, viewId);
			}

		}
		BundleHelper.unregisterBundle(BUNDLE_NAME, connection);
	}

}
