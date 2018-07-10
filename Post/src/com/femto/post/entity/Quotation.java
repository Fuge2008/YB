package com.femto.post.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class Quotation implements Serializable {
	
	int id;
	String wenjiaoId,name,curPrice,currentGains,sumNum, sumMoney;
	
	
	public String getCurrentGains() {
		return currentGains;
	}

	public void setCurrentGains(String currentGains) {
		this.currentGains = currentGains;
	}

	public Quotation(int id,String wenjiaoId, String name, String curPrice,
			String currentGains, String sumNum, String sumMoney) {
		super();
		this.id = id;
		this.wenjiaoId = wenjiaoId;
		this.name = name;
		this.curPrice = curPrice;
		this.currentGains = currentGains;
		this.sumNum = sumNum;
		this.sumMoney = sumMoney;
	}


	public Quotation() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCurPrice() {
		return curPrice;
	}

	public void setCurPrice(String curPrice) {
		this.curPrice = curPrice;
	}

	public String getSumNum() {
		return sumNum;
	}

	public void setSumNum(String sumNum) {
		this.sumNum = sumNum;
	}

	public String getSumMoney() {
		return sumMoney;
	}

	public void setSumMoney(String sumMoney) {
		this.sumMoney = sumMoney;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getWenjiaoId() {
		return wenjiaoId;
	}

	public void setWenjiaoId(String wenjiaoId) {
		this.wenjiaoId = wenjiaoId;
	}
	
}
