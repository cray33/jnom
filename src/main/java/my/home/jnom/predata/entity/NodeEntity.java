package my.home.jnom.predata.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NodeEntity {
    private Long osmId;
    private Date timestamp;
    private Double lat;
    private Double lon;
    private Map<String, String> tags = new HashMap<>();
    private Integer jnomType;

    public NodeEntity() {
    }

    public NodeEntity(Long osmId) {
        this.osmId = osmId;
    }

    public Long getOsmId() {
        return osmId;
    }

    public void setOsmId(Long osmId) {
        this.osmId = osmId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
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

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

    public Integer getJnomType() {
        return jnomType;
    }

    public void setJnomType(Integer jnomType) {
        this.jnomType = jnomType;
    }

    @Override
    public String toString() {
        return "NodeEntity{" +
                "osmId=" + osmId +
                ", timestamp=" + timestamp +
                ", lat=" + lat +
                ", lon=" + lon +
                ", tags=" + tags +
                '}';
    }
}
