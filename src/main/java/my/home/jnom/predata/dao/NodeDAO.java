package my.home.jnom.predata.dao;

import my.home.jnom.entity.JnomObjectType;
import my.home.jnom.predata.entity.NodeEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class NodeDAO {
    private static final Logger LOG = LoggerFactory.getLogger(NodeDAO.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void insertOrUpdateNode(NodeEntity entity) {
        boolean isExist = jdbcTemplate.queryForObject(
                "SELECT coalesce((SELECT true FROM predata.node WHERE osm_id = ? LIMIT 1), false)",
                Boolean.TYPE, entity.getOsmId());

        entity.setTags(null);

        //LOG.debug("Node {}, isExist = {}", entity.getOsmId(), isExist);

        if (isExist) {
            jdbcTemplate.update("UPDATE predata.node\n" +
                    " SET timestamp_=?, lat=?, lon=?, tags=?, jnom_type=? \n" +
                    " WHERE osm_id=? ", entity.getTimestamp(), entity.getLat(), entity.getLon(),
                    entity.getTags(), entity.getJnomType(),
                    entity.getOsmId());
        } else {
            //LOG.info(entity.toString());
            jdbcTemplate.update("INSERT INTO predata.node(\n" +
                    " osm_id, timestamp_, lat, lon, tags, jnom_type)\n" +
                    " VALUES (?, ?, ?, ?, ?, ?);",
                    entity.getOsmId(), entity.getTimestamp(), entity.getLat(),
                    entity.getLon(), entity.getTags(), entity.getJnomType());
        }
    }

    public List<Long> getNodesForJnomType(JnomObjectType jnomType) {
        return jdbcTemplate.query("SELECT distinct wxn.node_osm_id FROM predata.relation rltn \n" +
                "INNER JOIN predata.member mmbr ON mmbr.relation_osm_id = rltn.osm_id\n" +
                "INNER JOIN predata.way_x_node wxn ON wxn.way_osm_id = mmbr.ref\n" +
                "WHERE rltn.jnom_type = ? AND mmbr.type = 'way'\n" +
                "UNION\n" +
                "SELECT wxn.node_osm_id FROM predata.way way\n" +
                "INNER JOIN predata.way_x_node wxn ON wxn.way_osm_id = way.osm_id\n" +
                "WHERE way.jnom_type = ? ",
                new RowMapper<Long>() {
                    @Override
                    public Long mapRow(ResultSet rs, int i) throws SQLException {
                        return rs.getLong("node_osm_id");
                    }
                }, jnomType.getValue(), jnomType.getValue());
    }
}
