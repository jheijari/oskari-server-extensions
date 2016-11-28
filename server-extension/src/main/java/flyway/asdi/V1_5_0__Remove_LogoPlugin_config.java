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
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.util.List;

/**
 * Removed mapUrlPrefix and termsUrl config from LogoPlugin if available
 */
public class V1_5_0__Remove_LogoPlugin_config implements JdbcMigration {

    private static final Logger LOG = LogFactory.getLogger(V1_5_0__Remove_LogoPlugin_config.class);

    private ViewService service = new ViewServiceIbatisImpl();
    private static final int BATCH_SIZE = 50;
    private int updatedViewCount = 0;

    private static final String PLUGIN_NAME = "Oskari.mapframework.bundle.mapmodule.plugin.LogoPlugin";

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
            boolean changed = adjustPlugin(mapfull);
            if(!changed) {
                // if neither was done, skip update
                continue;
            }
            service.updateBundleSettingsForView(view.getId(), mapfull);
            updatedViewCount++;
        }
        return list.size() == BATCH_SIZE;
    }


    private boolean adjustPlugin(final Bundle mapfull) throws JSONException {
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
                JSONObject pluginConfig = plugin.optJSONObject("config");
                if(pluginConfig == null) {
                    break;
                }
                pluginConfig.remove("mapUrlPrefix");
                pluginConfig.remove("termsUrl");
                found = true;
                break;
            }
        }
        return found;
    }
}
