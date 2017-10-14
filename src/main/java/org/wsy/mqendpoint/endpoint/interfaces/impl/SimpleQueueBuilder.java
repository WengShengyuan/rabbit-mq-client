package org.wsy.mqendpoint.endpoint.interfaces.impl;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.wsy.mqendpoint.core.MqCore;
import org.wsy.mqendpoint.core.StaticValue;
import org.wsy.mqendpoint.endpoint.EndpointPropertiesWrapper;
import org.wsy.mqendpoint.endpoint.interfaces.IQueueBuilder;

public class SimpleQueueBuilder implements IQueueBuilder {
	private static final Logger logger = LogManager.getLogger(SimpleQueueBuilder.class);

	public void init(EndpointPropertiesWrapper config) throws Exception {
		config.valid();
		Connection connection = null;
		Channel channel = null;
		try {
			connection = MqCore.getInstance().borrowObject();
			channel = connection.createChannel();
			if (!config.getEndPointType().equals(StaticValue.ENDPOINTTYPE.BUILDER))
				throw new Exception("wrong endpoit type, builder only!");
			if (config.getBindType().equals(StaticValue.BINDTYPE.QUEUE)) {
				channel.queueDeclare(config.getQueueName(), config.isDurable(), config.isExclusive(),
						config.isAutoDelete(), config.getArguments());
				logger.debug("queue declare : [" + config.getQueueName() + "]");
			} else if (config.getBindType().equals(StaticValue.BINDTYPE.EXCHANGE)) {
				channel.queueDeclare(config.getQueueName(), config.isDurable(), config.isExclusive(),
						config.isAutoDelete(), config.getArguments());
				channel.exchangeDeclare(config.getExchangeName(), config.getExchangeType(), config.isDurable(),
						config.isAutoDelete(), config.getArguments());
				channel.queueBind(config.getQueueName(), config.getExchangeName(), config.getRoutingKey());
				logger.debug("queue bind to exchange : [" + config.getExchangeName() + ", " + config.getExchangeType()
						+ "]@" + config.getQueueName() + "#" + config.getRoutingKey());
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
