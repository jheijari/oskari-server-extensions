package flyway.asdi;

import fi.nls.oskari.domain.map.view.Bundle;
import fi.nls.oskari.domain.map.view.View;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.map.view.AppSetupServiceMybatisImpl;
import fi.nls.oskari.map.view.ViewService;
import fi.nls.oskari.util.JSONHelper;
import fi.nls.oskari.view.modifier.ViewModifier;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.util.List;

/**
 * Created by SMAKINEN on 29.6.2015.
 */
public class V1_0_1__Add_Arcgis_support implements JdbcMigration {

    private static final Logger LOG = LogFactory.getLogger(V1_0_1__Add_Arcgis_support.class);

    private ViewService service = new AppSetupServiceMybatisImpl();
    private static final int BATCH_SIZE = 50;
    private int updatedViewCount = 0;

    private static final String MAP_ARCGIS = "maparcgis";
    private static final String PLUGIN_NAME = "Oskari.arcgis.bundle.maparcgis.plugin.ArcGisLayerPlugin";

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
            boolean addedImport = updateImportBundles(mapfull);
            boolean addedPlugin = addPlugin(mapfull);
            if(!addedImport && !addedPlugin) {
                // if neither was done, skip update
                continue;
            }
            service.updateBundleSettingsForView(view.getId(), mapfull);
            updatedViewCount++;
        }
        return list.size() == BATCH_SIZE;
    }

    private boolean updateImportBundles(final Bundle mapfull) throws JSONException {
        final JSONObject startup = JSONHelper.createJSONObject(mapfull.getStartup());
        final JSONObject metadata = startup.optJSONObject("metadata");
        final JSONObject importedBundles = metadata.optJSONObject("Import-Bundle");
        if(importedBundles.has(MAP_ARCGIS)) {
            // already has it
            return false;
        }
        final JSONObject mapArcgis = JSONHelper.createJSONObject("bundlePath", "/Oskari/packages/arcgis/bundle/");
        final boolean success = JSONHelper.putValue(importedBundles, MAP_ARCGIS, mapArcgis);

        if(!success) {
            throw new RuntimeException("Error updating importedBundles" + importedBundles.toString(2));
        }
        mapfull.setStartup(startup.toString(2));
        return true;
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
