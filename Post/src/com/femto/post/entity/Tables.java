package com.femto.post.entity;

import java.io.Serializable;

public class Tables implements Serializable {
	
	 /*"wenJiaoId": "1",
     "id": 1,
     "dingPanId": 0,
     "currentGains": "2.5%",
     "code": "100001",
     "fullname": "综合指数",
     "curPrice": "2172.6"*/
	
	String code, fullname, cd,wenJiaoId,curPrice;
	int  dingPanId;
	public String getWenJiaoId() {
		return wenJiaoId;
	}
	public void setWenJiaoId(String wenJiaoId) {
		this.wenJiaoId = wenJiaoId;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	int id;
	Long num;
	String YesterBalancePrice, OpenPrice, currentGains, HighPrice,
			LowPrice, TotalAmount, TotalMoney;
	//Michael
	boolean isLooked = false;

	
	public Tables(int id,String wenJiaoId,int dingPanId,String code,String fullname,String curPrice,String currentGains) {
		this.id = id;
		this.dingPanId = dingPanId;
		this.wenJiaoId = wenJiaoId;
		this.fullname = fullname;
		this.code = code;
		this.curPrice = curPrice;
		this.currentGains = currentGains;
	}
	/*public Tables(String code, String fullname, Long num, String totalAmount,
			String totalMoney, String yesterBalancePrice, String openPrice,
			String curPrice, String currentGains, String highPrice,
			String lowPrice, String cd) {
		super();
		this.code = code;
		this.fullname = fullname;
		this.num = num;
		TotalAmount = totalAmount;
		TotalMoney = totalMoney;
		YesterBalancePrice = yesterBalancePrice;
		OpenPrice = openPrice;
		CurPrice = curPrice;
		CurrentGains = currentGains;
		HighPrice = highPrice;
		LowPrice = lowPrice;
		this.cd = cd;
	}*/
	public void setDingpanId(int dingpanId) {
		this.dingPanId = dingpanId;
	}
	public int getDingpanId() {
		return dingPanId;
	}
	public void setLooked(boolean a) {
		this.isLooked = a;
	}
	public boolean getLooked() {
		return isLooked;
	}
	
	public String getCd() {
		return cd;
	}

	public void setCd(String cd) {
		this.cd = cd;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public Long getNum() {
		return num;
	}

	public void setNum(Long num) {
		this.num = num;
	}

	public String getTotalAmount() {
		return TotalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		TotalAmount = totalAmount;
	}

	public String getTotalMoney() {
		return TotalMoney;
	}

	public void setTotalMoney(String totalMoney) {
		TotalMoney = totalMoney;
	}

	public String getYesterBalancePrice() {
		return YesterBalancePrice;
	}

	public void setYesterBalancePrice(String yesterBalancePrice) {
		YesterBalancePrice = yesterBalancePrice;
	}

	public String getOpenPrice() {
		return OpenPrice;
	}

	public void setOpenPrice(String openPrice) {
		OpenPrice = openPrice;
	}

	public String getCurPrice() {
		return curPrice;
	}

	public void setCurPrice(String curPrice) {
		this.curPrice = curPrice;
	}

	public String getCurrentGains() {
		return currentGains;
	}

	public void setCurrentGains(String currentGains) {
		this.currentGains = currentGains;
	}

	public String getHighPrice() {
		return HighPrice;
	}

	public void setHighPrice(String highPrice) {
		HighPrice = highPrice;
	}

	public String getLowPrice() {
		return LowPrice;
	}

	public void setLowPrice(String lowPrice) {
		LowPrice = lowPrice;
	}

}
