package com.yifu.emos.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * @auther YIFU GAO
 * @date 2023/01/07/22:22
 * File Info:
 */
@Component
@Scope("prototype")
public class EmailTask implements Serializable {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${emos.email.system}")
    private String mailbox;

    @Async
    public void sendAsync(SimpleMailMessage message){
        message.setFrom(mailbox);
        javaMailSender.send(message);
    }
}
