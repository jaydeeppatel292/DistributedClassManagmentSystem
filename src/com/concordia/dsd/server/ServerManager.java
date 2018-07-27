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
    private HashMap<Location, HashMap<Integer,CenterServerImpl>> centerServerMap = new HashMap<>();

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
    public void addServer(Location location,int port, CenterServerImpl centerServer) {
        if(centerServerMap.get(location)==null){
            centerServerMap.put(location,new HashMap<>());
        }
        centerServerMap.get(location).put(port,centerServer);
    }

    /**
     * Get CenterServer based on Location
     * @param location
     * @return
     */
    public CenterServerImpl getCenterServer(Location location,int port) {
        return centerServerMap.get(location).get(port);
    }

    /**
     * Get CenterServer based on Location
     * @param location
     * @return
     */
    public HashMap<Integer, CenterServerImpl> getCenterServer(Location location) {
        return centerServerMap.get(location);
    }
}
