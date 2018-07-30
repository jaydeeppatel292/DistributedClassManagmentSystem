package com.concordia.dsd.server.UDP;

import com.concordia.dsd.global.cmsenum.Location;
import com.concordia.dsd.global.constants.CMSConstants;
import com.concordia.dsd.global.constants.CMSLogMessages;
import com.concordia.dsd.global.constants.ServerConfig;
import com.concordia.dsd.global.enums.RequestType;
import com.concordia.dsd.model.Record;
import com.concordia.dsd.model.StudentRecord;
import com.concordia.dsd.model.TeacherRecord;
import com.concordia.dsd.server.ServerManager;
import com.concordia.dsd.server.ServerManager.CenterServerInfo;
import com.concordia.dsd.server.corba.bully.LeaderElection;
import com.concordia.dsd.server.generics.FIFORequestQueueModel;
import com.concordia.dsd.utils.LoggingUtil;
import com.concordia.dsd.utils.SerializingUtil;
import net.rudp.ReliableSocket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UDPRequest extends Thread {
    private String responseFromUDP;
    private Logger logger = null;
    private FIFORequestQueueModel reqObj;
    private String serverUDPHostAddress;
    private int serverUDPPort;
    private Location serverLocation;
    private Object outPutObj;
    public Object getOutPutObj() {
        return outPutObj;
    }

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

    public void invokeServerDown(){
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
    }

    TimerTask task = new TimerTask(){
        public void run(){
            invokeServerDown();
        }
    };

    @Override
    public void run() {
        InetAddress address = getInetAddress(serverUDPHostAddress);
        ReliableSocket socket = null;
        try {


            Timer timer = new Timer();
            timer.schedule( task, ServerConfig.PING_REQUEST_TIMEOUT);

            socket = new ReliableSocket(serverUDPHostAddress,serverUDPPort);

            timer.cancel();
            timer.purge();
            OutputStream outputStream = socket.getOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(reqObj);
            objectOutputStream.close();

            ObjectInputStream objectIn = new ObjectInputStream(socket.getInputStream());
            Object object = objectIn.readObject();
            objectIn.close();
            String response = null;
            if(object instanceof String) {
                response = (String)object;
            }
            outPutObj = object;

            switch (reqObj.getRequestType()) {
                case GET_RECORD_COUNT:
                case GET_RECORD_COUNT_SUBS:
                case CREATE_S_RECORD:
                case CREATE_T_RECORD:
                case UPDATE_RECORD:
                case TRANSFER_RECORD:
                case PING_SERVER:
                case FAIL_SERVER:
                case DELETE_RECORD:
                    setResponseFromUDP(response.trim());
                    break;
                case GET_RECORD:
                    break;
                case ELECTION:
                    if (response.equals(CMSConstants.OK_MESSAGE)) {
                        logger.log(Level.INFO, String.format(CMSLogMessages.ELECTION_FAILURE_MESSAGE, serverUDPPort));
                    }
                    break;
                case COORDINATOR:
                    setResponseFromUDP(response.trim());
                    break;

            }
        } catch (SocketTimeoutException e) {
            invokeServerDown();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            // TODO Auto-generated catch block
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                }
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


}
