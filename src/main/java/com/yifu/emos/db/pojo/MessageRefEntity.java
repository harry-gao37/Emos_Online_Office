package com.yifu.emos.db.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * @auther YIFU GAO
 * @date 2023/01/10/21:06
 * File Info:
 */
@Data
@Document(collation = "message_ref")
public class MessageRefEntity implements Serializable {
    @Id
    private String _id;

    @Indexed
    private String messageId;

    @Indexed
    private Integer receiverId;

    @Indexed
    private Boolean readFlag;

    @Indexed
    private Boolean lastFlag;
}
