package org.wsy.mqendpoint.core.connection;

import java.util.TimerTask;

public class ConnectionFacatoryDaemon extends TimerTask{
	
	@Override
	public void run() {
		ConnectionFactoryManager.getInstance().check();
	}
}
