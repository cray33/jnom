package my.home.jnom.dao.jnom;

import lombok.AllArgsConstructor;
import my.home.jnom.entity.AdmBoundaryEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AllArgsConstructor
public class AdmBoundaryDAO {
    private JdbcTemplate jdbcTemplate;

    public List<Long> findAllOsmIds(Integer maxAdminLevel) {
        return jdbcTemplate.query("SELECT osm_id " +
                " FROM jnom.administrative_boundary " +
                " WHERE admin_level <= ?", (rs, rowNum) -> rs.getLong("osm_id"), maxAdminLevel);
    }

    public void insertOfUpdate(AdmBoundaryEntity entity) {
        boolean isExist = jdbcTemplate.queryForObject(
                "SELECT coalesce((SELECT true FROM jnom.administrative_boundary WHERE osm_id = ? LIMIT 1), false)",
                Boolean.TYPE, entity.getOsmId());

        if (isExist) {
            jdbcTemplate.update("UPDATE jnom.administrative_boundary\n" +
                    "   SET name=?, tags=?, admin_level=?, parent_osm_id=?, lat=?, lon=?, way=? \n" +
                    " WHERE osm_id=?\n",
                    entity.getName(),
                    entity.getTags(),
                    entity.getAdminLevel(), entity.getParentOsmId(),
                    entity.getLat(), entity.getLon(), entity.getWay(),
                    entity.getOsmId());
        } else {
            jdbcTemplate.update("INSERT INTO jnom.administrative_boundary(\n" +
                    "            osm_id, name, tags, admin_level, parent_osm_id, lat, lon, way)\n" +
                    "    VALUES (?, ?, ?,  ?, ?, ?, ?, ?);\n",
                    entity.getOsmId(), entity.getName(), entity.getTags(),
                    entity.getAdminLevel(), entity.getParentOsmId(),
                    entity.getLat(), entity.getLon(), entity.getWay());
        }
    }

}
