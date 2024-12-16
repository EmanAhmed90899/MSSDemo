package com.hemaya.mssdemo.model.mdlAddDevice.Response;

import com.google.gson.annotations.SerializedName;

public class Result{

	@SerializedName("instanceActivationMessage")
	private String instanceActivationMessage;

	public String getInstanceActivationMessage(){
		return instanceActivationMessage;
	}
}