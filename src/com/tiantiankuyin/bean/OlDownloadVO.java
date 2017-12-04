package com.tiantiankuyin.bean;

import java.util.List;

/**
 * 下载地址列表
 * 
 * @author Perry
 */
public class OlDownloadVO {

	/** 下载地址集合 */
	private List<OlDownloadItem> dataList;

	public OlDownloadVO() {
	}
	
	public List<OlDownloadItem> getDataList() {
		return dataList;
	}

	public void setDataList(List<OlDownloadItem> dataList) {
		this.dataList = dataList;
	}

}
