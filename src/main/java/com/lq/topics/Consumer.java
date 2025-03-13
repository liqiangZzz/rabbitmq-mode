package com.lq.topics;

import com.lq.utils.RabbitMQConnectionUtil;
import com.rabbitmq.client.*;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @program: rabbitmq-mode
 * @pageName com.lq.topics
 * @className consumer
 * @description: topic模式 消费者
 * @author: liqiang
 * @create: 2023-08-14 11:36
 **/
public class Consumer {

    public static final String QUEUE_NAME1 = "topic-one";
    public static final String QUEUE_NAME2 = "topic-two";

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
            Channel finalChannel = channel;
            DefaultConsumer callback = new DefaultConsumer(finalChannel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    //处理接收到的消息
                    System.out.println("消费者获取到消息：" + new String(body, StandardCharsets.UTF_8));
                    //关闭自动ack确认，要手动确认ack ， multiple 是否多消息消费
                    finalChannel.basicAck(envelope.getDeliveryTag(), false);
                }
            };

            //callback 用于回调， 为true开启ack自动确认,fales 关闭自动ack确认，分别监控两个队列
            channel.basicConsume(QUEUE_NAME1, false, callback);
            channel.basicConsume(QUEUE_NAME2, false, callback);
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
