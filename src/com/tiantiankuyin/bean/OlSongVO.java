package com.tiantiankuyin.bean;

import java.io.Serializable;

/**
 * 在线音乐信息VO对象
 * 
 * @author Erica
 */
public class OlSongVO implements Serializable {
	private static final long serialVersionUID = -225330819319427939L;
	/** 歌名 */
	private String song;
	/** 歌手 */
	private String singer;
	/** 歌曲时长 如:"1:30" */
	private String time;
	/** 歌曲主id */
	private String gid;
	/** 高质版ID */
	private String highId;
	/** 高质版地址 */
	private String highUrl;
	/** 高质源地址 */
	private String highPrtUrl;
	/** 高质版大小 */
	private String highSize;
	/** 流畅版ID */
	private String lowId;
	/** 流畅版地址 */
	private String lowUrl;
	/** 流畅版地址 */
	private String lowPrtUrl;
	/** 流畅版大小 */
	private String lowSize;
	/** 铃声版地址 */
	private String ringUrl;
	/** 铃声版地址 */
	private String ringPrtUrl;
	/** 铃声版大小 */
	private String ringSize;
	/** 铃声版ID */
	private String ringId;
	/** 铃声版歌曲时长:"1:30" */
	private String ringTime;
	/** 歌词ID 用来获取指定歌词 */
	private String lyricId;

	//彩铃试听地址
	private String crbtListenDir;
	
	//振铃试听地址
	private String ringListenDir;
	
	//全曲试听地址
	private String songListenDir;
	
	//是否有杜比歌曲 0没有，1有
	private String hasDolby;
	//歌曲编码--自己wap服务器
	private String songCode;
	
	//包月服务编号
	private String serviceId;
	
    public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	/**
     * 获取彩铃试听地址
     * @return
     */
	public String getCrbtListenDir() {
		return crbtListenDir;
	}

	/**
	 * 设置彩铃试听地址
	 * @param crbtListenDir
	 */
	public void setCrbtListenDir(String crbtListenDir) {
		this.crbtListenDir = crbtListenDir;
	}

	/**
	 * 获取振铃试听地址
	 * @return
	 */
	public String getRingListenDir() {
		return ringListenDir;
	}

	/**
	 * 设置振铃试听地址
	 * @param ringListenDir
	 */
	public void setRingListenDir(String ringListenDir) {
		this.ringListenDir = ringListenDir;
	}

	/**
	 * 获取全曲试听地址
	 * @return
	 */
	public String getSongListenDir() {
		return songListenDir;
	}

	/**
	 * 设置全曲试听地址
	 * @param songListenDir
	 */
	public void setSongListenDir(String songListenDir) {
		this.songListenDir = songListenDir;
	}

	/**
	 * 获取是否有杜比歌曲  0没有，1有
	 * @return
	 */
	public String getHasDolby() {
		return hasDolby;
	}

	/**
	 * 设置是否有杜比歌曲   0没有，1有
	 * @param hasDolby
	 */
	public void setHasDolby(String hasDolby) {
		this.hasDolby = hasDolby;
	}
	
	/**
	 * 获取wap服务器上面的歌曲编号
	 * @return
	 */
	public String getSongCode() {
		return songCode;
	}

	/**
	 * 设置wap服务器上面的歌曲编号
	 * @param songCode
	 */
	public void setSongCode(String songCode) {
		this.songCode = songCode;
	}

	public OlSongVO() {

	}

	public String getLyricId() {
		return lyricId;
	}

	public void setLyricId(String lyricId) {
		this.lyricId = lyricId;
	}

	public String getSong() {
		return song;
	}

	public void setSong(String song) {
		this.song = song;
	}

	public String getSinger() {
		return singer;
	}

	public void setSinger(String singer) {
		this.singer = singer;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getGid() {
		return gid;
	}

	public void setGid(String gid) {
		this.gid = gid;
	}

	// TODO 此处需改为 long,或将其它地方改为string
	public String getHighId() {
		return highId;
	}

	public void setHighId(String highId) {
		this.highId = highId;
	}

	public String getHighUrl() {
		return highUrl;
	}

	public void setHighUrl(String highUrl) {
		this.highUrl = highUrl;
	}

	public String getHighSize() {
		return highSize;
	}

	public void setHighSize(String highSize) {
		this.highSize = highSize;
	}

	public String getLowId() {
		return lowId;
	}

	public void setLowId(String lowId) {
		this.lowId = lowId;
	}

	public String getLowUrl() {
		return lowUrl;
	}

	public void setLowUrl(String lowUrl) {
		this.lowUrl = lowUrl;
	}

	public String getLowSize() {
		return lowSize;
	}

	public void setLowSize(String lowSize) {
		this.lowSize = lowSize;
	}

	public String getRingUrl() {
		return ringUrl;
	}

	public void setRingUrl(String ringUrl) {
		this.ringUrl = ringUrl;
	}

	public String getRingSize() {
		return ringSize;
	}

	public void setRingSize(String ringSize) {
		this.ringSize = ringSize;
	}

	public String getRingId() {
		return ringId;
	}

	public void setRingId(String ringId) {
		this.ringId = ringId;
	}

	public String getRingTime() {
		return ringTime;
	}

	public void setRingTime(String ringTime) {
		this.ringTime = ringTime;
	}
	

	public String getHighPrtUrl() {
		return highPrtUrl;
	}

	public void setHighPrtUrl(String highPrtUrl) {
		this.highPrtUrl = highPrtUrl;
	}

	public String getLowPrtUrl() {
		return lowPrtUrl;
	}

	public void setLowPrtUrl(String lowPrtUrl) {
		this.lowPrtUrl = lowPrtUrl;
	}

	public String getRingPrtUrl() {
		return ringPrtUrl;
	}

	public void setRingPrtUrl(String ringPrtUrl) {
		this.ringPrtUrl = ringPrtUrl;
	}
	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		OlSongVO songVO = (OlSongVO) o;
		if (this.getHighId() != null && songVO.getHighId() != null
				&& this.getHighId().equals(songVO.getHighId()))
			return true;
		if (this.getLowId() != null && songVO.getLowId() != null
				&& this.getLowId().equals(songVO.getLowId()))
			return true;
		if (this.getRingId() != null && songVO.getRingId() != null
				&& this.getRingId().equals(songVO.getRingId()))
			return true;
		return false;
	}

	@Override
	public int hashCode() {
		if (this.getHighId() != null)
			return Integer.valueOf(getHighId());
		if (this.getLowId() != null)
			return Integer.valueOf(getLowId());
		if (this.getRingId() != null)
			return Integer.valueOf(getRingId());
		return 0;
	}
}
