package com.concordia.dsd.server.UDP;

import com.concordia.dsd.global.cmsenum.Location;
import com.concordia.dsd.global.constants.CMSLogMessages;
import com.concordia.dsd.global.enums.FrontEndNotify;
import com.concordia.dsd.server.FrontEndImpl;
import com.concordia.dsd.server.generics.CenterServerImpl;
import com.concordia.dsd.server.generics.FIFORequestQueueModel;
import com.concordia.dsd.server.interfaces.UDPServerInterface;
import com.concordia.dsd.utils.LoggingUtil;
import net.rudp.ReliableServerSocket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FrontEndUDPServer implements UDPServerInterface, Runnable {

    private ReliableServerSocket serverSocket = null;
    private FrontEndImpl frontEndImpl;
    private Logger logger = null;
    private int udpPort;
    private String udpHostAddress;
    private static final String RESPONSE_OK = new String("SUCCESS");

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
        try {
            while (true) {
                Socket clientSocket = null;
                try {

                    clientSocket = serverSocket.accept();
                    ObjectInputStream objectIn = new ObjectInputStream(clientSocket.getInputStream());
                    Object object = objectIn.readObject();
                    objectIn.close();

                    String requestType=null;
                    if(object instanceof String ){
                        requestType = (String) object;
                    }
                    if (requestType.trim().equals(FrontEndNotify.BULLY_STARTED.toString())) {
                        logger.log(Level.INFO, CMSLogMessages.LEADER_ELECTION_STARTED);
                        frontEndImpl.setBullyRunning(true);
                    } else {
                        logger.log(Level.INFO, CMSLogMessages.LEADER_ELECTION_COMPLETED);
                        frontEndImpl.setBullyRunning(false);
                    }

                    OutputStream outputStream = clientSocket.getOutputStream();
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                    objectOutputStream.writeObject(new String(RESPONSE_OK));
                    objectOutputStream.close();
                } catch (IOException e) {
                    logger.log(Level.SEVERE, e.getMessage());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    if (clientSocket != null) {
                        try {
                            clientSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } finally {
            if (serverSocket != null) {
                serverSocket.close();
            }
        }
    }

    @Override
    public void initializeServerSocket() {
        try {
            serverSocket = new ReliableServerSocket(getCenterServerPort());
        } catch (SocketException e) {
            logger.log(Level.SEVERE, e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
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
