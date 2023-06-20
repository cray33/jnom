package my.home.jnom.dao.osm;

import lombok.AllArgsConstructor;
import my.home.jnom.entity.AdmBoundaryEntity;
import my.home.jnom.entity.CityEntity;
import my.home.jnom.entity.HouseEntity;
import org.postgis.PGgeometry;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class OsmPolygonDAO {
    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<AdmBoundaryEntity> findAdmBoundaries(Integer adminLevel) {
        return jdbcTemplate.query("SELECT *, " +
                " ST_X(ST_Transform(ST_Centroid(way), 4326)) AS lon," +
                " ST_Y(ST_Transform (ST_Centroid(way), 4326)) AS lat \n" +
                " FROM osm.planet_osm_polygon " +
                " WHERE boundary = 'administrative' AND admin_level = ?", (rs, i) -> {
                    return AdmBoundaryEntity.builder()
                            .osmId(rs.getLong("osm_id"))
                            .name(rs.getString("name"))
                            .tags((Map<String, String>)(rs.getObject("tags")))
                            .adminLevel(Integer.parseInt(rs.getString("admin_level")))
                            .lat(rs.getDouble("lat"))
                            .lon(rs.getDouble("lon"))
                            .way((PGgeometry) rs.getObject("way"))
                            .build();
                }, String.valueOf(adminLevel));
    }

    public Optional<Long> findParentForAdmBoundary(Long osmId) {
        List<Long> result = jdbcTemplate.query("SELECT parent.osm_id " +
                " FROM osm.planet_osm_polygon child\n" +
                " INNER JOIN osm.planet_osm_polygon parent on ST_Within(child.way, parent.way)\n" +
                " WHERE child.osm_id = ? and parent.admin_level < child.admin_level\n" +
                " ORDER BY parent.admin_level DESC " +
                " LIMIT 1", (rs, rowNum) -> rs.getLong("osm_id"), osmId);

        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    public Optional<Long> findOwnerForObject(Long osmId, List<Long> possibleParents) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("possibleParents", possibleParents)
                .addValue("osmId", osmId);

        List<Long> result = namedParameterJdbcTemplate.query("SELECT adm.osm_id " +
                " FROM osm.planet_osm_polygon obj\n" +
                " INNER JOIN osm.planet_osm_polygon adm ON ST_Within(obj.way, adm.way)\n" +
                " WHERE obj.osm_id = :osmId AND adm.osm_id IN (:possibleParents) \n" +
                " ORDER BY adm.admin_level DESC " +
                " LIMIT 1", parameters, (rs, rowNum) -> rs.getLong("osm_id"));

        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    public List<CityEntity> findCities() {
        return jdbcTemplate.query("SELECT *," +
                " ST_X(ST_Transform(ST_Centroid(way), 4326)) AS lon," +     //TODO we're losing the man-made center of city
                " ST_Y(ST_Transform (ST_Centroid(way), 4326)) AS lat " +
                " FROM osm.planet_osm_polygon" +
                " WHERE place IN ('city', 'town', 'village', 'hamlet')", (rs, rowNum) -> {
            return CityEntity.builder()
                    .osmId(rs.getLong("osm_id"))
                    .name(rs.getString("name"))
                    .tags((Map<String, String>) (rs.getObject("tags")))
                    .population(rs.getInt("population"))
                    .lat(rs.getDouble("lat"))
                    .lon(rs.getDouble("lon"))
                    .wayArea(rs.getFloat("way_area"))
                    .way((PGgeometry) rs.getObject("way"))
                    .build();
        });
    }

    public List<HouseEntity> findHouses() {
        return jdbcTemplate.query("SELECT osm_id, \"addr:housenumber\", tags, " +
                " ST_X(ST_Transform(ST_Centroid(way), 4326)) AS lon," +
                " ST_Y(ST_Transform (ST_Centroid(way), 4326)) AS lat " +
                " FROM osm.planet_osm_polygon" +
                " WHERE building IS NOT null AND \"addr:housenumber\" IS NOT null " +
                " AND  tags -> 'addr:street' IS NOT null ", (rs, rowNum) -> {
            return HouseEntity.builder()
                    .osmId(rs.getLong("osm_id"))
                    .houseNumber(rs.getString("addr:housenumber"))
                    .tags((Map<String, String>)(rs.getObject("tags")))
                    .lat(rs.getDouble("lat"))
                    .lon(rs.getDouble("lon"))
                    .build();
        });
    }
}
