package com.concordia.dsd.server.UDP;

import com.concordia.dsd.global.cmsenum.Location;
import com.concordia.dsd.global.cmsenum.Status;
import com.concordia.dsd.global.constants.CMSLogMessages;
import com.concordia.dsd.model.ClassMap;
import com.concordia.dsd.model.Record;
import com.concordia.dsd.model.StudentRecord;
import com.concordia.dsd.model.TeacherRecord;
import com.concordia.dsd.server.ServerManager;

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
     * Implementation of Create Teacher Record
     * @param firstName
     * @param lastName
     * @param address
     * @param phone
     * @param specialization
     * @param location
     * @param managerId
     * @return
     */
    public String createTRecord(int masterPort,String firstName, String lastName, String address, String phone, String specialization,
                                Location location, String managerId) {

        try {
            UDPRequest udpRequest = new UDPRequest(ServerManager.getInstance().getCenterServer(serverLocation,masterPort));
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

    /**
     * Implementation of create Student Record
     * @param firstName
     * @param lastName
     * @param courseRegistered
     * @param status
     * @param statusDate
     * @param managerId
     * @return
     */
    public String createSRecord(int masterPort,String firstName, String lastName, String courseRegistered, Status status,
                                String statusDate, String managerId) {

        try {
            UDPRequest udpRequest = new UDPRequest(ServerManager.getInstance().getCenterServer(serverLocation,masterPort));
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

    /**
     * Get Record Counts from all servers
     * @param managerId
     * @return
     */
    public String getRecordCounts(int masterPort,String managerId) {
        try {
            UDPRequest udpRequest = new UDPRequest(ServerManager.getInstance().getCenterServer(serverLocation,masterPort));
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

    /**
     * Edit Record
     * @param recordId
     * @param fieldName
     * @param newValue
     * @param managerId
     * @return
     */
    public String editRecord(int masterPort,String recordId, String fieldName, String newValue, String managerId) {
        try {
            UDPRequest udpRequest = new UDPRequest(ServerManager.getInstance().getCenterServer(serverLocation,masterPort));
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

    /**
     * Transfer Record from one server to other server
     * @param managerId
     * @param recordId
     * @param remoteCenterServerName
     * @return
     */
    public String transferRecord(int masterPort,String managerId, String recordId, String remoteCenterServerName) {
        try {
            UDPRequest udpRequest = new UDPRequest(ServerManager.getInstance().getCenterServer(serverLocation,masterPort));
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
