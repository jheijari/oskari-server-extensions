package flyway.asdi;

import java.sql.Connection;
import java.util.List;

import org.flywaydb.core.api.migration.jdbc.JdbcMigration;

import fi.nls.oskari.util.FlywayHelper;

public class V1_19_1__add_oskari_guided_tour_to_views implements JdbcMigration {

	private static final String BUNDLE_NAME = "guidedtour";

	@Override
	public void migrate(Connection connection) throws Exception {

		final List<Long> views = FlywayHelper.getUserAndDefaultViewIds(connection);
		for (Long viewId : views) {
			if (FlywayHelper.viewContainsBundle(connection, BUNDLE_NAME, viewId)) {
				continue;
			}
			FlywayHelper.addBundleWithDefaults(connection, viewId, BUNDLE_NAME);
		}
	}

}
