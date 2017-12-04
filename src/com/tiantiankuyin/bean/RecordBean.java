package com.tiantiankuyin.bean;

import java.io.Serializable;

public class RecordBean implements Serializable{
	private static final long serialVersionUID = 3755416275380165026L;
	public int id;
	public String name;
	public String imgurl;
	public int count;
	public RecordBean(int id,String name,String imgurl,int count){
		this.id=id;
		this.name=name;
		this.imgurl=imgurl;
		this.count=count;
	}
}
