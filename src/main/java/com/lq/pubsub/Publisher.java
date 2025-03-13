package com.lq.pubsub;

import com.lq.utils.RabbitMQConnectionUtil;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

/**
 * @program: rabbitmq-mode
 * @pageName com.lq.pubsub
 * @className Publisher
 * @description: Publish/Subscribe 模式 生产者
 * @author: liqiang
 * @create: 2023-08-11 17:22
 **/
public class Publisher {
    public static final String EXCHANGE_NAME = "pubsub";
    public static final String QUEUE_NAME1 = "pubsub-one";
    public static final String QUEUE_NAME2 = "pubsub-two";

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
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);

            //4. 构建队列
            channel.queueDeclare(QUEUE_NAME1, false, false, false, null);
            channel.queueDeclare(QUEUE_NAME2, false, false, false, null);

            //5. 绑定交换机和队列，使用的是FANOUT类型的交换机，绑定方式是直接绑定，和 routingKey 无关
            channel.queueBind(QUEUE_NAME1, EXCHANGE_NAME, "");
            channel.queueBind(QUEUE_NAME2, EXCHANGE_NAME, "12EQ3E");

            //发送消息到交换机 和 routingKey 无关
            //使用 mandatory 标记，你可以确保消息在无法被正常投递时得到通知，并采取适当的后续处理。
            channel.basicPublish(EXCHANGE_NAME, "sqadas", true,null, "pubsub 测试！！".getBytes(StandardCharsets.UTF_8));
            System.out.println("消息发送成功！");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
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
