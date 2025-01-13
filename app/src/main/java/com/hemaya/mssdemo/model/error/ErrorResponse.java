package com.hemaya.mssdemo.model.error;

import com.google.gson.annotations.SerializedName;

public class ErrorResponse{

	@SerializedName("message")
	private String message;

	public String getMessage(){
		return message;
	}
}