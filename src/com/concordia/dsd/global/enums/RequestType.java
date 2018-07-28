package com.concordia.dsd.global.enums;

import java.io.Serializable;

public enum RequestType implements Serializable {
    CREATE_S_RECORD,
    CREATE_T_RECORD,
    GET_RECORD_COUNT,
    UPDATE_RECORD,
    TRANSFER_RECORD,
    ELECTION,
    COORDINATOR,
    GET_RECORD,
    SYNC_BACKUP_SERVER,
    GET_RECORD_COUNT_SUBS,
    DELETE_RECORD,
    PING_SERVER,
    FAIL_SERVER
}
