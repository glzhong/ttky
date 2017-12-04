package com.tiantiankuyin.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class LrcTimeLine extends FrameLayout {
	
	private boolean isShow = false;
	
	private String time;
	
	private int linePos;
	
	private int screenWidth;
	
	private Paint linePaint;
	
	private Paint fontPaint;
	
	private int fontSize = 12;

	public LrcTimeLine(Context context) {
		super(context);
		init();
	}

	public LrcTimeLine(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public LrcTimeLine(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public boolean isShow() {
		return isShow;
	}

	public void setShow(boolean isShow) {
		this.isShow = isShow;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	private void init() {
		setWillNotDraw(false);
		screenWidth = ((Activity) getContext()).getWindowManager()
				.getDefaultDisplay().getWidth();
		
		if (screenWidth < 480) {
			fontSize = 15;
		} else if (screenWidth >= 480 && screenWidth < 720) {
			fontSize = 22;
		} else {// PAD
			fontSize = 32;
		}
		
		linePos = getTop() + (getBottom() - getTop())/2;
		
		linePaint = new Paint();
		linePaint.setAntiAlias(true);
		linePaint.setColor(getContext().getResources().getColor(com.tiantiankuyin.R.color.lrc_height_light));
	    linePaint.setTextSize(fontSize);
		linePaint.setTypeface(Typeface.SERIF);
		linePaint.setTextAlign(Paint.Align.CENTER);
		
		fontPaint = new Paint();
		fontPaint.setAntiAlias(true);
		fontPaint.setColor(getContext().getResources().getColor(com.tiantiankuyin.R.color.lrc_time));
		fontPaint.setTextSize(fontSize);
		fontPaint.setTypeface(Typeface.SERIF);
		fontPaint.setTextAlign(Paint.Align.CENTER);
	}
	

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (isShow) {
			linePos = getTop() + (getBottom() - getTop())/2 - 30;
			canvas.drawText(time, 30, linePos-1, fontPaint);
			canvas.drawLine(0, linePos, screenWidth, linePos, linePaint);
		}
	}
	
}
