package com.example.demo.controller;

import com.maowudi.docker.controller.base.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController extends BaseController {


    @RequestMapping("/ok")
    @Override
    public String successRespMsg() {
        return super.successRespMsg();
    }
}
