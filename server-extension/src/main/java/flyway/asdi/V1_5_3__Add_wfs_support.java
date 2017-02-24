package flyway.asdi;

import fi.nls.oskari.domain.map.view.Bundle;
import fi.nls.oskari.domain.map.view.View;
import fi.nls.oskari.domain.map.view.ViewTypes;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.map.view.ViewService;
import fi.nls.oskari.map.view.ViewServiceIbatisImpl;
import fi.nls.oskari.util.JSONHelper;
import fi.nls.oskari.view.modifier.ViewModifier;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.util.List;

/**
 * Created by SMAKINEN on 24.2.2017.
 */
public class V1_5_3__Add_wfs_support implements JdbcMigration {

    private static final Logger LOG = LogFactory.getLogger(V1_5_3__Add_wfs_support.class);

    private ViewService service = new ViewServiceIbatisImpl();
    private static final int BATCH_SIZE = 50;
    private int updatedViewCount = 0;

    private static final String PLUGIN_NAME = "Oskari.mapframework.bundle.mapwfs2.plugin.WfsLayerPlugin";

    public void migrate(Connection connection)
            throws Exception {
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
            boolean addedPlugin = addPlugin(mapfull);
            if(!addedPlugin) {
                // if neither was done, skip update
                continue;
            }
            service.updateBundleSettingsForView(view.getId(), mapfull);
            updatedViewCount++;
        }
        return list.size() == BATCH_SIZE;
    }


    private boolean addPlugin(final Bundle mapfull) throws JSONException {
        final JSONObject config = mapfull.getConfigJSON();
        final JSONArray plugins = config.optJSONArray("plugins");
        if(plugins == null) {
            throw new RuntimeException("No plugins" + config.toString(2));
            //continue;
        }
        boolean found = false;
        for(int i = 0; i < plugins.length(); ++i) {
            JSONObject plugin = plugins.getJSONObject(i);
            if(PLUGIN_NAME.equals(plugin.optString("id"))) {
                found = true;
                break;
            }
        }
        // add plugin if not there yet
        if(!found) {
            plugins.put(JSONHelper.createJSONObject("id", PLUGIN_NAME));
        }
        return true;
    }
}
