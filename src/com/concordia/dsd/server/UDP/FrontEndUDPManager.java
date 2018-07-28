package com.concordia.dsd.server.UDP;

import com.concordia.dsd.server.FrontEndImpl;
import com.concordia.dsd.server.generics.FIFORequestQueueModel;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FrontEndUDPManager {
    private Logger serverLogger;

    public FrontEndUDPManager(Logger serverLogger) {
        this.serverLogger = serverLogger;
    }

    /**
     * Implementation to send udp request
     *
     * @return
     */
    public byte[] sendUDPRequestForSelection(FrontEndImpl.MasterServerInfo masterServerInfo, FIFORequestQueueModel fifoRequestQueueModel) {
        try {
            if (fifoRequestQueueModel.isSyncRequest()) {
                System.out.println("Sync request!!");
            }
            UDPRequest udpRequest = new UDPRequest(masterServerInfo.getLocation(), masterServerInfo.getHostAddress(), masterServerInfo.getPort(), fifoRequestQueueModel);
            udpRequest.start();
            udpRequest.join();
            return udpRequest.getServerResponse();
        } catch (SecurityException e) {
            serverLogger.log(Level.SEVERE, e.getMessage());
        } catch (IOException e) {
            serverLogger.log(Level.SEVERE, e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Implementation to send udp request
     *
     * @return
     */
    public String sendUDPRequest(FrontEndImpl.MasterServerInfo masterServerInfo, FIFORequestQueueModel fifoRequestQueueModel) {
        try {
            if (fifoRequestQueueModel.isSyncRequest()) {
                System.out.println("Sync request!!");
            }
            UDPRequest udpRequest = new UDPRequest(masterServerInfo.getLocation(), masterServerInfo.getHostAddress(), masterServerInfo.getPort(), fifoRequestQueueModel);
            udpRequest.start();
            udpRequest.join();
            return udpRequest.getResponseFromUDP();
        } catch (SecurityException e) {
            serverLogger.log(Level.SEVERE, e.getMessage());
        } catch (IOException e) {
            serverLogger.log(Level.SEVERE, e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

}
