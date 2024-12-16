package com.hemaya.mssdemo.model.identification;


public class StepperModel{

	private String title;

	private String content;

	public StepperModel(String title, String content){
		this.title = title;
		this.content = content;
	}
	public String getTitle(){
		return title;
	}

	public String getContent(){
		return content;
	}
}