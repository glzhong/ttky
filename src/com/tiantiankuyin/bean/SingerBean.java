package com.tiantiankuyin.bean;

import java.io.Serializable;

/** 歌手对象Bean
 * @author Erica
 *  */
public class SingerBean implements Serializable{
	private static final long serialVersionUID = -5313484791171007369L;
	/** 歌手名 */
	private String author;
	/** 热度 */
	private String downloadNum;
	/** 头像  */
	private String imgUrl;
	
	public SingerBean(){}
	
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getDownloadNum() {
		return downloadNum;
	}
	public void setDownloadNum(String downloadNum) {
		this.downloadNum = downloadNum;
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
			SingerBean another = (SingerBean) o;
			if (another != null && another.getAuthor() != null
					&& this.getAuthor() != null && this.getAuthor().equals(another.getAuthor()))
				return true;
		}
		return false;
	}
	
}
