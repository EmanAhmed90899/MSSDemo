package com.hemaya.mssdemo.model.identification.resendOtpRequest;

import com.google.gson.annotations.SerializedName;

public class ResendOtpRequest{

	@SerializedName("uuid")
	private String uuid;

	public ResendOtpRequest(String uuid) {
		this.uuid = uuid;
	}

	public String getUuid(){
		return uuid;
	}
}