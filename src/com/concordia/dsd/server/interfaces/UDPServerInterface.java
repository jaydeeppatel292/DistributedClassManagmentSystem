package com.concordia.dsd.server.interfaces;

import java.net.InetAddress;

public interface UDPServerInterface {
	void initializeServerSocket();
	
	InetAddress getInetAddress();
	
	int getCenterServerPort();
}
