# 订单系统

## 简介
订单系统是一个基于Spring Boot的微服务应用，提供订单管理、通知推送、WebSocket通信等功能。

## 技术栈
- Spring Boot 3.x
- WebSocket
- RabbitMQ
- Lombok
- JUnit 5 & Mockito

## 功能模块

### 订单服务
- 订单创建、查询、更新
- 订单状态管理
- 订单事件发布

### WebSocket通信
- APP端WebSocket连接
- 门店端WebSocket连接
- 简单测试端点
- 会话管理和心跳检测

### 消息通知
- 用户通知（订单状态变更等）
- 门店通知（新订单等）
- RabbitMQ消息队列集成

## 事件机制

系统使用Spring Event机制实现内部事件处理：

- `OrderOperationEvent`: 订单操作事件（创建、取消、更新等）
- `UserNotificationListener`: 用户通知监听器
- `StoreNotificationListener`: 门店通知监听器
- `PaymentNotificationListener`: 支付通知监听器
- 
## API接口

### WebSocket端点
- `/ws/app` - APP端WebSocket连接端点
- `/ws/store` - 门店端WebSocket连接端点

### REST API
- `POST /api/orders` - 创建订单
- `GET /api/orders/{id}` - 查询订单详情
- `POST /api/orders/status/{id}` - 更新订单状态

## 配置说明
- 服务端口: 8881
- WebSocket路径前缀: /order-service
- RabbitMQ集成配置

## Docker环境

项目包含Docker Compose配置，用于快速启动开发环境：

- RabbitMQ消息队列服务
- 管理界面端口: 15672
- AMQP协议端口: 5672
- 默认用户名: cy
- 默认密码: 123456
- 虚拟主机: cy

启动命令：
```bash
cd docker
# 可选：复制.env.example到.env并修改配置
cp .env.example .env
# 启动服务
docker-compose up -d
```

## 环境变量配置

为了安全起见，敏感信息（如数据库密码、API密钥等）应通过环境变量配置。
各服务均支持通过.env文件配置环境变量：

- `RABBITMQ_HOST`: RabbitMQ服务器地址
- `RABBITMQ_PORT`: RabbitMQ端口
- `RABBITMQ_USERNAME`: RabbitMQ用户名
- `RABBITMQ_PASSWORD`: RabbitMQ密码
- `RABBITMQ_VIRTUAL_HOST`: RabbitMQ虚拟主机

每个服务目录下都包含.env文件示例，可以复制并修改为实际配置。

**注意**：项目已集成[dotenv-java](https://github.com/cdimascio/dotenv-java)库，可自动读取.env文件中的环境变量。应用启动时会自动加载.env文件中的配置。

如果需要手动设置环境变量，可以通过以下方式：

1. 在系统环境中设置变量
2. 在启动应用时通过命令行参数指定：
   ```bash
   export RABBITMQ_HOST=120.78.138.7
   export RABBITMQ_PORT=5672
   # 或者
   java -jar app.jar --RABBITMQ_HOST=120.78.138.7 --RABBITMQ_PORT=5672
   ```
