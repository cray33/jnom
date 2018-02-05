package my.home.jnom.predata.entity;

import java.util.*;

public class WayEntity {
    private Long osmId;
    private Date timestamp;
    private List<Long> nodes = new ArrayList<>();
    private Map<String, String> tags = new HashMap<>();
    private int jnomType;

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

    public List<Long> getNodes() {
        return nodes;
    }

    public void setNodes(List<Long> nodes) {
        this.nodes = nodes;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

    public int getJnomType() {
        return jnomType;
    }

    public void setJnomType(int jnomType) {
        this.jnomType = jnomType;
    }

    @Override
    public String toString() {
        return "WayEntity{" +
                "osmId=" + osmId +
                ", timestamp=" + timestamp +
                ", tags=" + tags +
                '}';
    }
}
