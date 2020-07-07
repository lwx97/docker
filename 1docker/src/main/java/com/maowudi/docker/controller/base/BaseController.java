package com.maowudi.docker.controller.base;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;

public class BaseController {

    private Object data;

    private String msg;

    private String success = "success";

    private String error = "error";

    private HashMap<Object, Object> map = new HashMap<>();

    public String successRespMsg(Object data, String msg) {
        map.clear();
        map.put("data", data);
        map.put("msg", msg);
        map.put("status", success);
        return JSONObject.toJSONString(map);
    }

    public String successRespMsg(Object data) {
        map.clear();
        map.put("data", data);
        map.put("msg", "");
        map.put("status", success);
        return JSONObject.toJSONString(map);
    }

    public String successRespMsg(String msg) {
        map.clear();
        map.put("msg", msg);
        map.put("status", success);
        return JSONObject.toJSONString(map);
    }

    public String successRespMsg() {
        map.clear();
        map.put("status", success);
        return JSONObject.toJSONString(map);
    }

    public String errorRespMsg(String msg) {
        map.clear();
        map.put("msg", msg);
        map.put("status", error);
        return JSONObject.toJSONString(map);
    }

    public String errorRespMsg() {
        map.clear();
        map.put("msg", "");
        map.put("status", error);
        return JSONObject.toJSONString(map);
    }


}
