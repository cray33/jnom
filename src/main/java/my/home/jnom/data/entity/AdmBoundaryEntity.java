package my.home.jnom.data.entity;

import my.home.jnom.entity.Border;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdmBoundaryEntity {
    private Long osmId;
    private String name;
    private Map<String, String> tags = new HashMap<>();
    private Integer adminLevel;
    private Long parentOsmId;
    private Border border;


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

    public Integer getAdminLevel() {
        return adminLevel;
    }

    public void setAdminLevel(Integer adminLevel) {
        this.adminLevel = adminLevel;
    }

    public Long getParentOsmId() {
        return parentOsmId;
    }

    public void setParentOsmId(Long parentOsmId) {
        this.parentOsmId = parentOsmId;
    }

    public Border getBorder() {
        return border;
    }

    public void setBorder(Border border) {
        this.border = border;
    }

    @Override
    public String toString() {
        return "AdmBoundaryEntity{" +
                "osmId=" + osmId +
                ", name='" + name + '\'' +
                ", tags=" + tags +
                ", adminLevel=" + adminLevel +
                ", parentOsmId=" + parentOsmId +
                '}';
    }
}
