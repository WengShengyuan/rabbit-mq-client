package org.wsy.mqendpoint.core;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeoutException;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

public class MqCore {

	private static final Logger logger = LogManager.getLogger(MqCore.class);

	private static volatile MqCore instance;
	private static ObjectPool<Connection> pool;
	private static GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
	private static int maxActive = 15;
	private static int maxWait = 60 * 1000;
	private static int maxIdle = 10;
	private static int minIdle = 3;
	private static boolean testOnBorrow = false;
	private static long timeBetweenEvictionRunsMillis = 10 * 60 * 1000;
	private static boolean testWhileIdle = false;
	private static boolean blockWhenExhausted = true;

	public static MqCore getInstance() {
		if (instance == null) {
			synchronized (MqCore.class) {
				if (instance == null) {
					instance = new MqCore();
				}
			}
		}
		return instance;
	}

	private MqCore() {
		try {
			logger.debug("reading properties [maxActive] ...");
			maxActive = Integer.valueOf(Config.get("mq.pool.maxActive"));
		} catch (NumberFormatException e1) {
			logger.warn("fail to get [maxActive] value of mq-endpoint.properties. set to default:" + maxActive);
		}
		try {
			logger.debug("reading properties [maxWait] ...");
			maxWait = Integer.valueOf(Config.get("mq.pool.maxWait"));
		} catch (NumberFormatException e1) {
			logger.warn("fail to get [maxWait] value of mq-endpoint.properties. set to default:" + maxWait);
		}
		try {
			logger.debug("reading properties [maxIdle] ...");
			maxIdle = Integer.valueOf(Config.get("mq.pool.maxIdle"));
		} catch (NumberFormatException e1) {
			logger.warn("fail to get [maxIdle] value of mq-endpoint.properties. set to default:" + maxIdle);
		}
		try {
			logger.debug("reading properties [minIdle] ...");
			minIdle = Integer.valueOf(Config.get("mq.pool.minIdle"));
		} catch (NumberFormatException e1) {
			logger.warn("fail to get [minIdle] value of mq-endpoint.properties. set to default:" + minIdle);
		}
		try {
			logger.debug("reading properties [testOnBorrow] ...");
			testOnBorrow = Boolean.valueOf(Config.get("mq.pool.testOnBorrow"));
		} catch (Exception e1) {
			logger.warn("fail to get [testOnBorrow] value of mq-endpoint.properties. set to default:" + testOnBorrow);
		}
		try {
			logger.debug("reading properties [timeBetweenEvictionRunsMillis] ...");
			timeBetweenEvictionRunsMillis = Long.valueOf(Config.get("mq.pool.timeBetweenEvictionRunsMillis"));
		} catch (NumberFormatException e1) {
			logger.warn("fail to get [timeBetweenEvictionRunsMillis] value of mq-endpoint.properties. set to default:"
					+ timeBetweenEvictionRunsMillis);
		}
		try {
			logger.debug("reading properties [testWhileIdle] ...");
			testWhileIdle = Boolean.valueOf(Config.get("mq.pool.testWhileIdle"));
		} catch (Exception e1) {
			logger.warn("fail to get [testWhileIdle] value of mq-endpoint.properties. set to default:" + testWhileIdle);
		}

		try {
			logger.debug("reading properties [blockWhenExhausted] ...");
			blockWhenExhausted = Boolean.valueOf(Config.get("mq.pool.blockWhenExhausted"));
		} catch (Exception e1) {
			logger.warn("fail to get [blockWhenExhausted] value of mq-endpoint.properties. set to default:"
					+ blockWhenExhausted);
		}
		logger.debug("ChannelPool config: [maxActive: " + maxActive + ",maxWait: " + maxWait + ",maxIdle: " + maxIdle
				+ ",minIdle: " + minIdle + ",testOnBorrow: " + testOnBorrow + ",timeBetweenEvictionRunsMillis: "
				+ timeBetweenEvictionRunsMillis + ",testWhileIdle: " + testWhileIdle + ",blockWhenExhausted: "
				+ blockWhenExhausted + "]");
		poolConfig.setMaxIdle(maxIdle);
		poolConfig.setMinIdle(minIdle);
		poolConfig.setMaxWaitMillis(maxWait);
		poolConfig.setTestOnBorrow(testOnBorrow);
		poolConfig.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
		poolConfig.setTestWhileIdle(testWhileIdle);
		poolConfig.setBlockWhenExhausted(blockWhenExhausted);
		pool = new GenericObjectPool<Connection>(new PooledConnectionFactory(), poolConfig);
		logger.debug("ChannelPool initialized.");
	}

	public Connection borrowObject() throws NoSuchElementException, IllegalStateException, Exception {
		return pool.borrowObject();
	}

	public void returnObject(Connection connection) throws Exception {
		pool.returnObject(connection);
	}
	
	public void poolCheck(){
		logger.info("ChannelPool status: [active: "+pool.getNumActive()+", idle: "+pool.getNumIdle()+"]");
	} 
}
