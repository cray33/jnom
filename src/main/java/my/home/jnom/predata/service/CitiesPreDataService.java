package my.home.jnom.predata.service;

import my.home.jnom.entity.JnomObjectType;
import my.home.jnom.predata.dao.MemberDAO;
import my.home.jnom.predata.dao.NodeDAO;
import my.home.jnom.predata.dao.RelationDAO;
import my.home.jnom.predata.dao.WayDAO;
import my.home.jnom.predata.entity.NodeEntity;
import my.home.jnom.predata.entity.RelationEntity;
import my.home.jnom.predata.entity.WayEntity;
import my.home.jnom.predata.handler.NodeHandler;
import my.home.jnom.predata.handler.RelationHandler;
import my.home.jnom.predata.handler.WayHandler;
import my.home.jnom.predata.handler.strategy.city.CityNodeStrategy;
import my.home.jnom.predata.handler.strategy.city.CityRelationStrategy;
import my.home.jnom.predata.handler.strategy.city.CityWayStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.parsers.SAXParser;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@Service
public class CitiesPreDataService {
    private static final Logger LOG = LoggerFactory.getLogger(CitiesPreDataService.class);

    @Autowired
    private RelationDAO relationDAO;

    @Autowired
    private MemberDAO memberDAO;

    @Autowired
    private WayDAO wayDAO;

    @Autowired
    private NodeDAO nodeDAO;

    @Autowired
    private RequestService requestService;

    public int loadCitiesFromRelations(SAXParser parser, String osmFile) throws IOException, SAXException, SQLException {
        RelationHandler cityRelationHandler = new RelationHandler(new CityRelationStrategy());
        parser.parse(osmFile, cityRelationHandler);
        List<RelationEntity> entities = cityRelationHandler.getLoadedRelations();
        for (RelationEntity entity : entities) {
            LOG.info(entity.toString());
            relationDAO.insertOfUpdate(entity);
            memberDAO.insertOrUpdateMembers(entity);
        }

        return entities.size();
    }

    public int loadCitiesFromWays(SAXParser parser, String osmFile) throws IOException, SAXException, SQLException {
        WayHandler cityWayHandler = new WayHandler(new CityWayStrategy());
        parser.parse(osmFile, cityWayHandler);
        List<WayEntity> entities = cityWayHandler.getLoadedWays();
        for (WayEntity entity : entities) {
            LOG.info(entity.toString());
            wayDAO.insertOrUpdateWay(entity);
        }

        return entities.size();
    }

    public int loadCitiesFromNodes(SAXParser parser, String osmFile) throws IOException, SAXException, SQLException {
        NodeHandler cityNodeHandler = new NodeHandler(new CityNodeStrategy());
        parser.parse(osmFile, cityNodeHandler);
        List<NodeEntity> entities = cityNodeHandler.getLoadedNodes();
        for (NodeEntity entity : entities) {
            LOG.info(entity.toString());
            nodeDAO.insertOrUpdateNode(entity);
        }

        return entities.size();
    }

    public void loadWays(SAXParser parser, String osmFile) throws IOException, SAXException {
        List<Long> waysToInsert = relationDAO.getWaysForType(JnomObjectType.CITY);

        WayHandler wayHandler = new WayHandler(waysToInsert);
        parser.parse(osmFile, wayHandler);
        List<WayEntity> ways = wayHandler.getLoadedWays();
        ways.addAll(requestService.loadWaysFromRemote(parser, wayHandler.getUnloadedWays()));

        System.out.println("constains = " + waysToInsert.contains(280903578L));

        for (WayEntity way : ways) {
            if (way.getOsmId().equals(49788804L) || way.getOsmId().equals(280903578L)) {
                System.out.println("asdfasdf");
            }
            wayDAO.insertOrUpdateWay(way);
        }

    }

    public void loadNodes(SAXParser parser, String osmFile) throws IOException, SAXException {
        List<Long> nodesToInsert = nodeDAO.getNodesForJnomType(JnomObjectType.CITY);

        NodeHandler nodeHandler = new NodeHandler(nodesToInsert);
        parser.parse(osmFile, nodeHandler);
        List<NodeEntity> nodes = nodeHandler.getLoadedNodes();
        nodes.addAll(requestService.loadNodesFromRemote(parser, nodeHandler.getUnloadedNodes()));

        for (NodeEntity node : nodes) {
            nodeDAO.insertOrUpdateNode(node);
        }

    }

}
