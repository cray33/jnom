package my.home.jnom.predata.handler.strategy.city;

import my.home.jnom.entity.JnomObjectType;
import my.home.jnom.predata.entity.WayEntity;
import my.home.jnom.predata.handler.strategy.WayStrategy;

public class CityWayStrategy extends WayStrategy {

    @Override
    public JnomObjectType getJnomObjectType() {
        return JnomObjectType.CITY;
    }

    @Override
    public boolean checkEntityCondition(WayEntity currentEntity) {
        return currentEntity.getTags().containsKey("place") &&
                CityUtils.checkPlaceType(currentEntity.getTags().get("place"));
    }
}

