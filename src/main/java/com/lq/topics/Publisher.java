package com.lq.topics;

import com.lq.utils.RabbitMQConnectionUtil;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

/**
 * @program: rabbitmq-mode
 * @pageName com.lq.topics
 * @className Publisher
 * @description: topic 模式  生产者
 * @author: liqiang
 * @create: 2023-08-14 11:31
 **/
public class Publisher {
    public static final String EXCHANGE_NAME = "topic";
    public static final String QUEUE_NAME1 = "topic-one";
    public static final String QUEUE_NAME2 = "topic-two";


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
            // 声明一个交换机，指定交换机的名称和类型（此处使用的是TOPIC类型）
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

            //4. 构建队列
            // 声明两个队列，指定队列的名称和其他参数（非持久化、非排他、非自动删除）
            channel.queueDeclare(QUEUE_NAME1, false, false, false, null);
            channel.queueDeclare(QUEUE_NAME2, false, false, false, null);

            //5. 绑定交换机和队列，
            // TOPIC类型的交换机在和队列绑定时，需要以aaa.bbb.ccc..方式编写routingkey
            // 其中有两个特殊字符：*（相当于占位符），#（相当通配符）
            channel.queueBind(QUEUE_NAME1, EXCHANGE_NAME, "*.orange.*");
            channel.queueBind(QUEUE_NAME2, EXCHANGE_NAME, "*.*.rabbit");
            channel.queueBind(QUEUE_NAME2, EXCHANGE_NAME, "lazy.#");

            //发送消息到交换机
            // 使用指定的交换机、routing key和消息内容发送消息
            // 此处使用了mandatory标记来确保消息在无法被正常投递时能够得到通知,并采取适当的后续处理。
            channel.basicPublish(EXCHANGE_NAME, "big.orange.rabbit", true,null, "大橙兔！".getBytes(StandardCharsets.UTF_8));
            channel.basicPublish(EXCHANGE_NAME, "small.white.rabbit", true,null, "小白兔".getBytes(StandardCharsets.UTF_8));
            channel.basicPublish(EXCHANGE_NAME, "lazy.dog.dog", true,null, "懒狗！".getBytes(StandardCharsets.UTF_8));
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
