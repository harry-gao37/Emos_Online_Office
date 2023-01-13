package com.yifu.emos.service.impl;

import cn.hutool.json.JSONArray;
import com.yifu.emos.db.dao.TbMeetingDao;
import com.yifu.emos.db.pojo.TbMeeting;
import com.yifu.emos.exception.EmosException;
import com.yifu.emos.service.MeetingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @auther YIFU GAO
 * @date 2023/01/12/20:39
 * File Info:
 */
@Service
@Slf4j
public class MeetingServiceImpl implements MeetingService {

    @Autowired
    private TbMeetingDao meetingDao;

    @Override
    public void insertMeeting(TbMeeting entity) {
        int row = meetingDao.insertMeeting(entity);
        if (row != 1) {
            throw new EmosException("Fail to create meeting");
        }

        //TODO 审批工作流
    }

    @Override
    public ArrayList<HashMap> searchMyMeetingListByPage(HashMap param) {

        ArrayList<HashMap> list = meetingDao.searchMyMeetingListByPage(param);

        String date = null;
        ArrayList resultList = new ArrayList();
        HashMap resultMap = null;
        JSONArray array = null;
        for (HashMap map : list) {
            String temp = map.get("date").toString();
            //if date is change, we need to create a new date list
            if (!temp.equals(date)) {
                date = temp;
                resultMap = new HashMap();
                resultMap.put("date", date);
                array = new JSONArray();
                //this pass a reference
                resultMap.put("list", array);
                resultList.add(resultMap);
            }
            array.put(map);
        }
        return resultList;
    }
}
