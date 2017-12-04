package com.tiantiankuyin.bean;

import java.io.Serializable;

/**
 * 歌手 歌曲数据库模型类
 * 
 * @author Perry
 * 
 */
public class ArtistVO implements Comparable<ArtistVO>, Serializable {

	private static final long serialVersionUID = 3784688361624027925L;

	/** 歌手名字 */
	private long artistID;

	/** 歌手名字 */
	private String artistName;

	/** 对应歌手的歌曲数量 */
	private int numOfSong;

	/** 保存歌手首字母 */
	private String firstLetter;

	public ArtistVO(long artistID, String artistName, int numOfSong,
			String firstLetter) {
		super();
		this.artistID = artistID;
		this.artistName = artistName;
		this.numOfSong = numOfSong;
		this.firstLetter = firstLetter;
	}

	public String getArtistName() {
		return artistName;
	}

	public void setArtistName(String artistName) {
		this.artistName = artistName;
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

	public long getArtistID() {
		return artistID;
	}

	public void setArtistID(long artistID) {
		this.artistID = artistID;
	}

	@Override
	public int compareTo(ArtistVO another) {
		String firstLetter = this.getFirstLetter();
		String anotherFirstLetter = another.getFirstLetter();
		if ((firstLetter != null && "#".equals(firstLetter))
				&& (anotherFirstLetter != null && "#"
						.equals(anotherFirstLetter))) {
			return another.getNumOfSong() - this.getNumOfSong();
		}
		else if ((firstLetter != null && "#".equals(firstLetter))
				&& !(anotherFirstLetter != null && "#"
						.equals(anotherFirstLetter)))
			return 1;
		else if (!(firstLetter != null && "#".equals(firstLetter))
				&& (anotherFirstLetter != null && "#"
				.equals(anotherFirstLetter)))
			return -1;
		else if(firstLetter.charAt(0) - anotherFirstLetter.charAt(0) == 0){
			return another.getNumOfSong() - this.getNumOfSong();
		}else
			return firstLetter.charAt(0) - anotherFirstLetter.charAt(0);
	}
}
