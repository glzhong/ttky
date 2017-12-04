package com.tiantiankuyin.bean;

import java.io.Serializable;

public class HotSaleItem implements Serializable{

	private static final long serialVersionUID = -6166643781036086956L;
	private int id;
	private String imgUrl;
	private String intor;
	private String name;
	private int status;
	private int theOrder;
	private int type;
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
	public String getIntor() {
		return intor;
	}
	public void setIntor(String intor) {
		this.intor = intor;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getTheOrder() {
		return theOrder;
	}
	public void setTheOrder(int theOrder) {
		this.theOrder = theOrder;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
}
