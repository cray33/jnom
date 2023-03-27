package my.home.jnom.entity;

import lombok.Getter;
import lombok.Setter;
import org.postgis.PGgeometry;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class StreetEntity {
    private UUID id;
    private Long osmId;
    private String name;
    private Long cityOsmId;
    private Long admBoundaryOsmId;
    private Double lat;
    private Double lon;

    private Float wayLength;

    private PGgeometry way;

    private List<Long> consistOfOsmIds = new ArrayList<>();

    public StreetEntity() {
        id = UUID.randomUUID();
    }

    public StreetEntity(UUID id) {
        this.id = id;
    }
}
