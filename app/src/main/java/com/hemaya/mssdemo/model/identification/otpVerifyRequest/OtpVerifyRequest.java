package com.hemaya.mssdemo.model.identification.otpVerifyRequest;

import com.google.gson.annotations.SerializedName;

public class OtpVerifyRequest{

	@SerializedName("otp")
	private String otp;

	@SerializedName("uuid")
	private String uuid;

	public OtpVerifyRequest(String otp, String uuid) {
		this.otp = otp;
		this.uuid = uuid;
	}

	public String getOtp(){
		return otp;
	}

	public String getUuid(){
		return uuid;
	}
}