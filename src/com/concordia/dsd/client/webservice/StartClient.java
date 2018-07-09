package com.concordia.dsd.client.webservice;

import com.concordia.dsd.client.webservice.generated.WSCenterServer;
import com.concordia.dsd.client.webservice.generated.WSCenterServerImplService;
import com.concordia.dsd.global.constants.CMSLogMessages;
import com.concordia.dsd.utils.ConfigManager;
import com.concordia.dsd.utils.LoggingUtil;
import com.concordia.dsd.utils.Validator;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StartClient {
    static Logger clientLogger;
    private static WSCenterServer wsCenterServer;
    private static String[][] hostPortArray;

    public static String[][] getHostPortArray() {
        return hostPortArray;
    }

    public static boolean createConnection(String managerId) throws MalformedURLException {


        hostPortArray = ConfigManager.getInstance().getServersInfo();
        if (managerId.length() != 7) {
            System.out.println("Invalid Manager Id length"); //log needs to be put
            return false;
        }

        for (int i = 0; i < hostPortArray.length; i++) {

            if (managerId.substring(0, 3).equalsIgnoreCase(hostPortArray[i][0])) {
                URL url = new URL("http://"+hostPortArray[i][2]+":"+hostPortArray[i][1]+"/"+hostPortArray[i][0]+"?wsdl");

                WSCenterServerImplService wsCenterServerImplService  = new WSCenterServerImplService(url,new QName("http://webservice.server.dsd.concordia.com/", "WSCenterServerImplService"));
                wsCenterServer = wsCenterServerImplService.getWSCenterServerImplPort();
                return true;
            }
        }
        System.out.println("Invalid Manager Id location"); //log needs to be put

        return false;
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        Scanner c = new Scanner(System.in);
        System.out.println("Welcome to the Distributed Class Management System:");
        boolean managerLogInFlag = true;
        while (managerLogInFlag) {
            System.out.println("Enter the manager Id: ");
            String managerId = c.nextLine().toUpperCase();
            if (createConnection(managerId)) {
                clientLogger = LoggingUtil.getInstance().getClientLogger(managerId);
                boolean selectionFlag = true;
                while (selectionFlag) {
                    String userSelection = selectionMenu();
                    switch (userSelection) {

                        case "1":
                            String studentRecordid = wsCenterServer.createSRecord(getFieldInput("First Name", ""), getFieldInput("Last Name", "").toUpperCase(),
                                    getFieldInput("courses registered(separated by commas)", ""), getFieldInput("status active/inactive", "status"),
                                    getFieldInput("status date(dd-mm-yyyy)", "date"), managerId);
                            clientLogger.log(Level.INFO, String.format(CMSLogMessages.CREATED_STUDENT_RECORD_MSG, studentRecordid, managerId));
                            System.out.println("Record successfully created with record ID: " + studentRecordid);
                            break;

                        case "2":
                            String locs = "[";
                            for (int i = 0; i < getHostPortArray().length; i++) {
                                locs = locs + " " + getHostPortArray()[i][0];
                            }
                            locs = locs + "]";

                            String teacherRecordid = wsCenterServer.createTRecord(getFieldInput("First Name", ""), getFieldInput("Last Name", "").toUpperCase(),
                                    getFieldInput("address", ""), getFieldInput("phone", "phone"),
                                    getFieldInput("specialization", ""), getFieldInput("location" + locs, "location").toUpperCase(), managerId);
                            clientLogger.log(Level.INFO, String.format(CMSLogMessages.CREATED_TEACHER_RECORD_MSG, teacherRecordid, managerId));
                            System.out.println("Record successfully created with record ID: " + teacherRecordid);
                            break;
                        case "3":
                            String countOfRec = wsCenterServer.getRecordCounts(managerId);
                            System.out.println(countOfRec);
                            clientLogger.log(Level.INFO, CMSLogMessages.RECORD_COUNT, countOfRec);
                            break;

                        case "4":
                            String inputRecordId = getFieldInput("record id", "recordId");
                            String fieldNames = " ";
                            String fieldToBeChanged=" ";
                            if (inputRecordId.substring(0, 2).equalsIgnoreCase("TR")) {
                                fieldNames = "(address, location, phone)";
                                fieldToBeChanged = getFieldInput("field" + fieldNames, "UpdateTR");
                            } else if (inputRecordId.substring(0, 2).equalsIgnoreCase("SR")) {
                                fieldNames = "(statusdate, status, coursesregistered)";
                                fieldToBeChanged = getFieldInput("field" + fieldNames, "UpdateSR");
                            }

                            String newFieldValue = getFieldInput("New Value", "");
                            String returnValue = wsCenterServer.editRecord(inputRecordId.toUpperCase(), fieldToBeChanged, newFieldValue, managerId);
                            if(returnValue.equals("TRUE")) {
                                clientLogger.log(Level.INFO, String.format(CMSLogMessages.UPDATE_RECORD_MSG, fieldToBeChanged, newFieldValue, inputRecordId, managerId));
                                System.out.println("Record successfully updated");
                            }
                            else {clientLogger.log(Level.SEVERE, String.format(returnValue, inputRecordId));
                                System.out.println(String.format(returnValue, inputRecordId));
                            }
                            break;

                        case "5":
                            String recordId = getFieldInput("record id", "").toUpperCase();
                            String destinationLoc = getFieldInput("destination server", "location");
                            String transferStatus = wsCenterServer.transferRecord(managerId, recordId, destinationLoc);
                            clientLogger.log(Level.INFO, transferStatus);
                            System.out.println(transferStatus);
                            break;

                        case "6":
                            selectionFlag = false;
                            break;
                        case "7":
                            selectionFlag = false;
                            managerLogInFlag = false;
                            break;
                    }

                }
            }
        }

    }


    public static String getFieldInput(String fieldName, String fieldType) {

        String input;
        while (true) {
            System.out.println("Please enter " + fieldName);
            Scanner sc = new Scanner(System.in);
            input = sc.nextLine();
            if (fieldType.equalsIgnoreCase("status")) {
                if (!(input.equalsIgnoreCase("active") || input.equalsIgnoreCase("inactive"))) {
                    System.out.println("Status can be active or inactive");
                    continue;
                } else {
                    input = input.toLowerCase();
                }
            } else if (fieldType.equalsIgnoreCase("date")) {
                if (!input.matches("\\d{2}-\\d{2}-\\d{4}") || !Validator.getInstance().isValidDate(input, "dd-MM-yyyy")) {
                    System.out.println("Not a valid Date Format");
                    continue;
                }
            } else if (fieldType.equalsIgnoreCase("location")) {
                boolean checkFlag = false;
                for (int i = 0; i < getHostPortArray().length; i++) {
                    if (input.equalsIgnoreCase(getHostPortArray()[i][0])) {
                        checkFlag = true;
                        break;
                    }
                }
                if (!checkFlag) {
                    System.out.println("Invalid location");
                    continue;
                }
            } else if (fieldType.equalsIgnoreCase("phone")) {
                if (!input.matches("^[0-9]+$")) {
                    System.out.println("Invalid Phone number");
                    continue;
                }
            } else if (fieldType.equalsIgnoreCase("recordid")) {
                if ((input.length() != 7) || !(input.substring(2, 7).matches("^[0-9]*$")) || !(input.substring(0, 2).equalsIgnoreCase("TR") || input.substring(0, 2).equalsIgnoreCase("SR"))) {
                    System.out.println("Invalid Record Id.");
                    continue;
                }
            }
            else if(fieldType.equalsIgnoreCase("UpdateTR")){
                List<String> fieldArr = Arrays.asList("address", "location", "phone");
                if(!fieldArr.contains(input.toLowerCase())){
                    System.out.println("Invalid field name");
                    continue;
                }
            }
            else if(fieldType.equalsIgnoreCase("UpdateSR")){
                List<String> fieldArr = Arrays.asList("statusdate", "status", "coursesregistered");
                if(!fieldArr.contains(input.toLowerCase())){
                    System.out.println("Invalid field name");
                    continue;
                }
            }
            break;
        }
        return input;
    }

    public static String selectionMenu() {

        System.out.println("1. Add a Student");
        System.out.println("2. Add a Teacher");
        System.out.println("3. Get records count");
        System.out.println("4. Edit a record");
        System.out.println("5. Transfer a record");
        System.out.println("6. Log out");
        System.out.println("7. Exit");
        System.out.println();
        System.out.println("Please make a selection");
        Scanner sc = new Scanner(System.in);
        return sc.nextLine();
    }

}

