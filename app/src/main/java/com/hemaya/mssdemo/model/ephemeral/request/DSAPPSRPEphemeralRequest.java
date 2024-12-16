package com.hemaya.mssdemo.model.ephemeral.request;

import com.google.gson.annotations.SerializedName;

public class DSAPPSRPEphemeralRequest{

	@SerializedName("registrationIdentifier")
	private String registrationIdentifier;

	@SerializedName("clientEphemeralPublicKey")
	private String clientEphemeralPublicKey;

	public DSAPPSRPEphemeralRequest(String registrationIdentifier, String clientEphemeralPublicKey) {
		this.registrationIdentifier = registrationIdentifier;
		this.clientEphemeralPublicKey = clientEphemeralPublicKey;
	}

	public String getRegistrationIdentifier(){
		return registrationIdentifier;
	}

	public String getClientEphemeralPublicKey(){
		return clientEphemeralPublicKey;
	}
}