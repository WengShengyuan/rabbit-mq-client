package org.wsy.mqendpoint.endpoint.interfaces.impl;

import java.lang.reflect.Constructor;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import org.wsy.mqendpoint.endpoint.EndpointPropertiesWrapper;
import org.wsy.mqendpoint.endpoint.interfaces.IConsumer;
import org.wsy.mqendpoint.endpoint.interfaces.thread.AutoAckConsumerThread;

/**
 * 消费者线程创建实现
 * @author WSY
 *
 */
public class SimpleConsumer implements IConsumer {

	private static final Logger logger = LogManager.getLogger(SimpleConsumer.class);

	public void generateConsumer(EndpointPropertiesWrapper config, Class consumerClass,int consumerCount) throws Exception {
		config.valid();
		int actualInstanceCount = 0;
		for (int i = 0; i < consumerCount; i++) {
			try {
				Constructor constructor = consumerClass.getDeclaredConstructor(new Class[] { EndpointPropertiesWrapper.class });
				constructor.setAccessible(true);
				AutoAckConsumerThread thread = (AutoAckConsumerThread) constructor
						.newInstance(new Object[] { config });
				thread.start();
				actualInstanceCount++;
				Thread.sleep(1);
			} catch (Exception e) {
				logger.error("fail to process endpointConfig and channel.", e);
			}
		}
		logger.debug("instance created for [queue:" + config.getQueueName()+"]: " + actualInstanceCount);
	}
}
