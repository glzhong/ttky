package com.tiantiankuyin.para;

import android.content.Context;
import android.content.SharedPreferences;

import com.tiantiankuyin.utils.Lg;
import com.tiantiankuyin.TianlApp;

/**
 * 用于存储悦听客户端的本地配置参数
 * 
 * @author Perry
 */
// TODO
// 此类与com.tiantiankuyin.para.UserData及com.tiantiankuyin.para.SharedPreferencesManager功能重复，需逐步合并
public class SPHelper {

	private static final String TAG = SPHelper.class.getSimpleName();
	/**
	 * 非本地歌曲播放模式 1 省流量 0 标准
	 */
	private static final String NETWORK_PLAYMODEL = "network_playmodel";
	/** Sharedpreferences 的存储名字 */
	private static final String SP_EASOU_MUSIC_NAME = "easou_music";

	/** 是否自安装后，初次启动客户端 */
	private static final String IS_FIRST_LOGIN = "isFirstLogin";
	/**
	 * by andrew 搜索联想词最新的 15个
	 */
	private static final String SEARCH_LENVON_KEY = "search_lenvon_key";
	private Context context;

	private SharedPreferences mSharedPreferences;
	private SharedPreferences.Editor mEditor;
	/**
	 * 音乐品质 1 高品质 0 流畅版
	 */
	private static final String QUALITY = "quality";
	/**
	 * 播放文件夹路径
	 */
	private static final String FOLD_PATH = "fold_path";
	/**
	 * 保存热词
	 */
	private static final String HOT_KEYS = "hot_keys";

	/**
	 * 分类 被查看的次数
	 */
	private static final String CATEGORY_TIMES = "category_times";
	/**
	 * 拔出耳机自动暂停
	 */
	private static final String EAR_OFF_PAUSE = "ear_off_pause";

	/**
	 * 歌词页屏幕常亮
	 */
	private static final String LRC_SCREEN_ON = "lrc_screen_on";
	/**
	 * 歌曲边听边存
	 */
	private static final String LISTEN_DOWNLOAD = "listen_download";
	/**
	 * 自动获取歌词
	 */
	private static final String AUTO_DOWNLOAD_LRC = "auto_download_lrc";
	/**
	 * 自动获取歌手图
	 */
	private static final String AUTO_DOWNLOAD_PIC = "auto_download_pic";
	/**
	 * 始终后台运行预约任务
	 */
	private static final String RUNNING_BACK_DOWNLOAD = "running_back_download";

	private static SPHelper mSPHelper;

	private SPHelper() {
		this.context = TianlApp.newInstance();
		mSharedPreferences = this.context.getSharedPreferences(SP_EASOU_MUSIC_NAME, Context.MODE_PRIVATE);
		mEditor = mSharedPreferences.edit();

	}

	public static SPHelper newInstance() {
		if (mSPHelper == null) {
			synchronized (SPHelper.class) {
				if (mSPHelper == null) {
					mSPHelper = new SPHelper();
				}
			}
		}
		return mSPHelper;
	}

	/**
	 * 设置自初次启动后，已经启动过客户端
	 * 
	 * @author Perry
	 */
	public void setIsFirstLogin() {
		mEditor.putBoolean(IS_FIRST_LOGIN, false);
		mEditor.commit();
	}

	/**
	 * 获取是否自安装后，第一次启动客户端 用于判断是否需要启动引导界面
	 * 
	 * @return true 则为第一次启动，false 则不是第一次启动
	 */
	public boolean getIsFirstLogin() {
		boolean bl = false;
		try {
			bl = mSharedPreferences.getBoolean(IS_FIRST_LOGIN, true);
		} catch (Exception e) {
			Lg.e(TAG, "getIsFirstLogin fail");
		}
		return bl;
	}

	/**
	 * 保存 联想词 最新的 15个
	 */
	public void saveLenovKey(String value) {
		mEditor.putString(SEARCH_LENVON_KEY, value);
		mEditor.commit();
	}

	/**
	 * 获取 联想词
	 */
	public String getLenovKey() {
		return mSharedPreferences.getString(SEARCH_LENVON_KEY, null);
	}

	/**
	 * 获取后台运行预约任务设置
	 * 
	 * @return
	 */
	public boolean getAwaysRuningBackDownload() {
		return mSharedPreferences.getBoolean(RUNNING_BACK_DOWNLOAD, false);
	}

	public void setAwaysRuningBackDownload(boolean isRuningBack) {
		mEditor.putBoolean(RUNNING_BACK_DOWNLOAD, isRuningBack);
		mEditor.commit();
	}

	/**
	 * 获取边听边存信息
	 * 
	 * @return
	 */
	public boolean getListenDownload() {
		return mSharedPreferences.getBoolean(LISTEN_DOWNLOAD, true);
	}

	/**
	 * 歌曲边听边存设置
	 * 
	 * @return
	 */
	public void setListenDownload(boolean isListenDownload) {
		mEditor.putBoolean(LISTEN_DOWNLOAD, isListenDownload);
		mEditor.commit();
	}

	/**
	 * 获取歌词页屏幕常亮信息
	 * 
	 * @return
	 */
	public boolean getLrcScreenOn() {
		return mSharedPreferences.getBoolean(LRC_SCREEN_ON, true);
	}

	/**
	 * 歌词页屏幕常亮设置
	 * 
	 * @return
	 */
	public void setLrcScreenOn(boolean isListenDownload) {
		mEditor.putBoolean(LRC_SCREEN_ON, isListenDownload);
		mEditor.commit();
	}

	/**
	 * 获取自动获取歌词信息
	 * 
	 * @return
	 */
	public boolean getAutoDownloadLrc() {
		return mSharedPreferences.getBoolean(AUTO_DOWNLOAD_LRC, true);
	}

	/**
	 * 自动获取歌词设置
	 * 
	 * @return
	 */
	public void setAutoDownloadLrc(boolean isListenDownload) {
		mEditor.putBoolean(AUTO_DOWNLOAD_LRC, isListenDownload);
		mEditor.commit();
	}

	/**
	 * 获取自动获取歌手图信息
	 * 
	 * @return
	 */
	public boolean getAutoDownloadPic() {
		return mSharedPreferences.getBoolean(AUTO_DOWNLOAD_PIC, true);
	}

	/**
	 * 自动获取歌手图设置
	 * 
	 * @return
	 */
	public void setAutoDownloadPic(boolean isListenDownload) {
		mEditor.putBoolean(AUTO_DOWNLOAD_PIC, isListenDownload);
		mEditor.commit();
	}

	/**
	 * 获取拔出耳机自动暂停信息
	 * 
	 * @return
	 */
	public boolean getEarOffPause() {
		return mSharedPreferences.getBoolean(EAR_OFF_PAUSE, false);
	}

	/**
	 * 拔出耳机自动暂停设置
	 * 
	 * @return
	 */
	public void setEarOffPause(boolean isListenDownload) {
		mEditor.putBoolean(EAR_OFF_PAUSE, isListenDownload);
		mEditor.commit();
	}

	/**
	 * 获取音乐品质信息
	 * 
	 * @return
	 */
	public int getQuality() {
		return mSharedPreferences.getInt(QUALITY, 1);
	}

	/**
	 * 音乐品质设置
	 * 
	 * @return
	 */
	public void setQuality(int quality) {
		mEditor.putInt(QUALITY, quality);
		mEditor.commit();
	}

	/**
	 * 非本地歌曲播放模式 信息
	 * 
	 * @return
	 */
	public int getNetWorkPlayModel() {
		return mSharedPreferences.getInt(NETWORK_PLAYMODEL, 1);
	}

	/**
	 * 非本地歌曲播放模式 设置
	 * 
	 * @return
	 */
	public void setNetWorkPlayModel(int quality) {
		mEditor.putInt(NETWORK_PLAYMODEL, quality);
		mEditor.commit();
	}

	public String getCategoryTimes() {
		return mSharedPreferences.getString(CATEGORY_TIMES, null);
	}

	public void setCategoryTimes(String count) {
		mEditor.putString(CATEGORY_TIMES, count);
		mEditor.commit();
	}

	public String getFoldPath() {
		return mSharedPreferences.getString(FOLD_PATH, null);
	}

	public void setFoldPath(String path) {
		mEditor.putString(FOLD_PATH, path);
		mEditor.commit();
	}

	public String getHotKeys() {
		return mSharedPreferences.getString(HOT_KEYS, null);
	}

	// TODO 热词数据保存本地缓存后，当前代码中并没有使用该缓存数据
	public void setHotKeys(String keys) {
		mEditor.putString(HOT_KEYS, keys);
		mEditor.commit();
	}

	public void setPayInfo(String gid, boolean isPaySuccess) {
		mEditor.putBoolean(gid, isPaySuccess);
		mEditor.commit();
	}
	
	public boolean getPayInfo(String gid){
		return mSharedPreferences.getBoolean(gid, false);
	}
}
