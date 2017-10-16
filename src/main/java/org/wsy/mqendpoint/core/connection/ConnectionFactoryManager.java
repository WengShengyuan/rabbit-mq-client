package org.wsy.mqendpoint.core.connection;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class ConnectionFactoryManager {

	private static final Logger logger = LogManager.getLogger(ConnectionFactoryManager.class);

	private static volatile ConnectionFactoryManager instance;
	private Map<String, ConnectionFactory> allFactories;
	private Map<String, ConnectionFactory> availableFactories;

	public Map<String, ConnectionFactory> getAllFactories() {
		return allFactories;
	}

	public void setAllFactories(Map<String, ConnectionFactory> allFactories) {
		this.allFactories = allFactories;
	}

	public Map<String, ConnectionFactory> getAvailableFactories() {
		return availableFactories;
	}

	public void setAvailableFactories(Map<String, ConnectionFactory> availableFactories) {
		this.availableFactories = availableFactories;
	}

	private ConnectionFactoryManager() {
		this.allFactories = new HashMap<String, ConnectionFactory>();
		this.availableFactories = new HashMap<String, ConnectionFactory>();
	}

	public static ConnectionFactoryManager getInstance() {
		if (instance == null) {
			synchronized (ConnectionFactoryManager.class) {
				if (instance == null) {
					instance = new ConnectionFactoryManager();
				}
			}
		}
		return instance;
	}

	public synchronized ConnectionFactory pickOne() {
		return availableFactories
				.get(availableFactories.keySet().toArray()[new Random().nextInt(availableFactories.keySet().size())]);
	}

	public synchronized void kick(String postPort) {
		availableFactories.remove(postPort);
	}

	public synchronized void register(String hostPort, ConnectionFactory factory) {
		this.allFactories.put(hostPort, factory);
		logger.debug("testing factory:"+hostPort+" ...");
		Connection connection = null;
		try {
			connection = factory.newConnection();
			this.availableFactories.put(hostPort, factory);
			logger.info("connection: " + hostPort + " is available.");
		} catch (Exception e) {
			logger.error("fail to establish connection: " + hostPort, e);
		} finally {
			if (connection != null)
				try {
					connection.close();
				} catch (IOException e) {}
		}
	}
	
	public void check() {
		logger.debug("start factory health check...");
		Map<String, ConnectionFactory> tmpAvailable = new HashMap<String, ConnectionFactory>();
		for(String key:this.allFactories.keySet()){
			ConnectionFactory factory = this.allFactories.get(key);
			Connection connection = null;
			String hostPort = "";
			try {
				connection = factory.newConnection();
				hostPort = factory.getHost()+":"+factory.getPort();
				tmpAvailable.put(hostPort, factory);
			} catch (Exception e) {
				logger.error("fail to establish connection: " + hostPort, e);
			} finally {
				if (connection != null)
					try {
						connection.close();
					} catch (IOException e) {}
			}
		}
		logger.debug("factory available count:"+(tmpAvailable==null?0:tmpAvailable.size()));
		this.availableFactories = tmpAvailable;
	}
}
