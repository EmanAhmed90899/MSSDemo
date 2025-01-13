package com.hemaya.mssdemo.model.identification.otpVerifyResponse;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class OtpVerifyResponse{

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

	@Override
	public String toString() {
		return "OtpVerifyResponse{" +
				"data=" + data +
				", errorCode=" + errorCode +
				", message='" + message + '\'' +
				", errors=" + errors +
				'}';
	}
}