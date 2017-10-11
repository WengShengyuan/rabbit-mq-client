package org.wsy.mqendpoint.endpoint.interfaces;

import org.wsy.mqendpoint.endpoint.EndpointPropertiesWrapper;

public interface IQueueBuilder {
	
	void init(EndpointPropertiesWrapper config) throws Exception;

}
