package com.tiantiankuyin.bean;

import java.io.Serializable;

public class RelateInfo implements Serializable{

	private static final long serialVersionUID = -3515369116665067404L;

	/** 自增长id */
	private long id;

	/** 关联歌单的ID */
	private long listID;

	/** 关联歌曲的ID */
	private long songID;

	/** 歌曲关联的时间 */
	private long dataAdded;

	public RelateInfo(long id, long listID, long songID, long dataAdded) {
		super();
		this.id = id;
		this.listID = listID;
		this.songID = songID;
		this.dataAdded = dataAdded;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getListID() {
		return listID;
	}

	public void setListID(long listID) {
		this.listID = listID;
	}

	public long getSongID() {
		return songID;
	}

	public void setSongID(long songID) {
		this.songID = songID;
	}

	public long getDataAdded() {
		return dataAdded;
	}

	public void setDataAdded(long dataAdded) {
		this.dataAdded = dataAdded;
	}

}
