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
public class AdmBoundaryEntity {
    private Long osmId;
    private String name;
    private Map<String, String> tags = new HashMap<>();
    private Integer adminLevel;
    private Long parentOsmId;

    private Double lat;

    private Double lon;

    private PGgeometry way;
}
