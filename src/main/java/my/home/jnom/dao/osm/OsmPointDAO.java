package my.home.jnom.dao.osm;

import lombok.AllArgsConstructor;
import my.home.jnom.entity.CityEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@AllArgsConstructor
public class OsmPointDAO {
    private JdbcTemplate jdbcTemplate;

    public List<CityEntity> findCities() {
        return jdbcTemplate.query("SELECT *," +
                " ST_X(ST_Transform(way, 4326)) as lon," +
                " ST_Y(ST_Transform(way, 4326)) as lat " +
                " FROM osm.planet_osm_point" +
                " WHERE place IN ('city', 'town', 'village', 'hamlet')", (rs, rowNum) -> {
            return CityEntity.builder()
                    .osmId(rs.getLong("osm_id"))
                    .name(rs.getString("name"))
                    .tags((Map<String, String>) (rs.getObject("tags")))
                    .population(rs.getInt("population"))
                    .lat(rs.getDouble("lat"))
                    .lon(rs.getDouble("lon"))
                    .build();
                });
    }

}
