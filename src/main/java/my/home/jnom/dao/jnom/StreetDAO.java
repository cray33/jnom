package my.home.jnom.dao.jnom;

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
public class StreetDAO {
    @Autowired
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
        return jdbcTemplate.query("SELECT id, osm_id, name, adm_boundary_osm_id, city_osm_id, lat, lon \n" +
                " FROM jnom.street " +
                " WHERE city_osm_id = ? AND name = ? " +
                " LIMIT 1", (rs, i) -> {
                    StreetEntity entity = new StreetEntity(UUID.fromString(rs.getString("id")));
                    entity.setOsmId(rs.getLong("osm_id"));
                    entity.setCityOsmId(rs.getLong("city_osm_id"));
                    entity.setName(rs.getString("name"));
                    entity.setLat(rs.getDouble("lat"));
                    entity.setLon(rs.getDouble("lon"));
                    //entity.setWay((PGgeometry) rs.getObject("way"));
                    entity.setConsistOfOsmIds(Arrays.asList((Long[]) rs.getArray("consist_of").getArray()));
                    return entity;
                }, cityOsmId, streetName);
    }

    public List<StreetEntity> findStreetsFormatName(Long cityId, String query) {
        return jdbcTemplate.query("SELECT street.id AS id, street.osm_id, street.name || ' (' || adm.name || ')' as name, street.city_osm_id, " +
                " street.lat, street.lon \n" +
                " FROM jnom.street street" +
                " INNER JOIN jnom.administrative_boundary adm ON street.adm_boundary_osm_id = adm.osm_id" +
                " WHERE street.city_osm_id = ? " +
                " AND street.name ILIKE '%' || ? || '%'" +
                " ORDER BY street.name ASC", new RowMapper<StreetEntity>() {
            @Override
            public StreetEntity mapRow(ResultSet rs, int i) throws SQLException {
                StreetEntity dto = new StreetEntity(UUID.fromString(rs.getString("id")));
                dto.setOsmId(rs.getLong("osm_id"));
                dto.setCityOsmId(rs.getLong("city_osm_id"));
                dto.setName(rs.getString("name"));
                dto.setLat(rs.getDouble("lat"));
                dto.setLon(rs.getDouble("lon"));
                return dto;
            }
        }, cityId, query);
    }
}
