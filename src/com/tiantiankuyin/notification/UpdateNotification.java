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
 * 升级状态通知栏消息管理
 * 
 * @author looming
 * 
 */
public class UpdateNotification {
	private static final int NOTIFICATION_ID = Constant.NotificationId.UPDATE;
	private PendingIntent mContentIntent;
	private Context mContext = TianlApp.newInstance();
	private Notification mNotification;
	private static UpdateNotification mInstence = new UpdateNotification();
	private NotificationManager notificationManager;

	private UpdateNotification() {
		notificationManager = (NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
		initContentIntent();
	}

	public static UpdateNotification getInstence() {
		return mInstence;
	};

	private void initContentIntent() {
		int icon = R.drawable.notification_start;
		Intent intent = new Intent(IntentAction.INTENT_BASE_ACTIVITY);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		intent.setClass(mContext, DownloadActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		mContentIntent = PendingIntent.getActivity(mContext, 0,
				intent, 0);
		mNotification = new Notification(icon, "",
				System.currentTimeMillis());
	}


	public void setup(String noticeTitle) {
		mNotification.setLatestEventInfo(mContext,noticeTitle, "",
				mContentIntent);
		notificationManager.notify(NOTIFICATION_ID, mNotification);
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
	 *          
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
	
	public void updateNotify(int current, int total) {
		mNotification.contentView.setProgressBar(R.id.downpb, (int) total,
				(int) current, false);
		if (total != 0) {
			if ((current * 100 / total) % 100 == 10)
				mNotification.contentView.setTextViewText(R.id.downCount, current
						* 100 / total + "%");
		}
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
}
