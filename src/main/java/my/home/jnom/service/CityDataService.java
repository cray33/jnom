package my.home.jnom.service;

import my.home.jnom.dao.jnom.AdmBoundaryDAO;
import my.home.jnom.dao.jnom.CityDAO;
import my.home.jnom.dao.osm.OsmPolygonDAO;
import my.home.jnom.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityDataService {
    @Autowired
    private AdmBoundaryDAO admBoundaryDAO;

    @Autowired
    private OsmPolygonDAO osmPolygonDAO;

    @Autowired
    private CityDAO cityDAO;

    public void importCities() {
        List<CityEntity> cities = osmPolygonDAO.findCities();
        List<Long> admBoundariesIds = admBoundaryDAO.findAllOsmIds(9);
        cities.forEach(city -> {
            city.setAdmBoundaryOsmId(osmPolygonDAO.findOwnerForObject(city.getOsmId(), admBoundariesIds).orElse(null));
            if (city.getName() == null) {
                return; //TODO find a point inside a polygon with place=village or similar
            }
            cityDAO.insertOfUpdate(city);
        });
    }
}