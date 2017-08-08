package flyway.asdi;

import fi.mml.map.mapwindow.service.wms.WebMapService;
import fi.mml.map.mapwindow.service.wms.WebMapServiceFactory;
import fi.nls.oskari.domain.map.OskariLayer;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.map.layer.OskariLayerService;
import fi.nls.oskari.map.layer.formatters.LayerJSONFormatterWMS;
import fi.nls.oskari.service.ServiceException;
import fi.nls.oskari.service.capabilities.CapabilitiesCacheService;
import fi.nls.oskari.service.capabilities.OskariLayerCapabilities;
import fi.nls.oskari.util.JSONHelper;
import fi.nls.oskari.util.ServiceFactory;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.util.List;


/**
 * Adds "ui-components" bundle to mapfull imports.
 * Fixes an issue of missing PopupService in non-minified envs.
 */
public class V1_10_0__update_wmslayer_capabilities implements JdbcMigration {

    private static final Logger LOG = LogFactory.getLogger(V1_10_0__update_wmslayer_capabilities.class);

    private OskariLayerService mapLayerService;
    private CapabilitiesCacheService capabilitiesService;

    public void migrate(Connection connection)
            throws Exception {
        mapLayerService = ServiceFactory.getMapLayerService();
        capabilitiesService = ServiceFactory.getCapabilitiesCacheService();
        int updateCount = updateLayers();
        LOG.info("Updated views:", updateCount);
    }
    private int updateLayers() throws Exception {
        int updateCount = 0 ;
        List<OskariLayer> list = mapLayerService.findAll();
        for(OskariLayer layer : list) {
            if(!layer.getType().equals(OskariLayer.TYPE_WMS)) {
                continue;
            }
            updateCaps(layer);
            // save layer.getStyle() and layer.getCapabilities()
            mapLayerService.update(layer);
            updateCount++;
        }
        return updateCount;
    }


    private void updateCaps(final OskariLayer ml) throws JSONException {
        try {
            OskariLayerCapabilities capabilities = capabilitiesService.getCapabilities(ml, true);
            // flush cache, otherwise only db is updated but code retains the old cached version
            WebMapServiceFactory.flushCache(ml.getId());
            // parse capabilities
            WebMapService wms = WebMapServiceFactory.createFromXML(ml.getName(), capabilities.getData());
            if (wms == null) {
                throw new ServiceException("Couldn't parse capabilities for service!");
            }
            JSONObject caps = LayerJSONFormatterWMS.createCapabilitiesJSON(wms);
            ml.setCapabilities(caps);
            // Fix default style, if no legendimage setup
            String style = this.getDefaultStyle(ml, caps);
            if (style != null) {
                ml.setStyle(style);
            }
        } catch (Exception e) {}
    }

    private static final String KEY_STYLES = "styles";
    private static final String KEY_NAME = "name";

    /**
     * Get 1st style name of capabilites styles
     * @param ml  layer data
     * @param caps  oskari wms capabilities
     * @return
     */
    private String getDefaultStyle(OskariLayer ml, final JSONObject caps) {
        String style = null;
        if (ml.getId() == -1 && ml.getLegendImage() == null && caps.has(KEY_STYLES)) {
            // Take 1st style name for default - geotools parsing is not always correct
            JSONArray styles = JSONHelper.getJSONArray(caps, KEY_STYLES);
            JSONObject jstyle = JSONHelper.getJSONObject(styles, 0);
            if (jstyle != null) {
                style = JSONHelper.getStringFromJSON(jstyle, KEY_NAME, null);
                return style;
            }
        }
        return style;
    }
}
