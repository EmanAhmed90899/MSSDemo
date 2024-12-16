package com.hemaya.mssdemo.model.mdlActivate.Response;

import com.google.gson.annotations.SerializedName;

public class MdlActivateResponse{

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
		return "MdlActivateResponse{" +
				"result=" + result +
				", resultCodes=" + resultCodes +
				'}';
	}
}