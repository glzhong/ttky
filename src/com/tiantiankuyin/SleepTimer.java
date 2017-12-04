package com.tiantiankuyin;

import java.util.Timer;
import java.util.TimerTask;

/** 睡眠定时
 * @author Perry */
public class SleepTimer {
	/**
	 * 睡眠定时Timer
	 */
	public Timer sleepTimer;
	/**
	 * 睡眠定时设定的分钟数 小于0 代表进入退出状态。 等于0 代表当前没有设定定时睡眠 大于0 代表当前设定的睡眠时间
	 */
	private int sleepTimerMinutes;

	/**
	 * 睡眠定时设定的分钟数 小于0 代表进入退出状态。 等于0 代表当前没有设定定时睡眠 大于0 代表当前设定的睡眠时间
	 */
	public int getSleepTimerMinutes() {
		return sleepTimerMinutes;
	}

	/**
	 * 睡眠定时设定的分钟数 小于0 代表进入退出状态。 等于0 代表当前没有设定定时睡眠 大于0 代表当前设定的睡眠时间
	 */
	public void setSleepTimerMinutes(int sleepTimerMinutes) {
		this.sleepTimerMinutes = sleepTimerMinutes;
	}

	/**
	 * 设置，并开启睡眠定时功能。
	 * @param minutes
	 * @author Erica edit by perry 2012-09-25 14:29:13
	 */
	public void setTimer(int minutes) {
		// 设置当前睡眠的时间
		sleepTimerMinutes = minutes;
		sleepTimer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				if (--sleepTimerMinutes <= 0) {
					// hasAlert = true; // 禁止预约提示
					TianlApp.newInstance().exit();// 退出客户端
				}
			}
		};
		// 设置当前睡眠的时间
		sleepTimer.schedule(task, 1000 * 60, 1000 * 60);


	}

	/**
	 * 取消睡眠定时
	 */
	public void cancelTimer() {
		if (sleepTimer != null) {
			sleepTimer.cancel();
			sleepTimer = null;
		}
		sleepTimerMinutes = 0;
	}

}
