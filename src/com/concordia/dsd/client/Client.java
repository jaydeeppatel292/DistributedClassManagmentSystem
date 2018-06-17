package com.concordia.dsd.client;


import com.concordia.dsd.global.cmsenum.Location;
import com.concordia.dsd.global.cmsenum.Status;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {

	private Logger logger = null;

	private void initializeManagers() throws Exception {
		HashSet<String> managers = new HashSet<>();
		managers.add("MTL1111");
		managers.add("MTL1112");
		managers.add("MTL1113");
		managers.add("MTL1114");
		managers.add("MTL1115");
		managers.add("LVL1111");
		managers.add("LVL1112");
		managers.add("DDO1111");
		managers.add("DDO1112");

		for (String managerId : managers) {
			Thread managerThread = new Thread(new ClientManager(managerId));
			managerThread.start();
			managerThread.join();
		}
		
		ManagerClient managerClient = new ManagerClient("MTL0012");
		System.out.println("Initially :: Record Count: " + managerClient.getRecordCounts());
	}

	public static void main(String[] args) throws Exception {
		new Client().initializeManagers();
	}

	class ClientManager implements Runnable {

		private String managerId;

		public ClientManager(String managerId) {
			this.managerId = managerId;
		}

		@Override
		public void run() {
			try {
				ManagerClient managerClient = new ManagerClient(managerId);
				String trRecordId = managerClient.createTRecord("Sohrab", "Singh", "2285 Saint Mathieu", "5149617181",
						"AI", Location.MTL);
				String courses = new String("English, Maths");

				String srRecordId = managerClient.createSRecord("Sarthak", "Arora", courses, Status.active,
						"17 August");
			} catch (Exception e) {
				logger.log(Level.SEVERE, e.getMessage());
			}
		}

	}
}
