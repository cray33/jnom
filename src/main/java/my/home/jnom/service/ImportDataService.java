package my.home.jnom.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class ImportDataService {
    private AdmBoundaryService admBoundaryService;
    private CityService cityService;
    private StreetService streetService;
    private HouseService houseService;

    public void importData() {
        log.info("The import of administration boundaries has started");
        admBoundaryService.importAdmBoundaries();
        log.info("Done.");

        log.info("The import of cities has started");
        cityService.importCities();
        log.info("Done.");

        log.info("The import of streets has started");
        streetService.importStreets();
        log.info("Done.");

        log.info("The import of houses has started");
        houseService.importHouses();
        log.info("Done.");

    }

}
