package flyway.asdi;

import org.json.JSONObject;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by SMAKINEN on 31.5.2016.
 */
public class V1_1_3__maplayer_categories_updateTest {

    @Test
    public void testGetThemes()
            throws Exception {
        V1_1_3__maplayer_categories_update u = new V1_1_3__maplayer_categories_update();
        List<JSONObject> categories = u.getThemes();
        assertEquals("Should get 19 categories", 19, categories.size());
    }
}