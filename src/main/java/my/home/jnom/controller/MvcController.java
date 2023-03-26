package my.home.jnom.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MvcController {

    /*@RequestMapping("/admin")
    public String welcome(@RequestParam(value = "relationOsmId", required = false) Long relationOsmId,
                          @RequestParam(value = "wayOsmId", required = false) Long wayOsmId,
                          @RequestParam(value = "pointLat", required = false) Double pointLat,
                          @RequestParam(value = "pointLon", required = false) Double pointLon,
                          Map<String, Object> model) {
        if (relationOsmId != null) {
            Border border = borderService.getBorder(preDataDAO.getBorderCoords(relationOsmId));
            if (border != null) {
                model.put("border", border);
            } else {
                model.put("setOfLines", preDataDAO.getBorderCoords(relationOsmId).values());
            }
        } else {
            model.put("line", preDataDAO.getWayCoords(wayOsmId));
        }
        model.put("pointLat", pointLat);
        model.put("pointLon", pointLon);
        return "admin";
    }*/

    @RequestMapping("/index")
    public String index(Map<String, Object> model) {
        return "index";
    }

}