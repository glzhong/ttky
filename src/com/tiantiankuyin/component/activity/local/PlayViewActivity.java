package com.tiantiankuyin.component.activity.local;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.tiantiankuyin.bean.MusicInfo;
import com.tiantiankuyin.component.activity.local.PlayViewMainActivity.MainPagerListener;
import com.tiantiankuyin.component.activity.local.PlayViewMainActivity.PlayButtonListener;
import com.tiantiankuyin.component.activity.local.PlayViewSimilarActivity.MyPagerListener;
import com.tiantiankuyin.para.IntentAction;
import com.tiantiankuyin.para.SPHelper;
import com.tiantiankuyin.play.PlayLogicManager;
import com.tiantiankuyin.utils.CommonUtils;
import com.tiantiankuyin.utils.LyricViewOntouchListener;
import com.tiantiankuyin.utils.SearchPictureAndLrcManager;
import com.tiantiankuyin.view.EasouViewPager;
//import com.umeng.analytics.MobclickAgent;
import com.tiantiankuyin.component.service.IRemotePlayerListener;
import com.tiantiankuyin.R;

/**
 * 播放页界面
 * @author andrew
 * 
 */
public class PlayViewActivity extends Activity {
	public static final String ACTIVITY_ID = "PlayViewActivity";
	/** 分页控件 */
	private EasouViewPager mPager; 
	/** 分页内容集合 */
	private List<View> listViews; 
	private MyPagerAdapter pageAdapter;
	private LocalActivityManager mLocalActivityManager;
	private CancelNetMusicBroadCastReciever cancelNetMusicBroadCastReciever;
	private LinearLayout waitingPB;
	/** 播放主界面view */
	private View main; 
	/** 播放相似歌曲view */
	private View similar; 
	/** 歌词详情页 */
	private View lrc; 
	public static PlayViewActivity instance;
	/** 播放控制条上一首 */
	private ImageButton play_view_btn_prew;
	/** 播放控制条开始播放和暂停播放 */
	private ImageButton play_view_btn_start; 
	/** 播放控制条下一首 */
	private ImageButton play_view_btn_next; 
	/** 播放进度控制条 */
	private SeekBar seekBar; 
	/** 当前播放时间 */
	private TextView currentTime; 
	/** 歌曲总时长 */
	private TextView totalTime; 
	/** 导航点第一个点  */
	private ImageView page_one; 
	/** 导航点第二个点 */
	private ImageView page_two; 
	/** 导航点第三个点 */
	private ImageView page_three; 
	/** 在相似歌曲页面 */
	public static final int SIMILAR_PAGE = 1; 
	/** 在播放主界面 */
	public static final int MAIN_PAGE = 2; 
	/** 在歌词详情页 */
	public static final int LRC_PAGE = 3; 
	public static int page;
	public MyPagerListener pageListener;
	public PlayButtonListener buttonListener;
	public ImageView play_view_btn_prew_un;
	public ImageView play_view_btn_next_un;
	public ImageView play_view_btn_start_un;
	
	public boolean isStoping = false;
	public Handler handler = new Handler();
	
	private OnSeekBarChangeListener seekBarChangeListener = new OnSeekBarChangeListener() {
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// 拖动歌曲
			PlayLogicManager.newInstance().seekTo(seekBar.getProgress());
			PlayLogicManager.newInstance().setDragingSeekBar(false);
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			PlayLogicManager.newInstance().setDragingSeekBar(true);
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// 暂停的时候刷新 时间
			seekToFreshTime(seekBar.getProgress());
		}
	};
	
	public void setButtonListener(PlayButtonListener buttonListener) {
		this.buttonListener = buttonListener;
	}

	public MainPagerListener mainListener;

	public void setMainListener(MainPagerListener mainListener) {
		this.mainListener = mainListener;
	}

	public void setPageListener(MyPagerListener pageListener) {
		this.pageListener = pageListener;
	}

	private void seekToFreshTime(int progress) {
		String value = CommonUtils.timeFormate(progress);// 设置当前时间
		currentTime.setText(value);
		// 刷新mini歌词
		if (PlayViewMainActivity.instance != null
				&& PlayViewMainActivity.instance.getMini_lyric() != null) {
			PlayViewMainActivity.instance.getMini_lyric().updateIndex(progress);
			PlayViewMainActivity.instance.getMini_lyric().invalidate();
		}
		// 刷新歌词详情页歌词
		if (PlayViewLrcActivity.instance != null
				&& PlayViewLrcActivity.instance.getPlayview_detail_lyric() != null) {
			PlayViewLrcActivity.instance.getPlayview_detail_lyric()
					.updateIndex(progress);
			PlayViewLrcActivity.instance.getPlayview_detail_lyric()
					.invalidate();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (SPHelper.newInstance().getLrcScreenOn()) {// 根据设置需求设置歌词也常亮
			getWindow()
					.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}
		instance = this;

		PlayLogicManager.newInstance().addObserver(
				PlayerListener.class.toString(), new PlayerListener());// 注册播放引擎观察者

		setContentView(R.layout.local_play_view);
		mLocalActivityManager = new LocalActivityManager(this, true);
		initView();
		initPlayControlView();// 初始化播放控制条view
		initPlayButton();
		register();
	}

	/**
	 * 初始化播放按钮
	 */
	private void initPlayButton() {
		MusicInfo musicInfo = PlayLogicManager.newInstance().getMusicInfo();// 获取当前正在播放的歌曲
		if (musicInfo != null) {
			play_view_btn_prew_un.setVisibility(View.GONE);
			play_view_btn_next_un.setVisibility(View.GONE);
			play_view_btn_start_un.setVisibility(View.GONE);
			play_view_btn_next.setVisibility(View.VISIBLE);
			play_view_btn_prew.setVisibility(View.VISIBLE);
			play_view_btn_start.setVisibility(View.VISIBLE);
		} else {
			play_view_btn_prew_un.setVisibility(View.VISIBLE);
			play_view_btn_next_un.setVisibility(View.VISIBLE);
			play_view_btn_start_un.setVisibility(View.VISIBLE);
			play_view_btn_next.setVisibility(View.GONE);
			play_view_btn_prew.setVisibility(View.GONE);
			play_view_btn_start.setVisibility(View.GONE);
		}
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		mPager = (EasouViewPager) findViewById(R.id.vPager);
		mPager.setOnTouchIntercept(true);
		listViews = new ArrayList<View>();
		page_one = (ImageView) findViewById(R.id.page_one);
		page_two = (ImageView) findViewById(R.id.page_two);
		page_three = (ImageView) findViewById(R.id.page_three);
		play_view_btn_prew_un = (ImageView) findViewById(R.id.play_view_btn_prew_un);
		play_view_btn_next_un = (ImageView) findViewById(R.id.play_view_btn_next_un);
		play_view_btn_start_un = (ImageView) findViewById(R.id.play_view_btn_start_un);
	}

	/**
	 * 初始化当前播放信息
	 */
	private void initPlayInfo() {
		MusicInfo currentMusicInfo = PlayLogicManager.newInstance()
				.getMusicInfo();
		if (currentMusicInfo != null) {// 当前是否存在播放的歌曲 如果存在就设置信息
			seekBar.setMax((int) currentMusicInfo.getDuration());// 设置seekbar最大值
			seekBar.setSecondaryProgress(0);
			String value = CommonUtils.timeFormate((int) currentMusicInfo
					.getDuration());// 转换时间
			totalTime.setText(value);// 设置最大时间
		}
	}

	/**
	 * 刷新播放进度条
	 */
	private void freshPlaySeekbar(int currentMilliseconds) {
		if (seekBar != null) {
			seekBar.setProgress(currentMilliseconds);// 刷新 设置当前进度
			String value = CommonUtils.timeFormate(currentMilliseconds);// 设置当前时间
			currentTime.setText(value);// 设置当前时间
			
			//若当前时间没有，则重新获取时间
			if(seekBar.getMax() == 0) {
				int duration = PlayLogicManager.newInstance().getDuration();
				if (duration != 0) {
					PlayLogicManager.newInstance().getmCurMusic().setDuration(duration);
					seekBar.setMax(duration);
					String totalValue = CommonUtils.timeFormate(duration);// 转换时间
					totalTime.setText(totalValue);// 设置最大时间
				}
			}
		}
	}

	class MyLyricViewOntouchListener implements LyricViewOntouchListener {
		@Override
		public void onProgressChagne(long currentTime) {
			freshPlaySeekbar((int) currentTime);
		}
	}

	/**
	 * 初始化播放页播放控制条view
	 */
	private void initPlayControlView() {
		play_view_btn_prew = (ImageButton) findViewById(R.id.play_view_btn_prew);
		play_view_btn_start = (ImageButton) findViewById(R.id.play_view_btn_start);
		play_view_btn_next = (ImageButton) findViewById(R.id.play_view_btn_next);
		waitingPB = (LinearLayout) findViewById(R.id.waiting);
		waitingPB.setOnClickListener(waitingListener);
		if (PlayLogicManager.newInstance().getIsPlaying()) {// 正在播放
			play_view_btn_start
					.setImageResource(R.drawable.playview_pause_btn_click);// 设置背景为暂停
		} else {
			play_view_btn_start
					.setImageResource(R.drawable.playview_play_btn_click);// 设置背景为开始播放
		}
		play_view_btn_prew.setOnClickListener(listener);// 设置点击事件
		play_view_btn_start.setOnClickListener(listener);// 设置点击事件
		play_view_btn_next.setOnClickListener(listener);// 设置点击事件
		seekBar = (SeekBar) findViewById(R.id.seekBar);
		seekBar.setOnSeekBarChangeListener(seekBarChangeListener);
		currentTime = (TextView) findViewById(R.id.currentTime);
		totalTime = (TextView) findViewById(R.id.totalTime);
	}

	private View.OnClickListener waitingListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			sendBroadcast(new Intent(
					IntentAction.INTENT_BROADCAST_CANLE_NET_MUSIC));
		}
	};


	private void register() {
		cancelNetMusicBroadCastReciever = new CancelNetMusicBroadCastReciever();
		IntentFilter filterCancel = new IntentFilter();
		filterCancel.addAction(IntentAction.INTENT_BROADCAST_CANLE_NET_MUSIC);
		this.registerReceiver(cancelNetMusicBroadCastReciever, filterCancel);
	}

	private void unRegister() {
		if (cancelNetMusicBroadCastReciever != null) {
			unregisterReceiver(cancelNetMusicBroadCastReciever);
		}
	}

	@Override
	protected void onDestroy() {
		unRegister();
		super.onDestroy();
	}

	class CancelNetMusicBroadCastReciever extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			waitingClick();
		}
	}

	public void waitingClick() {
		isStoping = true;
		waitingPB.setVisibility(View.GONE);
		play_view_btn_start.setVisibility(View.VISIBLE);
		play_view_btn_start
				.setImageResource(R.drawable.playview_play_btn_click);
	}

	private View.OnClickListener listener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (buttonListener != null) {// 监听播放 button点击事件
				buttonListener.onButtonClick();
			}
			if (v.getId() == R.id.play_view_btn_prew) {// 播放上一首
				try {
					PlayLogicManager.newInstance().prev();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
				}
			} else if (v.getId() == R.id.play_view_btn_start) {// 开始播放和暂停播放
				if (PlayLogicManager.newInstance().getIsPlaying()) {
					try {
						PlayLogicManager.newInstance().pause();
						play_view_btn_start
								.setImageResource(R.drawable.playview_play_btn_click);// 如果是暂停状态设置背景为开始播放
					} catch (Exception e) {
						// TODO Auto-generated catch block
						// e.printStackTrace();
					}
				} else {
					try {
						boolean isSuccess = PlayLogicManager.newInstance()
								.play();
						if (isSuccess) {
							play_view_btn_start
									.setImageResource(R.drawable.playview_pause_btn_click);// 如果是播放状态设置背景为暂停
						}
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						// e.printStackTrace();
					}
				}
			} else if (v.getId() == R.id.play_view_btn_next) {// 播放下一首
				try {
					PlayLogicManager.newInstance().next(true);
				} catch (IllegalStateException e) {
					// e.printStackTrace();
				} catch (IOException e) {
					// e.printStackTrace();
				}
			}
		}
	};


	/**
	 * 初始化viewpager
	 */
	private void init() {
		if (listViews.size() <= 0) {
			if (similar != null) {
				listViews.add(similar);
			}
			if (main != null) {
				listViews.add(main);
			}
			if (lrc != null) {
				listViews.add(lrc);
			}
			pageAdapter = new MyPagerAdapter(listViews);
			mPager.setAdapter(pageAdapter);
			mPager.setCurrentItem(1);// 默认显示播放页中间那个
			mPager.setOnPageChangeListener(new MyOnPageChangeListener());
			if (PlayViewLrcActivity.instance != null
					&& PlayViewLrcActivity.instance.getPlayview_detail_lyric() != null) {// 设置歌词监听器
				PlayViewLrcActivity.instance.getPlayview_detail_lyric()
						.setListener(new MyLyricViewOntouchListener());
			}
		}
	}

	public EasouViewPager getmPager() {
		return mPager;
	}

	/**
	 * 把相似歌曲界面 ，播放主界面，歌词详情界面 三个activity转换成view
	 */
	private void getActivityToView() {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		if (main == null) {// 播放主页面
			Intent intent = new Intent(this, PlayViewMainActivity.class);
			Window subActivity = mLocalActivityManager.startActivity(
					PlayViewMainActivity.ACTIVITY_ID, intent);
			main = subActivity.getDecorView();
			main.setLayoutParams(params);
		}
		if (similar == null) {// 播放主页面
			Intent intent = new Intent(this, PlayViewSimilarActivity.class);
			Window subActivity = mLocalActivityManager.startActivity(
					PlayViewSimilarActivity.ACTIVITY_ID, intent);
			similar = subActivity.getDecorView();
			similar.setLayoutParams(params);
		}
		if (lrc == null) {// 播放歌词详情页面
			Intent intent = new Intent(this, PlayViewLrcActivity.class);
			Window subActivity = mLocalActivityManager.startActivity(
					PlayViewLrcActivity.ACTIVITY_ID, intent);
			lrc = subActivity.getDecorView();
			lrc.setLayoutParams(params);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 友盟统计 日志 add by perry 2012-10-23
		//MobclickAgent.onResume(this);
		mLocalActivityManager.dispatchResume();
		getActivityToView();
		init();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 友盟统计 日志 add by perry 2012-10-23
		//MobclickAgent.onPause(this);
		mLocalActivityManager.dispatchPause(isFinishing());
	}

	/**
	 * 滑动到第一页 导航点显示情况
	 */
	private void switchFirstTipPoint() {
		page_one.setImageResource(R.drawable.playview_point_img_bright);
		page_two.setImageResource(R.drawable.playview_point_img_default);
		page_three.setImageResource(R.drawable.playview_point_img_default);
	}

	/**
	 * 滑动到第二页 导航点显示情况
	 */
	private void switchSecondTipPoint() {
		page_one.setImageResource(R.drawable.playview_point_img_default);
		page_two.setImageResource(R.drawable.playview_point_img_bright);
		page_three.setImageResource(R.drawable.playview_point_img_default);
	}

	/**
	 * 滑动到第三页 导航点显示情况
	 */
	private void switchThirdTipPoint() {
		page_one.setImageResource(R.drawable.playview_point_img_default);
		page_two.setImageResource(R.drawable.playview_point_img_default);
		page_three.setImageResource(R.drawable.playview_point_img_bright);
	}

	/**
	 * 页卡切换监听
	 */
	public class MyOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int arg0) {
			switch (arg0) {
			case 0:// 第一页 换导航点
				page = SIMILAR_PAGE;
				switchFirstTipPoint();
				if (pageListener != null) {
					pageListener.onPage(0);
				}
				if (mainListener != null) {
					mainListener.onPage(0);
				}
				break;
			case 1:// 翻页到第二页
				page = MAIN_PAGE;
				switchSecondTipPoint();
				if (pageListener != null) {
					pageListener.onPage(1);
				}
				if (mainListener != null) {
					mainListener.onPage(1);
				}
				break;
			case 2:// 翻页到第三页
				page = LRC_PAGE;
				if (pageListener != null) {
					pageListener.onPage(2);
				}
				if (mainListener != null) {
					mainListener.onPage(2);
				}
				switchThirdTipPoint();
				break;
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

	/**
	 * ViewPager适配器
	 */
	public class MyPagerAdapter extends PagerAdapter {
		public List<View> mListViews;

		public MyPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(mListViews.get(arg1));
		}

		@Override
		public void finishUpdate(View arg0) {
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(mListViews.get(arg1), 0);
			return mListViews.get(arg1);
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}
	}

	public class PlayerListener extends IRemotePlayerListener.Stub {
		@Override
		public void onError(int what, int extra) {
			// TODO Auto-generated method stub

		}

		private void cancelLoading() {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					waitingPB.setVisibility(View.GONE);
					play_view_btn_start.setVisibility(View.VISIBLE);
					play_view_btn_start
							.setImageResource(R.drawable.playview_play_btn_click);
				}
			});
		}

		@Override
		public void onPrepared() {
			if (isStoping) {// 已经取消了网络加载 暂停该次播放
				PlayLogicManager.newInstance().stop();
				isStoping = false;
				cancelLoading();// 取消加载恢复按钮
				return;
			}
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					waitingPB.setVisibility(View.GONE);
					play_view_btn_start.setVisibility(View.VISIBLE);
					play_view_btn_start
							.setImageResource(R.drawable.playview_pause_btn_click);
				}
			});

		}

		@Override
		public void onStartBuffer() {

		}

		private void freshSencondProgress(int percent) {
			if (seekBar != null) {
				seekBar.setSecondaryProgress(percent * seekBar.getMax() / 100);
			}
		}

		@Override
		public void onBufferingUpdate(final int percent) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					freshSencondProgress(percent);
				}
			});
		}

		@Override
		public void onStartPlay() {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					initPlayInfo();// 重新初始化播放信息
					play_view_btn_start
							.setImageResource(R.drawable.playview_pause_btn_click);// 设置背景为暂停
					initPlayButton();
				}
			});
		}

		@Override
		public void onMusicPause() {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					play_view_btn_start
							.setImageResource(R.drawable.playview_play_btn_click);// 设置背景为暂停
				}
			});
		}

		@Override
		public void onProgressChanged(final int currentMilliseconds) {
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					freshPlaySeekbar(currentMilliseconds);// 刷新进度条
				}
			});
		}

		@Override
		public void onCompletion() {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					play_view_btn_start
							.setImageResource(R.drawable.playview_play_btn_click);// 设置背景为暂停
				}
			});
		}

		@Override
		public void onMusicStop() {

		}

		@Override
		public void onBufferComplete() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onBuffer() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onCacheUpdate(long currentCache) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPreparing() {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					waitingPB.setVisibility(View.VISIBLE);
					play_view_btn_start.setVisibility(View.GONE);
					currentTime.setText("00:00");
					totalTime.setText("00:00");
					seekBar.setMax(0);
					seekBar.setSecondaryProgress(0);
					seekBar.setProgress(0);
				}
			});

		}
	}

	public void sendMessage(int flag, int value) {
		Message msg = new Message();
		msg.what = flag;
		if (value > 0) {
			msg.arg1 = value;
		}
		handler.sendMessage(msg);
	}

	public void initViewAndData() {
		currentTime.setText("00:00");
		MusicInfo currentMusicInfo = PlayLogicManager.newInstance()
				.getMusicInfo();
		if (currentMusicInfo != null) {// 当前是否存在播放的歌曲 如果存在就设置信息
			String value = CommonUtils.timeFormate((int) currentMusicInfo
					.getDuration());// 转换时间
			totalTime.setText(value);// 设置最大时间
			seekBar.setMax((int) currentMusicInfo.getDuration());// 设置seekbar最大值
		} else {
			totalTime.setText("00:00");// 设置最大时间
			seekBar.setMax(0);// 设置seekbar最大值
		}
		seekBar.setSecondaryProgress(0);
		seekBar.setProgress(0);
		MusicInfo musicInfo = PlayLogicManager.newInstance().getMusicInfo();// 获取当前正在播放的歌曲
		if (PlayViewMainActivity.instance != null
				&& PlayViewMainActivity.instance.getMini_lyric() != null) {
			PlayViewMainActivity.instance.getMini_lyric().clear();
			if (musicInfo != null) {
				SearchPictureAndLrcManager.getInstance().getLrcUrlFromNet(
						PlayViewMainActivity.instance,
						PlayViewMainActivity.instance, musicInfo.getTitle(),
						musicInfo.getArtist(), musicInfo.getLyricId());
			}
		}
		if (PlayViewLrcActivity.instance != null
				&& PlayViewLrcActivity.instance.getPlayview_detail_lyric() != null) {
			PlayViewLrcActivity.instance.getPlayview_detail_lyric().clear();
			PlayViewLrcActivity.instance.init();
		}
		if (PlayViewMainActivity.instance != null
				&& PlayViewMainActivity.instance.getSinger_image() != null) {
			PlayViewMainActivity.instance.getSinger_image().setImageDrawable(
					getResources().getDrawable(
							R.drawable.playview_main_singer_img));
			if (musicInfo != null) {
				SearchPictureAndLrcManager.getInstance()
						.getSingerImageUrlFromNet(
								PlayViewMainActivity.instance,
								PlayViewMainActivity.instance,
								musicInfo.getArtist(), musicInfo.getAlbum());
			}
		}
	}
}
