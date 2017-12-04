package com.tiantiankuyin.component.service;

import com.tiantiankuyin.component.service.IRemotePlayerListener;

interface IRemotePlayService {

	boolean isPrepared();

	void startPlayMusic(String musicPath);
	void startPlayNetMusic(String url, String songName,String fileID, String gid, String singerName);
	void play();
	void pause();
	void stop();
	void seekTo(int milliseconds);
	int getCurrentPosition();
	int getDuration();

	boolean isPlaying();

	void addObserver(String observerName,IRemotePlayerListener l);

	void deleteObserver(String observerName);

	void deleteObservers();

}
