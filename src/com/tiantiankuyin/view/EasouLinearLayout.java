package com.tiantiankuyin.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import com.tiantiankuyin.component.activity.local.PlayViewMainActivity;

public class EasouLinearLayout extends LinearLayout {

	public EasouLinearLayout(Context context) {
		super(context);
	}
	
	public EasouLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		PlayViewMainActivity.viewPager.requestDisallowInterceptTouchEvent(true);
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		PlayViewMainActivity.viewPager.requestDisallowInterceptTouchEvent(true);
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		PlayViewMainActivity.viewPager.requestDisallowInterceptTouchEvent(true);
		return super.onTouchEvent(event);
	}
}
