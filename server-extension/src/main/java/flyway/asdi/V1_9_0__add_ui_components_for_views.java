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
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.util.List;


/**
 * Adds "ui-components" bundle to mapfull imports.
 * Fixes an issue of missing PopupService in non-minified envs.
 */
public class V1_9_0__add_ui_components_for_views implements JdbcMigration {
    private static final String BUNDLE_ID = "ui-components";

    private static final Logger LOG = LogFactory.getLogger(V1_9_0__add_ui_components_for_views.class);

    private ViewService service;
    private static final int BATCH_SIZE = 50;
    private int updatedViewCount = 0;

    public void migrate(Connection connection)
            throws Exception {
        service = new AppSetupServiceMybatisImpl();
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
            boolean addedImport = addImport(mapfull, BUNDLE_ID, "/Oskari/packages/framework/bundle/");
            if(!addedImport) {
                // if not modified, skip update
                continue;
            }
            service.updateBundleSettingsForView(view.getId(), mapfull);
            updatedViewCount++;
        }
        return list.size() == BATCH_SIZE;
    }


    private boolean addImport(final Bundle mapfull, String name, String path) throws JSONException {
        final JSONObject startup = new JSONObject(mapfull.getStartup());
        final JSONObject metadata = startup.optJSONObject("metadata");
        if(metadata == null) {
            return false;
        }
        final JSONObject imports = metadata.optJSONObject("Import-Bundle");
        if(imports == null) {
            return false;
        }
        JSONObject bundle = imports.optJSONObject(name);
        if(bundle != null) {
            return false;
        }
        bundle = JSONHelper.createJSONObject("bundlePath", path);
        imports.put(name, bundle);
        mapfull.setStartup(startup.toString(2));
        return true;
    }
}
