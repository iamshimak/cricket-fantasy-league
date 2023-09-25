package com.cricket.fantasy.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @RequestMapping(path = {"/", "/home"}, method = {RequestMethod.GET})
    @SuppressWarnings("static-method")
    public String home() {
        return  "Incentivio Google Ordering deployment successful...";
    }
}
