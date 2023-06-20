package my.home.jnom;

import org.springframework.boot.test.context.TestConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class TestDatabaseConfiguration {
    private static final PostgreSQLContainer postgreSQLContainer;

    static {
        DockerImageName image = DockerImageName.parse("postgis/postgis:13-3.3-alpine")
                .asCompatibleSubstituteFor("postgres");
        postgreSQLContainer = new PostgreSQLContainer(image);
        postgreSQLContainer.withUsername("jnom");
        postgreSQLContainer.withPassword("jnom");
        postgreSQLContainer.withInitScript("schema.sql");
        postgreSQLContainer.withDatabaseName("jnom");
        postgreSQLContainer.start();


        System.setProperty("spring.datasource.url", postgreSQLContainer.getJdbcUrl());
        System.setProperty("spring.datasource.password", postgreSQLContainer.getPassword());
        System.setProperty("spring.datasource.username", postgreSQLContainer.getUsername());
    }
}
