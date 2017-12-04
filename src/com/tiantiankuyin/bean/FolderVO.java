package com.tiantiankuyin.bean;

import java.io.Serializable;

/**
 * 文件夹数据库模型
 * 
 * @author Perry
 * 
 */
public class FolderVO implements Serializable{

	private static final long serialVersionUID = -8534509448848463228L;
	/*
	 * 例如:文件夹全路径为:/mnt/sdcard/mp3/music
	 * 文件夹名称为:music 
	 * 文件夹路径为:/mnt/sdcard/mp3
	 */

	/** 文件夹名称 */
	private String folderName;
	/** 文件夹路径 */
	private String folderPath;
	/** 文件夹全路径 */
	private String folderFullPath;
	/** 文件夹下的歌曲数量 */
	private Integer numOfSongs;

	public FolderVO(String folderName, String folderPath,
			String folderFullPath, Integer numOfSongs) {
		super();
		this.folderName = folderName;
		this.folderPath = folderPath;
		this.folderFullPath = folderFullPath;
		this.numOfSongs = numOfSongs;
	}

	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	public String getFolderPath() {
		return folderPath;
	}

	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}

	public String getFolderFullPath() {
		return folderFullPath;
	}

	public void setFolderFullPath(String folderFullPath) {
		this.folderFullPath = folderFullPath;
	}

	public Integer getNumOfSongs() {
		return numOfSongs;
	}

	public void setNumOfSongs(Integer numOfSongs) {
		this.numOfSongs = numOfSongs;
	}

}
