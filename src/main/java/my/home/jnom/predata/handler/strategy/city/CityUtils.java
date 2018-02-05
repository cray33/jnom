package my.home.jnom.predata.handler.strategy.city;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CityUtils {
    private static final List<String> allowedPlaces = new ArrayList<>();
    static {
        allowedPlaces.addAll(Arrays.asList("city", "town", "village", "hamlet"));
    }

    public static boolean checkPlaceType(String place) {
        return allowedPlaces.contains(place);
    }
}
