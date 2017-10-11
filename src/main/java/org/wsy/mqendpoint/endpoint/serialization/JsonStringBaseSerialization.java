package org.wsy.mqendpoint.endpoint.serialization;

import com.alibaba.fastjson.JSONObject;

public class JsonStringBaseSerialization {
	
	public static byte[] toByte(Object object) throws Exception {
		return JSONObject.toJSONString(object).getBytes();
	}

	public static Object toObject(byte[] objectBytes) throws Exception {
		return JSONObject.parse(new String(objectBytes));
	}
}
