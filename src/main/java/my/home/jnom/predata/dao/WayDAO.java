package my.home.jnom.predata.dao;

import my.home.jnom.entity.JnomObjectType;
import my.home.jnom.predata.entity.WayEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Repository
public class WayDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void insertOrUpdateWay(WayEntity entity) {
        boolean wayIsExist = jdbcTemplate.queryForObject(
                "SELECT coalesce((SELECT true FROM predata.way WHERE osm_id = ? LIMIT 1), false)",
                Boolean.TYPE, entity.getOsmId());

        if (wayIsExist) {
            jdbcTemplate.update("UPDATE predata.way\n" +
                    " SET tags=?::hstore, timestamp_=?, jnom_type=?\n" +
                    " WHERE osm_id=?",
                   entity.getTags(), entity.getTimestamp(),
                    entity.getJnomType(),
                    entity.getOsmId());
        } else {
            jdbcTemplate.update("INSERT INTO predata.way(\n" +
                    " osm_id, tags, timestamp_, jnom_type)\n" +
                    " VALUES (?, ?::hstore, ?, ?);",
                    entity.getOsmId(), entity.getTags(),
                    entity.getTimestamp(), entity.getJnomType());
        }

        jdbcTemplate.update("DELETE FROM  predata.way_x_node WHERE way_osm_id = ?", entity.getOsmId());
        for (int i = 0; i < entity.getNodes().size(); i++) {
            Long nodeOsmId = entity.getNodes().get(i);
            /*boolean isExist = jdbcTemplate.queryForObject(
                    "SELECT coalesce((SELECT true FROM predata.way_x_node WHERE way_osm_id = ? AND node_osm_id = ? LIMIT 1), false)",
                    Boolean.TYPE, entity.getOsmId(), nodeOsmId);
            if (isExist) {
                jdbcTemplate.update("UPDATE predata.way_x_node\n" +
                        " SET order_by=?\n" +
                        " WHERE way_osm_id=? AND node_osm_id=?",
                        i, entity.getOsmId(), nodeOsmId);
            } else {
            */

                jdbcTemplate.update("INSERT INTO predata.way_x_node(\n" +
                        " way_osm_id, node_osm_id, order_by)\n" +
                        " VALUES (?, ?, ?);",
                        entity.getOsmId(), nodeOsmId, i);
           // }
        }
    }



    public List<WayEntity> getWayByType(JnomObjectType jnomType) {
        return jdbcTemplate.query("SELECT * FROM predata.way" +
                " WHERE jnom_type = ? ORDER BY osm_id ASC",
                new RowMapper<WayEntity>() {
                    @Override
                    public WayEntity mapRow(ResultSet rs, int i) throws SQLException {
                        WayEntity entity = new WayEntity();
                        entity.setOsmId(rs.getLong("osm_id"));
                        entity.setTimestamp(rs.getTimestamp("timestamp_"));
                        entity.setTags((Map<String, String>)(rs.getObject("tags")));
                        return entity;
                    }
                }, jnomType.getValue());
    }
}
