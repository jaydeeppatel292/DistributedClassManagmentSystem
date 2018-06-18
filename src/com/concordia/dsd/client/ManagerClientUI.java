package com.concordia.dsd.client;


import com.concordia.dsd.exception.ManagerInvalidException;
import com.concordia.dsd.global.cmsenum.Location;
import com.concordia.dsd.global.cmsenum.Status;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class ManagerClientUI {
	static String[] studentEditFields = { "statusdate", "status", "coursesregistered" };
	static String[] teacherEditFields = { "address", "location", "phone" };

	public static void main(String[] args) {
		while (true) {
			boolean exit = false;
			try {
				String managerId = getUserInput("Manager ID");
				if (managerId.length() != 7) {
					throw new ManagerInvalidException(
							"Invalid Manager id. Enter valid Manager Id (<Three digit Location code><4 digit unique id>)");
				}
				ManagerClient centerClientManager = new ManagerClient(managerId);
				showWelcomeMessage(managerId.substring(0, 3) + " server");
				boolean shouldBreak = false;

				while (true) {
					showMenu();
					String userChoice = getUserInput("your choice");
					switch (userChoice) {
					case "1":
						centerClientManager.createSRecord(getUserInput("first name"), getUserInput("last name"),
								getUserInput("courses"), Status.valueOf(getUserInput("Status (active/inactive)")),
								getUserInput("Date (dd/mm/yyyy)"), managerId);
						break;
					case "2":
						centerClientManager.createTRecord(getUserInput("first name"), getUserInput("last name"),
								getUserInput("address"), getUserInput("phone"), getUserInput("specialization"),
								Location.valueOf(getUserInput("Location code (MTL/LVL/DDO)")), managerId);
						break;
					case "3":
						String editRecordId = getUserInput("Record Id");
						String fieldName = getUserInput("field name "
								+ (editRecordId.startsWith("TR") ? Arrays.asList(studentEditFields).toString()
										: Arrays.asList(teacherEditFields).toString()));
						String fieldValue = getUserInput("new value");
						centerClientManager.editRecord(editRecordId, fieldName, fieldValue, managerId);
						break;
					case "4":
						centerClientManager.getRecordCounts(managerId);
						break;
					case "5":
						shouldBreak = true;
						break;
					case "6":
						shouldBreak = true;
						exit = true;
						break;
					default:
						showMenu();
					}
					if (shouldBreak)
						break;
				}

			} catch (ManagerInvalidException e) {
				System.out.println(e.getMessage());
			} catch (Exception e) {
				System.out.println("ERROR!!!" + e.getMessage());
			}

			if (exit) {
				System.out.println("Good Bye");
				break;	
			}
		}
	}

	public static List<String> getStudentCourses() throws IOException {
		String val = null;
		List<String> courses = new ArrayList<>();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			System.out.println("Enter next course or done: ");
			val = br.readLine();
			if (!val.trim().equals("done"))
				courses.add(val);
			else
				break;
		}
		return courses;
	}

	public static void showMenu() {
		System.out.println();
		System.out.println();
		System.out.println("1. Add a Student");
		System.out.println("2. Add a Teacher");
		System.out.println("3. Edit a record");
		System.out.println("4. Get records count");
		System.out.println("5. Log out");
		System.out.println("6. Exit");
		System.out.println();
	}

	public static void showWelcomeMessage(String serverLocation) {
		System.out.println();
		System.out.println("****************************************");
		System.out.println("Welcome to " + serverLocation);
		System.out.println("****************************************");
	}

	public static String getUserInput(String field) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Enter " + field + ": ");
		String input = br.readLine();
		if (field.startsWith("Date")) {
			while (!input.matches("([0-9]{2})/([0-9]{2})/([0-9]{4})")) {
				input = br.readLine();
			}
		}
		return input;
	}
}
