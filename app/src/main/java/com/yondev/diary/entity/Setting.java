package com.yondev.diary.entity;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.text.TextUtils;

public class Setting {
	
	private String code;
	private String name;
	private String value;
	private byte[] valueBlob;
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	public  List<String> getList() {
		List<String> res = new ArrayList<String>();
		if(this.value != null)
			res =  new ArrayList<String>(Arrays.asList(this.value.split("\\|")));
		return res;
		
	}
	public void setList(List<String> list) {
		this.value = TextUtils.join("|",list);
	}

	public byte[] getValueBlob() {
		return valueBlob;
	}

	public void setValueBlob(byte[] valueBlob) {
		this.valueBlob = valueBlob;
	}
}
