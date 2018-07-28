package com.concordia.dsd.server;

import com.concordia.dsd.global.cmsenum.Location;
import com.concordia.dsd.global.cmsenum.Status;
import com.concordia.dsd.global.enums.RequestType;
import com.concordia.dsd.model.StudentRecord;
import com.concordia.dsd.model.TeacherRecord;
import com.concordia.dsd.server.UDP.FrontEndUDPManager;
import com.concordia.dsd.server.UDP.FrontEndUDPServer;
import com.concordia.dsd.server.generics.FIFORequestQueueModel;
import com.concordia.dsd.utils.LoggingUtil;
import com.concordia.dsd.utils.SerializingUtil;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

public class FrontEndImpl {
    private final String udpHostAddress;
    private int udpPort;
    private InetAddress ipAddress;
    private FrontEndUDPServer udpServer;
    private Logger serverLogger;
    private FrontEndUDPManager udpManager;
    private int masterPort;
    private int myPort;
    private String masterHostAddress;
    private ConcurrentLinkedQueue<FIFORequestQueueModel> requestQueue;
    private HashMap<Location, MasterServerInfo> masterServerInfoHashMap = new HashMap<>();
    private boolean isBullyRunning=false;

    public ConcurrentLinkedQueue<FIFORequestQueueModel> getRequestQueue() {
        return requestQueue;
    }

    public boolean isBullyRunning() {
        return isBullyRunning;
    }

    public void setBullyRunning(boolean bullyRunning) {
        isBullyRunning = bullyRunning;
    }

    public FIFORequestQueueModel getRequestFromQueue() {
        return requestQueue.peek();
    }

    public FIFORequestQueueModel dequeueRequestFromQueue() {
        return requestQueue.poll();
    }

    public void setMasterServer(Location location, int port, String hostAddress) {
//        serverLogger.log(Level.INFO, String.format(CMSLogMessages.MASTER_SERVER_INIT, String.valueOf(port), String.valueOf(location)));
        masterServerInfoHashMap.put(location, new MasterServerInfo(port, location, hostAddress));
    }

    public MasterServerInfo getMasterServerForLocation(Location location) {
        return masterServerInfoHashMap.get(location);
    }

    public String getMasterHostAddress() {
        return masterHostAddress;
    }

    public void setMasterHostAddress(String masterHostAddress) {
        this.masterHostAddress = masterHostAddress;
    }

    public int getMasterPort() {
        return masterPort;
    }

    public void setMasterPort(int masterPort) {
        this.masterPort = masterPort;
    }

    public int getMyPort() {
        return myPort;
    }

    public void setMyPort(int myPort) {
        this.myPort = myPort;
    }

    /**
     * Initialize Constructor
     * Also Start Udp Server
     *
     * @throws SecurityException
     * @throws IOException
     */
    public FrontEndImpl(String udpHostAddress, int port) throws SecurityException, IOException {
        this.myPort = port;
        this.udpHostAddress = udpHostAddress;
        requestQueue = new ConcurrentLinkedQueue<>();
        try {
            serverLogger = LoggingUtil.getInstance().getServerLogger(Location.valueOf("FE"));
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        udpManager = new FrontEndUDPManager(serverLogger);
        udpServer = new FrontEndUDPServer(this, udpHostAddress, port);

        // Initialize UDP Server
        initCenterServerModel();

        // Start UDP Server
        new Thread(udpServer).start();
        new BackupServerSyncManager(this).start();
    }

    /**
     * Initialize UDP Server
     */
    private void initCenterServerModel() {
        setUdpPort(udpServer.getCenterServerPort());
        setIpAddress(udpServer.getInetAddress());
    }

    public FrontEndUDPManager getUdpManager() {
        return udpManager;
    }

    public int getUdpPort() {
        return udpPort;
    }

    public void setUdpPort(int udpPort) {
        this.udpPort = udpPort;
    }

    public InetAddress getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(InetAddress ipAddress) {
        this.ipAddress = ipAddress;
    }

    public FrontEndUDPServer getUdpServer() {
        return udpServer;
    }

    public Logger getServerLogger() {
        return serverLogger;
    }


    public String sendBackupSyncRequest(FIFORequestQueueModel fifoRequestQueueModel) {
        FIFORequestQueueModel fifoRequest = fifoRequestQueueModel.createCopy();
        fifoRequest.setSyncRequest(true);
        return getUdpManager().sendUDPRequest(getMasterServerForLocation(fifoRequest.getRequestLocation()), fifoRequest);
    }

    /**
     * Implementation of Create Teacher Record
     *
     * @param firstName
     * @param lastName
     * @param address
     * @param phone
     * @param specialization
     * @param location
     * @param managerId
     * @return
     */
    public String createTRecord(String firstName, String lastName, String address, String phone, String specialization,
                                Location location, String managerId) {
        String requestedLocation = managerId.substring(0, 3);

        TeacherRecord teacher = new TeacherRecord("", firstName, lastName, address, phone, specialization, location);
        FIFORequestQueueModel obj = new FIFORequestQueueModel(RequestType.CREATE_T_RECORD, teacher, managerId, Location.valueOf(requestedLocation));
        requestQueue.add(obj);
        return getUdpManager().sendUDPRequest(getMasterServerForLocation(Location.valueOf(requestedLocation)), obj);
    }

    /**
     * Implementation of create Student Record
     *
     * @param firstName
     * @param lastName
     * @param courseRegistered
     * @param status
     * @param statusDate
     * @param managerId
     * @return
     */
    public String createSRecord(String firstName, String lastName, String courseRegistered, Status status,
                                String statusDate, String managerId) {
        String requestedLocation = managerId.substring(0, 3);
        StudentRecord student = new StudentRecord("", firstName, lastName, status, courseRegistered, statusDate);
        FIFORequestQueueModel obj = new FIFORequestQueueModel(RequestType.CREATE_S_RECORD, student, managerId, Location.valueOf(requestedLocation));
        requestQueue.add(obj);
        return getUdpManager().sendUDPRequest(getMasterServerForLocation(Location.valueOf(requestedLocation)), obj);
    }

    /**
     * Get Record Counts from all servers
     *
     * @param managerId
     * @return
     */
    public String getRecordCounts(String managerId) {
        String requestedLocation = managerId.substring(0, 3);
        FIFORequestQueueModel obj = new FIFORequestQueueModel(RequestType.GET_RECORD_COUNT, managerId, Location.valueOf(requestedLocation));
        return getUdpManager().sendUDPRequest(getMasterServerForLocation(Location.valueOf(requestedLocation)), obj);
    }

    /**
     * Edit Record
     *
     * @param recordId
     * @param fieldName
     * @param newValue
     * @param managerId
     * @return
     */
    public String editRecord(String recordId, String fieldName, String newValue, String managerId) {
        String requestedLocation = managerId.substring(0, 3);
        FIFORequestQueueModel obj = new FIFORequestQueueModel(RequestType.UPDATE_RECORD, recordId, fieldName, newValue, managerId, "", Location.valueOf(requestedLocation));
        requestQueue.add(obj);
        return getUdpManager().sendUDPRequest(getMasterServerForLocation(Location.valueOf(requestedLocation)), obj);
    }

    /**
     * Transfer Record from one server to other server
     *
     * @param managerId
     * @param recordId
     * @param remoteCenterServerName
     * @return
     */
    public String transferRecord(String managerId, String recordId, String remoteCenterServerName) {
        String requestedLocation = managerId.substring(0, 3);
        FIFORequestQueueModel obj = new FIFORequestQueueModel(RequestType.TRANSFER_RECORD, recordId, "", "", managerId, remoteCenterServerName, Location.valueOf(requestedLocation));

        FIFORequestQueueModel getRecordRequest = obj.createCopy();
        getRecordRequest.setSyncRequest(false);
        getRecordRequest.setRequestType(RequestType.GET_RECORD);
        byte[] recordInByte = getUdpManager().sendUDPRequestForSelection(getMasterServerForLocation(getRecordRequest.getRequestLocation()), getRecordRequest);
        Object record = SerializingUtil.getInstance().getObjectFromSerialized(recordInByte);
        //FIFORequestQueueModel insertRequest = obj.createCopy();
        //FIFORequestQueueModel deleteRequest = obj.createCopy();
        FIFORequestQueueModel insertRequest = new FIFORequestQueueModel();
        FIFORequestQueueModel deleteRequest = new FIFORequestQueueModel();
        insertRequest.setSyncRequest(true);
        deleteRequest.setSyncRequest(true);
        deleteRequest.setRequestType(RequestType.DELETE_RECORD);
        if (record instanceof StudentRecord) {
            insertRequest.setStudentRecord((StudentRecord) record);
            insertRequest.setRequestType(RequestType.CREATE_S_RECORD);
            deleteRequest.setStudentRecord((StudentRecord) record);
        } else if (record instanceof TeacherRecord) {
            insertRequest.setTeacherRecord((TeacherRecord) record);
            insertRequest.setRequestType(RequestType.CREATE_T_RECORD);
            deleteRequest.setTeacherRecord((TeacherRecord) record);
        } else {
            return (String) record;
        }
        insertRequest.setRequestLocation(Location.valueOf(insertRequest.getCenterServerName()));
//        System.out.println("RECORD RECEIVED::::"+insertRequest.toString());
        requestQueue.add(insertRequest);
        requestQueue.add(deleteRequest);
        return getUdpManager().sendUDPRequest(getMasterServerForLocation(Location.valueOf(requestedLocation)), obj);
    }

    public class MasterServerInfo {
        private int port;
        private Location location;
        private String hostAddress;

        public Location getLocation() {
            return location;
        }

        public MasterServerInfo(int port, Location location, String hostAddress) {
            this.port = port;
            this.location = location;
            this.hostAddress = hostAddress;
        }

        public int getPort() {
            return port;
        }

        public String getHostAddress() {
            return hostAddress;
        }

        public void setHostAddress(String hostAddress) {
            this.hostAddress = hostAddress;
        }
    }
}
