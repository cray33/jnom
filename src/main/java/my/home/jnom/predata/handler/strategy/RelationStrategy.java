package my.home.jnom.predata.handler.strategy;

import my.home.jnom.entity.JnomObjectType;
import my.home.jnom.predata.entity.RelationEntity;

/**
 * Created with IntelliJ IDEA.
 * User: Павел
 * Date: 02.02.18
 * Time: 20:27
 * To change this template use File | Settings | File Templates.
 */
public class RelationStrategy {
    public JnomObjectType getJnomObjectType() {
        return JnomObjectType.NONE;
    }

    public boolean checkEntityCondition(RelationEntity currentEntity) {
        return true;
    }

    public void handleEntity(RelationEntity currentEntity) {

    }
}
