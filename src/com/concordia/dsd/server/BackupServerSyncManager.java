package com.concordia.dsd.server;

import com.concordia.dsd.server.generics.FIFORequestQueueModel;

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
            FIFORequestQueueModel fifoRequestQueueModel =  frontEnd.getRequestFromQueue();
            if(fifoRequestQueueModel!=null){
                String response = frontEnd.sendBackupSyncRequest(fifoRequestQueueModel);
                if(response!=null){
                    frontEnd.dequeueRequestFromQueue();
                }
            }
        }
    }
}
