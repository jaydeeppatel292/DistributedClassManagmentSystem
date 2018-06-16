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
import org.omg.CORBA.ORB;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
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
    public CenterServerImpl(Location location) throws SecurityException, IOException {
        this.location = location;
        try {
            serverLogger = LoggingUtil.getInstance().getLogger(this.location);
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
        initCenterServerModel();
        new Thread(udpServer).start();

    }
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

    public String createTRecord(String firstName, String lastName, String address, String phone, String specialization,
                                Location location) {
        String recordId = null;
        Record record = getTeacherManager().insertRecord(firstName, lastName, address, phone, specialization, location);
        if (record != null) {
            recordId = record.getRecordId();
        }
        return recordId;
    }

    public String createSRecord(String firstName, String lastName, List<String> courseRegistered, Status status,
                                String statusDate) {
        String recordId = null;
        Record record = getStudentManager().insertRecord(firstName, lastName, courseRegistered, status, statusDate);
        if (record != null) {
            recordId = record.getRecordId();
        }
        return recordId;
    }

    public String getRecordCounts() {
        return getUdpManager().getRecordCounts();
    }

    public void editRecord(String recordId, String fieldName, String newValue) {
        Record record = getRecordMap().lookupRecord(recordId);
        if (record instanceof StudentRecord) {
            getStudentManager().updateRecord(record,recordId,fieldName,newValue);
        }else if(record instanceof TeacherRecord){
            getTeacherManager().updateRecord(record,recordId,fieldName,newValue);
        }else {
            getServerLogger().log(Level.SEVERE, CMSLogMessages.RECORD_NOT_FOUND, recordId);
        }
    }
}
