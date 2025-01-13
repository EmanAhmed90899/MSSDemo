package com.hemaya.mssdemo.model.identification.resendOtpResponse;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ResendOtpResponse{

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