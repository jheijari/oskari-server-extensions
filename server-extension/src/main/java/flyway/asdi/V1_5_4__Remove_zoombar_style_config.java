package flyway.asdi;

import fi.nls.oskari.domain.map.view.Bundle;
import fi.nls.oskari.domain.map.view.View;
import fi.nls.oskari.domain.map.view.ViewTypes;
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
public class V1_5_4__Remove_zoombar_style_config implements JdbcMigration {

    private static final Logger LOG = LogFactory.getLogger(V1_5_4__Remove_zoombar_style_config.class);

    private ViewService service = new ViewServiceIbatisImpl();
    private static final int BATCH_SIZE = 50;
    private int updatedViewCount = 0;

    private static final String PLUGIN_NAME_ZOOM = "Oskari.mapframework.bundle.mapmodule.plugin.Portti2Zoombar";
    private static final String PLUGIN_NAME_PAN = "Oskari.mapframework.bundle.mapmodule.plugin.PanButtons";

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
            boolean changed = adjustPlugins(mapfull);
            if(!changed) {
                // if neither was done, skip update
                continue;
            }
            // update mapOptions
            if(!view.getType().equalsIgnoreCase(ViewTypes.PUBLISHED) && !view.getType().equalsIgnoreCase(ViewTypes.PUBLISH_TEMPLATE)) {
                // update mapOptions
                JSONObject conf = mapfull.getConfigJSON();
                JSONObject opts = conf.optJSONObject("mapOptions");
                if(opts == null) {
                    opts = new JSONObject();
                    conf.put("mapOptions", opts);
                }
                JSONObject style = opts.optJSONObject("style");
                if(style == null) {
                    style = new JSONObject();
                    opts.put("style", style);
                }
                style.put("toolStyle", "rounded-light");
            }
            service.updateBundleSettingsForView(view.getId(), mapfull);
            updatedViewCount++;
        }
        return list.size() == BATCH_SIZE;
    }

    private boolean adjustPlugins(final Bundle mapfull) throws JSONException {
        final JSONObject config = mapfull.getConfigJSON();
        final JSONArray plugins = config.optJSONArray("plugins");
        if(plugins == null) {
            throw new RuntimeException("No plugins" + config.toString(2));
            //continue;
        }
        boolean found = false;
        for(int i = 0; i < plugins.length(); ++i) {
            JSONObject plugin = plugins.getJSONObject(i);
            String pluginId = plugin.optString("id");
            if(PLUGIN_NAME_ZOOM.equals(pluginId) ||
                    PLUGIN_NAME_PAN.equals(pluginId)) {
                JSONObject pluginConfig = plugin.optJSONObject("config");
                if(pluginConfig == null) {
                    continue;
                }
                pluginConfig.remove("toolStyle");
                found = true;
            }
        }
        return found;
    }
}
