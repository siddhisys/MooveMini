package com.moove.model;

public class UsersModel {
	private Integer User_ID;
    private String User_Name;
    private String User_Email;
    private String User_Gender;
    private String password;
    private String User_Address;
    private String User_Status;
    private Integer Role_Id;
    private String image_path;
    
	public UsersModel() {
	}

	public UsersModel(String user_Name, String password) {
		this.User_Name = user_Name;
		this.password = password;
	}

	public UsersModel(Integer user_ID, String user_Name, String user_Email, String user_Gender, String password,
			String user_Address, String user_Status, Integer role_Id, String image_path) {
		super();
		this.User_ID = user_ID;
		this.User_Name = user_Name;
		this.User_Email = user_Email;
		this.User_Gender = user_Gender;
		this.password = password;
		this.User_Address = user_Address;
		this.User_Status = user_Status;
		this.Role_Id = role_Id;
		this.image_path = image_path;
	}

	public UsersModel(String user_Name, String user_Email, String user_Gender, String password, String user_Address,
			String user_Status, Integer role_Id, String image_path) {
		this.User_Name = user_Name;
		this.User_Email = user_Email;
		this.User_Gender = user_Gender;
		this.password = password;
		this.User_Address = user_Address;
		this.User_Status = user_Status;
		this.Role_Id = role_Id;
		this.image_path = image_path;
	}

	public Integer getUser_ID() {
		return User_ID;
	}

	public void setUser_ID(Integer user_ID) {
		User_ID = user_ID;
	}

	public String getUser_Name() {
		return User_Name;
	}

	public void setUser_Name(String user_Name) {
		User_Name = user_Name;
	}

	public String getUser_Email() {
		return User_Email;
	}

	public void setUser_Email(String user_Email) {
		User_Email = user_Email;
	}

	public String getUser_Gender() {
		return User_Gender;
	}

	public void setUser_Gender(String user_Gender) {
		User_Gender = user_Gender;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUser_Address() {
		return User_Address;
	}

	public void setUser_Address(String user_Address) {
		User_Address = user_Address;
	}

	public String getUser_Status() {
		return User_Status;
	}

	public void setUser_Status(String user_Status) {
		User_Status = user_Status;
	}

	public Integer getRole_Id() {
		return Role_Id;
	}

	public void setRole_Id(Integer role_Id) {
		Role_Id = role_Id;
	}

	public String getImage_path() {
		return image_path;
	}

	public void setImage_path(String image_path) {
		this.image_path = image_path;
	}
}
