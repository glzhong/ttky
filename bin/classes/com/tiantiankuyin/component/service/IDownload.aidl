package com.tiantiankuyin.component.service;
import com.tiantiankuyin.component.service.DownloadFile;
interface IDownload {
	/**
	 * 开始某个下载任务
	 * 
	 * @param file
	 * @param isNewFileTask
	 *            是否是一个新文件任务
	 */
	void startDownloadTask(in DownloadFile file, boolean isNewFileTask);

	/**
	 * 暂停某个下载任务
	 * 
	 * @param file
	 */
	void pauseDownloadTask(in DownloadFile file);

	/**
	 * 删除某个下载任务
	 * 
	 * @param file
	 */
	void deleteDownloadTask(in DownloadFile file);

	/**
	 * 开始全部下载任务
	 */
	void startAllDownloadTask();

	/**
	 * 开始全部普通下载任务
	 */
	void startAllNormalDownloadTask();

	/**
	 * 开始全部预约下载任务
	 */
	void startALlWifiDownloadTask();

	/**
	 * 暂停全部下载任务
	 */
	void pauseAllDownloadTask();

	/**
	 * 暂停全部普通下载任务
	 */
	void pauseAllNormalDownloadTask();

	/**
	 * 暂停全部预约下载任务
	 */
	void pauseAllWifiDownloadTask();

	/**
	 * 删除所有已下载任务
	 */
	void deleteAllWifiDownloadTask();
}
