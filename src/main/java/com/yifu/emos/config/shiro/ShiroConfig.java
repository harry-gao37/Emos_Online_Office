package com.yifu.emos.config.shiro;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @auther YIFU GAO
 * @date 2022/12/27/22:57
 * File Info:
 */
@Configuration
public class ShiroConfig {

    @Bean("securityManager")
    public SecurityManager securityManager(OAuth2Realm realm){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(realm);
        //because jwt is storing at client side, so we don't need to store jwt at server;
        securityManager.setRememberMeManager(null);
        return securityManager;
    }
    @Bean("ShiroFilter")
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager, OAuth2Filter oAuth2Filter){
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager);

        HashMap<String, Filter> map = new HashMap<>();
        map.put("oauth2",oAuth2Filter);
        shiroFilter.setFilters(map);



        //intercept specific url
        Map<String, String> filterMap = new LinkedHashMap<>();
        filterMap.put("/webjars/**", "anon");
        filterMap.put("/druid/**", "anon");
        filterMap.put("/app/**", "anon");
        filterMap.put("/sys/login", "anon");
        filterMap.put("/swagger/**", "anon");
        filterMap.put("/v3/**", "anon");
        filterMap.put("/swagger-ui.html", "anon");
        filterMap.put("/swagger-ui/index.html", "anon");
        filterMap.put("/emos/swagger-ui/index.html", "anon");
        filterMap.put("/swagger-resources/**", "anon");
        filterMap.put("/captcha.jpg", "anon");
        filterMap.put("/user/register", "anon");
        filterMap.put("/user/login", "anon");
        filterMap.put("/test/**", "anon");
        filterMap.put("/meeting/recieveNotify", "anon");
        filterMap.put("/**", "oauth2");

        shiroFilter.setFilterChainDefinitionMap(filterMap);
        return shiroFilter;

    }

    //spring ioc lifecycle
    @Bean("lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor(){
        return new LifecycleBeanPostProcessor();
    }


    //aop in spring, bean name default is lower case of returned class
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager){
        //for aop
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }

}
