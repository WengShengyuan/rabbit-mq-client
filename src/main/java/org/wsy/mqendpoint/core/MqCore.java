package org.wsy.mqendpoint.core;

import java.util.NoSuchElementException;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.rabbitmq.client.Connection;
import org.wsy.mqendpoint.core.connection.ConnectionFacatoryDaemon;
import org.wsy.mqendpoint.core.connection.PooledConnectionFactory;

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
	private static int delay = 5000;
	private static int interval = 60000;

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
		try {
			logger.debug("reading properties [daemon delay] ...");
			delay = Integer.valueOf(Config.get("mq.daemon.delay"));
		} catch (Exception e) {
			logger.warn("fail to get [daemon delay] value of mq-endpoint.properties. set to default:" + delay);
		}
		try {
			logger.debug("reading properties [daemon interval] ...");
			interval = Integer.valueOf(Config.get("mq.daemon.interval"));
		} catch (Exception e) {
			logger.warn("fail to get [daemon interval] value of mq-endpoint.properties. set to default:" + interval);
		}

		logger.debug("ChannelPool config: [maxActive: " + maxActive + ",maxWait: " + maxWait + ",maxIdle: " + maxIdle
				+ ",minIdle: " + minIdle + ",testOnBorrow: " + testOnBorrow + ",timeBetweenEvictionRunsMillis: "
				+ timeBetweenEvictionRunsMillis + ",testWhileIdle: " + testWhileIdle + ",blockWhenExhausted: "
				+ blockWhenExhausted + ",daemon delay: "+delay+", daemon interval: "+interval+"]");
		poolConfig.setMaxIdle(maxIdle);
		poolConfig.setMinIdle(minIdle);
		poolConfig.setMaxWaitMillis(maxWait);
		poolConfig.setTestOnBorrow(testOnBorrow);
		poolConfig.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
		poolConfig.setTestWhileIdle(testWhileIdle);
		poolConfig.setBlockWhenExhausted(blockWhenExhausted);
		pool = new GenericObjectPool<Connection>(new PooledConnectionFactory(), poolConfig);
		logger.debug("ChannelPool initialized, start factory daemon...");
		// start daemon task
		TimerTask daemonTask = new ConnectionFacatoryDaemon();
		Timer timer = new Timer(true);
		timer.schedule(daemonTask, delay, interval);
		logger.debug("factory daemon on.");
	}

	public Connection borrowObject() throws NoSuchElementException, IllegalStateException, Exception {
		return pool.borrowObject();
	}

	public void returnObject(Connection connection) throws Exception {
		pool.returnObject(connection);
	}

	public void poolCheck() {
		logger.info("ChannelPool status: [active: " + pool.getNumActive() + ", idle: " + pool.getNumIdle() + "]");
	}
}
