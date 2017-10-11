package demo;

import org.wsy.mqendpoint.endpoint.EndpointPropertiesWrapper;
import org.wsy.mqendpoint.endpoint.interfaces.thread.AutoAckConsumerThread;

public class SysoutConsumer extends AutoAckConsumerThread{

	public SysoutConsumer(EndpointPropertiesWrapper config) {
		super(config);
	}

	@Override
	protected void handle(Object data) throws Exception{
//		System.out.println("---------");
//		System.out.println(this.config.getOuterObjs());
//		System.out.println(data);
//		System.out.println("---------");
//		throw new Exception("test error.");
	}
}
