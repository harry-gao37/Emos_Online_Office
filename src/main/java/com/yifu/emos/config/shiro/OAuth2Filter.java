package com.yifu.emos.config.shiro;

import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import org.apache.http.HttpStatus;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

/**
 * @auther YIFU GAO
 * @date 2022/12/27/21:24
 * File Info:
 */
@Component
@Scope("prototype")
public class OAuth2Filter extends AuthenticatingFilter {
    @Autowired
    private ThreadLocalToken threadLocalToken;

    @Value("${emos.jwt.cache-expire}")
    private int cacheExipre;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    protected AuthenticationToken createToken(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String requestToken = getRequestToken(request);
        if(StrUtil.isBlank(requestToken)){
            return null;
        }
        //shiro will check whether it is  valid token
        return new OAuth2Token(requestToken);
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        // intercept every request that is not options
        HttpServletRequest req = (HttpServletRequest) request;
        if (req.getMethod().equals(RequestMethod.OPTIONS.name())){
            return true;
        }
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        //dealing with every request to check token info
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Credentials","true");
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));

        threadLocalToken.clear();
        String requestToken = getRequestToken(request);
        if (StrUtil.isBlank(requestToken)){
            response.setStatus(HttpStatus.SC_UNAUTHORIZED);
            response.getWriter().print("Invalid Token");
            return false;
        }

        try {
            jwtUtil.verifyToken(requestToken);
        } catch (TokenExpiredException e) {
            //token expired
            if(redisTemplate.hasKey(requestToken)){
                redisTemplate.delete(requestToken);
                int userId = jwtUtil.getUserId(requestToken);
                String token = jwtUtil.createToken(userId);
                redisTemplate.opsForValue().set(token,userId+"",cacheExipre, TimeUnit.DAYS);
                threadLocalToken.setToken(token);

            }else{
                response.setStatus(HttpStatus.SC_UNAUTHORIZED);
                response.getWriter().print("Expired Token, need log in");
                return false;
            }
        }catch(Exception e){
            response.setStatus(HttpStatus.SC_UNAUTHORIZED);
            response.getWriter().print("Invalid Token");
            return false;
        }

        boolean b = executeLogin(servletRequest, servletResponse);
        return b;
    }

    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Credentials","true");
        resp.setHeader("Access-Control-Allow-Origin", req.getHeader("Origin"));

        resp.setStatus(HttpStatus.SC_UNAUTHORIZED);

        try {
            resp.getWriter().print(e.getMessage());
        } catch (Exception ex) {
        }
        return false;
    }

    public String getRequestToken(HttpServletRequest request){
        String token = request.getHeader("token");
        //try retrieve from body
        if (StrUtil.isBlank(token)){
            token = request.getParameter("token");
        }
        return token;
    }
}
