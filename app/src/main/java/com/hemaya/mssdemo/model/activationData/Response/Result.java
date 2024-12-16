package com.hemaya.mssdemo.model.activationData.Response;

import com.google.gson.annotations.SerializedName;

public class Result{

	@SerializedName("encryptedLicenseActivationMessage")
	private String encryptedLicenseActivationMessage;

	@SerializedName("encryptedCounter")
	private String encryptedCounter;

	@SerializedName("serverEvidenceMessage")
	private String serverEvidenceMessage;
	@SerializedName("mac")
	private String mac;
	public String getEncryptedLicenseActivationMessage(){
		return encryptedLicenseActivationMessage;
	}

	public String getEncryptedCounter(){
		return encryptedCounter;
	}

	public String getServerEvidenceMessage(){
		return serverEvidenceMessage;
	}
	public String getMac(){
		return mac;
	}

	@Override
	public String toString() {
		return "Result{" +
				"encryptedLicenseActivationMessage='" + encryptedLicenseActivationMessage + '\'' +
				", encryptedCounter='" + encryptedCounter + '\'' +
				", serverEvidenceMessage='" + serverEvidenceMessage + '\'' +
				", mac='" + mac + '\'' +
				'}';
	}
}