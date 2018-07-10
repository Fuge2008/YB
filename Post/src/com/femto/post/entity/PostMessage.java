package com.femto.post.entity;

import java.io.Serializable;

@SuppressWarnings("serial")
public class PostMessage implements Serializable {
	String fullname, currentGains, code,curPrice;
	int dingpanId;
	
	
	public PostMessage() {
		super();
	}


	public PostMessage(String fullname, String currentGains, String curPrice,
			String code,int dingpanId) {
		super();
		this.fullname = fullname;
		this.currentGains = currentGains;
		this.curPrice = curPrice;
		this.code = code;
		this.dingpanId = dingpanId;
	}

	public int getDingpanId() {
		return dingpanId;
	}
	public String getFullname() {
		return fullname;
	}


	public void setFullname(String fullname) {
		this.fullname = fullname;
	}


	public String getCurrentGains() {
		return currentGains;
	}


	public void setCurrentGains(String currentGains) {
		this.currentGains = currentGains;
	}


	public String getCurPrice() {
		return curPrice;
	}


	public void setCurPrice(String curPrice) {
		this.curPrice = curPrice;
	}


	public String getCode() {
		return code;
	}


	public void setCode(String code) {
		this.code = code;
	}
	
	
}
