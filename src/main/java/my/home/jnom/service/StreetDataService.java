package my.home.jnom.service;

import lombok.extern.slf4j.Slf4j;
import my.home.jnom.dao.jnom.AdmBoundaryDAO;
import my.home.jnom.dao.jnom.CityDAO;
import my.home.jnom.dao.jnom.StreetDAO;
import my.home.jnom.dao.osm.OsmLineDAO;
import my.home.jnom.entity.StreetEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class StreetDataService {
    @Autowired
    private OsmLineDAO osmLineDAO;
    @Autowired
    private CityDAO cityDAO;
    @Autowired
    private StreetDAO streetDAO;
    @Autowired
    private AdmBoundaryDAO admBoundaryDAO;

    public void importStreets() {
        List<StreetEntity> streets = osmLineDAO.findStreets();
        List<Long> cityIds = cityDAO.findAllOsmIds();
        List<Long> admBoundaries = admBoundaryDAO.findAllOsmIds(10);
        for (StreetEntity street : streets) {
            Long cityOsmId = osmLineDAO.findCityForStreet(street.getOsmId(), cityIds).orElse(null);
            Long admBoundaryOsmId = osmLineDAO.findAdmBoundaryForStreet(street.getOsmId(), admBoundaries).orElse(null);

            if (cityOsmId == null) {
                log.warn("The city for street osmId = " + street.getOsmId()
                        + " name = \"" + street.getName() + "\" has not found");
                continue;
            }
            if (admBoundaryOsmId == null) {
                log.warn("The admBoundary for street osmId = " + street.getOsmId()
                        + " name = \"" + street.getName() + "\" has not found");
                continue;
            }
            street.setCityOsmId(cityOsmId);
            street.setAdmBoundaryOsmId(admBoundaryOsmId);
            streetDAO.insertOfUpdate(street);
        }
    }

}
