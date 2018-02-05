package my.home.jnom.predata.dao;

import my.home.jnom.entity.JnomObjectType;
import my.home.jnom.predata.entity.RelationEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Repository
public class RelationDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void insertOfUpdate(RelationEntity entity) throws SQLException {
        boolean isExist = jdbcTemplate.queryForObject(
                "SELECT coalesce((SELECT true FROM predata.relation WHERE osm_id = ? LIMIT 1), false)",
                Boolean.TYPE, entity.getOsmId());

        if (isExist) {
            jdbcTemplate.update("UPDATE predata.relation \n" +
                    " SET timestamp_=?, tags = ?::hstore\n, admin_level = ?, jnom_type = ?" +
                    " WHERE osm_id = ?; ",
                    entity.getTimestamp(),
                   entity.getTags(), entity.getAdminLevel(), entity.getJnomType(),
                    entity.getOsmId());
        } else {
            jdbcTemplate.update("INSERT INTO predata.relation(\n" +
                    "  osm_id, timestamp_, tags, admin_level, jnom_type)\n" +
                    " VALUES (?, ?, ?::hstore, ?, ?);",
                    entity.getOsmId(), entity.getTimestamp(),
                    entity.getTags(),
                    entity.getAdminLevel(), entity.getJnomType());
        }
    }

    public List<RelationEntity> getRelationsByLevel(Integer adminLevel) {
        return jdbcTemplate.query("SELECT * FROM predata.relation" +
                        " WHERE admin_level = ? ORDER BY osm_id ASC",
                new RowMapper<RelationEntity>() {
                    @Override
                    public RelationEntity mapRow(ResultSet rs, int i) throws SQLException {
                        RelationEntity entity = new RelationEntity();
                        entity.setOsmId(rs.getLong("osm_id"));
                        entity.setTimestamp(rs.getTimestamp("timestamp_"));
                        entity.setAdminLevel(rs.getInt("admin_level"));
                        entity.setTags((Map<String, String>)(rs.getObject("tags")));
                        return entity;
                    }
                }, adminLevel);
    }

    public List<RelationEntity> getRelationsByType(JnomObjectType jnomType) {
        return jdbcTemplate.query("SELECT * FROM predata.relation" +
                " WHERE jnom_type = ? ORDER BY osm_id ASC",
                new RowMapper<RelationEntity>() {
                    @Override
                    public RelationEntity mapRow(ResultSet rs, int i) throws SQLException {
                        RelationEntity entity = new RelationEntity();
                        entity.setOsmId(rs.getLong("osm_id"));
                        entity.setTimestamp(rs.getTimestamp("timestamp_"));
                        entity.setAdminLevel(rs.getInt("admin_level"));
                        entity.setTags((Map<String,String>)rs.getObject("tags"));
                        //entity.setTags(Utils.hstoreStringToMap(rs.getString("tags")));
                        return entity;
                    }
                }, jnomType.getValue());
    }

    public List<Long> getWaysForType(JnomObjectType jnomType) {
        return jdbcTemplate.query("SELECT distinct mmbr.ref AS way_osm_id FROM predata.relation rltn \n" +
                "INNER JOIN predata.member mmbr ON mmbr.relation_osm_id = rltn.osm_id\n" +
                "WHERE rltn.jnom_type = ? AND mmbr.type = 'way'  ",
                new RowMapper<Long>() {
                    @Override
                    public Long mapRow(ResultSet resultSet, int i) throws SQLException {
                        return resultSet.getLong("way_osm_id");
                    }
                }, jnomType.getValue());
    }
}
