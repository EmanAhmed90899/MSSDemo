package com.hemaya.mssdemo.model.error.validationError;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Errors{

	@SerializedName("national_id")
	private List<String> nationalId;

	public List<String> getNationalId(){
		return nationalId;
	}
}