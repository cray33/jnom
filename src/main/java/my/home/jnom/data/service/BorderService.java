package my.home.jnom.data.service;

import my.home.jnom.entity.Border;
import my.home.jnom.entity.Coords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

//TODO unit tests
@Service
public class BorderService {
    private static final Logger LOG = LoggerFactory.getLogger(BorderService.class);

    /**
     * Form set of closed borders from list of points
     * @param ways
     * @return
     */
    public Border getBorder(Map<Long, List<Coords>> ways) {
        Border result = new Border();

        List<Long> addedWays = new ArrayList<>(ways.size());
        List<Coords> currentWay = null;
        for (Map.Entry<Long, List<Coords>> entry : ways.entrySet()) {
            if (currentWay == null) {
                currentWay = new ArrayList<>();
                currentWay.addAll(entry.getValue());
                addedWays.add(entry.getKey());
            }
            Long lastNodeOsmId = currentWay.get(currentWay.size()-1).getNodeOsmId();
            if (lastNodeOsmId.equals(currentWay.get(0).getNodeOsmId())) {
                //the ring was closed
                if (currentWay.get(0).getRole().equals("outer")) {
                    result.getOuters().add(currentWay);
                } else if (currentWay.get(0).getRole().equals("inner")) {
                    result.getInners().add(currentWay);
                }
                currentWay = null;
                continue;
            }

            Map.Entry<Long, List<Coords>> nextWay = findWayByNode(lastNodeOsmId, ways, addedWays);
            if (nextWay == null) {
                LOG.warn("Not fount next way. WayOsmId = {} lastNodeOsmId = {}", entry.getKey(), lastNodeOsmId);
                return null;
            }
            currentWay.addAll(nextWay.getValue().subList(1, nextWay.getValue().size()));
            addedWays.add(nextWay.getKey());
        }

        return result;

    }



    private Map.Entry<Long, List<Coords>> findWayByNode(Long nodeOsmId, Map<Long, List<Coords>> ways, List<Long> addedWays) {
        for (Map.Entry<Long, List<Coords>> entry : ways.entrySet()) {
            if (addedWays.contains(entry.getKey())) {
                continue;
            }

            Long firstNodeOsmId = entry.getValue().get(0).getNodeOsmId();
            Long lastNodeOsmId = entry.getValue().get(entry.getValue().size()-1).getNodeOsmId();

            if (firstNodeOsmId.equals(nodeOsmId)) {
                return entry;
            } else if (lastNodeOsmId.equals(nodeOsmId)) {
                Collections.reverse(entry.getValue());
                return entry;
            }
        }

        return null;

    }

    /**
     * Find any point inside polygon
     * @param coords
     * @return
     */
    public Coords getPointInsideBoundary(List<Coords> coords) {
        for (int i = 2; i < coords.size(); i++) {
            Coords p1 = coords.get(i-2);
            Coords p3 = coords.get(i);

            Coords halfwayPoint = new Coords();
            halfwayPoint.setLat((p1.getLat() + p3.getLat()) / 2);
            halfwayPoint.setLon((p1.getLon() + p3.getLon()) / 2);
            if (checkPointInPolygon(halfwayPoint, coords)) {
                return halfwayPoint;
            }
        }

        throw new RuntimeException("getPointInsideBoundary is incorrect. Fixme.  ");
    }

    /**
     * Find the biggest outer borders from set of outer borders
     * @param border
     * @return
     */
    public List<Coords> getBiggerOuter(Border border) {
        List<Coords> biggerOuter = null;
        for (List<Coords> outer : border.getOuters()) {
            if (biggerOuter == null || biggerOuter.size() < outer.size()) {
                biggerOuter = outer;
            }
        }

        return biggerOuter;
    }

    public static boolean checkPointInBorder(Coords point, Border border) {
        boolean result = false;
        for (List<Coords> outer : border.getOuters()) {
            if (checkPointInPolygon(point, outer)) {
                result = true;
                break;
            }
        }

        for (List<Coords> inner : border.getInners()) {
            if (checkPointInPolygon(point, inner)) {
                result = false;
                break;
            }
        }

        return result;
    }

    /**
     * Return true if the given point is contained inside the boundary.
     * See: http://www.ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/pnpoly.html
     * @param point The point to check
     * @param polygon
     * @return true if the point is inside the boundary, false otherwise
     *
     */
    public static boolean checkPointInPolygon(Coords point, List<Coords> polygon) {
        int i;
        int j;
        boolean result = false;
        for (i = 0, j = polygon.size() - 1; i < polygon.size(); j = i++) {
            if ((polygon.get(i).getLat() > point.getLat()) != (polygon.get(j).getLat() > point.getLat()) &&
                    (point.getLon() < (polygon.get(j).getLon() - polygon.get(i).getLon()) * (point.getLat() - polygon.get(i).getLat()) / (polygon.get(j).getLat()-polygon.get(i).getLat()) + polygon.get(i).getLon())) {
                result = !result;
            }
        }
        return result;
    }

    /**
     * Get center of polygon. Based on avg lat and lon
     * @param wayCoords
     * @return
     */
    public Coords getCenterByAvg(List<Coords> wayCoords) {
        double sumLat = 0;
        double sumLon = 0;
        for (Coords coord : wayCoords) {
            sumLat += coord.getLat();
            sumLon += coord.getLon();
        }

        Coords result = new Coords();
        result.setLat(sumLat / wayCoords.size());
        result.setLon(sumLon / wayCoords.size());
        return result;
    }

    public Coords getAdminCentre(Border border) {
        List<Coords> biggestOuter = getBiggerOuter(border);
        Coords center = getCenterByAvg(biggestOuter);
        if (checkPointInBorder(center, border)) {
            return center;
        }
        return getPointInsideBoundary(biggestOuter);
    }

    public static void main(String[] args) {
        Coords c1 = new Coords();
        c1.setLat(47.6590445);
        c1.setLon(45.7644218);

        Coords c2 = new Coords();
        c2.setLat(47.6548633);
        c2.setLon(45.7644952);

        Coords c3 = new Coords();
        c3.setLat(47.6547655);
        c3.setLon(45.7522105);

        Coords c4 = new Coords();
        c4.setLat(47.6589467);
        c4.setLon(45.7521371);

        Coords c5 = new Coords();
        c5.setLat(47.6590445);
        c5.setLon(45.7644218);

        List<Coords> border = Arrays.asList(c1, c2, c3, c4, c5);

        Coords center = new Coords();
        center.setLat(47.65622443333);
        center.setLon(45.7603425);

        System.out.println(checkPointInPolygon(center, border));
    }
}
