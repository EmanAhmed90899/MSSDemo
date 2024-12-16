package com.hemaya.mssdemo.model.mdlActivate.Request;

import com.google.gson.annotations.SerializedName;

public class MdlActivateRequest{

	@SerializedName("registrationIdentifier")
	private String registrationIdentifier;

	@SerializedName("signature")
	private String signature;

	public MdlActivateRequest(String registrationIdentifier, String signature) {
		this.registrationIdentifier = registrationIdentifier;
		this.signature = signature;
	}

	public String getRegistrationIdentifier(){
		return registrationIdentifier;
	}

	public String getSignature(){
		return signature;
	}
}