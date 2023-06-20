package my.home.jnom.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MvcController {

    @RequestMapping("/index")
    public String index(Map<String, Object> model) {
        return "index";
    }

}