package com.concordia.dsd.server.UDP;

import com.concordia.dsd.global.cmsenum.Location;
import com.concordia.dsd.global.cmsenum.Status;
import com.concordia.dsd.global.constants.CMSLogMessages;
import com.concordia.dsd.global.enums.RequestType;
import com.concordia.dsd.model.ClassMap;
import com.concordia.dsd.model.Record;
import com.concordia.dsd.model.StudentRecord;
import com.concordia.dsd.model.TeacherRecord;
import com.concordia.dsd.server.ServerManager;
import com.concordia.dsd.server.generics.CenterServerImpl;
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

    public UDPRequest[] createUDPReqObj(List<ServerManager.CenterServerInfo> processList,FIFORequestQueueModel fifoRequestQueueModel){
        UDPRequest[] requests;
        int counter = 0;
        requests = new UDPRequest[processList.size()];
        for(ServerManager.CenterServerInfo centerServerInfo: processList){
            if(centerServerInfo.getPort() != myPort){
                try {
                    requests[counter] = new UDPRequest(centerServerInfo.getLocation(),centerServerInfo.getHostAddress(),centerServerInfo.getPort(),fifoRequestQueueModel);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                requests[counter].start();
                counter++;
            }
        }
        return requests;
    }

    /**
     * Add student record in backup processes
     * @param processList
     * @return
     */
    public String sendBackUpProcessRequestFromController(List<ServerManager.CenterServerInfo> processList, FIFORequestQueueModel fifoRequestQueueModel){

        UDPRequest[] requests = createUDPReqObj(processList,fifoRequestQueueModel);
        for(UDPRequest req : requests){
            try {
                req.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return "SUCCESS";
    }

    public String sendBackUpProcessRequests(FIFORequestQueueModel requestQueueModel){
        return ServerManager.getInstance().getCenterServer(serverLocation ,ServerManager.getInstance().getMasterServerPort(serverLocation)).sendBackUpProcessRequestFromController(requestQueueModel);
    }

    /**
     * Add teacher record in backup processes
     */
    public String addTeacherRecord(List<ServerManager.CenterServerInfo> processList, FIFORequestQueueModel requestObj){

        UDPRequest[] requests = createUDPReqObj(processList,requestObj);
        for(UDPRequest req : requests){
            try {
                req.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return "SUCCESS";
    }


    /*public String transferRecordsFromProcess(List<ServerManager.CenterServerInfo> processList, FIFORequestQueueModel fifoRequestQueueModel){

        UDPRequest[] requests = createUDPReqObj(processList,fifoRequestQueueModel);
        for(UDPRequest req : requests){
            req.getCenterServer().transferRecord(managerId, recordId, remoteCenterServerName);
        }
        return "SUCCESS";
    }*/

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
                    ServerManager.CenterServerInfo centerInfo = ServerManager.getInstance().getMasterServerInfo(location);
                    FIFORequestQueueModel reqObj = new FIFORequestQueueModel(RequestType.GET_RECORD_COUNT, managerId, location);
                    requests[counter] = new UDPRequest(location, centerInfo.getHostAddress(), centerInfo.getPort(), reqObj);
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
            stringBuffer.append(", ").append(request.getServerLocation());
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
            ServerManager.CenterServerInfo centerInfo = ServerManager.getInstance().getMasterServerInfo(Location.valueOf(remoteCenterServerName));
            UDPRequest serverObject = null;
            if (typeOfRec == 'S') {
                FIFORequestQueueModel reqModel = new FIFORequestQueueModel(RequestType.CREATE_S_RECORD, record, managerId,Location.valueOf(remoteCenterServerName));
                serverObject = new UDPRequest(Location.valueOf(remoteCenterServerName), centerInfo.getHostAddress(), centerInfo.getPort(), reqModel);
                serverObject.start();
            } else if (typeOfRec == 'T') {
                FIFORequestQueueModel reqModel = new FIFORequestQueueModel(RequestType.CREATE_T_RECORD, record, managerId,Location.valueOf(remoteCenterServerName));
                serverObject = new UDPRequest(Location.valueOf(remoteCenterServerName), centerInfo.getHostAddress(), centerInfo.getPort(), reqModel);
                serverObject.start();
            }
            serverObject.join();
            serverLogger.log(Level.INFO, String.format(CMSLogMessages.TRANSFER_RECORD_SUCCESS, record.getRecordId(), managerId));
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (InterruptedException e) {
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
