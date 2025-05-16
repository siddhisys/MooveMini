package com.moove.model;

public class EnrollmentModel {
	private Integer Enrollment_Id;
	private Integer User_Id;
	private Integer Program_Id;
	
	public EnrollmentModel() {
	}

	public EnrollmentModel(Integer enrollment_Id, Integer user_Id, Integer program_Id) {
		super();
		this.Enrollment_Id = enrollment_Id;
		this.User_Id = user_Id;
		this.Program_Id = program_Id;
	}

	public EnrollmentModel(Integer enrollment_Id) {
		this.Enrollment_Id = enrollment_Id;
	}

	public EnrollmentModel(Integer user_Id, Integer program_Id) {
		this.User_Id = user_Id;
		this.Program_Id = program_Id;
	}

	public Integer getEnrollment_Id() {
		return Enrollment_Id;
	}

	public void setEnrollment_Id(Integer enrollment_Id) {
		Enrollment_Id = enrollment_Id;
	}

	public Integer getUser_Id() {
		return User_Id;
	}

	public void setUser_Id(Integer user_Id) {
		User_Id = user_Id;
	}

	public Integer getProgram_Id() {
		return Program_Id;
	}

	public void setProgram_Id(Integer program_Id) {
		Program_Id = program_Id;
	}
}

