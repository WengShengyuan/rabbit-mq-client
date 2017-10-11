package org.wsy.mqendpoint.endpoint;

import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.wsy.mqendpoint.core.Config;
import org.wsy.mqendpoint.core.StaticValue;

/**
 * 统一队列配置
 * @author WSY
 *
 */
public class EndpointPropertiesWrapper {
	
	private static final Logger logger = LogManager.getLogger(EndpointPropertiesWrapper.class);
	
	private String endPointType;// 角色类型：生产者/消费者
	private String bindType="";// 绑定类型：直接通过队列发布/通过exchange发布
	
	private String queueName="";// 队列名称
	private String exchangeName="";// exchange名称
	private String exchangeType="direct";// exchange 分发类型：direct/fanout/topic
	private String routingKey="";// 匹配关键词
	
	private Map<String, Object> outerObjs;// 外部注入变量
	
	private boolean durable = true;
	private boolean exclusive = false;
	private boolean autoDelete = false;
	private boolean autoAck=true;
	
	Map<String,Object> arguments=null;
	
	public EndpointPropertiesWrapper() {
		try {
			durable = Boolean.valueOf(Config.get("mq.defaults.queue.durable"));
		} catch (Exception e) {
			logger.warn("fail to read [mq.defaults.queue.durable] from config.");
		}
		try {
			exclusive = Boolean.valueOf(Config.get("mq.defaults.queue.exclusive"));
		} catch (Exception e) {
			logger.warn("fail to read [mq.defaults.queue.exclusive] from config.");
		}
		try {
			autoDelete = Boolean.valueOf(Config.get("mq.defaults.queue.autoDelete"));
		} catch (Exception e) {
			logger.warn("fail to read [mq.defaults.queue.autoDelete] from config.");
		}
		try {
			autoAck = Boolean.valueOf(Config.get("mq.defaults.msg.autoack"));
		} catch(Exception e){
			logger.warn("fail to read [mq.defaults.msg.autoack] from config.");
		}
		try{
			exchangeType = Config.get("mq.defaults.msg.defaultExchangeType");
		} catch(Exception e){
			logger.warn("fail to read [mq.defaults.msg.defaultExchangeType] from config.");
		}
	}
	
	public String getEndPointType() {
		return endPointType;
	}

	public String getBindType() {
		return bindType;
	}

	public EndpointPropertiesWrapper setBindType(String bindType) {
		this.bindType = bindType;
		return this;
	}

	public String getExchangeName() {
		return exchangeName;
	}

	public EndpointPropertiesWrapper setExchangeName(String exchangeName) {
		this.exchangeName = exchangeName;
		return this;
	}

	public EndpointPropertiesWrapper setEndPointType(String endPointType) {
		this.endPointType = endPointType;
		return this;
	}

	public String getQueueName() {
		return queueName;
	}

	public EndpointPropertiesWrapper setQueueName(String queueName) {
		this.queueName = queueName;
		return this;
	}

	public boolean isDurable() {
		return durable;
	}

	public EndpointPropertiesWrapper setDurable(boolean durable) {
		this.durable = durable;
		return this;
	}

	public boolean isExclusive() {
		return exclusive;
	}

	public EndpointPropertiesWrapper setExclusive(boolean exclusive) {
		this.exclusive = exclusive;
		return this;
	}

	public boolean isAutoDelete() {
		return autoDelete;
	}

	public EndpointPropertiesWrapper setAutoDelete(boolean autoDelete) {
		this.autoDelete = autoDelete;
		return this;
	}

	public Map<String, Object> getArguments() {
		return arguments;
	}

	public EndpointPropertiesWrapper setArguments(Map<String, Object> arguments) {
		this.arguments = arguments;
		return this;
	}
	
	public String getExchangeType() {
		return exchangeType;
	}

	public EndpointPropertiesWrapper setExchangeType(String exchangeType) {
		this.exchangeType = exchangeType;
		return this;
	}

	public String getRoutingKey() {
		return routingKey;
	}

	public EndpointPropertiesWrapper setRoutingKey(String routingKey) {
		this.routingKey = routingKey;
		return this;
	}

	public boolean isAutoAck() {
		return autoAck;
	}

	public EndpointPropertiesWrapper setAutoAck(boolean autoAck) {
		this.autoAck = autoAck;
		return this;
	}

	public void valid() throws Exception {
		if(endPointType==null||endPointType.isEmpty())
			throw new Exception("endPointType is empty.");
		if(bindType==null||bindType.isEmpty())
			throw new Exception("bindType is empty.");
		
		// queue name must be given
		if(StaticValue.BINDTYPE.QUEUE.equals(this.bindType) && (queueName==null || queueName.isEmpty()))
			throw new Exception("queueName not given.");
		if(StaticValue.BINDTYPE.EXCHANGE.equals(bindType) && (exchangeName==null||exchangeName.isEmpty()||exchangeType==null||exchangeType.isEmpty()))
			throw new Exception("[exchange only mode] -> exchangeName or exchangeType not given.[exchangeName:"+exchangeName+", exchangeType:"+exchangeType+"]");
	}

	public Map<String, Object> getOuterObjs() {
		return outerObjs;
	}

	public EndpointPropertiesWrapper setOuterObjs(Map<String, Object> outerObjs) {
		this.outerObjs = outerObjs;
		return this;
	}
	
	
}
