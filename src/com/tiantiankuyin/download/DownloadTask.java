package com.tiantiankuyin.download;

import com.tiantiankuyin.component.service.DownloadFile;

/**
 * 文件任务
 * 
 * @author DK
 * 
 */
public abstract class DownloadTask implements Runnable {

	/**
	 * 任务
	 * 
	 * @param file
	 */
	public DownloadTask(DownloadFile file) {
		this.file = file;
	}

	protected DownloadFile file; // 任务文件

	/**
	 * 获取当前任务的文件对象
	 * 
	 * @return
	 */
	public DownloadFile getDownloadTaskData() {
		return this.file;
	}
}
