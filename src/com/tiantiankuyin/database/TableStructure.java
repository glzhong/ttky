package com.tiantiankuyin.database;

// TODO 建议：一个表对应一个内部类。内部类中包含TABLE_NAME，及相关字段名
public class TableStructure {
	/**
	 * 音乐表
	 * @author Perry
	 */
	public static class Music {
		/** 音乐表 对应数据库中的表名：music */
		public final static String TABLE_NAME = "music";
		
		/** 音乐表中 自增的歌曲ID 的字段  */
		public final static String MUSIC_ID = "_id";
		/** 音乐表中 系统媒体库中的ID 的字段 */
		public final static String MUSIC_SYSTEM_ID = "_systemID";
		/** 音乐表中歌曲所在的路径的字段 */
		public final static String MUSIC_LOCAL_URL = "_localUrl";
		/** 音乐表中 的字段 */
		public final static String MUSIC_FOLDER_URL = "_folderUrl";
		/** 音乐表中 歌曲的文件名 的字段 */
		public final static String MUSIC_DISPLAY_NAME = "_displayName";
		/** 音乐表中 歌曲所占的空间大小 的字段 */
		public final static String MUSIC_SIZE = "_size";
		/** 音乐表中 ID3信息中的歌名 的字段 */
		public final static String MUSIC_TITLE = "_title";
		/** 音乐表中 播放时长 的字段  */
		public final static String MUSIC_DURATION = "_duration";
		/** 音乐表中 歌曲码率 的字段 */
		public final static String MUSIC_CODE_RATE = "_codeRate";
		/** 音乐表中 歌手ID 的字段 */
		public final static String MUSIC_ARTIST_ID = "_artistID";
		/** 音乐表中 歌手名 的字段 */
		public final static String MUSIC_ARTIST = "_artist";
		/** 音乐表中 专辑ID 的字段 */
		public final static String MUSIC_ALBUM_ID = "_albumID";
		/** 音乐表中 专辑名 的字段 */
		public final static String MUSIC_ALBUM = "_album";
		/** 音乐表中 歌曲关联图片的路径 的字段  */
		public final static String MUSIC_IMAGE_URL = "_imageUrl";
		/** 音乐表中 歌曲关联歌词的路径 的字段 */
		public final static String MUSIC_LRC_URL = "_lrcUrl";
		/** 音乐表中 歌曲被收藏 的时间 */
		public final static String MUSIC_DATE_ADDED_FAV = "_dateAddedFav";
		/** 音乐表中 歌曲入库的时间 的字段,毫秒值 */
		public final static String MUSIC_DATE_ADDED = "_dateAdded";
		/** 音乐表中 歌曲入库的时间 的字段,秒值 */
		public final static String MUSIC_DATE_MODIFIED = "_dateModified";
		/** 音乐表中 歌曲按歌手排序的关键字 */
		public final static String MUSIC_ARTIST_SORT_KEY = "_artistSortKey";
		/** 音乐表中 歌曲按专辑排序的关键字 */
		public final static String MUSIC_ALBUM_SORT_KEY = "_albumSortKey";
		/** 服务器上的GID */
		public final static String MUSIC_GID = "_gid";
		/** 服务器上的fileId */
		public final static String MUSIC_FILE_ID = "_fileID";

	}

	/**
	 * 歌单表
	 * @author Perry
	 */
	public static class SongList{
		
	/** 歌单表 对应数据库中的表名：songlist */
	public final static String TABLE_NAME = "songlist";
	/** 歌单表中 自增的歌单ID 的字段 */
	public final static String SONGLIST_ID = "_id";
	/** 歌单表中 歌单的名称 的字段  */
	public final static String SONGLIST_NAME = "_name";
	/** 歌单表中 创建时间 的字段 */
	public final static String SONGLIST_DATA_CREATED = "_dataCreated";
	}
	
	
	/**
	 * 歌曲与歌单关联表
	 * @author Perry
	 */
	public static class RelateList {
		
		/** 歌曲与歌单关联表 对应数据库中的表名：relate_list */
		public final static String TABLE_NAME = "relate_list";
		
		/** 自增的ID 的字段 */
		public final static String RELATE_LIST_ID = "_id";
		/** 关联歌单的ID 的字段 */
		public final static String RELATE_LIST_LIST_ID = "_listID";
		/** 关联歌曲的ID 的字段 */
		public final static String RELATE_LIST_SONG_ID = "_songID";
		/** 关联歌曲的时间 的字段 */
		public final static String RELATE_LIST_DATA_ADDED = "_dataAdded";
	}
}
