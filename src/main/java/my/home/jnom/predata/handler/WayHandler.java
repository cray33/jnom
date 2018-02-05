package my.home.jnom.predata.handler;

import my.home.jnom.Utils;
import my.home.jnom.predata.entity.WayEntity;
import my.home.jnom.predata.handler.strategy.WayStrategy;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WayHandler extends DefaultHandler {
    private List<Long> waysToLoad;
    private List<Long> unloadedWays;

    private WayEntity currentWay;
    private List<WayEntity> loadedWays = new ArrayList<>();

    private WayStrategy strategy;

    public WayHandler(WayStrategy strategy) {
        this.strategy = strategy;
    }

    public WayHandler(List<Long> waysToLoad) {
        Collections.sort(waysToLoad);
        this.waysToLoad = waysToLoad;
        this.unloadedWays = new ArrayList<>(waysToLoad);
        this.strategy = new WayStrategy();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (currentWay == null && "way".equals(qName)) {
            Long osmId = Long.parseLong(attributes.getValue("id"));
            if (waysToLoad != null && Collections.binarySearch(waysToLoad, osmId) < 0) {
                return;
            }

            currentWay = new WayEntity();
            currentWay.setOsmId(osmId);
            currentWay.setTimestamp(Utils.parseDate(attributes.getValue("timestamp")));
            return;
        }
        if (currentWay != null && "nd".equals(qName)) {
            currentWay.getNodes().add(Long.parseLong(attributes.getValue("ref")));
            return;
        }
        if (currentWay != null && "tag".equals(qName)) {
            currentWay.getTags().put(attributes.getValue("k"), attributes.getValue("v"));
            return;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (currentWay != null && "way".equals(qName)) {
            if (strategy.checkEntityCondition(currentWay)) {
                currentWay.setJnomType(strategy.getJnomObjectType().getValue());
                loadedWays.add(currentWay);
                if (unloadedWays != null) {
                    unloadedWays.remove(currentWay.getOsmId());
                }
            }
            currentWay = null;
        }
    }

    public List<WayEntity> getLoadedWays() {
        return loadedWays;
    }

    public List<Long> getUnloadedWays() {
        return unloadedWays;
    }

}
