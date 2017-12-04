package com.tiantiankuyin.view;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Gallery;

/**
 * 本地扫描页-显示功能介绍图片
 * 
 * @author Perry
 * 
 */
public class LocalScanBannerGallery extends Gallery {

	private float myX1;
	private float myX2;

	private static final int MSG_SLIDE = 1;
	private static final long PERIOD = 4000; // 自动滑动的时间

	private Timer timer = new Timer();

	private TimerTask task = new TimerTask() {

		@Override
		public void run() {
			Message msg = Message.obtain();
			msg.what = MSG_SLIDE;
			handler.sendMessage(msg);
		}
	};

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_SLIDE:
				scrollToRight();
				break;

			default:
				break;
			}
		}

	};

	public LocalScanBannerGallery(Context context) {
		super(context);
	}
	
	public LocalScanBannerGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LocalScanBannerGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		if (isAutoSlideMode) {
			cancelAutoSlide();
		}
		myX1 = e.getRawX();
		return super.onDown(e);
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		myX2 = e2.getRawX();
		if (myX2 - myX1 < 0) {
			scrollToRight();
		} else if (myX2 - myX1 > 0) {
			scrollToLeft();
		}
		if (isAutoSlideMode) {
			startAutoSlide();
		}
		return false;
	}

	private void scrollToLeft() {
		onScroll(null, null, -1, 0);
		super.onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT, null);
	}

	private void scrollToRight() {
		onScroll(null, null, 1, 0); // 防止加了spacing之后onKeyDown无效
		onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);
	}

	public void cancelAutoSlide() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	private boolean isAutoSlideMode; // 当前Gallery是否是自动滚动模式

	public void startAutoSlide() {
		isAutoSlideMode = true;
		if (timer == null) {
			timer = new Timer();
			task = null;
			task = new TimerTask() {

				@Override
				public void run() {
					Message m = Message.obtain();
					m.what = MSG_SLIDE;
					handler.sendMessage(m);
				}
			};
		}
		timer.schedule(task, PERIOD, PERIOD);
	}
}
