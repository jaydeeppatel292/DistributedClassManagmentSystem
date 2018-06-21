package com.concordia.dsd.model;


import com.concordia.dsd.model.Record;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClassMap {
    private HashMap<String, List<Record>> recordMap;

    public ClassMap() {
        recordMap = new HashMap<>();
    }

    /**
     * Add RecordList
     * @param key
     * @param recordList
     */
    private void putRecordList(String key, List<Record> recordList) {
        // synchronized on databse
        synchronized (recordMap) {
            recordMap.put(key, recordList);
        }
    }

    /**
     * Get Record list
     * @param key
     * @return
     */
    private List<Record> getRecordList(String key) {
        // synchronized on databse
        synchronized (recordMap) {
            return recordMap.get(key);
        }
    }

    /**
     * Add new Record in recordlist
     * @param recordKey
     * @param record
     */
    public void addRecord(String recordKey, Record record) {
        List<Record> recordList = getRecordList(recordKey.toUpperCase());
        if (recordList != null) {
            recordList.add(record);
        } else {
            recordList = new ArrayList<>();
            recordList.add(record);
        }
        putRecordList(recordKey.toUpperCase(), recordList);
    }

    /**
     * Lookup Record for recordId
     * @param recordId
     * @return
     */
    public Record lookupRecord(String recordId) {
        Record record = null;
        synchronized (recordMap) {
            for (Map.Entry<String, List<Record>> entry : recordMap.entrySet()) {
                List<Record> recordList = entry.getValue();
                if (recordList != null && recordList.size() > 0) {
                    for (Record recordLocal : recordList) {
                        if (recordLocal.getRecordId().equals(recordId)) {
                            record = recordLocal;
                        }
                    }
                }
            }
        }
        return record;
    }

    /**
     * Get Records Count
     * @return
     */
    public Integer getRecordsCount() {
        int count = 0;
        synchronized (recordMap) {
            for (List<Record> list : recordMap.values()) {
                count += list.size();
            }
        }
        return count;
    }

    /**
     * Delete Record form recordlist
     * @param record
     */
    public void deleteRecord(Record record) {
        List<Record> recordList = getRecordList(record.getLastName().substring(0, 1).toUpperCase());
        for (Record rec : recordList) {
            if (rec.getRecordId().equals(record.getRecordId())) {
                recordList.remove(rec);
                break;
            }
        }
        putRecordList(record.getLastName().substring(0, 1), recordList);
    }
}
