package org.wsy.mqendpoint.endpoint.interfaces.thread;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.wsy.mqendpoint.core.MqCore;
import org.wsy.mqendpoint.core.StaticValue;
import org.wsy.mqendpoint.endpoint.EndpointPropertiesWrapper;
import org.wsy.mqendpoint.endpoint.serialization.SimpleSerialization;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

/**
 * 自动consume并且发出ack信号的消费者线程实现<br>
 * <strong>*必须重写handle方法*</strong>
 * 
 * @author WSY
 *
 */
public class AutoAckConsumerThread extends Thread {
	private static final Logger logger = LogManager.getLogger(AutoAckConsumerThread.class);
	private Channel channel;
	private Connection connection;
	private Consumer consumer;
	protected EndpointPropertiesWrapper config;

	public AutoAckConsumerThread(EndpointPropertiesWrapper config) {
		this.config = config;
	}

	@Override
	public void run() {

		try {
			connection = MqCore.getInstance().borrowObject();
			channel = connection.createChannel();
			if (!config.getEndPointType().equals(StaticValue.ENDPOINTTYPE.CONSUMER))
				throw new Exception("wrong endpoint type, consumer only!");
			if (config.getBindType().equals(StaticValue.BINDTYPE.QUEUE)) {
				channel.queueDeclare(config.getQueueName(), config.isDurable(), config.isExclusive(),
						config.isAutoDelete(), config.getArguments());
				logger.debug("queue receive <- [" + config.getQueueName() + "]");
			} else if (config.getBindType().equals(StaticValue.BINDTYPE.EXCHANGE)) {
				channel.queueDeclare(config.getQueueName(), config.isDurable(), config.isExclusive(),
						config.isAutoDelete(), config.getArguments());
				channel.exchangeDeclare(config.getExchangeName(), config.getExchangeType(), config.isDurable(),
						config.isAutoDelete(), config.getArguments());
				channel.queueBind(config.getQueueName(), config.getExchangeName(), config.getRoutingKey());
				logger.debug("exchange receive <- [" + config.getExchangeName() + ", " + config.getExchangeType() + ", "
						+ config.getRoutingKey() + "]");
			} else {
				throw new Exception("wrong bindtype: " + config.getBindType() + ".");
			}

			consumer = new DefaultConsumer(this.channel) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
						byte[] body) throws IOException {
					try {
						logger.debug("Cosumer[" + consumerTag + "] via channel:#" + getChannel().getChannelNumber()
								+ ", start handling delivery[" + envelope.getDeliveryTag() + "] ...");
						handle(SimpleSerialization.toObject(body));
						getChannel().basicAck(envelope.getDeliveryTag(), false);
					} catch (Exception e) {
						exceptionHandler(e);
						getChannel().basicNack(envelope.getDeliveryTag(), false, true);
					} finally {
//						if(null != channel)
//							try {
//								channel.close();
//							} catch (TimeoutException e) {
//							}
					}
				}
			};
			channel.basicConsume(config.getQueueName(), false, consumer);
		} catch (Exception e) {
			try {
				Thread.currentThread().sleep(1000);
			} catch (InterruptedException e1) {
			}
			logger.error("fail to process endpointConfig and channel.", e);
		} finally {
//			try {
//				MqCore.getInstance().returnObject(connection);
//			} catch (Exception e) { }
		}
	}

	protected void handle(Object data) throws Exception {
		throw new Exception("please overwrite this method.");
	}

	protected void exceptionHandler(Throwable e) {
		try {
			Thread.currentThread().sleep(1000);
		} catch (InterruptedException e1) {
		}
		logger.error("AutoAckConsumerThread exception handler:" + e,e);
	}
}
