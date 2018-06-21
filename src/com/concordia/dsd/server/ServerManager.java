package com.concordia.dsd.server;

import com.concordia.dsd.global.cmsenum.Location;
import com.concordia.dsd.server.RMI.RMICenterServerImpl;
import com.concordia.dsd.server.generics.CenterServerImpl;

import java.util.HashMap;

/**
 * Singleton ServerManager
 * Central Server Repository
 */
public class ServerManager {
    private static ServerManager ourInstance = new ServerManager();
    private HashMap<Location, CenterServerImpl> centerServerMap = new HashMap<>();

    public static ServerManager getInstance() {
        return ourInstance;
    }

    private ServerManager() {
    }

    /**
     * Add Created Server
     * @param location
     * @param centerServer
     */
    public void addServer(Location location, CenterServerImpl centerServer) {
        centerServerMap.put(location, centerServer);
    }

    /**
     * Get CenterServer based on Location
     * @param location
     * @return
     */
    public CenterServerImpl getCenterServer(Location location) {
        return centerServerMap.get(location);
    }
}
