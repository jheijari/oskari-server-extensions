package flyway.asdistats;

import fi.mml.portti.domain.permissions.Permissions;
import fi.mml.portti.service.db.permissions.PermissionsService;
import fi.mml.portti.service.db.permissions.PermissionsServiceIbatisImpl;
import fi.nls.oskari.domain.Role;
import fi.nls.oskari.domain.User;
import fi.nls.oskari.domain.map.OskariLayer;
import fi.nls.oskari.service.ServiceException;
import fi.nls.oskari.service.ServiceRuntimeException;
import fi.nls.oskari.service.UserService;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;
import org.oskari.permissions.model.OskariLayerResource;
import org.oskari.permissions.model.Permission;
import org.oskari.permissions.model.Resource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Add permissions for regionset: countries
 */
public class V1_2__add_permission_for_regionset implements JdbcMigration {

    public void migrate(Connection connection)
            throws SQLException {
        PermissionsService service = new PermissionsServiceIbatisImpl();
        for(Resource resToUpdate : getResources()) {
            Resource dbRes = service.findResource(resToUpdate);
            if(dbRes != null) {
                resToUpdate = dbRes;
            }
            for(Role role : getRoles()) {
                if(resToUpdate.hasPermission(role, Permissions.PERMISSION_TYPE_VIEW_LAYER)) {
                    // already had the permission
                    continue;
                }
                final Permission permission = new Permission();
                permission.setExternalType(Permissions.EXTERNAL_TYPE_ROLE);
                permission.setType(Permissions.PERMISSION_TYPE_VIEW_LAYER);
                permission.setExternalId(Long.toString(role.getId()));
                resToUpdate.addPermission(permission);
            }
            service.saveResourcePermissions(resToUpdate);
        }
    }

    // statslayers described as layer resources for permissions handling
    protected List<Resource> getResources() {
        List<Resource> list = new ArrayList<>();
        list.add(new OskariLayerResource(OskariLayer.TYPE_STATS, "resources://regionsets/ne_110m_countries-3575.json", "ne_110m_countries-3575"));
        return list;
    }

    private List<Role> getRoles() {
        List<Role> list = new ArrayList<>();
        try {
            // "logged in" user
            list.add(Role.getDefaultUserRole());
            // guest user
            User guest = UserService.getInstance().getGuestUser();
            list.addAll(guest.getRoles());
        } catch (ServiceException ex) {
            throw new ServiceRuntimeException("Unable to get roles");
        }
        return list;
    }
}
