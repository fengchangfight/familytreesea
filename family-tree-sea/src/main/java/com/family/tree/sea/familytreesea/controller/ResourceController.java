package com.family.tree.sea.familytreesea.controller;

import com.family.tree.sea.familytreesea.config.PathConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(PathConstants.RESOURCE)
public class ResourceController {

    @Autowired
    private Environment env;

    @RequestMapping(method = RequestMethod.GET)
    public Map<String,String> getStaticResourceBase(){
        String staticBase = env.getProperty("static.base");
        String restBase = env.getProperty("family.service.base");

        Map<String,String> ret = new HashMap<>();
        ret.put("static", staticBase);
        ret.put("rest", restBase);
        return ret;
    }
}
