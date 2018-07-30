package com.concordia.dsd.server;

import com.concordia.dsd.server.generics.FIFORequestQueueModel;

import java.util.Iterator;

public class BackupServerSyncManager extends Thread {


    private FrontEndImpl frontEnd;

    public BackupServerSyncManager(FrontEndImpl frontEnd) {

        this.frontEnd = frontEnd;
    }

    @Override
    public void run() {
        super.run();
        while (true){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(!frontEnd.isBullyRunning()) {
                FIFORequestQueueModel fifoRequestQueueModel = frontEnd.getRequestFromQueue();
                if (fifoRequestQueueModel != null) {
                    String response = frontEnd.sendBackupSyncRequest(fifoRequestQueueModel);
                    frontEnd.dequeueRequestFromQueue();
                }
            }else{
                Iterator<FIFORequestQueueModel> itr =  frontEnd.getRequestQueue().iterator();
                while(itr.hasNext()){
                    itr.next().setNeedToUpdateMaster(true);
                }
            }
        }
    }
}
