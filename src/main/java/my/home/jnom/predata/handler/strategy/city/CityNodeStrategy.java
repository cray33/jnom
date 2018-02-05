package my.home.jnom.predata.handler.strategy.city;

import my.home.jnom.entity.JnomObjectType;
import my.home.jnom.predata.entity.NodeEntity;
import my.home.jnom.predata.handler.strategy.NodeStrategy;

public class CityNodeStrategy extends NodeStrategy {

    @Override
    public JnomObjectType getJnomObjectType() {
        return JnomObjectType.CITY;
    }

    @Override
    public boolean checkEntityCondition(NodeEntity currentEntity) {
        return currentEntity.getTags().containsKey("place") &&
                CityUtils.checkPlaceType(currentEntity.getTags().get("place"));
    }

}