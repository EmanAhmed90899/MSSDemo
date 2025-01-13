package com.hemaya.mssdemo.model.identification.otpResponse;

import com.google.gson.annotations.SerializedName;

public class Data{

	@SerializedName("expiration")
	private String expiration;

	@SerializedName("uuid")
	private String uuid;

	public String getExpiration(){
		return expiration;
	}

	public String getUuid(){
		return uuid;
	}
}