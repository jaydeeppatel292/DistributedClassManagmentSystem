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
    SYNC_BACKUP_SERVER
}
