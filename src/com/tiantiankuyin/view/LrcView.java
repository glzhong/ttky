package com.tiantiankuyin.view;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.tiantiankuyin.bean.Lyric;
import com.tiantiankuyin.bean.MusicInfo;
import com.tiantiankuyin.bean.Sentence;
import com.tiantiankuyin.play.PlayLogicManager;
import com.tiantiankuyin.utils.CommonUtils;
import com.tiantiankuyin.utils.Lg;
import com.tiantiankuyin.utils.LyricViewOntouchListener;

/**
 * 自定义歌词控件
 * 
 * @author Steven
 * 
 */
public class LrcView extends LinearLayout {

	/**
	 * 是否可以拖动歌词
	 */
	private boolean touchable = true;
	/**
	 * 是否是小屏模式
	 */
	private boolean isMiniModel = false;
	private Lyric lyricModel;
	private List<Sentence> list;
	private List<TextView> textList;
	// private final LayoutParams textParams = new LayoutParams(
	// ViewGroup.LayoutParams.FILL_PARENT, DY);

	private Scroller mScroller;

	private int index = 0;

	private float midHeight;
	private static int DY = 40; // 每一行的间隔
	private static final float fontSize = 16;

	private float mLastMotionX;
	private float mLastMotionY;

	private final static int MAX_SPEED = 2000;

	private VelocityTracker mVelocityTracker;

	/**
	 * 处理歌词移动一秒的距离
	 */
	private float lastY;

	private int screenWidth;

	private int screenHeight;

	private int curLine;

	/**
	 * 用于记录是否显示分隔线和时间
	 */
	boolean isTouch = false;

	/** 当前显示时间 */
	private long mCurTime;

	private OnLrcHeightLightListener lightListener;

	private MusicInfo musicInfo;

	private int heightLightTVHeight;

	private LyricViewOntouchListener listener;// 歌词滑动监听器

	private boolean hasLrc = false;

	public void setListener(LyricViewOntouchListener listener) {
		this.listener = listener;
	}

	public boolean isMiniModel() {
		return isMiniModel;
	}

	public void setMiniModel(boolean isMiniModel) {
		this.isMiniModel = isMiniModel;
	}

	public boolean isTouchable() {
		return touchable;
	}

	public void setTouchable(boolean touchable) {
		this.touchable = touchable;
	}

	/***
	 * 配合Scroller 进行动态滚动
	 */
	@Override
	public void computeScroll() {
		super.computeScroll();
		if (mScroller.computeScrollOffset()) {
			this.scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			this.postInvalidate();
		}
	}

	public LrcView(Context context) {
		super(context);
		init();
	}

	public LrcView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public void loading() {
		Lg.d("loading");
		hasLrc = false;
		removeAllViews();

		LayoutParams textParams = null;
		if (!isMiniModel) {
			textParams = new LayoutParams(LayoutParams.FILL_PARENT,
					screenHeight / 2);
		} else {
			textParams = new LayoutParams(LayoutParams.FILL_PARENT, 80);
		}

		TextView textView = new TextView(getContext());
		textView.setText("正在加载...");
		textView.setVisibility(VISIBLE);
		textView.setGravity(Gravity.CENTER);
		textView.setTextColor(getContext().getResources().getColor(
				com.tiantiankuyin.R.color.lrc_default));
		textView.setTextSize(fontSize);
		textView.setLayoutParams(textParams);
		addView(textView);
		measure(0, 0);
		
		if (getChildCount() > 0) {
			View v = getChildAt(0);
			v.setLayoutParams(textParams);
		}
	}

	public void searchFail() {
		Lg.d("searchFail");
		hasLrc = false;
		removeAllViews();

		LayoutParams textParams = null;
		if (!isMiniModel) {
			textParams = new LayoutParams(LayoutParams.FILL_PARENT,
					screenHeight / 2);
		} else {
			textParams = new LayoutParams(LayoutParams.FILL_PARENT, 80);
		}
		TextView textView = new TextView(getContext());
		textView.setText("没有搜索到对应歌词");
		textView.setVisibility(VISIBLE);
		textView.setGravity(Gravity.CENTER);
		textView.setTextColor(getContext().getResources().getColor(
				com.tiantiankuyin.R.color.lrc_default));
		textView.setTextSize(fontSize);
		textView.setLayoutParams(textParams);
		addView(textView);
		measure(0, 0);
		
		if (getChildCount() > 0) {
			View v = getChildAt(0);
			v.setLayoutParams(textParams);
		}
	}
	
	public void emptySD() {
		Lg.d("emptySD");
		hasLrc = false;
		removeAllViews();

		LayoutParams textParams = null;
		if (!isMiniModel) {
			textParams = new LayoutParams(LayoutParams.FILL_PARENT,
					screenHeight / 2);
		} else {
			textParams = new LayoutParams(LayoutParams.FILL_PARENT, 80);
		}
		TextView textView = new TextView(getContext());
		textView.setText("当前没有可用SD卡");
		textView.setVisibility(VISIBLE);
		textView.setGravity(Gravity.CENTER);
		textView.setTextColor(getContext().getResources().getColor(
				com.tiantiankuyin.R.color.lrc_default));
		textView.setTextSize(fontSize);
		textView.setLayoutParams(textParams);
		addView(textView);
		measure(0, 0);
		if (getChildCount() > 0) {
			View v = getChildAt(0);
			v.setLayoutParams(textParams);
		}
	}

	public void clear() {
		hasLrc = false;
		this.removeAllViews();
		textList.clear();
		this.list = null;
		this.scrollTo(0, 0);
		index = 0;
	}

	public OnLrcHeightLightListener getLightListener() {
		return lightListener;
	}

	public void setLightListener(OnLrcHeightLightListener lightListener) {
		this.lightListener = lightListener;
	}

	private void init() {
		screenWidth = ((Activity) getContext()).getWindowManager()
				.getDefaultDisplay().getWidth();

		screenHeight = ((Activity) getContext()).getWindowManager()
				.getDefaultDisplay().getHeight();

		LinearLayout.LayoutParams linerParams = new LinearLayout.LayoutParams(
				(int) (screenWidth * 0.7), ViewGroup.LayoutParams.FILL_PARENT);

		setWillNotDraw(false);
		this.setOrientation(LinearLayout.VERTICAL);
		linerParams.gravity = Gravity.CENTER;

		this.setLayoutParams(linerParams);

		/*
		 * linePaint = new Paint(); linePaint.setAntiAlias(true);
		 * linePaint.setColor(0xff699216); // linePaint.setTextSize(textSize);
		 * linePaint.setTypeface(Typeface.SERIF);
		 * linePaint.setTextAlign(Paint.Align.CENTER);
		 */
		textList = new ArrayList<TextView>();
		mScroller = new Scroller(this.getContext());

		DY = screenHeight / 20;
	}

	/**
	 * 初始化歌词
	 * 
	 * @param path
	 */
	public void setLyric(String path) {
		Lg.d("setLyric");
		if (!CommonUtils.isExternalStorageAvailable()) { // 外部存储器不可用
			emptySD();
			return;
		}
		lyricModel = new Lyric(new File(path));
		this.list = lyricModel.list;

		musicInfo = PlayLogicManager.newInstance().getMusicInfo();
		// 加载歌词时 最前面空出的空间
		LayoutParams spaceParams = null;
		if (!isMiniModel) {
			spaceParams = new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
					(int) (screenHeight / 2 - 100));
		} else {
			spaceParams = new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
					80);
		}

		// Log.d("midHeight:" + midHeight);
		
		//this.measure(0, 0);
		//Log.d("width:" +  getMeasuredWidth() + ", heigth:" + getMeasuredHeight());

		View spaceView = new View(getContext());
		spaceView.setLayoutParams(spaceParams);
		addView(spaceView);

		Paint temp = new Paint();
		temp.setTextSize(fontSize);
		for (Sentence sentence : this.list) {
			float len = temp.measureText(sentence.getContent());
			int rowCount = (int) Math.ceil(len / (screenWidth * 0.65));
			// Log.d("getContent:" + sentence.getContent() + ", Len:" + len);

			LayoutParams textParams = new LayoutParams(
					LayoutParams.FILL_PARENT, DY * rowCount);
			TextView textView = new TextView(getContext());
			textView.setText(sentence.getContent());
			textView.setVisibility(VISIBLE);
			textView.setLines(rowCount);
			textView.setGravity(Gravity.CENTER);
			textView.setTextColor(Color.parseColor("#ffbababa"));
			textView.setTextSize(fontSize);
			textView.setLayoutParams(textParams);
			textList.add(textView);
			addView(textView);
		}

		// 最后面空出的空间
		// spaceView = new View(layout.getContext());
		// spaceView.setLayoutParams(spaceParams);
		// layout.addView(spaceView);

		hasLrc = true;
		// measure(0, 0);
		
		if (getChildCount() > 0) {
			View v = getChildAt(0);
			v.setLayoutParams(spaceParams);
		}
		invalidate();
	
	}

	/**
	 * 更新歌词序列
	 * 
	 * @param time
	 * @return
	 */
	public void updateIndex(long time) {
		if (lyricModel == null || list == null || list.size() == 0 || isTouch)
			return;
		mCurTime = time;

		// 歌词序号
		int t = lyricModel.getNowSentenceIndex(time);
		if (index != t && t != -1 && textList.size() > 0) {
			// Log.d("t:" + t + ",index:" + index);
			TextView oldOne = textList.get(index);
			oldOne.setTextColor(getContext().getResources().getColor(
					com.tiantiankuyin.R.color.lrc_default));
			TextView newOne = textList.get(t);
			newOne.setTextColor(getContext().getResources().getColor(
					com.tiantiankuyin.R.color.lrc_height_light));
			int oldHeight = oldOne.getTop();
			index = t;

			TextView tv = textList.get(index);
			int tvHeight = tv.getMeasuredHeight();
			curLine = (int) (oldHeight + fontSize - midHeight + tvHeight / 2);
			mScroller.startScroll(this.getScrollX(), curLine, 0, tvHeight, 500);
			invalidate();
		}
	}

	private void updateHeightLightRow(long time, boolean isBack, int distance) {
		mCurTime = time;
		// 歌词序号
		int t = lyricModel.getNowSentenceIndex(time);
		if (index != t && t != -1) {
			//移动距离超过一行
			TextView oldOne = textList.get(index);
			oldOne.setTextColor(getContext().getResources().getColor(
					com.tiantiankuyin.R.color.lrc_default));
			TextView newOne = textList.get(t);
			newOne.setTextColor(getContext().getResources().getColor(
					com.tiantiankuyin.R.color.lrc_height_light));

			int oldHeight = oldOne.getTop();
			index = t;

			curLine = (int) (oldHeight + fontSize - midHeight + (isBack ? -1
					* distance : heightLightTVHeight) / 2);
			 //scrollBy(0, isBack ? -1 * distance :
			//	 heightLightTVHeight);
			mScroller.startScroll(this.getScrollX(), curLine, 0, isBack ? -1 * distance : heightLightTVHeight, 0);
		} else {
			//移动距离未超过一行
			scrollBy(0, isBack ? -1 * distance : distance);
			heightLightTVHeight -= distance;
		}
		invalidate();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		// Log.d("onSizeChanged: H " + h);
		midHeight = h * 0.5f;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!touchable) {
			return false;
		}

		int rs = event.getPointerCount();
		if (!hasLrc || rs != 1 || list.size() == 0) {
			return false;
		}

		if (lyricModel != null) {
			final float x = event.getX();
			final float y = event.getY();
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (mVelocityTracker == null) {
					mVelocityTracker = VelocityTracker.obtain();
				} else {
					mVelocityTracker.clear();
				}
				mVelocityTracker.addMovement(event);
				if (!mScroller.isFinished()) {
					mScroller.abortAnimation();
				}
				mLastMotionX = x;
				mLastMotionY = y;

				lastY = y;
				isTouch = true;

				break;
			case MotionEvent.ACTION_MOVE:
				int deltaX = (int) (mLastMotionX - x);
				int deltaY = (int) (mLastMotionY - y);
				mVelocityTracker.addMovement(event);
				mVelocityTracker.computeCurrentVelocity(1000);
				int velocityY = (int) mVelocityTracker.getYVelocity();

				// Log.d("velocityY:" + velocityY);

				// 限制只能拖到歌词末尾和开头、限制最快拖动速度1秒钟小于2000像素
				if ((Math.abs(deltaY) > 200 && Math.abs(deltaX) > 10)
						|| (musicInfo != null
								&& mCurTime > musicInfo.getDuration() && deltaY > 0)
						|| Math.abs(velocityY) > MAX_SPEED
						|| (mCurTime <= 0 && deltaY < 0))
					break;
				
				mLastMotionY = y;
				mLastMotionX = x;

				if (lightListener != null) {
					lightListener.showLine(CommonUtils
							.timeFormate((int) mCurTime));
				}

				int disY = (int) (lastY - y);

				if (index >= list.size() || index < 0) {
					break;
				}
				long during = list.get(index).getDuring();

				TextView tv = textList.get(index);
				if (during != 0) {
					int tvHeight = tv.getMeasuredHeight();
					
					//+0.5 处理第秒移动的距离太小的问题   不至于distancePer的值为0
					int distancePer = (int)Math.round((((double)tvHeight * 1000) / during) + 0.5);
					heightLightTVHeight = tvHeight;

					// 每次移动超过了1秒的距离
					if (Math.abs(disY) >= distancePer && distancePer != 0) {
						int bs = (int) Math.abs(Math.floor(disY / distancePer));

						// 处理快速滑动的情况
						for (int i = 0; i < bs; i++) {
							if (disY >= 0) {
								mCurTime += 1000L;
								if(lrcDragProtect()){
									updateHeightLightRow(mCurTime, false,
											0);
									break;
								}
								updateHeightLightRow(mCurTime, false,
										distancePer);
							} else {
								mCurTime -= 1000l;
								if(lrcDragProtect()){
									updateHeightLightRow(mCurTime, false,
											0);
									break;
								}
								updateHeightLightRow(mCurTime, true,
										distancePer);
							}
							lastY = y;
						}
					}
				}
				break;
			case MotionEvent.ACTION_UP:
				isTouch = false;
				PlayLogicManager.newInstance().seekTo((int) mCurTime);
				lightListener.hide();
				// 刷新seekbar
				listener.onProgressChagne(mCurTime);
				if (mVelocityTracker != null) {
					mVelocityTracker.recycle();
					mVelocityTracker = null;
				}
				break;
			case MotionEvent.ACTION_CANCEL:
				isTouch = false;
				lightListener.hide();
//				System.out.println("ACTION_CANCEL");
				break;
			}
		}
		return true;
	}

	private boolean lrcDragProtect() {
		if (mCurTime < 0) {
			mCurTime = 0;
			return true;
		}
		//减1001，是为了使播放引擎seekto不跳转到歌词末尾，而停止播放。这样播放逻辑管理，可在收到自然播放结束后，执行跳转下一首的逻辑
		if (mCurTime > musicInfo.getDuration()-1001) {
			mCurTime = musicInfo.getDuration()-1001;
			return true;
		}
		return false;
	}

	/**
	 * 处理拖动提示线
	 * 
	 * @author Steven
	 * 
	 */
	public interface OnLrcHeightLightListener {
		// 显示提示线
		public void showLine(String time);

		// 隐藏提示线
		public void hide();
	}
}
