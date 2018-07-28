package com.concordia.dsd.model;


import com.concordia.dsd.exception.InvalidFieldException;
import com.concordia.dsd.global.cmsenum.Location;

import java.io.Serializable;

public class TeacherRecord extends Record implements Serializable {
	
	private String address;

	private String phone;

	private String specialization;

	private Location location;

	public TeacherRecord(String recordId, String firstName, String lastName, String address, String phone,
			String specialization, Location location) {
		super(recordId, firstName, lastName);
		this.address = address;
		this.phone = phone;
		this.specialization = specialization;
		this.location = location;
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
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address
	 *            the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @param phone
	 *            the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * @return the specialization
	 */
	public String getSpecialization() {
		return specialization;
	}

	/**
	 * @param specialization
	 *            the specialization to set
	 */
	public void setSpecialization(String specialization) {
		this.specialization = specialization;
	}

	/**
	 * @return the location
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * @param location
	 *            the location to set
	 */
	public void setLocation(Location location) {
		this.location = location;
	}
}
