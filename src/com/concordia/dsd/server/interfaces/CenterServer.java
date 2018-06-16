package com.concordia.dsd.server.interfaces;

import com.concordia.dsd.global.cmsenum.Location;
import com.concordia.dsd.global.cmsenum.Status;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;

public interface CenterServer extends Remote {
	
	String createTRecord(String firstName, String lastName, String address, String phone, String specialization,
                         Location location) throws RemoteException;

	String createSRecord(String firstName, String lastName, List<String> courseRegistered, Status status,
						 String statusDate) throws RemoteException;

	String getRecordCounts() throws RemoteException;

	void editRecord(String recordID, String fieldName, String newValue) throws RemoteException;

}
