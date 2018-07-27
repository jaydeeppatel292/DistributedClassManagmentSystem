package com.concordia.dsd.server.UDP;

import com.concordia.dsd.global.cmsenum.Location;
import com.concordia.dsd.global.cmsenum.Status;
import com.concordia.dsd.global.constants.CMSLogMessages;
import com.concordia.dsd.model.ClassMap;
import com.concordia.dsd.model.Record;
import com.concordia.dsd.model.StudentRecord;
import com.concordia.dsd.model.TeacherRecord;
import com.concordia.dsd.server.FrontEndImpl;
import com.concordia.dsd.server.ServerManager;
import com.concordia.dsd.server.generics.FIFORequestQueueModel;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FrontEndUDPManager {
    private Location serverLocation;
    private Logger serverLogger;

    public FrontEndUDPManager(Location serverLocation, Logger serverLogger) {
        this.serverLocation = serverLocation;
        this.serverLogger = serverLogger;
    }

    /**
     * Implementation to send udp request
     * @return
     */
    public String sendUDPRequest(FrontEndImpl.MasterServerInfo masterServerInfo, FIFORequestQueueModel fifoRequestQueueModel) {

        try {

            UDPRequest udpRequest = new UDPRequest(masterServerInfo.getLocation(),masterServerInfo.getHostAddress(),masterServerInfo.getPort(),fifoRequestQueueModel);
            udpRequest.start();
            udpRequest.join();
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
