package com.concordia.dsd.server.corba.bully;

import java.util.List;

public class LeaderElection extends Thread {

    private List<Integer> processIdList;

    public LeaderElection(List<Integer> idList) {
        processIdList = idList;
    }

    @Override
    public void run() {
        initElection();
    }

    private void initElection() {
        for (Integer processId : processIdList) {
            //TODO check all active replica servers
        }
    }
}
