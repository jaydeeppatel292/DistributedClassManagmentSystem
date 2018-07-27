package com.concordia.dsd.server.UDP;

import com.concordia.dsd.global.cmsenum.Location;
import com.concordia.dsd.global.cmsenum.MessageType;
import com.concordia.dsd.global.constants.CMSConstants;
import com.concordia.dsd.global.constants.CMSLogMessages;
import com.concordia.dsd.server.generics.CenterServerImpl;
import com.concordia.dsd.server.generics.FIFORequestQueueModel;
import com.concordia.dsd.utils.LoggingUtil;
import com.concordia.dsd.utils.SerializingUtil;

import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UDPRequest extends Thread {
    private String responseFromUDP;
    private Logger logger = null;
    private FIFORequestQueueModel reqObj;
    private MessageType messageType;
    private String serverUDPHostAddress;
    private int serverUDPPort;
    private Location serverLocation;
    public UDPRequest(Location serverLocation,String serverUDPHostAddress,int serverUDPPort, FIFORequestQueueModel reqObj) throws SecurityException, IOException {
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
            logger.log(Level.INFO, String.format(CMSLogMessages.RECORD_COUNT_SERVER_INIT,
                    serverLocation.toString(), address, serverUDPPort));
            byte[] data;
            data = SerializingUtil.getInstance().getSerializedFIFOObject(reqObj);
            DatagramPacket packet = new DatagramPacket(data, data.length, address, serverUDPPort);
            socket.send(packet);

            data = new byte[1000];
            socket.receive(new DatagramPacket(data, data.length));
            String response = new String(data);
            switch (reqObj.getRequestType()) {
                case GET_RECORD_COUNT:
                    logger.log(Level.INFO, String.format(CMSLogMessages.RECORD_COUNT_SERVER_COMPLETE,
                            serverLocation.toString(), address, serverUDPPort, response.trim()));
                    setResponseFromUDP(response.trim());
                    break;
                case CREATE_S_RECORD:
                    break;
                case CREATE_T_RECORD:
                    break;
                case UPDATE_RECORD:
                    break;
                case TRANSFER_RECORD:
                    break;
                case ELECTION:
                    String electionMessage = new String(data);
                    if (electionMessage.equals(CMSConstants.OK_MESSAGE)) {

                    }
                    break;
                case COORDINATOR:
                    break;
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

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public InetAddress getInetAddress(String udpHostAddress) {
        try {
            return InetAddress.getByName(udpHostAddress);
        } catch (UnknownHostException e) {
            logger.log(Level.SEVERE, e.getMessage());
            return null;
        }
    }

}
