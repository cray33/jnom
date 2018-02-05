package my.home.jnom.controller;

import my.home.jnom.data.dao.AdmBoundaryDAO;
import my.home.jnom.data.dao.CityDAO;
import my.home.jnom.data.dao.HouseDAO;
import my.home.jnom.data.dao.StreetDAO;
import my.home.jnom.data.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Павел
 * Date: 01.01.18
 * Time: 1:43
 * To change this template use File | Settings | File Templates.
 */
@RestController
public class MyRestController {
    private static final Logger LOG = LoggerFactory.getLogger(MyRestController.class);

   @Autowired
   private AdmBoundaryDAO admBoundaryDAO;

    @Autowired
    private CityDAO cityDAO;

    @Autowired
    private StreetDAO streetDAO;

    @Autowired
    private HouseDAO houseDAO;

    @RequestMapping("/rest/getCity.json")
    public List<CityOrAdmBoundaryEntity> getCities(@RequestParam(value="q") String query) {
        List<CityOrAdmBoundaryEntity> result = admBoundaryDAO.findAdmBoundaries(query);
        result.addAll(cityDAO.findCities(query));

        return result;
    }

    @RequestMapping("/rest/getStreets.json")
    public List<StreetEntity> getStreets(@RequestParam(value="admBoundaryOsmId") Long admBoundaryOsmId,
                                         @RequestParam(value="q") String query) {
        List<StreetEntity> result = streetDAO.findStreets(admBoundaryOsmId, query);

        return result;
    }

    @RequestMapping("/rest/getHouses.json")
    public List<HouseEntity> getHouses(@RequestParam(value="admBoundaryOsmId") Long admBoundaryOsmId,
                                        @RequestParam(value="cityOsmId") Long cityOsmId,
                                        @RequestParam(value="streetOsmId") Long streetOsmId,
                                         @RequestParam(value="q") String query) {
        List<HouseEntity> result = houseDAO.findHouses(admBoundaryOsmId, cityOsmId, streetOsmId, query);

        return result;
    }

}
