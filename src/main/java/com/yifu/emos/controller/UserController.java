package com.yifu.emos.controller;

import com.yifu.emos.common.util.R;
import com.yifu.emos.config.shiro.JwtUtil;
import com.yifu.emos.controller.form.LoginForm;
import com.yifu.emos.controller.form.RegisterForm;
import com.yifu.emos.controller.form.SearchUserGroupByDeptForm;
import com.yifu.emos.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @auther YIFU GAO
 * @date 2022/12/30/19:00
 * File Info: return token and cache token in reids
 */

@RestController
@RequestMapping("/user")
@Api("User module Web Interface")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${emos.jwt.cache-expire}")
    private int cacheExpire;

    @PostMapping("/register")
    @ApiOperation("Register User")
    public R register(@Valid @RequestBody RegisterForm form) {
        int id = userService.registerUser(form.getRegisterCode(), form.getCode(), form.getNickname(), form.getPhoto());
        String token = jwtUtil.createToken(id);
        Set<String> permSet = userService.searchUserPermissions(id);
        saveCacheToken(token, id);
        return R.ok("Successfully Registered").put("token", token).put("permission", permSet);
    }

    @PostMapping("/login")
    @ApiOperation("User login")
    public R login(@Valid @RequestBody LoginForm form) {
        int id = userService.login(form.getCode());
        String token = jwtUtil.createToken(id);
        saveCacheToken(token, id);
        Set<String> permSet = userService.searchUserPermissions(id);
        return R.ok("Successfully Logged in").put("token", token).put("permission", permSet);
    }

    @GetMapping("/searchUserSummary")
    @ApiOperation("User Summary")
    public R searchUserSummary(@RequestHeader("token") String token) {
        int userId = jwtUtil.getUserId(token);
        HashMap userSummary = userService.searchUserSummary(userId);
        return R.ok().put("result", userSummary);
    }

    @PostMapping("/searchUserGroupByDept")
    @ApiOperation("search users according to department")
    @RequiresPermissions(value={"ROOT","EMPLOYEE:SELECT"},logical = Logical.OR )
    public R searchUserGroupByDept(@Valid @RequestBody SearchUserGroupByDeptForm form){
        ArrayList<HashMap> list = userService.searchUserGroupByDept(form.getKeyword());
        return R.ok().put("result",list);

    }

    private void saveCacheToken(String token, int userId) {
        redisTemplate.opsForValue().set(token, userId + "", cacheExpire, TimeUnit.DAYS);
    }


}
