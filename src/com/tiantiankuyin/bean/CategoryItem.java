package com.tiantiankuyin.bean;

import java.io.Serializable;

public class CategoryItem implements Serializable{

	private static final long serialVersionUID = 8546614115392243047L;
	private int id;
	private String imgUrl;
	private String type;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	@Override
	public boolean equals(Object o) {
		CategoryItem item=(CategoryItem)o;
		if(id==item.id){
			return true;
		}
		return false;
	}
	@Override
	public int hashCode() {
		if(id>0){
			return this.id;
		}
		return 0;
	}
}
