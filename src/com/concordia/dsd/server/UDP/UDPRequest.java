package com.concordia.dsd.server.UDP;

import com.concordia.dsd.global.constants.CMSLogMessages;
import com.concordia.dsd.server.RMI.RMICenterServerImpl;
import com.concordia.dsd.server.generics.CenterServerImpl;
import com.concordia.dsd.utils.LoggingUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UDPRequest extends Thread {
	private CenterServerImpl centerServer;
	private int requestedRecordCount;
	private Logger logger = null;
	private static final Integer RECORD_COUNT_REQUEST = 0;

	public UDPRequest(CenterServerImpl centerServerImpl) throws SecurityException, IOException {
		centerServer = centerServerImpl;
		logger = LoggingUtil.getInstance().getLogger(centerServerImpl.getLocation());
	}

	public int getRequestedRecordCount() {
		return requestedRecordCount;
	}

	public void setRequestedRecordCount(int requestedRecordCount) {
		this.requestedRecordCount = requestedRecordCount;
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
			byte[] data = RECORD_COUNT_REQUEST.toString().getBytes();
			DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
			socket.send(packet);

			data = new byte[1000];
			socket.receive(new DatagramPacket(data, data.length));
			String recordCount = new String(data);
			logger.log(Level.INFO, String.format(CMSLogMessages.RECORD_COUNT_SERVER_COMPLETE,
					centerServer.getLocation().toString(), address, port, recordCount.trim()));
			setRequestedRecordCount(Integer.parseInt(recordCount.trim()));
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

}
