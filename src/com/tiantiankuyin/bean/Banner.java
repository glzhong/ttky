package com.tiantiankuyin.bean;

import java.io.Serializable;

import android.graphics.drawable.Drawable;

/**
 * Banner Bean
 * 
 * @author DK
 * 
 */
public class Banner implements Serializable, Comparable<Banner> {

	private static final long serialVersionUID = 2955704097391335349L;

	// 顺序
	private int order;
	// Banner服务器资源ID
	private long bannerID;
	// Banner图片地址
	private String imageURL;
	// Banner图片
	private Drawable bannerImage;
	// Banner类型
	private BannerType bannerType;
	// Banner资源，如果是精选集则对应精选集ID,如果是广告则对应广告URL,
	// 如果是插件则对应插件接口
	private String resource;
	// 是否正在加载图片
	private boolean isLoadingImg;
	private Object tag;

	/**
	 * 获取Banner图片
	 * 
	 * @return
	 */
	public Drawable getBannerImage() {
		return bannerImage;
	}

	/**
	 * 设置Banner图片
	 * 
	 * @param bannerImage
	 */
	public void setBannerImage(Drawable bannerImage) {
		this.bannerImage = bannerImage;
	}

	/**
	 * 获取Banner服务器资源ID
	 * 
	 * @return
	 */
	public long getBannerID() {
		return bannerID;
	}

	/**
	 * 设置Banner服务器资源ID
	 * 
	 * @param bannerID
	 */
	public void setBannerID(long bannerID) {
		this.bannerID = bannerID;
	}

	/**
	 * 获取Banner图片地址
	 * 
	 * @return
	 */
	public String getImageURL() {
		return imageURL;
	}

	/**
	 * 设置Banner图片地址
	 * 
	 * @param imageURL
	 */
	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	/**
	 * 获取Banner类型
	 * 
	 * @return
	 */
	public BannerType getBannerType() {
		return bannerType;
	}

	/**
	 * 设置Banner类型
	 * 
	 * @param bannerType
	 */
	public void setBannerType(BannerType bannerType) {
		this.bannerType = bannerType;
	}

	/**
	 * 获取Banner资源信息
	 * 
	 * @return
	 */
	public String getResource() {
		return resource;
	}

	/**
	 * 设置Banner资源信息
	 * 
	 * @param resource
	 */
	public void setResource(String resource) {
		this.resource = resource;
	}

	/**
	 * 是否正在加载图片
	 * 
	 * @return
	 */
	public boolean isLoadingImg() {
		return isLoadingImg;
	}

	/**
	 * 是否正在加载图片
	 * 
	 * @param isLoadingImg
	 */
	public void setLoadingImg(boolean isLoadingImg) {
		this.isLoadingImg = isLoadingImg;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public Object getTag() {
		return tag;
	}

	public void setTag(Object tag) {
		this.tag = tag;
	}

	/**
	 * Banner类型
	 * 
	 * @author DK
	 * 
	 */
	public enum BannerType {

		/** 精选集 */
		Omnibus,
		/** 广告 */
		POSTER,
		/** 插件 */
		PLUGIN

	}

	@Override
	public int compareTo(Banner another) {
		return this.getOrder() - another.getOrder();
	}
}