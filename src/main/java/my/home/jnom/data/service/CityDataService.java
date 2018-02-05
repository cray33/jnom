package my.home.jnom.data.service;

import my.home.jnom.entity.JnomObjectType;
import my.home.jnom.data.dao.CityDAO;
import my.home.jnom.data.entity.AdmBoundaryEntity;
import my.home.jnom.data.entity.CityEntity;
import my.home.jnom.entity.Border;
import my.home.jnom.entity.Coords;
import my.home.jnom.predata.dao.PreDataDAO;
import my.home.jnom.predata.dao.RelationDAO;
import my.home.jnom.predata.dao.WayDAO;
import my.home.jnom.predata.entity.RelationEntity;
import my.home.jnom.predata.entity.WayEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CityDataService {
    private static final Logger LOG = LoggerFactory.getLogger(CityDataService.class);
    @Autowired
    private PreDataDAO preDataDAO;

    @Autowired
    private RelationDAO relationDAO;

    @Autowired
    private WayDAO wayDAO;

    @Autowired
    private BorderService borderService;

    @Autowired
    private AdmBoundaryDataService admBoundaryDataService;

    @Autowired
    private CityDAO cityDAO;

    private List<CityEntity> loadedCities = new ArrayList<>();

    public void loadCities() {
        List<RelationEntity> cityRelations = relationDAO.getRelationsByType(JnomObjectType.CITY);
        for (RelationEntity entity : cityRelations) {
            LOG.info(entity.toString());
            handleEntity(entity.getOsmId(), entity.getTags());
        }

        List<WayEntity> cityWays = wayDAO.getWayByType(JnomObjectType.CITY);
        for (WayEntity entity : cityWays) {
            LOG.info(entity.toString());

            handleEntity(entity.getOsmId(), entity.getTags());
        }
    }

    private void handleEntity(Long osmId, Map<String, String> tags) {
        Border border = borderService.getBorder(preDataDAO.getBorderCoords(osmId));

        CityEntity cityEntity = new CityEntity();
        cityEntity.setName(tags.get("name"));
        if (cityEntity.getName() == null) {
            return;
        }
        cityEntity.setTags(tags);
        if (tags != null && ! tags.isEmpty() && tags.containsKey("population")) {
            cityEntity.setPopulation(Integer.parseInt(tags.get("population")));
        }
        cityEntity.setOsmId(osmId);
        cityEntity.setBorder(border);
        cityEntity.setAdmBoundaryOsmId(getParentId(border));
        Coords center = borderService.getCenterByAvg(borderService.getBiggerOuter(border));
        cityEntity.setLat(center.getLat());
        cityEntity.setLon(center.getLon());

        loadedCities.add(cityEntity);

        LOG.info(cityEntity.toString());

        cityDAO.insertOfUpdate(cityEntity);
    }

    private Long getParentId(Border border) {
        Coords center = border.getAdminCentre();
        if (center == null) {
            center = borderService.getPointInsideBoundary(borderService.getBiggerOuter(border));
        }

        AdmBoundaryEntity parent = admBoundaryDataService.findParent(center, 9);

        return parent != null ? parent.getOsmId() : null;
    }

    /**
     * Find city by coords
     * @param point
     * @return
     */
    public Long findCity(Coords point) {
        for (CityEntity city : loadedCities) {
            if (borderService.checkPointInBorder(point, city.getBorder())) {
                return city.getOsmId();
            }
        }

        return null;
    }

}
