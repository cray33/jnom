package my.home.jnom.predata.dao;

import my.home.jnom.predata.entity.RelationEntity;
import my.home.jnom.predata.entity.MemberEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class MemberDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void insertOrUpdateMembers(RelationEntity entity) {
        if (entity.getMembers().isEmpty()) {
            return;
        }

        for (int i = 0; i < entity.getMembers().size(); i++) {
            MemberEntity member = entity.getMembers().get(i);
            if (member.getRef().equals(552884063L)) {
                System.out.println("asdfasf");
            }
            boolean isExist = jdbcTemplate.queryForObject(
                    "SELECT coalesce((SELECT true FROM predata.member WHERE relation_osm_id = ? AND ref = ? LIMIT 1), false)",
                    Boolean.TYPE, entity.getOsmId(), member.getRef());

            if (isExist) {
                jdbcTemplate.update("UPDATE predata.member\n" +
                        " SET type=?, role=?, order_by=?\n" +
                        " WHERE relation_osm_id=? AND ref=?;",
                        member.getType(), member.getRole(), i,
                        entity.getOsmId(), member.getRef());
            } else {
                jdbcTemplate.update("INSERT INTO predata.member(\n" +
                        " relation_osm_id, ref, type, role, order_by)\n" +
                        " VALUES (?, ?, ?, ?, ?);",
                        entity.getOsmId(), member.getRef(), member.getType(),
                        member.getRole(), i);
            }
        }

    }

}
