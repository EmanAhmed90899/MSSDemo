package com.hemaya.mssdemo.model.identification.otpResponse;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class OtpResponse{

	@SerializedName("data")
	private Data data;

	@SerializedName("error_code")
	private Object errorCode;

	@SerializedName("message")
	private String message;

	@SerializedName("errors")
	private List<Object> errors;

	public Data getData(){
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