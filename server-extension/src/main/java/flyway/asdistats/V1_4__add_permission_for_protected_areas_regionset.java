package flyway.asdistats;

import fi.nls.oskari.domain.map.OskariLayer;
import org.oskari.permissions.model.OskariLayerResource;
import org.oskari.permissions.model.Resource;

import java.util.ArrayList;
import java.util.List;

/**
 * Add permissions for regionset: protected areas
 */
public class V1_4__add_permission_for_protected_areas_regionset extends V1_2__add_permission_for_regionset {

    // statslayers described as layer resources for permissions handling
    protected List<Resource> getResources() {
        List<Resource> list = new ArrayList<>();
        list.add(new OskariLayerResource(OskariLayer.TYPE_STATS, "resources://regionsets/protectedareas-3575.json", "protectedareas-3575"));
        return list;
    }
}
