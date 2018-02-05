package my.home.jnom.predata.handler;

import my.home.jnom.Utils;
import my.home.jnom.predata.entity.MemberEntity;
import my.home.jnom.predata.entity.NodeEntity;
import my.home.jnom.predata.entity.RelationEntity;
import my.home.jnom.predata.entity.WayEntity;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Parser for parsing result of the web requests
 * For example https://www.openstreetmap.org/api/0.6/relation/1853865/full
 */
public class WebApiHandler extends DefaultHandler {
    private List<NodeEntity> nodeList = new ArrayList<>();
    private List<WayEntity> wayList = new ArrayList<>();

    private Long relationId;
    private RelationEntity tempRelation;
    private RelationEntity currentRelation;
    private WayEntity currentWay;
    private NodeEntity currentNode;

    public WebApiHandler(Long relationId) {
        this.relationId = relationId;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        // ---- node --------
        if (currentNode == null && "node".equals(qName)) {
            Long osmId = Long.parseLong(attributes.getValue("id"));

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

        //------- way ----------------
        if (currentWay == null && "way".equals(qName)) {
            Long osmId = Long.parseLong(attributes.getValue("id"));

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

        //------ relation --------------
        if (tempRelation == null && "relation".equals(qName)) {
            Long osmId = Long.parseLong(attributes.getValue("id"));
            if ( ! osmId.equals(relationId)) {
                return;
            }
            tempRelation = new RelationEntity();
            tempRelation.setOsmId(osmId);
            tempRelation.setTimestamp(Utils.parseDate(attributes.getValue("timestamp")));
            return;
        }

        if (tempRelation != null && "member".equals(qName)) {
            MemberEntity member = new MemberEntity();
            member.setType(attributes.getValue("type"));
            member.setRef(Long.parseLong(attributes.getValue("ref")));
            member.setRole(attributes.getValue("role"));
            tempRelation.getMembers().add(member);

            return;
        }

        if (tempRelation != null && "tag".equals(qName)) {
            tempRelation.getTags().put(attributes.getValue("k"), attributes.getValue("v"));
            return;
        }

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        // -------- node ------------
        if (currentNode != null && "node".equals(qName)) {
            nodeList.add(currentNode);
            currentNode = null;
        }

        //---------- way -------------
        if (currentWay != null && "way".equals(qName)) {
            wayList.add(currentWay);
            currentWay = null;
        }

        // -------- relation --------
        if (tempRelation != null && "relation".equals(qName)) {
            tempRelation.setAdminLevel(Integer.parseInt(tempRelation.getTags().get("admin_level")));
            currentRelation = tempRelation;
            tempRelation = null;
        }

    }

    public List<NodeEntity> getNodeList() {
        return nodeList;
    }

    public List<WayEntity> getWayList() {
        return wayList;
    }

    public RelationEntity getCurrentRelation() {
        return currentRelation;
    }
}
