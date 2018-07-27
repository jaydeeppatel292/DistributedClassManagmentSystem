package com.concordia.dsd.server;

import com.concordia.dsd.global.cmsenum.Location;
import com.concordia.dsd.global.cmsenum.Status;
import com.concordia.dsd.global.constants.CMSLogMessages;
import com.concordia.dsd.model.ClassMap;
import com.concordia.dsd.model.Record;
import com.concordia.dsd.model.StudentRecord;
import com.concordia.dsd.model.TeacherRecord;
import com.concordia.dsd.server.UDP.FrontEndUDPManager;
import com.concordia.dsd.server.UDP.FrontEndUDPServer;
import com.concordia.dsd.server.UDP.UDPServer;
import com.concordia.dsd.utils.LoggingUtil;

import java.io.IOException;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FrontEndImpl{
    private int udpPort;
    private InetAddress ipAddress;
    private FrontEndUDPServer udpServer;
    private Logger serverLogger;
    private Location location;
    private FrontEndUDPManager udpManager;
    private int masterPort;
    private int myPort;
    private String masterHostAddress;


    public String getMasterHostAddress() {
        return masterHostAddress;
    }

    public void setMasterHostAddress(String masterHostAddress) {
        this.masterHostAddress = masterHostAddress;
    }

    public int getMasterPort() {
        return masterPort;
    }

    public void setMasterPort(int masterPort) {
        this.masterPort = masterPort;
    }

    public int getMyPort() {
        return myPort;
    }

    public void setMyPort(int myPort) {
        this.myPort = myPort;
    }

    /**
     * Initialize Constructor
     * Also Start Udp Server
     * @param location
     * @throws SecurityException
     * @throws IOException
     */
    public FrontEndImpl(Location location) throws SecurityException, IOException {
        this.location = location;
        try {
            serverLogger = LoggingUtil.getInstance().getServerLogger(this.location);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        udpManager = new FrontEndUDPManager(location, serverLogger);
        udpServer = new FrontEndUDPServer(this);

        // Initialize UDP Server
        initCenterServerModel();

        // Start UDP Server
        new Thread(udpServer).start();

    }

    /**
     * Initialize UDP Server
     */
    private void initCenterServerModel() {
        setUdpPort(udpServer.getCenterServerPort());
        setIpAddress(udpServer.getInetAddress());
    }

    public FrontEndUDPManager getUdpManager() {
        return udpManager;
    }

    public int getUdpPort() {
        return udpPort;
    }

    public void setUdpPort(int udpPort) {
        this.udpPort = udpPort;
    }

    public InetAddress getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(InetAddress ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public FrontEndUDPServer getUdpServer() {
        return udpServer;
    }

    public Logger getServerLogger() {
        return serverLogger;
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
    public String createTRecord(String firstName, String lastName, String address, String phone, String specialization,
                                Location location, String managerId) {
        return getUdpManager().createTRecord(firstName,lastName,address,phone,specialization,location,managerId);
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
    public String createSRecord(String firstName, String lastName, String courseRegistered, Status status,
                                String statusDate, String managerId) {
        return getUdpManager().createSRecord(firstName,lastName,courseRegistered,status,statusDate,managerId);
    }

    /**
     * Get Record Counts from all servers
     * @param managerId
     * @return
     */
    public String getRecordCounts(String managerId) {
        return getUdpManager().getRecordCounts(managerId);
    }

    /**
     * Edit Record
     * @param recordId
     * @param fieldName
     * @param newValue
     * @param managerId
     * @return
     */
    public String editRecord(String recordId, String fieldName, String newValue, String managerId) {
        return getUdpManager().editRecord(recordId,fieldName,newValue,managerId);
    }

    /**
     * Transfer Record from one server to other server
     * @param managerId
     * @param recordId
     * @param remoteCenterServerName
     * @return
     */
    public String transferRecord(String managerId, String recordId, String remoteCenterServerName) {
        return getUdpManager().transferRecord(managerId,recordId,remoteCenterServerName);
    }
}
