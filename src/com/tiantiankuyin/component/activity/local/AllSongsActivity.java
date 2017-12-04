package com.tiantiankuyin.component.activity.local;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import com.tiantiankuyin.component.activity.BaseActivity;
import com.tiantiankuyin.para.IntentAction;
import com.tiantiankuyin.utils.CommonUtils;
import com.tiantiankuyin.R;
import com.tiantiankuyin.TianlApp;


/**
 * 本地歌曲模块-全部歌曲页
 * 
 * @author Erica
 * @note on 2012-9-8 目前程序中数据库操作部分有测试数据，需要后期删掉
 */
public class AllSongsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View rootView = LayoutInflater.from(this).inflate(
				R.layout.local_all_songs, null);
		setContentView(rootView);
		CommonUtils.setTitle(rootView, getString(R.string.all_music), true,
				true);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	@Override
	public void onBackPressed() {
		backToLastView();
	}

//	/** 获取默认数据测试入口 */
//	public void makeUpMusicInfo() {
 	//	musicInfos = LocalMusicManager.getInstence().getAllMusic();
//		if (musicInfos != null && musicInfos.size() > 0) {
//			PlayLogicManager.newInstance().setMusicInfo(musicInfos, 0,
//					" select * from " + TableStructure.Music.TABLE_NAME
//							+ " order by " + MusicColumn.MUSIC_DATE_ADDED
//							+ " desc");
//		}
//		initView();
//	}

	/**
	 * 返回主界面
	 * 
	 * @author Erica
	 * */
	private void backToLastView() {
		Intent intent = new Intent(IntentAction.INTENT_LOCAL_ACTIVITY);
		intent.putExtra(BaseActivity.INTENT_ACTIVITY_NAME, "LocalActivity");
		int level = TianlApp.newInstance().getPageLevel();
		BaseActivity.newInstance().showActivity(intent,
				level - 1 == 0 ? 1 : level - 1);
	}

	@Override	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}

	@Override
	public void onResume() {
	    super.onResume();
		//友盟统计 日志 add by perry 2012-10-23
	   // MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
	    super.onPause();
		//友盟统计 日志 add by perry 2012-10-23
	   // MobclickAgent.onPause(this);
	}
}
