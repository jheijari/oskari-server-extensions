package flyway.asdi;

import fi.nls.oskari.domain.map.view.View;
import fi.nls.oskari.map.view.AppSetupServiceMybatisImpl;
import fi.nls.oskari.map.view.ViewService;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;
import org.json.JSONObject;

import java.sql.Connection;

/**
 * Created by MMLDEV on 16.1.2018.
 */
public class V1_14_0__projection_appsetups implements JdbcMigration {

    // EPSG:3571-3576 (North Pole LAEAs) 3575 already exists
    private static final String[] PROJECTIONS_TO_ADD = new String[] {"EPSG:3571", "EPSG:3572", "EPSG:3573", "EPSG:3574", "EPSG:3576"};

    public void migrate(Connection connection)
            throws Exception {
        ViewService service = new AppSetupServiceMybatisImpl();
        View defaultView = service.getViewWithConf(service.getDefaultViewId());
        for(String srs : PROJECTIONS_TO_ADD) {
            View view = defaultView.cloneBasicInfo();
            JSONObject opts = view.getMapOptions();
            opts.put("srsName", srs);
            service.addView(view);
        }
    }

}
