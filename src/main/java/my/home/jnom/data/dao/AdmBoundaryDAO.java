package my.home.jnom.data.dao;

import my.home.jnom.data.entity.AdmBoundaryEntity;
import my.home.jnom.data.entity.CityOrAdmBoundaryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class AdmBoundaryDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void insertOfUpdate(AdmBoundaryEntity entity) {
        boolean isExist = jdbcTemplate.queryForObject(
                "SELECT coalesce((SELECT true FROM data.administrative_boundary WHERE osm_id = ? LIMIT 1), false)",
                Boolean.TYPE, entity.getOsmId());

        if (isExist) {
            jdbcTemplate.update("UPDATE data.administrative_boundary\n" +
                    "   SET name=?, tags=?,  admin_level=?, parent_osm_id=?, lat=?, lon=? \n" +
                    " WHERE osm_id=?\n",
                    entity.getName(),
                    entity.getTags(),
                    entity.getAdminLevel(), entity.getParentOsmId(),
                    entity.getBorder().getAdminCentre().getLat(), entity.getBorder().getAdminCentre().getLon(),
                    entity.getOsmId());
        } else {
            jdbcTemplate.update("INSERT INTO data.administrative_boundary(\n" +
                    "            osm_id, name, tags, admin_level, parent_osm_id, lat, lon)\n" +
                    "    VALUES (?, ?, ?,  ?, ?, ?, ?);\n",
                    entity.getOsmId(), entity.getName(), entity.getTags(),
                    entity.getAdminLevel(), entity.getParentOsmId(),
                    entity.getBorder().getAdminCentre().getLat(), entity.getBorder().getAdminCentre().getLon());
        }

    }

    public List<CityOrAdmBoundaryEntity> findAdmBoundaries(String query) {
        return jdbcTemplate.query("SELECT osm_id, data.get_name_with_parents(osm_id) as full_name," +
                " lat, lon \n" +
                " FROM data.administrative_boundary WHERE name ILIKE '%' || ? || '%'", new RowMapper<CityOrAdmBoundaryEntity>() {
            @Override
            public CityOrAdmBoundaryEntity mapRow(ResultSet rs, int i) throws SQLException {
                CityOrAdmBoundaryEntity dto = new CityOrAdmBoundaryEntity();
                dto.setAdmBoundaryOsmId(rs.getLong("osm_id"));
                dto.setName(rs.getString("full_name"));
                dto.setLat(rs.getDouble("lat"));
                dto.setLon(rs.getDouble("lon"));
                return dto;
            }
        }, query);
    }


}
