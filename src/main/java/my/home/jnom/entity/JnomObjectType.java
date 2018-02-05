package my.home.jnom.entity;

/**
 * Created with IntelliJ IDEA.
 * User: Павел
 * Date: 19.01.18
 * Time: 21:37
 * To change this template use File | Settings | File Templates.
 */
public enum JnomObjectType {
    NONE(0), ADM_BOUNDARY(1), CITY(2), STREET(3), HOUSE(4);

    private final int value;
    private JnomObjectType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
