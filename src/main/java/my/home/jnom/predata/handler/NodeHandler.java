package my.home.jnom.predata.handler;

import my.home.jnom.Utils;
import my.home.jnom.predata.entity.NodeEntity;
import my.home.jnom.predata.handler.strategy.NodeStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NodeHandler extends DefaultHandler {
    private static final Logger LOG = LoggerFactory.getLogger(NodeHandler.class);
    private List<Long> nodesToLoad;
    private List<Long> unloadedNodes;

    private List<NodeEntity> loadedNodes = new ArrayList<>();
    private NodeEntity currentNode;

    private NodeStrategy strategy;

    public NodeHandler(NodeStrategy strategy) {
        this.strategy = strategy;
    }

    public NodeHandler(List<Long> nodesToLoad) {
        Collections.sort(nodesToLoad);
        this.nodesToLoad = nodesToLoad;
        this.unloadedNodes = new ArrayList<>(nodesToLoad);
        this.strategy = new NodeStrategy();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (currentNode == null && "node".equals(qName)) {
            Long osmId = Long.parseLong(attributes.getValue("id"));
            if (nodesToLoad != null && Collections.binarySearch(nodesToLoad, osmId) < 0) {
                return;
            }

            currentNode = new NodeEntity();
            currentNode.setOsmId(osmId);
            currentNode.setTimestamp(Utils.parseDate(attributes.getValue("timestamp")));
            currentNode.setLat(Double.parseDouble(attributes.getValue("lat")));
            currentNode.setLon(Double.parseDouble(attributes.getValue("lon")));
            return;
        }
        if (currentNode != null && "tag".equals(qName)) {
            currentNode.getTags().put(attributes.getValue("k"), attributes.getValue("v"));
            return;
        }

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (currentNode != null && "node".equals(qName)) {
            if (strategy.checkEntityCondition(currentNode)) {
                currentNode.setJnomType(strategy.getJnomObjectType().getValue());
                loadedNodes.add(currentNode);
                if (unloadedNodes != null) {
                    unloadedNodes.remove(currentNode.getOsmId());
                }

                if (loadedNodes.size() % 10000 == 0) {
                    int percent = (int)(((double)loadedNodes.size() /  nodesToLoad.size()) * 100);
                    LOG.info("Loaded {} from {} nodes, {}%", loadedNodes.size(), nodesToLoad.size(), percent);
                }
            }
            currentNode = null;
        }
    }

    public List<NodeEntity> getLoadedNodes() {
        return loadedNodes;
    }

    public List<Long> getUnloadedNodes() {
        return  unloadedNodes;
    }

    public static void main(String[] args) {
        int d1 = 6000;
        int d2 = 333245;
        int d3 = (int)(((double)d1 / d2) * 100);
        System.out.println(d3);
    }
}
