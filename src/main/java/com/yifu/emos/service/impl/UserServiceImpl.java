package com.yifu.emos.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.yifu.emos.db.dao.TbDeptDao;
import com.yifu.emos.db.dao.TbUserDao;
import com.yifu.emos.db.pojo.MessageEntity;
import com.yifu.emos.db.pojo.TbUser;
import com.yifu.emos.exception.EmosException;
import com.yifu.emos.service.UserService;
import com.yifu.emos.task.MessageTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @auther YIFU GAO
 * @date 2022/12/30/17:50
 * File Info:
 */
@Slf4j
@Service
@Scope("prototype")
public class UserServiceImpl implements UserService {
    @Value("${wx.app-id}")
    private String appId;

    @Value("${wx.app-secret}")
    private String appSecret;

    @Autowired
    private TbUserDao userDao;

    @Autowired
    private TbDeptDao deptDao;

    @Autowired
    private MessageTask messageTask;


    //after receiving code from frontend, we can request to get userinfo from third party
    private String getOpenId(String code) {
        String url = "https://api.weixin.qq.com/sns/jscode2session";
        HashMap map = new HashMap<>();
        map.put("appid", appId);
        map.put("secret", appSecret);
        map.put("js_code", code);
        map.put("grant_type", "authorization_code");
        //helping to request user info
        String response = HttpUtil.post(url, map);
        JSONObject jsonObject = JSONUtil.parseObj(response);
        String openid = jsonObject.getStr("openid");
        if (openid == null || openid.length() == 0) {
            throw new RuntimeException("temporary login credential error");
        }
        return openid;

    }

    @Override
    public int registerUser(String registerCode, String code, String nickname, String photo) {
        if (registerCode.equals("000000")) {
            boolean bool = userDao.haveRootUser();
            System.out.println(registerCode);
            System.out.println(bool);
            if (!bool) {
                String openId = getOpenId(code);
                HashMap param = new HashMap();
                param.put("openId", openId);
                param.put("nickname", nickname);
                param.put("photo", photo);
                param.put("role", "[0]");
                param.put("status", 1);
                param.put("createTime", new Date());
                param.put("root", true);
                userDao.insert(param);
                int id = userDao.searchIdByOpenId(openId);

                MessageEntity entity = new MessageEntity();
                entity.setSenderId(0);
                entity.setSenderName("System Message");
                entity.setMsg("Welcome to become adminstrator, Please Update your personal info");
                entity.setUuid(IdUtil.simpleUUID());
                entity.setSendTime(new Date());
                messageTask.sendAsync(id+"",entity);
                return id;
            }
            else {
                //becasue this is our business error
                throw new EmosException("unable to bind admin account");
            }
        }
        else {
            //normal user

        }
        return 0;
    }

    @Override
    public Set<String> searchUserPermissions(int userId) {
        Set<String> permissions = userDao.searchUserPermissions(userId);
        return permissions;
    }

    @Override
    public Integer login(String code) {
        String openId = getOpenId(code);
        Integer id = userDao.searchIdByOpenId(openId);
        if (id == null){
            throw new EmosException("account not exist");
        }
//        messageTask.receiveAsync(id+"");
        return id;
    }

    @Override
    public TbUser searchById(int userId) {
        TbUser user = userDao.searchById(userId);
        return user;
    }

    @Override
    public String searchUserHiredate(int userId) {
        String hiredate = userDao.searchUserHiredate(userId);
        return hiredate;
    }

    @Override
    public HashMap searchUserSummary(int userId) {
        HashMap map = userDao.searchUserSummary(userId);
        return map;
    }

    @Override
    public ArrayList<HashMap> searchUserGroupByDept(String keyword) {
        ArrayList<HashMap> list1 = deptDao.searchDeptMembers(keyword);
        ArrayList<HashMap> list2 = userDao.searchUserGroupByDept(keyword);
        for(HashMap  map_1 : list1 ){
            long deptId = (Long) map_1.get("id");
            ArrayList members = new ArrayList();
            for(HashMap map_2 : list2){
                long id = (Long)map_2.get("deptId");
                if(deptId == id){
                    members.add(map_2);
                }
            }
            map_1.put("memmbers",members);
        }
        return list1;
    }


}
