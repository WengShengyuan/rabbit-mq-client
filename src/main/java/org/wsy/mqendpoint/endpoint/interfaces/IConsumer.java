package org.wsy.mqendpoint.endpoint.interfaces;

import org.wsy.mqendpoint.endpoint.EndpointPropertiesWrapper;

public interface IConsumer {
	
	/**
	 * 根据需求创建消费者
	 * @param config 队列配置，与应用相关
	 * @param consumerClass 消费者实现类参照{@link org.wsy.mqendpoint.endpoint.interfaces.thread.AutoAckConsumerThread} 的子类
	 * @param consumerCount 一次创建的消费者线程数量
	 * @throws Exception
	 */
	void generateConsumer(EndpointPropertiesWrapper config,Class consumerClass,int consumerCount) throws Exception;
}
