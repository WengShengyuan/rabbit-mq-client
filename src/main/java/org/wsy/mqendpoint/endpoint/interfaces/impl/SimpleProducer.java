package org.wsy.mqendpoint.endpoint.interfaces.impl;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.wsy.mqendpoint.core.MqCore;
import org.wsy.mqendpoint.core.StaticValue;
import org.wsy.mqendpoint.endpoint.EndpointPropertiesWrapper;
import org.wsy.mqendpoint.endpoint.interfaces.IProducer;
import org.wsy.mqendpoint.endpoint.serialization.SimpleSerialization;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ChannelContinuationTimeoutException;
import com.rabbitmq.client.Connection;

public class SimpleProducer implements IProducer {
	private static final Logger logger = LogManager.getLogger(SimpleProducer.class);

	public void publish(EndpointPropertiesWrapper config, Object data) throws Exception {
		config.valid();
		Connection connection = null;
		Channel channel = null;
		try {
			connection = MqCore.getInstance().borrowObject();
			channel = connection.createChannel();
			if (!config.getEndPointType().equals(StaticValue.ENDPOINTTYPE.PRODUCER))
				throw new Exception("wrong endpoit type, producer only!");
			if (config.getBindType().equals(StaticValue.BINDTYPE.QUEUE)) {
				channel.queueDeclare(config.getQueueName(), config.isDurable(), config.isExclusive(),
						config.isAutoDelete(), config.getArguments());
				logger.debug("queue send -> [" + config.getQueueName() + "] : " + data);
				channel.basicPublish(config.getExchangeName(), config.getQueueName(), null,
						SimpleSerialization.toByte(data));
			} else if (config.getBindType().equals(StaticValue.BINDTYPE.EXCHANGE)) {
				channel.exchangeDeclare(config.getExchangeName(), config.getExchangeType());
				logger.debug("exchange send -> [" + config.getExchangeName() + ", " + config.getExchangeType() + ", "
						+ config.getRoutingKey() + "] : " + data);
				channel.basicPublish(config.getExchangeName(), config.getRoutingKey(), null,
						SimpleSerialization.toByte(data));
			} else {
				throw new Exception("wrong bindtype: " + config.getBindType() + ".");
			}

		} catch (Exception e) {
			logger.error("fail to process endpointConfig and channel.", e);
			throw e;
		} finally {
			try {
				MqCore.getInstance().returnObject(connection);
			} catch (Exception e) {
			}
			if (null != channel)
				channel.close();
		}
	}
}
