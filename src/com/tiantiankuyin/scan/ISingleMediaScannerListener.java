package com.tiantiankuyin.scan;

import com.tiantiankuyin.bean.MusicInfo;

public interface ISingleMediaScannerListener {

	
	/**
	 * 单个扫描开始通知
	 * @author Perry	
	 */
	public void onSingleMediaScannerBegin();
	
	/**
	 * 单个扫描完成通知
	 * @param path 扫描完成后的地址
	 * @param musicInfo 扫描后，已经插入到宜搜音乐对应的MusicInfo
	 * @author Perry	
	 */
	public void onSingleMediaCompleted(String path, MusicInfo musicInfo);
	
	/**
	 * 单个扫描后，错误通知
	 * @param path 扫描后的文件路径
	 * @param errorType 扫描错误类型
	 * @author Perry	
	 */
	public void onSingleMediaFail(String path, SingleMediaScannerErrorType errorType);
	
	/**
	 * 单个扫描错误类型。
	 * 1、要扫描的文件不存在。2、系统解析错误。3、音乐不符合插入宜搜音乐数据标准
	 * @author Perry
	 *
	 */
	public enum SingleMediaScannerErrorType {
		
		/** 要进行扫描的文件不存在  */
		FILE_NOT_EXIST,
		
		/** 调用系统解析后，解析失败。无数据返回  */
		SCANNER_FAIL,
		
		/** 系统解析后，有数据。但该音乐不符合插入宜搜音乐数据库标准。插入失败  */
		INSERT_FAIL
		
	}
	
	
}
