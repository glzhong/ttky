package com.tiantiankuyin.database.dao;

import java.util.List;

import com.tiantiankuyin.bean.ArtistVO;

public interface IArtistDao {

	/**
	 * 获取音乐表下的所有文件夹信息的集合
	 * 
	 * @return
	 * @author Perry
	 */
	public abstract List<ArtistVO> selectArtistDatas();

	
}
