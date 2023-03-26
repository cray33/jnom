package my.home.jnom.dao.jnom;

import my.home.jnom.entity.CityEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class CityDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Long> findAllOsmIds() {
        return jdbcTemplate.query("SELECT osm_id " +
                " FROM jnom.city ", (rs, rowNum) -> rs.getLong("osm_id"));
    }

    public void insertOfUpdate(CityEntity entity) {
        boolean isExist = jdbcTemplate.queryForObject(
                "SELECT coalesce((SELECT true FROM jnom.city WHERE osm_id = ? LIMIT 1), false)",
                Boolean.TYPE, entity.getOsmId());

        if (isExist) {
            jdbcTemplate.update("UPDATE jnom.city\n" +
                    "   SET name=?, tags=?, population=?, adm_boundary_osm_id=?, lat=?, lon=?, way_area=?, way=? \n" +
                    " WHERE osm_id=? AND way_area < ?",
                    entity.getName(), entity.getTags(),
                    entity.getPopulation(), entity.getAdmBoundaryOsmId(),
                    entity.getLat(), entity.getLon(), entity.getWayArea(), entity.getWay(),
                    entity.getOsmId(), entity.getWayArea());
        } else {
            jdbcTemplate.update("INSERT INTO jnom.city(\n" +
                    "            osm_id, name, tags, population, adm_boundary_osm_id, lat, lon, way_area, way)\n" +
                    "    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?); ",
                    entity.getOsmId(), entity.getName(), entity.getTags(),
                    entity.getPopulation(), entity.getAdmBoundaryOsmId(),
                    entity.getLat(), entity.getLon(), entity.getWayArea(), entity.getWay());
        }

    }

    public List<CityEntity> findCities(String query) {
        return jdbcTemplate.query("SELECT osm_id, adm_boundary_osm_id, " +
                " name, lat, lon \n" +
                " FROM jnom.city WHERE name ILIKE '%' || ? || '%'", (rs, i) -> {
                    CityEntity entity = new CityEntity();
                    entity.setOsmId(rs.getLong("osm_id"));
                    entity.setAdmBoundaryOsmId(rs.getLong("adm_boundary_osm_id"));
                    entity.setName(rs.getString("name"));
                    entity.setLat(rs.getDouble("lat"));
                    entity.setLon(rs.getDouble("lon"));
                    return entity;
                }, query);
    }
}
