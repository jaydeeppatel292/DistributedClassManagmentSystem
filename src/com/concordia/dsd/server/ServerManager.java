package com.concordia.dsd.server;

import com.concordia.dsd.global.cmsenum.Location;
import com.concordia.dsd.server.RMI.RMICenterServerImpl;
import com.concordia.dsd.server.generics.CenterServerImpl;

import java.util.HashMap;

public class ServerManager {
    private static ServerManager ourInstance = new ServerManager();
    private HashMap<Location, CenterServerImpl> centerServerMap = new HashMap<>();

    public static ServerManager getInstance() {
        return ourInstance;
    }

    private ServerManager() {
    }

    public void addServer(Location location, CenterServerImpl centerServer) {
        centerServerMap.put(location, centerServer);
    }

    public CenterServerImpl getCenterServer(Location location) {
        return centerServerMap.get(location);
    }
}
