package my.home.jnom.predata.handler;

import my.home.jnom.Utils;
import my.home.jnom.predata.entity.MemberEntity;
import my.home.jnom.predata.entity.RelationEntity;
import my.home.jnom.predata.handler.strategy.RelationStrategy;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Павел
 * Date: 02.02.18
 * Time: 20:26
 * To change this template use File | Settings | File Templates.
 */
public class RelationHandler extends DefaultHandler {
    private List<RelationEntity> loadedRelations = new ArrayList<>();
    private RelationEntity currentEntity;

    private RelationStrategy strategy;

    public RelationHandler(RelationStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (currentEntity == null && "relation".equals(qName)) {
            currentEntity = new RelationEntity();
            currentEntity.setOsmId(Long.parseLong(attributes.getValue("id")));
            currentEntity.setTimestamp(Utils.parseDate(attributes.getValue("timestamp")));
            return;
        }

        if (currentEntity != null && "member".equals(qName)) {
            MemberEntity member = new MemberEntity();
            member.setType(attributes.getValue("type"));
            member.setRef(Long.parseLong(attributes.getValue("ref")));
            member.setRole(attributes.getValue("role"));
            currentEntity.getMembers().add(member);
            return;
        }

        if (currentEntity != null && "tag".equals(qName)) {
            currentEntity.getTags().put(attributes.getValue("k"), attributes.getValue("v"));
            return;
        }


    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (currentEntity != null && "relation".equals(qName)) {
            if (strategy.checkEntityCondition(currentEntity)) {
                currentEntity.setJnomType(strategy.getJnomObjectType().getValue());
                strategy.handleEntity(currentEntity);
                loadedRelations.add(currentEntity);

            }
            currentEntity = null;
        }
    }

    public List<RelationEntity> getLoadedRelations() {
        return loadedRelations;
    }
}
