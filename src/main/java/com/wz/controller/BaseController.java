package com.wz.controller;

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
    public String add (@WZRequestParam("id") int id) {
        baseService.add();
        System.out.println(id);
        return String.valueOf(id);
    }
}
