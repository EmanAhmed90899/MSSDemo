package com.hemaya.mssdemo.model.synchronizeModel;

import com.google.gson.annotations.SerializedName;

public class Result{

	@SerializedName("serverTime")
	private int serverTime;

	public int getServerTime(){
		return serverTime;
	}
}