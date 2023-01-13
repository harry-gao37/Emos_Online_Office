package com.yifu.emos.config.shiro;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * @auther YIFU GAO
 * @date 2022/12/26/21:26
 * file info: used for shiro framework
 */

public class OAuth2Token implements AuthenticationToken {


    private String token;


    public OAuth2Token(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
