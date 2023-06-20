package my.home.jnom.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import my.home.jnom.dao.jnom.AdmBoundaryDAO;
import my.home.jnom.dao.jnom.CityDAO;
import my.home.jnom.dao.jnom.HouseDAO;
import my.home.jnom.dao.jnom.StreetDAO;
import my.home.jnom.dao.osm.OsmPolygonDAO;
import my.home.jnom.entity.HouseEntity;
import my.home.jnom.entity.StreetEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@AllArgsConstructor
public class HouseService {
    private HouseDAO houseDAO;
    private OsmPolygonDAO osmPolygonDAO;
    private AdmBoundaryDAO admBoundaryDAO;
    private CityDAO cityDAO;
    private StreetDAO streetDAO;

    public void importHouses() {
        List<HouseEntity> houses = osmPolygonDAO.findHouses();
        List<Long> admBoundaries = admBoundaryDAO.findAllOsmIds(10);
        List<Long> cities = cityDAO.findAllOsmIds();
        for (HouseEntity house : houses) {
            Long admBoundaryOsmId = osmPolygonDAO.findOwnerForObject(house.getOsmId(), admBoundaries).orElse(null);
            Long cityOsmId = osmPolygonDAO.findOwnerForObject(house.getOsmId(), cities).orElse(null);

            if (admBoundaryOsmId == null) {
                log.warn("The admBoundary for house osmId = " + house.getOsmId()
                        + " HouseNumber = \"" + house.getHouseNumber() + "\" has not found");
                continue;
            }
            if (cityOsmId == null) {
                log.warn("The city for house osmId = " + house.getOsmId()
                        + " HouseNumber = \"" + house.getHouseNumber() + "\" has not found");
                continue;
            }
            UUID streetId = findStreet(admBoundaryOsmId, cityOsmId, house.getTags().get("addr:street")).orElse(null);
            if (streetId == null) {
                log.warn("The street for house osmId = " + house.getOsmId()
                        + " HouseNumber = \"" + house.getHouseNumber() + "\" has not found");
                continue;
            }
            house.setAdmBoundaryOsmId(admBoundaryOsmId);
            house.setCityOsmId(cityOsmId);
            house.setStreetId(streetId);
            houseDAO.insertOfUpdate(house);

        }
    }

    public List<HouseEntity> findHouses(Long cityOsmId, UUID streetId, String query) {
        return houseDAO.findHouses(cityOsmId, streetId, query);
    }

    private Optional<UUID> findStreet(Long admBoundaryOsmId, Long cityOsmId, String streetName) {
        List<StreetEntity> streets = streetDAO.findStreets(cityOsmId, streetName);
        if (streets.isEmpty()) {
            return Optional.empty();
        } else if (streets.size() > 1) {
            return streets.stream()
                    .filter(s -> Objects.equals(s.getAdmBoundaryOsmId(), admBoundaryOsmId))
                    .map(StreetEntity::getId)
                    .findFirst();
        } else {
            return Optional.of(streets.get(0).getId());
        }
    }
}
