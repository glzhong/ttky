package com.tiantiankuyin.bean;

import java.io.Serializable;

/**
 * 专辑的数据库模型类
 * 
 * @author Perry
 * 
 */
public class AlbumVO implements Comparable<AlbumVO>, Serializable {

	private static final long serialVersionUID = 5274685827182081L;
	/** 专辑ID */
	private long albumID;
	/** 专辑名称 */
	private String albumName;
	/** 专辑对应的歌曲数量 */
	private int numOfSong;
	/** 保存专辑拼音首字母 */
	private String firstLetter;

	public AlbumVO(long albumID, String albumName, int numOfSong,
			String firstLetter) {
		super();
		this.albumID = albumID;
		this.albumName = albumName;
		this.numOfSong = numOfSong;
		this.firstLetter = firstLetter;
	}

	public String getAlbumName() {
		return albumName;
	}

	public void setAlbumName(String albumName) {
		this.albumName = albumName;
	}

	public int getNumOfSong() {
		return numOfSong;
	}

	public void setNumOfSong(int numOfSong) {
		this.numOfSong = numOfSong;
	}

	public String getFirstLetter() {
		return firstLetter;
	}

	public void setFirstLetter(String firstLetter) {
		this.firstLetter = firstLetter;
	}

	public long getAlbumID() {
		return albumID;
	}

	public void setAlbumID(long albumID) {
		this.albumID = albumID;
	}

	@Override
	public int compareTo(AlbumVO another) {
		String firstLetter = this.getFirstLetter();
		String anotherFirstLetter = another.getFirstLetter();
		if ((firstLetter != null && "#".equals(firstLetter))
				&& (anotherFirstLetter != null && "#"
						.equals(anotherFirstLetter)))
			return another.getNumOfSong() - this.getNumOfSong();
		else if ((firstLetter != null && "#".equals(firstLetter))
				&& !(anotherFirstLetter != null && "#"
						.equals(anotherFirstLetter)))
			return 1;
		else if (!(firstLetter != null && "#".equals(firstLetter))
				&& (anotherFirstLetter != null && "#"
						.equals(anotherFirstLetter)))
			return -1;
		else if (firstLetter.charAt(0) == anotherFirstLetter.charAt(0)) {
			return another.getNumOfSong() - this.getNumOfSong();
		}
		return firstLetter.charAt(0) - anotherFirstLetter.charAt(0);
	}
}
