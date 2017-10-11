# rabbit-mq-client
个人使用的Rabbit-mq的封装，用于不方便使用Spring-data-rabbit等方便的组件的场景，或者用于一些小型项目。

## 1. 特性
1. 支持多host，随机切换
2. host异常时候即时抛弃该host，同时适时重试连接
3. 采用CommonPools作为连接池
4. 封装好的Publisher和Consumer
5. 消费者封装与消费逻辑处理分离，支持在基本消费者模型上自定义消费行为

## 2. 用法
```java
/* 支持直接声明QUEUE和EXCHANGE（DIRECT, FANOUT, TOPIC） */

// 例子1：对于EXCHANGE - TOPIC的用法
// 1-1 exchange和队列声明
new SimpleQueueBuilder().init(EndpointPropertiesWrapperFactory
					.builder("testExchange-topic", "testQueue-topic-topic1", "topic")
					.setExchangeType(StaticValue.EXCHANGETYPE.TOPIC));
// 1-2 发布内容
new SimpleProducer().publish(EndpointPropertiesWrapperFactory.producer("testExchange-topic", "topic")
								.setExchangeType(StaticValue.EXCHANGETYPE.TOPIC),"testMessage:"+new Date());
// 1-3 消费内容
new SimpleConsumer().generateConsumer(EndpointPropertiesWrapperFactory.consumer("testQueue-topic-topic1"), SysoutConsumer.class, 2);

// 例子2：直接声明队列使用
new SimpleQueueBuilder().init(EndpointPropertiesWrapperFactory.builder("queue"));
//.....其它请看代码
```

