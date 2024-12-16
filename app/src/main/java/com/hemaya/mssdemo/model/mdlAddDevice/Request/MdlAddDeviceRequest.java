package com.hemaya.mssdemo.model.mdlAddDevice.Request;

import com.google.gson.annotations.SerializedName;

public class MdlAddDeviceRequest{

	@SerializedName("registrationIdentifier")
	private String registrationIdentifier;

	@SerializedName("description")
	private String description;

	@SerializedName("deviceCode")
	private String deviceCode;

	public MdlAddDeviceRequest(String registrationIdentifier, String description, String deviceCode) {
		this.registrationIdentifier = registrationIdentifier;
		this.description = description;
		this.deviceCode = deviceCode;
	}

	public String getRegistrationIdentifier(){
		return registrationIdentifier;
	}

	public String getDescription(){
		return description;
	}

	public String getDeviceCode(){
		return deviceCode;
	}
}