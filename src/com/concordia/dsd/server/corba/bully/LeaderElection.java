package com.concordia.dsd.server.corba.bully;

import com.concordia.dsd.global.cmsenum.Location;
import com.concordia.dsd.global.enums.FrontEndNotify;
import com.concordia.dsd.server.ServerManager;
import com.concordia.dsd.server.UDP.FrontEndUDPServer;
import com.concordia.dsd.server.UDP.UDPManager;
import net.rudp.ReliableSocket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.List;
import java.util.Random;

public class LeaderElection extends Thread {

    private List<Integer> processIdList;
    private Location location;

    public LeaderElection(Location location, List<Integer> idList) {
        processIdList = idList;
        this.location = location;
    }

    @Override
    public void run() {
        initElection();
    }

    private void initElection() {
        int randomNumber = new Random().nextInt(processIdList.size());
        //TODO remove 1 and fix it with random number case
        UDPManager udpManager = ServerManager.getInstance().getCenterServer(location, processIdList.get(1)).getUdpManager();
//        processIdList.remove(randomNumber);
        notifyFrontend();
        if(udpManager.initElection(location, processIdList)) {
            udpManager.sendCoordinationMessage();
        }
    }

    private void notifyFrontend() {
        FrontEndUDPServer frontEndUDPServer = ServerManager.getInstance().getFrontEndServer().getFrontEndImpl().getUdpServer();
        InetAddress address = frontEndUDPServer.getInetAddress();
        ReliableSocket socket = null;

        try {
            socket = new ReliableSocket(frontEndUDPServer.getInetAddress().getHostAddress(),frontEndUDPServer.getCenterServerPort());
            OutputStream outputStream = socket.getOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(new String(String.valueOf(FrontEndNotify.BULLY_STARTED)));
            objectOutputStream.close();

            ObjectInputStream objectIn = new ObjectInputStream(socket.getInputStream());
            Object object = objectIn.readObject();
            objectIn.close();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }

    }
}
