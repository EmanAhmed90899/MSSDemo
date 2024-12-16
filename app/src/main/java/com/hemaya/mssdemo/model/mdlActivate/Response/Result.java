package com.hemaya.mssdemo.model.mdlActivate.Response;

import com.google.gson.annotations.SerializedName;

public class Result{

	@SerializedName("serialNumber")
	private String serialNumber;

	@SerializedName("domain")
	private String domain;

	@SerializedName("userID")
	private String userID;

	public String getSerialNumber(){
		return serialNumber;
	}

	public String getDomain(){
		return domain;
	}

	public String getUserID(){
		return userID;
	}

	@Override
	public String toString() {
		return "Result{" +
				"serialNumber='" + serialNumber + '\'' +
				", domain='" + domain + '\'' +
				", userID='" + userID + '\'' +
				'}';
	}
}