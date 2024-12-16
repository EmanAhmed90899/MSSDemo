package com.hemaya.mssdemo.model.ephemeral.response;

import com.google.gson.annotations.SerializedName;

public class ResultCodes{

	@SerializedName("returnCode")
	private int returnCode;

	@SerializedName("returnCodeEnum")
	private String returnCodeEnum;

	@SerializedName("statusCodeEnum")
	private String statusCodeEnum;

	@SerializedName("statusCode")
	private int statusCode;

	public int getReturnCode(){
		return returnCode;
	}

	public String getReturnCodeEnum(){
		return returnCodeEnum;
	}

	public String getStatusCodeEnum(){
		return statusCodeEnum;
	}

	public int getStatusCode(){
		return statusCode;
	}

	@Override
	public String toString() {
		return "ResultCodes{" +
				"returnCode=" + returnCode +
				", returnCodeEnum='" + returnCodeEnum + '\'' +
				", statusCodeEnum='" + statusCodeEnum + '\'' +
				", statusCode=" + statusCode +
				'}';
	}
}