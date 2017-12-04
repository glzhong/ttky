package com.tiantiankuyin.bean;

/**
 * 下载地址item
 * 
 * @author Perry
 */
public class OlDownloadItem {
	/** 歌曲组id */
	private int id;
	/** 歌曲的下载地址 */
	private String url;

	public OlDownloadItem() {
	};
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}