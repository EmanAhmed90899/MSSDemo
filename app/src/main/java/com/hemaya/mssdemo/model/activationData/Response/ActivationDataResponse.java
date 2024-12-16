package com.hemaya.mssdemo.model.activationData.Response;

import com.google.gson.annotations.SerializedName;

public class ActivationDataResponse{

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


	@Override
	public String toString() {
		return "ActivationDataResponse{" +
				"result=" + result +
				", resultCodes=" + resultCodes +
				'}';
	}
}