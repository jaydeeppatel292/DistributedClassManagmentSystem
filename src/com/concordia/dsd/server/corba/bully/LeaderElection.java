package com.concordia.dsd.server.corba.bully;

import com.concordia.dsd.global.cmsenum.Location;
import com.concordia.dsd.server.ServerManager;
import com.concordia.dsd.server.UDP.UDPManager;

import java.util.List;
import java.util.Random;

public class LeaderElection extends Thread {

    private List<Integer> processIdList;
    private Location location;

    public LeaderElection(Location location, List<Integer> idList) {
        processIdList = idList;
        this.location = location;
    }

    @Override
    public void run() {
        initElection();
    }

    private void initElection() {
        int randomNumber = new Random().nextInt(processIdList.size());
        UDPManager udpManager = ServerManager.getInstance().getCenterServer(location, processIdList.get(randomNumber)).getUdpManager();
//        processIdList.remove(randomNumber);
        udpManager.initElection(location, processIdList);
    }
}
