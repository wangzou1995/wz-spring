package com.wz.bean;

import com.wz.base.annotation.WZAutowired;
import com.wz.base.annotation.WZService;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserBean {
    @WZAutowired
    private AnBean anBean;
    public String name;
}
