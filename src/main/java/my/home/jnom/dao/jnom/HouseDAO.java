package my.home.jnom.dao.jnom;

import lombok.AllArgsConstructor;
import my.home.jnom.entity.HouseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Repository
@AllArgsConstructor
public class HouseDAO {
    private JdbcTemplate jdbcTemplate;

    public void insertOfUpdate(HouseEntity entity) {
        boolean isExist = jdbcTemplate.queryForObject(
                "SELECT coalesce((SELECT true FROM jnom.house WHERE osm_id = ? LIMIT 1), false)",
                Boolean.TYPE, entity.getOsmId());

        if (isExist) {
            jdbcTemplate.update("UPDATE jnom.house\n" +
                    "   SET tags=?, house_number=?, street_id=?, city_osm_id=?, \n" +
                    "       adm_boundary_osm_id=?, lat=?, lon=?\n" +
                    " WHERE osm_id=?;",
                    entity.getTags(), entity.getHouseNumber(),  entity.getStreetId(),
                    entity.getCityOsmId(), entity.getAdmBoundaryOsmId(), entity.getLat(), entity.getLon(),
                    entity.getOsmId());
        } else {
            jdbcTemplate.update("INSERT INTO jnom.house(\n" +
                    "            osm_id, tags, house_number, street_id, city_osm_id, adm_boundary_osm_id, \n" +
                    "            lat, lon)\n" +
                    "    VALUES (?, ?, ?, ?, ?, ?, ?, ?);\n",
                    entity.getOsmId(), entity.getTags(), entity.getHouseNumber(), entity.getStreetId(),
                    entity.getCityOsmId(), entity.getAdmBoundaryOsmId(), entity.getLat(), entity.getLon());
        }

    }

    public List<HouseEntity> findHouses(Long cityOsmId, UUID streetId, String query) {
        return jdbcTemplate.query("SELECT osm_id, house_number, street_id, city_osm_id, adm_boundary_osm_id, \n" +
                " lat, lon "  +
                " FROM jnom.house " +
                " WHERE city_osm_id = ? \n" +
                " AND street_id = ? \n" +
                " AND house_number LIKE ? || '%'" +
                " ORDER BY house_number ASC", new RowMapper<HouseEntity>() {
            @Override
            public HouseEntity mapRow(ResultSet rs, int i) throws SQLException {
                return HouseEntity.builder()
                        .osmId(rs.getLong("osm_id"))
                        .streetId(UUID.fromString(rs.getString("street_id")))
                        .cityOsmId(rs.getLong("city_osm_id"))
                        .admBoundaryOsmId(rs.getLong("adm_boundary_osm_id"))
                        .houseNumber(rs.getString("house_number"))
                        .lat(rs.getDouble("lat"))
                        .lon(rs.getDouble("lon"))
                        .build();
            }
        }, cityOsmId, streetId, query);
    }
}
