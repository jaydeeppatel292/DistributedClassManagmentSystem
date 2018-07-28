package com.concordia.dsd.server;

import com.concordia.dsd.global.cmsenum.Location;
import com.concordia.dsd.server.generics.CenterServerImpl;

import java.util.*;

/**
 * Singleton ServerManager
 * Central Server Repository
 */
public class ServerManager {
    private static ServerManager ourInstance = new ServerManager();
    private HashMap<Location, List<CenterServerInfo>> centerServerMap = new HashMap<>();
    private FrontEndServer frontEndServer;


    public static ServerManager getInstance() {
        return ourInstance;
    }

    private ServerManager() {

    }

    public FrontEndServer getFrontEndServer() {
        return frontEndServer;
    }

    public void setFrontEndServer(FrontEndServer frontEndServer) {
        this.frontEndServer = frontEndServer;
    }

    public void setNewMasterServer(Location location, int masterPort) {
        for (CenterServerInfo centerServerInfo : centerServerMap.get(location)) {
            if (centerServerInfo.getPort() == masterPort) {
                centerServerInfo.setMaster(true);
                frontEndServer.getFrontEndImpl().setMasterServer(location, masterPort, centerServerInfo.getHostAddress());
            } else {
                centerServerInfo.setMaster(false);
            }
        }
    }

    public List<CenterServerInfo> getAllMasterServer() {
        List<CenterServerInfo> masterServerList = new ArrayList<>();
        Iterator it = centerServerMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            for (CenterServerInfo centerServerInfo : centerServerMap.get((Location) pair.getKey())) {
                if (centerServerInfo.isMaster()) {
                    masterServerList.add(centerServerInfo);
                }
            }
        }
        return masterServerList;
    }

    public List<CenterServerInfo> getAllBackupServerList(Location location) {
        List<CenterServerInfo> backupServerList = new ArrayList<>();
        for (CenterServerInfo centerServerInfo : centerServerMap.get(location)) {
            if (!centerServerInfo.isMaster()) {
                backupServerList.add(centerServerInfo);
            }
        }
        return backupServerList;
    }

    public List<Integer> getAllBackupServerPort(Location location) {
        List<Integer> backupServerList = new ArrayList<>();
        for (CenterServerInfo centerServerInfo : centerServerMap.get(location)) {
            if (!centerServerInfo.isMaster()) {
                backupServerList.add(centerServerInfo.getPort());
            }
        }
        return backupServerList;
    }

    /**
     * Add Created Server
     *
     * @param location
     * @param centerServer
     * @param hostAddress
     */
    public void addServer(Location location, int port, CenterServerImpl centerServer, String hostAddress) {
        if (centerServerMap.get(location) == null) {
            centerServerMap.put(location, new ArrayList<>());
        }
        centerServerMap.get(location).add(new CenterServerInfo(port, centerServer, location, hostAddress));
    }

    public CenterServerInfo getServerInfo(Location location, int port) {
        for (CenterServerInfo serverInfo : centerServerMap.get(location)) {
            if (serverInfo.getPort() == port) {
                return serverInfo;
            }
        }
        return null;
    }

    /**
     * Get CenterServerInfo based on Location
     *
     * @param location
     * @return
     */
    public CenterServerImpl getCenterServer(Location location, int port) {
        for (CenterServerInfo centerServerInfo : centerServerMap.get(location)) {
            if (centerServerInfo.getPort() == port) {
                return centerServerInfo.getCenterServerImpl();
            }
        }
        return null;
    }

    public CenterServerInfo getMasterServerInfo(Location location) {
        for (CenterServerInfo centerServerInfo : centerServerMap.get(location)) {
            if (centerServerInfo.isMaster()) {
                return centerServerInfo;
            }
        }
        return null;
    }


    public Integer getMasterServerPort(Location location) {
        for (CenterServerInfo centerServerInfo : centerServerMap.get(location)) {
            if (centerServerInfo.isMaster()) {
                return centerServerInfo.getPort();
            }
        }
        return null;
    }


    public List<CenterServerInfo> getAllServerList(Location location) {
        List<CenterServerInfo> serverList = new ArrayList<>();
        for (CenterServerInfo centerServerInfo : centerServerMap.get(location)) {
            serverList.add(centerServerInfo);
        }
        return serverList;
    }

    public void removeReplicaServer(Location location, int port) {
        Iterator<CenterServerInfo> centerServerInfoIterator = centerServerMap.get(location).iterator();
        while (centerServerInfoIterator.hasNext()) {
            if (centerServerInfoIterator.next().getPort() == port) {
                centerServerInfoIterator.remove();
            }
        }
    }


    public class CenterServerInfo {
        private int port;
        private CenterServerImpl centerServerImpl;
        private boolean isMaster;
        private Location location;
        private boolean isActive;
        private String hostAddress;

        public Location getLocation() {
            return location;
        }

        public boolean isMaster() {
            return isMaster;
        }

        public void setMaster(boolean master) {
            isMaster = master;
        }

        public CenterServerInfo(int port, CenterServerImpl centerServerImpl, Location location, String hostAddress) {
            this.port = port;
            this.centerServerImpl = centerServerImpl;
            this.location = location;
            this.hostAddress = hostAddress;
            this.isMaster = false;
            this.isActive = true;
        }

        public int getPort() {
            return port;
        }

        public CenterServerImpl getCenterServerImpl() {
            return centerServerImpl;
        }

        public boolean isActive() {
            return isActive;
        }

        public String getHostAddress() {
            return hostAddress;
        }
    }
}
