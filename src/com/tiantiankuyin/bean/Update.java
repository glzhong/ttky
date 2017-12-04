package com.tiantiankuyin.bean;

import java.io.Serializable;

/**
 * 升级
 * @author DK
 *
 */
public class Update implements Serializable {

	private static final long serialVersionUID = 1L;
	private String version;	//版本号
	private String url;	//升级地址
	private String describe;	//新版本描述

	private boolean showMsgAgain = false;//是否强制提醒信息。
	
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public boolean isShowMsgAgain() {
		return showMsgAgain;
	}

	public void setShowMsgAgain(boolean showMsgAgain) {
		this.showMsgAgain = showMsgAgain;
	}
}
