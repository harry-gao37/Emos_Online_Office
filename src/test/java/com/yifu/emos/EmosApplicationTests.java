package com.yifu.emos;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.yifu.emos.db.pojo.MessageEntity;
import com.yifu.emos.db.pojo.MessageRefEntity;
import com.yifu.emos.db.pojo.TbMeeting;
import com.yifu.emos.service.MeetingService;
import com.yifu.emos.service.MessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
class EmosApplicationTests {
    @Autowired
    private MessageService messageService;

    @Autowired
    private MeetingService meetingService;

    @Test
    void contextLoads() {
        for (int i = 0; i <= 100; i++) {
            MessageEntity message = new MessageEntity();
            message.setUuid(IdUtil.simpleUUID());
            message.setSenderId(0);
            message.setSenderName("System Message");
            message.setMsg("This is the " + i + "Test Message");
            message.setSendTime(new Date());
            String id = messageService.insertMessage(message);

            MessageRefEntity ref = new MessageRefEntity();
            ref.setMessageId(id);
            ref.setReceiverId(11); //look admin id
            ref.setLastFlag(true);
            ref.setReadFlag(false);
            messageService.insertRef(ref);
        }
    }

    @Test
    void createMeetingData(){
        for (int i = 0; i <= 100; i++) {
            TbMeeting meeting=new TbMeeting();
            meeting.setId((long)i);
            meeting.setUuid(IdUtil.simpleUUID());
            meeting.setTitle("Test Meeting"+i);
            meeting.setCreatorId(12L); //ROOT User ID
            meeting.setDate(DateUtil.today());
            meeting.setPlace("Online Meeting");
            meeting.setStart("08:30");
            meeting.setEnd("10:30");
            meeting.setType((short) 1);
            meeting.setMembers("[12,16]");
            meeting.setDesc("Meeting for test");
            meeting.setInstanceId(IdUtil.simpleUUID());
            meeting.setStatus((short)3);
            meeting.setCreateTime(new Date());
            meetingService.insertMeeting(meeting);
        }
    }

}



