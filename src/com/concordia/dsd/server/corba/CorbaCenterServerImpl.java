package com.concordia.dsd.server.corba;

import CenterServerApp.CenterPOA;
import com.concordia.dsd.global.cmsenum.Location;
import com.concordia.dsd.global.cmsenum.Status;
import com.concordia.dsd.global.constants.CMSLogMessages;
import com.concordia.dsd.model.Record;
import com.concordia.dsd.model.StudentRecord;
import com.concordia.dsd.model.TeacherRecord;
import com.concordia.dsd.server.generics.CenterServerImpl;
import org.omg.CORBA.ORB;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;


class CorbaCenterServerImpl extends CenterPOA {
    private ORB orb;
    private CenterServerImpl<CorbaCenterServerImpl> centerServerCenterImpl;
    public CorbaCenterServerImpl(Location location) throws SecurityException, IOException {
        centerServerCenterImpl = new CenterServerImpl<>(location);
    }
    public void setORB(ORB orb_val) {
        orb = orb_val;
    }


    public void shutdown() {
        orb.shutdown(false);
    }


    @Override
    public String createTRecord(String firstName, String lastName, String address, String phone, String specialization, String location) {
        //TODO exception handling for invalid location value ..
        return centerServerCenterImpl.createTRecord(firstName,lastName,address,phone,specialization,Location.valueOf(location));
    }

    @Override
    public String createSRecord(String firstName, String lastName, String courseRegistered, String status, String statusDate) {
        List<String> courseList = Arrays.asList(courseRegistered.split(","));
        //TODO exception handling for invalid status value ..
        return centerServerCenterImpl.createSRecord(firstName,lastName,courseList,Status.valueOf(status),statusDate);
    }
    @Override
    public String getRecordCounts() {
        return centerServerCenterImpl.getRecordCounts();
    }

    @Override
    public void editRecord(String recordId, String fieldName, String newValue) {
        centerServerCenterImpl.editRecord(recordId,fieldName,newValue);
    }
}