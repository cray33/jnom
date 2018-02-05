package my.home.jnom.data.entity;

public class CityOrAdmBoundaryEntity {
    private Long cityOsmId;
    private Long admBoundaryOsmId;
    private String name;
    private Double lat;
    private Double lon;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
