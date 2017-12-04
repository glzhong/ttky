package com.tiantiankuyin.bean;

import java.io.Serializable;
import java.util.List;

public class CategoryVO implements Serializable{
	private static final long serialVersionUID = -4597827111365163627L;
	private String desc;
	private List<CategoryItem> dataList;
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public List<CategoryItem> getDataList() {
		return dataList;
	}
	public void setDataList(List<CategoryItem> dataList) {
		this.dataList = dataList;
	}
}
