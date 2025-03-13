package com.lq.utils;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @program: rabbitmq-mode
 * @pageName com.lq.utils
 * @className RabbitMQConnectionUtil
 * @description: rabbitMQ连接工具类
 * @author: liqiang
 * @create: 2023-08-11 13:57
 **/
public class RabbitMQConnectionUtil {
    public static final String RABBITMQ_HOST = "192.168.186.131";

    public static final int RABBITMQ_PORT = 5672;

    public static final String RABBITMQ_USERNAME = "guest";

    public static final String RABBITMQ_PASSWORD = "guest";

    public static final String RABBITMQ_VIRTUAL_HOST = "/";

    /**
     * 构建RabbitMQ的连接对象
     * @return
     * @throws IOException
     * @throws TimeoutException
     */
    public static Connection getConnection() throws IOException, TimeoutException {
        //1. 创建Connection工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();
        //2. 设置RabbitMQ的连接信息
        connectionFactory.setHost(RABBITMQ_HOST);
        connectionFactory.setPort(RABBITMQ_PORT);
        connectionFactory.setUsername(RABBITMQ_USERNAME);
        connectionFactory.setPassword(RABBITMQ_PASSWORD);
        connectionFactory.setVirtualHost(RABBITMQ_VIRTUAL_HOST);
        //3. 返回连接对象
        return connectionFactory.newConnection();
    }
}
