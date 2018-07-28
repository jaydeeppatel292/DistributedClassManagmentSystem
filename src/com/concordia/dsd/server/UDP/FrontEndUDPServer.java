package com.concordia.dsd.server.UDP;

import com.concordia.dsd.global.cmsenum.Location;
import com.concordia.dsd.global.constants.CMSLogMessages;
import com.concordia.dsd.global.enums.FrontEndNotify;
import com.concordia.dsd.server.FrontEndImpl;
import com.concordia.dsd.server.generics.CenterServerImpl;
import com.concordia.dsd.server.interfaces.UDPServerInterface;
import com.concordia.dsd.utils.LoggingUtil;

import java.io.IOException;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FrontEndUDPServer implements UDPServerInterface, Runnable {

    private DatagramSocket socket = null;
    private FrontEndImpl frontEndImpl;
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
    public FrontEndUDPServer(FrontEndImpl centerServerImpl, String udpHostAddress, int port) throws SecurityException, IOException {
        super();
        this.udpHostAddress = udpHostAddress;
        this.udpPort = port;
        frontEndImpl = centerServerImpl;
        logger = LoggingUtil.getInstance().getServerLogger(Location.valueOf("FE"));
        initializeServerSocket();
    }

    @Override
    public void run() {
        logger.log(Level.INFO, String.format(CMSLogMessages.UDP_SERVER_INIT, "FE"));
        byte[] buffer;
        DatagramPacket request;
        DatagramSocket datagramSocket = null;
        try {
            while (true) {
                try {
                    buffer = new byte[1000];
                    request = new DatagramPacket(buffer, buffer.length);
                    socket.receive(request);
                    String requestType = new String(request.getData());
                    if (requestType.equals(String.valueOf(FrontEndNotify.BULLY_STARTED))) {
                        logger.log(Level.INFO, CMSLogMessages.LEADER_ELECTION_STARTED);
                    } else {
                        logger.log(Level.INFO, CMSLogMessages.LEADER_ELECTION_COMPLETED);
                    }

                    byte[] responseData = null;
                    datagramSocket = new DatagramSocket();
                    datagramSocket.send(new DatagramPacket(responseData, responseData.length, request.getAddress(),
                            request.getPort()));
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
