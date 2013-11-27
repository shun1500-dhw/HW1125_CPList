package com.example.hw1125_cplist;

import android.net.Uri;

public class ArticleDTO {
	private Uri pictureImageURI;
	private String nameTxt;
	private String telNumberTxt;
	private String playSEURI;

	public ArticleDTO() {
		super();
		this.pictureImageURI = null;
		this.nameTxt = "";
		this.telNumberTxt = "";
		this.playSEURI = "";
	}

	public Uri getPictureImageURI() {
		return pictureImageURI;
	}

	public void setPictureImageURI(Uri pictureImageURI) {
		this.pictureImageURI = pictureImageURI;
	}

	public String getNameTxt() {
		return nameTxt;
	}

	public void setNameTxt(String nameTxt) {
		this.nameTxt = nameTxt;
	}

	public String getTelNumberTxt() {
		return telNumberTxt;
	}

	public void setTelNumberTxt(String telNumberTxt) {
		this.telNumberTxt = telNumberTxt;
	}

	public String getPlaySEURI() {
		return playSEURI;
	}

	public void setPlaySEURI(String playSEURI) {
		this.playSEURI = playSEURI;
	}

}
