package com.tiantiankuyin.database.dao;

import java.util.List;

import com.tiantiankuyin.bean.SongListInfo;

public interface ISongListDao {

	/**
	 * 向歌单表中插入一条歌单信息
	 * 调用前，请确保当前歌单名并不存在数据库中。
	 * 
	 * @param songListInfo
	 * @return
	 * @author Perry
	 */
	public abstract boolean insertSongListData(String songListName);

	/**
	 * 根据歌单ID 删除一条歌单信息，及其所关联的歌单关系
	 * 
	 * @param songListInfo
	 * @return
	 * @author Perry
	 */
	public abstract boolean deleteSongListDatas(long songListID);

	/**
	 * 
	 * 
	 * @param songListInfo
	 * @return
	 * @author Perry	
	 */
	public abstract boolean updateSongListData(SongListInfo songListInfo);

	/**
	 * 获取歌单表下，所有的歌单信息。
	 * 
	 * @return
	 * @author Perry
	 */
	public abstract List<SongListInfo> selectSongListDatas();

	/**
	 * 获取歌单表下，所有的歌单信息。
	 * 
	 * @param selectSQL
	 * @param selectionArgs
	 * @return
	 * @author Perry
	 */
	public abstract List<SongListInfo> selcetSongListDatasBySQL(
			String selectSQL, String[] selectionArgs);
	
	/**
	 * 获取歌单表下，所有的歌单总数。
	 * 
	 * @return
	 * @author ajowei
	 */
	public abstract int selectSongListDatasCount();

}
