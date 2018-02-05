package flyway.asdi;

import fi.nls.oskari.db.DBHandler;
import fi.nls.oskari.domain.map.view.Bundle;
import fi.nls.oskari.domain.map.view.View;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.map.view.ViewService;
import fi.nls.oskari.map.view.ViewServiceIbatisImpl;
import fi.nls.oskari.util.JSONHelper;
import fi.nls.oskari.view.modifier.ViewModifier;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.util.List;

/**
 * Created by SMAKINEN on 29.6.2015.
 */
public class V1__Extent_and_resolutions_change implements JdbcMigration {

    private static final Logger LOG = LogFactory.getLogger(V1__Extent_and_resolutions_change.class);

    private ViewService service = new ViewServiceIbatisImpl();
    private static final int BATCH_SIZE = 50;
    private static final double[] NEW_RESOLUTIONS = {
            38197.92815,
            19098.96407,
            9549.482037,
            4774.741019,
            2387.370509,
            1193.685255,
            596.8426273,
            298.4213137,
            149.2106568,
            74.60532841,
            37.30266421,
            18.6513321,
            9.325666052,
            4.662833026,
            2.331416513,
            1.165708256,
            0.582854128,
            0.291427064
    };

    private int updatedViewCount = 0;

    public void migrate(Connection connection)
            throws Exception {


        // add initial content
        DBHandler.setupAppContent(connection, "app-asdi.json");

        int page = 1;
        while(updateViews(page)) {
            page++;
        }
        LOG.info("Updated views:", updatedViewCount);
    }
    private boolean updateViews(int page)
            throws Exception {
        List<View> list = service.getViews(page, BATCH_SIZE);
        LOG.info("Got", list.size(), "views on page", page);
        for(View view : list) {
            final Bundle mapfull = view.getBundleByName(ViewModifier.BUNDLE_MAPFULL);
            final JSONObject config = mapfull.getConfigJSON();
            final JSONObject mapOptions = config.optJSONObject("mapOptions");
            if(mapOptions == null) {
                throw new RuntimeException("No mapOptions" + config.toString(2));
                //continue;
            }
            final JSONObject maxExtent = mapOptions.optJSONObject("maxExtent");
            final boolean extentUpdateSucceeded =
                    JSONHelper.putValue(maxExtent, "bottom", -4889334.802954878) &&
                    JSONHelper.putValue(maxExtent, "left", -4889334.802954878) &&
                    JSONHelper.putValue(maxExtent, "right", 4889334.802954878) &&
                    JSONHelper.putValue(maxExtent, "top", 	4889334.802954878);
            if(!extentUpdateSucceeded) {
                throw new RuntimeException("Error updating extent" + maxExtent.toString(2));
            }
            final JSONArray resolutions = new JSONArray(NEW_RESOLUTIONS);
            // overwrite resolutions
            final boolean resolutionUpdateSucceeded =
                    JSONHelper.putValue(mapOptions, "resolutions", resolutions);
            if(!resolutionUpdateSucceeded) {
                throw new RuntimeException("Error updating resolutions" + resolutions.toString(2));
            }

            service.updateBundleSettingsForView(view.getId(), mapfull);
            updatedViewCount++;
        }
        return list.size() == BATCH_SIZE;
    }
}
