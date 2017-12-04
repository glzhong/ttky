package com.tiantiankuyin.bean;

import java.io.Serializable;

public class DownloadBean implements Serializable{
	  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	  String  fileID;
	  String  gid;
	  String songName;
	  String  cp;
	  String  A;
	  String singerName;
	  String file;
	  
	/**
	 * 
	 */
	public DownloadBean() {
		super();
	}
	public DownloadBean(String fileID, String gid, String songName, String cp,
			String a, String singerName,String file) {
		super();
		this.fileID = fileID;
		this.gid = gid;
		this.songName = songName;
		this.cp = cp;
		A = a;
		this.singerName = singerName;
		this.file=file;
	}
	
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
	public String getFileID() {
		return fileID;
	}
	public void setFileID(String fileID) {
		this.fileID = fileID;
	}
	public String getGid() {
		return gid;
	}
	public void setGid(String gid) {
		this.gid = gid;
	}
	public String getSongName() {
		return songName;
	}
	public void setSongName(String songName) {
		this.songName = songName;
	}
	public String getCp() {
		return cp;
	}
	public void setCp(String cp) {
		this.cp = cp;
	}
	public String getA() {
		return A;
	}
	public void setA(String a) {
		A = a;
	}
	public String getSingerName() {
		return singerName;
	}
	public void setSingerName(String singerName) {
		this.singerName = singerName;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
    
}
