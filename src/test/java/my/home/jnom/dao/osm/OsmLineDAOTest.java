package my.home.jnom.dao.osm;

import my.home.jnom.DatabaseTest;
import my.home.jnom.entity.StreetEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.offset;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
public class OsmLineDAOTest extends DatabaseTest {

    @Autowired
    private OsmLineDAO osmLineDAO;

    @Autowired
    private JdbcTemplate jdbcTemplate;

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

    @Test
    public void findAllLinesInTheStreetSuccess() {
        jdbcTemplate.update("INSERT INTO osm.planet_osm_line(osm_id, name, way) VALUES (?, ?," +
                "ST_GeomFromEWKT('SRID=3857;LINESTRING (5924231.162576911 7729374.615983587, 5924213.574097365 7729371.582829598," +
                " 5924061.277902011 7729345.302331217, 5923962.236951052 7729328.202752757, 5923792.485859541 7729298.909635522, " +
                "5923784.036710191 7729297.443964786, 5923774.107011612 7729295.734015956, 5923675.533602514 7729278.736331187," +
                " 5923642.950387559 7729273.097586653)'))",
                154198077L, "Красногеройская улица");
        jdbcTemplate.update("INSERT INTO osm.planet_osm_line(osm_id, name, way) VALUES (?, ?, " +
                "ST_GeomFromEWKT('SRID=3857;LINESTRING (5924966.327626058 7729522.244508434, 5924942.894873246 7729517.725225432," +
                " 5924849.954230383 7729498.141696715, 5924741.606969994 7729475.301095859, 5924729.2059787195 7729471.779333729," +
                " 5924715.090667287 7729469.01078198, 5924249.719536026 7729378.483765586, 5924231.162576911 7729374.615983587)'))",
                 106395366L, "Красногеройская улица");
        jdbcTemplate.update("INSERT INTO osm.planet_osm_line(osm_id, name, way) VALUES (?, ?, " +
                        "ST_GeomFromEWKT('SRID=3857;LINESTRING (5925407.620351461 7729690.070591922, " +
                        "5925665.224785106 7729733.41201404, 5925704.331322222 7729739.9875543, 5925748.95930608 7729734.246679827)'))",
                345244017L, "Красногеройская улица");
        jdbcTemplate.update("INSERT INTO osm.planet_osm_line(osm_id, name, way) VALUES (?, ?, " +
                        "ST_GeomFromEWKT('SRID=3857;LINESTRING (5924970.580030606 7729609.943558093, 5925014.250666845 7729618.188297831, " +
                        "5925019.660794097 7729619.226524956, 5925051.1753419405 7729625.170886857, 5925160.068067835 7729645.731900462, " +
                        "5925203.772099921 7729653.997036414, 5925312.586902171 7729674.558127818, 5925344.613519671 7729679.851091021, " +
                        "5925407.620351461 7729690.070591922)'))",
                104295849L, "Университетская улица");

        List<Long> consistOf = osmLineDAO.findAllLinesInTheStreet(154198077L, "Красногеройская улица");

        assertThat(consistOf)
                .containsExactlyInAnyOrder(154198077L, 106395366L, 345244017L)
                .doesNotContain(104295849L);
    }

    private void insertToOsmLine(Long osmId, String highway, String name) {
        jdbcTemplate.update("INSERT INTO osm.planet_osm_line(osm_id, highway, name, way) " +
                        "VALUES (?,?,?,ST_GeomFromEWKT('SRID=3857;LINESTRING (5912249.612011899 7729023.877389588, " +
                        "5912200.286345529 7728850.447905041, 5912195.098857258 7728832.738778429)')) ",
                osmId, highway, name);
    }
}
