package com.concordia.dsd.server.interfaces;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public interface WSCenterServer {
    @WebMethod
    String createTRecord (String firstName, String lastName, String address, String phone, String specialization, String location, String managerId);
    @WebMethod
    String createSRecord (String firstName, String lastName, String courseRegistered, String status, String statusDate, String managerId);
    @WebMethod
    String getRecordCounts (String managerId);
    @WebMethod
    String editRecord (String recordId, String fieldName, String newValue, String managerId);
    @WebMethod
    String transferRecord (String managerID, String recordID, String remoteCenterServerName);
}
