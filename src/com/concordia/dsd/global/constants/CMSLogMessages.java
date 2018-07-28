package com.concordia.dsd.global.constants;

public class CMSLogMessages {
	
    public static final String INVALID_LAST_NAME_MSG = "Failed to create record: Invalid last name {0}";
	public static final String CREATED_STUDENT_RECORD_MSG = "Created student record: Record ID = %s by manager %s";
    public static final String CREATED_TEACHER_RECORD_MSG = "Created teacher record: Record ID = %s by manager %s";
    public static final String UPDATE_RECORD_MSG = "Updated field %s of record with the value %s : Record ID = %s by manager %s";
	public static final String RECORD_NOT_FOUND = "Failed to update record: Record %s Not Found";
    public static final String RECORDID_NOT_FOUND = "Failed to update record: Record %S Not Found";
    public static final String RECORD_COUNT = "Get Record Counts: {0}";
    public static final String RECORD_COUNT_SERVER_INIT = "UDP Request initiated to retrieve from %s Server on %s:%s";
    public static final String RECORD_COUNT_SERVER_COMPLETE = "UDP Request completed to retrieve from %s Server on %s:%s with Record Count: %s";
    public static final String UDP_SERVER_INIT  = "UDP %s Server Initiated";
	public static final String EDIT_RECORD_FAILED_MSG = "Edit record operation failed";
	public static final String CREATE_RECORD_FAILED_MSG = "Create record operation failed";
    public static final String TRANSFER_RECORD_SUCCESS = "Record %s Transferred Successfully by %s";
    public static final String ELECTION_INIT = "Election initiated by %s port";
    public static final String ELECTION_FAILURE_MESSAGE = "OK Message received by %s port";
    public static final String COORDINATOR_FOUND = "Coordinator Server found in %s port. Broadcast notify initiated";
    public static final String COORDINATOR_NOTIFY_MESSAGE = "Hi %s Server port, I'm the Coordinator at %s port";
    public static final String MASTER_FAILURE_MESSAGE = "Master Server running on port %s on location %s is down.Calling Bully Algorithm to select the new leader";
    public static final String REPLICA_FAILURE_MESSAGE = "Replica Server of location %s with port %s is down. Removing replica from the %s server list.";
}
