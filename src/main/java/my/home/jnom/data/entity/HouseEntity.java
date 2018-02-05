package my.home.jnom.data.entity;

import java.util.HashMap;
import java.util.Map;

public class HouseEntity {
    private Long osmId;
    private Map<String, String> tags = new HashMap<>();
    private String houseNumber;
    private Long streetOsmId;
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

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public Long getStreetOsmId() {
        return streetOsmId;
    }

    public void setStreetOsmId(Long streetOsmId) {
        this.streetOsmId = streetOsmId;
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
        return "HouseEntity{" +
                "osmId=" + osmId +
                ", tags=" + tags +
                ", houseNumber='" + houseNumber + '\'' +
                ", streetOsmId=" + streetOsmId +
                ", cityOsmId=" + cityOsmId +
                ", admBoundaryOsmId=" + admBoundaryOsmId +
                '}';
    }
}
