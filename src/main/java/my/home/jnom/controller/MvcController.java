package my.home.jnom.controller;

/**
 * Created with IntelliJ IDEA.
 * User: Павел
 * Date: 19.12.17
 * Time: 21:06
 * To change this template use File | Settings | File Templates.
 */
import java.util.Map;

import my.home.jnom.entity.Border;
import my.home.jnom.predata.dao.PreDataDAO;
import my.home.jnom.data.service.BorderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MvcController {

    @Autowired
    private PreDataDAO preDataDAO;

    @Autowired
    private BorderService borderService;

    @RequestMapping("/admin")
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
    }

    @RequestMapping("/searchbox")
    public String welcome(
                          Map<String, Object> model) {
        return "searchbox";
    }

    @RequestMapping("/test")
    public String test(
            Map<String, Object> model) {
        return "test";
    }

}