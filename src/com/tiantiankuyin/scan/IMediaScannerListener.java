package com.tiantiankuyin.scan;


/**
 * 扫描监听器
 * 
 * @author Perry
 * 
 */
public interface IMediaScannerListener {

	/**
	 * 所有文件扫描完成通知
	 * 
	 * @param mediaPaths
	 * @author Perry
	 */
	public void onScanMediaAllCompleted(int songNum);

	/**
	 * 正在扫描的歌曲数目改变通知
	 * 
	 * @param currLen
	 * @author Perry
	 */
	public void onScanningMediaCountChanged(int maxLen,int currLen, int songNum);

	
	/**
	 * 开始扫描
	 */
	public void onScanMediaBegin();
}
