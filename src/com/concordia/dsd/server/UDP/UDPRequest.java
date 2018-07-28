package com.concordia.dsd.server.UDP;

import com.concordia.dsd.global.cmsenum.Location;
import com.concordia.dsd.global.constants.CMSConstants;
import com.concordia.dsd.global.constants.CMSLogMessages;
import com.concordia.dsd.global.constants.ServerConfig;
import com.concordia.dsd.global.enums.RequestType;
import com.concordia.dsd.server.ServerManager;
import com.concordia.dsd.server.ServerManager.CenterServerInfo;
import com.concordia.dsd.server.corba.bully.LeaderElection;
import com.concordia.dsd.server.generics.FIFORequestQueueModel;
import com.concordia.dsd.utils.LoggingUtil;
import com.concordia.dsd.utils.SerializingUtil;

import java.io.IOException;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UDPRequest extends Thread {
    private String responseFromUDP;
    private Logger logger = null;
    private FIFORequestQueueModel reqObj;
    private String serverUDPHostAddress;
    private int serverUDPPort;
    private Location serverLocation;
    private byte[] serverResponse;

    public UDPRequest(Location serverLocation, String serverUDPHostAddress, int serverUDPPort,
                      FIFORequestQueueModel reqObj) throws SecurityException, IOException {
        this.serverLocation = serverLocation;
        this.serverUDPHostAddress = serverUDPHostAddress;
        this.serverUDPPort = serverUDPPort;
        logger = LoggingUtil.getInstance().getServerLogger(serverLocation);
        this.reqObj = reqObj;
    }

    public Location getServerLocation() {
        return serverLocation;
    }

    public String getResponseFromUDP() {
        return responseFromUDP;
    }

    public void setResponseFromUDP(String responseFromUDP) {
        this.responseFromUDP = responseFromUDP;
    }

    @Override
    public void run() {
        InetAddress address = getInetAddress(serverUDPHostAddress);
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            //logger.log(Level.INFO, String.format(CMSLogMessages.RECORD_COUNT_SERVER_INIT, serverLocation.toString(),
            //address, serverUDPPort));
            byte[] data;

            data = SerializingUtil.getInstance().getSerializedFIFOObject(reqObj);
            DatagramPacket packet = new DatagramPacket(data, data.length, address, serverUDPPort);
            socket.send(packet);

            data = new byte[1000];
            if (reqObj.getRequestType().equals(RequestType.PING_SERVER)) {
                socket.setSoTimeout(ServerConfig.PING_REQUEST_TIMEOUT);
            }
            DatagramPacket receivedPacket = new DatagramPacket(data, data.length);
            socket.receive(receivedPacket);
            this.serverResponse = data;
            String response = new String(data);
            switch (reqObj.getRequestType()) {
                case GET_RECORD_COUNT:
                    setResponseFromUDP(response.trim());
                    break;
                case GET_RECORD:
                    setResponseFromUDP(response.trim());
                    break;
                case GET_RECORD_COUNT_SUBS:
                    setResponseFromUDP(response.trim());
                    break;
                case CREATE_S_RECORD:
                    setResponseFromUDP(response.trim());
                    break;
                case CREATE_T_RECORD:
                    setResponseFromUDP(response.trim());
                    break;
                case UPDATE_RECORD:
                    setResponseFromUDP(response.trim());
                    break;
                case TRANSFER_RECORD:
                    setResponseFromUDP(response.trim());
                    break;
                case ELECTION:
                    if (response.equals(CMSConstants.OK_MESSAGE)) {
                        logger.log(Level.INFO, String.format(CMSLogMessages.ELECTION_FAILURE_MESSAGE, serverUDPPort));
                    }
                    break;
                case COORDINATOR:
                    break;
                case PING_SERVER:
                    setResponseFromUDP(response.trim());
                    break;
                case FAIL_SERVER:
                    setResponseFromUDP(response.trim());
                    break;
            }
        } catch (SocketTimeoutException e) {
            System.out.println("Socket Timeout Exception");
            setResponseFromUDP(CMSConstants.SERVER_DOWN_MESSAGE);
            CenterServerInfo centerServerInfo = ServerManager.getInstance().getServerInfo(serverLocation,
                    serverUDPPort);
            if (centerServerInfo != null) {
                if (centerServerInfo.isMaster()) {
                    logger.log(Level.INFO,
                            String.format(CMSLogMessages.MASTER_FAILURE_MESSAGE, serverUDPPort, serverLocation));
                    new LeaderElection(serverLocation, ServerManager.getInstance().getAllBackupServerPort(serverLocation))
                            .start();
                } else {
                    logger.log(Level.INFO,
                            String.format(CMSLogMessages.REPLICA_FAILURE_MESSAGE, serverLocation, serverUDPPort, serverLocation));
                    ServerManager.getInstance().removeReplicaServer(serverLocation, serverUDPPort);
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }

    public InetAddress getInetAddress(String udpHostAddress) {
        try {
            return InetAddress.getByName(udpHostAddress);
        } catch (UnknownHostException e) {
            logger.log(Level.SEVERE, e.getMessage());
            return null;
        }
    }

    public byte[] getServerResponse() {
        return serverResponse;
    }
}
