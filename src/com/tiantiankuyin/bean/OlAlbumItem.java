package com.tiantiankuyin.bean;

import java.io.Serializable;

/**
 * 在线音乐，精选集item
 * @author Erica
 *
 */
public class OlAlbumItem implements Serializable{
	private static final long serialVersionUID = -384519314879563714L;
	/** 精选集id */
	private int id;
	/** 精选集标题 */
	private String name;
	/** 精选集的描述内容 */
	private String intro;
	/** 精选集内的歌曲数量 */
	private long musicCount;
	/** 精选集对应的图片地址 */
	private String imgUrl;
	/** 精选集标签 */
	private String tag;
	/** 精选集状态 */
	private String status;
	/** 精选集排序序号  */
	private int theOrder;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIntro() {
		return intro;
	}
	public void setIntro(String intro) {
		this.intro = intro;
	}


	public long getMusicCount() {
		return musicCount;
	}
	public void setMusicCount(long musicCount) {
		this.musicCount = musicCount;
	}
	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}

	public int getTheOrder() {
		return theOrder;
	}
	public void setTheOrder(int theOrder) {
		this.theOrder = theOrder;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public boolean equals(Object o) {
		if (o != null) {
			OlAlbumItem another = (OlAlbumItem) o;
			if (another != null && another.getId() >0
					&& this.getId()>0 && this.getId()==another.getId())
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