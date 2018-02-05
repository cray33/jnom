package my.home.jnom.data.service;

import my.home.jnom.data.dao.AdmBoundaryDAO;
import my.home.jnom.data.entity.AdmBoundaryEntity;
import my.home.jnom.entity.Border;
import my.home.jnom.entity.Coords;
import my.home.jnom.predata.dao.PreDataDAO;
import my.home.jnom.predata.dao.RelationDAO;
import my.home.jnom.predata.entity.RelationEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class AdmBoundaryDataService {
    private static final Logger LOG = LoggerFactory.getLogger(AdmBoundaryDataService.class);
    @Autowired
    private PreDataDAO preDataDAO;

    @Autowired
    private RelationDAO relationDAO;

    @Autowired
    private AdmBoundaryDAO admBoundaryDAO;

    @Autowired
    private BorderService borderService;

    private Map<Integer, List<AdmBoundaryEntity>> loadedBoundaries = new HashMap<>();

    public void loadAdmBoundaries() {
        // admin_level https://wiki.openstreetmap.org/wiki/Tag:boundary%3Dadministrative
        for (int i = 3; i <= 10; i++) {
            List<RelationEntity> relations = relationDAO.getRelationsByLevel(i);
            for (RelationEntity entity : relations) {
                LOG.info(entity.toString());

                Border border = borderService.getBorder(preDataDAO.getBorderCoords(entity.getOsmId()));
                if (border != null) {
                    Coords adminCentre = preDataDAO.getAdminCentre(entity.getOsmId());
                    if (adminCentre == null) {
                         adminCentre = borderService.getAdminCentre(border);
                    }
                    border.setAdminCentre(adminCentre);
                }

                AdmBoundaryEntity boundaryEntity = new AdmBoundaryEntity();
                boundaryEntity.setName(entity.getTags().get("name"));
                boundaryEntity.setTags(entity.getTags());
                boundaryEntity.setAdminLevel(entity.getAdminLevel());
                boundaryEntity.setOsmId(entity.getOsmId());
                boundaryEntity.setBorder(border);
                boundaryEntity.setParentOsmId(getParentId(entity, border));

                if ( ! loadedBoundaries.containsKey(i)) {
                    loadedBoundaries.put(i, new ArrayList<>());
                }
                loadedBoundaries.get(i).add(boundaryEntity);

                admBoundaryDAO.insertOfUpdate(boundaryEntity);

            }
        }
    }

    private Long getParentId(RelationEntity entity, Border border) {
        Coords center = border.getAdminCentre();
        if (center == null) {
            center = borderService.getPointInsideBoundary(borderService.getBiggerOuter(border));
        }

        AdmBoundaryEntity parent = findParent(center, entity.getAdminLevel());

        return parent != null ? parent.getOsmId() : null;
    }

    public AdmBoundaryEntity findParent(Coords point, Integer adminLevel) {
        if (adminLevel == null) {
            adminLevel = 12;
        }

        // We are find for parents at the admin_level -1. If not found, we find on the admin_level -2. etc
        for (int i = adminLevel-1; i >= 2; i--) {
            List<AdmBoundaryEntity> possibleParents = loadedBoundaries.get(i);
            if (possibleParents == null) {
                continue;
            }
            for (AdmBoundaryEntity possibleParent : possibleParents) {
                if (borderService.checkPointInBorder(point, possibleParent.getBorder())) {
                    LOG.info("parent is found");
                    return possibleParent;
                }
            }
        }

        return null;
    }
}
