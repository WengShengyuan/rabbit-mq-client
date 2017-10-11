# rabbit-mq-client
个人使用的Rabbit-mq的封装，用于不方便使用Spring-data-rabbit等方便的组件的场景，或者用于一些小型项目。te

## 1. 特性
1. 支持多host，随机切换
2. host异常时候即使抛弃该host，同时适时重试连接
3. 采用CommonPools作为连接池
4. 封装好的Publisher和Consumer

## 2. 用法
### 2.1 队列的声明
```java
new SimpleQueueBuilder().init(EndpointPropertiesWrapperFactory
					.builder("testExchange-topic", "testQueue-topic-topic1", "topic")
					.setExchangeType(StaticValue.EXCHANGETYPE.TOPIC));
```
