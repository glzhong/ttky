package com.tiantiankuyin.bean;

/**
 * 从网络获取歌词列表的歌词Bean
 * 用于Gson解析
 * @author Perry
 */
public class OlLrcItem {
	public String lrcurl;
	public String singer;
	public String song;

	public OlLrcItem() {
	}

	public String getLrcurl() {
		return lrcurl;
	}

	public void setLrcurl(String lrcurl) {
		this.lrcurl = lrcurl;
	}

	public String getSinger() {
		return singer;
	}

	public void setSinger(String singer) {
		this.singer = singer;
	}

	public String getSong() {
		return song;
	}

	public void setSong(String song) {
		this.song = song;
	}
}
