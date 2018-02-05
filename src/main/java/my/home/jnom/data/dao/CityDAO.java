package my.home.jnom.data.dao;

import my.home.jnom.data.entity.AdmBoundaryEntity;
import my.home.jnom.data.entity.CityEntity;
import my.home.jnom.data.entity.CityOrAdmBoundaryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


@Repository
public class CityDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void insertOfUpdate(CityEntity entity) {
        boolean isExist = jdbcTemplate.queryForObject(
                "SELECT coalesce((SELECT true FROM data.city WHERE osm_id = ? LIMIT 1), false)",
                Boolean.TYPE, entity.getOsmId());

        if (isExist) {
            jdbcTemplate.update("UPDATE data.city\n" +
                    "   SET name=?, tags=?, population=?, adm_boundary_osm_id=?, lat=?, lon=? \n" +
                    " WHERE osm_id=? ",
                    entity.getName(), entity.getTags(),
                    entity.getPopulation(), entity.getAdmBoundaryOsmId(),
                    entity.getLat(), entity.getLon(),
                    entity.getOsmId());
        } else {
            jdbcTemplate.update("INSERT INTO data.city(\n" +
                    "            osm_id, name, tags, population, adm_boundary_osm_id, lat, lon)\n" +
                    "    VALUES (?, ?, ?, ?, ?, ?, ?); ",
                    entity.getOsmId(), entity.getName(), entity.getTags(),
                    entity.getPopulation(), entity.getAdmBoundaryOsmId(),
                    entity.getLat(), entity.getLon());
        }

    }

    public List<CityOrAdmBoundaryEntity> findCities(String query) {
        return jdbcTemplate.query("SELECT osm_id, adm_boundary_osm_id, " +
                " name || ', ' || data.get_name_with_parents(adm_boundary_osm_id) as full_name, lat, lon \n" +
                " FROM data.city WHERE name ILIKE '%' || ? || '%'", new RowMapper<CityOrAdmBoundaryEntity>() {
            @Override
            public CityOrAdmBoundaryEntity mapRow(ResultSet rs, int i) throws SQLException {
                CityOrAdmBoundaryEntity dto = new CityOrAdmBoundaryEntity();
                dto.setCityOsmId(rs.getLong("osm_id"));
                dto.setAdmBoundaryOsmId(rs.getLong("adm_boundary_osm_id"));
                dto.setName(rs.getString("full_name"));
                dto.setLat(rs.getDouble("lat"));
                dto.setLon(rs.getDouble("lon"));
                return dto;
            }
        }, query);
    }
}
