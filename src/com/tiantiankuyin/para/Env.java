package com.tiantiankuyin.para;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.tiantiankuyin.R;
import com.tiantiankuyin.TianlApp;
 

public class Env {
	/** 客户端版本号 */
	private static String version;
	/** 用户的机型 */
	private static String model;
	/** 手机号码 */
	private static String phoneNum; 
	/** 意见反馈音乐客户端标识 */
	public final static int FEEDBACK_APK_ID = 4140172; 
	/** 用于意见反馈post参数带过去的 */ 
	private static SharedPreferencesManager mSharedPreferencesManager = new SharedPreferencesManager();

	/** 渠道号 */
	public static String cid = "gm0es";

	public static void initEnv() {
		/*getPhoneNumber();// 获取手机号码
		getModel();// 获取机型
		getVersion();// 获取版本号
		getChannel();//获取渠道号
		checkAACCompatiblity();// 获取是否支持AAC格式。
*/	}
	
	/*// 获取手机号码
	public static String getPhoneNumber() {
		if (phoneNum == null || phoneNum.length() == 0) {
			TelephonyManager tm = (TelephonyManager) Easou.newInstance()
					.getSystemService(Context.TELEPHONY_SERVICE);
			phoneNum = tm.getLine1Number();
		}
		return phoneNum;
	}*/

	// 获取手机真实机型
	public static String getModel() {
		if (model == null || model.length() == 0) {
			Build bd = new Build();
			model = bd.MODEL;
		}
		return model;
	}

	// 获取版本号
	public static String getVersion() {
		if (version == null || version.length() == 0) {
			try {
				PackageManager packageManager = TianlApp.newInstance()
						.getPackageManager();
				// getPackageName()是你当前类的包名，0代表是获取版本信息
				PackageInfo packInfo = packageManager.getPackageInfo(TianlApp
						.newInstance().getPackageName(), 0);
				version = packInfo.versionName;
				return version;
			} catch (Exception e) {
			}
		}
		return version;
	}

	/**
	 * 获取渠道号
	 * 
	 * @author Perry
	 */
	public static String getChannel() {
		if (cid == null || cid.length() == 0) {
			String channelId = "gm0es";
			try {
				ApplicationInfo ai = TianlApp
						.newInstance()
						.getPackageManager()
						.getApplicationInfo(
								TianlApp.newInstance().getPackageName(),
								PackageManager.GET_META_DATA);
				Object value = ai.metaData.get("UMENG_CHANNEL");
				if (value != null) {
					channelId = value.toString();
				}
			} catch (Exception e) {
			}
			cid = channelId;
		}
		return cid;
	}
	
	/**
	 * 屏幕宽
	 * @return
	 */
	public static int getScreenWidth(){
		return mSharedPreferencesManager.readIntPreferences(SharedPreferencesManager.SCREEN_WIDTH);
	}
	/**
	 * 记录屏幕宽
	 * @return
	 */
	public static void setScreenWidth(int screenWidth){
		mSharedPreferencesManager.writeIntPreferences(SharedPreferencesManager.SCREEN_WIDTH,screenWidth);
	}
	
	/**
	 * 屏幕高
	 * @return
	 */
	public static int getScreenHeight(){
		return mSharedPreferencesManager.readIntPreferences(SharedPreferencesManager.SCREEN_HEIGHT);
	}
	
	/**
	 * 记录屏幕高
	 * @return
	 */
	public static void setScreenHeight(int screenWidtHeight){
		mSharedPreferencesManager.writeIntPreferences(SharedPreferencesManager.SCREEN_HEIGHT,screenWidtHeight);
	}
	
	/**
	 * 屏幕Density
	 * @return
	 */
	public static int getScreenDensity(){
		return mSharedPreferencesManager.readIntPreferences(SharedPreferencesManager.SCREEN_DENSITY);
	}
	/**
	 * 记录屏幕Density
	 * @return
	 */
	public static void setScreenDensity(int screenDensity){
		mSharedPreferencesManager.writeIntPreferences(SharedPreferencesManager.SCREEN_DENSITY,screenDensity);
	}
	
	public static boolean isWifiAvaliable(){
		return mSharedPreferencesManager.readBooleanPreferences(SharedPreferencesManager.IS_WIFI_AVALIABLE,false);

	}
	public static void setWifiAvaliable(boolean isWifiAbaliable){
		mSharedPreferencesManager.writeBooleanPreferences(SharedPreferencesManager.IS_WIFI_AVALIABLE,isWifiAbaliable);

	}
	
	/**
	 * 是否兼容acc
	 * @param isAACCompatiblity
	 */
	public static void setAACCompatiblity(boolean isAACCompatiblity){
		mSharedPreferencesManager.writeBooleanPreferences(SharedPreferencesManager.IS_AAC_COMPATIBLITY,isAACCompatiblity);
	}
	
	/**
	 * 是否兼容acc
	 * @param isAACCompatiblity
	 */
	public static boolean isAACCompatiblity(){
		return mSharedPreferencesManager.readBooleanPreferences(SharedPreferencesManager.IS_AAC_COMPATIBLITY, false);
	}
	
	/**
	 * 检测当前是否支持 自压缩的AAC播放格式。
	 * 
	 * @return true 支持, false 不支持
	 * @author Perry
	 */
	public static boolean checkAACCompatiblity() {
		MediaPlayer mp = MediaPlayer
				.create(TianlApp.newInstance(), R.raw.test_aac);

		if (mp == null) {// 若创建的MediaPlayer对象为空，则不支持AAC
			Env.setAACCompatiblity(false);
			return false;
		} else {// 若不为空，则支持AAC
			mp.release();// 释放MediaPlayer对象
			Env.setAACCompatiblity(true);
			return true;
		}
	}

	
}
