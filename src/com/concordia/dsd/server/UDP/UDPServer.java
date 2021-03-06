package com.concordia.dsd.server.UDP;

import com.concordia.dsd.global.cmsenum.Location;
import com.concordia.dsd.global.cmsenum.MessageType;
import com.concordia.dsd.global.constants.CMSLogMessages;
import com.concordia.dsd.global.constants.ServerConfig;
import com.concordia.dsd.server.corba.bully.LeaderOperationInterface;
import com.concordia.dsd.server.generics.CenterServerImpl;
import com.concordia.dsd.server.interfaces.UDPServerInterface;
import com.concordia.dsd.utils.LoggingUtil;

import java.io.IOException;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UDPServer implements UDPServerInterface, LeaderOperationInterface, Runnable {

    private DatagramSocket socket = null;
    private CenterServerImpl centerServer;
    private Logger logger = null;
    private MessageType messageType;

    /**
     * Constructor UDPServer
     *
     * @param centerServerImpl
     * @throws SecurityException
     * @throws IOException
     */
    public UDPServer(CenterServerImpl centerServerImpl) throws SecurityException, IOException {
        super();
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

                    messageType = MessageType.valueOf(new String(request.getData()));
                    byte[] responseData;
                    datagramSocket = new DatagramSocket();
                    switch (messageType) {
                        case RECORD_COUNT:
                            // Sending back record count by requested client UDPRequest
                            responseData = centerServer.getRecordMap().getRecordsCount().toString().getBytes();
                            datagramSocket.send(new DatagramPacket(responseData, responseData.length, request.getAddress(),
                                    request.getPort()));
                            break;
                        case TRANSFER_RECORD:
                            break;
                        case EDIT_RECORD:
                            break;
                        case CREATE_S_RECORD:
                            break;
                        case CREATE_T_RECORD:
                            break;
                        case ELECTION:
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
        if (centerServer.getLocation().equals(Location.MTL)) {
            return ServerConfig.UDP_PORT_MTL;
        } else if (centerServer.getLocation().equals(Location.LVL)) {
            return ServerConfig.UDP_PORT_LVL;
        } else {
            return ServerConfig.UDP_PORT_DDO;
        }
    }

    @Override
    public InetAddress getInetAddress() {
        try {
            return InetAddress.getByName(ServerConfig.UDP_HOST_NAME);
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
}
