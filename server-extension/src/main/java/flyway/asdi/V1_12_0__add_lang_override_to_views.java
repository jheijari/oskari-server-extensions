package flyway.asdi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;

/**
 * Created by MMLDEV on 16.1.2018.
 */
public class V1_12_0__add_lang_override_to_views implements JdbcMigration {
    private static final  String BUNDLE_ID = "asdi-lang-overrides";

    public void migrate(Connection connection)
            throws Exception {
        // Get default views
        final ArrayList<Long> views = getDefaultViews(connection);

        for(int i=0;i<views.size();i++){
            Long viewId = views.get(i);
            final boolean hasLangOverride = hasExistingBundle(connection, BUNDLE_ID, viewId);
            if (!hasLangOverride) {
                makeInsert(viewId, connection);
            }
        }
    }

    private void makeInsert(Long viewId, Connection connection) throws SQLException {
        final PreparedStatement statement =
                connection.prepareStatement("INSERT INTO portti_view_bundle_seq" +
                        "(view_id, bundle_id, seqno, config, state, startup, bundleinstance) " +
                        "VALUES (" +
                        "?, " +
                        "(SELECT id FROM portti_bundle WHERE name=?), " +
                        "0, " +
                        "?, ?, " +
                        "(SELECT startup FROM portti_bundle WHERE name=?), " +
                        "?)");
        try {
            statement.setLong(1, viewId);
            statement.setString(2, BUNDLE_ID);
            statement.setString(3, "{}");
            statement.setString(4, "{}");
            statement.setString(5, BUNDLE_ID);
            statement.setString(6, BUNDLE_ID);
            statement.execute();
        } finally {
            statement.close();
        }
    }

    private ArrayList<Long> getDefaultViews(Connection connection) throws Exception {
        ArrayList<Long> ids = new ArrayList<Long>();

        final PreparedStatement statement =
                connection.prepareStatement("SELECT id FROM portti_view " +
                        "WHERE type='DEFAULT' OR type='USER'");
        try {
            ResultSet rs = statement.executeQuery();
            while(rs.next()) {
                ids.add(rs.getLong("id"));
            }
        } finally {
            statement.close();
        }
        return ids;
    }

    private boolean hasExistingBundle(Connection connection, String bundle, Long viewId)
            throws Exception {
        final PreparedStatement statement =
                connection.prepareStatement("SELECT * FROM portti_view_bundle_seq " +
                        "WHERE bundle_id = (SELECT id FROM portti_bundle WHERE name=?) " +
                        "AND view_id=?");
        statement.setString(1,bundle);
        statement.setLong(2, viewId);
        try {
            ResultSet rs = statement.executeQuery();
            return rs.next();
        } finally {
            statement.close();
        }
    }
}
