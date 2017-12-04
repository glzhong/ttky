package com.tiantiankuyin.view;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.graphics.Region.Op;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

import com.tiantiankuyin.bean.Lyric;
import com.tiantiankuyin.bean.Sentence;
import com.tiantiankuyin.component.activity.local.PlayViewLrcActivity;
import com.tiantiankuyin.para.Env;
import com.tiantiankuyin.play.PlayLogicManager;
import com.tiantiankuyin.utils.CommonUtils;
import com.tiantiankuyin.utils.LyricViewOntouchListener;
import com.tiantiankuyin.R;
import com.tiantiankuyin.TianlApp;

/**
 * 滚动歌词对象
 * @author Erica
 */
public class LyricView extends TextView
{
	/** 绘制歌词画笔  */
	private Paint mPaint;
	/** 提示语绘制 X */
	private float mX;
	/** 提示语绘制 Y */
	private int mY;
	/** 歌词对象 */
	private static Lyric mLyric;
	/** 绘制拖动提示画笔对象 */
	private Paint mPathPaint1;
	/** 拖动索引 */
	public int index = 0;
	/** 当前显示时间 */
	private long mCurTime;
	/** 歌词列表对象 */
	private List<Sentence> list;
	/** 拖动位置 Y */
	public float mTouchHistoryY;
	/** 是否显示提示下划线  */
	private boolean show_lyric_lines;

	/** 居中控制 Y */
	private float middleY;
	/** 字符间距 */
	private int textSpan = 40;
	/** 歌词文字大小 */
	private int textSize ;
	/** 当前组件显示 Y */
	private int viewY;
	/** 屏幕 高 */
	private int viewH;
	/** 屏幕 宽 */
	private int viewW;
	/** 上一行 */
	private int upLine=0;
	/** 下一行 */
	private int downLine=0;
	/** 是否显示提示语 */
	boolean isShowPrompt = true;
	/** 是否显示线条 */
	public boolean isLine=false;
	/** 是否限制歌词行数 */
	public boolean isShowLines=false;
	
	/** 是否触控中  */
	boolean isTouch = false;
	/** 拖动提示显示时间计算量 */
	private long time2;
	/** 拖动控制行对象  */
	int mIndex=0;
	private LyricViewOntouchListener listener;//歌词滑动监听器
	
	public void setListener(LyricViewOntouchListener listener) {
		this.listener = listener;
	}
	/**
	 * 是否需要触控
	 */
	private boolean isNeedTouch;
	/*private EasouViewPager viewPager;*/
	/** 自定义构造函数  */
	public LyricView(Context context,boolean isShowPrompt)
	{
		super(context);
		this.isShowPrompt = isShowPrompt;
		init();
	}
	/** 自定义构造函数 */
	public LyricView(Context context, AttributeSet attr)
	{
		super(context, attr);	
		init();
	}
	/** 自定义构造函数 */
	public LyricView(Context context, AttributeSet attr, int i)
	{
		super(context, attr, i);	
		init();
	}
	
	/** 设置是否需要触控 */
	public void setNeedTouch(boolean isNeedTouch) {
		this.isNeedTouch = isNeedTouch;
	}
	/** 歌词是否显示下划线
	 * @return  isShowLines
	 * */
	public boolean isShowLines() {
		return isShowLines;
	}
	/** 设置歌词是否显示下划线 调用类对象时需要设置该函数
	 * @author Erica
	 * @param isShowLines boolean 
	 *  */
	public void setShowLines(boolean isShowLines) {
		this.isShowLines = isShowLines;
	}
	/** 当前歌词是否显示下划线*/
	public boolean isLine() {
		return isLine;
	}
	/** 设置当前突出显示歌词是否显示下划线
	 *  调用类对象时需要设置该函数
	 * @author Erica
	 * @param isShowLines boolean 
	 *  */
	public void setLine(boolean isLine) {
		this.isLine = isLine;
	}
	/** 获取歌词队列
	 *  @return list
	 *  */
	public List<Sentence> getList() {
		return list;
	}
	/** 设置歌词队列
	 * @param list List<Sentence>
	 *  */
	public void setList(List<Sentence> list) {
		this.list = list;
	}

	/** 设置歌词初始化属性项
	 * @param  _screenWidth int 屏幕宽
	 * @param _screenHeight int 屏幕高
	 * */

	private void initLyricComponent(int _screenWidth,int _screenHeight){
		viewH = _screenHeight;
		viewW = _screenWidth;

//		if (viewW <= 320) {
//			textSize = 15;
//			show_lyric_lines = true;
//		} else 
		if (viewW < 480) {
			textSize = 15;
			show_lyric_lines =true;
		} else if (viewW >= 480 && viewW < 720) {
			textSize = 22;
			show_lyric_lines = false;
		} else {// PAD
			textSize = 32;
			show_lyric_lines = false;
		}
	}
	
	/** 更变歌词                     
	 * @param path String 设置一个数据地址（歌词地址 ）
	 * */
	public void renewLrc(String path)
	{
		if(path == null)
		{
			return;
		}
		File mFile = new File(path);
		if(path!=null&&mFile.exists())
		{
			mLyric = new Lyric(new File(path));
			list = mLyric.list;
		}else
		{
			mLyric = null;
			list = null;
		}			
	}
	
	/** 销毁显示对象
	 *  */
	public void destroy()
	{
		if(mLyric!=null || list!=null)
		{
			mLyric = null;
			list = null;
		}
	}
		
	/** 初始化歌词界面显示所需对象 
	 *  */
	private void init()
	{
		setFocusable(true);
		this.setPadding(0, 100, 0, 100);
		
		int screen_W = Env.getScreenWidth();
		int screen_H = Env.getScreenHeight();
		
		if(screen_W != 0 && screen_H != 0){
			initLyricComponent(screen_W,screen_H);
		}else{
			initLyricComponent(480,800);
		}
		textSpan = viewH * 40/ 800;;
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setTextSize(textSize);
		mPaint.setColor(0xffbababa);
		mPaint.setTypeface(Typeface.SERIF);
		mPaint.setTextAlign(Paint.Align.CENTER);
		
		mPathPaint1 = new Paint();
		mPathPaint1.setAntiAlias(true);
		mPathPaint1.setColor(0xff699216);
		mPathPaint1.setTextSize(textSize);
		mPathPaint1.setTypeface(Typeface.SERIF);
		mPathPaint1.setTextAlign(Paint.Align.CENTER);

	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);	
		if(list == null ||(list!=null&&list.size() == 0)||mLyric==null )
		{
			if(isShowPrompt)
			{
				//如果没有找到歌词就居中显示提示语
				int height = getHeight();
				FontMetrics fontMetrics = mPathPaint1.getFontMetrics(); 
				// 计算文字高度 
				float fontHeight = fontMetrics.bottom - fontMetrics.top; 
				// 计算文字baseline 
				float textBaseY = height - (height - fontHeight) / 2 - fontMetrics.bottom;
				if(CommonUtils.isHasNetwork(TianlApp.newInstance())){//是否有网络
					if(PlayViewLrcActivity.instance!=null&&PlayViewLrcActivity.status==PlayViewLrcActivity.LRC_SEARCH_ING){
						canvas.drawText(getResources().getString(R.string.lrc_search_ing),mX, textBaseY, mPathPaint1);
					}else{
						canvas.drawText(getResources().getString(R.string.has_no_lrc),mX, textBaseY, mPathPaint1);
					}
				}else{//当前无网络
					canvas.drawText(getResources().getString(R.string.has_no_net),mX, textBaseY, mPathPaint1);
				}
				/*if(Contants.LRYIC_PATH_SEARCH_RESULT ==0){//正在搜索
					canvas.drawText(getResources().getString(R.string.searching_lyric),mX, textBaseY, mPathPaint1);
				}else if(Contants.LRYIC_PATH_SEARCH_RESULT ==1){//搜索失败
					if(CommonUtils.isHasNetwork(App.getInstance())){//是否有网络
						canvas.drawText(getResources().getString(R.string.no_lyric),mX, textBaseY, mPathPaint1);
					}else{
						canvas.drawText(getResources().getString(R.string.has_no_use_network),mX, textBaseY, mPathPaint1);
					}
				}*/
			}
			return ;
		}
		if(index >= list.size() ||index == -1)
		{
			return;
		}
		Sentence sentence = list.get(index);
		float total = sentence.getDuring();
		float per = (sentence.getToTime() + mLyric.getOffset() - mCurTime) / total;
		float curY = middleY + textSpan * per;
		String text = sentence.getContent();
		if (text != null && text.length() > 0)
		{
			Rect bound = new Rect();
			mPathPaint1.getTextBounds(text, 0, text.length(), bound);
			mPaint.setTextSize(textSize);
			mPathPaint1.setTextSize(textSize);
			
			if(isLine() && isTouch){
				mPaint.setTextAlign(Paint.Align.LEFT);
				//Log.e("mCurTime:" + mCurTime);
				canvas.drawText(CommonUtils.timeFormate((int)mCurTime), 10, middleY-1, mPaint);
				mPaint.setTextAlign(Paint.Align.CENTER);
				canvas.drawLine(0, middleY+20, viewW, middleY+20, mPathPaint1);
				canvas.drawText(text, mX, curY, mPathPaint1);
			}else{
				canvas.drawText(text, mX, curY, mPaint);
				canvas.drawText(text, mX, curY, mPathPaint1);
			}
		}
		canvas.clipRect(0, 0, canvas.getWidth(), viewY + viewH - 10, Op.REPLACE);
		
		float tempY = curY;
		for (int i = index - 1; i >= 0; i--)
		{
			mPaint.setTextSize(textSize-2);
			tempY = tempY - textSpan;
			if (tempY < 0)
			{
				break;
			}
			if(isShowLines && show_lyric_lines){
				canvas.drawText(list.get(i).getContent(), mX, tempY, mPaint);
				break;
			}else if(isShowLines && !show_lyric_lines){
				canvas.drawText(list.get(i).getContent(), mX, tempY, mPaint);
				upLine++;
				if(upLine == 2){
					upLine=0;
					break;
				}
			}else{
				canvas.drawText(list.get(i).getContent(), mX, tempY, mPaint);
			} 
		}

		tempY = curY;
		for (int i = index + 1; i < list.size(); i++)
		{
			mPaint.setTextSize(textSize-2);
			tempY = tempY + textSpan;
			if (tempY > mY)
			{
				break;
			}
			if(isShowLines && show_lyric_lines){//在限制歌词行数下的小屏显示三行
				canvas.drawText(list.get(i).getContent(), mX, tempY, mPaint);
				break;
			}else if(isShowLines && !show_lyric_lines){//在限制歌词行数下的大屏显示五行
				canvas.drawText(list.get(i).getContent(), mX, tempY, mPaint);
				downLine++;
				if(downLine == 2){
					downLine=0;
					break;
				}
			}else{
				canvas.drawText(list.get(i).getContent(), mX, tempY, mPaint);
			}
		}

	}
	
	/**
	 *设置组件的显示y坐标和高度
	 * @param y
	 * @param h
	 */
	public void setViewSize(int y, int h)
	{
		viewY = y;
		viewH = h;
	}
	/** 歌词文字大小变化处理
	 *  */
	protected void onSizeChanged(int w, int h, int ow, int oh)
	{
		super.onSizeChanged(w, h, ow, oh);
		mX = w * 0.5f; // remember the center of the screen
		mY = h;
		middleY = h * 0.3f;
	}

	/** 在播放时候，要更新歌词
	 *  */
	public long updateIndex(long time)
	{
		//Log.e("updateIndex:" + time + ", index:" + index);
		if(mLyric == null || list == null)
		{ 
			return 0;
		}
		if(!isTouch){
			index = mLyric.getNowSentenceIndex(time);
			mCurTime = time;
			if(index<0 || index>=list.size()-1){
				index =list.size()-1;	
//				return index;
			}
		//	Log.e("index:" + index);
			if(index < 0)
				return 0;
			Sentence sen = list.get(index);
			return sen.getDuring();
		}else{
			index = mLyric.getNowSentenceIndex(mCurTime);
			if(index != -1){
				Sentence sentence = list.get(index);
				return sentence.getDuring();
			}else{
				return list.size()-1;
			}
		}
		
	}
/*	public void setViewPager(ViewPager viewPager){
		this.viewPager=viewPager;
	}*/
	/** 更新当前歌词位置 
	 * */
	public int update(int arg){
		if(mLyric == null || list == null)
		{
			return 0;
		}
		mIndex=index+arg;
		if(mIndex<=0){
			mIndex=0;
		}else if(mIndex>=list.size()){
			mIndex=list.size()-1;
		}
		Sentence sen=list.get(mIndex);
		int time=(int)sen.getFromTime();
		return time;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(mLyric!=null&&isNeedTouch&&list!=null){
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				/*viewPager.setOnTouchIntercept(false);*/
				isTouch = true;
				time2 = mCurTime;
				mTouchHistoryY = event.getY();
				mIndex =  mLyric.getNowSentenceIndex(mCurTime);
				break;
			case MotionEvent.ACTION_MOVE:
				index = mLyric.getNowSentenceIndex(mCurTime);
				if(mIndex != index){
					time2 = mCurTime;
					mIndex = index;
					mTouchHistoryY = event.getY();
				}
				if(index<0 || index>=list.size()){
					index =list.size()-1;	
				}
				Sentence sentence = list.get(index);
				// 当前句子总计时间
				float total = sentence.getDuring();
				mCurTime = (long) (time2 +(mTouchHistoryY - event.getY())/(textSpan+0.0f)*total);
				//Log.e("ACTION_MOVE:" + mCurTime + ", Total:" + total + ",index:" + index);
				index = mLyric.getNowSentenceIndex(mCurTime);
				if(index<0 || index>=list.size()){
					index =list.size()-1;	

				}
				invalidate();
				break;
			case MotionEvent.ACTION_UP:
				/*viewPager.setOnTouchIntercept(true);*/
				if(mCurTime>list.get(list.size()-1).getFromTime())
					mCurTime = list.get(list.size()-1).getFromTime();
				if(mCurTime<list.get(0).getFromTime())
					mCurTime = list.get(0).getFromTime();
				PlayLogicManager.newInstance().seekTo((int)mCurTime);
				invalidate();
				//刷新seekbar
				listener.onProgressChagne(mCurTime);
				isTouch = false;
				break;
			case MotionEvent.ACTION_CANCEL:
				isTouch = false;
				break;
			}
		}
		return super.onTouchEvent(event);
	}
}