package com.lq.confirms;

import com.lq.utils.RabbitMQConnectionUtil;
import com.rabbitmq.client.*;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @program: rabbitmq-mode
 * @pageName com.lq.confirms
 * @className Publisher
 * @description:
 * @author: liqiang
 * @create: 2023-08-15 14:32
 **/
public class Publisher {

    public static final String QUEUE_NAME = "confirms";

    @Test
    public void publish() {
        Connection connection = null;
        Channel channel = null;
        try {
            //1. 获取连接对象
            connection = RabbitMQConnectionUtil.getConnection();
            //2. 构建Channel
            channel = connection.createChannel();
            //3. 构建队列   durable 设置队列可持久化
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);

            //4. 开启confirms
            channel.confirmSelect();
            // 添加确认监听器，处理消息发送到Exchange时的成功和失败情况
            channel.addConfirmListener(new ConfirmListener() {
                @Override
                public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                    System.out.println("消息成功的发送到Exchange！");
                }

                @Override
                public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                    System.out.println("消息没有发送到Exchange，尝试重试，或者保存到数据库做其他补偿操作！");
                }
            });

            // 添加返回监听器，处理消息没有路由到指定队列的情况
            channel.addReturnListener(new ReturnListener() {
                @Override
                public void handleReturn(int replyCode, String replyText, String exchange, String routingKey, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    System.out.println("消息没有路由到指定队列，做其他的补偿措施！！");
                    System.out.println("----- handle return -----");
                    System.out.println("replyCode = " + replyCode);
                    System.out.println("replyText = " + replyText);
                    System.out.println("exchange = " + exchange);
                    System.out.println("properties = " + properties);
                    System.out.println("msg = " + new String(body));
                }
            });

            // 设置消息的持久化！
            AMQP.BasicProperties props = new AMQP.BasicProperties()
                    .builder()
                    .deliveryMode(2)
                    .build();

            //5. 发布消息
            String message = "confirms!!!!";
            // 采用默认交换机 默认不用写
            channel.basicPublish("", QUEUE_NAME, true, props, message.getBytes(StandardCharsets.UTF_8));
            System.out.println("消息发送成功！");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 确保通道和连接在finally块中关闭，避免资源泄露
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
