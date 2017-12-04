package com.tiantiankuyin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.tiantiankuyin.component.service.DownloadService;
import com.tiantiankuyin.notification.DownloadNotification;
import com.tiantiankuyin.para.Env;
import com.tiantiankuyin.para.SPHelper;
import com.tiantiankuyin.play.PlayLogicManager;
import com.tiantiankuyin.R;


public class GlobalReceiver {

	// 闹钟事件
	public static final String MUSIC_ALARM_ON = "com.android.alarmclock.alarmon";
	public static final String MUSIC_ALARM_OFF = "com.android.alarmclock.alarmoff";
	// 计时器事件
	public static final String MUSIC_TIMER_ON = "com.android.alarmclock.timeron";
	public static final String MUSIC_TIMER_OFF = "com.android.alarmclock.timeroff";

	private boolean phoneState = false;
	/**接电话后重新获取焦点*/
	private boolean resumeAfterCall;
	/**接电话中暂停*/
	private boolean pausedInCall;
	/**闹钟暂停*/
	private boolean isAlarmClockPause;
	/**计时器暂停*/
	private boolean isTimerClockPause;
	/** 是否已注册 wifi状态监听广播 */
	private boolean isWifiStateReceiverregist = false;

	private Context context;

	private TelephonyManager mTelephonyManager;
	public BroadcastReceiver wifiStateReceiver; // wifi状态监听广播

	private BroadcastReceiver alarmOnListener = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (PlayLogicManager.newInstance() != null
					&& PlayLogicManager.newInstance().getIsPlaying()) {
				PlayLogicManager.newInstance().pause();
				isAlarmClockPause = true;

			}
		}
	};

	private BroadcastReceiver alarmOffListener = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (PlayLogicManager.newInstance() != null
					&& PlayLogicManager.newInstance().getIsPlaying()
					&& isAlarmClockPause) {
				if (!phoneState) {
					PlayLogicManager.newInstance().play();
					isAlarmClockPause = false;
				}
			}
		}
	};

	private BroadcastReceiver timerOnListener = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (PlayLogicManager.newInstance() != null
					&& PlayLogicManager.newInstance().getIsPlaying()) {
				PlayLogicManager.newInstance().pause();
				isTimerClockPause = true;
			}
		}
	};

	private BroadcastReceiver timerOffListener = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (PlayLogicManager.newInstance() != null
					&& !PlayLogicManager.newInstance().getIsPlaying()
					&& isTimerClockPause) {
				if (!phoneState) {
					PlayLogicManager.newInstance().play();
					isTimerClockPause = false;
				}
			}
		}
	};

	private BroadcastReceiver shutdownListener = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (PlayLogicManager.newInstance() != null
					&& PlayLogicManager.newInstance().getIsPlaying()) {
				PlayLogicManager.newInstance().pause();
			}
		}
	};

	// 监听耳机事件
	private BroadcastReceiver mediaReceive = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent
					.getAction())) {
				if (SPHelper.newInstance().getEarOffPause()) {
					PlayLogicManager.newInstance().pause();
				}
			}
		}
	};

	private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
		public void onCallStateChanged(int state, String incomingNumber) {
			if (state == TelephonyManager.CALL_STATE_RINGING) {
				phoneState = true;
				resumeAfterCall = (PlayLogicManager.newInstance().getIsPlaying() || resumeAfterCall);
				PlayLogicManager.newInstance().pause();
				if (resumeAfterCall) {
					pausedInCall = true;
				}
			} else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
				phoneState = true;
				resumeAfterCall = (PlayLogicManager.newInstance().getIsPlaying() || resumeAfterCall);
				PlayLogicManager.newInstance().pause();
				if (resumeAfterCall) {
					pausedInCall = true;
				}
			} else if (state == TelephonyManager.CALL_STATE_IDLE) {
				phoneState = false;
				if (resumeAfterCall) {
					PlayLogicManager.newInstance().play();
					resumeAfterCall = false;
					pausedInCall = false;
				} else if (isAlarmClockPause) {
					PlayLogicManager.newInstance().play();
					isAlarmClockPause = false;
				} else if (isTimerClockPause) {
					PlayLogicManager.newInstance().play();
					isTimerClockPause = false;
				}
			}
		};
	};

	private void wifiReceiver(Intent intent) {
		if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
			NetworkInfo info = intent
					.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
			boolean alllowDownload = TianlApp.newInstance().isWifiDownloadLock();
			if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
				// 已经连接
				if (DownloadService.newInstance().binder != null
						&& alllowDownload) {
					DownloadService.newInstance().binder
							.startALlWifiDownloadTask();

					// 在系统状态栏中显示消息
					if (DownloadService.newInstance().binder
							.isDownloadingAppointmentMusic()) {
						DownloadNotification.getInstence().update(
								R.string.notification_download_music_content);
					}
				}
				Env.setWifiAvaliable(true);
				boolean lock = TianlApp.newInstance().isWifiDownloadLock();
				if (!lock) {
					DownloadService.newInstance().binder
							.startALlWifiDownloadTask();
					// 在系统状态栏中显示消息
					if (DownloadService.newInstance().binder
							.isDownloadingAppointmentMusic()) {
						DownloadNotification.getInstence().update(
								R.string.notification_download_music_content);
					}
				}
			} else if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
				// wifi断开连接
				if (DownloadService.newInstance().binder != null) {
					DownloadService.newInstance().binder
							.pauseAllWifiDownloadTask();
					// 取消在系统状态栏中显示消息
					DownloadNotification.getInstence().cancel();

				}
				Env.setWifiAvaliable(false);
			}
		} else if (intent.getAction().equals(
				WifiManager.WIFI_STATE_CHANGED_ACTION)) {
			int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
			if (wifiState == WifiManager.WIFI_STATE_DISABLED) {
				if (DownloadService.newInstance().binder != null) {
					DownloadService.newInstance().binder
							.pauseAllWifiDownloadTask();
					// 取消在系统状态栏中显示消息
					if (DownloadService.newInstance().binder
							.isDownloadingAppointmentMusic()) {
						DownloadNotification.getInstence().cancel();
					}
				}

				Env.setWifiAvaliable(false);
			}
		}
	}

	public void registeReceiver() {
		wifiStateReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, final Intent intent) {
				new Thread() {
					public void run() {
						wifiReceiver(intent);
					};
				}.start();
			}
		};

		IntentFilter wifiFilter = new IntentFilter();
		wifiFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
		wifiFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		wifiFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		context.registerReceiver(wifiStateReceiver, wifiFilter);
		isWifiStateReceiverregist = true;

		IntentFilter alarmOnFilter = new IntentFilter(MUSIC_ALARM_ON);
		context.registerReceiver(alarmOnListener, alarmOnFilter);
		IntentFilter alarmOffFilter = new IntentFilter(MUSIC_ALARM_OFF);
		context.registerReceiver(alarmOffListener, alarmOffFilter);

		IntentFilter timerOnFilter = new IntentFilter(MUSIC_TIMER_ON);
		context.registerReceiver(timerOnListener, timerOnFilter);
		IntentFilter timerOffFilter = new IntentFilter(MUSIC_TIMER_OFF);
		context.registerReceiver(timerOffListener, timerOffFilter);

		IntentFilter shutdownFilter = new IntentFilter(Intent.ACTION_SHUTDOWN);
		context.registerReceiver(shutdownListener, shutdownFilter);

		IntentFilter filter = new IntentFilter();
		filter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
		context.registerReceiver(mediaReceive, filter);

		//注册媒体打开
		IntentFilter mediaFilter = new IntentFilter();
		mediaFilter.addAction(Intent.ACTION_MEDIA_BUTTON);
		mediaFilter.setPriority(Integer.MAX_VALUE);// 设置优先级，优先级太低可能被阻碍，收不到信息。一般默认优先级为0，通话优先级为1，

		/*mTelephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		mTelephonyManager.listen(mPhoneStateListener,
				PhoneStateListener.LISTEN_CALL_STATE);*/
	}

	/**
	 * 注销广播
	 */
	public void closeReceiver() {
		boolean awaysRuningBackDownload = SPHelper.newInstance()
				.getAwaysRuningBackDownload();
		if (wifiStateReceiver != null && !awaysRuningBackDownload) {
			if (isWifiStateReceiverregist)
				context.unregisterReceiver(wifiStateReceiver); // 注销监听wifi广播
		}
	}
	
	
	public GlobalReceiver(Context context){
		this.context = context;
	}


}
