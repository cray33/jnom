package my.home.jnom.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ImportDataService {

    @Autowired
    private AdmBoundaryDataService admBoundaryDataService;

    @Autowired
    private CityDataService cityDataService;

    @Autowired
    private StreetDataService streetDataService;

    @Autowired
    private HouseDataService houseDataService;

    public void importData() {
        log.info("The import of administration boundaries is started");
        admBoundaryDataService.importAdmBoundaries();
        log.info("Done.");

        log.info("The import of cities is started");
        cityDataService.importCities();
        log.info("Done.");

        log.info("The import of streets is started");
        streetDataService.importStreets();
        log.info("Done.");

        log.info("The import of houses is started");
        houseDataService.importHouses();
        log.info("Done.");

    }

}
