package com.yifu.emos.service.impl;


import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateRange;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.unit.DataUnit;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.yifu.emos.config.SystemConstants;
import com.yifu.emos.db.dao.*;
import com.yifu.emos.db.pojo.TbCheckin;
import com.yifu.emos.db.pojo.TbFaceModel;
import com.yifu.emos.exception.EmosException;
import com.yifu.emos.service.CheckinService;
import com.yifu.emos.task.EmailTask;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * @auther YIFU GAO
 * @date 2023/01/04/20:11
 * File Info: will implement async tasks
 */

@Service
@Scope("prototype")
@Slf4j
public class CheckinServiceImpl implements CheckinService {

    @Autowired
    private TbUserDao userDao;

    @Autowired
    private SystemConstants constants;

    @Autowired
    private TbHolidaysDao holidaysDao;

    @Autowired
    private TbWorkdayDao workdayDao;

    @Autowired
    private TbCheckinDao checkinDao;


    @Autowired
    private TbFaceModelDao faceModelDao;

    @Autowired
    private EmailTask emailTask;

    @Value("${emos.face.createFaceModelUrl}")
    private String createFaceModelUrl;

    @Value("${emos.face.checkinUrl")
    private String checkinUrl;

    @Value("${emos.email.hr}")
    private String hrEmail;

    @Value("${emos.code}")
    private String code;


    @Override
    public String validCheckIn(int userId, String date) {
        boolean flag1 = holidaysDao.searchTodayIsHolidays() != null ? true : false;
        boolean flag2 = workdayDao.searchTodayIsWorkday() != null ? true : false;

        String type = "Workday";
        if (DateUtil.date().isWeekend()) {
            type = "Holiday";
        }
        if (flag1) {
            type = "Holiday";
        } else if (flag2) {
            type = "Workday";
        }

        if (type.equals("Holiday")) {
            return "Today is holiday! No need to check in";
        } else {
            DateTime now = DateUtil.date();
            String start = DateUtil.today() + " " + constants.attendanceStartTime;
            String end = DateUtil.today() + " " + constants.attendanceEndTime;
            DateTime attendanceStart = DateUtil.parse(start);
            DateTime attendanceEnd = DateUtil.parse(end);
            if (now.isBefore(attendanceStart)) {
                return "Can not check in because you are early";
            } else if (now.isAfter(attendanceEnd)) {
                return "Can not check in because you are late";
            } else {
                HashMap hashMap = new HashMap();
                hashMap.put("userId", userId);
                hashMap.put("date", date);
                hashMap.put("start", start);
                hashMap.put("end", end);
                boolean bool = checkinDao.haveCheckedIn(hashMap) != null ? true : false;
                return bool ? "You have already checked in today!" : "You can check in";
            }
        }
    }

    @Override
    public void checkin(HashMap param) {
        Date d1 = DateUtil.date();
        Date d2 = DateUtil.parse(DateUtil.today() + " " + constants.attendanceTime);
        Date d3 = DateUtil.parse(DateUtil.today() + " " + constants.attendanceEndTime);
        int status = 1;

        //justify whether user check in late
        if (d1.compareTo(d2) <= 0) {
            status = 1;
        } else if (d1.compareTo(d2) > 0 && d1.compareTo(d3) <= 0) {
            status = 2;
        }
        int userId = (Integer) param.get("userId");
        String faceModel = faceModelDao.searchFaceModel(userId);
        if (faceModel == null) {
            throw new EmosException("No Face Model Exists");
        } else {
            String path = (String) param.get("path");
            HttpRequest request = HttpUtil.createPost(checkinUrl);
            request.form("photo", FileUtil.file(path), "targetModel", faceModel);
            request.form("code", code);
            HttpResponse response = request.execute();
            if (response.getStatus() != 200) {
                log.error("Face Model Service Error");
                throw new EmosException("Face Model Service Error");
            }
            String body = response.body();
            if ("无法识别出人脸".equals(body) || "照片中存在多张人脸".equals(body)) {
                throw new EmosException(body);
            } else if ("False".equals(body)) {
                throw new EmosException("Invalid Check in, Not User");
            } else if ("True".equals(body)) {
                String address = (String) param.get("address");

                //TODO 查看是否是在公司进行签到的，如果不是就需要发送邮件进行警告
//                if(!StrUtil.isBlank(city)&&!StrUtil.isBlank(district)){
//                    String code=cityDao.searchCode(city);
//                    try{
//                        String url = "http://m." + code + ".bendibao.com/news/yqdengji/?qu=" + district;
//                        Document document=Jsoup.connect(url).get();
//                        Elements elements=document.getElementsByClass("list-content");
//                        if(elements.size()>0){
//                            Element element=elements.get(0);
//                            String result=element.select("p:last-child").text();
////                            result="高风险";
//                            if("高风险".equals(result)){
//                                risk=3;
//                                //发送告警邮件
//                                HashMap<String,String> map=userDao.searchNameAndDept(userId);
//                                String name = map.get("name");
//                                String deptName = map.get("dept_name");
//                                deptName = deptName != null ? deptName : "";
//                                SimpleMailMessage message=new SimpleMailMessage();
//                                message.setTo(hrEmail);
//                                message.setSubject("员工" + name + "身处高风险疫情地区警告");
//                                message.setText(deptName + "员工" + name + "，" + DateUtil.format(new Date(), "yyyy年MM月dd日") + "处于" + address + "，属于新冠疫情高风险地区，请及时与该员工联系，核实情况！");
//                               //注意这里的异常警告发送
//                                emailTask.sendAsync(message);
//                            }
//                            else if("中风险".equals(result)){
//                                risk=2;
//                            }
//                        }
//                    }catch (Exception e){
//                        log.error("执行异常",e);
//                        throw new EmosException("获取风险等级失败");
//                    }
//                }

                //send notification
                HashMap<String, String> map = userDao.searchNameAndDept(userId);
                String name = map.get("name");
                String deptName = map.get("dept_name");
                deptName = deptName != null ? deptName : "";
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(hrEmail);
                message.setSubject("User" + name + "may be not in company");
                message.setText(deptName + "user" + name + ", " + DateUtil.format(new Date(), "yyyy-mm-dd") + "in" + address + "not check in company");
                emailTask.sendAsync(message);

                //store check info
                String country = (String) param.get("country");
                String province = (String) param.get("province");
                String city = (String) param.get("city");
                String district = (String) param.get("district");

                TbCheckin entity = new TbCheckin();
                entity.setUserId(userId);
                entity.setAddress(address);
                entity.setCountry(country);
                entity.setProvince(province);
                entity.setCity(city);
                entity.setDistrict(district);
                entity.setStatus((byte) status);
//                entity.setRisk(risk);
                entity.setDate(DateUtil.today());
                entity.setCreateTime(d1);
                checkinDao.insert(entity);
            }
        }
    }


    // New User Check in
    @Override
    public void createFaceModel(int userId, String path) {
        HttpRequest request = HttpUtil.createPost(createFaceModelUrl);
        request.form("photo", FileUtil.file(path));
        request.form("code", code);
        HttpResponse response = request.execute();
        String body = response.body();
        if ("无法识别出人脸".equals(body) || "照片中存在多张人脸".equals(body)) {
            throw new EmosException(body);
        } else {
            TbFaceModel entity = new TbFaceModel();
            entity.setUserId(userId);
            entity.setFaceModel(body);
            faceModelDao.insert(entity);
        }
    }

    @Override
    public HashMap searchTodayCheckin(int userId) {
        HashMap map = checkinDao.searchTodayCheckin(userId);
        return map;
    }

    @Override
    public long searchCheckinDays(int userId) {
        long days = checkinDao.searchCheckinDays(userId);
        return days;
    }

    @Override
    public ArrayList<HashMap> searchWeekCheckin(HashMap param) {
        ArrayList<HashMap> checkinList = checkinDao.searchWeekCheckin(param);
        ArrayList<String> holidaysList = holidaysDao.searchHolidaysInRange(param);
        ArrayList<String> workdaysList = workdayDao.searchWorkdayInRange(param);
        DateTime startDate = DateUtil.parseDate(param.get("startDate").toString());
        DateTime endDate = DateUtil.parseDate(param.get("endDate").toString());
        DateRange range = DateUtil.range(startDate, endDate, DateField.DAY_OF_MONTH);
        ArrayList<HashMap> list = new ArrayList<>();
        range.forEach(day -> {
            String date = day.toString("yyyy-MM-dd");
            String type = "WorkDay";
            if (day.isWeekend()) {
                type = "Holiday";
            }
            if (holidaysList != null && holidaysList.contains(day)) {
                type = "Holiday";
            } else if (workdaysList != null && workdaysList.contains(day)) {
                type = "WorkDay";
            }

            //check whether user did not go to work
            String status = "";
            boolean flag = false;
            if (type.equals("WorkDay") && DateUtil.compare(day, DateUtil.date()) <= 0) {
                status = "Absent";
                for (HashMap<String, String> map : checkinList) {
                    if (map.containsValue(date)) {
                        status = map.get("status");
                        flag = true;
                        break;
                    }
                }
                DateTime endTime = DateUtil.parse(DateUtil.today() + constants.attendanceEndTime);
                String today = DateUtil.today();
                if (date.equals(today) && DateUtil.date().isBefore(endTime) && flag == false) {
                    status = "";
                }
            }
            HashMap map = new HashMap();
            map.put("date", date);
            map.put("status", status);
            map.put("type", type);
            map.put("day", day.dayOfWeekEnum());
            list.add(map);
        });


        return list;
    }

    @Override
    public ArrayList<HashMap> searchMonthCheckin(HashMap param) {
        return this.searchWeekCheckin(param);
    }
}
