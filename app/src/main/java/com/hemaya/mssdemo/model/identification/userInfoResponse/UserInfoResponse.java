package com.hemaya.mssdemo.model.identification.userInfoResponse;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class UserInfoResponse {

	@SerializedName("data")
	private Data data;

	@SerializedName("error_code")
	private String errorCode;

	@SerializedName("message")
	private String message;

	@SerializedName("errors")
	private List<Object> errors;

	public Data getData(){
		return data;
	}

	public String getErrorCode(){
		return errorCode;
	}

	public String getMessage(){
		return message;
	}

	public List<Object> getErrors(){
		return errors;
	}
}