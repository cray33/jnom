package my.home.jnom.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.postgis.PGgeometry;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Builder
public class CityEntity {
    private Long osmId;
    private String name;
    private Map<String, String> tags = new HashMap<>();
    private Integer population;
    private Long admBoundaryOsmId;
    private Double lat;
    private Double lon;
    private Float wayArea;
    private PGgeometry way;
}
