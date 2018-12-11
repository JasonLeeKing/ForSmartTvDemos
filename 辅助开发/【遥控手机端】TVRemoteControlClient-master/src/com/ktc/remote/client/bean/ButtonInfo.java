package com.ktc.remote.client.bean;

public class ButtonInfo {
	
    public static final int BTN_TYPE_NORMAL = 100101;
    public static final int BTN_TYPE_AROOW = 100102;
    public static final int BTN_TYPE_TITLE = 100103;
    
	private int btnid = -1;
	private int btntype = BTN_TYPE_NORMAL;
	private String btnname = "";

	public int getBtnId() {
		return btnid;
	}
	public void setBtnId(int btnid) {
		this.btnid = btnid;
	}

	public int getBtnType() {
		return btntype;
	}
	public void setBtnType(int btntype) {
		this.btntype = btntype;
	}

	public String getBtnName() {
		return btnname;
	}
	public void setBtnName(String btnname) {
		this.btnname = btnname;
	}
}