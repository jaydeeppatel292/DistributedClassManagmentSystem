package com.concordia.dsd.server.generics;

import com.concordia.dsd.global.cmsenum.Location;
import com.concordia.dsd.global.cmsenum.Status;
import com.concordia.dsd.global.constants.CMSLogMessages;
import com.concordia.dsd.model.ClassMap;
import com.concordia.dsd.model.Record;
import com.concordia.dsd.model.StudentRecord;
import com.concordia.dsd.model.TeacherRecord;
import com.concordia.dsd.server.UDP.UDPManager;
import com.concordia.dsd.server.UDP.UDPServer;
import com.concordia.dsd.server.manager.StudentManager;
import com.concordia.dsd.server.manager.TeacherManager;
import com.concordia.dsd.utils.LoggingUtil;

import java.io.IOException;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CenterServerImpl<T> {
    private int udpPort;
    private InetAddress ipAddress;
    private UDPServer udpServer;
    private Logger serverLogger;
    private Location location;
    private ClassMap recordMap;
    private StudentManager studentManager;
    private TeacherManager teacherManager;
    private UDPManager udpManager;

    /**
     * Initialize Constructor
     * Also Start Udp Server
     * @param location
     * @throws SecurityException
     * @throws IOException
     */
    public CenterServerImpl(Location location) throws SecurityException, IOException {
        this.location = location;
        try {
            serverLogger = LoggingUtil.getInstance().getServerLogger(this.location);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        recordMap = new ClassMap();
        studentManager = new StudentManager(recordMap, serverLogger);
        teacherManager = new TeacherManager(recordMap, serverLogger);
        udpManager = new UDPManager(location,recordMap, serverLogger);
        udpServer = new UDPServer(this);

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

    public StudentManager getStudentManager() {
        return studentManager;
    }

    public TeacherManager getTeacherManager() {
        return teacherManager;
    }

    public UDPManager getUdpManager() {
        return udpManager;
    }

    public ClassMap getRecordMap() {
        return recordMap;
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

    public UDPServer getUdpServer() {
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
        String recordId = null;
        Record record = getTeacherManager().insertRecord(firstName, lastName, address, phone, specialization, location, managerId);
        if (record != null) {
            recordId = record.getRecordId();
        }
        return recordId;
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
        String recordId = null;
        Record record = getStudentManager().insertRecord(firstName, lastName, courseRegistered, status, statusDate, managerId);
        if (record != null) {
            recordId = record.getRecordId();
        }
        return recordId;
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
        Record record = getRecordMap().lookupRecord(recordId);
        if (record instanceof StudentRecord) {
            getStudentManager().updateRecord(record,recordId,fieldName,newValue, managerId);
            return "TRUE";
        }else if(record instanceof TeacherRecord){
            getTeacherManager().updateRecord(record,recordId,fieldName,newValue, managerId);
            return "TRUE";
        }else {
            getServerLogger().log(Level.SEVERE, CMSLogMessages.RECORD_NOT_FOUND, recordId);
            return String.format(CMSLogMessages.RECORD_NOT_FOUND, recordId);
        }

    }

    /**
     * Transfer Record from one server to other server
     * @param managerId
     * @param recordId
     * @param remoteCenterServerName
     * @return
     */
    public String transferRecord(String managerId, String recordId, String remoteCenterServerName) {
        Record record = getRecordMap().lookupRecord(recordId);
        char typeOfRec;
        String returnValue = "";
        if (record instanceof StudentRecord) {
            typeOfRec='S';
            getUdpManager().transferRecord(managerId, record, remoteCenterServerName, typeOfRec);
            returnValue=String.format(CMSLogMessages.TRANSFER_RECORD_SUCCESS, recordId, managerId);
            recordMap.deleteRecord(record);
        }else if(record instanceof TeacherRecord){
            typeOfRec='T';
            getUdpManager().transferRecord(managerId, record, remoteCenterServerName, typeOfRec);
            returnValue=String.format(CMSLogMessages.TRANSFER_RECORD_SUCCESS, recordId, managerId);
            recordMap.deleteRecord(record);
        }else {
            getServerLogger().log(Level.SEVERE, CMSLogMessages.RECORD_NOT_FOUND, recordId);
            returnValue =  String.format(CMSLogMessages.RECORDID_NOT_FOUND, recordId);
        }
        return  returnValue;
    }
}
