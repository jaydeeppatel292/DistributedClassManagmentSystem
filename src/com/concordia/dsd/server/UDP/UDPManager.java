package com.concordia.dsd.server.UDP;

import com.concordia.dsd.global.cmsenum.Location;
import com.concordia.dsd.global.cmsenum.Status;
import com.concordia.dsd.global.constants.CMSLogMessages;
import com.concordia.dsd.model.ClassMap;
import com.concordia.dsd.model.Record;
import com.concordia.dsd.model.StudentRecord;
import com.concordia.dsd.model.TeacherRecord;
import com.concordia.dsd.server.ServerManager;
import com.concordia.dsd.server.generics.FIFORequestQueueModel;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UDPManager {
    private Location serverLocation;
    private ClassMap recordMap;
    private Logger serverLogger;
    private int myPort;


    public UDPManager(Location serverLocation, ClassMap recordMap, Logger serverLogger, int myPort) {
        this.serverLocation = serverLocation;
        this.recordMap = recordMap;
        this.serverLogger = serverLogger;
        this.myPort = myPort;
    }

    public UDPRequest[] createUDPReqObj(List<Integer> processList){
        UDPRequest[] requests;
        int counter = 0;
        requests = new UDPRequest[processList.size()];
        for(int port : ServerManager.getInstance().getAllBackupServerPort(serverLocation)){
            if(port != myPort){
                try {
                    requests[counter] = new UDPRequest(ServerManager.getInstance().getCenterServer(serverLocation, port));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                requests[counter].start();
                counter++;
            }
        }
        return requests;
    }

    public String sendBackUpProcessRequests(FIFORequestQueueModel requestQueueModel){
        return ServerManager.getInstance().getCenterServer(serverLocation ,ServerManager.getInstance().getMasterServerPort(serverLocation)).sendBackUpProcessRequestFromController(requestQueueModel);
    }

    /**
     * Add teacher record in backup processes
     * @param processList
     * @param firstName
     * @param lastName
     * @param address
     * @param phone
     * @param specialization
     * @param location
     * @param managerId
     * @return
     */
    public String addTeacherRecord(List<Integer> processList, String firstName, String lastName, String address, String phone, String specialization,
                                   Location location, String managerId){

        UDPRequest[] requests = createUDPReqObj(processList);
        for(UDPRequest req : requests){
            req.getCenterServer().createTRecord(firstName, lastName, address, phone, specialization, location, managerId);
        }
        return "SUCCESS";
    }

    /**
     * Add student record in backup processes
     * @param processList
     * @param firstName
     * @param lastName
     * @param courseRegistered
     * @param status
     * @param statusDate
     * @param managerId
     * @return
     */
    public String addStudentRecord(List<Integer> processList, String firstName, String lastName, String courseRegistered, Status status,
                                   String statusDate, String managerId){

        UDPRequest[] requests = createUDPReqObj(processList);
        for(UDPRequest req : requests){
            try {
                req.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            req.getCenterServer().createSRecord(firstName, lastName, courseRegistered, status, statusDate, managerId);
        }

        return "SUCCESS";
    }

    public String getRecordCountFromProcess(List<Integer> processList, String managerId){

        UDPRequest[] requests = createUDPReqObj(processList);
        for(UDPRequest req : requests){
            req.getCenterServer().getRecordCounts(managerId);
        }
        return "SUCCESS";
    }


    public String updateRecords(List<Integer> processList, String recordId, String fieldName, String newValue, String managerId){

        UDPRequest[] requests = createUDPReqObj(processList);
        for(UDPRequest req : requests){
            req.getCenterServer().editRecord(recordId, fieldName, newValue, managerId);
        }
        return "SUCCESS";
    }

    public String transferRecordsFromProcess(List<Integer> processList, String managerId, String recordId, String remoteCenterServerName){

        UDPRequest[] requests = createUDPReqObj(processList);
        for(UDPRequest req : requests){
            req.getCenterServer().transferRecord(managerId, recordId, remoteCenterServerName);
        }
        return "SUCCESS";
    }

    /**
     * Implementation of Get Record Count From All server
     *
     * @param managerId
     * @return
     */
    public String getRecordCounts(String managerId) {
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
                    int port = ServerManager.getInstance().getMasterServerPort(location);
                    requests[counter] = new UDPRequest(ServerManager.getInstance().getCenterServer(location, port));
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
            stringBuffer.append(" ").append(request.getResponseFromUDP());
        }
        serverLogger.log(Level.INFO, CMSLogMessages.RECORD_COUNT + " requested by manager: " + managerId, stringBuffer.toString());
        System.out.println("Record Count : " + stringBuffer.toString());
        return stringBuffer.toString();
    }

    /**
     * Implementation of Transfer Record from one server to another
     *
     * @param managerId
     * @param record
     * @param remoteCenterServerName
     * @param typeOfRec
     */
    public void transferRecord(String managerId, Record record, String remoteCenterServerName, char typeOfRec) {
        try {
            int port = ServerManager.getInstance().getMasterServerPort(Location.valueOf(remoteCenterServerName));
            UDPRequest serverObject = new UDPRequest(ServerManager.getInstance().getCenterServer(Location.valueOf(remoteCenterServerName.toUpperCase()), port));
            serverObject.start();
            if (typeOfRec == 'S') {
                StudentRecord studentRecord = (StudentRecord) record;
                serverObject.getCenterServer().createSRecord(studentRecord.getFirstName(), studentRecord.getLastName(), studentRecord.getCourseRegistered(), studentRecord.getStatus(),
                        studentRecord.getStatusDate(), managerId);
            } else if (typeOfRec == 'T') {
                TeacherRecord teacherRecord = (TeacherRecord) record;
                serverObject.getCenterServer().createTRecord(teacherRecord.getFirstName(), teacherRecord.getLastName(), teacherRecord.getAddress(), teacherRecord.getPhone(),
                        teacherRecord.getSpecialization(), teacherRecord.getLocation(), managerId);
            }
            try {
                serverObject.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            serverLogger.log(Level.INFO, String.format(CMSLogMessages.TRANSFER_RECORD_SUCCESS, record.getRecordId(), managerId));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initElection(List<Integer> processIdList) {
        UDPRequest[] udpRequests = new UDPRequest[processIdList.size() - 1];
        for (int i = 0; i < processIdList.size(); i++) {
            try {
                udpRequests[i] = new UDPRequest(ServerManager.getInstance().getCenterServer(serverLocation, processIdList.get(i)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (UDPRequest request : udpRequests) {
            try {
                request.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
