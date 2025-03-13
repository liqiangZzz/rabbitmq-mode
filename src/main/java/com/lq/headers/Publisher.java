package com.lq.headers;

import com.lq.utils.RabbitMQConnectionUtil;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: rabbitmq-mode
 * @pageName com.lq.headers
 * @className Publiosher
 * @description: headers模式
 * @author: liqiang
 * @create: 2023-08-16 15:28
 **/
public class Publisher {


    private static final String EXCHANGE_NAME = "headers-exchange";
    private static final String QUEUE_NAME = "headers-queue";

    @Test
    public void publish() {
        Connection connection = null;
        Channel channel = null;
        try {
            //1. 获取连接对象
            connection = RabbitMQConnectionUtil.getConnection();
            //2. 构建Channel
            channel = connection.createChannel();
            //3.创建交换机
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.HEADERS);
            //4. 构建队列
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            //5.绑定 交换机和队列
            Map<String, Object> arguments = new HashMap<>();
            arguments.put("x-match", "any");
            arguments.put("name", "jack");
            arguments.put("age", "25");
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "", arguments);

            String msg = "header测试消息！";
            Map<String, Object> headers = new HashMap<>();
            headers.put("name","jack");
            headers.put("age","23");
            AMQP.BasicProperties props = new AMQP.BasicProperties().builder().headers(headers).build();

            //使用 mandatory 标记，你可以确保消息在无法被正常投递时得到通知，并采取适当的后续处理。
            channel.basicPublish(EXCHANGE_NAME, "", true,props, msg.getBytes(StandardCharsets.UTF_8));

            System.out.println("发送消息成功，header = " + headers);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭通道和连接
            if (channel != null) {
                try {
                    channel.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
