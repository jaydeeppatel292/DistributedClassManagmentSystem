package com.concordia.dsd.server.UDP;

import com.concordia.dsd.global.cmsenum.Location;
import com.concordia.dsd.global.cmsenum.MessageType;
import com.concordia.dsd.global.constants.CMSConstants;
import com.concordia.dsd.global.constants.CMSLogMessages;
import com.concordia.dsd.global.constants.ServerConfig;
import com.concordia.dsd.server.ServerManager;
import com.concordia.dsd.server.corba.bully.LeaderOperationInterface;
import com.concordia.dsd.server.generics.CenterServerImpl;
import com.concordia.dsd.server.generics.FIFORequestQueueModel;
import com.concordia.dsd.server.interfaces.UDPServerInterface;
import com.concordia.dsd.utils.LoggingUtil;
import com.concordia.dsd.utils.SerializingUtil;

import java.io.IOException;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UDPServer implements UDPServerInterface, LeaderOperationInterface, Runnable {

    private DatagramSocket socket = null;
    private CenterServerImpl centerServer;
    private Logger logger = null;
    private MessageType messageType;
    private int udpPort;
    private String udpHostAddress;
    /**
     * Constructor UDPServer
     *
     * @param centerServerImpl
     * @throws SecurityException
     * @throws IOException
     */
    public UDPServer(CenterServerImpl centerServerImpl,String udpHostAddress,int port) throws SecurityException, IOException {
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
                    FIFORequestQueueModel receivedObj = SerializingUtil.getInstance().getFIFOObjectFromSerialized(request.getData());
                    //messageType = MessageType.valueOf(new String(request.getData()));
                    byte[] responseData;
                    datagramSocket = new DatagramSocket();
                    switch (receivedObj.getRequestType()) {
                        case GET_RECORD_COUNT:
                            // Sending back record count by requested client UDPRequest
                            responseData = centerServer.getRecordMap().getRecordsCount().toString().getBytes();
                            datagramSocket.send(new DatagramPacket(responseData, responseData.length, request.getAddress(),
                                    request.getPort()));
                            break;
                        case TRANSFER_RECORD:
                            break;
                        case UPDATE_RECORD:
                            break;
                        case CREATE_S_RECORD:
                            responseData = centerServer.createSRecord(receivedObj.getStudentRecord().getFirstName(), receivedObj.getStudentRecord().getLastName(), receivedObj.getStudentRecord().getCourseRegistered(), receivedObj.getStudentRecord().getStatus(), receivedObj.getStudentRecord().getStatusDate(), receivedObj.getManagerId()).getBytes();;
                            datagramSocket.send(new DatagramPacket(responseData, responseData.length, request.getAddress(),
                                    request.getPort()));
                            break;
                        case CREATE_T_RECORD:
                            break;
                        case ELECTION:
                            if (request.getPort() < centerServer.getUdpPort()) {
                                responseData = CMSConstants.OK_MESSAGE.getBytes();
                                datagramSocket.send(new DatagramPacket(responseData, responseData.length, request.getAddress(),
                                        request.getPort()));
                                // init election as the current server could be leader
                                //TODO init election params configure
//                                centerServer.getUdpManager().initElection();
                            }
                            break;
                        case COORDINATOR:
                            break;
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

    @Override
    public void sendOkMessage() {

    }

    @Override
    public void sendCoordinatorMessage() {

    }

    @Override
    public void initiateElection() {

    }
}
