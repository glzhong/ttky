package com.tiantiankuyin.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.tiantiankuyin.component.activity.DownloadActivity;
import com.tiantiankuyin.para.Constant;
import com.tiantiankuyin.para.IntentAction;
import com.tiantiankuyin.R;
import com.tiantiankuyin.TianlApp;

/**
 * 系统状态通知栏消息管理
 * 
 * @author looming
 * 
 */
public class DownloadNotification {
	private static final int NOTIFICATION_ID = Constant.NotificationId.DOWNLOAD;
	private PendingIntent mContentIntent;
	private Context mContext = TianlApp.newInstance();
	private Notification mNotification;
	private static DownloadNotification mInstence = new DownloadNotification();
	private NotificationManager notificationManager;
	
	private DownloadNotification() {
		notificationManager = (NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotification = new Notification(
					R.drawable.dialog_book_download_icon_img,
					mContext.getString(R.string.notification_download_music_appointment),
					System.currentTimeMillis());
		initContentIntent();
	}

	public static DownloadNotification getInstence() {
		return mInstence;
	};

	private void initContentIntent() {
		Intent intent = new Intent(IntentAction.INTENT_DOWNLOAD_ACTIVITY);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.setClass(mContext, DownloadActivity.class);
		mContentIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
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
			noticeTitle = mContext
					.getString(R.string.notification_download_music_appointment)
					+ " - "
					+ noticeTitle;
		}

		notificationManager.cancel(NOTIFICATION_ID);
		mNotification.flags |= Notification.FLAG_ONGOING_EVENT;
		mNotification.setLatestEventInfo(mContext, noticeTitle, noticeContent,
				mContentIntent);
		notificationManager.notify(NOTIFICATION_ID, mNotification);
	}

	public void update(String noticeContent) {
		//Easou.getNotificationManager().cancel(NOTIFICATION_ID);
		mNotification.flags |= Notification.FLAG_ONGOING_EVENT;
		mNotification.setLatestEventInfo(mContext, mContext.getResources()
				.getString(R.string.notification_download_music_appointment),
				noticeContent, mContentIntent);
		notificationManager.notify(NOTIFICATION_ID, mNotification);
	}

	public void update(int resId) {
		//Easou.getNotificationManager().cancel(NOTIFICATION_ID);
		mNotification.flags |= Notification.FLAG_ONGOING_EVENT;
		mNotification.setLatestEventInfo(mContext, mContext.getResources()
				.getString(R.string.notification_download_music_appointment),
				mContext.getString(resId), mContentIntent);
		notificationManager.notify(NOTIFICATION_ID, mNotification);
	}

	public void cancel() {
		notificationManager.cancel(NOTIFICATION_ID);
		notificationManager.cancel(1);
	}

}
