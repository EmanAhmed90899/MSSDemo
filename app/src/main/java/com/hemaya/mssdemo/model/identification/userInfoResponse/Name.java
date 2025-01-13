package com.hemaya.mssdemo.model.identification.userInfoResponse;

import com.google.gson.annotations.SerializedName;

public class Name{

	@SerializedName("ar")
	private String ar;

	@SerializedName("en")
	private String en;

	public String getAr(){
		return ar;
	}

	public String getEn(){
		return en;
	}
}