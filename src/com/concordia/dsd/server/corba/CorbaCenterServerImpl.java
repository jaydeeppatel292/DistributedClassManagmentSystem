package com.concordia.dsd.server.corba;

import CenterServerApp.CenterPOA;
import com.concordia.dsd.global.cmsenum.Location;
import com.concordia.dsd.global.cmsenum.Status;
import com.concordia.dsd.server.generics.CenterServerImpl;
import org.omg.CORBA.ORB;

import java.io.IOException;


class CorbaCenterServerImpl extends CenterPOA {
    private ORB orb;
    private CenterServerImpl<CorbaCenterServerImpl> centerServerCenterImpl;

    /**
     * Constructor: CorbaCenterServerImpl
     * @param location
     * @throws SecurityException
     * @throws IOException
     */
    public CorbaCenterServerImpl(Location location,String host, int port) throws SecurityException, IOException {
        centerServerCenterImpl = new CenterServerImpl<>(location,host, port);
    }

    /**
     * Set ORB
     * @param orb_val
     */
    public void setORB(ORB orb_val) {
        orb = orb_val;
    }

    /**
     * On Server ShutDown Request
     */
    public void shutdown() {
        orb.shutdown(false);
    }

    /**
     * Get CenterServerImpl
     * @return
     */
    public CenterServerImpl<CorbaCenterServerImpl> getCenterServerCenterImpl() {
        return centerServerCenterImpl;
    }

    /**@ovverride
     * Create Teacher Record
     * @param firstName
     * @param lastName
     * @param address
     * @param phone
     * @param specialization
     * @param location
     * @param managerId
     * @return
     */
    @Override
    public String createTRecord(String firstName, String lastName, String address, String phone, String specialization, String location, String managerId) {
        return centerServerCenterImpl.createTRecord(firstName,lastName,address,phone,specialization,Location.valueOf(location), managerId);
    }

    /**@ovverride
     * Create Student Record
     * @param firstName
     * @param lastName
     * @param courseRegistered
     * @param status
     * @param statusDate
     * @param managerId
     * @return
     */
    @Override
    public String createSRecord(String firstName, String lastName, String courseRegistered, String status, String statusDate, String managerId) {
        return centerServerCenterImpl.createSRecord(firstName,lastName,courseRegistered,Status.valueOf(status),statusDate, managerId);
    }

    /**
     * Get Record Count
     * @param managerId
     * @return
     */
    @Override
    public String getRecordCounts(String managerId) {
        return centerServerCenterImpl.getRecordCounts(managerId);
    }

    /**
     * Edit Record
     * @param recordId
     * @param fieldName
     * @param newValue
     * @param managerId
     * @return
     */
    @Override
    public String editRecord(String recordId, String fieldName, String newValue, String managerId) {
        return centerServerCenterImpl.editRecord(recordId,fieldName,newValue, managerId);
    }

    /**
     * Transfer Record
     * @param managerId
     * @param recordId
     * @param remoteCenterServerName
     * @return
     */
    @Override
    public String transferRecord(String managerId, String recordId, String remoteCenterServerName){
        return centerServerCenterImpl.transferRecord(managerId, recordId, remoteCenterServerName);
    }
}