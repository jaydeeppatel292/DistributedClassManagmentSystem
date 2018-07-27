package com.concordia.dsd.server.UDP;

import com.concordia.dsd.global.cmsenum.MessageType;
import com.concordia.dsd.global.constants.CMSConstants;
import com.concordia.dsd.global.constants.CMSLogMessages;
import com.concordia.dsd.server.generics.CenterServerImpl;
import com.concordia.dsd.server.generics.FIFORequestQueueModel;
import com.concordia.dsd.utils.LoggingUtil;
import com.concordia.dsd.utils.SerializingUtil;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UDPRequest extends Thread {
    private CenterServerImpl centerServer;
    private String responseFromUDP;
    private Logger logger = null;
    private FIFORequestQueueModel reqObj;
    private MessageType messageType;

    public UDPRequest(CenterServerImpl centerServerImpl, FIFORequestQueueModel reqObj) throws SecurityException, IOException {
        centerServer = centerServerImpl;
        logger = LoggingUtil.getInstance().getServerLogger(centerServerImpl.getLocation());
        this.reqObj = reqObj;
    }

    public String getResponseFromUDP() {
        return responseFromUDP;
    }

    public void setResponseFromUDP(String responseFromUDP) {
        this.responseFromUDP = responseFromUDP;
    }

    public CenterServerImpl getCenterServer() {
        return centerServer;
    }


    @Override
    public void run() {
        InetAddress address = centerServer.getIpAddress();
        int port = centerServer.getUdpPort();
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            logger.log(Level.INFO, String.format(CMSLogMessages.RECORD_COUNT_SERVER_INIT,
                    centerServer.getLocation().toString(), address, port));
            byte[] data;
            data = SerializingUtil.getInstance().getSerializedFIFOObject(reqObj);
            DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
            socket.send(packet);

            data = new byte[1000];
            socket.receive(new DatagramPacket(data, data.length));
            String response = new String(data);
            switch (messageType) {
                case RECORD_COUNT:

                    logger.log(Level.INFO, String.format(CMSLogMessages.RECORD_COUNT_SERVER_COMPLETE,
                            centerServer.getLocation().toString(), address, port, response.trim()));
                    setResponseFromUDP(response.trim());
                    break;
                case CREATE_S_RECORD:
                    response
                    break;
                case CREATE_T_RECORD:
                    break;
                case EDIT_RECORD:
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
}
