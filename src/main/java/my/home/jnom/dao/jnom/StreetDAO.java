package my.home.jnom.dao.jnom;

import lombok.AllArgsConstructor;
import my.home.jnom.entity.StreetEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Repository
@AllArgsConstructor
public class StreetDAO {
    private JdbcTemplate jdbcTemplate;

    public void insertOfUpdate(StreetEntity entity) {
        boolean isExist = jdbcTemplate.queryForObject(
                "SELECT coalesce((SELECT true FROM jnom.street " +
                        " WHERE name = ? AND city_osm_id = ? AND adm_boundary_osm_id = ? LIMIT 1), false)",
                Boolean.TYPE, entity.getName(), entity.getCityOsmId(), entity.getAdmBoundaryOsmId());

        if (isExist) {
            jdbcTemplate.update("UPDATE jnom.street\n" +
                    "   SET osm_id=?, name=?, city_osm_id=?, adm_boundary_osm_id=?, lat=?, lon=?, way_length=?, consist_of=? \n" +
                    " WHERE name=? AND city_osm_id=? AND adm_boundary_osm_id = ? AND way_length < ?",
                    entity.getOsmId(), entity.getName(), entity.getCityOsmId(), entity.getAdmBoundaryOsmId(),
                    entity.getLat(), entity.getLon(), entity.getWayLength(), entity.getConsistOfOsmIds().toArray(new Long[0]),
                    entity.getName(), entity.getCityOsmId(),entity.getAdmBoundaryOsmId(), entity.getWayLength());
        } else {
            jdbcTemplate.update("INSERT INTO jnom.street(\n" +
                    "            id, osm_id, name, city_osm_id, adm_boundary_osm_id, lat, lon, way_length, consist_of)\n" +
                    "    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?); ",
                    entity.getId(), entity.getOsmId(), entity.getName(), entity.getCityOsmId(), entity.getAdmBoundaryOsmId(),
                    entity.getLat(), entity.getLon(), entity.getWayLength(), entity.getConsistOfOsmIds().toArray(new Long[0]));
        }

    }

    public List<StreetEntity> findStreets(Long cityOsmId, String streetName) {
        return jdbcTemplate.query("SELECT * \n" +
                " FROM jnom.street " +
                " WHERE city_osm_id = ? AND name = ? " +
                " LIMIT 1", new StreetRowMapper(), cityOsmId, streetName);
    }

    public List<StreetEntity> findStreetsAndFormatName(Long cityId, String query) {
        return jdbcTemplate.query("SELECT street.*, street.name || ' (' || adm.name || ')' as name " +
                " FROM jnom.street street" +
                " INNER JOIN jnom.administrative_boundary adm ON street.adm_boundary_osm_id = adm.osm_id" +
                " WHERE street.city_osm_id = ? " +
                " AND street.name ILIKE '%' || ? || '%'" +
                " ORDER BY street.name ASC", new StreetRowMapper(), cityId, query);
    }

    public List<StreetEntity> findAll() {
        return jdbcTemplate.query("SELECT * FROM jnom.street", new StreetRowMapper());
    }

    class StreetRowMapper implements RowMapper<StreetEntity> {

        @Override
        public StreetEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
            return StreetEntity.builder()
                    .id(UUID.fromString(rs.getString("id")))
                    .osmId(rs.getLong("osm_id"))
                    .name(rs.getString("name"))
                    .admBoundaryOsmId(rs.getLong("adm_boundary_osm_id"))
                    .cityOsmId(rs.getLong("city_osm_id"))
                    .lat(rs.getDouble("lat"))
                    .lon(rs.getDouble("lon"))
                    .wayLength(rs.getFloat("way_length"))
                    //.way((PGgeometry) rs.getObject("way"));
                    .consistOfOsmIds(Arrays.asList((Long[]) rs.getArray("consist_of").getArray()))
                    .build();
        }
    }
}
