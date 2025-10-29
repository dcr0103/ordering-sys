# Docker环境说明

## RabbitMQ服务

本项目使用Docker Compose来运行RabbitMQ服务。

### 启动服务

```bash
# 进入docker目录
cd docker

# 启动RabbitMQ服务
docker-compose up -d
```

### 访问RabbitMQ

- **AMQP端口**: 5672
- **管理界面**: http://localhost:15672
- **默认用户名**: cy
- **默认密码**: 123456
- **虚拟主机**: cy

### 停止服务

```bash
# 停止并移除容器
docker-compose down

# 停止并移除容器及数据卷
docker-compose down -v
```

### 服务配置

RabbitMQ已经预配置了以下内容：

#### 虚拟主机
- `/` (默认)
- `cy` (项目专用)

#### 队列
- `order.stat.queue` (订单统计队列)
- `order.notify.queue.stat` (统计服务通知队列)
- `order.notify.queue.crm` (CRM服务通知队列)
- `order.notify.queue.inventory` (库存服务通知队列)

#### 交换机
- `order.stat.exchange` (订单统计直连交换机)
- `order.fanout.exchange` (订单通知扇出交换机)

#### 绑定关系
- `order.stat.exchange` -> `order.stat.queue` (routing key: order.stat.#)
- `order.fanout.exchange` -> `order.notify.queue.stat`
- `order.fanout.exchange` -> `order.notify.queue.crm`
- `order.fanout.exchange` -> `order.notify.queue.inventory`