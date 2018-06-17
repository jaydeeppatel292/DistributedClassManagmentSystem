package com.concordia.dsd.server.interfaces;

import com.concordia.dsd.exception.InvalidFieldException;
import com.concordia.dsd.global.cmsenum.Status;
import com.concordia.dsd.model.Record;

import java.util.List;

public interface StudentHandler extends RecordHandler {
    Status validateStatus(String statusType) throws InvalidFieldException;
    Record insertRecord(String firstName, String lastName, String registerdCourseList, Status status, String statusDate);
}
