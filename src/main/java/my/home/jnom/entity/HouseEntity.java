package my.home.jnom.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Builder
public class HouseEntity {
    private Long osmId;
    private Map<String, String> tags = new HashMap<>();
    private String houseNumber;
    private UUID streetId;
    private Long cityOsmId;
    private Long admBoundaryOsmId;
    private Double lat;
    private Double lon;
}
