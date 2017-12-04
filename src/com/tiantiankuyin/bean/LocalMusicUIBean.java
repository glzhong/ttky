package com.tiantiankuyin.bean;

import java.io.Serializable;

public class LocalMusicUIBean implements Serializable{

	private static final long serialVersionUID = -1678478741979735474L;
	
	private String name;//本地文件名称
	private int resId;//本地文件图片ID
	private int count;//本地文件数量
	public LocalMusicUIBean(){}
	public LocalMusicUIBean(String name,int resId,int count){
		this.name=name;
		this.resId=resId;
		this.count=count;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getResId() {
		return resId;
	}
	public void setResId(int resId) {
		this.resId = resId;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
}
