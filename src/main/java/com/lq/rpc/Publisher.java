package com.lq.rpc;

import com.lq.utils.RabbitMQConnectionUtil;
import com.rabbitmq.client.*;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * @program: rabbitmq-mode
 * @pageName com.lq.rpc
 * @className Publisher
 * @description: rpc模式 客户端
 * @author: liqiang
 * @create: 2023-08-14 14:27
 **/
public class Publisher {

    public static final String QUEUE_PUBLISHER = "rpc_publisher";
    public static final String QUEUE_CONSUMER = "rpc_consumer";

    @Test
    public void publish() {
        Connection connection = null;
        Channel channel = null;
        try {
            //1. 获取连接对象
            connection = RabbitMQConnectionUtil.getConnection();
            //2. 构建Channel
            //通过连接对象创建一个通道，后续的操作都是基于通道进行的
            channel = connection.createChannel();

            //3. 构建队列
            //声明（创建）两个队列，一个用于发送消息，一个用于接收回复消息（非持久化、非排他、非自动删除）
            channel.queueDeclare(QUEUE_PUBLISHER, false, false, false, null);
            channel.queueDeclare(QUEUE_CONSUMER, false, false, false, null);

            //4.发布消息
            //构建要发送的消息内容和唯一标识符
            String message = "hello rpc";
            String uuid = UUID.randomUUID().toString();

            //设置消息的属性，包括回复队列和相关ID，以便后续处理回复消息
            AMQP.BasicProperties props = new AMQP.BasicProperties()
                    .builder()
                    .replyTo(QUEUE_CONSUMER)
                    .correlationId(uuid)
                    .build();

            //发送消息
            //使用 mandatory 标记，你可以确保消息在无法被正常投递时得到通知，并采取适当的后续处理。
            channel.basicPublish("", QUEUE_PUBLISHER, true,props, message.getBytes(StandardCharsets.UTF_8));

            //监听消息
            //准备消费回复队列中的消息
            Channel finalChannel = channel;

            //采用基础消费方式监听队列，当有消息到达时进行处理
            channel.basicConsume(QUEUE_CONSUMER, false, new DefaultConsumer(finalChannel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    //处理接收到的消息，确保是对应的回复消息
                    String correlationId = properties.getCorrelationId();
                    if (correlationId != null && correlationId.equalsIgnoreCase(uuid)) {
                        System.out.println("消费者获取到消息：" + new String(body, StandardCharsets.UTF_8));
                    }
                    //确认消息已被成功消费
                    finalChannel.basicAck(envelope.getDeliveryTag(), false);
                }
            });

            System.out.println("消息发送成功！");
            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //资源关闭，确保通道和连接在使用完毕后被正确关闭
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

