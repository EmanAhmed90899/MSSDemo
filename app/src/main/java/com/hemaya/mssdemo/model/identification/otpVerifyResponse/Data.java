package com.hemaya.mssdemo.model.identification.otpVerifyResponse;

import com.google.gson.annotations.SerializedName;

public class Data{

	@SerializedName("verified")
	private boolean verified;

	@SerializedName("uuid")
	private String uuid;

	public boolean isVerified(){
		return verified;
	}

	public String getUuid(){
		return uuid;
	}

	@Override
	public String toString() {
		return "Data{" +
				"verified=" + verified +
				", uuid='" + uuid + '\'' +
				'}';
	}
}