package com.yifu.emos.config.shiro;

import com.yifu.emos.db.pojo.TbUser;
import com.yifu.emos.service.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @auther YIFU GAO
 * @date 2022/12/26/21:40
 * File Info:
 */
@Component
public class OAuth2Realm extends AuthorizingRealm {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof OAuth2Token;
    }

    //when verifying authorization
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {

        // get user authorization list
        TbUser tbUser = (TbUser) principalCollection.getPrimaryPrincipal();
        Integer id = tbUser.getId();
        Set<String> permissionSet = userService.searchUserPermissions(id);
        // put authorization list into info list
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.setStringPermissions(permissionSet);
        return info;
    }

    //when log in
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        // resolve userId from JWT and check whether this account has been frozen
        String token = (String) authenticationToken.getPrincipal();
        int userId = jwtUtil.getUserId(token);
        TbUser user = userService.searchById(userId);

        if (user == null) {
            throw new LockedAccountException("Account has been locked, please contact admin");
        }

        // adding user info and token to info object
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user, token, getName());
        return info;    
    }
}
