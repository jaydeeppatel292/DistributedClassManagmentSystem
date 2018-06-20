package com.concordia.dsd.server.manager;

import com.concordia.dsd.exception.InvalidFieldException;
import com.concordia.dsd.global.cmsenum.Location;
import com.concordia.dsd.global.constants.CMSConstants;
import com.concordia.dsd.global.constants.CMSLogMessages;
import com.concordia.dsd.model.Record;
import com.concordia.dsd.model.StudentRecord;
import com.concordia.dsd.model.TeacherRecord;
import com.concordia.dsd.server.interfaces.TeacherHandler;
import com.concordia.dsd.model.ClassMap;
import com.concordia.dsd.utils.Validator;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TeacherManager implements TeacherHandler {
    private AtomicInteger teacherRecordCounter;
    private Logger serverLogger;
    private ClassMap classMap;

    public TeacherManager(ClassMap classMap, Logger serverLogger) {
        this.serverLogger = serverLogger;
        this.classMap = classMap;
        teacherRecordCounter = new AtomicInteger();
    }
    @Override
    public Location validateLocation(String newValue) throws InvalidFieldException {
        try {
            return Location.valueOf(newValue);
        } catch (IllegalArgumentException exception) {
            throw new InvalidFieldException("Entered Location is invalid: choices(MTL,LVL,DDO)");
        }
    }

    @Override
    public Record insertRecord(String firstName, String lastName, String address, String phone, String specialization, Location location, String managerId) {
        TeacherRecord teacherRecord = null;
        try {
            if (Validator.getInstance().isValidLastName(lastName)) {
                String recordId = CMSConstants.TEACHER_ID_PREFIX
                        + String.format("%05d", teacherRecordCounter.incrementAndGet());
                teacherRecord = new TeacherRecord(recordId, firstName, lastName, address, phone,
                        specialization, location);
                classMap.addRecord(Character.toString(lastName.charAt(0)), teacherRecord);
                serverLogger.log(Level.INFO, String.format(CMSLogMessages.CREATED_TEACHER_RECORD_MSG, recordId, managerId));
            } else {
                serverLogger.log(Level.SEVERE, CMSLogMessages.INVALID_LAST_NAME_MSG, lastName);
            }
        } catch (Exception e) {
            serverLogger.log(Level.SEVERE, CMSLogMessages.CREATE_RECORD_FAILED_MSG);
            System.out.println(e.getMessage());
        }
        return teacherRecord;
    }

    @Override
    public void updateRecord(Record record, String recordID, String fieldName, String newValue, String managerId) {
        TeacherRecord teacherRecord = (TeacherRecord) record;
        if (teacherRecord != null) {
            try {
                String field = fieldName.toLowerCase();
                if (field.equalsIgnoreCase("address")) {
                    teacherRecord.setAddress(newValue);
                } else if (field.equalsIgnoreCase("phone")) {
                    teacherRecord.setPhone(newValue);
                } else if (field.equalsIgnoreCase("location")) {
                    teacherRecord.setLocation(this.validateLocation(newValue));
                } else {
                    throw new InvalidFieldException("Entered Field can-not be changed or it does not exist ");
                }
                serverLogger.log(Level.INFO,
                        String.format(CMSLogMessages.UPDATE_RECORD_MSG, fieldName, newValue, teacherRecord.getRecordId(), managerId));
            } catch (InvalidFieldException e) {
                serverLogger.log(Level.SEVERE, e.getMessage());
            }
        } else {
            serverLogger.log(Level.SEVERE, CMSLogMessages.RECORD_NOT_FOUND, teacherRecord.getRecordId());
        }
    }
}
