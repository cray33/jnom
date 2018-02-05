package my.home.jnom.data.service;

import my.home.jnom.entity.JnomObjectType;
import my.home.jnom.data.dao.HouseDAO;
import my.home.jnom.data.entity.HouseEntity;
import my.home.jnom.entity.Coords;
import my.home.jnom.predata.dao.PreDataDAO;
import my.home.jnom.predata.dao.WayDAO;
import my.home.jnom.predata.entity.WayEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class HouseDataService {
    private static final Logger LOG = LoggerFactory.getLogger(HouseDataService.class);

    @Autowired
    private WayDAO wayDAO;

    @Autowired
    private PreDataDAO preDataDAO;

    @Autowired
    private HouseDAO houseDAO;

    @Autowired
    private AdmBoundaryDataService admBoundaryDataService;

    @Autowired
    private CityDataService cityDataService;

    @Autowired
    private StreetDataService streetDataService;

    @Autowired
    private BorderService borderService;

    public void loadHouses() {
        List<WayEntity> cityHouses = wayDAO.getWayByType(JnomObjectType.HOUSE);
        for (WayEntity entity : cityHouses) {
            LOG.info(entity.toString());

            handleEntity(entity.getOsmId(), entity.getTags());
        }
    }

    private void handleEntity(Long osmId, Map<String, String> tags) {
        if ( ! tags.containsKey("addr:street")) {
            return;
        }

        List<Coords> wayCoords = preDataDAO.getWayCoords(osmId);

        HouseEntity houseEntity = new HouseEntity();
        houseEntity.setHouseNumber(tags.get("addr:housenumber"));
        if (houseEntity.getHouseNumber() == null) {
            return;
        }
        houseEntity.setTags(tags);
        houseEntity.setOsmId(osmId);
        houseEntity.setAdmBoundaryOsmId(admBoundaryDataService.findParent(wayCoords.get(0), null).getOsmId());
        houseEntity.setCityOsmId(cityDataService.findCity(wayCoords.get(0)));
        houseEntity.setStreetOsmId(streetDataService.findStreet(
                houseEntity.getAdmBoundaryOsmId(), houseEntity.getCityOsmId(), tags.get("addr:street")));
        Coords center = borderService.getCenterByAvg(wayCoords);
        houseEntity.setLat(center.getLat());
        houseEntity.setLon(center.getLon());

        LOG.info(houseEntity.toString());

        houseDAO.insertOfUpdate(houseEntity);
    }

}
