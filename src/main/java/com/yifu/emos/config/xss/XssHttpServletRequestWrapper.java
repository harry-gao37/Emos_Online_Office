package com.yifu.emos.config.xss;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HtmlUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @auther YIFU GAO
 * @date 2022/12/25/21:58
 * TODO decorator design
 * syntax transform for request header, parameters and data
 */

public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {
    public XssHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String getParameter(String name) {
        String parameter = super.getParameter(name);
        if (!StrUtil.hasEmpty(parameter)) {
            parameter = HtmlUtil.filter(parameter);
        }

        return parameter;
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                String parameter = values[i];
                if (!StrUtil.hasEmpty(parameter)) {
                    parameter = HtmlUtil.filter(parameter);
                }
                values[i] = parameter;
            }
        }
        return values;
    }


    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> parameters = super.getParameterMap();
        LinkedHashMap<String, String[]> map = new LinkedHashMap<>();
        if (map != null) {
            for (String key : parameters.keySet()) {
                String[] parameter = parameters.get(key);
                for (int i = 0; i < parameter.length; i++) {
                    String p = parameter[i];
                    if (!StrUtil.hasEmpty(p)) {
                        p = HtmlUtil.filter(p);
                    }
                    parameter[i] = p;
                }
                map.put(key, parameter);
            }
        }
        return map;
    }

    @Override
    public String getHeader(String name) {
        String parameter = super.getHeader(name);
        if (!StrUtil.hasEmpty(parameter)) {
            parameter = HtmlUtil.filter(parameter);
        }

        return parameter;
    }


    @Override
    public ServletInputStream getInputStream() throws IOException {
        ServletInputStream in = super.getInputStream();
        InputStreamReader reader = new InputStreamReader(in, Charset.forName("UTF-8"));
        BufferedReader buffer = new BufferedReader(reader);
        StringBuffer body = new StringBuffer();
        String line;
        while ((line = buffer.readLine()) != null) {
            body.append(line);
        }

        buffer.close();
        reader.close();
        in.close();

        Map<String, Object> map = JSONUtil.parseObj(body.toString());
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        for (String key : map.keySet()) {
            Object o = map.get(key);
            if (o instanceof String) {
                if (!StrUtil.hasEmpty(o.toString())) {
                    result.put(key, HtmlUtil.filter(o.toString()));
                }
            } else {
                result.put(key, o);
            }
        }

        String json = JSONUtil.toJsonStr(result);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(json.getBytes());
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }

            @Override
            public int read() throws IOException {
                return byteArrayInputStream.read();
            }
        };


    }
}
