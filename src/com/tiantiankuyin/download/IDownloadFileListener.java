package com.tiantiankuyin.download;

import java.io.Serializable;

import com.tiantiankuyin.component.service.DownloadFile;

/**
 * 文件监听器
 * 
 * @author DK
 * 
 */
public interface IDownloadFileListener extends Serializable {

	/**
	 * 文件任务状态
	 * 
	 * @author DK
	 * 
	 */
	public enum DownloadState {
		/** 等待 */
		STATE_WAITING,
		/** 正在进行 */
		STATE_DOWNING,
		/** 暂停 */
		STATE_PAUSED,
		/** 已完成 */
		STATE_DOWNCOMPLETE,
		/** 失败 */
		STATE_FAILED
	}

	/**
	 * 状态改变通知
	 */
	public void onDownloadStateChanged(DownloadFile file);

	/**
	 * 进度改变通知
	 * 
	 * @param maxLen
	 *            文件最大长度
	 * @param currLen
	 *            当前已经完成的长度
	 */
	public void onDownloadProgressChanged(DownloadFile file, long maxLen,
			long currLen);

	/**
	 * 保存的文件名有变化通知
	 * 
	 * @param fileName
	 *            文件名
	 */
	public void onDownloadFileNameChanged(DownloadFile file, String fileName);
}
