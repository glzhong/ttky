package com.tiantiankuyin.database.dao;

import java.util.List;

import com.tiantiankuyin.bean.AlbumVO;

public interface IAlbumDao {

	/**
	 * 获取音乐表下的所有文件夹信息的集合
	 * 
	 * @return
	 * @author Perry
	 */
	public abstract List<AlbumVO> selectAlbumDatas();

}
