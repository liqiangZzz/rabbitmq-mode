package com.lq.rpc;

import com.lq.utils.RabbitMQConnectionUtil;
import com.rabbitmq.client.*;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @program: rabbitmq-mode
 * @pageName com.lq.rpc
 * @className Consumer
 * @description: rpc模式 服务端
 * @author: liqiang
 * @create: 2023-08-14 14:40
 **/
public class Consumer {

    public static final String QUEUE_PUBLISHER = "rpc_publisher";
    public static final String QUEUE_CONSUMER = "rpc_consumer";

    @Test
    public void consumer() {
        Connection connection = null;
        Channel channel = null;
        try {
            //1. 获取连接对象
            connection = RabbitMQConnectionUtil.getConnection();
            //2. 构建Channel
            channel = connection.createChannel();
            //3.监听信息
            Channel finalChannel = channel;
            DefaultConsumer callback = new DefaultConsumer(finalChannel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    //处理接收到的消息
                    System.out.println("消费者获取到消息：" + new String(body, StandardCharsets.UTF_8));
                    String resp = "获取到了client发出的请求，这里是响应的信息";
                    //获取消息的UUID
                    String uuid = properties.getCorrelationId();
                    //获取回复队列的名称
                    String respQueueName = properties.getReplyTo();
                    //构建消息的属性，包括UUID
                    AMQP.BasicProperties props = new AMQP.BasicProperties()
                            .builder()
                            .correlationId(uuid)
                            .build();
                    //使用 mandatory 标记，你可以确保消息在无法被正常投递时得到通知，并采取适当的后续处理。
                    finalChannel.basicPublish("", respQueueName,true, props, resp.getBytes());
                    //关闭自动ack确认，要手动确认ack ， multiple 是否多消息消费
                    finalChannel.basicAck(envelope.getDeliveryTag(), false);
                }
            };
            //开始消费消息，第二个参数表示是否自动ack
            channel.basicConsume(QUEUE_PUBLISHER, false, callback);

            //阻塞主线程，保持程序运行
            System.out.println("开始监听队列");
            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //确保通道关闭
            if (channel != null) {
                try {
                    channel.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //确保连接关闭
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
