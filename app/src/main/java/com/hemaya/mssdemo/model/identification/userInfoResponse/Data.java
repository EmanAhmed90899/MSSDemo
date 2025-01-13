package com.hemaya.mssdemo.model.identification.userInfoResponse;

import com.google.gson.annotations.SerializedName;

public class Data{

	@SerializedName("nationalId")
	private String nationalId;

	@SerializedName("phone")
	private String phone;
@SerializedName("vascoId")
	private String vascoId;

	@SerializedName("name")
	private Name name;

	public String getNationalId(){
		return nationalId;
	}

	public String getPhone(){
		return phone;
	}

	public Name getName(){
		return name;
	}

	public String getVascoId(){
		return vascoId;
	}
}