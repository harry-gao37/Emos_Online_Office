package com.yifu.emos.db.dao;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import com.yifu.emos.db.pojo.MessageEntity;
import com.yifu.emos.db.pojo.MessageRefEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Repository
public class MessageDao {
    @Autowired
    private MongoTemplate mongoTemplate;

    public String insert(MessageEntity entity) {
        Date sendTime = entity.getSendTime();
        //can change to other time
        sendTime = DateUtil.offset(sendTime, DateField.HOUR, 8);
        entity.setSendTime(sendTime);
        entity = mongoTemplate.save(entity);
        return entity.get_id();
    }

    public List<HashMap> searchMessageByPage(int userId, long start, int length) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("$toString", "$_id");
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.addFields().addField("id").withValue(jsonObject).build(),
                Aggregation.lookup("message_ref", "id", "messageId", "ref"),
                //notice after match, ref only contains userId messageref
                Aggregation.match(Criteria.where("ref.receiverId").is(userId)),
                Aggregation.sort(Sort.by(Sort.Direction.DESC, "sendTime")),
                Aggregation.skip(start),
                Aggregation.limit(length)
        );

        AggregationResults<HashMap> message = mongoTemplate.aggregate(aggregation, "message", HashMap.class);

        //all messages belong to a user
        List<HashMap> results = message.getMappedResults();
        results.forEach(one -> {
            List<MessageRefEntity> ref = (List<MessageRefEntity>) one.get("ref");
            MessageRefEntity entity = ref.get(0);
            boolean readFlag = entity.getReadFlag();
            String id = entity.get_id();
            one.put("readFlag", readFlag);
            //helping to retrieve message corresponding to receiver because we do not want to delete original message
            one.put("refId", id);
            //ref is combined field
            one.remove("ref");
            //remove receiver corresponding to message
            one.remove("_id");
            Date sendTime = (Date) one.get("sendTime");
            sendTime = DateUtil.offset(sendTime, DateField.HOUR, -8);

            //if message is sent today: only send time otherwise sent date
            String today = DateUtil.today();
            if (today.equals(DateUtil.date(sendTime).toDateStr())) {
                one.put("sendTime", DateUtil.format(sendTime, "HH:mm"));
            } else {
                one.put("sendTime", DateUtil.format(sendTime, "yyyy/MM/dd"));
            }
        });

        return results;

    }

public HashMap searchMessageById(String id){
    HashMap map = mongoTemplate.findById(id, HashMap.class, "message");
    Date sendTime = (Date) map.get("sendTime");
    sendTime = DateUtil.offset(sendTime, DateField.HOUR,-8);
    map.replace("sendTime", DateUtil.format(sendTime, "yyyy-MM-dd HH:mm"));
    return map;
}


}
