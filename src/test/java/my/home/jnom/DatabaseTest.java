package my.home.jnom;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

@Import(TestDatabaseConfiguration.class)
public abstract class DatabaseTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    public void clear() {
        jdbcTemplate.update("DELETE FROM osm.planet_osm_line");
        jdbcTemplate.update("DELETE FROM osm.planet_osm_polygon");
        jdbcTemplate.update("DELETE FROM jnom.house");
        jdbcTemplate.update("DELETE FROM jnom.street");
        jdbcTemplate.update("DELETE FROM jnom.city");
        jdbcTemplate.update("DELETE FROM jnom.administrative_boundary");
    }
}
