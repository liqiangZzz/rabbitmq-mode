package com.lq.routing;

import com.lq.utils.RabbitMQConnectionUtil;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

/**
 * @program: rabbitmq-mode
 * @pageName com.lq.routing
 * @className Publisher
 * @description: routing模式生产者
 * @author: liqiang
 * @create: 2023-08-14 10:03
 **/
public class Publisher {
    public static final String EXCHANGE_NAME = "routing";
    public static final String QUEUE_NAME1 = "routing-one";
    public static final String QUEUE_NAME2 = "routing-two";

    @Test
    public void publish() {
        Connection connection = null;
        Channel channel = null;
        try {
            //1. 获取连接对象
            connection = RabbitMQConnectionUtil.getConnection();
            //2. 构建Channel
            // 通过连接对象创建一个通道，所有的交互都是通过通道进行的
            channel = connection.createChannel();
            //3.创建交换机
            // 声明一个交换机，指定交换机的名称和类型（此处为DIRECT类型）
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

            //4. 构建队列
            // 声明两个队列，设置队列的名称，不持久化，不独占，不自动删除，没有额外的参数
            channel.queueDeclare(QUEUE_NAME1, false, false, false, null);
            channel.queueDeclare(QUEUE_NAME2, false, false, false, null);

            //5. 绑定交换机和队列，使用的是DIRECT类型的交换机
            // 将队列和交换机进行绑定，并指定路由键，以便交换机知道如何将消息路由到队列
            channel.queueBind(QUEUE_NAME1, EXCHANGE_NAME, "ORANGE");
            channel.queueBind(QUEUE_NAME2, EXCHANGE_NAME, "BLACK");
            channel.queueBind(QUEUE_NAME2, EXCHANGE_NAME, "GREEN");

            //发送消息到交换机
            // 使用mandatory标记，你可以确保消息在无法被正常投递时得到通知，并采取适当的后续处理。
            // 发布消息到交换机，指定交换机、路由键、消息是否持久化、消息内容
            channel.basicPublish(EXCHANGE_NAME, "ORANGE", true,null, "大橙子！".getBytes(StandardCharsets.UTF_8));
            channel.basicPublish(EXCHANGE_NAME, "BLACK", true,null, "黑布林大狸子".getBytes(StandardCharsets.UTF_8));
            channel.basicPublish(EXCHANGE_NAME, "WHITE", true,null, "小白兔！".getBytes(StandardCharsets.UTF_8));
            System.out.println("消息发送成功！");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭通道和连接的资源回收
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
