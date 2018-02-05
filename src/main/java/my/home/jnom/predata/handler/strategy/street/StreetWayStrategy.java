package my.home.jnom.predata.handler.strategy.street;

import my.home.jnom.entity.JnomObjectType;
import my.home.jnom.predata.entity.WayEntity;
import my.home.jnom.predata.handler.strategy.WayStrategy;

public class StreetWayStrategy extends WayStrategy {

    @Override
    public JnomObjectType getJnomObjectType() {
        return JnomObjectType.STREET;
    }

    @Override
    public boolean checkEntityCondition(WayEntity currentEntity) {
        return  currentEntity.getTags().containsKey("highway") &&
                currentEntity.getTags().containsKey("name");
    }

}
