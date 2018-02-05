package my.home.jnom.predata.handler.strategy.city;

import my.home.jnom.entity.JnomObjectType;
import my.home.jnom.predata.entity.RelationEntity;
import my.home.jnom.predata.handler.strategy.RelationStrategy;

public class CityRelationStrategy extends RelationStrategy {
    @Override
    public JnomObjectType getJnomObjectType() {
        return JnomObjectType.CITY;
    }

    @Override
    public boolean checkEntityCondition(RelationEntity currentEntity) {
        return currentEntity.getTags().containsKey("place") &&
                CityUtils.checkPlaceType(currentEntity.getTags().get("place"));
    }
}
