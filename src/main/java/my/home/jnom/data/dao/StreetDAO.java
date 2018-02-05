package my.home.jnom.data.dao;

import my.home.jnom.data.entity.CityEntity;
import my.home.jnom.data.entity.CityOrAdmBoundaryEntity;
import my.home.jnom.data.entity.StreetEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class StreetDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void insertOfUpdate(StreetEntity entity) {
        boolean isExist = jdbcTemplate.queryForObject(
                "SELECT coalesce((SELECT true FROM data.street WHERE osm_id = ? LIMIT 1), false)",
                Boolean.TYPE, entity.getOsmId());

        boolean isCopy = jdbcTemplate.queryForObject(
                "SELECT coalesce((SELECT true FROM data.street " +
                        " WHERE coalesce(city_osm_id, 1) = coalesce(?, 1) AND adm_boundary_osm_id = ?" +
                        " AND name = ? AND is_copy != true AND osm_id != ? LIMIT 1), false)",
                Boolean.TYPE, entity.getCityOsmId(), entity.getAdmBoundaryOsmId(),
                entity.getName(), entity.getOsmId());

        if (isExist) {
            jdbcTemplate.update("UPDATE data.street\n" +
                    "   SET name=?, tags=?, city_osm_id=?, adm_boundary_osm_id=?, is_copy=?, lat=?, lon=? \n" +
                    " WHERE osm_id=? ",
                    entity.getName(), entity.getTags(),
                    entity.getCityOsmId(), entity.getAdmBoundaryOsmId(), isCopy,
                    entity.getLat(), entity.getLon(),
                    entity.getOsmId());
        } else {
            jdbcTemplate.update("INSERT INTO data.street(\n" +
                    "            osm_id, name, tags, city_osm_id, adm_boundary_osm_id, is_copy, lat, lon)\n" +
                    "    VALUES (?, ?, ?, ?, ?, ?, ?, ?); ",
                    entity.getOsmId(), entity.getName(), entity.getTags(),
                    entity.getCityOsmId(), entity.getAdmBoundaryOsmId(), isCopy,
                    entity.getLat(), entity.getLon());
        }

    }

    public List<StreetEntity> findStreets(Long admBoundaryOsmId, String query) {
        return jdbcTemplate.query("SELECT osm_id, name, city_osm_id, adm_boundary_osm_id," +
                " lat, lon \n" +
                " FROM data.street " +
                " WHERE data.check_parent_has_child(adm_boundary_osm_id, ?) " +
                " AND name ILIKE '%' || ? || '%'" +
                " AND is_copy != true" +
                " ORDER BY name ASC", new RowMapper<StreetEntity>() {
            @Override
            public StreetEntity mapRow(ResultSet rs, int i) throws SQLException {
                StreetEntity dto = new StreetEntity();
                dto.setOsmId(rs.getLong("osm_id"));
                dto.setCityOsmId(rs.getLong("city_osm_id"));
                dto.setAdmBoundaryOsmId(rs.getLong("adm_boundary_osm_id"));
                dto.setName(rs.getString("name"));
                dto.setLat(rs.getDouble("lat"));
                dto.setLon(rs.getDouble("lon"));
                return dto;
            }
        }, admBoundaryOsmId, query);
    }
}
