package my.home.jnom.dao.osm;

import my.home.jnom.dao.DatabaseTest;
import my.home.jnom.entity.StreetEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.offset;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class OsmLineDAOTest extends DatabaseTest {

    @Autowired
    private OsmLineDAO osmLineDAO;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    public void clear() {
        jdbcTemplate.update("DELETE FROM osm.planet_osm_line");
        jdbcTemplate.update("DELETE FROM osm.planet_osm_polygon");
    }

    @Test
    public void testFindStreetsSuccess() {
        insertToOsmLine(534255162L, "secondary", "Pushkinskaya");

        List<StreetEntity> list = osmLineDAO.findStreets();

        assertThat(list).hasSize(1);
        StreetEntity entity = list.get(0);
        assertThat(entity.getOsmId()).isEqualTo(534255162L);
        assertThat(entity.getName()).isEqualTo("Pushkinskaya");
        assertThat(entity.getLat()).isEqualTo(56.84708551350134d, offset(0.00000000000001));
        assertThat(entity.getLon()).isEqualTo(53.110397617734954d, offset(0.00000000000001));
        assertThat(entity.getWayLength()).isEqualTo(198.7608f, offset(0.0001f));
    }

    @Test
    public void testFindStreetsHasntFound() {
        insertToOsmLine(2L, null, "Pushkinskaya");
        insertToOsmLine(3L, "secondary", null);

        List<StreetEntity> list = osmLineDAO.findStreets();

        assertThat(list).isEmpty();
    }

    @Test
    public void findCityForStreetWithinSuccess() {
        jdbcTemplate.update("INSERT INTO osm.planet_osm_polygon(osm_id, way) VALUES (?, " +
                "ST_GeomFromEWKT('SRID=3857;POLYGON ((6701949.634421419 6638678.948553811, 6702086.98040916 6637906.20351944," +
                " 6702674.168459196 6638139.038579477, 6702472.335090438 6638672.903861024, 6702115.64517804 6638806.739011618," +
                " 6701949.634421419 6638678.948553811))'))", 262011114L);
        jdbcTemplate.update("INSERT INTO osm.planet_osm_polygon(osm_id, way) VALUES (?, " +
                "ST_GeomFromEWKT('SRID=3857;POLYGON ((6664504.240143207 6604108.541899143, 6664844.120812496 6603618.0994060645, " +
                "6667355.577580385 6604143.527974178, 6667322.760594499 6604532.268239848, 6665879.759431193 6604610.699903295," +
                " 6664504.240143207 6604108.541899143))'))", 384188629L);
        jdbcTemplate.update("INSERT INTO osm.planet_osm_line(osm_id, way) VALUES (?, " +
                "ST_GeomFromEWKT('SRID=3857;LINESTRING (6666035.918412878 6604091.348998586, 6666862.220729139 6604245.345007658, " +
                "6666951.064814741 6604260.75535859)'))", 730134350L);

        Optional<Long> cityId = osmLineDAO.findCityForStreet(730134350L, Arrays.asList(262011114L, 384188629L));

        assertThat(cityId).isPresent().hasValue(384188629L);
    }

    @Test
    public void findCityForStreetNoInList() {
        jdbcTemplate.update("INSERT INTO osm.planet_osm_polygon(osm_id, way) VALUES (?, " +
                "ST_GeomFromEWKT('SRID=3857;POLYGON ((6701949.634421419 6638678.948553811, 6702086.98040916 6637906.20351944," +
                " 6702674.168459196 6638139.038579477, 6702472.335090438 6638672.903861024, 6702115.64517804 6638806.739011618," +
                " 6701949.634421419 6638678.948553811))'))", 262011114L);
        jdbcTemplate.update("INSERT INTO osm.planet_osm_polygon(osm_id, way) VALUES (?, " +
                "ST_GeomFromEWKT('SRID=3857;POLYGON ((6664504.240143207 6604108.541899143, 6664844.120812496 6603618.0994060645, " +
                "6667355.577580385 6604143.527974178, 6667322.760594499 6604532.268239848, 6665879.759431193 6604610.699903295," +
                " 6664504.240143207 6604108.541899143))'))", 384188629L);
        jdbcTemplate.update("INSERT INTO osm.planet_osm_line(osm_id, way) VALUES (?, " +
                "ST_GeomFromEWKT('SRID=3857;LINESTRING (6666035.918412878 6604091.348998586, 6666862.220729139 6604245.345007658, " +
                "6666951.064814741 6604260.75535859)'))", 730134350L);

        Optional<Long> cityId = osmLineDAO.findCityForStreet(730134350L, Arrays.asList(262011114L));

        assertThat(cityId).isNotPresent();
    }

    @Test
    public void findCityForStreetCrossesSuccess() {
        jdbcTemplate.update("INSERT INTO osm.planet_osm_polygon(osm_id, way) VALUES (?, " +
                "ST_GeomFromEWKT('SRID=3857;POLYGON ((6701949.634421419 6638678.948553811, 6702086.98040916 6637906.20351944," +
                " 6702674.168459196 6638139.038579477, 6702472.335090438 6638672.903861024, 6702115.64517804 6638806.739011618," +
                " 6701949.634421419 6638678.948553811))'))", 262011114L);
        jdbcTemplate.update("INSERT INTO osm.planet_osm_polygon(osm_id, way) VALUES (?, " +
                "ST_GeomFromEWKT('SRID=3857;POLYGON ((6664504.240143207 6604108.541899143, 6664844.120812496 6603618.0994060645, " +
                "6667355.577580385 6604143.527974178, 6667322.760594499 6604532.268239848, 6665879.759431193 6604610.699903295," +
                " 6664504.240143207 6604108.541899143))'))", 384188629L);
        jdbcTemplate.update("INSERT INTO osm.planet_osm_line(osm_id, way) VALUES (?, " +
                "ST_GeomFromEWKT('SRID=3857;LINESTRING (6665195.467389339 6604554.245976871, 6665222.362178314 6604510.608310744, " +
                "6665697.529424766 6604205.751306088, 6665732.339029536 6604169.070374276, " +
                "6665771.701601481 6604042.100540004)'))", 262584189L);

        Optional<Long> cityId = osmLineDAO.findCityForStreet(262584189L, Arrays.asList(262011114L, 384188629L));

        assertThat(cityId).isPresent().hasValue(384188629L);
    }

    @Test
    public void findAdmBoundaryForStreetWithinSuccess() {
        jdbcTemplate.update("INSERT INTO osm.planet_osm_polygon(osm_id, admin_level, way) VALUES (?, ?," +
                "ST_GeomFromEWKT('SRID=3857;POLYGON ((6701949.634421419 6638678.948553811, 6702086.98040916 6637906.20351944," +
                " 6702674.168459196 6638139.038579477, 6702472.335090438 6638672.903861024, 6702115.64517804 6638806.739011618," +
                " 6701949.634421419 6638678.948553811))'))", 262011114L, 5);
        jdbcTemplate.update("INSERT INTO osm.planet_osm_polygon(osm_id, admin_level, way) VALUES (?, ?," +
                "ST_GeomFromEWKT('SRID=3857;POLYGON ((6664504.240143207 6604108.541899143, 6664844.120812496 6603618.0994060645, " +
                "6667355.577580385 6604143.527974178, 6667322.760594499 6604532.268239848, 6665879.759431193 6604610.699903295," +
                " 6664504.240143207 6604108.541899143))'))", 384188629L, 5);
        jdbcTemplate.update("INSERT INTO osm.planet_osm_line(osm_id, way) VALUES (?, " +
                "ST_GeomFromEWKT('SRID=3857;LINESTRING (6666035.918412878 6604091.348998586, 6666862.220729139 6604245.345007658, " +
                "6666951.064814741 6604260.75535859)'))", 730134350L);

        Optional<Long> admBoundaryId = osmLineDAO.findAdmBoundaryForStreet(730134350L, Arrays.asList(262011114L, 384188629L));

        assertThat(admBoundaryId).isPresent().hasValue(384188629L);
    }

    @Test
    public void findAdmBoundaryForStreetNoInList() {
        jdbcTemplate.update("INSERT INTO osm.planet_osm_polygon(osm_id, admin_level, way) VALUES (?, ?," +
                "ST_GeomFromEWKT('SRID=3857;POLYGON ((6701949.634421419 6638678.948553811, 6702086.98040916 6637906.20351944," +
                " 6702674.168459196 6638139.038579477, 6702472.335090438 6638672.903861024, 6702115.64517804 6638806.739011618," +
                " 6701949.634421419 6638678.948553811))'))", 262011114L, 5);
        jdbcTemplate.update("INSERT INTO osm.planet_osm_polygon(osm_id, admin_level, way) VALUES (?, ?," +
                "ST_GeomFromEWKT('SRID=3857;POLYGON ((6664504.240143207 6604108.541899143, 6664844.120812496 6603618.0994060645, " +
                "6667355.577580385 6604143.527974178, 6667322.760594499 6604532.268239848, 6665879.759431193 6604610.699903295," +
                " 6664504.240143207 6604108.541899143))'))", 384188629L, 5);
        jdbcTemplate.update("INSERT INTO osm.planet_osm_line(osm_id, way) VALUES (?, " +
                "ST_GeomFromEWKT('SRID=3857;LINESTRING (6666035.918412878 6604091.348998586, 6666862.220729139 6604245.345007658, " +
                "6666951.064814741 6604260.75535859)'))", 730134350L);

        Optional<Long> admBoundaryId = osmLineDAO.findCityForStreet(730134350L, Arrays.asList(262011114L));

        assertThat(admBoundaryId).isNotPresent();
    }

    @Test
    public void findAdmBoundaryForStreetCrossesSuccess() {
        jdbcTemplate.update("INSERT INTO osm.planet_osm_polygon(osm_id, admin_level, way) VALUES (?, ?," +
                "ST_GeomFromEWKT('SRID=3857;POLYGON ((6701949.634421419 6638678.948553811, 6702086.98040916 6637906.20351944," +
                " 6702674.168459196 6638139.038579477, 6702472.335090438 6638672.903861024, 6702115.64517804 6638806.739011618," +
                " 6701949.634421419 6638678.948553811))'))", 262011114L, 5);
        jdbcTemplate.update("INSERT INTO osm.planet_osm_polygon(osm_id, admin_level, way) VALUES (?, ?," +
                "ST_GeomFromEWKT('SRID=3857;POLYGON ((6664504.240143207 6604108.541899143, 6664844.120812496 6603618.0994060645, " +
                "6667355.577580385 6604143.527974178, 6667322.760594499 6604532.268239848, 6665879.759431193 6604610.699903295," +
                " 6664504.240143207 6604108.541899143))'))", 384188629L, 5);
        jdbcTemplate.update("INSERT INTO osm.planet_osm_line(osm_id, way) VALUES (?, " +
                "ST_GeomFromEWKT('SRID=3857;LINESTRING (6665195.467389339 6604554.245976871, 6665222.362178314 6604510.608310744, " +
                "6665697.529424766 6604205.751306088, 6665732.339029536 6604169.070374276, " +
                "6665771.701601481 6604042.100540004)'))", 262584189L);

        Optional<Long> admBoundaryId = osmLineDAO.findAdmBoundaryForStreet(262584189L, Arrays.asList(262011114L, 384188629L));

        assertThat(admBoundaryId).isPresent().hasValue(384188629L);
    }

    @Test
    public void findAdmBoundaryForStreetOrderByAdminLevelSuccess() {
        jdbcTemplate.update("INSERT INTO osm.planet_osm_polygon(osm_id, admin_level, way) VALUES (?, ?," +
                "ST_GeomFromEWKT('SRID=3857;POLYGON ((6664504.240143207 6604108.541899143, 6664844.120812496 6603618.0994060645, " +
                "6667355.577580385 6604143.527974178, 6667322.760594499 6604532.268239848, 6665879.759431193 6604610.699903295," +
                " 6664504.240143207 6604108.541899143))'))", 384188629L, 5);
        jdbcTemplate.update("INSERT INTO osm.planet_osm_polygon(osm_id, admin_level, way) VALUES (?, ?," +
                "ST_GeomFromEWKT('SRID=3857;POLYGON ((6664504.240143207 6604108.541899143, 6664844.120812496 6603618.0994060645, " +
                "6667355.577580385 6604143.527974178, 6667322.760594499 6604532.268239848, 6665879.759431193 6604610.699903295," +
                " 6664504.240143207 6604108.541899143))'))", 3841886290L, 6);
        jdbcTemplate.update("INSERT INTO osm.planet_osm_line(osm_id, way) VALUES (?, " +
                "ST_GeomFromEWKT('SRID=3857;LINESTRING (6665195.467389339 6604554.245976871, 6665222.362178314 6604510.608310744, " +
                "6665697.529424766 6604205.751306088, 6665732.339029536 6604169.070374276, " +
                "6665771.701601481 6604042.100540004)'))", 262584189L);

        Optional<Long> admBoundaryId = osmLineDAO.findAdmBoundaryForStreet(262584189L, Arrays.asList(384188629L, 3841886290L));

        assertThat(admBoundaryId).isPresent().hasValue(3841886290L);
    }
    /*
     List<Long> result = namedParameterJdbcTemplate.query("SELECT adm.osm_id " +
                " FROM planet_osm_line street\n" +
                " INNER JOIN planet_osm_polygon adm on ST_Within(street.way, adm.way) or ST_Crosses(street.way, street.way)\n" +
                " WHERE street.osm_id = :osmId AND adm.osm_id IN (:admBoundaries) \n" +
                " ORDER BY adm.admin_level DESC " +
                " LIMIT 1", parameters, (rs, rowNum) -> rs.getLong("osm_id"));
     */

    private void insertToOsmLine(Long osmId, String highway, String name) {
        jdbcTemplate.update("INSERT INTO osm.planet_osm_line(osm_id, highway, name, way) " +
                        "VALUES (?,?,?,ST_GeomFromEWKT('SRID=3857;LINESTRING (5912249.612011899 7729023.877389588, " +
                        "5912200.286345529 7728850.447905041, 5912195.098857258 7728832.738778429)')) ",
                osmId, highway, name);
    }
}
