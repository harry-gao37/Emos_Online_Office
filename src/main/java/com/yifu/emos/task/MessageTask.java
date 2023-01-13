package com.yifu.emos.task;

import com.rabbitmq.client.*;
import com.yifu.emos.db.pojo.MessageEntity;
import com.yifu.emos.db.pojo.MessageRefEntity;
import com.yifu.emos.exception.EmosException;
import com.yifu.emos.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @auther YIFU GAO
 * @date 2023/01/11/20:49
 * File Info:
 */
@Component
@Slf4j
public class MessageTask {
    @Autowired
    private ConnectionFactory factory;

    @Autowired
    private MessageService messageService;

    //topic: which queue == user
    public void send(String topic, MessageEntity entity) {
        String id = messageService.insertMessage(entity);
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel();
        ) {
            channel.queueDeclare(topic, true, false, false, null);
            HashMap map = new HashMap();
            map.put("messageId", id);
            AMQP.BasicProperties properties = new AMQP.BasicProperties().builder().headers(map).build();
            channel.basicPublish("", topic, properties, entity.getMsg().getBytes(StandardCharsets.UTF_8));
            log.debug("Successfully Sending Message to MQ");
        } catch (Exception e) {
            log.error("Implementation Exception", e);
            throw new EmosException("Fail to sending message to MQ");
        }
    }

    @Async
    public void sendAsync(String topic, MessageEntity entity) {
        send(topic, entity);
    }

    public int receive(String topic) {
        //receive how many data
        int i = 0;
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel();
        ) {
            channel.queueDeclare(topic, true, false, false, null);
            while (true) {
                GetResponse response = channel.basicGet(topic, false);
                if (response != null) {
                    AMQP.BasicProperties properties = response.getProps();
                    Map<String, Object> map = properties.getHeaders();
                    String messageId = map.get("messageId").toString();
                    byte[] body = response.getBody();
                    String message = new String(body);
                    log.debug("Receiving Message from RabbitMQ: " + message);

                    MessageRefEntity entity = new MessageRefEntity();
                    entity.setMessageId(messageId);
                    entity.setReceiverId(Integer.parseInt(topic));
                    entity.setReadFlag(false);
                    entity.setLastFlag(true);
                    messageService.insertRef(entity);

                    long deliveryTag = response.getEnvelope().getDeliveryTag();
                    channel.basicAck(deliveryTag, false);
                    i++;
                } else {
                    break;
                }

            }
        } catch (Exception e) {
            log.error("Implementation Exception", e);
            throw new EmosException("Fail to receiving message to MQ");
        }
        return i;
    }

    @Async
    public int receiveAsync(String topic) {
        return receive(topic);
    }

    public void deleteQueue(String topic) {
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel();
        ) {
            channel.queueDelete(topic);
            log.debug("Successfully Delete message queue");
        } catch (Exception e) {
            log.error("Implementation Exception", e);
            throw new EmosException("Fail to deleting queue to MQ");
        }
    }

    @Async
    public void deleteQueueAsync(String topic) {
        deleteQueue(topic);
    }


}
