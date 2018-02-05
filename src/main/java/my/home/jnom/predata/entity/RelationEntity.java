package my.home.jnom.predata.entity;

import java.util.*;

public class RelationEntity {

    private Long osmId;
    private Date timestamp;
    private List<MemberEntity> members = new ArrayList<>();
    private Map<String, String> tags = new HashMap<>();
    private int jnomType;
    private int adminLevel;

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

    public List<MemberEntity> getMembers() {
        return members;
    }

    public void setMembers(List<MemberEntity> members) {
        this.members = members;
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

    public int getAdminLevel() {
        return adminLevel;
    }

    public void setAdminLevel(int adminLevel) {
        this.adminLevel = adminLevel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RelationEntity that = (RelationEntity) o;

        if (!osmId.equals(that.osmId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return osmId.hashCode();
    }

    @Override
    public String toString() {
        return "RelationEntity{" +
                "osmId=" + osmId +
                ", timestamp=" + timestamp +
                ", tags=" + tags +
                '}';
    }
}
