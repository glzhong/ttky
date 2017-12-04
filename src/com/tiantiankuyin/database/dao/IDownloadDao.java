package com.tiantiankuyin.database.dao;

import java.util.Set;

import com.tiantiankuyin.component.service.DownloadFile;

/**
 * 数据库操作接口
 * 
 * @author DK
 * 
 */
public interface IDownloadDao {

	/**
	 * 向数据库中插入任务文件信息
	 * 
	 * @param file
	 *            文件bean
	 * @return 操作是否成功
	 */
	public abstract boolean insertDownloadData(DownloadFile file);

	/**
	 * 根据文件ID删除任务文件数据
	 * 
	 * @param 文件ID
	 * @return
	 */
	public abstract boolean deleteDownloadData(String fileID);

	/**
	 * 根据文件ID修改文件数据
	 * 
	 * @param file
	 * @return 操作是否成功
	 */
	public abstract boolean updateDownloadData(DownloadFile file);

	/**
	 * 取出所有任务文件数据
	 * 
	 * @return
	 */
	public abstract Set<DownloadFile> selectDownloadDatas();

	/**
	 * 根据查询条件返回结果集
	 * 
	 * @param selectSQL
	 *            查询sql语句
	 * @param selectionArgs
	 *            查询参数
	 * @return
	 */
	public abstract Set<DownloadFile> selectDownloadDatasBySQL(
			String selectSQL, String[] selectionArgs);

	/**
	 * 批量插入任务文件数据
	 * 
	 * @param downloadFiles
	 * @param dropTable
	 *            是否删除表
	 * @return
	 */
	public abstract boolean insertAllDownloadDatas(
			Set<DownloadFile> downloadFiles, boolean dropTable);

	/**
	 * 批量删除任务文件数据
	 * 
	 * @param dropTable
	 *            是否删除表
	 * @param fileIDs
	 *            要删除的文件ID
	 * @return
	 */
	public abstract boolean deleteAllDownloadDatas(boolean dropTable,
			String... fileIDs);

	/**
	 * 批量删除指定条件数据
	 * 
	 * @param selectSQL
	 * @param selectionArgs
	 * @return
	 */
	public abstract boolean deleteAllDownloadDatasBySQL(String selectSQL,
			String[] selectionArgs);

	/**
	 * 根据ID判断数据是否存在
	 * 
	 * @param fileID
	 * @return
	 */
	public abstract boolean isDownloadFileExist(String fileID);
}
