package com.hemaya.mssdemo.model.mdlAddDevice.Response;

import com.google.gson.annotations.SerializedName;

public class MdlAddDeviceResponse{

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