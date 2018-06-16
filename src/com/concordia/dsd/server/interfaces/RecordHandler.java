package com.concordia.dsd.server.interfaces;

import com.concordia.dsd.model.Record;

interface RecordHandler {
    void updateRecord(Record record,String recordID, String fieldName, String newValue);
}
