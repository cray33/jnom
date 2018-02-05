package my.home.jnom.predata.handler.strategy;

import my.home.jnom.entity.JnomObjectType;
import my.home.jnom.predata.entity.WayEntity;

/**
 * Created with IntelliJ IDEA.
 * User: Павел
 * Date: 02.02.18
 * Time: 19:48
 * To change this template use File | Settings | File Templates.
 */
public class WayStrategy {
    public JnomObjectType getJnomObjectType() {
        return JnomObjectType.NONE;
    }

    public boolean checkEntityCondition(WayEntity currentEntity) {
        return true;
    }
}
