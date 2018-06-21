package com.concordia.dsd.utils;

import com.concordia.dsd.exception.ManagerInvalidException;
import com.concordia.dsd.global.cmsenum.Location;
import com.concordia.dsd.global.constants.ServerConfig;
import com.concordia.dsd.server.interfaces.CenterServer;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ManagerClientUtil {

	/**
	 * Get Center Server based on given manager id
	 * @param managerId
	 * @return
	 * @throws Exception
	 */
	public static CenterServer getCenterServer(String managerId) throws Exception {
		Registry registry = LocateRegistry.getRegistry(ServerConfig.REGISTRY_PORT);

		if (managerId.startsWith(String.valueOf(Location.MTL))) {
			return (CenterServer) registry.lookup(ServerConfig.MTL_SERVER);
		} else if (managerId.startsWith(String.valueOf(Location.LVL))) {
			return (CenterServer) registry.lookup(ServerConfig.LVL_SERVER);
		} else if (managerId.startsWith(String.valueOf(Location.DDO))) {
			return (CenterServer) registry.lookup(ServerConfig.DDO_SERVER);
		} else {
			throw new ManagerInvalidException("Invalid Manager id : Location code does not exist");
		}

	}
}
