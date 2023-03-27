package my.home.jnom.dao.osm;

import my.home.jnom.entity.StreetEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class OsmLineDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<StreetEntity> findStreets() {
        return jdbcTemplate.query("SELECT osm_id, name, " +
                " ST_X(ST_Transform(ST_Centroid(way), 4326)) AS lon," +
                " ST_Y(ST_Transform (ST_Centroid(way), 4326)) AS lat, \n" +
                " ST_Length(way) as way_length \n" +
                " FROM osm.planet_osm_line " +
                " WHERE highway IS NOT null AND name IS NOT null", (rs, i) -> {
            StreetEntity entity = new StreetEntity();
            entity.setOsmId(rs.getLong("osm_id"));
            entity.setName(rs.getString("name"));
            entity.setLat(rs.getDouble("lat"));
            entity.setLon(rs.getDouble("lon"));
            entity.setWayLength(rs.getFloat("way_length"));
            //entity.setWay((PGgeometry) rs.getObject("way"));
            return entity;
        });
    }

    public Optional<Long> findCityForStreet(Long osmId, List<Long> cities) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("cities", cities)
                .addValue("osmId", osmId);

        List<Long> result = namedParameterJdbcTemplate.query("SELECT city.osm_id " +
                " FROM osm.planet_osm_line street\n" +
                " INNER JOIN osm.planet_osm_polygon city on ST_Within(street.way, city.way) or ST_Crosses(street.way, city.way)\n" +
                " WHERE street.osm_id = :osmId AND city.osm_id IN (:cities) \n" +
                " LIMIT 1", parameters, (rs, rowNum) -> rs.getLong("osm_id"));

        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    public Optional<Long> findAdmBoundaryForStreet(Long osmId, List<Long> admBoundaries) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("admBoundaries", admBoundaries)
                .addValue("osmId", osmId);

        List<Long> result = namedParameterJdbcTemplate.query("SELECT adm.osm_id " +
                " FROM osm.planet_osm_line street\n" +
                " INNER JOIN osm.planet_osm_polygon adm on ST_Within(street.way, adm.way) or ST_Crosses(street.way, adm.way)\n" +
                " WHERE street.osm_id = :osmId AND adm.osm_id IN (:admBoundaries) \n" +
                " ORDER BY adm.admin_level DESC " +
                " LIMIT 1", parameters, (rs, rowNum) -> rs.getLong("osm_id"));

        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    public List<Long> findAllLinesInTheStreet(Long lineOsmId, String streetName) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("lineOsmId", lineOsmId)
                .addValue("streetName", streetName);

        return namedParameterJdbcTemplate.query("WITH RECURSIVE lines AS ( " +
                " SELECT line1.osm_id, line1.way FROM osm.planet_osm_line line1 WHERE line1.osm_id = :lineOsmId " +
                " UNION " +
                " SELECT line2.osm_id, line2.way from osm.planet_osm_line line2 " +
                " INNER JOIN lines line3 ON  ST_DWithin(line2.way, line3.way, 2000) " +
                " WHERE line2.\"name\" = :streetName " +
                " ) SELECT * FROM lines;",
                parameters, (rs, rowNum) -> rs.getLong("osm_id"));
    }
}
