package com.yifu.emos.db.dao;

import com.yifu.emos.db.pojo.TbMeeting;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;

@Mapper
public interface TbMeetingDao {
    public int insertMeeting(TbMeeting entity);
    public ArrayList<HashMap> searchMyMeetingListByPage(HashMap param);
}