package com.concordia.dsd.server.corba.failure;

import java.io.IOException;
import java.util.List;

import com.concordia.dsd.global.cmsenum.Location;
import com.concordia.dsd.server.ServerManager;
import com.concordia.dsd.server.ServerManager.CenterServerInfo;

public class FailureDetectionService extends Thread {

	private List<CenterServerInfo> servers;

	private CommunicationHandler communicationHandler;

	public FailureDetectionService(Location location) {
		servers = ServerManager.getInstance().getAllServerList(location);
		communicationHandler = new CommunicationHandler();
	}

	@Override
	public void run() {
		try {
			while (true) {
				for (CenterServerInfo centerServerInfo : servers) {
					for (CenterServerInfo serverInfo : servers) {
						if (centerServerInfo.getPort() != serverInfo.getPort()) {
							communicationHandler.ping(centerServerInfo, serverInfo);
						}
					}
				}

			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
