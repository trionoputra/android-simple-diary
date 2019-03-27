package com.yondev.diary.entity;

import java.util.ArrayList;
import java.util.Date;

public class Diary {
	private Integer code;
	private String title;
	private String feel;
	private Date date;
	private ArrayList<DetailDiary> detail;
	
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getFeel() {
		return feel;
	}
	public void setFeel(String feel) {
		this.feel = feel;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public ArrayList<DetailDiary> getDetail() {
		return detail;
	}
	public void setDetail(ArrayList<DetailDiary> detail) {
		this.detail = detail;
	}
	
}
