package flyway.asdi;

import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.util.IOHelper;
import fi.nls.oskari.util.JSONHelper;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 1) insert new themes
 * 2) remove all theme links
 * 3) remove old themes
 * 4) select one of new themes and link all maplayers to it
 */
public class V1_1_3__maplayer_categories_update implements JdbcMigration {

    private static final Logger LOG = LogFactory.getLogger(V1_1_3__maplayer_categories_update.class);


    public void migrate(Connection connection)
            throws Exception {
        // read new categories to replace themes from JSON-files
        List<JSONObject> categories = getThemes();
        // delete all old themes
        removeThemes(connection);
        // insert new categories
        insertCategories(categories, connection);
        // get an id for any of the new categories
        long id = getSomeRandomCategoryId(connection);
        List<Long> layerIdList = getMaplayerIds(connection);
        // link all layers to id
        linkLayers(layerIdList, id, connection);
    }

    public List<JSONObject> getThemes() throws Exception {
        Map<String, String> en = readJSON("en");
        Map<String, String> fi = readJSON("fi");
        Map<String, String> sv = readJSON("sv");
        List<JSONObject> list = new ArrayList<>(en.size());
        for(Map.Entry<String, String> item : en.entrySet()) {
            JSONObject theme = new JSONObject();
            JSONHelper.putValue(theme, "en", JSONHelper.createJSONObject("name", item.getValue()));
            JSONHelper.putValue(theme, "fi", JSONHelper.createJSONObject("name", fi.get(item.getKey())));
            JSONHelper.putValue(theme, "sv", JSONHelper.createJSONObject("name", sv.get(item.getKey())));
            // oikein:
            //{"fi":{"name":"Koordinaattijärjestelmät"}, "sv": { "name" : "Referenskoordinatsystem"},"en": { "name" : "Coordinate reference systems"}}
            // nyt menee väärin:
            // {"name":{"fi":"Aluesuunnittelu/kiinteistöjärjestelmä","sv":"Fastigheter och fysisk planering","en":"Planning / Cadastre"}}
            list.add(theme);
        }
        return list;
    }

    public void removeThemes(Connection conn) throws Exception {
        // oskari_maplayer_themes should cascade delete;
        // SELECT maplayerid, themeid FROM oskari_maplayer_themes;
        // SELECT id, locale FROM portti_inspiretheme;
        final String sql = "DELETE FROM oskari_maplayer_group";
        try (final PreparedStatement statement = conn.prepareStatement(sql)){
            statement.execute();
        }
    }

    private void insertCategories(List<JSONObject> categories, Connection conn) throws SQLException {
        final String sql = "INSERT INTO oskari_maplayer_group (locale) VALUES (?)";
        for(JSONObject theme : categories) {
            try (final PreparedStatement statement = conn.prepareStatement(sql)){
                statement.setString(1, theme.toString());
                statement.execute();
            }
        }
    }
    private void linkLayers(List<Long> layers, final long themeid, Connection conn) throws SQLException {
        final String sql = "INSERT INTO oskari_maplayer_group_link (maplayerid, groupid) VALUES (?, ?)";
        for(Long layerid : layers) {
            try (final PreparedStatement statement = conn.prepareStatement(sql)){
                statement.setLong(1, layerid);
                statement.setLong(2, themeid);
                statement.execute();
            }
        }
    }

    private long getSomeRandomCategoryId(Connection connection) throws Exception {
        final String sql = "SELECT id FROM oskari_maplayer_group LIMIT 1";
        try (final PreparedStatement statement = connection.prepareStatement(sql)) {
            try (ResultSet rs = statement.executeQuery()) {
                if(rs.next()) {
                    return rs.getLong("id");
                }
            }
        }
        throw new RuntimeException("No category id found - cannot proceed!");
    }

    private List<Long> getMaplayerIds(Connection connection) throws Exception {
        List<Long> list = new ArrayList<>();
        final String sql = "SELECT id FROM oskari_maplayer";
        try (final PreparedStatement statement = connection.prepareStatement(sql)) {
            try (ResultSet rs = statement.executeQuery()) {
                while(rs.next()) {
                    list.add(rs.getLong("id"));
                }
            }
        }
        return list;
    }

    public Map<String, String> readJSON(final String lang) throws Exception {
        final String file = IOHelper.readString(this.getClass().getResourceAsStream("/json/TopicCategory." + lang + ".json"));
        JSONObject json = JSONHelper.createJSONObject(file);
        JSONObject codelist = json.optJSONObject("metadata-codelist");
        JSONArray items = codelist.optJSONArray("containeditems");

        Map<String, String> idAndName = new HashMap<>();
        for(int i = 0; i < items.length(); ++i) {
            JSONObject item = items.optJSONObject(i);
            idAndName.put(getId(item), getLabel(item));
        }
        return idAndName;
    }

    private String getId(JSONObject item) {
        JSONObject value = item.optJSONObject("value");
        return value.optString("id");
    }
    private String getLabel(JSONObject item) {
        JSONObject value = item.optJSONObject("value");
        JSONObject label = value.optJSONObject("label");
        return label.optString("text");
    }

}
