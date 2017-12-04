package com.tiantiankuyin.component.service;

import java.io.File;
import java.util.Iterator;
import java.util.Set;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.IBinder; 

import com.tiantiankuyin.component.service.DownloadFile.DownloadType;
import com.tiantiankuyin.database.bll.LocalMusicManager;
import com.tiantiankuyin.database.bll.OnDataPreparedListener;
import com.tiantiankuyin.database.bll.OnlineMusicManager;
import com.tiantiankuyin.download.DownloadEngine;
import com.tiantiankuyin.download.IDownloadFileListener.DownloadState;
import com.tiantiankuyin.download.IDownloadListener.DownloadErrorType;
import com.tiantiankuyin.para.Constant;
import com.tiantiankuyin.utils.CommonUtils;
import com.tiantiankuyin.utils.Lg;
import com.tiantiankuyin.component.service.IDownload.Stub;
import com.tiantiankuyin.TianlApp;

/**
 * 任务服务
 * 
 * @author DK
 * 
 */
public class DownloadService extends Service {
	/** wifi锁 */
	private WifiLock mWifiLock; 
	/** 任务引擎 */
	public static DownloadEngine engine;
	/** 远程调用接口 */
	public DownloadBinder binder; 
	private static DownloadService downloadService;

	public static DownloadService newInstance() {
		return downloadService;
	}

	 
	public IBinder onBind(Intent intent) {
		return binder;
	}

	 
	public void onCreate() {
//		setForeground(true); // 设置service的优先级为前台进程级别
		mWifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE))
				.createWifiLock(WifiManager.WIFI_MODE_FULL, "downloadService");
		mWifiLock.acquire();
		binder = new DownloadBinder();
		downloadService = this;
		initDownloadEngine(); // 初始化引擎
	}

 
	public void onDestroy() {
		super.onDestroy();
		if (binder != null)
			binder.pauseAllDownloadTask();
		if (engine != null)
			engine.saveDownloadInfo();
	}

	/**
	 * 初始化引擎
	 */
	private void initDownloadEngine() {
		new Thread() {
			public void run() {
				try {
					Set<DownloadFile> downloadingFiles = LocalMusicManager.getInstence().getDownloadDatasByState(LocalMusicManager.DOWNLOADING);
					Set<DownloadFile> downloadedFiles =  LocalMusicManager.getInstence().getDownloadDatasByState(LocalMusicManager.DOWNLOADED);
					engine = DownloadEngine.newInstance(DownloadService.this,
							TianlApp.newInstance(), downloadingFiles,
							downloadedFiles);
					if (downloadingFiles != null && downloadingFiles.size() > 0) {
						Iterator<DownloadFile> iterator = downloadedFiles
								.iterator();
						while (iterator.hasNext()) {
							DownloadFile file = iterator.next();
							file.setDownloadListener(TianlApp.newInstance());
						}
					}
				} catch (Exception e) {
//					e.printStackTrace();
				}
			};
		}.start();
	}

	/**
	 * 删除临时文件
	 * 
	 * @param file
	 */
	public void deleteLocalFile(DownloadFile file) {
		File tempFile = new File(Constant.SdcardPath.DOWNLOAD_TMP_SAVEPATH + "/"
				+ CommonUtils.MD5(file.getFileID()));
		if (tempFile.exists())
			tempFile.delete();
	}

	public boolean hasNormalDownloadFile() {
		Set<DownloadFile> downloadingFiles = DownloadEngine.downloadingFiles;
		if (downloadingFiles != null) {
			Iterator<DownloadFile> iterator = downloadingFiles.iterator();
			while (iterator.hasNext()) {
				DownloadFile file = iterator.next();
				if (file.getFileType() == DownloadType.DOWNLOAD_TYPE_MUSIC
						&& (file.getState() == DownloadState.STATE_DOWNING || file
								.getState() == DownloadState.STATE_WAITING))
					return true;
			}
		}
		return false;
	}

	public void onDownloadFileCompleted(DownloadFile file) {
		// 任务完成
	}

	public void onDownloadingFileCountChanged(int count) {
		// 任务文件数改变
	}

	public void onDownloadError(DownloadFile file, DownloadErrorType error) {
		// 错误
	}

	/**
	 * 
	 * 远程调用接口
	 * 
	 * @author DK
	 * 
	 */
	public class DownloadBinder extends Stub {

		/**
		 * 开始某个任务
		 * 
		 * @param file
		 * @param isNewFileTask
		 *            是否是一个新文件任务
		 */
		public void startDownloadTask(final DownloadFile file,
				final boolean isNewFileTask) {
			if (file == null || engine == null)
				return;
			//重新获取链接
			OnlineMusicManager.getInstence().getSongUrlData(TianlApp.newInstance(),
					new OnDataPreparedListener<String>() {
						@Override
						public void onDataPrepared(String data) {
							if (data != null) {	
								String url = data;
								if (url.startsWith("\"") && url.endsWith("\"")) {
									url = url.substring(url.indexOf("\"") + 1,
											url.lastIndexOf("\""));
								}
								file.setUrl(url);
								//修复铃声版会被覆盖的问题。edit by perry 2012-10-24 14:50:10
								if(file.getFileName() ==null || !file.getFileName().contains("铃声")){
									file.setFileName(CommonUtils.getFileNameByUrl(url));// 重新设置文件名，防止filename为空
								}
								engine.newDownloadTask(file, isNewFileTask);
								
							}else {
								Lg.d("getSongUrlData() == null");
								engine.newDownloadTask(file, isNewFileTask);
								return;
							}
						}
					}, CommonUtils.getDownloadUrl(file.getGid(), file.getFileID()),true);
		}

		/**
		 * 暂停某个任务
		 * 
		 * @param file
		 */
		public void pauseDownloadTask(DownloadFile file) {
			if (file == null)
				return;
			engine.pauseDownloadFile(file);
		}

		/**
		 * 删除某个任务
		 * 
		 * @param file
		 */
		public void deleteDownloadTask(DownloadFile file) {
			if (file == null)
				return;
			engine.pauseDownloadFile(file);
			if (file.getState() != DownloadState.STATE_DOWNING
					&& DownloadEngine.downloadingFiles != null) { // 如果是未完成的任务
				DownloadEngine.downloadingFiles.remove(file);
			}
			LocalMusicManager.getInstence().deleteDownloadData(file.getFileID());
		}

		public void deleteDownloadedTask(DownloadFile file) {
			if (file == null)
				return;
			if (DownloadEngine.downloadedFiles != null)
				DownloadEngine.downloadedFiles.remove(file); // 如果是已完成的任务
			LocalMusicManager.getInstence().deleteDownloadData(file.getFileID());
		}

		/**
		 * 开始全部任务
		 */
		public void startAllDownloadTask() {
			Set<DownloadFile> downloadingFiles = DownloadEngine.downloadingFiles;
			if (downloadingFiles == null)
				return;
			Iterator<DownloadFile> iterator = downloadingFiles.iterator();
			while (iterator.hasNext()) {
				DownloadFile file = iterator.next();
				engine.newDownloadTask(file, false);
			}
		}

		/**
		 * 开始全部普通任务
		 */
		public void startAllNormalDownloadTask() {
			Set<DownloadFile> downloadingFiles = DownloadEngine.downloadingFiles;
			if (downloadingFiles == null)
				return;
			Iterator<DownloadFile> iterator = downloadingFiles.iterator();
			while (iterator.hasNext()) {
				DownloadFile file = iterator.next();
				if (file.getFileType() == DownloadType.DOWNLOAD_TYPE_MUSIC)
					engine.newDownloadTask(file, false);
			}
		}

		/**
		 * 开始全部预约任务
		 */
		public void startALlWifiDownloadTask() {
			Set<DownloadFile> downloadingFiles = DownloadEngine.downloadingFiles;
			if (downloadingFiles == null)
				return;
			Iterator<DownloadFile> iterator = downloadingFiles.iterator();
			while (iterator.hasNext()) {
				DownloadFile file = iterator.next();
				if (file.getFileType() == DownloadType.DOWNLOAD_TYPE_MUSIC_APPOINTMENT)
					engine.newDownloadTask(file, false);
			}
		}

		/**
		 * 暂停全部任务
		 */
		public void pauseAllDownloadTask() {
			Set<DownloadFile> downloadingFiles = DownloadEngine.downloadingFiles;
			if (downloadingFiles == null)
				return;
			Iterator<DownloadFile> iterator = downloadingFiles.iterator();
			while (iterator.hasNext()) {
				DownloadFile file = iterator.next();
				engine.pauseDownloadFile(file);
			}
		}

		/**
		 * 暂停全部普通任务
		 */
		public void pauseAllNormalDownloadTask() {
			Set<DownloadFile> downloadingFiles = DownloadEngine.downloadingFiles;
			if (downloadingFiles == null)
				return;
			Iterator<DownloadFile> iterator = downloadingFiles.iterator();
			while (iterator.hasNext()) {
				DownloadFile file = iterator.next();
				if (file.getFileType() == DownloadType.DOWNLOAD_TYPE_MUSIC)
					engine.pauseDownloadFile(file);
			}
		}

		/**
		 * 暂停全部预约任务
		 */
		public void pauseAllWifiDownloadTask() {
			Set<DownloadFile> downloadingFiles = DownloadEngine.downloadingFiles;
			if (downloadingFiles == null)
				return;
			Iterator<DownloadFile> iterator = downloadingFiles.iterator();
			while (iterator.hasNext()) {
				DownloadFile file = iterator.next();
				if (file.getFileType() == DownloadType.DOWNLOAD_TYPE_MUSIC_APPOINTMENT)
					engine.pauseDownloadFile(file);
			}
		}

		/**
		 * 删除所有已任务
		 */
		public void deleteAllWifiDownloadTask() {
			Set<DownloadFile> downloadingFiles = DownloadEngine.downloadedFiles;
			if (downloadingFiles == null)
				return;
			Iterator<DownloadFile> iterator = downloadingFiles.iterator();
			while (iterator.hasNext()) {
				DownloadFile file = iterator.next();
				deleteDownloadTask(file);
			}
		}

		/**
		 * 是否正在通过 wifi预约歌曲
		 * @return
		 */
		public boolean isDownloadingAppointmentMusic() {
			Set<DownloadFile> downloadingFiles = DownloadEngine.downloadingFiles;
			if (downloadingFiles == null)
				return false;
			Iterator<DownloadFile> iterator = downloadingFiles.iterator();
			while (iterator.hasNext()) {
				DownloadFile downloadFile = iterator.next();
				if (downloadFile.getFileType() == DownloadFile.DownloadType.DOWNLOAD_TYPE_MUSIC_APPOINTMENT
						&& (downloadFile.getState() == DownloadState.STATE_DOWNING || downloadFile.getState() == DownloadState.STATE_WAITING)) {
					return true;
				}
			}
			return false;
		}

	}
}
