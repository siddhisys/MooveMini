package com.moove.model;

public class ProgramModel {
	private Integer Program_Id;
	private String Program_Name;
	private String Program_Level;
	private Integer Program_Classes;
	private String Program_Desc;
	private String image_path;
	
	public ProgramModel() {
	}

	public ProgramModel(String program_Name, String program_Level, String image_path, String program_Desc, Integer program_Classes) {
		super();
		this.Program_Name = program_Name;
		this.Program_Level = program_Level;
		this.setProgram_Classes(program_Classes);
		this.image_path = image_path;
		
	}
	
	public ProgramModel(String Program_Name) {
		this.Program_Name = Program_Name;
	}

	public Integer getProgram_Id() {
		return Program_Id;
	}

	public void setProgram_Id(Integer program_Id) {
		Program_Id = program_Id;
	}

	public String getProgram_Name() {
		return Program_Name;
	}

	public void setProgram_Name(String program_Name) {
		Program_Name = program_Name;
	}

	public String getProgram_Level() {
		return Program_Level;
	}

	public void setProgram_Level(String program_Level) {
		Program_Level = program_Level;
	}

	public String getImage_path() {
		return image_path;
	}

	public void setImage_path(String image_path) {
		this.image_path = image_path;
	}

	public String getProgram_Desc() {
		return Program_Desc;
	}

	public void setProgram_Desc(String program_Desc) {
		Program_Desc = program_Desc;
	}

	public Integer getProgram_Classes() {
		return Program_Classes;
	}

	public void setProgram_Classes(Integer program_Classes) {
		Program_Classes = program_Classes;
	}
}
