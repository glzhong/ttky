package com.tiantiankuyin.para;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.tiantiankuyin.TianlApp;

/**
 * 作用：提供对sharedPreferences的读写操作
 * 使用方法：在此注册 key，然后在UserData中编写相关的 get set方法，通过get,set方法间接调用本类，业务模块不可直接调用本类中的方法
 * 
 * @author looming
 */
public /*package*/ class SharedPreferencesManager {
	
	private static final Context context = TianlApp.newInstance();
	/** 客户端是否是初次启动 */
	public static final String IS_FIRST_START_APP = "isFirstStartApp";  
	/** 客户端初次启动的时间。单位：秒 */
	public static final String FIRST_START_TIME = "firstStartTime";  
	/** 客户端是否已经展示过引导页面 */
	public static final String IS_SHOWED_NEW_USER_GUIDE = "isShowedGuide"; 
	/** 客户端是否已经存储过相应数据 */
	public static final String IS_SAVE_LOCALMSG = "isSaveLocalMsg"; 
	/** 客户端是否已经存储过相应数据 */
	public static final String IS_SAVE_ONLINEMSG = "isSaveOnlineMsg"; 
	/** 客户端是否已经展示过WIFI预约引导页面。 */
	public static final String IS_SHOWED_WIFI_DOWNLOAD_GUID = "isShowedWifiGuide";  
	/** 客户端是否支持宜搜压制的AAC格式。 */
	public static final String IS_AAC_COMPATIBLITY = "isAACCompatiblity"; 
	/** 是否已从服务器获取了推荐数据 */
	public static final String IS_LOADED_RECOMMONDD_DATA_FROM_SERVER = "loadRecommondDataFromServer"; 
	/** 已经提示升级信息的日期 */
	public static final String SHOWED_UPDATE_MSG_DATA = "isShowUpDateMsgToday";
	/** 不再提示升级信息的版本名 */
	public static final String NOT_SHOW_UPDATE_MSG_VERSION = "notShowUpdateMsgVersion";
	
	/** 当前操作歌单SQL */
	public static final String CURRENT_MUSICLIST_SQL = "currentMusicListSql";
	
	/** 当前播放歌单SQL */
	public static String CURRENT_PALYING_MUSICLIST_SQL = "sqlString";
	
	/** 当前播放歌曲index */
	public static String cur_Position = "currentPosition";
	/** 当前播放模式 */
	public static String cur_PlayType = "currentPlayType";
	/**在xml中存储Banner数量*/
	public static final String BANNER_SP_KEY = "BANNER_COUNT"; 
	
	/** 屏幕分辨率 宽 */
	public static final String SCREEN_WIDTH = "scrennWidth";
	/** 屏幕分辨率 高 */
	public static final String SCREEN_HEIGHT = "screenHeight";
	/** 屏幕的密度 */
	public static final String SCREEN_DENSITY = "densityDpi";
	
	/**
	 * WIFI是否可用
	 */
	public static final String IS_WIFI_AVALIABLE = "WIFI_AVALIABLE";
	

	
	
	/**
	 * 写入boolean
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public void writeBooleanPreferences(String key,
			boolean value) {
		SharedPreferences sp = context.getSharedPreferences(
				UserData.SHARED_PREFERENCES_NAME,
				Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		edit.putBoolean(key, value);
		edit.commit();
	}

	/**
	 * 读取boolean
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public boolean readBooleanPreferences(String key,
			boolean defaultValue) {
		Context context = TianlApp.newInstance();
		SharedPreferences sp = context.getSharedPreferences(
				UserData.SHARED_PREFERENCES_NAME,
				Context.MODE_PRIVATE);
		return sp.getBoolean(key, defaultValue);
	}

	/**
	 * 写入String
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public void writeStringPreferences(String key,
			String value) {
		SharedPreferences sp = context.getSharedPreferences(
				UserData.SHARED_PREFERENCES_NAME,
				Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		edit.putString(key, value);
		edit.commit();
	}

	/**
	 * 读取String
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public String readStringPreferences(String key) {
		SharedPreferences sp = context.getSharedPreferences(
				UserData.SHARED_PREFERENCES_NAME,
				Context.MODE_PRIVATE);
		return sp.getString(key, null);
	}

	/**
	 * 写入Long
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public void writeLongPreferences(String key,
			long value) {
		SharedPreferences sp = context.getSharedPreferences(
				UserData.SHARED_PREFERENCES_NAME,
				Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		edit.putLong(key, value);
		edit.commit();
	}

	/**
	 * 读取Long
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public long readLongPreferences(String key,
			long value) {
		SharedPreferences sp = context.getSharedPreferences(
				UserData.SHARED_PREFERENCES_NAME,
				Context.MODE_PRIVATE);
		return sp.getLong(key, 0);
	}

	/**
	 * 写入int
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public void writeIntPreferences(String key,
			int value) {
		SharedPreferences sp = context.getSharedPreferences(
				UserData.SHARED_PREFERENCES_NAME,
				Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		edit.putInt(key, value);
		edit.commit();
	}

	/**
	 * 读取int
	 * 
	 * @param context
	 * @param key
	 */
	public int readIntPreferences(String key) {
		SharedPreferences sp = context.getSharedPreferences(
				UserData.SHARED_PREFERENCES_NAME,
				Context.MODE_PRIVATE);
		return sp.getInt(key, 0);
	}
}

