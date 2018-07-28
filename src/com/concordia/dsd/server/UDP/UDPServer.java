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
import net.rudp.ReliableServerSocket;

import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UDPServer implements UDPServerInterface, Runnable {

    private ReliableServerSocket serverSocket = null;
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
        /*if (udpPort == ServerManager.getInstance().getMasterServerPort(centerServer.getLocation())) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/

        try {
            while (true) {
                Socket clientSocket = null;
                try {
                    clientSocket = serverSocket.accept();
                    ObjectInputStream objectIn = new ObjectInputStream(clientSocket.getInputStream());
                    Object object = objectIn.readObject();
                    FIFORequestQueueModel receivedObj=null;
                    if(object instanceof FIFORequestQueueModel){
                        receivedObj = (FIFORequestQueueModel) object;
                    }
                    objectIn.close();


                    //messageType = MessageType.valueOf(new String(request.getData()));
//                    System.out.println("RECEIVED OBJ:" + receivedObj.toString());
                    OutputStream outputStream = clientSocket.getOutputStream();
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);


                    if (receivedObj!=null && receivedObj.isSyncRequest()) {
//                        System.out.println("inside insync");
                        receivedObj.setSyncRequest(false);
                        String response = centerServer.sendBackUpProcessRequestFromController(receivedObj);
                        objectOutputStream.writeObject(new String(response));
                        objectOutputStream.close();
                    } else if(receivedObj!=null){
                        switch (receivedObj.getRequestType()) {
                            case GET_RECORD:
                                // Sending back record count by requested client UDPRequest
                                Record record = centerServer.getRecordByRecordId(receivedObj.getRecordId());
                                if (record instanceof StudentRecord) {
                                    StudentRecord studentRecord = (StudentRecord) record;
                                    objectOutputStream.writeObject(studentRecord);
                                } else if (record instanceof TeacherRecord) {
                                    TeacherRecord teacherRecord = (TeacherRecord) record;
                                    objectOutputStream.writeObject(teacherRecord);
                                } else {
                                    objectOutputStream.writeObject(new String("Record Not Found"));
                                }
                                objectOutputStream.close();
                                break;
                            case GET_RECORD_COUNT:
                                // Sending back record count by requested client UDPRequest
//                                System.out.println("inside switch in UDP server");
                                String response = centerServer.getRecordCounts(receivedObj.getManagerId());
                                objectOutputStream.writeObject(new String(response));
                                objectOutputStream.close();
                                break;
                            case GET_RECORD_COUNT_SUBS:
                                // Sending back record count by requested client UDPRequest
                                System.out.println("inside switch in UDP server");
                                response = centerServer.getRecordMap().getRecordsCount().toString();
                                objectOutputStream.writeObject(new String(response));
                                objectOutputStream.close();

                                break;
                            case TRANSFER_RECORD:
                                response = centerServer.transferRecord(receivedObj.getManagerId(), receivedObj.getRecordId(), receivedObj.getCenterServerName());
                                objectOutputStream.writeObject(new String(response));
                                objectOutputStream.close();

                                break;
                            case UPDATE_RECORD:
                                response = centerServer.editRecord(receivedObj.getRecordId(), receivedObj.getFieldName(), receivedObj.getNewValue(), receivedObj.getManagerId());
                                objectOutputStream.writeObject(new String(response));
                                objectOutputStream.close();
                                break;
                            case CREATE_S_RECORD:
                                response = centerServer.createSRecord(receivedObj.getStudentRecord().getFirstName(), receivedObj.getStudentRecord().getLastName(), receivedObj.getStudentRecord().getCourseRegistered(), receivedObj.getStudentRecord().getStatus(), receivedObj.getStudentRecord().getStatusDate(), receivedObj.getManagerId());
                                objectOutputStream.writeObject(new String(response));
                                objectOutputStream.close();
                                break;
                            case CREATE_T_RECORD:
                                response = centerServer.createTRecord(receivedObj.getTeacherRecord().getFirstName(), receivedObj.getTeacherRecord().getLastName(), receivedObj.getTeacherRecord().getAddress(), receivedObj.getTeacherRecord().getPhone(), receivedObj.getTeacherRecord().getSpecialization(), receivedObj.getTeacherRecord().getLocation(), receivedObj.getManagerId());
                                objectOutputStream.writeObject(new String(response));
                                objectOutputStream.close();
                                break;
                            case DELETE_RECORD:
                                response = null;
                                if (receivedObj.getStudentRecord() != null) {
                                    response = centerServer.deleteRecord(receivedObj.getStudentRecord());
                                } else if (receivedObj.getStudentRecord() == null) {
                                    response = centerServer.deleteRecord(receivedObj.getTeacherRecord());
                                }
                                objectOutputStream.writeObject(new String(response));
                                objectOutputStream.close();
                                break;
                            case ELECTION:
                                if (clientSocket.getPort() < centerServer.getUdpPort()) {
                                    response = CMSConstants.OK_MESSAGE;
                                    objectOutputStream.writeObject(new String(response));
                                    objectOutputStream.close();

                                    boolean isCoordinator = centerServer.getUdpManager().initElection(centerServer.getLocation(), receivedObj.getProcessIdList());
                                    if (isCoordinator) {
                                        centerServer.getUdpManager().sendCoordinationMessage();
                                    }
                                }
                                break;
                            case COORDINATOR:
                                logger.log(Level.INFO, String.format(CMSLogMessages.COORDINATOR_NOTIFY_MESSAGE, centerServer.getUdpPort(), clientSocket.getPort()));
                                break;

                            case PING_SERVER:
                                response = CMSConstants.SERVER_UP_MESSAGE;
                                objectOutputStream.writeObject(new String(response));
                                objectOutputStream.close();

                                break;
                            case FAIL_SERVER:
                                logger.log(Level.INFO, String.format(CMSLogMessages.FAIL_SERVER_INIT, udpPort));
                                try {
                                    Thread.sleep(12000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
                    }
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
