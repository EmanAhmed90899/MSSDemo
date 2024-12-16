package com.hemaya.mssdemo.model.activationData.Request;

import com.google.gson.annotations.SerializedName;

public class ActivationDataRequest{

	@SerializedName("registrationIdentifier")
	private String registrationIdentifier;

	@SerializedName("clientEvidenceMessage")
	private String clientEvidenceMessage;

	public ActivationDataRequest(String registrationIdentifier, String clientEvidenceMessage) {
		this.registrationIdentifier = registrationIdentifier;
		this.clientEvidenceMessage = clientEvidenceMessage;
	}

	public String getRegistrationIdentifier(){
		return registrationIdentifier;
	}

	public String getClientEvidenceMessage(){
		return clientEvidenceMessage;
	}
}