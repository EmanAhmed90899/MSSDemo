package com.hemaya.mssdemo.model.identification.UserInfoRequest;

import com.google.gson.annotations.SerializedName;

public class UserInfoRequest {
	@SerializedName("national_id")
	private String national_id;


	public UserInfoRequest(String national_id) {
		this.national_id = national_id;
	}

	public String getNationalId(){
		return national_id;
	}



}