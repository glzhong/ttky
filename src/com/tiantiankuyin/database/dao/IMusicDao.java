package com.tiantiankuyin.database.dao;

import java.util.List;

import com.tiantiankuyin.bean.MusicInfo;

/**
 * 音乐表 操作接口
 * 
 * @author Perry
 * 
 */
public interface IMusicDao {

	/**
	 * 向歌曲表中插入一条歌曲信息
	 * 
	 * @param song
	 *            歌曲bean
	 * @return 插入是否成功
	 * @author Perry
	 */
	public abstract boolean insertMusicData(MusicInfo song);

	/**
	 * 根据歌曲ID删除一条歌曲信息。
	 * 
	 * @param songID
	 *            歌曲ID
	 * @return 是否删除成功
	 * @author Perry
	 */
	public abstract boolean deleteMusicData(String songID);

	/**
	 * 根据Bean中提供的信息，修改Bean为songId的信息。
	 * 
	 * @param song
	 * @return 修改是否成功
	 * @author Perry
	 */
	public abstract boolean updateMusicData(MusicInfo song);

	/**
	 * 根据Bean中提供的信息，修改Bean为songId的信息。
	 * 
	 * @param song
	 * @return 修改是否成功
	 * @author Perry
	 */
	public abstract boolean updateMusicDatas(List<MusicInfo> songs);
	
	/**
	 * 获取音乐表下的所有歌曲信息的集合。
	 * 
	 * @return 所有歌曲信息的集合
	 * @author Perry
	 */
	public abstract List<MusicInfo> selectMusicDatas();

	/**
	 * 根据查询条件返回歌曲信息集合。
	 * 
	 * @param selectSQL
	 *            查询的SQL语句
	 * @param selectionArgs
	 *            查询参数
	 * @return 歌曲信息的集合
	 * @author Perry
	 */
	public abstract List<MusicInfo> selectMusicDatasBySQL(String selectSQL,
			String[] selectionArgs);

	/**
	 * 根据查询条件返回歌曲信息条数
	 * @param selectSQL
	 * @param selectionArgs
	 * @return 歌曲数量，若没有则为0
	 * @author Perry	
	 */
	public abstract int selectMusicDataCountBySQL(String selectSQL,
			String[] selectionArgs);
	
	/**
	 * 批量插入歌曲数据
	 * 
	 * @param songs
	 *            歌曲信息的集合
	 * @return 操作是否成功
	 * @author Perry
	 */
	public abstract boolean insertAllMusicDatas(List<MusicInfo> songs);

	/**
	 * 批量删除歌曲数据
	 * 
	 * @param songIDs
	 *            要删除的歌曲ID
	 * @return 是否成功删除
	 * @author Perry
	 */
	public abstract boolean deleteAllMusicDatas(String... songIDs);

	/**
	 * 根据ID判断歌曲是否存在数据库
	 * 
	 * @param songID
	 *            歌曲ID
	 * @return 是否存在
	 * @author Perry
	 */
	public abstract boolean isMusicDataExist(String songID);

	/**
	 * 根据服务器上的FileID判断歌曲是否已经存在本地。
	 * 
	 * @param fileID
	 *            服务器上对应的FileID
	 * @return 是否存在。
	 * @author Perry
	 */
	public abstract boolean isNetMusicDataExist(String fileID);
}
