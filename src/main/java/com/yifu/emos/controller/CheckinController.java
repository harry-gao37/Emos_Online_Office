package com.yifu.emos.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import com.yifu.emos.common.util.R;
import com.yifu.emos.config.SystemConstants;
import com.yifu.emos.config.shiro.JwtUtil;
import com.yifu.emos.controller.form.CheckinfoForm;
import com.yifu.emos.controller.form.searchMonthCheckinForm;
import com.yifu.emos.exception.EmosException;
import com.yifu.emos.service.CheckinService;
import com.yifu.emos.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @auther YIFU GAO
 * @date 2023/01/04/20:34
 * File Info:
 */
@RestController
@Slf4j
@RequestMapping("/checkin")
@Api("CheckInModule")
public class CheckinController {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CheckinService checkinService;

    @Autowired
    private UserService userService;

    @Value("${emos.image-folder}")
    private String imageFolder;


    @Autowired
    private SystemConstants constants;

    @GetMapping("/validCheckIn")
    @ApiOperation("Examine whether user can check in today")
    public R validCheckIn(@RequestHeader("token") String token) {
        int userId = jwtUtil.getUserId(token);
        String result = checkinService.validCheckIn(userId, DateUtil.today());
        return R.ok(result);
    }

    @PostMapping("/checkin")
    @ApiOperation("Check in")
    public R checkIn(@Valid CheckinfoForm form, @RequestParam("photo")MultipartFile file, @RequestHeader("token") String token){
        if(file == null){
            return R.error("No submitted file");
        }
        int userId = jwtUtil.getUserId(token);
        String fileName = file.getOriginalFilename().toLowerCase();
        if (!fileName.endsWith(".jpg")){
            return R.error("You must submit JPG format photo");
        }
        else{
            String path = imageFolder + "/" + fileName;
            try {
                file.transferTo(Paths.get(path));
                HashMap param = new HashMap();
                param.put("userId",userId);
                param.put("path",path);
                param.put("city",form.getCity());
                param.put("district",form.getDistrict());
                param.put("address",form.getAddress());
                param.put("country",form.getCountry());
                param.put("province",form.getProvince());
                checkinService.checkin(param);
                return R.ok("Successfully checked in");

            } catch (IOException e) {
                log.error(e.getMessage(),e);
                throw new EmosException("Failed to Store Error");
            }
            finally {
                FileUtil.del(path);
            }
        }
    }

    @PostMapping("/createFaceModel")
    @ApiOperation("create face model")
    public R createFaceModel(@RequestParam("photo") MultipartFile file, @RequestHeader("token") String token){
        if(file == null){
            return R.error("No submitted file");
        }
        int userId = jwtUtil.getUserId(token);
        String fileName = file.getOriginalFilename().toLowerCase();
        if (!fileName.endsWith(".jpg")){
            return R.error("You must submit JPG format photo");
        }
        else{
            String path = imageFolder + "/" + fileName;
            try {
                //transfer to destination location
                file.transferTo(Paths.get(path));
                checkinService.createFaceModel(userId, path);
                return R.ok("Successfully create face model");

            } catch (IOException e) {
                log.error(e.getMessage(),e);
                throw new EmosException("Failed to Store Error");
            }
            finally {
                FileUtil.del(path);
            }
        }
    }




    @GetMapping("/searchTodayCheckin")
    @ApiOperation("search user today checkin data")
    public R searchTodayCheckin(@RequestHeader("token") String token){
        int userId = jwtUtil.getUserId(token);
        HashMap map = checkinService.searchTodayCheckin(userId);
        map.put("attendanceTime", constants.attendanceTime);
        map.put("closingTime",constants.closingTime);
        long days = checkinService.searchCheckinDays(userId);
        map.put("checkinDays",days);

        DateTime hiredate = DateUtil.parse(userService.searchUserHiredate(userId));
        DateTime startDate = DateUtil.beginOfWeek(DateUtil.date());
        if(startDate.isBefore(hiredate)){
            startDate=hiredate;
        }
        DateTime endDate = DateUtil.endOfWeek(DateUtil.date());
        HashMap param = new HashMap();
        param.put("startDate",startDate.toString());
        param.put("endDate",endDate.toString());
        param.put("userId",userId);
        ArrayList<HashMap> list = checkinService.searchWeekCheckin(param);
        map.put("weekCheckin",list);
        return R.ok().put("result",map);
    }


    @PostMapping("/searchMonthCheckin")
    @ApiOperation("Search User monthly checkin data")
    public R searchMonthCheckin(@Valid @RequestBody searchMonthCheckinForm form, @RequestHeader("token") String token){
        int userId = jwtUtil.getUserId(token);
        DateTime hiredate = DateUtil.parse(userService.searchUserHiredate(userId));
        String month = form.getMonth() < 10 ? "0" + form.getMonth() : form.getMonth().toString();
        DateTime startDate = DateUtil.parse(form.getYear() + "-" + month + "-01");
        if(startDate.isBefore(DateUtil.beginOfMonth(hiredate))){
            throw new EmosException("You can only search data after this month");
        }
        if(startDate.isBefore(hiredate)){
            startDate = hiredate;
        }
        DateTime endDate = DateUtil.endOfMonth(startDate);
        HashMap hashMap = new HashMap();
        hashMap.put("userId",userId);
        hashMap.put("startDate",startDate.toString());
        hashMap.put("endDate",endDate.toString());
        ArrayList<HashMap> list = checkinService.searchMonthCheckin(hashMap);
        int sum_1= 0, sum_2 = 0, sum_3 = 0;
        for(HashMap<String,String> one : list){
            String type = one.get("type");
            String status = one.get("status");
            if("WorkDay".equals(type)){
                if("Normal".equals(status)){
                    sum_1++;
                }
                else if("Late".equals(status)){
                    sum_2++;
                }
                else if("Absent".equals(status)){
                    sum_3++;
                }
            }
        }
        return R.ok().put("list",list).put("sum_1",sum_1).put("sum_2",sum_2).put("sum_3",sum_3);
    }
}
