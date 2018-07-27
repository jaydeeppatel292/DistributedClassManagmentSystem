package com.concordia.dsd.server.RMI;


import com.concordia.dsd.global.cmsenum.Location;
import com.concordia.dsd.global.constants.ServerConfig;
import com.concordia.dsd.server.ServerManager;
import com.concordia.dsd.server.generics.CenterServerImpl;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;

public class Server {
	
	public static HashMap<Location, RMICenterServerImpl> centralRepository = new HashMap<>();

	public static void main(String[] args) throws Exception {
		for	(Location location: Location.values()) {
		    RMICenterServerImpl rmiCenterServer = new RMICenterServerImpl(location);
			centralRepository.put(location, rmiCenterServer);
            ServerManager.getInstance().addServer(location,0 ,rmiCenterServer.getCenterServerCenterImpl());
		}
		Registry registry = LocateRegistry.createRegistry(ServerConfig.REGISTRY_PORT);
		registry.bind(ServerConfig.MTL_SERVER, centralRepository.get(Location.MTL));
		registry.bind(ServerConfig.LVL_SERVER, centralRepository.get(Location.LVL));
		registry.bind(ServerConfig.DDO_SERVER, centralRepository.get(Location.DDO));

		System.out.println("Server has started");
	}
}
