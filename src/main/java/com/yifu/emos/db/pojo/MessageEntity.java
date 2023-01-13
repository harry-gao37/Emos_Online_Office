package com.yifu.emos.db.pojo;

import lombok.Data;
import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * @auther YIFU GAO
 * @date 2023/01/10/20:56
 * File Info:
 */
@Data
@Document(collation = "message")
public class MessageEntity implements Serializable {
    @Id
    private String _id;

    @Indexed(unique = true)
    private String uuid;

    @Indexed
    private Integer senderId;


    private String senderPhoto = "";


    private String senderName;

    private String msg;

    @Indexed
    private Date sendTime;
}
