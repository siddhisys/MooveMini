package com.moove.model;

public class RoleModel {
	private Integer Role_ID;
    private String Role_Name;
    
    public RoleModel() {
	}

	public RoleModel(Integer role_ID) {
		this.Role_ID = role_ID;
	}

	public RoleModel(Integer role_ID, String role_Name) {
		this.Role_ID = role_ID;
		this.Role_Name = role_Name;
	}

	public RoleModel(String role_Name) {
		this.Role_Name = role_Name;
	}

	public Integer getRole_ID() {
		return Role_ID;
	}

	public void setRole_ID(Integer role_ID) {
		Role_ID = role_ID;
	}

	public String getRole_Name() {
		return Role_Name;
	}

	public void setRole_Name(String role_Name) {
		Role_Name = role_Name;
	}
}
