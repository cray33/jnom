package my.home.jnom.controller;

import lombok.AllArgsConstructor;
import my.home.jnom.entity.CityEntity;
import my.home.jnom.entity.HouseEntity;
import my.home.jnom.entity.StreetEntity;
import my.home.jnom.service.CityService;
import my.home.jnom.service.HouseService;
import my.home.jnom.service.ImportDataService;
import my.home.jnom.service.StreetService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@AllArgsConstructor
@RequestMapping("/rest")
public class JnomRestController {

    private CityService cityService;
    private StreetService streetService;
    private HouseService houseService;
    private ImportDataService importDataService;

    @GetMapping("/cities")
    public List<CityEntity> getCities(@RequestParam(value="q") String query) {
        return cityService.findCities(query);
    }

    @GetMapping("/streets")
    public List<StreetEntity> getStreets(@RequestParam(value="cityId") Long cityId,
                                         @RequestParam(value="q") String query) {
        return streetService.findStreetsAndFormatName(cityId, query);
    }

    @GetMapping("/houses")
    public List<HouseEntity> getHouses( @RequestParam(value="cityOsmId") Long cityOsmId,
                                       @RequestParam(value="streetId") UUID streetId,
                                       @RequestParam(value="q") String query) {
        List<HouseEntity> result = houseService.findHouses(cityOsmId, streetId, query);

        return result;
    }

    @GetMapping("/startImport")
    public void startImport() {
        importDataService.importData();
    }
}
