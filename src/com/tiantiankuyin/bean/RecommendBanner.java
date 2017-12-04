package com.tiantiankuyin.bean;

import java.io.Serializable;
import java.util.List;

public class RecommendBanner implements Serializable{

	private static final long serialVersionUID = 6564692556708959344L;

	private RecommendBanner() {
	}

	private int count; // 条数
	private List<ServerBanner> adList;

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public List<ServerBanner> getAdList() {
		return adList;
	}

	public void setAdList(List<ServerBanner> adList) {
		this.adList = adList;
	}

	public static class ServerBanner implements Serializable {

		private static final long serialVersionUID = 1L;

		public ServerBanner() {
		}

		/*
		 * 该数据为服务器端过来，请不要更改字段名
		 */
		private String img; // 图片url
		private String intent; // 插件意图，以类名结尾
		private int orderNum; // 排序序号
		private int tyId; // 如果是精选集，则对应精选集id供跳转精选集子页
		private String url; // 如果是广告，则对应广告链接
		private int type; // 类型 3为精选集，4为广告，5为插件

		public String getImg() {
			return img;
		}

		public void setImg(String img) {
			this.img = img;
		}

		public String getIntent() {
			return intent;
		}

		public void setIntent(String intent) {
			this.intent = intent;
		}

		public int getOrderNum() {
			return orderNum;
		}

		public void setOrderNum(int orderNum) {
			this.orderNum = orderNum;
		}

		public int getTyId() {
			return tyId;
		}

		public void setTyId(int tyId) {
			this.tyId = tyId;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}
	}
}
