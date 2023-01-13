package com.yifu.emos.db.dao;

import com.yifu.emos.db.pojo.TbWorkday;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;

@Mapper
public interface TbWorkdayDao {
    public Integer searchTodayIsWorkday();
    public ArrayList<String> searchWorkdayInRange(HashMap param);
}