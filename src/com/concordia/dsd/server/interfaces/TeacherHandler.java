package com.concordia.dsd.server.interfaces;

import com.concordia.dsd.exception.InvalidFieldException;
import com.concordia.dsd.global.cmsenum.Location;
import com.concordia.dsd.model.Record;

public interface TeacherHandler extends RecordHandler {
    Record insertRecord(String firstName, String lastName, String address, String phone, String specialization,
                        Location location);
    Location validateLocation(String newValue) throws InvalidFieldException;

}
