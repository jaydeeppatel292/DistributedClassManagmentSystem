package com.concordia.dsd.server.corba.bully;

import com.concordia.dsd.global.cmsenum.Location;
import com.concordia.dsd.global.enums.FrontEndNotify;
import com.concordia.dsd.server.ServerManager;
import com.concordia.dsd.server.UDP.FrontEndUDPServer;
import com.concordia.dsd.server.UDP.UDPManager;

import java.io.IOException;
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
        UDPManager udpManager = ServerManager.getInstance().getCenterServer(location, processIdList.get(randomNumber)).getUdpManager();
//        processIdList.remove(randomNumber);
        udpManager.initElection(location, processIdList);
        notifyFrontend();
    }

    private void notifyFrontend() {
        FrontEndUDPServer frontEndUDPServer = ServerManager.getInstance().getFrontEndServer().getFrontEndImpl().getUdpServer();
        InetAddress address = frontEndUDPServer.getInetAddress();
        try {
            DatagramSocket socket = new DatagramSocket();
            byte[] data = String.valueOf(FrontEndNotify.BULLY_STARTED).getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length, address, frontEndUDPServer.getCenterServerPort());
            socket.send(packet);

            data = new byte[1000];
            DatagramPacket receivedPacket = new DatagramPacket(data, data.length);
            socket.receive(receivedPacket);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
