package se.inera.privatlakarportal.web.controller;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WebController {

    @RequestMapping(value="/", method = GET)
    public String getIndexPage() {
        return "index";
    }

    @RequestMapping(value="/version", method = GET)
    public String getVersionPage() {
        return "version";
    }
}
