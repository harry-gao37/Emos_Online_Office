package com.yifu.emos.config.shiro;

import org.springframework.stereotype.Component;

/**
 * @auther YIFU GAO
 * @date 2022/12/26/22:05
 * File Info:
 */
@Component
public class ThreadLocalToken {
    private ThreadLocal<String> local = new ThreadLocal<>();
    public void setToken(String token){
        local.set(token);
    }


    public String getToken(){
        return local.get();
    }

    public void clear(){
        local.remove();
    }

}
