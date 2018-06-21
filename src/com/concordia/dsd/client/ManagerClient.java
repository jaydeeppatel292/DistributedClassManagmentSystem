package com.concordia.dsd.client;

import com.concordia.dsd.global.cmsenum.Location;
import com.concordia.dsd.global.cmsenum.Status;
import com.concordia.dsd.global.constants.CMSLogMessages;
import com.concordia.dsd.server.interfaces.CenterServer;
import com.concordia.dsd.utils.LoggingUtil;
import com.concordia.dsd.utils.ManagerClientUtil;

import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ManagerClient {
	String managerId;
	CenterServer server;
	Logger clientLogger;

	public ManagerClient(String managerId) throws Exception {
		this.managerId = managerId;
		server = ManagerClientUtil.getCenterServer(managerId);
		clientLogger = LoggingUtil.getInstance().getClientLogger(managerId);
	}

	public String createTRecord(String firstName, String lastName, String address, String phone, String specialization,
			Location location, String managerId) throws RemoteException {
		String recordId = server.createTRecord(firstName, lastName, address, phone, specialization, location, managerId);
		clientLogger.log(Level.INFO, String.format(CMSLogMessages.CREATED_TEACHER_RECORD_MSG, recordId,"MANAGER ID = " + managerId));
		return recordId;
	}

	public String createSRecord(String firstName, String lastName, String courseRegistered, Status status,
								String statusDate, String managerId) throws RemoteException {
		String recordId = server.createSRecord(firstName, lastName, courseRegistered, status, statusDate, managerId);
		clientLogger.log(Level.INFO,  String.format(CMSLogMessages.CREATED_STUDENT_RECORD_MSG , recordId, "MANAGER ID = " + managerId));
		return recordId;
	}

	public String getRecordCounts(String managerId) throws RemoteException {
		String recordCounts = server.getRecordCounts(managerId);
		clientLogger.log(Level.INFO, CMSLogMessages.RECORD_COUNT, recordCounts);
		return recordCounts;
	}

	public void editRecord(String recordId, String fieldName, String newValue, String managerId) throws RemoteException {
		server.editRecord(recordId, fieldName, newValue, managerId);
		clientLogger.log(Level.INFO, String.format(CMSLogMessages.UPDATE_RECORD_MSG, fieldName, newValue, recordId ,"MANAGER ID = " + managerId));
	}

	public String getManagerId() {
		return managerId;
	}

	public void setManagerId(String managerId) {
		this.managerId = managerId;
	}

}
