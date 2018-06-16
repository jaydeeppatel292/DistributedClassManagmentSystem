package com.concordia.dsd.server.UDP;

import com.concordia.dsd.global.cmsenum.Location;
import com.concordia.dsd.global.constants.CMSLogMessages;
import com.concordia.dsd.model.ClassMap;
import com.concordia.dsd.server.RMI.Server;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UDPManager {
    private Location serverLocation;
    private ClassMap recordMap;
    private Logger serverLogger;

    public UDPManager(Location serverLocation, ClassMap recordMap, Logger serverLogger) {
        this.serverLocation = serverLocation;
        this.recordMap = recordMap;
        this.serverLogger = serverLogger;
    }

    public String getRecordCounts() {
        StringBuffer stringBuffer = new StringBuffer();
        String recordCount = null;
        UDPRequest[] requests = new UDPRequest[Location.values().length - 1];
        int counter = 0;
        for (Location location : Location.values()) {
            if (location == serverLocation) {
                recordCount = location.toString() + " " + recordMap.getRecordsCount();
                stringBuffer.append(recordCount);
            } else {
                try {
                    requests[counter] = new UDPRequest(Server.centralRepository.get(location).getCenterServerCenterImpl());
                } catch (SecurityException e) {
                    serverLogger.log(Level.SEVERE, e.getMessage());
                } catch (IOException e) {
                    serverLogger.log(Level.SEVERE, e.getMessage());
                }
                requests[counter].start();
                counter++;
            }
        }

        for (UDPRequest request : requests) {
            try {
                request.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            stringBuffer.append(", ").append(request.getCenterServer().getLocation().toString());
            stringBuffer.append(" ").append(request.getRequestedRecordCount());
        }
        serverLogger.log(Level.INFO, CMSLogMessages.RECORD_COUNT, stringBuffer.toString());
        System.out.println("Record Count : " + stringBuffer.toString());
        return stringBuffer.toString();
    }
}
