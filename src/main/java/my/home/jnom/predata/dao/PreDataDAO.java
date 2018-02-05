package my.home.jnom.predata.dao;

import my.home.jnom.entity.Coords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class PreDataDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Map<Long, List<Coords>> getBorderCoords(Long osmId) {
         final Map<Long, List<Coords>> result = new LinkedHashMap<>();
         jdbcTemplate.query("(SELECT wxn.way_osm_id, nd.lat, nd.lon, mmbr.role, nd.osm_id AS node_osm_id  \n" +
                 "FROM predata.member mmbr\n" +
                 "INNER JOIN predata.way_x_node wxn ON wxn.way_osm_id = mmbr.ref\n" +
                 " INNER JOIN predata.node nd ON nd.osm_id = wxn.node_osm_id\n" +
                 " WHERE mmbr.type = 'way' AND mmbr.relation_osm_id = ?\n" +
                 " ORDER BY mmbr.order_by ASC, wxn.order_by asc\n" +
                 " ) UNION ALL (\n" +
                 " SELECT way.osm_id AS way_osm_id, nd.lat, nd.lon, 'outer' as role, nd.osm_id AS node_osm_id\n" +
                 " FROM predata.way way\n" +
                 " INNER JOIN predata.way_x_node wxn ON wxn.way_osm_id = way.osm_id\n" +
                 " INNER JOIN predata.node nd ON nd.osm_id = wxn.node_osm_id\n" +
                 " WHERE way.osm_id = ?\n" +
                 " ORDER BY wxn.order_by asc\n" +
                 " )", new RowCallbackHandler() {
             @Override
             public void processRow(ResultSet rs) throws SQLException {
                 Long wayOsmId = rs.getLong("way_osm_id");
                 List<Coords> coords = result.get(wayOsmId);
                 if (coords == null) {
                     coords = new ArrayList<>();
                     result.put(wayOsmId, coords);
                 }
                 Coords coord = new Coords();
                 coord.setLat(rs.getDouble("lat"));
                 coord.setLon(rs.getDouble("lon"));
                 coord.setRole(rs.getString("role"));
                 coord.setNodeOsmId(rs.getLong("node_osm_id"));
                 coords.add(coord);

             }
         }, osmId, osmId);

        return result;
    }

    public Coords getAdminCentre(Long relationOsmId) {
        List<Coords> result = jdbcTemplate.query("SELECT * FROM predata.member mmbr\n" +
                "INNER JOIN predata.node nd ON nd.osm_id = mmbr.ref\n" +
                "WHERE mmbr.role = 'admin_centre' AND mmbr.relation_osm_id = ?", new RowMapper<Coords>() {
            @Override
            public Coords mapRow(ResultSet rs, int i) throws SQLException {
                Coords coord = new Coords();
                coord.setLat(rs.getDouble("lat"));
                coord.setLon(rs.getDouble("lon"));
                coord.setRole(rs.getString("role"));
                coord.setNodeOsmId(rs.getLong("ref"));
                return coord;
            }
        }, relationOsmId);

        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }

    public List<Coords> getWayCoords(Long wayOsmId) {
        return jdbcTemplate.query("select * from  predata.way_x_node wxn  \n" +
                " inner join predata.node nd ON nd.osm_id = wxn.node_osm_id\n" +
                " WHERE wxn.way_osm_id = ?" +
                " order by wxn.order_by asc",
                new RowMapper<Coords>() {
                    @Override
                    public Coords mapRow(ResultSet rs, int i) throws SQLException {
                        Coords result = new Coords();
                        result.setLat(rs.getDouble("lat"));
                        result.setLon(rs.getDouble("lon"));
                        return result;
                    }
                }, wayOsmId);
    }
}
