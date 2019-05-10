package flyway.asdi;

import java.sql.Connection;
import java.util.List;

import org.flywaydb.core.api.migration.jdbc.JdbcMigration;

import fi.nls.oskari.util.FlywayHelper;

/**
 * Adds printout bundle to all user/default views
 */
public class V1_17_0__add_printout_bundle_to_views implements JdbcMigration {
	
	private static final String BUNDLE_ID = "printout";
	
	@Override
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
