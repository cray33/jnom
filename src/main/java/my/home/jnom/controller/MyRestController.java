package my.home.jnom.controller;

import my.home.jnom.dao.jnom.CityDAO;
import my.home.jnom.dao.jnom.HouseDAO;
import my.home.jnom.dao.jnom.StreetDAO;
import my.home.jnom.entity.CityEntity;
import my.home.jnom.entity.HouseEntity;
import my.home.jnom.entity.StreetEntity;
import my.home.jnom.service.ImportDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/rest")
public class MyRestController {
    @Autowired
    private CityDAO cityDAO;

    @Autowired
    private StreetDAO streetDAO;

    @Autowired
    private HouseDAO houseDAO;

    @Autowired
    private ImportDataService importDataService;

    @GetMapping("/cities")
    public List<CityEntity> getCities(@RequestParam(value="q") String query) {
        return cityDAO.findCities(query);
    }

    @GetMapping("/streets")
    public List<StreetEntity> getStreets(@RequestParam(value="cityId") Long cityId,
                                         @RequestParam(value="q") String query) {
        List<StreetEntity> result = streetDAO.findStreetsFormatName(cityId, query);

        return result;
    }

    @GetMapping("/houses")
    public List<HouseEntity> getHouses( @RequestParam(value="cityOsmId") Long cityOsmId,
                                       @RequestParam(value="streetId") UUID streetId,
                                       @RequestParam(value="q") String query) {
        List<HouseEntity> result = houseDAO.findHouses(cityOsmId, streetId, query);

        return result;
    }

    @GetMapping("/startImport")
    public void startImport() {
        importDataService.importData();
    }
}
