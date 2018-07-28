package com.concordia.dsd.server.generics;

import com.concordia.dsd.global.cmsenum.Location;
import com.concordia.dsd.global.enums.RequestType;
import com.concordia.dsd.model.Record;
import com.concordia.dsd.model.StudentRecord;
import com.concordia.dsd.model.TeacherRecord;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FIFORequestQueueModel implements Serializable {


    private RequestType requestType;
    private StudentRecord studentRecord;
    private TeacherRecord teacherRecord;
    private String recordId;
    private String fieldName;
    private String newValue;
    private String managerId;
    private String centerServerName;
    private Location requestLocation;
    private transient List<Integer> processIdList = new ArrayList<>();
    private boolean isSyncRequest = false;

    public FIFORequestQueueModel createCopy() {
        FIFORequestQueueModel fifoRequestQueueModel = new FIFORequestQueueModel(requestType, managerId, requestLocation);
        fifoRequestQueueModel.requestType = this.requestType;
        fifoRequestQueueModel.studentRecord = this.studentRecord;
        fifoRequestQueueModel.teacherRecord = this.teacherRecord;
        fifoRequestQueueModel.recordId = this.recordId;
        fifoRequestQueueModel.fieldName = this.fieldName;
        fifoRequestQueueModel.newValue = this.newValue;
        fifoRequestQueueModel.managerId = this.managerId;
        fifoRequestQueueModel.centerServerName = this.centerServerName;
        fifoRequestQueueModel.requestLocation = this.requestLocation;
        fifoRequestQueueModel.isSyncRequest = this.isSyncRequest;
        return fifoRequestQueueModel;
    }

    public void setRequestLocation(Location requestLocation) {
        this.requestLocation = requestLocation;
    }

    public void setSyncRequest(boolean syncRequest) {
        isSyncRequest = syncRequest;
    }

    public boolean isSyncRequest() {
        return isSyncRequest;
    }

    public FIFORequestQueueModel(RequestType requestType) {
        this.requestType = requestType;
    }

    public FIFORequestQueueModel(RequestType requestType, List<Integer> processIdList) {
        this.requestType = requestType;
        this.processIdList = processIdList;
    }

    public FIFORequestQueueModel(RequestType requestType, String managerId, Location requestLocation) {
        this.requestType = requestType;
        this.managerId = managerId;
        this.requestLocation = requestLocation;
    }

    public FIFORequestQueueModel(RequestType requestType, Record record, String managerId, Location requestLocation) {
        this.requestLocation = requestLocation;
        if (requestType == RequestType.CREATE_S_RECORD)
            this.studentRecord = (StudentRecord) record;
        else
            this.teacherRecord = (TeacherRecord) record;

        this.requestType = requestType;
        this.managerId = managerId;

    }


    public FIFORequestQueueModel(RequestType requestType, String recordId, String fieldName, String newValue, String managerId, String centerServerName, Location requestLocation) {
        this.requestType = requestType;
        this.recordId = recordId;
        this.fieldName = fieldName;
        this.newValue = newValue;
        this.managerId = managerId;
        this.requestLocation = requestLocation;
        if (this.requestType == RequestType.TRANSFER_RECORD) {
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

    public Location getRequestLocation() {
        return requestLocation;
    }

    public List<Integer> getProcessIdList() {
        return processIdList;
    }

    public void setProcessIdList(List<Integer> processIdList) {
        this.processIdList = processIdList;
    }

    @Override
    public String toString() {
        return "MANAGERID: " + managerId + " REQUESTTYPE" + requestType + " LOCATION:: " + requestLocation.toString();
    }
}
