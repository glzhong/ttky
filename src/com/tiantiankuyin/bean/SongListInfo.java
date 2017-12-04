package com.tiantiankuyin.bean;

import java.io.Serializable;

/**
 * 歌单的数据库模型类
 * 
 * @author Perry
 * 
 */
public class SongListInfo implements Serializable{

	private static final long serialVersionUID = 1195159111797364324L;

	/** 自增长id */
	private long id;

	/** 歌单名 */
	private String name;

	/** 歌单创建日期 */
	private long dataCreated;

	public SongListInfo(long id, String name, long dataCreated) {
		super();
		this.id = id;
		this.name = name;
		this.dataCreated = dataCreated;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getDataCreated() {
		return dataCreated;
	}

	public void setDataCreated(long dataCreated) {
		this.dataCreated = dataCreated;
	}

}
