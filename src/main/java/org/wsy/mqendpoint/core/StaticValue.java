package org.wsy.mqendpoint.core;

public class StaticValue {
	
	public static class PATH {
		public static final String MQENDPOINTPROPERTIES = "mq-endpoint.properties";
	}
	
	public static class ENDPOINTTYPE {
		public static final String PRODUCER = "PRODUCER";
		public static final String CONSUMER = "CONSUMER";
		public static final String BUILDER = "BUILDER";
	}
	
	public static class BINDTYPE {
		public static final String QUEUE = "QUEUE";
		public static final String EXCHANGE = "EXCHANGE";
	}
	
	public static class EXCHANGETYPE {
		public static final String DIRECT = "direct";
		public static final String FANOUT = "fanout";
		public static final String TOPIC = "topic";
	}

}
