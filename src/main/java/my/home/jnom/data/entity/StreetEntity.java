package my.home.jnom.data.entity;

import my.home.jnom.entity.Border;

import java.util.HashMap;
import java.util.Map;

public class StreetEntity {
    private Long osmId;
    private String name;
    private Map<String, String> tags = new HashMap<>();
    private Long cityOsmId;
    private Long admBoundaryOsmId;
    private Double lat;
    private Double lon;

    public Long getOsmId() {
        return osmId;
    }

    public void setOsmId(Long osmId) {
        this.osmId = osmId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

    public Long getCityOsmId() {
        return cityOsmId;
    }

    public void setCityOsmId(Long cityOsmId) {
        this.cityOsmId = cityOsmId;
    }

    public Long getAdmBoundaryOsmId() {
        return admBoundaryOsmId;
    }

    public void setAdmBoundaryOsmId(Long admBoundaryOsmId) {
        this.admBoundaryOsmId = admBoundaryOsmId;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    @Override
    public String toString() {
        return "StreetEntity{" +
                "osmId=" + osmId +
                ", name='" + name + '\'' +
                ", cityOsmId=" + cityOsmId +
                ", admBoundaryOsmId=" + admBoundaryOsmId +
                '}';
    }
}
