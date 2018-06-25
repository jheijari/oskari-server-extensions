package flyway.asdi;

import fi.nls.oskari.map.layer.OskariLayerService;
import fi.nls.oskari.map.view.util.ViewHelper;
import fi.nls.oskari.service.ServiceException;
import fi.nls.oskari.service.capabilities.CapabilitiesCacheService;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;
import org.oskari.capabilities.CapabilitiesUpdateService;
import org.oskari.service.util.ServiceFactory;

import java.sql.Connection;
import java.util.Set;

/**
 * Created by MMLDEV on 16.1.2018.
 */
public class V1_14_2__trigger_capabilities_update implements JdbcMigration {


    public void migrate(Connection connection) throws Exception {
        OskariLayerService layerService = ServiceFactory.getMapLayerService();

        CapabilitiesCacheService capabilitiesCacheService = ServiceFactory.getCapabilitiesCacheService();
        CapabilitiesUpdateService capabilitiesUpdateService = new CapabilitiesUpdateService(
                layerService, capabilitiesCacheService);

        try {
            capabilitiesUpdateService.updateCapabilities(layerService.findAll(), getSystemCRSs());
        } catch (Exception ignore) {
            // some will fail and that is ok
        }
    }

    private Set<String> getSystemCRSs() throws ServiceException {
        return ViewHelper.getSystemCRSs(ServiceFactory.getViewService());
    }
}
