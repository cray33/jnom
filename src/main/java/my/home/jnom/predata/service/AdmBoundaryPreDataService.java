package my.home.jnom.predata.service;

import my.home.jnom.predata.dao.MemberDAO;
import my.home.jnom.predata.dao.NodeDAO;
import my.home.jnom.predata.dao.RelationDAO;
import my.home.jnom.predata.dao.WayDAO;
import my.home.jnom.predata.entity.NodeEntity;
import my.home.jnom.predata.entity.RelationEntity;
import my.home.jnom.predata.entity.WayEntity;
import my.home.jnom.predata.handler.RelationHandler;
import my.home.jnom.predata.handler.WebApiHandler;
import my.home.jnom.predata.handler.strategy.adm_boundary.AdmBoundaryRelationHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.SAXParser;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AdmBoundaryPreDataService {

    private static final Logger LOG = LoggerFactory.getLogger(AdmBoundaryPreDataService.class);

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

    /**
     * Load administrative boundaries from relations
     * @param parser
     * @param osmFile
     * @return
     * @throws IOException
     * @throws SAXException
     * @throws SQLException
     */
    public int loadAdmBoundaryRelations(SAXParser parser, String osmFile) throws IOException, SAXException, SQLException {
        RelationHandler relationHandler = new RelationHandler(new AdmBoundaryRelationHandler());
        parser.parse(osmFile, relationHandler);
        List<RelationEntity> entities = relationHandler.getLoadedRelations();
        for (RelationEntity entity : entities) {
            LOG.info(entity.toString());
            if (entity.getOsmId().equals(1075831L)) {
                System.out.println("asdfa");
            }
            relationDAO.insertOfUpdate(entity);
            //memberDAO.insertOrUpdateMembers(entity);
        }

        return entities.size();
    }

  /*  public void loadWays(SAXParser parser, String osmFile) throws IOException, SAXException {
        List<Long> waysToInsert = relationDAO.getWaysForType(JnomObjectType.ADM_BOUNDARY);
        System.out.println("waysToInsert = " + waysToInsert.size());

        WayHandler wayHandler = new WayHandler(waysToInsert);
        parser.parse(osmFile, wayHandler);
        List<WayEntity> ways = wayHandler.getLoadedWays();
        System.out.println("ways count = " + ways.size() + " unloaded = " + wayHandler.getUnloadedWays().size());
        List<WayEntity> loadedFromRemote = requestService.loadWaysFromRemote(parser, wayHandler.getUnloadedWays());
        System.out.println("Loaded from remote = " + loadedFromRemote.size());
        ways.addAll(loadedFromRemote);

        LOG.info("wayDAO.insertOrUpdateWay");
        for (WayEntity way : ways) {
            wayDAO.insertOrUpdateWay(way);
        }
    }

    public void loadNodes(SAXParser parser, String osmFile) throws IOException, SAXException {
        List<Long> nodesToInsert = nodeDAO.getNodesForJnomType(JnomObjectType.ADM_BOUNDARY);

        NodeHandler nodeHandler = new NodeHandler(nodesToInsert);
        parser.parse(osmFile, nodeHandler);
        List<NodeEntity> nodes = nodeHandler.getLoadedNodes();
        nodes.addAll(requestService.loadNodesFromRemote(parser, nodeHandler.getUnloadedNodes()));

        for (NodeEntity node : nodes) {
            nodeDAO.insertOrUpdateNode(node);
        }

    }
   */
    /**
     * Load borders of these administrative boundaries
     * @param parser
     * @throws SAXException
     */
    public void loadAdministrativeBoundaryBorders(SAXParser parser) throws SAXException {
        // admin_level https://wiki.openstreetmap.org/wiki/Tag:boundary%3Dadministrative
        Set<RelationEntity> unsuccessfulRelations = new HashSet<>();

        for (int i = 3; i < 10; i++) {
            List<RelationEntity> relations = relationDAO.getRelationsByLevel(i);
            for (RelationEntity relation : relations) {
                try {
                    handleRelation(parser, relation);
                } catch (IOException ex) {
                    LOG.error("Error while request", ex);
                    unsuccessfulRelations.add(relation);
                } catch (SQLException ex) {
                    LOG.error("sql exception", ex);
                }
            }
        }

        // keep requests until success
        while ( ! unsuccessfulRelations.isEmpty()) {
            try {
                RelationEntity relation = unsuccessfulRelations.iterator().next();
                handleRelation(parser, relation);
                unsuccessfulRelations.remove(relation);
            } catch (IOException ex) {
                LOG.error("Error while request", ex);
            } catch (SQLException ex) {
                LOG.error("sql exception", ex);
            }
        }
    }

    private void handleRelation(SAXParser parser, RelationEntity relation) throws IOException, SQLException, SAXException {
        LOG.info("Handle {}", relation );
        String url = "http://www.openstreetmap.org/api/0.6/relation/" + relation.getOsmId() + "/full";
        LOG.info("request to {}", url);

        WebApiHandler wayApiParser = new WebApiHandler(relation.getOsmId());

        HttpURLConnection huc = (HttpURLConnection) new URL(url).openConnection();
        HttpURLConnection.setFollowRedirects(false);
        huc.setConnectTimeout(15 * 1000);
        huc.setReadTimeout(15 * 1000);
        huc.setRequestMethod("GET");
        //huc.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.1.2) Gecko/20090729 Firefox/3.5.2 (.NET CLR 3.5.30729)");
        huc.connect();
        InputStream input = huc.getInputStream();

        parser.parse(new InputSource(input), wayApiParser);

        relation = wayApiParser.getCurrentRelation();
        relationDAO.insertOfUpdate(relation);
        memberDAO.insertOrUpdateMembers(relation);

        for (WayEntity way : wayApiParser.getWayList()) {
            //LOG.info("insertOrUpdateWay {}", way);
            wayDAO.insertOrUpdateWay(way);
        }

        LOG.info("insert nodes...");
        for (NodeEntity node : wayApiParser.getNodeList()) {
            nodeDAO.insertOrUpdateNode(node);
        }
    }

}
