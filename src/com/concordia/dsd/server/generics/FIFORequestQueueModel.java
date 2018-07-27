package com.concordia.dsd.server.generics;

import com.concordia.dsd.global.enums.RequestType;
import com.concordia.dsd.model.Record;
import com.concordia.dsd.model.StudentRecord;
import com.concordia.dsd.model.TeacherRecord;

import java.io.Serializable;

public class FIFORequestQueueModel implements Serializable{

    private RequestType requestType;
    private StudentRecord studentRecord;
    private TeacherRecord teacherRecord;
    private String recordId;
    private String fieldName;
    private String newValue;
    private String managerId;
    private String centerServerName;

    public FIFORequestQueueModel(RequestType requestType, String managerId) {
        this.requestType = requestType;
        this.managerId = managerId;
    }

    public FIFORequestQueueModel(RequestType requestType, Record record, String managerId) {
        if(requestType == RequestType.CREATE_S_RECORD)
            this.studentRecord = (StudentRecord) record;
        else
            this.teacherRecord = (TeacherRecord) record;

        this.requestType = requestType;
        this.managerId = managerId;

    }


    public FIFORequestQueueModel(RequestType requestType, String recordId, String fieldName, String newValue, String managerId, String centerServerName) {
        this.requestType = requestType;
        this.recordId = recordId;
        this.fieldName = fieldName;
        this.newValue = newValue;
        this.managerId = managerId;
        if(this.requestType == RequestType.TRANSFER_RECORD){
            this.centerServerName = centerServerName;
        }
    }

    public String getCenterServerName() {
        return centerServerName;
    }

    public void setCenterServerName(String centerServerName) {
        this.centerServerName = centerServerName;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public String getRecordId() {
        return recordId;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public void setStudentRecord(StudentRecord studentRecord) {
        this.studentRecord = studentRecord;
    }

    public void setTeacherRecord(TeacherRecord teacherRecord) {
        this.teacherRecord = teacherRecord;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public StudentRecord getStudentRecord() {
        return studentRecord;
    }

    public TeacherRecord getTeacherRecord() {
        return teacherRecord;
    }

    public String getManagerId() {
        return managerId;
    }
}
