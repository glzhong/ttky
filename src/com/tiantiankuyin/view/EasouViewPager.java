package com.tiantiankuyin.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class EasouViewPager extends ViewPager {
	private boolean flag=false;
	public EasouViewPager(Context context) {
        super(context);
    }
    
    public EasouViewPager(Context context, AttributeSet attrs) {
    	super(context, attrs);
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
    	 if(flag){
             return super.onInterceptTouchEvent(arg0);
	     }else{
	             return false;
	     }
    }
    public void setOnTouchIntercept(boolean flag){
    	this.flag=flag;
    }
}
