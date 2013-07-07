package com.shejiaomao.core.entity;

public class WifiTower extends BaseEntity {
	private static final long serialVersionUID = 5960298697344430821L;

	private String ssid; //
	
	private String password;
	
	private String macAddress;
	
	private Integer rssi; // 获取RSSI，RSSI就是接受信号强度指示
	
	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public Integer getRssi() {
		return rssi;
	}

	public void setRssi(Integer rssi) {
		this.rssi = rssi;
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
