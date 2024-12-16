package com.hemaya.mssdemo.model.ephemeral.response;

import com.google.gson.annotations.SerializedName;

public class Result{

	@SerializedName("salt")
	private String salt;

	@SerializedName("serverEphemeralPublicKey")
	private String serverEphemeralPublicKey;

	public String getSalt(){
		return salt;
	}

	public String getServerEphemeralPublicKey(){
		return serverEphemeralPublicKey;
	}

	@Override
	public String toString() {
		return "Result{" +
				"salt='" + salt + '\'' +
				", serverEphemeralPublicKey='" + serverEphemeralPublicKey + '\'' +
				'}';
	}
}