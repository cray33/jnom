package my.home.jnom.predata.handler.strategy.house;

import my.home.jnom.entity.JnomObjectType;
import my.home.jnom.predata.entity.WayEntity;
import my.home.jnom.predata.handler.strategy.WayStrategy;

public class HouseWayStrategy extends WayStrategy {
    @Override
    public JnomObjectType getJnomObjectType() {
        return JnomObjectType.HOUSE;
    }

    @Override
    public boolean checkEntityCondition(WayEntity currentEntity) {
        return currentEntity.getTags().containsKey("building") &&
                currentEntity.getTags().containsKey("addr:housenumber");
    }
}