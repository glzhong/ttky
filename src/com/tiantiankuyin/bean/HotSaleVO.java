package com.tiantiankuyin.bean;

import java.io.Serializable;
import java.util.List;

public class HotSaleVO implements Serializable{
	private static final long serialVersionUID = 3114297672771606164L;
	private int count;
	private List<HotSaleItem> adList;
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public List<HotSaleItem> getAdList() {
		return adList;
	}
	public void setAdList(List<HotSaleItem> adList) {
		this.adList = adList;
	}
}
