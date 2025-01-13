package com.hemaya.mssdemo.model.identification.otpRequest;

import com.google.gson.annotations.SerializedName;

public class OtpRequest{
	public OtpRequest(String phone, int digits) {
		this.phone = phone;
		this.digits = digits;
	}

	@SerializedName("phone")
	private String phone;

	@SerializedName("digits")
	private int digits;

	public String getPhone(){
		return phone;
	}

	public int getDigits(){
		return digits;
	}
}