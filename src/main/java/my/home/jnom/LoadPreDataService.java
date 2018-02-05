package my.home.jnom;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import my.home.jnom.predata.service.AdmBoundaryPreDataService;
import my.home.jnom.predata.service.CitiesPreDataService;
import my.home.jnom.predata.service.HousePreDataService;
import my.home.jnom.predata.service.StreetPreDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoadPreDataService {
    private static final Logger LOG = LoggerFactory.getLogger(LoadPreDataService.class);

    //private static final String OSM_FILE = "G:\\Pavel\\Java\\jNom\\RU-UD.osm";

    @Autowired
    private AdmBoundaryPreDataService admBoundaryPreDataService;

    @Autowired
    private CitiesPreDataService citiesPreDataService;

    @Autowired
    private StreetPreDataService streetPreDataService;

    @Autowired
    private HousePreDataService housePreDataService;

    public void loadPreData(boolean loadAdmBoundaries, String osmFile) {
        try {

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            if (loadAdmBoundaries) {
                LOG.info("PreData. Load admBoundary from relations...");
                int loadRelationsCount = admBoundaryPreDataService.loadAdmBoundaryRelations(saxParser, osmFile);
                LOG.info("Done. Loaded {} administrative boundaries", loadRelationsCount);

                LOG.info("PreData. Start to parse borders of the administrative relations ...");
                LOG.info("Load ways");
                //admBoundaryPreDataService.loadWays(saxParser, osmFile);
                LOG.info("Load nodes");
                //admBoundaryPreDataService.loadNodes(saxParser, osmFile);
                admBoundaryPreDataService.loadAdministrativeBoundaryBorders(saxParser);
                LOG.info("Done.");

                LOG.info("PreData. Start to parse cities from relations...");
                int loadCitiesCount = citiesPreDataService.loadCitiesFromRelations(saxParser, osmFile);
                LOG.info("Done. Loaded {} cities", loadCitiesCount);

                LOG.info("PreData. Start to parse cities from ways...");
                loadCitiesCount = citiesPreDataService.loadCitiesFromWays(saxParser, osmFile);
                LOG.info("Done. Loaded {} cities", loadCitiesCount);

                LOG.info("PreData. Start to parse cities from nodes...");
                loadCitiesCount = citiesPreDataService.loadCitiesFromNodes(saxParser, osmFile);
                LOG.info("Done. Loaded {} cities", loadCitiesCount);


                LOG.info("PreData. Start to load ways for cities...");
                citiesPreDataService.loadWays(saxParser, osmFile);
                LOG.info("PreData. Done.");
                LOG.info("PreData. Start to load nodes for cities...");
                citiesPreDataService.loadNodes(saxParser, osmFile);
                LOG.info("PreData. Done.");

               // LOG.info("Done. Loaded {} cities", loadCitiesCount);

                LOG.info("PreData. Start to parse streets...");
                int loadStreetsCount = streetPreDataService.loadStreetsFromWays(saxParser, osmFile);
                LOG.info("PreData. Start to load nodes for streets...");
                streetPreDataService.loadNodes(saxParser, osmFile);
                LOG.info("Done. Loaded {} streets", loadStreetsCount);

               LOG.info("PreData. Start to parse houses...");
                int loadHousesCount = housePreDataService.loadHousesFromWays(saxParser, osmFile);
                LOG.info("Done. Loaded {} houses", loadHousesCount);
                LOG.info("PreData. Start to load nodes for houses...");
                housePreDataService.loadNodes(saxParser, osmFile);
                LOG.info("Done.");

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
