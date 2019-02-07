package com.crrchz.crawl.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author lhj
 * @Description:
 * @Date: 2019/1/30 20:32
 */
@Controller
public class webController {

    @RequestMapping("spider")
    public String spider(){
        return "spider";
    }

    @RequestMapping("test")
    public String test(){
        return "test";
    }

}
