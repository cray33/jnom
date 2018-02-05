package my.home.jnom.predata.handler.strategy.adm_boundary;

import my.home.jnom.entity.JnomObjectType;
import my.home.jnom.predata.entity.RelationEntity;
import my.home.jnom.predata.handler.strategy.RelationStrategy;

public class AdmBoundaryRelationHandler extends RelationStrategy {
    @Override
    public JnomObjectType getJnomObjectType() {
        return JnomObjectType.ADM_BOUNDARY;
    }

    @Override
    public boolean checkEntityCondition(RelationEntity currentEntity) {
        return currentEntity.getTags().containsKey("type")
                && currentEntity.getTags().get("type").equals("boundary")
                && currentEntity.getTags().containsKey("boundary")
                && currentEntity.getTags().get("boundary").equals("administrative")
                && currentEntity.getTags().containsKey("admin_level");
    }

    @Override
    public void handleEntity(RelationEntity currentEntity) {
        currentEntity.setAdminLevel(Integer.parseInt(currentEntity.getTags().get("admin_level")));
    }

}
