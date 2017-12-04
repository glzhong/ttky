package com.tiantiankuyin.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 在线音乐，精选集item
 * @author Erica
 *
 */
public class OlAlbumList implements Serializable {
	private static final long serialVersionUID = 51642208351390159L;
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
	/** 精选集创建者 */
	private String author;
	/** 精选集音乐列表  */
	private List<OlSongVO> dataList;
	
	public OlAlbumList(){
		
	}

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

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public List<OlSongVO> getDataList() {
		return dataList;
	}

	public void setDataList(List<OlSongVO> dataList) {
		this.dataList = dataList;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o != null) {
			OlAlbumList another = (OlAlbumList) o;
			if (another != null && another.getId()>0
					&& this.getId()>0 && this.getId() == another.getId()&&another.getDataList().size()>0
					&&this.getDataList().size()>0&&another.getDataList().get(0).equals(this.getDataList().get(0)))
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
