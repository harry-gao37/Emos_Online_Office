package com.yifu.emos.common.util;

import org.apache.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * @auther YIFU GAO
 * @date 2022/12/23/22:12
 * TODO return response result
 */

public class R extends HashMap<String, Object> {
    public R() {
        //use put method below
        put("code", HttpStatus.SC_OK);
        put("msg", "success");
    }

    //like stream class
    public R put(String key, Object val) {
        super.put(key, val);
        return this;
    }

    public static R ok() {
        return new R();
    }

    public static R ok(String msg) {
        R r = new R();
        r.put("msg", msg);
        return r;
    }

    public static R ok(Map<String, Object> map) {
        R r = new R();
        r.putAll(map);
        return r;
    }

    //overwrite value in map and speify code we want
    public static R error(int code, String msg) {
        R r = new R();
        r.put("msg", msg);
        r.put("code", code);
        return r;
    }

    public static R error(String msg) {
        return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, msg);

    }

    public static R error() {
        return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "unknown error, please contact admin");

    }
}
