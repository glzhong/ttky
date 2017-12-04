package com.tiantiankuyin.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.tiantiankuyin.component.activity.BaseActivity;
import com.tiantiankuyin.para.Constant;
import com.tiantiankuyin.para.IntentAction;
import com.tiantiankuyin.R;
import com.tiantiankuyin.TianlApp;

/**
 * 音乐播放状态通知栏消息管理
 * 
 * @author looming
 * 
 */
public class PlayerNotification {
	public static final int NOTIFICATION_ID = Constant.NotificationId.PLAYER;
	private PendingIntent mContentIntent;
	private Context mContext = TianlApp.newInstance();
	private Notification mNotification;
	private static PlayerNotification mInstence = new PlayerNotification();
	private NotificationManager notificationManager;

	private PlayerNotification() {
		notificationManager = (NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
		initContentIntent();
	}

	public static PlayerNotification getInstence() {
		return mInstence;
	};

	private void initContentIntent() {
		Intent intent = new Intent(IntentAction.INTENT_BASE_ACTIVITY);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		intent.setClass(mContext, BaseActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		mContentIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
		mNotification = new Notification(R.drawable.icon48,
				mContext.getString(R.string.notification_title),
				System.currentTimeMillis());
	}

	/**
	 * 
	 * @param songName
	 * @param singerName
	 */
	public Notification setup() {
		mNotification.setLatestEventInfo(mContext,
				mContext.getString(R.string.notification_title), "",
				mContentIntent);
		notificationManager.notify(NOTIFICATION_ID, mNotification);
		return mNotification;
	}

	public void update(String noticeTitle, String noticeContent) {
		update(noticeTitle, noticeContent, false);
	}

	/**
	 * 
	 * @param noticeTitle
	 *            标题
	 * @param noticeContent
	 *            内容
	 * @param withAppName
	 *            是否包含应用名称，即“宜搜音乐”这几个字
	 */
	public void update(String noticeTitle, String noticeContent,
			boolean withAppName) {
		if (withAppName) {
			noticeTitle = mContext.getString(R.string.notification_title)
					+ " - " + noticeTitle;
		}

		mNotification.flags |= Notification.FLAG_ONGOING_EVENT;
		notificationManager.cancel(NOTIFICATION_ID);
		mNotification.setLatestEventInfo(mContext, noticeTitle, noticeContent,
				mContentIntent);
		notificationManager.notify(NOTIFICATION_ID, mNotification);
	}

	public void update(String noticeContent) {
		notificationManager.cancel(NOTIFICATION_ID);
		mNotification.setLatestEventInfo(mContext, mContext.getResources()
				.getString(R.string.notification_title), noticeContent,
				mContentIntent);
		notificationManager.notify(NOTIFICATION_ID, mNotification);
	}

	public void cancel() {
		notificationManager.cancel(NOTIFICATION_ID);
	}

	public Notification getNotification() {
		return mNotification;
	}

	public void setNotification(Notification notification) {
		this.mNotification = notification;
	}
	
	
}
