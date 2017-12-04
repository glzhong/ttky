package com.tiantiankuyin.component.service;

import android.os.Parcel;
import android.os.Parcelable;

import com.tiantiankuyin.bean.MusicInfo;
import com.tiantiankuyin.download.IDownloadFileListener;
import com.tiantiankuyin.download.IDownloadFileListener.DownloadState;

/**
 * 任务文件
 * 
 * @author DK
 * 
 */
public class DownloadFile implements Comparable<DownloadFile>, Parcelable {
	/** 文件对应的唯一标识 */
	private String fileID;  
	/** 分组id */
	private String gid;  
	/** 开始文件时间 */
	private long createTime;  
	/** 任务文件的URL */
	private String url;  
	/** 要保存的文件名 */
	private String fileName;  
	/** 任务文件的类型 */
	private DownloadType fileType;  
	/** 任务文件的后缀名 */
	private String suffix; 
	/** 任务文件临时文件名 */
	private String tempFileName;  
	/** 任务状态 */
	private DownloadState state;  
	/** 任务文件的总大小 */
	private long fileTotalSize;  
	/** 已完成的大小 */
	private long fileCurrentSize;  
	/** 文件存放路径 */
	private String savePath;  
	/** 任务监听器 */
	private IDownloadFileListener downloadListener;  
	/** 入库后歌曲名 */
	private String songName;  
	/** 入库后歌手名 */
	private String singerName;  

	private MusicInfo musicInfo;

	/**
	 * 任务文件bean
	 * 
	 * @param fileID
	 *            文件唯一标识
	 * @param url
	 *            文件地址
	 */
	public DownloadFile(String fileID, String gid, String url, String fileName,
			String songName, String singerName) {
		this.fileID = fileID;
		this.url = url;
		this.gid = gid;
		this.songName = songName;
		this.singerName = singerName;
		this.fileName = fileName;

	}

	/**
	 * 任务文件bean
	 * 
	 * @param fileID
	 *            文件唯一标识
	 * @param url
	 *            文件地址
	 * @param fileName
	 *            要保存的文件名
	 * @param listener
	 *            文件任务监听器
	 */
	public DownloadFile(String fileID, String gid, String url, String fileName,
			String songName, String singerName, IDownloadFileListener listener) {
		this(fileID, gid, url, fileName, songName, singerName);
		this.downloadListener = listener;
	}

	/**
	 * @param fileID
	 *            文件唯一标识
	 * @param url
	 *            文件地址
	 * @param fileName
	 *            要保存的文件名
	 * @param fileType
	 *            任务文件的任务类型
	 * @param listener
	 *            文件任务监听器
	 */
	public DownloadFile(String fileID, String gid, String url, String fileName,
			String songName, String singerName, DownloadType fileType,
			IDownloadFileListener listener) {
		this(fileID, gid, url, fileName, songName, singerName);
		this.fileType = fileType;
	}
	
	//TODO MusicInfo中fileId为long型，此处为String需统一
	/**
	 * 获取文件对应的唯一标识
	 * 
	 * @return
	 */
	public String getFileID() {
		return fileID;
	}

	/**
	 * 设置文件对应的唯一标识
	 * 
	 * @param id
	 */
	public void setFileID(String fileID) {
		this.fileID = fileID;
	}

	/**
	 * 获取初次任务文件时间
	 * 
	 * @return
	 */
	public long getCreateTime() {
		return createTime;
	}

	/**
	 * 设置初次任务文件时间
	 * 
	 * @return
	 */
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	/**
	 * 获取任务文件的 url
	 * 
	 * @return
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * 设置
	 * 
	 * @param url
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public DownloadType getFileType() {
		return fileType;
	}

	public void setFileType(DownloadType fileType) {
		this.fileType = fileType;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getTempFileName() {
		return tempFileName;
	}

	public void setTempFileName(String tempFileName) {
		this.tempFileName = tempFileName;
	}

	public DownloadState getState() {
		return state;
	}

	public void setState(DownloadState state) {
		this.state = state;
	}

	public long getFileTotalSize() {
		return fileTotalSize;
	}

	public void setFileTotalSize(long fileTotalSize) {
		this.fileTotalSize = fileTotalSize;
	}

	public long getFileCurrentSize() {
		return fileCurrentSize;
	}

	public void setFileCurrentSize(long fileCurrentSize) {
		this.fileCurrentSize = fileCurrentSize;
	}

	public String getSavePath() {
		return savePath;
	}

	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}

	public IDownloadFileListener getDownloadListener() {
		return downloadListener;
	}

	public void setDownloadListener(IDownloadFileListener downloadListener) {
		this.downloadListener = downloadListener;
	}

	public String getSongName() {
		return songName;
	}

	public void setSongName(String songName) {
		this.songName = songName;
	}

	public String getSingerName() {
		return singerName;
	}

	public void setSingerName(String singerName) {
		this.singerName = singerName;
	}

	public String getGid() {
		return gid;
	}

	public void setGid(String gid) {
		this.gid = gid;
	}

	public MusicInfo getMusicInfo() {
		return musicInfo;
	}

	public void setMusicInfo(MusicInfo musicInfo) {
		this.musicInfo = musicInfo;
	}

	@Override
	public boolean equals(Object downloadFile) {
		// 如果资源id相等，则认定为是同一文件
		if (this.fileID != null
				&& downloadFile != null
				&& this.fileID
						.equals(((DownloadFile) downloadFile).getFileID()))
			return true;
		return false;
	}

	@Override
	public int hashCode() {
		return this.fileID.hashCode();
	}

	/**
	 * 任务文件类型
	 * 
	 * @author DK
	 * 
	 */
	public enum DownloadType {

		/** 歌曲类型 */
		DOWNLOAD_TYPE_MUSIC,
		/** 预约歌曲类型 */
		DOWNLOAD_TYPE_MUSIC_APPOINTMENT,
		/** 歌词类型 */
		DOWNLOAD_TYPE_LRC,
		/** 更新APK类型 */
		DOWNLOAD_TYPE_APK,
		/** 插件类型 */
		DOWNLOAD_TYPE_PLUGIN;
	}

	@Override
	public int compareTo(DownloadFile another) {
		// if (this.getFileType() ==
		// DownloadType.DOWNLOAD_TYPE_MUSIC_APPOINTMENT
		// && another.getFileType() ==
		// DownloadType.DOWNLOAD_TYPE_MUSIC_APPOINTMENT) {// 如果都是预约任务类型
		// return (int) (this.getCreateTime() - another.getCreateTime());
		// } else if (this.getFileType() ==
		// DownloadType.DOWNLOAD_TYPE_MUSIC_APPOINTMENT
		// && another.getFileType() !=
		// DownloadType.DOWNLOAD_TYPE_MUSIC_APPOINTMENT) { //
		// 如果当前是预约任务类型，另一个是普通类型
		// return -1;
		// } else if (this.getFileType() !=
		// DownloadType.DOWNLOAD_TYPE_MUSIC_APPOINTMENT
		// && another.getFileType() ==
		// DownloadType.DOWNLOAD_TYPE_MUSIC_APPOINTMENT) { //
		// 如果当前不是预约任务类型，另一个是预约任务类型
		// return 1;
		// } else if (this.getFileType() !=
		// DownloadType.DOWNLOAD_TYPE_MUSIC_APPOINTMENT
		// && another.getFileType() !=
		// DownloadType.DOWNLOAD_TYPE_MUSIC_APPOINTMENT) { // 如果都不是预约任务类型
		// return (int) (this.getCreateTime() - another.getCreateTime());
		// }
//		return (int) (this.getCreateTime() - another.getCreateTime());
		if(this.fileID == null && another.getFileID() != null)
			return -1;
		if(this.fileID != null && another.getFileID() == null)
			return 1;
		if(this.fileID == null && another.getFileID() == null)
			return 0;
		return (this.fileID).compareTo(another.getFileID());
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(fileID);
		dest.writeLong(createTime);
		dest.writeString(url);
		dest.writeString(fileName);
		dest.writeSerializable(fileType);
		dest.writeString(suffix);
		dest.writeString(tempFileName);
		dest.writeSerializable(state);
		dest.writeLong(fileTotalSize);
		dest.writeLong(fileCurrentSize);
		dest.writeString(savePath);
		dest.writeValue(downloadListener);
	}

	public DownloadFile(Parcel in) {
		fileID = in.readString();
		createTime = in.readLong();
		url = in.readString();
		fileName = in.readString();
		fileType = (DownloadType) in.readSerializable();
		suffix = in.readString();
		tempFileName = in.readString();
		state = (DownloadState) in.readSerializable();
		fileTotalSize = in.readLong();
		fileCurrentSize = in.readLong();
		savePath = in.readString();
		downloadListener = (IDownloadFileListener) in.readSerializable();
	}

	public DownloadFile() {
		
	}

	public static final Parcelable.Creator<DownloadFile> CREATOR = new Parcelable.Creator<DownloadFile>() {
		public DownloadFile createFromParcel(Parcel in) {
			return new DownloadFile(in);
		}

		public DownloadFile[] newArray(int size) {
			return new DownloadFile[size];
		}
	};
}
