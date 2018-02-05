package my.home.jnom.entity;

public class Coords {
    private Double lat;
    private Double lon;
    private String role;
    private Long nodeOsmId;

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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getNodeOsmId() {
        return nodeOsmId;
    }

    public void setNodeOsmId(Long nodeOsmId) {
        this.nodeOsmId = nodeOsmId;
    }

    @Override
    public String toString() {
        return "Coords{" +
                "lat=" + lat +
                ", lon=" + lon +
                ", role='" + role + '\'' +
                ", nodeOsmId=" + nodeOsmId +
                '}';
    }
}
