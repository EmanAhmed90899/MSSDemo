package com.hemaya.mssdemo.model.error.validationError;

import com.google.gson.annotations.SerializedName;

public class ValidationResponse{

	@SerializedName("message")
	private String message;

	@SerializedName("errors")
	private Errors errors;

	public String getMessage(){
		return message;
	}

	public Errors getErrors(){
		return errors;
	}
}