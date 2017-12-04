package com.tiantiankuyin.download;

import com.tiantiankuyin.component.service.DownloadFile;

/**
 * 监听器
 * 
 * @author DK
 * 
 */
public interface IDownloadListener {

	/**
	 * 文件完成通知
	 * 
	 * @param df
	 */
	public void onDownloadFileCompleted(DownloadFile file);

	/**
	 * 正在进行的文件数目改变通知
	 * 
	 * @param count
	 */
	public void onDownloadingFileCountChanged(int count);

	/**
	 * 错误
	 * 
	 * @param error
	 *            错误信息
	 */
	public void onDownloadError(DownloadFile file, DownloadErrorType error);

	/**
	 * 文件错误类型
	 * 
	 * @author DK
	 * 
	 */
	public enum DownloadErrorType {
		/** 要开始的文件不存在 */
		FILE_NOT_EXIST,

		/** 外部存储器不可用 */
		NOSDCARD,

		/** 任务已存在 */
		TASKEXIST,

		/** 指定的保存目录不可用 */
		SAVEPATH_ILLEGAL,

		/** 要开始的文件已存在 */
		FILE_EXIST,

		/** 资源不存在检查url */
		RESOURCE_NONEXIST,

		/** 存储空间不足 */
		NONEMEMORY,

		/** IO异常 */
		IOERROR,

		/** 文件ID不存在 */
		NO_FILE_ID,

		/** 连接出错 */
		CONNECT_ERROR,

		/** 临时文件不存在 */
		TEMP_FILE_NOT_EXIST,

		/** SD卡空间不够 */
		NO_MEMORYSIZE
	}
}
