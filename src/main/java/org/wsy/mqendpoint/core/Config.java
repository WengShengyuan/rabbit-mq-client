package org.wsy.mqendpoint.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class Config {
	private static Properties properties;
	private static InputStream is;
	private static final String propertiesFilePath = StaticValue.PATH.MQENDPOINTPROPERTIES;
	private static final Logger logger = LogManager.getLogger(Config.class);
	static {
		try {
			logger.debug("reading properties from ["+propertiesFilePath+"] ...");
			properties = new Properties();
			is = Config.class.getClassLoader().getResourceAsStream(propertiesFilePath);
			properties.load(is);
			if (is != null) {
				is.close();
			}
		} catch (IOException e) {
			logger.error("fail to read: "+propertiesFilePath,e);
		} finally {
			if(is!=null)
				try {
					is.close();
				} catch (IOException e) {
					is = null;
				}
		}
	}

	public static String get(String key){
		return properties.getProperty(key,"");
	}
	
}
