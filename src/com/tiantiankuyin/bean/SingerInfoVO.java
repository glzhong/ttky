package com.tiantiankuyin.bean;

import java.io.Serializable;

public class SingerInfoVO implements Serializable{
	private static final long serialVersionUID = 537573802302167486L;
	/** 男 女 组合 ... */
	private String type;
	/** 地区 */
	private String area;
	/** 歌手名 */
	private String singer;
	/** 首字母 */
	private String pingyin;
	/** 译名 */
	private String tranName;
	/** 国籍 */
	private String country;
	/** 生日 */
	private String birthday;
	/** 出生地 */
	private String birthArea;
	/** 血型 */
	private String bloodType;
	/** 星座 */
	private String constellation;
	/** 身高 */
	private String stature;
	/** 教育程度 */
	private String education;
	/** 风格/流派 */
	private String genre;
	/** 简介 */
	private String intro;
	/** 图片地址 */
	private String imgUrl;

	public SingerInfoVO() {

	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getSinger() {
		return singer;
	}

	public void setSinger(String singer) {
		this.singer = singer;
	}

	public String getPingyin() {
		return pingyin;
	}

	public void setPingyin(String pingyin) {
		this.pingyin = pingyin;
	}

	public String getTranName() {
		return tranName;
	}

	public void setTranName(String tranName) {
		this.tranName = tranName;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getBirthArea() {
		return birthArea;
	}

	public void setBirthArea(String birthArea) {
		this.birthArea = birthArea;
	}

	public String getBloodType() {
		return bloodType;
	}

	public void setBloodType(String bloodType) {
		this.bloodType = bloodType;
	}

	public String getConstellation() {
		return constellation;
	}

	public void setConstellation(String constellation) {
		this.constellation = constellation;
	}

	public String getStature() {
		return stature;
	}

	public void setStature(String stature) {
		this.stature = stature;
	}

	public String getEducation() {
		return education;
	}

	public void setEducation(String education) {
		this.education = education;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o != null) {
			SingerInfoVO another = (SingerInfoVO) o;
			if (another != null && another.getSinger() != null
					&& this.getSinger() != null && this.getSinger().equals(another.getSinger()))
				return true;
		}
		return false;
	}

}
