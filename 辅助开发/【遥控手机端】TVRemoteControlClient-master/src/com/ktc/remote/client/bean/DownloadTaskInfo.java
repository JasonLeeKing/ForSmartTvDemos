package com.ktc.remote.client.bean;

public class DownloadTaskInfo {
	private int taskid;
	private String appid;
	private String icon;
	private String appname;
	private String pkgname;
	private String devid;
	private String devname;
	private int status;

	public int getTaskid() {
		return taskid;
	}

	public void setTaskid(int taskid) {
		this.taskid = taskid;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getAppname() {
		return appname;
	}

	public void setAppname(String appname) {
		this.appname = appname;
	}

	public String getPkgname() {
		return pkgname;
	}

	public void setPkgname(String pkgname) {
		this.pkgname = pkgname;
	}

	public String getDevid() {
		return devid;
	}

	public void setDevid(String devid) {
		this.devid = devid;
	}

	public String getDevname() {
		return devname;
	}

	public void setDevname(String devname) {
		this.devname = devname;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}