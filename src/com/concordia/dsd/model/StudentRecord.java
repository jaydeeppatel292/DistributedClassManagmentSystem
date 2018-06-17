package com.concordia.dsd.model;


import com.concordia.dsd.exception.InvalidFieldException;
import com.concordia.dsd.global.cmsenum.Status;

import java.util.List;

public class StudentRecord extends Record {

	private String courseRegistered;

	private Status status;

	private String statusDate;

	public StudentRecord(String recordId, String firstName, String lastName, Status status,
						 String courseRegistered, String statusDate) {
		super(recordId, firstName, lastName);
		this.status = status;
		this.courseRegistered = courseRegistered;
		this.statusDate = statusDate;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return super.getFirstName();
	}

	/**
	 * @param firstName
	 *            the firstName to set
	 */
	public void setFirstName(String firstName) {
		super.setFirstName(firstName);
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return super.getLastName();
	}

	/**
	 * @param lastName
	 *            the lastName to set
	 */
	public void setLastName(String lastName) {
		super.setLastName(lastName);
	}

	/**
	 * @return the courseRegistered
	 */
	public String getCourseRegistered() {
		return courseRegistered;
	}

	/**
	 * @param courseRegistered
	 *            the courseRegistered to set
	 */
	public void setCourseRegistered(String courseRegistered) {
		this.courseRegistered = courseRegistered;
	}

	/**
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(Status status) {
		this.status = status;
	}

	/**
	 * @return the statusDate
	 */
	public String getStatusDate() {
		return statusDate;
	}

	/**
	 * @param statusDate
	 *            the statusDate to set
	 */
	public void setStatusDate(String statusDate) {
		this.statusDate = statusDate;
	}

	private Status validateStatus(String newValue) throws InvalidFieldException {
		try {
			return Status.valueOf(newValue);
		} catch (IllegalArgumentException exception) {
			throw new InvalidFieldException("Entered status is invalid : choices(active,inactive)");
		}
	}

}
