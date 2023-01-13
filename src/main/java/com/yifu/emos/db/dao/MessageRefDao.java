package com.yifu.emos.db.dao;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.yifu.emos.db.pojo.MessageRefEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;


/**
 * @auther YIFU GAO
 * @date 2023/01/10/23:04
 * File Info:
 */

@Repository
public class MessageRefDao {
    @Autowired
    private MongoTemplate mongoTemplate;

    public String insert(MessageRefEntity entity) {
        MessageRefEntity save = mongoTemplate.save(entity);
        return save.get_id();
    }

    public long searchUnreadCount(int userId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("readFlag").is(false).and("receiverId").is(userId));
        long count = mongoTemplate.count(query, MessageRefEntity.class);
        return count;
    }

    public long searchLastCount(int userId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("lastFlag").is(true).and("receiverId").is(userId));
        Update update = new Update();
        update.set("lastFlag", false);
        UpdateResult result = mongoTemplate.updateMulti(query, update, "message_ref");
        long modifiedCount = result.getModifiedCount();
        return modifiedCount;
    }

    public long updateUnreadMessage(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        Update update = new Update();
        update.set("readFlag", true);
        UpdateResult result = mongoTemplate.updateFirst(query, update, "message_ref");
        long row = result.getModifiedCount();
        return row;
    }

    public long deleteMessageRefById(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        DeleteResult result = mongoTemplate.remove(query, "message_ref");
        long row = result.getDeletedCount();
        return row;
    }

    public long deleteUserMessageRef(int userId){
        Query query = new Query();
        query.addCriteria(Criteria.where("receiverId").is(userId));
        DeleteResult result = mongoTemplate.remove(query, "message_ref");
        long row = result.getDeletedCount();
        return row;
    }

}
