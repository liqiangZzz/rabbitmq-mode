package com.lq.helloword;

import com.lq.utils.RabbitMQConnectionUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

/**
 * @program: rabbitmq-mode
 * @pageName com.lq.helloword
 * @className Publisher
 * @description: helloword 模式
 * @author: liqiang
 * @create: 2023-08-11 16:01
 **/
public class Publisher {
    public static final String QUEUE_NAME = "hello";

    @Test
    public void publish() {
        Connection connection = null;
        Channel channel = null;
        try {
            //1. 获取连接对象
            connection = RabbitMQConnectionUtil.getConnection();
            //2. 构建Channel
            channel = connection.createChannel();
            //3. 构建队列
            //   参数：队列名称，是否持久化，是否独占，是否自动删除，其他属性
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            //4. 发布消息
            String message = "Hello World模式!";
            // 采用默认交换机 默认不用写
            //使用 mandatory 标记，你可以确保消息在无法被正常投递时得到通知，并采取适当的后续处理。
            channel.basicPublish("", QUEUE_NAME, true,null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println("消息发送成功！");
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
