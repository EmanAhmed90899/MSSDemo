package com.hemaya.mssdemo.model.identification.storeUserResponse;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class StoreUserResponse{

	@SerializedName("data")
	private List<Object> data;

	@SerializedName("error_code")
	private Object errorCode;

	@SerializedName("message")
	private String message;

	@SerializedName("errors")
	private List<Object> errors;

	public List<Object> getData(){
		return data;
	}

	public Object getErrorCode(){
		return errorCode;
	}

	public String getMessage(){
		return message;
	}

	public List<Object> getErrors(){
		return errors;
	}
}