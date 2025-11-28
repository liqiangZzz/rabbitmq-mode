# RabbitMQ 模式示例项目

本项目是一个基于 Java 的 RabbitMQ 消息队列示例集合，展示了 RabbitMQ 的六种基本工作模式的实现方式。

## 项目概述

该项目演示了如何使用 RabbitMQ 实现不同的消息传递模式，包括 Hello World、Work Queues、Publish/Subscribe、Routing、Topics 和 RPC 等模式。

## 技术栈

- Java 8
- RabbitMQ Client 5.16.0
- Maven 项目管理
- JUnit 4 测试框架

## 项目结构

```
src/main/java/com/lq/
├── confirms/          # 消息确认模式
├── headers/           # Headers Exchange 模式
├── helloword/         # Hello World 模式（简单模式）
├── pubsub/            # Publish/Subscribe 模式（发布订阅模式）
├── routing/           # Routing 模式
├── rpc/               # RPC 模式
├── topics/            # Topics 模式
├── utils/             # 工具类
└── workqueues/        # Work Queues 模式
```

## 配置要求

### RabbitMQ 服务器配置

项目中的 [RabbitMQConnectionUtil.java](file:///Users/Java/project/rabbitmq/rabbitmq-mode/src/main/java/com/lq/utils/RabbitMQConnectionUtil.java) 文件包含了默认的连接配置：

- 主机地址: `192.168.186.131`
- 端口: `5672`
- 用户名: `guest`
- 密码: `guest`
- 虚拟主机: `/`

请根据实际情况修改这些配置参数。

## 六种工作模式说明

### 1. Hello World 模式（简单模式）

最简单的消息传递模式，一个生产者向队列发送消息，一个消费者从队列接收消息。

相关类：
- [Publisher.java](file:///Users/Java/project/rabbitmq/rabbitmq-mode/src/main/java/com/lq/helloword/Publisher.java) - 消息生产者
- [Consumer.java](file:///Users/Java/project/rabbitmq/rabbitmq-mode/src/main/java/com/lq/helloword/Consumer.java) - 消息消费者

### 2. Work Queues 模式（工作队列模式）

允许多个消费者监听同一个队列，消息会被轮询分发给不同的消费者。

相关类：
- [Publisher.java](file:///Users/Java/project/rabbitmq/rabbitmq-mode/src/main/java/com/lq/workqueues/Publisher.java) - 消息生产者
- [Consumer.java](file:///Users/Java/project/rabbitmq/rabbitmq-mode/src/main/java/com/lq/workqueues/Consumer.java) - 消息消费者

### 3. Publish/Subscribe 模式（发布订阅模式）

使用 Fanout 类型的交换机，将消息广播到所有绑定的队列。

相关类：
- [Publisher.java](file:///Users/Java/project/rabbitmq/rabbitmq-mode/src/main/java/com/lq/pubsub/Publisher.java) - 消息生产者
- [Consumer.java](file:///Users/Java/project/rabbitmq/rabbitmq-mode/src/main/java/com/lq/pubsub/Consumer.java) - 消息消费者

### 4. Routing 模式（路由模式）

使用 Direct 类型的交换机，根据路由键精确匹配队列进行消息投递。

相关类：
- [Publisher.java](file:///Users/Java/project/rabbitmq/rabbitmq-mode/src/main/java/com/lq/routing/Publisher.java) - 消息生产者
- [Consumer.java](file:///Users/Java/project/rabbitmq/rabbitmq-mode/src/main/java/com/lq/routing/Consumer.java) - 消息消费者

### 5. Topics 模式（主题模式）

使用 Topic 类型的交换机，支持通配符匹配的路由键模式。

相关类：
- [Publisher.java](file:///Users/Java/project/rabbitmq/rabbitmq-mode/src/main/java/com/lq/topics/Publisher.java) - 消息生产者
- [Consumer.java](file:///Users/Java/project/rabbitmq/rabbitmq-mode/src/main/java/com/lq/topics/Consumer.java) - 消息消费者

### 6. RPC 模式（远程过程调用模式）

实现请求-响应式的通信模式，客户端发送请求并等待服务端的响应。

相关类：
- [Publisher.java](file:///Users/Java/project/rabbitmq/rabbitmq-mode/src/main/java/com/lq/rpc/Publisher.java) - 客户端
- [Consumer.java](file:///Users/Java/project/rabbitmq/rabbitmq-mode/src/main/java/com/lq/rpc/Consumer.java) - 服务端

## 运行方式

1. 确保 RabbitMQ 服务器已启动并可访问
2. 修改 [RabbitMQConnectionUtil.java](file:///Users/Java/project/rabbitmq/rabbitmq-mode/src/main/java/com/lq/utils/RabbitMQConnectionUtil.java) 中的连接参数以匹配您的环境
3. 使用 Maven 编译项目：
   ```bash
   mvn clean compile
   ```
4. 运行相应的测试方法来启动生产者或消费者

例如，运行 Hello World 模式的生产者：
```bash
mvn test -Dtest=com.lq.helloword.Publisher#publish
```

运行 Hello World 模式的消费者：
```bash
mvn test -Dtest=com.lq.helloword.Consumer#consumer
```

## 注意事项

1. 所有示例均使用 JUnit 测试方法，可通过 Maven 运行
2. 消费者程序通常包含 `System.in.read()` 来保持程序运行状态以监听消息
3. 在生产环境中，请根据实际需求调整连接配置和异常处理逻辑
4. 建议在运行示例前熟悉 RabbitMQ 的基本概念和工作原理

## 许可证

本项目仅供学习和参考使用。