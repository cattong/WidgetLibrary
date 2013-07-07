package com.shejiaomao.core.entity;

public class CellTower extends BaseEntity {
	private static final long serialVersionUID = -5001779145936286942L;

	private String cellId; //基站编号
	
	private String lac; //locationAreaCode;位置区域码
	
	private String mcc; //mobile_country_code;移动国家代码（中国的为460）
	
	private String mnc; //mobile_network_code;移动网络号码（中国移动为0，中国联通为1，中国电信为2）；
	
	private Integer bsss; //Base station signal strength，基站信号强度。
	
	public String getCellId() {
		return cellId;
	}

	public void setCellId(String cellId) {
		this.cellId = cellId;
	}

	public String getLac() {
		return lac;
	}

	public void setLac(String lac) {
		this.lac = lac;
	}

	public String getMcc() {
		return mcc;
	}

	public void setMcc(String mcc) {
		this.mcc = mcc;
	}

	public String getMnc() {
		return mnc;
	}

	public void setMnc(String mnc) {
		this.mnc = mnc;
	}


	public Integer getBsss() {
		return bsss;
	}

	public void setBsss(Integer bsss) {
		this.bsss = bsss;
	}

	@Override
	public boolean equals(Object o) {
		return false;
	}

	@Override
	public int hashCode() {
		return 0;
	}

}
