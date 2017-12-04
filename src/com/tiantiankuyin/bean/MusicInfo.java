package com.tiantiankuyin.bean;

import java.io.Serializable;

public class MusicInfo implements Serializable{

	private static final long serialVersionUID = -5087592419601238342L;

	/** 歌曲ID，自动增长 */
	private long id = -1;

	/** 系统ID，来自于数据库 */
	private long systemID = -1;

	/** 歌曲本地路径 */
	private String localUrl;

	/** 歌曲在线路径 */
	private String netUrl;

	/** 歌曲所属的文件夹路径 */
	private String folderUrl;

	/** 歌曲的文件名 */
	private String displayName;

	/** ID3中歌曲名 */
	private String title;

	/** 播放时长 */
	private long duration;

	/** 歌曲大小 */
	private int size;

	/** 歌曲的码率 */
	private long codeRate;

	/** 歌手ID */
	private long artistID;

	/** 歌手 */
	private String artist;

	/** 歌手名排序的关键字 */
	private String artistSortKey;

	/** 专辑ID */
	private long albumID;

	/** 专辑 */
	private String album;

	/** 专辑名排序的关键字 */
	private String albumSortKey;

	/** 歌曲所属歌词的保存路径 */
	private String lrcUrl;

	/** 歌曲所属图片的保存路径 */
	private String imageUrl;

	/** 歌曲添加收藏的时间。0：未被收藏； 非0：收藏的时间。 */
	private long dateAddedFav;

	/** 歌曲添加时间 */
	private long dateAdded;

	/** 歌曲最后修改时间 */
	private long dateModified;

	/** 服务器上的GID */
	private long gid;

	/** 服务器上的fileID */
	private String fileID;
	/** 网络歌曲的歌词ID */
	private String lyricId;
	
	public MusicInfo() {
	}

	public MusicInfo(long id, long systemID, String localUrl, 
			String folderUrl, String displayName, String title, long duration,
			int size, long codeRate, long artistID, String artist,
			String artistSortKey, long albumID, String album,
			String albumSortKey, String lrcUrl, String imageUrl,
			long dateAddedFav, long dateAdded, long dateModified, long gid,
			String fileID) {
		super();
		this.id = id;
		this.systemID = systemID;
		this.localUrl = localUrl;
		this.folderUrl = folderUrl;
		this.displayName = displayName;
		this.title = title;
		this.duration = duration;
		this.size = size;
		this.codeRate = codeRate;
		this.artistID = artistID;
		this.artist = artist;
		this.artistSortKey = artistSortKey;
		this.albumID = albumID;
		this.album = album;
		this.albumSortKey = albumSortKey;
		this.lrcUrl = lrcUrl;
		this.imageUrl = imageUrl;
		this.dateAddedFav = dateAddedFav;
		this.dateAdded = dateAdded;
		this.dateModified = dateModified;
		this.gid = gid;
		this.fileID = fileID;
	}



	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getSystemID() {
		return systemID;
	}

	public String getLyricId() {
		return lyricId;
	}

	public void setLyricId(String lyricId) {
		this.lyricId = lyricId;
	}

	public void setSystemID(long systemID) {
		this.systemID = systemID;
	}

	public String getLocalUrl() {
		return localUrl;
	}

	public void setLocalUrl(String localUrl) {
		this.localUrl = localUrl;
	}

	public String getNetUrl() {
		return netUrl;
	}

	public void setNetUrl(String netUrl) {
		this.netUrl = netUrl;
	}

	public String getFolderUrl() {
		return folderUrl;
	}

	public void setFolderUrl(String folderUrl) {
		this.folderUrl = folderUrl;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public long getCodeRate() {
		return codeRate;
	}

	public void setCodeRate(long codeRate) {
		this.codeRate = codeRate;
	}

	public long getArtistID() {
		return artistID;
	}

	public void setArtistID(long artistID) {
		this.artistID = artistID;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getArtistSortKey() {
		return artistSortKey;
	}

	public void setArtistSortKey(String artistSortKey) {
		this.artistSortKey = artistSortKey;
	}

	public long getAlbumID() {
		return albumID;
	}

	public void setAlbumID(long albumID) {
		this.albumID = albumID;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public String getAlbumSortKey() {
		return albumSortKey;
	}

	public void setAlbumSortKey(String albumSortKey) {
		this.albumSortKey = albumSortKey;
	}

	public String getLrcUrl() {
		return lrcUrl;
	}

	public void setLrcUrl(String lrcUrl) {
		this.lrcUrl = lrcUrl;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public long getGid() {
		return gid;
	}

	public void setGid(long gid) {
		this.gid = gid;
	}

	public String getFileID() {
		return fileID;
	}

	public void setFileID(String fileID) {
		this.fileID = fileID;
	}

	public long getDateAddedFav() {
		return dateAddedFav;
	}

	public void setDateAddedFav(long dateAddedFav) {
		this.dateAddedFav = dateAddedFav;
	}

	public long getDateAdded() {
		return dateAdded;
	}

	public long getDateModified() {
		return dateModified;
	}

	public void setDateModified(long dateModified) {
		this.dateModified = dateModified;
	}

	public void setDateAdded(long dateAdded) {
		this.dateAdded = dateAdded;
	}

	@Override
	public boolean equals(Object o) {
		if(o == null)
			return false;
		MusicInfo other = (MusicInfo) o;
		if (other.getId() == this.getId() && other.getLocalUrl().equals(this.getLocalUrl()))
			return true;
		return false;
	}

	@Override
	public int hashCode() {
		return (int) this.getId();
	}
}
