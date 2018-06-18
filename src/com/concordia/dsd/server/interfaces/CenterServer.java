package com.concordia.dsd.server.interfaces;

import com.concordia.dsd.global.cmsenum.Location;
import com.concordia.dsd.global.cmsenum.Status;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;

public interface CenterServer extends Remote {
	
	String createTRecord(String firstName, String lastName, String address, String phone, String specialization,
                         Location location, String managerId) throws RemoteException;

	String createSRecord(String firstName, String lastName, String courseRegistered, Status status,
						 String statusDate, String managerId) throws RemoteException;

	String getRecordCounts(String managerId) throws RemoteException;

	void editRecord(String recordID, String fieldName, String newValue, String managerId) throws RemoteException;

}
