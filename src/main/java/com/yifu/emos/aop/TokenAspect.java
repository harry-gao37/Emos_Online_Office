package com.yifu.emos.aop;

import com.yifu.emos.common.util.R;
import com.yifu.emos.config.shiro.ThreadLocalToken;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @auther YIFU GAO
 * @date 2022/12/27/23:23
 * File Info:
 */
@Aspect
@Component
public class TokenAspect {
    @Autowired
    private ThreadLocalToken threadLocalToken;

    @Pointcut("execution(public * com.yifu.emos.controller.*.*(..))")
    private void aspect(){

    }

    @Around("aspect()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        R r = (R) point.proceed();

        String token = threadLocalToken.getToken();
        if(token != null){
            r.put("token",token);
            threadLocalToken.clear();
        }
        return r;
    }
}
