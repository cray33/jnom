package my.home.jnom.dao.osm;

import my.home.jnom.entity.CityEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Repository
public class OsmPointDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<CityEntity> findCities() {
        return jdbcTemplate.query("SELECT *," +
                " ST_X(ST_Transform(way, 4326)) as lon," +
                " ST_Y(ST_Transform(way, 4326)) as lat " +
                " FROM osm.planet_osm_point" +
                " WHERE place IN ('city', 'town', 'village', 'hamlet')", new RowMapper<CityEntity>() {
            @Override
            public CityEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
                CityEntity entity = new CityEntity();
                entity.setOsmId(rs.getLong("osm_id"));
                entity.setName(rs.getString("name"));
                entity.setTags((Map<String, String>)(rs.getObject("tags")));
                entity.setPopulation(rs.getInt("population"));
                entity.setLat(rs.getDouble("lat"));
                entity.setLon(rs.getDouble("lon"));
                return entity;
            }
        });
    }

}
