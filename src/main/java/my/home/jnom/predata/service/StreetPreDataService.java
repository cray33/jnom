package my.home.jnom.predata.service;

import my.home.jnom.entity.JnomObjectType;
import my.home.jnom.predata.dao.NodeDAO;
import my.home.jnom.predata.dao.WayDAO;
import my.home.jnom.predata.entity.NodeEntity;
import my.home.jnom.predata.entity.WayEntity;
import my.home.jnom.predata.handler.NodeHandler;
import my.home.jnom.predata.handler.WayHandler;
import my.home.jnom.predata.handler.strategy.street.StreetWayStrategy;
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
public class StreetPreDataService {
    private static final Logger LOG = LoggerFactory.getLogger(StreetPreDataService.class);

    @Autowired
    private WayDAO wayDAO;

    @Autowired
    private NodeDAO nodeDAO;

    @Autowired
    private RequestService requestService;

    public int loadStreetsFromWays(SAXParser parser, String osmFile) throws IOException, SAXException, SQLException {
        WayHandler streetWayHandler = new WayHandler(new StreetWayStrategy());
        parser.parse(osmFile, streetWayHandler);
        List<WayEntity> entities = streetWayHandler.getLoadedWays();
        for (WayEntity entity : entities) {

            LOG.info(entity.toString());
            wayDAO.insertOrUpdateWay(entity);
        }

        return entities.size();
    }

    public void loadNodes(SAXParser parser, String osmFile) throws IOException, SAXException {
        List<Long> nodesToInsert = nodeDAO.getNodesForJnomType(JnomObjectType.STREET);

        NodeHandler nodeHandler = new NodeHandler(nodesToInsert);
        parser.parse(osmFile, nodeHandler);
        List<NodeEntity> nodes = nodeHandler.getLoadedNodes();
        nodes.addAll(requestService.loadNodesFromRemote(parser, nodeHandler.getUnloadedNodes()));
        for (NodeEntity node : nodes) {
            nodeDAO.insertOrUpdateNode(node);
        }
    }

}

