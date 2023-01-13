package com.yifu.emos.service;

import com.yifu.emos.db.pojo.TbMeeting;

import java.util.ArrayList;
import java.util.HashMap;

public interface MeetingService {
    public void insertMeeting(TbMeeting entity);
    public ArrayList<HashMap> searchMyMeetingListByPage(HashMap param);


}
