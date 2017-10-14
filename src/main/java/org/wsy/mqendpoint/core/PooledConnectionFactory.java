package org.wsy.mqendpoint.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Recoverable;
import com.rabbitmq.client.RecoveryListener;
import com.rabbitmq.client.ShutdownListener;
import com.rabbitmq.client.ShutdownSignalException;

public class PooledConnectionFactory extends BasePooledObjectFactory<Connection> {

	private static final Logger logger = LogManager.getLogger(PooledConnectionFactory.class);

	private static String hosts = "127.0.0.1:5672";
	private static String userName = "";
	private static String password = "";
	private static boolean automaticRecovery = true;
	private static long networkRecoveryInterval = 10000L;

	private static Map<String, ConnectionFactory> factories;

	public PooledConnectionFactory() {
		logger.info("instance ChannelFactory ...");
		// factory = new ConnectionFactory();
		try {
			hosts = Config.get("mq.hosts");
		} catch (Exception e) {
			logger.warn("fail to get value [mq.server] from mq-endpoint.properties, set to default.");
		}
		try {
			userName = Config.get("mq.user");
		} catch (Exception e) {
			logger.warn("fail to get value [mq.user] from mq-endpoint.properties, set to default.");
		}
		try {
			password = Config.get("mq.password");
		} catch (Exception e) {
			logger.warn("fail to get value [mq.password] from mq-endpoint.properties, set to default.");
		}
		try {
			automaticRecovery = Boolean.valueOf(Config.get("mq.automaticRecovery"));
		} catch (Exception e) {
			logger.warn("fail to get value [mq.automaticRecovery] from mq-endpoint.properties, set to default.");
		}
		try {
			networkRecoveryInterval = Long.valueOf(Config.get("mq.networkRecoveryInterval"));
		} catch (NumberFormatException e) {
			logger.warn("fail to get value [mq.networkRecoveryInterval] from mq-endpoint.properties, set to default.");
		}

		logger.info("ChannelFactory param : [hosts: " + hosts + ", userName: " + userName + ", password: " + password
				+ "]");

		String[] hostArray = hosts.split(",");
		if (hostArray.length > 0) {
			for (String host : hostArray) {
				registerFactory(host);
			}
			logger.info("ConnectionFactory map initialized. factories count:" + factories.keySet().size());
		} else {
			logger.warn("no ConnectionFactory initialized. hosts:" + hosts);
		}
	}

	/**
	 * 注册主机
	 * @param hostPort
	 */
	private synchronized static void registerFactory(String hostPort) {
		if (hostPort == null || hostPort.isEmpty())
			return;
		if (!hostPort.contains(":"))
			return;
		if (factories == null) {
			logger.info("initializing factories map...");
			factories = new HashMap<String, ConnectionFactory>();
		}
		if (factories.containsKey(hostPort)) {
			return;
		}

		String[] params = hostPort.split(":");
		if (params.length != 2) {
			logger.warn("hostPort illegal, length is not 2");
			return;
		}
		logger.info("registering new factory [" + hostPort + "] ...");
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(params[0]);
		factory.setPort(Integer.valueOf(params[1]));
		factory.setAutomaticRecoveryEnabled(automaticRecovery);
		factory.setNetworkRecoveryInterval(networkRecoveryInterval);
		factory.setUsername(userName);
		factory.setPassword(password);
		factories.put(hostPort, factory);
	}

	private static synchronized void refreshFactories(){
		logger.info("refreshing factories....");
		String[] hostArray = hosts.split(",");
		if (hostArray.length > 0) {
			for (String host : hostArray) {
				registerFactory(host);
			}
			logger.info("ConnectionFactory map initialized. factories count:" + factories.keySet().size());
		} else {
			logger.warn("no ConnectionFactory initialized. hosts:" + hosts);
		}
	}
	
	/**
	 * 踢出主机
	 * @param hostPort
	 */
	private static synchronized void kick(String hostPort) {
		factories.remove(hostPort);
	}

	/**
	 * 随机选择一个主机
	 * @return
	 */
	private static synchronized ConnectionFactory pickOne() {
		return factories.get(factories.keySet().toArray()[new Random().nextInt(factories.keySet().size())]);
	}

	@Override
	public Connection create() throws Exception {
		if(factories == null || factories.keySet().size()<1) {
			logger.warn("no available factories, refreshing...");
			refreshFactories();
		}
		ConnectionFactory factory = null;
		Connection connection = null;
		do {
			try {
				factory = pickOne();
				connection = factory.newConnection();
			} catch (Exception e) {
				logger.error("fail to create new connection from factory: [" + factory.getHost() + ":" + factory.getPort()
						+ "], kicking this one out and retry...");
				kick(factory.getHost() + ":" + factory.getPort());
			}
		} while(connection == null && factories.keySet().size()>0);
		if(connection == null) {
			throw new Exception("fail to get new connection. no hosts left to use.");
		}
		/* ADD CONNECTION & CHANNEL CONNECTION LISTENER */
		connection.addShutdownListener(new ShutdownListener() {
			public void shutdownCompleted(ShutdownSignalException cause) {
				String hardError = "";
				String applInit = "";
				if (cause.isHardError()) {
					hardError = "connection";
				} else {
					hardError = "channel";
				}

				if (cause.isInitiatedByApplication()) {
					applInit = "application";
				} else {
					applInit = "broker";
				}
				logger.warn("Connectivity to MQ has failed.  It was caused by " + applInit + " at the " + hardError
						+ " level.  Reason received " + cause.getReason());
			}
		});

		((Recoverable) connection).addRecoveryListener(new RecoveryListener() {
			public void handleRecovery(Recoverable recoverable) {
				if (recoverable instanceof Connection) {
					logger.info("Connection was recovered.");
				} else if (recoverable instanceof Channel) {
					int channelNumber = ((Channel) recoverable).getChannelNumber();
					logger.info("Connection to channel #" + channelNumber + " was recovered.");
				}
			}

			public void handleRecoveryStarted(Recoverable arg0) {
			}
		});
		/* ADD CONNECTION & CHANNEL CONNECTION LISTENER */
		logger.info("new connection was establesed...from host <- ["+factory.getHost()+":"+factory.getPort()+"]");
		
		if(factories.keySet().size() < hosts.split(",").length) {
			logger.warn("found unavailable host, refreshing...");
			refreshFactories();
		}
		
		return connection;
	}

	@Override
	public PooledObject<Connection> wrap(Connection obj) {
		return new DefaultPooledObject<Connection>(obj);
	}
	
	
    @Override
    public void destroyObject(PooledObject<Connection> p)
        throws Exception  {
    	if(p.getObject().isOpen())
    		p.getObject().close();
    }
    
    @Override
    public boolean validateObject(PooledObject<Connection> p){
        return p.getObject().isOpen();
    }
}