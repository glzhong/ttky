package com.tiantiankuyin.database.bll;

import com.tiantiankuyin.database.TableStructure;

public class SqlString {
	
	/** 查找所有歌曲的 */
	public static String getSqlForSelectAllMusicOrderByAddedDate() {
		return " select * from " + TableStructure.Music.TABLE_NAME
				+ " order by " + TableStructure.Music.MUSIC_DATE_ADDED
				+ " desc";
	}

	/** 按专辑名查找歌曲 */
	public static String getSqlForSelectMusicByAlbumName() {
		return " select * from " + TableStructure.Music.TABLE_NAME + " where "
				+ TableStructure.Music.MUSIC_ALBUM + " = ?";
	}

	/** 按专辑名查找歌曲 */
	public static String getSqlForSelectMusicBySingerName() {
		return " select * from " + TableStructure.Music.TABLE_NAME + " where "
				+ TableStructure.Music.MUSIC_ARTIST + " = ?";
	}

	/** 按专辑名查找歌曲 */
	public static String getSqlForSelectMusicByFolderUrl() {
		return "SELECT * FROM "
				+ TableStructure.Music.TABLE_NAME + " where "
				+ TableStructure.Music.MUSIC_FOLDER_URL + " =? order by "
				+ TableStructure.Music.MUSIC_DATE_ADDED + " desc";
	}

	/** 按FileId查找歌曲 */
	public static String getSqlForSelectMusicByFileId(String fileId) {
		return " select * from " + TableStructure.Music.TABLE_NAME + " where "
				+ TableStructure.Music.MUSIC_FILE_ID + " = " + fileId;
	}
	
	/** 查找喜爱的歌曲 */
	public static String getSqlForSelectFavMusic() {
		return "SELECT * FROM " + TableStructure.Music.TABLE_NAME
				+ " WHERE " + TableStructure.Music.MUSIC_DATE_ADDED_FAV + " >0 "
				+ " order by " + TableStructure.Music.MUSIC_DATE_ADDED_FAV + " desc";
	}
	
	/** 查找某歌单中的歌曲 */
	public static String getSqlForSelectMusicInSongList() {
		return "SELECT m.* FROM ( SELECT * FROM "
				+ TableStructure.RelateList.TABLE_NAME + " WHERE "
				+ TableStructure.RelateList.RELATE_LIST_LIST_ID
				+ " = ? ) AS r LEFT JOIN " + TableStructure.Music.TABLE_NAME
				+ " AS m  ON m." + TableStructure.Music.MUSIC_ID + " = r."
				+ TableStructure.RelateList.RELATE_LIST_SONG_ID + " ORDER BY r."
				+ TableStructure.RelateList.RELATE_LIST_DATA_ADDED + " DESC";
	}
	
	

}
