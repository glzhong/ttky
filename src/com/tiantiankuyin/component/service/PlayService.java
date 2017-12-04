package com.tiantiankuyin.component.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.tiantiankuyin.notification.PlayerNotification;
import com.tiantiankuyin.play.PlayEngine;
 

/**
 * 播放服务
 * 
 * @author DK
 * 
 */
public class PlayService extends Service {
	
	private PlayEngine mPlayEngine;

	@Override
	public void onCreate() {
		mPlayEngine = new PlayEngine();
		PlayerNotification.getInstence().setup();
		startForeground(PlayerNotification.NOTIFICATION_ID,PlayerNotification.getInstence().getNotification());
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		PlayerNotification.getInstence().cancel();
		stopForeground(true);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return new RemotePlayService(intent);
	}
	
	
	class RemotePlayService extends IRemotePlayService.Stub {
		public RemotePlayService(Intent intent){
			
		}
		
		
		@Override
		public IBinder asBinder() {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public void stop() throws RemoteException {
			mPlayEngine.stop();
			
		}
		
		@Override
		public void startPlayNetMusic(String url, String songName, String fileID,
				String gid, String singerName) throws RemoteException {
			mPlayEngine.startPlayNetMusic(url, songName, fileID, gid, singerName);
			
		}
		
		@Override
		public void startPlayMusic(String musicPath) throws RemoteException {
			mPlayEngine.startPlayMusic(musicPath);
			
		}
		@Override
		public void seekTo(int milliseconds) throws RemoteException {
			mPlayEngine.seekTo(milliseconds);
			
		}
		
		@Override
		public void play() throws RemoteException {
			mPlayEngine.play();
			
		}
		
		@Override
		public void pause() throws RemoteException {
			mPlayEngine.pause();
			
		}
		
		@Override
		public boolean isPrepared() throws RemoteException {
			return mPlayEngine.isPrepared();
		}
		
		@Override
		public boolean isPlaying() throws RemoteException {
			return mPlayEngine.isPlaying();
		}

		
		@Override
		public int getDuration() throws RemoteException {
			return mPlayEngine.getDuration();
		}
		
		@Override
		public int getCurrentPosition() throws RemoteException {
			return mPlayEngine.getCurrentPosition();
		}
		
		@Override
		public void deleteObservers() throws RemoteException {
			mPlayEngine.deleteObservers();
			
		}
		
		@Override
		public void deleteObserver(String observerName) throws RemoteException {
			mPlayEngine.deleteObserver(observerName);
			
		}
		
		@Override
		public void addObserver(String observerName,IRemotePlayerListener l) throws RemoteException {
			mPlayEngine.addObserver(observerName,l);
			
		}
	}	
	
}
