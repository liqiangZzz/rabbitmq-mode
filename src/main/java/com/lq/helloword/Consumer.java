package com.lq.helloword;

import com.lq.utils.RabbitMQConnectionUtil;
import com.rabbitmq.client.*;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

/**
 * @program: rabbitmq-mode
 * @pageName com.lq.helloword
 * @className Consumer
 * @description: hello word 模式 消费者
 * @author: wbliqiang
 * @create: 2023-08-11 16:15
 **/
public class Consumer {

    public static final String QUEUE_NAME = "hello";

    @Test
    public void consumer() {
        Connection connection = null;
        Channel channel = null;
        try {
            //1. 获取连接对象
            connection = RabbitMQConnectionUtil.getConnection();
            //2. 构建Channel
            channel = connection.createChannel();
            //3.监听消息
            DefaultConsumer callback = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                    //处理接收到的消息
                    System.out.println("消费者获取到消息：" + new String(body, StandardCharsets.UTF_8));
                }
            };
            //callback 用于回调， 为true开启ack自动确认,false 关闭自动ack确认
            channel.basicConsume(Consumer.QUEUE_NAME, true, callback);
            System.out.println("开始监听队列");
            //阻塞当前线程，保持程序运行以监听队列
            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //确保通道和连接被关闭
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
