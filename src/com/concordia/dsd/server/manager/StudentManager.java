package com.concordia.dsd.server.manager;

import com.concordia.dsd.exception.InvalidFieldException;
import com.concordia.dsd.global.cmsenum.Status;
import com.concordia.dsd.global.constants.CMSConstants;
import com.concordia.dsd.global.constants.CMSLogMessages;
import com.concordia.dsd.model.Record;
import com.concordia.dsd.model.StudentRecord;
import com.concordia.dsd.server.interfaces.StudentHandler;
import com.concordia.dsd.model.ClassMap;
import com.concordia.dsd.utils.Validator;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StudentManager implements StudentHandler{
    private AtomicInteger studentRecordCounter;
    private ClassMap classMap;
    private Logger serverLogger;

    public StudentManager(ClassMap classMap, Logger serverLogger) {
        this.classMap = classMap;
        this.serverLogger = serverLogger;
        studentRecordCounter = new AtomicInteger();
    }

    @Override
    public Status validateStatus(String statusType) throws InvalidFieldException {

        return null;
    }

    @Override
    public synchronized Record insertRecord(String firstName, String lastName, List<String> registerdCourseList, Status status, String statusDate) {
        StudentRecord studentRecord =null;
        try {
            if (Validator.getInstance().isValidLastName(lastName)) {
                String recordId = CMSConstants.STUDENT_ID_PREFIX
                        + String.format("%05d", studentRecordCounter.incrementAndGet());
                studentRecord = new StudentRecord(recordId, firstName, lastName, status, registerdCourseList,
                        statusDate);
                classMap.addRecord(Character.toString(lastName.charAt(0)), studentRecord);
                serverLogger.log(Level.INFO, String.format(CMSLogMessages.CREATED_STUDENT_RECORD_MSG, recordId, " "));
            } else {
                serverLogger.log(Level.SEVERE, CMSLogMessages.INVALID_LAST_NAME_MSG, lastName);
            }
        } catch (Exception e) {
            serverLogger.log(Level.SEVERE, CMSLogMessages.CREATE_RECORD_FAILED_MSG);
        }
        return studentRecord;
    }

    @Override
    public void updateRecord(Record record,String recordID, String fieldName, String newValue) {
        StudentRecord studentRecord = (StudentRecord) record;
        if (studentRecord != null) {
            try {
                String field = fieldName.toLowerCase();
                if (field.equalsIgnoreCase("statusdate")) {
                    studentRecord.setStatusDate(newValue);
                } else if (field.equalsIgnoreCase("status")) {
                    studentRecord.setStatus(this.validateStatus(newValue));
                } else if (field.equalsIgnoreCase("coursesregistered")) {
                    studentRecord.setCourseRegistered((studentRecord.getCourseRegistered()));
                } else {
                    throw new InvalidFieldException("Entered Field can-not be changed or it does not exist ");
                }
                serverLogger.log(Level.INFO,
                        String.format(CMSLogMessages.UPDATE_RECORD_MSG, fieldName, newValue, studentRecord.getRecordId(), " "));
            } catch (InvalidFieldException e) {
                serverLogger.log(Level.SEVERE, e.getMessage());
            }
        } else {
            serverLogger.log(Level.SEVERE, CMSLogMessages.RECORD_NOT_FOUND, studentRecord.getRecordId());
        }


    }
}
