package com.hemaya.mssdemo.model.identification.storeUserRequest;

import com.google.gson.annotations.SerializedName;

public class StoreUserRequest{

	@SerializedName("nationalId")
	private String nationalId;

	@SerializedName("serialNumber")
	private String serialNumber;

	@SerializedName("phone")
	private String phone;

	@SerializedName("vascoId")
	private String vascoId;

	@SerializedName("deviceId")
	private String deviceId;

	@SerializedName("platform")
	private String platform = "android";

	public StoreUserRequest(String nationalId, String serialNumber, String phone, String vascoId, String deviceId) {
		this.nationalId = nationalId;
		this.serialNumber = serialNumber;
		this.phone = phone;
		this.vascoId = vascoId;
		this.deviceId = deviceId;
	}

	public String getNationalId(){
		return nationalId;
	}

	public String getSerialNumber(){
		return serialNumber;
	}

	public String getPhone(){
		return phone;
	}

	public String getVascoId(){
		return vascoId;
	}

	public String getDeviceId(){
		return deviceId;
	}

	public String getPlatform(){
		return platform;
	}
}