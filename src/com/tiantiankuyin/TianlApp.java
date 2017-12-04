package com.tiantiankuyin;

 

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.tiantiankuyin.bean.MusicInfo;
import com.tiantiankuyin.component.activity.BaseActivity;
import com.tiantiankuyin.component.activity.DownloadedActivity;
import com.tiantiankuyin.component.activity.DownloadingActivity;
import com.tiantiankuyin.component.service.DownloadFile;
import com.tiantiankuyin.component.service.DownloadFile.DownloadType;
import com.tiantiankuyin.component.service.DownloadService;
import com.tiantiankuyin.component.service.DownloadService.DownloadBinder;
import com.tiantiankuyin.database.bll.SqlString;
import com.tiantiankuyin.download.DownloadEngine;
import com.tiantiankuyin.download.IDownloadFileListener;
import com.tiantiankuyin.download.IDownloadListener;
import com.tiantiankuyin.net.NetCache;
import com.tiantiankuyin.notification.DownloadNotification;
import com.tiantiankuyin.para.Env;
import com.tiantiankuyin.para.IntentAction;
import com.tiantiankuyin.para.SPHelper;
import com.tiantiankuyin.para.UserData;
import com.tiantiankuyin.play.PlayLogicManager;
import com.tiantiankuyin.utils.Introspection;
import com.tiantiankuyin.utils.UpLoadDatas;

// TODO 建议：将Download相关监听独立出来，根据界面需要动态注册及注销监听
public class TianlApp extends Application implements IDownloadFileListener,
		IDownloadListener {

	/**系统事件监听*/
	private GlobalReceiver globalReceiver;
	
	/**睡眠定时*/
	private SleepTimer sleepTimer = new SleepTimer();
	
	private static final long serialVersionUID = 1L;
	/** 本应用（宿主）的原生类加载器 */
	public static ClassLoader originalClassLoader;
	/** 自定义类加载器，供插件使用，每次启动某个插件apk时，都会切换该类加载器为插件 apk的DexClassLoader */
	public static ClassLoader easouClassLoader;
	/**
	 * 用于Activity之间传递数据的中介(key为activity的intentAction,value为bundle数据结构)
	 * 使用原则是，activity的开发者A，负责写入以该 activity的intentAction为key的bundle
	 * 如有其它开发者B，需写入该key，需与A沟通
	 */
	public static Map<String, Bundle> activityBundles;



	/** Application实例 */
	private static TianlApp easouInstance;



	public static String currentTab;

	/**
	 * 最近进入的歌单名称
	 */
	private long currentSonglistID;

	// TODO 建议：此字段仅用于两个页面间临时传递数据，建议将其移动到相关页面
	/** 临时存储 批量编辑的 歌单列表 */
	private List<MusicInfo> batchEditMusicInfos;

	/** Activity管理集合 */
	public ArrayList<Activity> activityList;
	// TODO 建议：此字段为PlayEngine的业务数据，建议从Easou中移除
	/** 用于当前播放的是哪个目录下的路径 当前播放路径 */
	public static String currentPlayPath; 
	/** 用于建立返回关系字段 */
	public static boolean isBacktoSingerList = false;
	private Handler mHandler;
	private Toast mToast;
	
	// 以下为全局变量，现集中在此，备统一处理
	/**
	 * 当前要显示的页面，为几级页面
	 */
	private int pageLevel = 0;

	/**
	 * 是否是因用户触发
	 */
	private boolean downloadFromeUserOperate = false;

	/**
	 * 预约锁
	 */
	private boolean wifiDownloadLock = false;

	
	/**
	 * 计数器
	 */
	public static int i = 0;


	private boolean isFromAll = false;

	public boolean isFromAll() {
		return isFromAll;
	}

	public void setFromAll(boolean isFromAll) {
		this.isFromAll = isFromAll;
	}

	/**
	 * 当前要显示的页面，为几级页面
	 */
	public int getPageLevel() {
		return pageLevel;
	}

	/**
	 * 当前要显示的页面，为几级页面
	 */
	public void setPageLevel(int pageLevel) {
		this.pageLevel = pageLevel;
	}

	/**
	 * 新建的任务，是否是因用户触发
	 */
	public boolean isDownloadFromeUserOperate() {
		return downloadFromeUserOperate;
	}

	/**
	 * 新建的任务，是否是因用户触发
	 */
	public void setDownloadFromeUserOperate(boolean downloadFromeUserOperate) {
		this.downloadFromeUserOperate = downloadFromeUserOperate;
	}

	/**
	 * 预约锁 全部暂停后为true,全部开始后为true
	 */
	public boolean isWifiDownloadLock() {
		return wifiDownloadLock;
	}

	/**
	 * 预约锁 全部暂停后为true,全部开始后为true
	 */
	public void setWifiDownloadLock(boolean wifiDownloadLock) {
		this.wifiDownloadLock = wifiDownloadLock;
	}



	@Override
	public void onCreate() {
		try {
			System.loadLibrary("mg20pbase");
		} catch (Exception e) {
			e.printStackTrace();
		}		
		init();
	}

	/**
	 * 应用程序初始化
	 */
	private void init() {
		easouInstance = this;
		mHandler = new Handler();
		mToast = Toast.makeText(this, "", Toast.LENGTH_LONG);
		globalReceiver = new GlobalReceiver(this);
		UserData.getInstence().setShowedWelcomActivity(false);// 初始化是否已经展示欢迎Activity。
		activityList = new ArrayList<Activity>(); // 初始化Activity管理集合
		activityBundles = new Hashtable<String, Bundle>();
		loadClassLoader();
		// 开启音乐服务
		PlayLogicManager.newInstance().bindService();
		
		TianlApp.newInstance().setPageLevel(1);

		// 开启服务
		Intent downloadService = new Intent(
				IntentAction.INTENT_DOWNLOAD_SERVICE);
		downloadService.setPackage(getPackageName());
		startService(downloadService);
		globalReceiver.registeReceiver();

		Env.initEnv();// 读取用户信息，运行环境信息

		TianlApp.newInstance().setWifiDownloadLock(false);
		
		new Thread(){//后台上传用户音乐数据库。
			public void run() {
				try{
				UpLoadDatas.getInstance().UpLoad();
				}catch(Exception e){
					
				}
			};
		}.start();
	}

	public void showToast(final int resId) {
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				mToast.cancel();
				mToast.setText(resId);
				mToast.show();
			}
		});
	}

	public void showToast(final String msg) {
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				mToast.cancel();
				mToast.setText(msg);
				mToast.show();
			}
		});
	}

	/**
	 * 获得Easou对象
	 * @return
	 */
	public static TianlApp newInstance() {
		if (easouInstance != null)
			return easouInstance;
		return null;
	}

	/**
	 * 为应用配置类加载器
	 */
	private void loadClassLoader() {
		try {
			Context mBase = new Introspection<Context>(this, "mBase").get(); // 得到当前对象的mBase字段，mBase用于对组建的创建
			/*
			 * 得到当前Activity中的mPackageInfo对象，因为Context的实现类为ContextImpl，
			 * 但所有工作都会在ActivityThread.PackageInfo
			 * 即mPackageInfo中完成，所以需要得到该对象，因为ActivityThread
			 * .PackageInfo并为在API中暴露，所以采用内省获得Object
			 */
			Object mPackageInfo = new Introspection<Object>(mBase,
					"mPackageInfo").get();
			Introspection<ClassLoader> mClassLoader = new Introspection<ClassLoader>(
					mPackageInfo, "mClassLoader");
			ClassLoader parentClassLoader = mClassLoader.get();// 获得当前Activity的类加载器，即当前Easou的类加载器
			originalClassLoader = parentClassLoader; // 将该加载器设为基类加载器
			EasouClassLoader easouClassLoader = new EasouClassLoader( // 构造Easou类加载器，即用户类加载器
					parentClassLoader);
			mClassLoader.set(easouClassLoader);// 将Easou类加载器设为当前mPackageInfo即Activity的类加载器
		} catch (IllegalArgumentException e) {
			// e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// e.printStackTrace();
		} catch (IllegalAccessException e) {
			// e.printStackTrace();
		}
	}

	/**
	 * Easou类加载器
	 * @author DK
	 * 
	 */
	private class EasouClassLoader extends ClassLoader {

		public EasouClassLoader(ClassLoader parentClassLoader) {
			super(parentClassLoader);
		}

		@Override
		public Class<?> loadClass(String className)
				throws ClassNotFoundException {
			if (easouClassLoader != null) {
				try {
					Class<?> clazz = easouClassLoader.loadClass(className);
					if (clazz != null)
						return clazz;
				} catch (ClassNotFoundException e) {
				}
			}
			return super.loadClass(className);
		}
	}

	public List<MusicInfo> getBatchEditMusicInfos() {
		return batchEditMusicInfos;
	}

	public void setBatchEditMusicInfos(List<MusicInfo> batchEditMusicInfos) {
		this.batchEditMusicInfos = batchEditMusicInfos;
	}

	@Override
	public void onDownloadStateChanged(DownloadFile file) {
		DownloadingActivity downloadingActivity = DownloadingActivity
				.newInstance();
		if (downloadingActivity != null) {
			downloadingActivity.onDownloadStateChanged(file);
		}
	}

	@Override
	public void onDownloadProgressChanged(DownloadFile file, long maxLen,
			long currLen) {
		DownloadingActivity downloadingActivity = DownloadingActivity
				.newInstance();
		if (downloadingActivity != null) {
			downloadingActivity
					.onDownloadProgressChanged(file, maxLen, currLen);
		}
	}

	@Override
	public void onDownloadFileNameChanged(DownloadFile file, String fileName) {
		DownloadingActivity downloadingActivity = DownloadingActivity
				.newInstance();
		if (downloadingActivity != null) {
			downloadingActivity.onDownloadFileNameChanged(file, fileName);
		}
	}

	public void exit() {
		BaseActivity.instance.finish();
		saveCurPlayState();

		// 清除7天后的缓存记录。 add by perry 2012-10-24 11:32:17
		NetCache.checkAndCleanCache();

		Intent playServiceIntent = new Intent(IntentAction.INTENT_PLAY_SERVICE);
		playServiceIntent.setPackage(getPackageName());
		Intent downloadService = new Intent(
				IntentAction.INTENT_DOWNLOAD_SERVICE);
		downloadService.setPackage(getPackageName());
		DownloadNotification.getInstence().cancel();
		if (!SPHelper.newInstance().getAwaysRuningBackDownload()) {
			globalReceiver.closeReceiver();
			stopService(downloadService);
			stopService(playServiceIntent);
			DownloadBinder binder = DownloadService.newInstance().binder;
			if (binder != null) {
				DownloadService.newInstance().binder.pauseAllDownloadTask();
			}
			DownloadEngine engine = DownloadService.engine;
			if (engine != null)
				DownloadService.engine.saveDownloadInfo();
			System.exit(0);
		} else {
			PlayLogicManager.newInstance().stop();
			stopService(playServiceIntent);
		}
	}

	/**
	 * 保存当前播放状态值
	 * @author Erica
	 * @note 修改保存逻辑 10.8
	 * */
	private void saveCurPlayState() {
		if (PlayLogicManager.newInstance().getmSql() == null)
			PlayLogicManager.newInstance().setmSql(
					SqlString.getSqlForSelectAllMusicOrderByAddedDate());
		if (!PlayLogicManager.newInstance().isNetData()) {

			UserData.getInstence().setCurrentPlayingMusicListSql(
					PlayLogicManager.newInstance().getmSql());
			UserData.getInstence().setCurrentPlayingType(
					PlayLogicManager.newInstance().getPlayType());
			UserData.getInstence().setCurrentPlayingPosition(
					PlayLogicManager.newInstance().getmCurPosition());
		} else {
			UserData.getInstence().setCurrentPlayingMusicListSql(
					SqlString.getSqlForSelectAllMusicOrderByAddedDate());
			UserData.getInstence().setCurrentPlayingType(0);
			UserData.getInstence().setCurrentPlayingPosition(0);
		}
	}

	@Override
	public void onDownloadFileCompleted(DownloadFile file) {
		// System.out
		// .println("change  change  change  change  change  change  change  change  change  change  change  change  ");
		boolean lock = isWifiDownloadLock();
		boolean wifiAvaliable = Env.isWifiAvaliable();
		if (file.getFileType() == DownloadType.DOWNLOAD_TYPE_MUSIC
				&& !DownloadService.newInstance().hasNormalDownloadFile()
				&& !lock && wifiAvaliable) {
			DownloadService.newInstance().binder.startALlWifiDownloadTask();
		}
		DownloadingActivity downloadingActivity = DownloadingActivity
				.newInstance();
		DownloadedActivity downloadedActivity = DownloadedActivity
				.newInstance();
		if (downloadingActivity != null) {
			downloadingActivity.onDownloadFileCompleted(file);
		}
		if (downloadedActivity != null) {
			downloadedActivity.onDownloadFileCompleted(file);
		}
		if (DownloadService.newInstance() != null) {
			DownloadService.newInstance().onDownloadFileCompleted(file);
		}
		DownloadService.engine.saveDownloadInfo();
		// 在系统状态栏中显示消息

		if (DownloadService.newInstance().binder
				.isDownloadingAppointmentMusic()) {
			DownloadNotification.getInstence().update(
					R.string.notification_download_music_content);
		} else {
			DownloadNotification.getInstence().cancel();
		}

	}

	@Override
	public void onDownloadingFileCountChanged(int count) {
		DownloadingActivity downloadingActivity = DownloadingActivity
				.newInstance();
		if (downloadingActivity != null) {
			downloadingActivity.onDownloadingFileCountChanged(count);
		}
		if (DownloadService.newInstance() != null) {
			DownloadService.newInstance().onDownloadingFileCountChanged(count);
		}

		if (DownloadService.newInstance().binder
				.isDownloadingAppointmentMusic()) {
			DownloadNotification.getInstence().update(
					R.string.notification_download_music_content);
		} else {
			DownloadNotification.getInstence().cancel();
		}
	}

	@Override
	public void onDownloadError(DownloadFile file, DownloadErrorType error) {
		DownloadingActivity downloadingActivity = DownloadingActivity
				.newInstance();
		boolean lock = TianlApp.newInstance().isWifiDownloadLock();
		boolean wifiAvaliable = Env.isWifiAvaliable();
		if (file.getFileType() == DownloadType.DOWNLOAD_TYPE_MUSIC
				&& !DownloadService.newInstance().hasNormalDownloadFile()
				&& !lock && wifiAvaliable) {
			DownloadService.newInstance().binder.startALlWifiDownloadTask();
		}
		if (downloadingActivity != null) {
			downloadingActivity.onDownloadError(file, error);
		}
		if (DownloadService.newInstance() != null) {
			DownloadService.newInstance().onDownloadError(file, error);
		}
		if (error == DownloadErrorType.TASKEXIST) {
			boolean fromAll = TianlApp.newInstance().isFromAll();
			if (!fromAll) {
				showToast(R.string.task_exit);
			}
		} else if (error == DownloadErrorType.NOSDCARD) {
			showToast(R.string.no_sdcard);
		}
	}

	public long getCurrentSonglistID() {
		return currentSonglistID;
	}

	public void setCurrentSonglistID(long currentSonglistID) {
		this.currentSonglistID = currentSonglistID;
	}

	public SleepTimer getSleepTimer() {
		return sleepTimer;
	}


}