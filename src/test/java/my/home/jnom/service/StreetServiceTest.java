package my.home.jnom.service;

import my.home.jnom.DatabaseTest;
import my.home.jnom.TestDatabaseConfiguration;
import my.home.jnom.dao.jnom.StreetDAO;
import my.home.jnom.dao.osm.OsmLineDAO;
import my.home.jnom.entity.StreetEntity;
import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class StreetServiceTest extends DatabaseTest {
    @Autowired
    private OsmLineDAO osmLineDAO;

    @Autowired
    private AdmBoundaryService admBoundaryService;

    @Autowired
    private CityService cityService;

    @Autowired
    private StreetService streetService;

    @Autowired
    private StreetDAO streetDAO;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void importStreetsSuccess() {
        insertAdmBoundaryAndCity();
        insertLinesAsStreets();

        streetService.importStreets();
        List<StreetEntity> streets = streetDAO.findAll();

        assertThat(streets).hasSize(2);
        assertThat(streets).extracting(StreetEntity::getName).containsOnly("Родниковая улица", "Новая родниковая улица");
        assertThat(streets.get(0).getAdmBoundaryOsmId()).isEqualTo(93904116L);
        assertThat(streets.get(0).getCityOsmId()).isEqualTo(93904116L);
        assertThat(streets)
                .filteredOn(x -> x.getName().equals("Родниковая улица"))
                .extracting(StreetEntity::getConsistOfOsmIds).containsOnly(Arrays.asList(734137195L, 147224358L));

    }

    private void insertAdmBoundaryAndCity() {
        jdbcTemplate.update("INSERT INTO osm.planet_osm_polygon(osm_id, admin_level, boundary, place, name, way) VALUES (?, ?, ?, ?, ?," +
                "ST_GeomFromEWKT('SRID=3857;POLYGON ((6098634.128628587 7874323.025982085, 6098803.222935103 7874080.40764188, " +
                "6099115.674481861 7873517.993795535, 6099082.590329197 7873495.939610132, 6099113.837710263 7873426.105238711, " +
                "6099064.211481268 7872847.143812505, 6099435.473115012 7872828.763493777, 6099764.466738102 7873343.387820699," +
                " 6099499.804648741 7873865.371265444, 6099464.883724479 7873854.354031917, 6099091.785319137 7874523.363681565, " +
                "6099191.026645179 7874613.418815649, 6099141.411548132 7874666.7261506, 6099205.731949913 7874738.398029662, " +
                "6099113.837710263 7874817.437088531, 6098734.883899705 7874500.393569117, 6098775.649097233 7874429.616181939, " +
                "6098634.128628587 7874323.025982085))'))",
                93904116L, 10, "administrative", "hamlet", "Чистопереволока");
        jdbcTemplate.update("INSERT INTO jnom.administrative_boundary(osm_id, name, admin_level) VALUES (?, ?, ?)",
                93904116L, "Чистопереволока", 10);
        jdbcTemplate.update("INSERT INTO jnom.city(osm_id, name) VALUES (?, ?)",
                93904116L, "Чистопереволока");
    }

    private void insertLinesAsStreets() {
        jdbcTemplate.update("INSERT INTO osm.planet_osm_line(osm_id, name, highway, way) VALUES (?, ?, 'primary'," +
                        "ST_GeomFromEWKT('SRID=3857;LINESTRING (6099276.87623648 7872914.877539912, 6099337.957241078 7873228.161704841, " +
                        "6099320.847435342 7873478.138630656)'))",
                734137195L, "Родниковая улица");
        jdbcTemplate.update("INSERT INTO osm.planet_osm_line(osm_id, name, highway, way) VALUES (?, ?,'primary'," +
                        "ST_GeomFromEWKT('SRID=3857;LINESTRING (6099186.607261395 7873752.523187268, 6099136.969900451 7873869.769864119, " +
                        "6099072.994589091 7873981.354039228, 6099061.239250864 7873999.010964648, 6099030.314696321 7874064.908385457, " +
                        "6098993.523604614 7874164.33656375, 6098978.116987089 7874195.294018177, 6098972.807047376 7874205.959015089," +
                        " 6098884.3414480435 7874312.651310582, 6098813.007918344 7874407.808331368, 6098806.4845961835 7874447.087407337)'))",
                147224358L, "Родниковая улица");
        jdbcTemplate.update("INSERT INTO osm.planet_osm_line(osm_id, name, highway, way) VALUES (?, ?,'primary'," +
                        "ST_GeomFromEWKT('SRID=3857;LINESTRING (6099122.275727666 7874325.847895195, 6099160.458313008 7874289.889331187, " +
                        "6099190.470047725 7874248.141922723, 6099213.735821301 7874171.577940855, 6099241.398714763 7874081.092348723)'))",
                734137770L, "Новая родниковая улица");
        jdbcTemplate.update("INSERT INTO osm.planet_osm_line(osm_id, name, highway, way) VALUES (?, ?,'primary'," +
                        "ST_GeomFromEWKT('SRID=3857;LINESTRING (6099241.398714763 7874081.092348723, 6099257.7070201645 7874028.556840456, " +
                        "6099279.926390527 7873982.329214994, 6099313.444689205 7873906.120645415, 6099349.612391763 7873895.12410066)'))",
                734137771L, null);
    }
}
