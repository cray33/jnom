package my.home.jnom;

import my.home.jnom.data.dao.AdmBoundaryDAO;
import my.home.jnom.data.service.AdmBoundaryDataService;
import my.home.jnom.data.service.CityDataService;
import my.home.jnom.data.service.HouseDataService;
import my.home.jnom.data.service.StreetDataService;
import my.home.jnom.predata.dao.PreDataDAO;
import my.home.jnom.predata.dao.RelationDAO;
import my.home.jnom.data.entity.AdmBoundaryEntity;
import my.home.jnom.entity.Border;
import my.home.jnom.entity.Coords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LoadDataService {
    private static final Logger LOG = LoggerFactory.getLogger(LoadDataService.class);

    @Autowired
    private AdmBoundaryDataService admBoundaryDataService;

    @Autowired
    private CityDataService cityDataService;

    @Autowired
    private StreetDataService streetDataService;

    @Autowired
    private HouseDataService houseDataService;

    public void handleData() {
        LOG.info("Data. Start to load administration boundaries");
        admBoundaryDataService.loadAdmBoundaries();
        LOG.info("Done.");

       LOG.info("Data. Start to load cities");
        cityDataService.loadCities();
        LOG.info("Done.");

        LOG.info("Data. Start to load streets");
        streetDataService.loadStreets();
        LOG.info("Done.");

        LOG.info("Data. Start to load houses");
        houseDataService.loadHouses();
        LOG.info("Done.");

    }

}
