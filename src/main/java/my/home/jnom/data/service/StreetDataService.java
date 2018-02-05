package my.home.jnom.data.service;

import my.home.jnom.entity.JnomObjectType;
import my.home.jnom.data.dao.StreetDAO;
import my.home.jnom.data.entity.AdmBoundaryEntity;
import my.home.jnom.data.entity.StreetEntity;
import my.home.jnom.entity.Coords;
import my.home.jnom.predata.dao.PreDataDAO;
import my.home.jnom.predata.dao.WayDAO;
import my.home.jnom.predata.entity.WayEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StreetDataService {
    private static final Logger LOG = LoggerFactory.getLogger(StreetDataService.class);

    @Autowired
    private WayDAO wayDAO;

    @Autowired
    private StreetDAO streetDAO;

    @Autowired
    private PreDataDAO preDataDAO;

    @Autowired
    private AdmBoundaryDataService admBoundaryDataService;

    @Autowired
    private CityDataService cityDataService;

    private Map<Long, List<StreetEntity>> streetsByAdmBoundary = new HashMap<>();
    private Map<Long, List<StreetEntity>> streetsByCity = new HashMap<>();

    public void loadStreets() {
        List<WayEntity> cityWays = wayDAO.getWayByType(JnomObjectType.STREET);
        for (WayEntity entity : cityWays) {
            LOG.info(entity.toString());

            handleEntity(entity.getOsmId(), entity.getTags());
        }
    }

    private void handleEntity(Long osmId, Map<String, String> tags) {
        List<Coords> wayCoords = preDataDAO.getWayCoords(osmId);

        StreetEntity streetEntity = new StreetEntity();
        streetEntity.setName(tags.get("name"));
        if (streetEntity.getName() == null) {
            return;
        }
        streetEntity.setTags(tags);
        streetEntity.setOsmId(osmId);
        streetEntity.setAdmBoundaryOsmId(findAdmBoundary(wayCoords));
        streetEntity.setCityOsmId(findCity(wayCoords));
        streetEntity.setLat(wayCoords.get(wayCoords.size() / 2).getLat());
        streetEntity.setLon(wayCoords.get(wayCoords.size() / 2).getLon());

        if ( ! streetsByAdmBoundary.containsKey(streetEntity.getAdmBoundaryOsmId())) {
            streetsByAdmBoundary.put(streetEntity.getAdmBoundaryOsmId(), new ArrayList<>());
        }
        streetsByAdmBoundary.get(streetEntity.getAdmBoundaryOsmId()).add(streetEntity);

        if (streetEntity.getCityOsmId() != null) {
            if ( ! streetsByCity.containsKey(streetEntity.getCityOsmId())) {
                streetsByCity.put(streetEntity.getCityOsmId(), new ArrayList<>());
            }
            streetsByCity.get(streetEntity.getCityOsmId()).add(streetEntity);
        }

        LOG.info(streetEntity.toString());

        streetDAO.insertOfUpdate(streetEntity);
    }

    private Long findAdmBoundary(List<Coords> wayCoords) {
        AdmBoundaryEntity admBoundary1 = admBoundaryDataService.findParent(wayCoords.get(0), null);
        AdmBoundaryEntity admBoundary2 = admBoundaryDataService.findParent(wayCoords.get(wayCoords.size() / 2), null);
        AdmBoundaryEntity admBoundary3 = admBoundaryDataService.findParent(wayCoords.get(wayCoords.size()-1), null);

        AdmBoundaryEntity result = admBoundary1;
        if (admBoundary2.getAdminLevel() > result.getAdminLevel()) {
            result = admBoundary2;
        } else if (admBoundary3.getAdminLevel() > result.getAdminLevel()) {
            result = admBoundary3;
        }

        return result.getOsmId();
    }

    private Long findCity(List<Coords> wayCoords) {
        Long cityOsmId = cityDataService.findCity(wayCoords.get(wayCoords.size() / 2));
        if (cityOsmId != null) {
            return cityOsmId;
        }

        cityOsmId = cityDataService.findCity(wayCoords.get(0));
        if (cityOsmId != null) {
            return cityOsmId;
        }

        cityOsmId = cityDataService.findCity(wayCoords.get(wayCoords.size() -1));
        return cityOsmId;
    }

    /**
     * Find street by name
     * @param admBoundaryOsmId
     * @param cityOsmId
     * @param name
     * @return
     */
    public Long findStreet(Long admBoundaryOsmId, Long cityOsmId, String name) {
        if (cityOsmId != null && streetsByCity.containsKey(cityOsmId)) {
            for (StreetEntity street : streetsByCity.get(cityOsmId)) {
                if (name.equalsIgnoreCase(street.getName())) {
                    return street.getOsmId();
                }
            }
        }

        if (admBoundaryOsmId != null && streetsByAdmBoundary.containsKey(admBoundaryOsmId)) {
            for (StreetEntity street : streetsByAdmBoundary.get(admBoundaryOsmId)) {
                if (name.equalsIgnoreCase(street.getName())) {
                    return street.getOsmId();
                }
            }
        }

        return null;
    }
}
