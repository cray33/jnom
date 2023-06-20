package my.home.jnom.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import my.home.jnom.dao.jnom.AdmBoundaryDAO;
import my.home.jnom.dao.jnom.CityDAO;
import my.home.jnom.dao.jnom.StreetDAO;
import my.home.jnom.dao.osm.OsmLineDAO;
import my.home.jnom.entity.StreetEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class StreetService {
    private OsmLineDAO osmLineDAO;
    private CityDAO cityDAO;
    private StreetDAO streetDAO;
    private AdmBoundaryDAO admBoundaryDAO;

    private List<Long> handledLines = new ArrayList<>();

    public void importStreets() {
        List<StreetEntity> streets = osmLineDAO.findStreets();
        List<Long> cityIds = cityDAO.findAllOsmIds();
        List<Long> admBoundaries = admBoundaryDAO.findAllOsmIds(10);
        for (StreetEntity street : streets) {
            handleStreet(street, cityIds, admBoundaries);
        }
        handledLines = new ArrayList<>();
    }

    public List<StreetEntity> findStreetsAndFormatName(Long cityId, String query) {
        return streetDAO.findStreetsAndFormatName(cityId, query);
    }

    private void handleStreet(StreetEntity street, List<Long> cityIds, List<Long> admBoundaries) {
        if (handledLines.contains(street.getOsmId())) {     // this line is handled
            return;
        }

        Long cityOsmId = osmLineDAO.findCityForStreet(street.getOsmId(), cityIds).orElse(null);
        Long admBoundaryOsmId = osmLineDAO.findAdmBoundaryForStreet(street.getOsmId(), admBoundaries).orElse(null);

        if (cityOsmId == null) {
            log.warn("The city for street osmId = " + street.getOsmId()
                    + " name = \"" + street.getName() + "\" has not found");
            return;
        }
        if (admBoundaryOsmId == null) {
            log.warn("The admBoundary for street osmId = " + street.getOsmId()
                    + " name = \"" + street.getName() + "\" has not found");
            return;
        }

        street.setId(UUID.randomUUID());
        street.setCityOsmId(cityOsmId);
        street.setAdmBoundaryOsmId(admBoundaryOsmId);

        List<Long> linesInTheStreet = osmLineDAO.findAllLinesInTheStreet(street.getOsmId(), street.getName());
        handledLines.addAll(linesInTheStreet);
        street.setConsistOfOsmIds(linesInTheStreet);

        streetDAO.insertOfUpdate(street);
    }

}
