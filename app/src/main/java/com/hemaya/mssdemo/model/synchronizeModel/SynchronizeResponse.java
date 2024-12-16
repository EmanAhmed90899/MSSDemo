package com.hemaya.mssdemo.model.synchronizeModel;

import com.google.gson.annotations.SerializedName;

public class SynchronizeResponse{

	@SerializedName("result")
	private Result result;

	@SerializedName("resultCodes")
	private ResultCodes resultCodes;

	public Result getResult(){
		return result;
	}

	public ResultCodes getResultCodes(){
		return resultCodes;
	}
}