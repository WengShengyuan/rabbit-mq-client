package demo;

import java.util.Date;

import org.wsy.mqendpoint.core.MqCore;
import org.wsy.mqendpoint.core.StaticValue;
import org.wsy.mqendpoint.endpoint.EndpointPropertiesWrapperFactory;
import org.wsy.mqendpoint.endpoint.interfaces.impl.SimpleConsumer;
import org.wsy.mqendpoint.endpoint.interfaces.impl.SimpleProducer;
import org.wsy.mqendpoint.endpoint.interfaces.impl.SimpleQueueBuilder;

public class TestCase {

	public static void main(String[] args) {

		// try {
		// System.out.println("============= STEP 1 : SEND OVER QUEUE
		// =================");
		// Map<String,Object> outerObjs = new HashMap<String,Object>();
		// outerObjs.put("outerObjs", "用于外部引入spring的依赖注入服务");
		//
		// new
		// SimpleProducer().publish(EndpointPropertiesWrapperFactory.producer("testQueue"),
		// "this is a message.");
		// new
		// SimpleConsumer().generateConsumer(EndpointPropertiesWrapperFactory.consumer("testQueue")
		// .setOuterObjs(outerObjs),
		// SysoutConsumer.class,counsumerCount);
		//
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		//
		// try {
		// System.out.println("============= STEP 2 : SEND OVER EXCHANGE
		// =================");
		// new
		// SimpleProducer().publish(EndpointPropertiesWrapperFactory.producer("testExchange","testQueue","routingKey"),
		// "this is a message.");
		// new
		// SimpleConsumer().generateConsumer(EndpointPropertiesWrapperFactory.consumer("testExchange","testQueue","routingKey"),
		// SysoutConsumer.class,counsumerCount);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		// try {
		// System.out.println("============= STEP 3 : EXCHANGE DIRECT
		// =================");
		// new
		// SimpleConsumer().generateConsumer(EndpointPropertiesWrapperFactory.consumer("testExchange","testQueue","routingKey"),
		// SysoutConsumer.class,counsumerCount);
		// new
		// SimpleProducer().publish(EndpointPropertiesWrapperFactory.producer("testExchange","testQueue","routingKey"),
		// "this is a message.");
		// } catch (Exception e1) {
		// }

		// try {
		// System.out.println("============= STEP 3 : EXCHANGE FANOUT
		// =================");
		// new
		// SimpleConsumer().generateConsumer(EndpointPropertiesWrapperFactory.consumer("testExchange-fanout","testQueue-fanout","routingKey").setExchangeType(StaticValue.EXCHANGETYPE.FANOUT),
		// SysoutConsumer.class,counsumerCount);
		// new
		// SimpleProducer().publish(EndpointPropertiesWrapperFactory.producer("testExchange-fanout","testQueue-fanout","routingKey").setExchangeType(StaticValue.EXCHANGETYPE.FANOUT),
		// "this is a message.");
		// } catch (Exception e1) {
		// }

		try {
//			System.out.println("============= STEP 3 : EXCHANGE TOPIC =================");
//			
//			new SimpleQueueBuilder().init(EndpointPropertiesWrapperFactory
//					.builder("testExchange-topic", "testQueue-topic-topic1", "topic")
//					.setExchangeType(StaticValue.EXCHANGETYPE.TOPIC));
//	
//			new SimpleQueueBuilder().init(EndpointPropertiesWrapperFactory
//					.builder("testExchange-topic", "testQueue-topic-topic2", "topic")
//					.setExchangeType(StaticValue.EXCHANGETYPE.TOPIC));
			
//			new SimpleQueueBuilder().init(EndpointPropertiesWrapperFactory
//					.builder("testExchange-topic", "testQueue-topic-topic1", "topic.*")
//					.setExchangeType(StaticValue.EXCHANGETYPE.TOPIC));
//			new SimpleQueueBuilder().init(EndpointPropertiesWrapperFactory
//					.builder("testExchange-topic", "testQueue-topic-topic2", "topic.*")
//					.setExchangeType(StaticValue.EXCHANGETYPE.TOPIC));
			
			
			
//			Map<String, Object> outerObjs = null;
//			
//			outerObjs = new HashMap<String, Object>();
//			outerObjs.put("outerObjs", "consumer@topic1");
//			new SimpleConsumer().generateConsumer(
//					EndpointPropertiesWrapperFactory.consumer("testQueue-topic-topic1").setOuterObjs(outerObjs), // 注入外部实例
//					SysoutConsumer.class, 1);
//
//			outerObjs = new HashMap<String, Object>();
//			outerObjs.put("outerObjs", "consumer@topic2");
//			new SimpleConsumer().generateConsumer(
//					EndpointPropertiesWrapperFactory.consumer("testQueue-topic-topic2").setOuterObjs(outerObjs), // 注入外部实例
//					SysoutConsumer.class, 1);
			
			
//			new SimpleConsumer().generateConsumer(EndpointPropertiesWrapperFactory.consumer("testQueue-topic"), SysoutConsumer.class, 2);
			
//			while (true) {
//				new SimpleProducer().publish(EndpointPropertiesWrapperFactory
//						.producer("testExchange-topic",  "topic")
//						.setExchangeType(StaticValue.EXCHANGETYPE.TOPIC), "message for topic.topic1");
//				
//				
//				
//				
//				
//				
//				
//				new SimpleProducer().publish(EndpointPropertiesWrapperFactory
//						.producer("testExchange-topic", "topic")
//						.setExchangeType(StaticValue.EXCHANGETYPE.TOPIC),
//						"message for topic.topic2");
				
//			}
			
		
			new SimpleQueueBuilder().init(EndpointPropertiesWrapperFactory
					.builder("testExchange-topic", "testQueue-topic-topic1", "topic")
					.setExchangeType(StaticValue.EXCHANGETYPE.TOPIC));
			new SimpleQueueBuilder().init(EndpointPropertiesWrapperFactory.builder("queue"));
		new Thread(new Runnable() {
			
			public void run() {
				try {
					while(true){
						new SimpleProducer().publish(EndpointPropertiesWrapperFactory.producer("testExchange-topic", "topic")
								.setExchangeType(StaticValue.EXCHANGETYPE.TOPIC),"testMessage:"+new Date());
//						Thread.currentThread().sleep(10);
					} 
				} catch (Exception e) {
				}
			}
		}).start();;
		
		new Thread(new Runnable() {
			
			public void run() {
				MqCore.getInstance().poolCheck();
				try {
					Thread.currentThread().sleep(1000);
				} catch (InterruptedException e) {
				}
			}
		}).start();
		
		
		new SimpleConsumer().generateConsumer(EndpointPropertiesWrapperFactory.consumer("testQueue-topic-topic1"), SysoutConsumer.class, 2);
		
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		System.out.println("================ CHANNEL POOL CHECK ==================");
		while (true) {
			MqCore.getInstance().poolCheck();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}

	}

}
