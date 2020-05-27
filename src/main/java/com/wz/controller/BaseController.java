package com.wz.controller;

import com.alibaba.fastjson.JSONObject;
import com.wz.base.annotation.WZAutowired;
import com.wz.base.annotation.WZController;
import com.wz.base.annotation.WZRequestMapping;
import com.wz.base.annotation.WZRequestParam;
import com.wz.service.BaseService;

@WZController
@WZRequestMapping("/web")
public class BaseController {
    @WZAutowired
    public BaseService baseService;
    @WZRequestMapping(value = "/api.json")
    public Object add (@WZRequestParam("id") int id) {
        baseService.add();
        System.out.println(id);
        JSONObject object = new JSONObject();
        object.put("success",true);
        object.put("code",1);
        object.put("data", id);
        return object;
    }
}
