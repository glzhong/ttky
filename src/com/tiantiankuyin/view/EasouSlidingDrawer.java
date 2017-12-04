package com.tiantiankuyin.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SlidingDrawer;

public class EasouSlidingDrawer extends SlidingDrawer{
	 	/** 抽屉行为控件ID */
		private int mHandleId = 0;     
		/** Handle 部分其他控件ID */
	    private int[] mTouchableIds = null;    
	    
	    public int[] getTouchableIds() {
	        return mTouchableIds;
	    }

	    public void setTouchableIds(int[] mTouchableIds) {
	        this.mTouchableIds = mTouchableIds;
	    }

	    public int getHandleId() {
	        return mHandleId;
	    }

	    public void setHandleId(int mHandleId) {
	        this.mHandleId = mHandleId;
	    }

	    public EasouSlidingDrawer(Context context, AttributeSet attrs) {
	        super(context, attrs);
	    }
	    
	    public EasouSlidingDrawer(Context context, AttributeSet attrs, int defStyle){
	        super(context, attrs, defStyle);
	    }
	    /*
	     * 获取控件的屏幕区域
	     */
	    public Rect getRectOnScreen(View view){
	        Rect rect = new Rect();
	        int[] location = new int[2];
	        View parent = view;
	        if(view.getParent() instanceof View){
	            parent = (View)view.getParent();
	        }
	        parent.getLocationOnScreen(location);
	        view.getHitRect(rect);
	        rect.offset(location[0], location[1]);
	        
	        return rect;
	    }
	    
	    public boolean onInterceptTouchEvent(MotionEvent event) {
	    	   // 触摸位置转换为屏幕坐标
	        int[] location = new int[2];
	        int x = (int)event.getX();
	        int y = (int)event.getY();
	        this.getLocationOnScreen(location);
	        x += location[0];
	        y += location[1];
	        
	        // handle部分独立按钮
	        if(mTouchableIds != null){
	            for(int id : mTouchableIds){
	                View view = findViewById(id);
	                Rect rect = getRectOnScreen(view);
	                if(rect.contains(x,y)){
	                    //return 
	              //      boolean result = view.dispatchTouchEvent(event);
	              //      Log.i("MySlidingDrawer dispatchTouchEvent", "" + result);
	                    return false;
	                }
	            }
	        }
	        
	        // 抽屉行为控件
	        if(event.getAction() == MotionEvent.ACTION_DOWN && mHandleId != 0){
	            View view = findViewById(mHandleId);
	            Rect rect = getRectOnScreen(view);
	            if(rect.contains(x, y)){//点击抽屉控件时交由系统处理
	            	
	            }else{
	                return false;
	            }
	        }
	        return super.onInterceptTouchEvent(event);
	    }
	    
	    @Override
	    public boolean onTouchEvent(MotionEvent event) {
	        return super.onTouchEvent(event);
	    }



}
