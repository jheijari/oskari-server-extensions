package flyway.asdi;

import fi.nls.oskari.domain.map.view.Bundle;
import fi.nls.oskari.domain.map.view.View;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.map.view.ViewService;
import fi.nls.oskari.map.view.ViewServiceIbatisImpl;
import fi.nls.oskari.view.modifier.ViewModifier;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;

public class V1_15_0__update_zoomlevels implements JdbcMigration {

    private static final Logger LOG = LogFactory.getLogger(V1_5_5__Add_mapwfs2_to_mapfull_imports.class);
    private static final int BATCH_SIZE = 50;
    private ViewService service;
    private int updatedViewCount = 0;
    private JSONArray resolutions;

    public void migrate(Connection connection)
            throws Exception {
        service = new ViewServiceIbatisImpl();
        resolutions = new JSONArray(Arrays.asList(new double[]{
                38197.92815, 19098.96407, 9549.482037, 4774.741019, 2387.370509, 1193.685255, 596.8426273,
                298.4213137, 149.2106568, 74.60532841, 37.30266421, 18.6513321, 9.325666052}));
        int page = 1;
        while (updateViews(page)) {
            page++;
        }
        LOG.info("Updated views:", updatedViewCount);
    }

    private boolean updateViews(int page)
            throws Exception {
        List<View> list = service.getViews(page, BATCH_SIZE);
        LOG.info("Got", list.size(), "views on page", page);
        for (View view : list) {
            final Bundle mapfull = view.getBundleByName(ViewModifier.BUNDLE_MAPFULL);
            // update mapOptions
            JSONObject config = mapfull.getConfigJSON();
            JSONObject opts = config.optJSONObject("mapOptions");
            if (opts == null) {
                continue;
            }
            opts.put("resolutions", resolutions);

            JSONObject state = mapfull.getStateJSON();
            if (state == null && state.optInt("zoom", -1) > 12) {
                state.put("zoom", 12);
            }

            service.updateBundleSettingsForView(view.getId(), mapfull);
            updatedViewCount++;
        }
        return list.size() == BATCH_SIZE;
    }

}
