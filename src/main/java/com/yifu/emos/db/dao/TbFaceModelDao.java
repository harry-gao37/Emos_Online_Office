package com.yifu.emos.db.dao;

import com.yifu.emos.db.pojo.TbFaceModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TbFaceModelDao {
    public String searchFaceModel(int userId);
    public void insert(TbFaceModel faceModel);
    public void deleteFaceModel(int userId);
}