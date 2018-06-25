package com.file.dto;

import javax.validation.constraints.NotNull;

public class UserDto {
	
	@NotNull(message="first name should not be empty")
	private String firstName;
	
	@NotNull(message="last name can not be empty")
	private String lastName;
	
	@NotNull(message="address can not be empty")
	private String address;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	

}
