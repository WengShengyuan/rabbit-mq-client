package org.wsy.mqendpoint.endpoint;

import org.wsy.mqendpoint.core.StaticValue;

/**
 * 同意队列配置生成器
 * @author WSY
 *
 */
public class EndpointPropertiesWrapperFactory {
	
	public static EndpointPropertiesWrapper buildDefault() {
		return new EndpointPropertiesWrapper();
	}

	public static EndpointPropertiesWrapper producer(){
		EndpointPropertiesWrapper wrapper = new EndpointPropertiesWrapper();
		wrapper.setEndPointType(StaticValue.ENDPOINTTYPE.PRODUCER);
		return wrapper;
	}
	public static EndpointPropertiesWrapper producer(String queueName) {
		EndpointPropertiesWrapper wrapper = new EndpointPropertiesWrapper();
		wrapper.setEndPointType(StaticValue.ENDPOINTTYPE.PRODUCER);
		wrapper.setQueueName(queueName);
		wrapper.setBindType(StaticValue.BINDTYPE.QUEUE);
		return wrapper;
	}
	public static EndpointPropertiesWrapper producer(String exchangeName,String routingKey) {
		EndpointPropertiesWrapper wrapper = new EndpointPropertiesWrapper();
		wrapper.setEndPointType(StaticValue.ENDPOINTTYPE.PRODUCER);
		wrapper.setExchangeName(exchangeName);
		wrapper.setRoutingKey(routingKey);
		wrapper.setBindType(StaticValue.BINDTYPE.EXCHANGE);
		return wrapper;
	}
	
	
	
	
	public static EndpointPropertiesWrapper consumer(String queueName){
		EndpointPropertiesWrapper wrapper = new EndpointPropertiesWrapper();
		wrapper.setEndPointType(StaticValue.ENDPOINTTYPE.CONSUMER);
		wrapper.setQueueName(queueName);
		wrapper.setBindType(StaticValue.BINDTYPE.QUEUE);
		return wrapper;
	}

	
	public static EndpointPropertiesWrapper builder(String queueName){
		EndpointPropertiesWrapper wrapper = new EndpointPropertiesWrapper();
		wrapper.setEndPointType(StaticValue.ENDPOINTTYPE.BUILDER);
		wrapper.setQueueName(queueName);
		wrapper.setBindType(StaticValue.BINDTYPE.QUEUE);
		return wrapper;
	}
	public static EndpointPropertiesWrapper builder(String exchangeName,String queueName,String routingKey){
		EndpointPropertiesWrapper wrapper = new EndpointPropertiesWrapper();
		wrapper.setEndPointType(StaticValue.ENDPOINTTYPE.BUILDER);
		wrapper.setExchangeName(exchangeName);
		wrapper.setQueueName(queueName);
		wrapper.setRoutingKey(routingKey);
		wrapper.setBindType(StaticValue.BINDTYPE.EXCHANGE);
		return wrapper;
	}
	
}
