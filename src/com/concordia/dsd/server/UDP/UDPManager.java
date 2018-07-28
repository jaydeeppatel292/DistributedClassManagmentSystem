package com.concordia.dsd.server.UDP;

import com.concordia.dsd.global.cmsenum.Location;
import com.concordia.dsd.global.constants.CMSLogMessages;
import com.concordia.dsd.global.enums.FrontEndNotify;
import com.concordia.dsd.global.enums.RequestType;
import com.concordia.dsd.model.ClassMap;
import com.concordia.dsd.model.Record;
import com.concordia.dsd.server.ServerManager;
import com.concordia.dsd.server.generics.FIFORequestQueueModel;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
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

    public UDPRequest[] createUDPReqObj(List<ServerManager.CenterServerInfo> processList, FIFORequestQueueModel fifoRequestQueueModel) {
        UDPRequest[] requests;
        int counter = 0;
        requests = new UDPRequest[processList.size()];
        for (ServerManager.CenterServerInfo centerServerInfo : processList) {
            if (centerServerInfo.getPort() != myPort) {
                try {
                    requests[counter] = new UDPRequest(centerServerInfo.getLocation(), centerServerInfo.getHostAddress(), centerServerInfo.getPort(), fifoRequestQueueModel);
                    requests[counter].start();
                    counter++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return requests;
    }

    /**
     * Add student record in backup processes
     *
     * @param processList
     * @return
     */
    public String sendBackUpProcessRequestFromController(List<ServerManager.CenterServerInfo> processList, FIFORequestQueueModel fifoRequestQueueModel) {

        UDPRequest[] requests = createUDPReqObj(processList, fifoRequestQueueModel);
        for (UDPRequest req : requests) {
            try {
                req.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return "SUCCESS";
    }

    public String sendBackUpProcessRequests(FIFORequestQueueModel requestQueueModel) {
        return ServerManager.getInstance().getCenterServer(serverLocation, ServerManager.getInstance().getMasterServerPort(serverLocation)).sendBackUpProcessRequestFromController(requestQueueModel);
    }

    /**
     * Add teacher record in backup processes
     */
    public String addTeacherRecord(List<ServerManager.CenterServerInfo> processList, FIFORequestQueueModel requestObj) {

        UDPRequest[] requests = createUDPReqObj(processList, requestObj);
        for (UDPRequest req : requests) {
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
        List<UDPRequest> requests = new ArrayList<>();
        int counter = 0;
        for (Location location : Location.values()) {
            if (location == serverLocation) {
                recordCount = location.toString() + " " + recordMap.getRecordsCount();
                stringBuffer.append(recordCount);
            } else if (location != Location.FE) {
                try {
                    ServerManager.CenterServerInfo centerInfo = ServerManager.getInstance().getMasterServerInfo(location);
                    FIFORequestQueueModel reqObj = new FIFORequestQueueModel(RequestType.GET_RECORD_COUNT_SUBS, managerId, location);
                    requests.add(new UDPRequest(location, centerInfo.getHostAddress(), centerInfo.getPort(), reqObj));
                } catch (SecurityException e) {
                    serverLogger.log(Level.SEVERE, e.getMessage());
                } catch (IOException e) {
                    serverLogger.log(Level.SEVERE, e.getMessage());
                }
                requests.get(counter).start();
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
                FIFORequestQueueModel reqModel = new FIFORequestQueueModel(RequestType.CREATE_S_RECORD, record, managerId, Location.valueOf(remoteCenterServerName));
                serverObject = new UDPRequest(Location.valueOf(remoteCenterServerName), centerInfo.getHostAddress(), centerInfo.getPort(), reqModel);
                serverObject.start();
            } else if (typeOfRec == 'T') {
                FIFORequestQueueModel reqModel = new FIFORequestQueueModel(RequestType.CREATE_T_RECORD, record, managerId, Location.valueOf(remoteCenterServerName));
                serverObject = new UDPRequest(Location.valueOf(remoteCenterServerName), centerInfo.getHostAddress(), centerInfo.getPort(), reqModel);
                serverObject.start();
            }
            serverObject.join();
            serverLogger.log(Level.INFO, String.format(CMSLogMessages.TRANSFER_RECORD_SUCCESS, record.getRecordId(), managerId));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public boolean initElection(Location location, List<Integer> processIdList) {
        boolean messageSent = false;
        for (int i = 0; i < processIdList.size(); i++) {
            if (processIdList.get(i) != myPort && processIdList.get(i) > myPort) {
                try {
                    FIFORequestQueueModel model = new FIFORequestQueueModel(RequestType.ELECTION, processIdList);
                    ServerManager.CenterServerInfo serverInfo = ServerManager.getInstance().getServerInfo(location, processIdList.get(i));
                    if (serverInfo != null) {
                        messageSent = true;
                        UDPRequest udpRequest = new UDPRequest(serverInfo.getLocation(), serverInfo.getHostAddress(), serverInfo.getPort(), model);
                        udpRequest.start();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return messageSent;
    }


    public void sendCoordinationMessage() {
        serverLogger.log(Level.INFO, String.format(CMSLogMessages.COORDINATOR_FOUND, myPort));
        notifyElectionCompleteToFrontend();
        ServerManager.getInstance().setNewMasterServer(serverLocation, myPort);
        for (Integer port : ServerManager.getInstance().getAllBackupServerPort(serverLocation)) {
            FIFORequestQueueModel model = new FIFORequestQueueModel(RequestType.COORDINATOR);
            ServerManager.CenterServerInfo serverInfo = ServerManager.getInstance().getServerInfo(serverLocation, port);
            try {
                UDPRequest udpRequest = new UDPRequest(serverInfo.getLocation(), serverInfo.getHostAddress(), serverInfo.getPort(), model);
                udpRequest.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void notifyElectionCompleteToFrontend() {
        FrontEndUDPServer frontEndUDPServer = ServerManager.getInstance().getFrontEndServer().getFrontEndImpl().getUdpServer();
        InetAddress address = frontEndUDPServer.getInetAddress();
        try {
            DatagramSocket socket = new DatagramSocket();
            byte[] data = String.valueOf(FrontEndNotify.BULLY_COMPLETED).getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length, address, frontEndUDPServer.getCenterServerPort());
            socket.send(packet);

            data = new byte[1000];
            DatagramPacket receivedPacket = new DatagramPacket(data, data.length);
            socket.receive(receivedPacket);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
