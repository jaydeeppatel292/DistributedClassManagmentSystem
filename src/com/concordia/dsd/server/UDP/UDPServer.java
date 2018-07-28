package com.concordia.dsd.server.UDP;

import com.concordia.dsd.global.constants.CMSConstants;
import com.concordia.dsd.global.constants.CMSLogMessages;
import com.concordia.dsd.model.Record;
import com.concordia.dsd.model.StudentRecord;
import com.concordia.dsd.model.TeacherRecord;
import com.concordia.dsd.server.generics.CenterServerImpl;
import com.concordia.dsd.server.generics.FIFORequestQueueModel;
import com.concordia.dsd.server.interfaces.UDPServerInterface;
import com.concordia.dsd.utils.LoggingUtil;
import com.concordia.dsd.utils.SerializingUtil;

import java.io.IOException;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UDPServer implements UDPServerInterface, Runnable {

    private DatagramSocket socket = null;
    private CenterServerImpl centerServer;
    private Logger logger = null;
    private int udpPort;
    private String udpHostAddress;

    /**
     * Constructor UDPServer
     *
     * @param centerServerImpl
     * @throws SecurityException
     * @throws IOException
     */
    public UDPServer(CenterServerImpl centerServerImpl, String udpHostAddress, int port) throws SecurityException, IOException {
        super();
        this.udpHostAddress = udpHostAddress;
        this.udpPort = port;
        centerServer = centerServerImpl;
        logger = LoggingUtil.getInstance().getServerLogger(centerServer.getLocation());
        initializeServerSocket();
    }

    @Override
    public void run() {
        logger.log(Level.INFO, String.format(CMSLogMessages.UDP_SERVER_INIT, centerServer.getLocation().toString()));
        byte[] buffer;
        DatagramPacket request;
        DatagramSocket datagramSocket = null;
        try {
            while (true) {
                try {
                    buffer = new byte[1000];
                    request = new DatagramPacket(buffer, buffer.length);
                    socket.receive(request);
                    System.out.println("UDP REQUEST received!! PORT::" + request.getPort());

                    FIFORequestQueueModel receivedObj = SerializingUtil.getInstance().getFIFOObjectFromSerialized(request.getData());
                    //messageType = MessageType.valueOf(new String(request.getData()));
                    System.out.println("RECEIVED OBJ:" + receivedObj.toString());
                    byte[] responseData = null;
                    datagramSocket = new DatagramSocket();
                    if (receivedObj.isSyncRequest()) {
                        System.out.println("inside insync");
                        receivedObj.setSyncRequest(false);
                        responseData = centerServer.sendBackUpProcessRequestFromController(receivedObj).getBytes();
                        datagramSocket.send(new DatagramPacket(responseData, responseData.length, request.getAddress(),
                                request.getPort()));
                    } else {
                        switch (receivedObj.getRequestType()) {
                            case GET_RECORD:
                                // Sending back record count by requested client UDPRequest
                                Record record = centerServer.getRecordByRecordId(receivedObj.getRecordId());
                                if (record instanceof StudentRecord) {
                                    StudentRecord studentRecord = (StudentRecord) record;
                                    responseData = SerializingUtil.getInstance().getSerializedObject(studentRecord);
                                } else if (record instanceof TeacherRecord) {
                                    TeacherRecord teacherRecord = (TeacherRecord) record;
                                    responseData = SerializingUtil.getInstance().getSerializedObject(teacherRecord);
                                } else {
                                    responseData = "Record Not Found".getBytes();
                                }
                                datagramSocket.send(new DatagramPacket(responseData, responseData.length, request.getAddress(),
                                        request.getPort()));
                                break;
                            case GET_RECORD_COUNT:
                                // Sending back record count by requested client UDPRequest
                                System.out.println("inside switch in UDP server");
                                responseData = centerServer.getRecordCounts(receivedObj.getManagerId()).getBytes();
                                datagramSocket.send(new DatagramPacket(responseData, responseData.length, request.getAddress(),
                                        request.getPort()));
                                break;
                            case GET_RECORD_COUNT_SUBS:
                                // Sending back record count by requested client UDPRequest
                                System.out.println("inside switch in UDP server");
                                responseData = centerServer.getRecordMap().getRecordsCount().toString().getBytes();
                                datagramSocket.send(new DatagramPacket(responseData, responseData.length, request.getAddress(),
                                        request.getPort()));
                                break;
                            case TRANSFER_RECORD:
                                responseData = centerServer.transferRecord(receivedObj.getManagerId(), receivedObj.getRecordId(), receivedObj.getCenterServerName()).getBytes();
                                datagramSocket.send(new DatagramPacket(responseData, responseData.length, request.getAddress(),
                                        request.getPort()));
                                break;
                            case UPDATE_RECORD:
                                responseData = centerServer.editRecord(receivedObj.getRecordId(), receivedObj.getFieldName(), receivedObj.getNewValue(), receivedObj.getManagerId()).getBytes();
                                datagramSocket.send(new DatagramPacket(responseData, responseData.length, request.getAddress(),
                                        request.getPort()));
                                break;
                            case CREATE_S_RECORD:
                                responseData = centerServer.createSRecord(receivedObj.getStudentRecord().getFirstName(), receivedObj.getStudentRecord().getLastName(), receivedObj.getStudentRecord().getCourseRegistered(), receivedObj.getStudentRecord().getStatus(), receivedObj.getStudentRecord().getStatusDate(), receivedObj.getManagerId()).getBytes();
                                datagramSocket.send(new DatagramPacket(responseData, responseData.length, request.getAddress(),
                                        request.getPort()));
                                break;
                            case CREATE_T_RECORD:
                                responseData = centerServer.createTRecord(receivedObj.getTeacherRecord().getFirstName(), receivedObj.getTeacherRecord().getLastName(), receivedObj.getTeacherRecord().getAddress(), receivedObj.getTeacherRecord().getPhone(), receivedObj.getTeacherRecord().getSpecialization(), receivedObj.getTeacherRecord().getLocation(), receivedObj.getManagerId()).getBytes();
                                datagramSocket.send(new DatagramPacket(responseData, responseData.length, request.getAddress(),
                                        request.getPort()));
                                break;
                            case DELETE_RECORD:
                                responseData = null;
                                if (receivedObj.getTeacherRecord() == null) {
                                    responseData = centerServer.deleteRecord(receivedObj.getStudentRecord()).getBytes();
                                } else if (receivedObj.getStudentRecord() == null) {
                                    responseData = centerServer.deleteRecord(receivedObj.getTeacherRecord()).getBytes();
                                }
                                datagramSocket.send(new DatagramPacket(responseData, responseData.length, request.getAddress(),
                                        request.getPort()));
                                break;
                            case ELECTION:
                                if (request.getPort() < centerServer.getUdpPort()) {
                                    responseData = CMSConstants.OK_MESSAGE.getBytes();
                                    datagramSocket.send(new DatagramPacket(responseData, responseData.length, request.getAddress(),
                                            request.getPort()));
                                    boolean isCoordinator = centerServer.getUdpManager().initElection(centerServer.getLocation(), receivedObj.getProcessIdList());
                                    if (isCoordinator) {
                                        centerServer.getUdpManager().sendCoordinationMessage();
                                    }
                                }
                                break;
                            case COORDINATOR:
                                logger.log(Level.INFO, String.format(CMSLogMessages.COORDINATOR_NOTIFY_MESSAGE, centerServer.getUdpPort(), request.getPort()));
                                break;

                            case PING_SERVER:
                                responseData = CMSConstants.SERVER_UP_MESSAGE.getBytes();
                                datagramSocket.send(new DatagramPacket(responseData, responseData.length, request.getAddress(),
                                         request.getPort()));
                                break;
                        }
                    }
                } catch (IOException e) {
                    logger.log(Level.SEVERE, e.getMessage());
                } finally {
                    if (datagramSocket != null) {
                        datagramSocket.close();
                    }
                }
            }
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }

    @Override
    public void initializeServerSocket() {
        try {
            socket = new DatagramSocket(getCenterServerPort());
        } catch (SocketException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
    }

    @Override
    public int getCenterServerPort() {
        return udpPort;
    }

    @Override
    public InetAddress getInetAddress() {
        try {
            return InetAddress.getByName(udpHostAddress);
        } catch (UnknownHostException e) {
            logger.log(Level.SEVERE, e.getMessage());
            return null;
        }
    }
}
