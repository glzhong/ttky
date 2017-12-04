package com.tiantiankuyin.para;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class UserData {

//临时数据---开始
//以下数据，生命周期与应用相同,退出应用后，即丢失
	/** 网络连接代理主机名 */
	private static String mProxyHost;

	/** 网络连接代理端口 */
	private static int mProxyPort;
//临时数据---结束
	
	public static String SHARED_PREFERENCES_NAME = "EASOU_PREFERENCES";
	private static UserData mInstence = new UserData();
	private SharedPreferencesManager mSharedPreferencesManager = new SharedPreferencesManager();
	
	/** 当前是否已经展示了欢迎页面 */
	private boolean isShowedWelcomActivity = false;
	
	public static UserData getInstence(){
		return mInstence;
	}
	
	public  boolean isShowedWelcomActivity() {
		return isShowedWelcomActivity;
	}

	/**
	 * 是否显示过欢迎页面
	 * @param isShowedWelcomActivity
	 */
	public void setShowedWelcomActivity(boolean isShowedWelcomActivity) {
		this.isShowedWelcomActivity = isShowedWelcomActivity;
	}
	
	/**
	 * 客户端是否已经展示过引导页面
	 * @param isShowedNewUserGuide
	 */
	public void setShowedNewUserGuide(boolean isShowedNewUserGuide){
		mSharedPreferencesManager.writeBooleanPreferences(SharedPreferencesManager.IS_SHOWED_NEW_USER_GUIDE,isShowedNewUserGuide);
	}
	
	/**
	 * 客户端是否已经展示过引导页面
	 */
	public boolean isShowedNewUserGuide(){
		return mSharedPreferencesManager.readBooleanPreferences(SharedPreferencesManager.IS_SHOWED_NEW_USER_GUIDE, false);
	}	
	
	/**
	 * 客户端是否已经展示过WIFI预约引导页面
	 * @param isShowedWifiGuide
	 */
	public void setShowedWifiDownloadGuide(boolean isShowedWifiGuide){
		mSharedPreferencesManager.writeBooleanPreferences(SharedPreferencesManager.IS_SHOWED_WIFI_DOWNLOAD_GUID,isShowedWifiGuide);
	}
	
	/**
	 * 客户端是否已经展示过WIFI预约引导页面
	 * @param isSavedLocalMsg
	 */
	public boolean isShowedWifiDownloadGuide(){
		return mSharedPreferencesManager.readBooleanPreferences(SharedPreferencesManager.IS_SHOWED_WIFI_DOWNLOAD_GUID, false);
	}
	/**
	 * 是否已扫描本地媒体
	 * @param isSavedLocalMsg
	 */
	public void setSavedLocalMsg(boolean isSavedLocalMsg){
		mSharedPreferencesManager.writeBooleanPreferences(SharedPreferencesManager.IS_SAVE_LOCALMSG,isSavedLocalMsg);
	}
	
	/**
	 * 是否已扫描本地媒体
	 * @param isSavedLocalMsg
	 */
	public boolean isSavedLocalMsg(){
		return mSharedPreferencesManager.readBooleanPreferences(SharedPreferencesManager.IS_SAVE_LOCALMSG, false);
	}
	
	
	/**
	 * 是否已获取线上媒体
	 * @param isSavedLocalMsg
	 */
	public void setSavedOnlineMsg(boolean isSavedOnlineMsg){
		mSharedPreferencesManager.writeBooleanPreferences(SharedPreferencesManager.IS_SAVE_ONLINEMSG,isSavedOnlineMsg);
	}
	
	/**
	 * 是否已获取线上媒体
	 * @param isSavedLocalMsg
	 */
	public boolean isSavedOnlieMsg(){
		return mSharedPreferencesManager.readBooleanPreferences(SharedPreferencesManager.IS_SAVE_ONLINEMSG, false);
	}
	
	/**
	 * 客户端是否是初次启动
	 * @param isSavedLocalMsg
	 */
	public void setFirstStartApp(boolean isFirstStartApp){
		mSharedPreferencesManager.writeBooleanPreferences(SharedPreferencesManager.IS_FIRST_START_APP,isFirstStartApp);
	}
	
	/**
	 * 客户端是否是初次启动
	 * @param isSavedLocalMsg
	 */
	public boolean isFirstStartApp(){
		return mSharedPreferencesManager.readBooleanPreferences(SharedPreferencesManager.IS_FIRST_START_APP, true);
	}
	
	/**
	 * 应用安装后的第一次启动时间
	 * @param isSavedLocalMsg
	 */
	public void setFirstStartTime(long firstStartTime){
		mSharedPreferencesManager.writeLongPreferences(SharedPreferencesManager.FIRST_START_TIME,firstStartTime);
	}
	
	/**
	 * 应用安装后的第一次启动时间
	 * @param isSavedLocalMsg
	 */
	public long getFirstStartTime(){
		return mSharedPreferencesManager.readLongPreferences(SharedPreferencesManager.FIRST_START_TIME,System.currentTimeMillis() / 1000 - 3 * 24 * 3600);
	}
	
	//TODO 该键可能没有用到，需进一步核实
	/**
	 * 用户查询当前正在操作的音乐列表的 sql
	 */
	public String getCurrentOperateMusicListSql() {
		return mSharedPreferencesManager.readStringPreferences(SharedPreferencesManager.CURRENT_MUSICLIST_SQL);

	}
	/**
	 * 用户查询当前正在操作的音乐列表的 sql
	 */
	public void setCurrentOperateMusicListSql(String currentMusicListSql) {
		mSharedPreferencesManager.writeStringPreferences(SharedPreferencesManager.CURRENT_MUSICLIST_SQL,currentMusicListSql);

	}
	
	/**
	 * 用户查询当前正在播放的歌单的 sql
	 */
	public String getCurrentPlayingMusicListSql() {
		return mSharedPreferencesManager.readStringPreferences(SharedPreferencesManager.CURRENT_PALYING_MUSICLIST_SQL);

	}
	/**
	 * 用户查询当前正在播放的歌单的 sql
	 */
	public void setCurrentPlayingMusicListSql(String currentMusicListSql) {
		mSharedPreferencesManager.writeStringPreferences(SharedPreferencesManager.CURRENT_PALYING_MUSICLIST_SQL,currentMusicListSql);
	}
	
	/**
	 * 用户当前正在播放的位置
	 */
	public int getCurrentPlayingPosition() {
		return mSharedPreferencesManager.readIntPreferences(SharedPreferencesManager.cur_Position);

	}
	/**
	 * 用户当前正在播放的位置
	 */
	public void setCurrentPlayingPosition(int currentPosition) {
		mSharedPreferencesManager.writeIntPreferences(SharedPreferencesManager.cur_Position,currentPosition);
	}
	
	/**
	 * 用户查询当前的播放类型
	 */
	public int getCurrentPlayingType() {
		return mSharedPreferencesManager.readIntPreferences(SharedPreferencesManager.cur_PlayType);

	}
	/**
	 * 用户当前正在播放的位置
	 */
	public void setCurrentPlayingType(int currentType) {
		mSharedPreferencesManager.writeIntPreferences(SharedPreferencesManager.cur_PlayType,currentType);
	}
	
	/**
	 * 在xml中存储Banner数量
	 */
	public int getBannerCount() {
		return mSharedPreferencesManager.readIntPreferences(SharedPreferencesManager.BANNER_SP_KEY);

	}
	/**
	 * 在xml中存储Banner数量
	 */
	public void setBannerCount(int count) {
		mSharedPreferencesManager.writeIntPreferences(SharedPreferencesManager.BANNER_SP_KEY,count);
	}
	
	/**
	 * 是否已从服务器获取了推荐数据
	 * @param isSavedLocalMsg
	 */
	public void setLoadedRecommondDataFromServer(boolean isLoadedRecommondDataFromServer){
		mSharedPreferencesManager.writeBooleanPreferences(SharedPreferencesManager.IS_LOADED_RECOMMONDD_DATA_FROM_SERVER,isLoadedRecommondDataFromServer);
	}
	
	/**
	 * 是否已从服务器获取了推荐数据
	 * @param isSavedLocalMsg
	 */
	public boolean isLoadedRecommondDataFromServer(){
		return mSharedPreferencesManager.readBooleanPreferences(SharedPreferencesManager.IS_LOADED_RECOMMONDD_DATA_FROM_SERVER, false);
	
	}
	
	
	/**
	 * 今天是否已经提示了升级信息。
	 * @return 是否已经提示了。
	 * @author Perry
	 */
	public boolean isShowUpdateMsgToday(){
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String mDateTime = formatter.format(cal.getTime());
		String ShowedData = mSharedPreferencesManager.readStringPreferences(SharedPreferencesManager.SHOWED_UPDATE_MSG_DATA);
		if(ShowedData != null && mDateTime.equals(ShowedData)){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 设置今天已经提示了升级信息。
	 * @author Perry
	 */
	public void setShowUpdateMsgToday(){
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String Today = formatter.format(cal.getTime());
		mSharedPreferencesManager.writeStringPreferences(SharedPreferencesManager.SHOWED_UPDATE_MSG_DATA, Today);
	}
	
	/**
	 * @return 当前版本是否再提醒升级。
	 * @author Perry
	 */
	public boolean isNotShowUpdateMsgAgain(){
		String version = Env.getVersion();
		if(version == null) {
			return false;
		}
		String notShowAgainVersion = mSharedPreferencesManager.readStringPreferences(SharedPreferencesManager.NOT_SHOW_UPDATE_MSG_VERSION);
		if(notShowAgainVersion!=null && notShowAgainVersion.equals(version)){
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * 设置当前版本不再提醒。
	 * @author Perry
	 */
	public void setNotShowUpdateMsg(){
		String version = Env.getVersion();
		if(version != null) {
			mSharedPreferencesManager.writeStringPreferences(SharedPreferencesManager.NOT_SHOW_UPDATE_MSG_VERSION, version);
		}
	}
	
	/** 网络连接代理主机名 */
	public String getProxyHost() {
		return mProxyHost;
	}
	/** 网络连接代理主机名 */
	public void setProxyHost(String proxyHost) {
		mProxyHost = proxyHost;
	}
	/** 网络连接代理端口 */
	public int getProxyPort() {
		return mProxyPort;
	}
	/** 网络连接代理端口 */
	public void setProxyPort(int proxyPort) {
		mProxyPort = proxyPort;
	}

}
