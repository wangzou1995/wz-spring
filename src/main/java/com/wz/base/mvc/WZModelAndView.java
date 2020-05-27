package com.wz.base.mvc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WZModelAndView {
    private String viewName;
    private Map<String,?> model;
}
