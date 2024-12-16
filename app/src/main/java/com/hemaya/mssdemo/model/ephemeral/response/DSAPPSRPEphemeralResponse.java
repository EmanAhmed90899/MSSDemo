package com.hemaya.mssdemo.model.ephemeral.response;

import com.google.gson.annotations.SerializedName;

public class DSAPPSRPEphemeralResponse{

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
		return "DSAPPSRPEphemeralResponse{" +
				"result=" + result +
				", resultCodes=" + resultCodes +
				'}';
	}
}