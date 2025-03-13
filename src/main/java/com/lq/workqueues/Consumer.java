package com.lq.workqueues;

import com.lq.utils.RabbitMQConnectionUtil;
import com.rabbitmq.client.*;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @program: rabbitmq-mode
 * @pageName com.lq.workqueues
 * @className Consumer
 * @description: work queue 模式 消费者
 * @author: liqiang
 * @create: 2023-08-11 16:35
 **/
public class Consumer {
    public static final String QUEUE_NAME = "work";

    @Test
    public void consumer() {
        Connection connection = null;
        Channel channel = null;
        try {
            //1. 获取连接对象
            connection = RabbitMQConnectionUtil.getConnection();
            //2. 构建Channel
            channel = connection.createChannel();
            //3. 设置消息的流控,每次拿多少消息
            channel.basicQos(3);
            //4.监听消息
            Channel finalChannel = channel;
            DefaultConsumer callback = new DefaultConsumer(finalChannel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("消费者1获取到消息：" + new String(body, StandardCharsets.UTF_8));
                    //关闭自动ack确认，要手动确认ack ， multiple 是否多消息消费
                    finalChannel.basicAck(envelope.getDeliveryTag(), false);
                }
            };
            //callback 用于回调， 为true开启ack自动确认,fales 关闭自动ack确认
            channel.basicConsume(QUEUE_NAME, false, callback);
            System.out.println("开始监听队列");

            System.in.read();
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

    @Test
    public void consumer2() {
        Connection connection = null;
        Channel channel = null;
        try {
            //1. 获取连接对象
            connection = RabbitMQConnectionUtil.getConnection();
            //2. 构建Channel
            channel = connection.createChannel();
            //3. 设置消息的流控,每次拿多少消息
            channel.basicQos(3);
            //4. 监听消息
            Channel finalChannel = channel;
            DefaultConsumer callback = new DefaultConsumer(finalChannel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("消费者2号-获取到消息：" + new String(body, StandardCharsets.UTF_8));
                    //关闭自动ack确认，要手动确认ack ， multiple 是否多消息消费
                    finalChannel.basicAck(envelope.getDeliveryTag(), false);
                }
            };
            //ack  为true开启ack自动确认,false 关闭自动ack确认， callback 用于回调
            channel.basicConsume(QUEUE_NAME, false, callback);
            System.out.println("开始监听队列");

            System.in.read();
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
