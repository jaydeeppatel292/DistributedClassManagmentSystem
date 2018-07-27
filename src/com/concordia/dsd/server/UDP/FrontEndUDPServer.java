package com.concordia.dsd.server.UDP;

import com.concordia.dsd.global.cmsenum.Location;
import com.concordia.dsd.global.constants.CMSLogMessages;
import com.concordia.dsd.global.constants.ServerConfig;
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

	/**
	 * Constructor UDPServer
	 * @param centerServerImpl
	 * @throws SecurityException
	 * @throws IOException
	 */
	public FrontEndUDPServer(FrontEndImpl centerServerImpl) throws SecurityException, IOException {
		super();
		frontEndImpl = centerServerImpl;
		logger = LoggingUtil.getInstance().getServerLogger(frontEndImpl.getLocation());
		initializeServerSocket();
	}

	@Override
	public void run() {
		logger.log(Level.INFO, String.format(CMSLogMessages.UDP_SERVER_INIT, frontEndImpl.getLocation().toString()));
		byte[] buffer;
		DatagramPacket request; 
		DatagramSocket datagramSocket = null;
		try {
			while (true) {
				try {
					buffer = new byte[1000];
					request = new DatagramPacket(buffer, buffer.length);
					socket.receive(request);
					// Sending back record count by requested client UDPRequest
					//TODO check type of request and perform actions..
//					byte[] responseData = frontEndImpl.getRecordMap().getRecordsCount().toString().getBytes();
					byte[] responseData = null;
					datagramSocket = new DatagramSocket();
					datagramSocket.send(new DatagramPacket(responseData, responseData.length, request.getAddress(),
                        request.getPort()));
				} catch (IOException e) {
					logger.log(Level.SEVERE, e.getMessage());
				}finally {
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
		if (frontEndImpl.getLocation().equals(Location.MTL)) {
			return ServerConfig.UDP_PORT_MTL;
		} else if (frontEndImpl.getLocation().equals(Location.LVL)) {
			return ServerConfig.UDP_PORT_LVL;
		} else {
			return ServerConfig.UDP_PORT_DDO;
		}
	}

	@Override
	public InetAddress getInetAddress() {
		try {
			return InetAddress.getByName(ServerConfig.UDP_HOST_NAME);
		} catch (UnknownHostException e) {
			logger.log(Level.SEVERE, e.getMessage());
			return null;
		}
	}

}
