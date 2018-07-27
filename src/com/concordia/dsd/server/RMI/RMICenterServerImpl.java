package com.concordia.dsd.server.RMI;

import com.concordia.dsd.global.cmsenum.Location;
import com.concordia.dsd.global.cmsenum.Status;
import com.concordia.dsd.global.constants.CMSLogMessages;
import com.concordia.dsd.model.Record;
import com.concordia.dsd.model.StudentRecord;
import com.concordia.dsd.model.TeacherRecord;
import com.concordia.dsd.server.generics.CenterServerImpl;
import com.concordia.dsd.server.interfaces.CenterServer;
import java.io.IOException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.logging.Level;

public class RMICenterServerImpl extends UnicastRemoteObject implements CenterServer {

    private static final long serialVersionUID = 667444777988616262L;
    private CenterServerImpl<RMICenterServerImpl> centerServerCenterImpl;

    protected RMICenterServerImpl(Location location) throws SecurityException, IOException {
        super();
        centerServerCenterImpl = new com.concordia.dsd.server.generics.CenterServerImpl<>(location,"localhost",0);
    }

    public CenterServerImpl<RMICenterServerImpl> getCenterServerCenterImpl() {
        return centerServerCenterImpl;
    }

    @Override
    public String createTRecord(String firstName, String lastName, String address, String phone, String specialization,
                                Location location, String managerId) {
        return centerServerCenterImpl.createTRecord(firstName,lastName,address,phone,specialization,location, managerId);
    }

    @Override
    public String createSRecord(String firstName, String lastName, String courseRegistered, Status status,
                                String statusDate, String managerId) {
        return centerServerCenterImpl.createSRecord(firstName,lastName,courseRegistered,status,statusDate, managerId);
    }

    @Override
    public String getRecordCounts(String managerId) {
        return centerServerCenterImpl.getRecordCounts(managerId);
    }

    @Override
    public void editRecord(String recordId, String fieldName, String newValue, String managerId) {
        centerServerCenterImpl.editRecord(recordId,fieldName,newValue, managerId);
    }
}
