package com.concordia.dsd.server;

import CenterServerApp.CenterPOA;
import com.concordia.dsd.global.cmsenum.Location;
import com.concordia.dsd.global.cmsenum.Status;
import com.concordia.dsd.server.generics.CenterServerImpl;
import org.omg.CORBA.ORB;

import java.io.IOException;


public class FrontEndServer extends CenterPOA {
    private ORB orb;
    private FrontEndImpl frontEndImpl;

    /**
     * Constructor: CorbaCenterServerImpl
     * @param location
     * @throws SecurityException
     * @throws IOException
     */
    public FrontEndServer(Location location,String hostAddress,int port) throws SecurityException, IOException {
        frontEndImpl = new FrontEndImpl(location,hostAddress,port);
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
    public FrontEndImpl getFrontEndImpl() {
        return frontEndImpl;
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
        return frontEndImpl.createTRecord(firstName,lastName,address,phone,specialization,Location.valueOf(location), managerId);
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
        return frontEndImpl.createSRecord(firstName,lastName,courseRegistered,Status.valueOf(status),statusDate, managerId);
    }

    /**
     * Get Record Count
     * @param managerId
     * @return
     */
    @Override
    public String getRecordCounts(String managerId) {
        return frontEndImpl.getRecordCounts(managerId);
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
        return frontEndImpl.editRecord(recordId,fieldName,newValue, managerId);
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
        return frontEndImpl.transferRecord(managerId, recordId, remoteCenterServerName);
    }
}