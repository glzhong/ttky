package com.tiantiankuyin.database.dao;

import java.util.List;

import com.tiantiankuyin.bean.MusicInfo;
import com.tiantiankuyin.bean.RelateInfo;
import com.tiantiankuyin.bean.SongListInfo;

public interface IRelateDao {

	/**
	 * 向关系表中插入一条歌曲与歌单的关联信息。
	 * 
	 * @param relateInfo
	 * @return 是否操作成功
	 * @author Perry
	 */
	public abstract boolean insertRelateData(RelateInfo relateInfo);

	/**
	 * 批量想关系表中插入多条歌曲与歌单的关联信息。
	 * 
	 * @param relateInfos
	 * @return 是否操作成功
	 * @author Perry
	 */
	public abstract boolean insertRelateDatas(List<RelateInfo> relateInfos);

	/**
	 * 根据Bean中提供的信息，删除一条关联信息。
	 * 
	 * @param relateInfo
	 * @return 是否操作成功
	 * @author Perry
	 */
	public abstract boolean deleteRelateData(RelateInfo relateInfo);

	/**
	 * 批量删除指定条件的数据
	 * 
	 * @param selectSQL
	 *            SQL语句
	 * @param selectionArgs
	 *            SQL语句的参数
	 * @return 是否操作成功
	 * @author Perry
	 */
	public abstract boolean deleteRelateDatasBySQL(String selectSQL,
			String[] selectionArgs);

	/**
	 * 批量删除 关系集合中的 所有关联信息。
	 * @param relateInfos
	 * @return
	 * @author Perry	
	 */
	public abstract boolean deleteAllRelateDatas(List<RelateInfo> relateInfos);
	
	/**
	 * 根据歌单ID，返回其关联的所有歌曲数据。
	 * 
	 * @param songListID
	 * @return 所有的歌曲ID 字符串数组，若无则返回null
	 * @author Perry
	 */
	public abstract List<MusicInfo> selectMusicDatasBySongListID(long songListID);

	/**
	 * 根据歌曲ID，返回其关联的所有歌单数据。
	 * 
	 * @param songID
	 * @return
	 * @author Perry
	 */
	public abstract List<SongListInfo> selectSongListDatasByMusicID(String songID);
	
	
	/**
	 * 根据歌单ID，返回其关联的所有歌曲数据量。
	 * 
	 * @param songListID
	 * @return 所有的歌曲数，若无则返回0
	 * @author ajowei
	 */
	public int selectMusicDatasCountBySongListID(String songListID);

}
