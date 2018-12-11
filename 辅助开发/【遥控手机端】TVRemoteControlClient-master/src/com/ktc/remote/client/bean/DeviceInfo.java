package com.ktc.remote.client.bean;

public class DeviceInfo {

	private String devicename;
	private String deviceip;
	private String deviceid;

	public String getDeviceid() {
		return deviceid;
	}

	public void setDeviceid(String deviceid) {
		this.deviceid = deviceid;
	}

	public String getDevicename() {
		return devicename;
	}

	public void setDevicename(String devicename) {
		this.devicename = devicename;
	}

	public String getDeviceip() {
		return deviceip;
	}

	public void setDeviceip(String deviceip) {
		this.deviceip = deviceip;
	}

	@Override
	public String toString() {
		return "deviceIP:" + deviceip + "  deviceID:" + deviceid
				+ "  deviceName:" + devicename;
	}
}
