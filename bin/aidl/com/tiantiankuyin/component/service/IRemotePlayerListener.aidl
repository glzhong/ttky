package com.tiantiankuyin.component.service;
interface IRemotePlayerListener {

	/**
	 * 播放器错误
	 * 
	 * @param what
	 *            分为MEDIA_ERROR_UNKNOWN和MEDIA_ERROR_SERVER_DIED
	 * @param extra
	 *            错误编码
	 */
	void onError(int what, int extra);

	/**
	 * 当播放器准备完毕
	 */
	void onPreparing();

	/**
	 * 当播放器准备完毕
	 */
	void onPrepared();

	/**
	 * 播放器开始缓冲
	 */
	void onStartBuffer();

	/**
	 * 播放在线多媒体缓冲进度
	 * 
	 * @param percent
	 */
	void onBufferingUpdate(int percent);

	/**
	 * 播放器开始播放
	 */
	void onStartPlay();

	/**
	 * 当暂停播放
	 */
	void onMusicPause();

	/**
	 * 当播放进度发生改变
	 * 
	 * @param currentMilliseconds
	 *            当前播放到的毫秒数
	 */
	void onProgressChanged(int currentMilliseconds);

	/**
	 * 当播放器已经播放完某个媒体文件
	 */
	void onCompletion();

	/**
	 * 当停止播放音乐
	 */
	void onMusicStop();

	/**
	 * 通知观察者正在缓冲（此缓冲不是缓冲完全部歌曲，是正在与服务器交互，可用于等待提示）
	 */
	void onBufferComplete();

	/**
	 * 通知观察者缓冲完成（此缓冲不是缓冲完全部歌曲，是请求完成可以播放）
	 */
	void onBuffer();

	/**
	 * 通知观察者缓存进度
	 * 
	 * @param currentCache
	 */
	void onCacheUpdate(long currentCache);
}
