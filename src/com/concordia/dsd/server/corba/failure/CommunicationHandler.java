package com.concordia.dsd.server.corba.failure;

import java.io.IOException;

import com.concordia.dsd.global.enums.RequestType;
import com.concordia.dsd.server.ServerManager.CenterServerInfo;
import com.concordia.dsd.server.UDP.UDPRequest;
import com.concordia.dsd.server.generics.FIFORequestQueueModel;

public class CommunicationHandler {

	public void ping(CenterServerInfo sender, CenterServerInfo receiver)
			throws SecurityException, IOException {
		FIFORequestQueueModel reqObj = new FIFORequestQueueModel(RequestType.PING_SERVER);
		UDPRequest request = new UDPRequest(sender.getLocation(), receiver.getHostAddress(),
				receiver.getPort(), reqObj);
		request.start();
	}

}
