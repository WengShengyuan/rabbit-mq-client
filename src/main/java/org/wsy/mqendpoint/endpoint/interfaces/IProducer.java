package org.wsy.mqendpoint.endpoint.interfaces;

import org.wsy.mqendpoint.endpoint.EndpointPropertiesWrapper;

public interface IProducer {
	
	/**
	 * 生产者接口
	 * @param config 队列配置，与应用相关
	 * @param data 发布的消息
	 * @throws Exception
	 */
	void publish(EndpointPropertiesWrapper config, Object data) throws Exception;
	
}
