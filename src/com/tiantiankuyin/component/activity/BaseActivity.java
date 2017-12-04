package com.tiantiankuyin.component.activity;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cmsc.cmmusic.init.InitCmmInterface;
import com.tiantiankuyin.bean.MusicInfo;
import com.tiantiankuyin.bean.Update;
import com.tiantiankuyin.component.activity.local.PlayViewActivity;
import com.tiantiankuyin.component.activity.local.PlayViewMainActivity;
import com.tiantiankuyin.component.activity.local.PlayViewSimilarActivity;
import com.tiantiankuyin.component.activity.local.SongFoldersActivity;
import com.tiantiankuyin.database.bll.LocalMusicManager;
import com.tiantiankuyin.database.bll.OnDataPreparedListener;
import com.tiantiankuyin.database.bll.OnlineMusicManager;
import com.tiantiankuyin.database.bll.SqlString;
import com.tiantiankuyin.net.EasouAsyncImageLoader.ILoadedImage;
import com.tiantiankuyin.para.Env;
import com.tiantiankuyin.para.IntentAction;
import com.tiantiankuyin.para.SPHelper;
import com.tiantiankuyin.para.UserData;
import com.tiantiankuyin.para.WebServiceUrl;
import com.tiantiankuyin.play.PlayLogicManager;
import com.tiantiankuyin.utils.BackKeyDownListener;
import com.tiantiankuyin.utils.CommonUtils;
import com.tiantiankuyin.utils.Lg;
import com.tiantiankuyin.utils.SearchPictureAndLrcManager;
import com.tiantiankuyin.view.EasouDialog;
import com.tiantiankuyin.view.EasouMenu;
import com.tiantiankuyin.view.EasouSlidingDrawer;
import com.tiantiankuyin.component.service.IRemotePlayerListener;

import android.app.LocalActivityManager;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup; 
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TabHost;
import android.widget.TextView;

import com.tiantiankuyin.R;
import com.tiantiankuyin.TianlApp;

/**
 * Activity基类，可用于统一控制底部菜单（本地、在线、搜索）和播放进度条
 * 
 * @author DK
 * 
 */
public class BaseActivity extends TabActivity implements ILoadedImage {
	private TabHost mTabHost; // TabActivity根节点
	private RadioGroup mBottomRadioGroup; // 底部菜单单选框集合
	private RadioButton local_rdo; // 本地歌曲按钮
	private RadioButton online_rdo; // 在线曲库按钮
	private RadioButton search_rdo; // 搜索按钮
	private LocalActivityManager mLocalActivityManager;
	private static BaseActivity mBbaseActivity;
	private static Map<String, String> mIntentAction; // 以Action为键，以Activity类名为.addFlags(Intent.)值
	private static final String TAG_LOCAL_ACTIVITY = "local";
	private static final String TAG_ONLINE_ACTIVITY = "online";
	private static final String TAG_SEARCH_ACTIVITY = "search";
	public static final String ACTIVITY_LEVEL = "LEVEL";
	public static final String INTENT_ACTIVITY_NAME = "ActivityName"; // 用以提取intent数据
	public static final String KEY_IS_DEAL_INTENT_PLAY = "isDealIntentPlay";// 用于提取Intent数据，当前是否需要播放歌曲。
	private LinearLayout content;// 抽屉里面的内容用来放入播放页面
	private EasouSlidingDrawer slidingDrawer;// 抽屉
	private View playView;// 抽屉里面的内容 playViewactivity 转换成的view;
	private LinearLayout mini_playControl;// mini播放控制条
	private LinearLayout play_view_top;// 播放页的头部 抽屉拉上去的时候 mini播放条变成头部
	private ImageButton mini_play;// mini控制条 的播放 和暂停button;
	private ImageButton mini_next;// mini控制条 的下一首button;
	private ImageView mini_singer_iamge;// mini控制条的歌手图片
	private TextView mini_song_name;// mini控制条的歌曲名称
	private TextView mini_singer_name;// mini控制条的歌手名称
	private SeekBar seekBar;// mini控制条的进度条
	private TextView songName;// 抽屉拉伸后头部的歌曲名称
	private TextView singerName;// 抽屉拉伸后头部的歌手名称
	public static BaseActivity instance;
	/** 物理键弹出菜单对象 */
	private EasouMenu mEasouMenu;
	public static boolean isMenuShow;
	public BackKeyDownListener backListener;// 按抽屉头back button的监听器
	private LinearLayout handle;// 抽屉手柄
	private ImageView mini_next_un;// 当没有歌曲的 时候不可点击的图片
	private ImageView mini_play_un;// 当没有歌曲的 时候不可点击的图片
	private DeleteBroadCastRecieve deleteBroadCastRecieve;
	private CancelNetMusicBroadCastReciever cancelNetMusicBroadCastReciever;
	private LinearLayout mSlidingDrawerHandle;
	private FrameLayout mFrameLayoutContent;
	private LinearLayout waitingPB;

	public boolean isStoping = false;
	
	public void setBackListener(BackKeyDownListener backListener) {
		this.backListener = backListener;
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.base_layout);
		instance = this;
		Lg.d("test", "BaseActivity is doing onCreate()");
		InitCmmInterface.initSDK(this);
		// 判断是否子安装后初次启动。若是则展示引导页面。
		if (!UserData.getInstence().isShowedNewUserGuide()) {
			UserData.getInstence().setShowedNewUserGuide(true);
			showGuideAcitvity();// 跳转到引导页面。
		}
		// 初始化主界面 下面头部是否显示的深度 因为在使用的时候 可能导致深度值不准 保存在本地深度值 当用户再次登录 没初始化的话会一直错下去
		TianlApp.newInstance().setPageLevel(1);
		// 要先判断一下当前是否正在播放
		findView();
		init();
		// 设置显示主体内容大小
		setContentBottomMargin(true);
		MusicInfo musicInfo = PlayLogicManager.newInstance().getMusicInfo();// 获取当前正在播放的歌曲
		if (musicInfo != null) {
			mini_next_un.setVisibility(View.GONE);
			mini_play_un.setVisibility(View.GONE);
			mini_next.setVisibility(View.VISIBLE);
			mini_play.setVisibility(View.VISIBLE);
			SearchPictureAndLrcManager.getInstance().getSingerImageUrlFromNet(
					this, this, musicInfo.getArtist(), musicInfo.getAlbum());// 初始化的时候获取歌手图片
		} else {
			mini_next_un.setVisibility(View.VISIBLE);
			mini_play_un.setVisibility(View.VISIBLE);
			mini_next.setVisibility(View.GONE);
			mini_play.setVisibility(View.GONE);
		}
		register();
		checkUpdate();
	}

	private void checkUpdate() {
		//今天是否已经提示。
		boolean isShowToday = UserData.getInstence().isShowUpdateMsgToday();
		if(!isShowToday){
			OnlineMusicManager.getInstence().getUpdate(this,
					new OnDataPreparedListener<Update>() {
						@Override
						public void onDataPrepared(final Update data) {
							if (data != null) {	
								if (data.getVersion() != null
										&& Env.getVersion() != null
										&& !data.getVersion().equalsIgnoreCase(
												Env.getVersion())) {//检测到有更新
									//当前版本是否不再提醒。
									boolean isNotShowAgain = UserData.getInstence().isNotShowUpdateMsgAgain();
									if(!UpdateActivity.isDownloadedUpdateAPK(data.getVersion()) && Env.isWifiAvaliable()){//当前并未下载最新升级包且处于wifi状态下
										new Thread(){
											public void run() {
												UpdateActivity.downloadUpdateAPK(data);
											};
										}.start();
									}else if(data.isShowMsgAgain() || !isNotShowAgain) {//当前需要强制提醒升级或需要再次提醒
										Intent intent = new Intent(
												IntentAction.INTENT_UPDATE_ACTIVITY);
										intent.putExtra(UpdateActivity.UPDATE_BEAN, data);
										startActivity(intent);
										UserData.getInstence().setShowUpdateMsgToday();//设置今天已经提示。
									}
								}else {//不需要升级
									UpdateActivity.isUpdate = false;
								}
							}else {
								Lg.d("getSingerListData() == null");
								UpdateActivity.isUpdate = false;
								return;
							}
						}
					}, WebServiceUrl.UPDATE);
		}
		
		
	}


	private void register() {
		deleteBroadCastRecieve = new DeleteBroadCastRecieve();
		IntentFilter filter = new IntentFilter();
		filter.addAction(IntentAction.INTENT_BROADCAST_UPDATE_MUSIC_VIEW);
		this.registerReceiver(deleteBroadCastRecieve, filter);

		cancelNetMusicBroadCastReciever = new CancelNetMusicBroadCastReciever();
		IntentFilter filterCancel = new IntentFilter();
		filterCancel.addAction(IntentAction.INTENT_BROADCAST_CANLE_NET_MUSIC);
		this.registerReceiver(cancelNetMusicBroadCastReciever, filterCancel);
	}

	private void unRegister() {
		if (deleteBroadCastRecieve != null) {
			unregisterReceiver(deleteBroadCastRecieve);
		}
		if (cancelNetMusicBroadCastReciever != null) {
			unregisterReceiver(cancelNetMusicBroadCastReciever);
		}
	}

	@Override
	protected void onDestroy() {
		unRegister();
		super.onDestroy();
	}

	/**
	 * 跳转到引导页面。
	 * 
	 * @author Perry
	 */
	private void showGuideAcitvity() {
		Intent intent = new Intent();
		intent.setAction(IntentAction.INTENT_GUIDE_ACTIVITY);// 设置跳引导页意图
		startActivityForResult(intent, 100);
		// startActivity(intent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 100) {
			if (CommonUtils.isHasNetwork(this))
				local_rdo.setChecked(true);
			else
				online_rdo.setChecked(true);
		}
	}

	/**
	 * 刷新播放进度条
	 */
	private void freshPlaySeekbar(int currentMilliseconds) {
		if (seekBar != null) {
			seekBar.setProgress(currentMilliseconds);
			
			if(seekBar.getMax() == 0) {
				int duration = PlayLogicManager.newInstance().getDuration();
				if (duration != 0) {
					seekBar.setMax(duration);
					PlayLogicManager.newInstance().getmCurMusic().setDuration(duration);
				}
			}
		}
	}

	/**
	 * 初始化抽屉内容
	 */
	private void initSlidingDrawer() {
		if (playView == null) {// 防止onResume 多次被执行 initSlidingDrawer方法多次被执行
								// 这样只执行一次
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			Intent intent = new Intent(this, PlayViewActivity.class);
			Window subActivity = mLocalActivityManager.startActivity(
					PlayViewActivity.ACTIVITY_ID, intent);
			playView = subActivity.getDecorView();
			content.addView(playView, params);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		mLocalActivityManager.dispatchResume();
		initSlidingDrawer();
	}

	/**
	 * 用于设置tab页面的bottom_margin属性，防止tab页面穿透抽屉
	 * 
	 * @param hasBottomMenu
	 *            是否把底部菜单纳入计算
	 */
	private void setContentBottomMargin(boolean hasBottomMenu) {
		int width = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		int mSlidingDrawerHandleHeight = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		int dip2px = CommonUtils.dip2px(this, 45);
		int mBottomRadioGroupHeight = View.MeasureSpec.makeMeasureSpec(dip2px,
				View.MeasureSpec.EXACTLY);
		mBottomRadioGroup.measure(width, mBottomRadioGroupHeight);
		mSlidingDrawerHandle.measure(width, mSlidingDrawerHandleHeight);
		int heightRadioGroup = mBottomRadioGroup.getMeasuredHeight();
		int heightSlidingDrawerHandle = mSlidingDrawerHandle
				.getMeasuredHeight();
		android.widget.RelativeLayout.LayoutParams layoutParams = new android.widget.RelativeLayout.LayoutParams(
				-2, -2);
		if (hasBottomMenu) {
			layoutParams.bottomMargin = heightRadioGroup
					+ heightSlidingDrawerHandle;
		} else {
			layoutParams.bottomMargin = heightSlidingDrawerHandle;
		}
		mFrameLayoutContent.setLayoutParams(layoutParams);
		mFrameLayoutContent.invalidate();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mLocalActivityManager.dispatchPause(isFinishing());
	}

	/**
	 * 获取View
	 */
	private void findView() {
		mLocalActivityManager = new LocalActivityManager(this, true);
		mTabHost = getTabHost();
		mBottomRadioGroup = (RadioGroup) findViewById(R.id.tab_group);
		mini_next_un = (ImageView) findViewById(R.id.mini_next_un);
		mini_play_un = (ImageView) findViewById(R.id.mini_play_un);
		local_rdo = (RadioButton) findViewById(R.id.local);
		online_rdo = (RadioButton) findViewById(R.id.online);
		search_rdo = (RadioButton) findViewById(R.id.search);
		content = (LinearLayout) findViewById(R.id.content);
		slidingDrawer = (EasouSlidingDrawer) findViewById(R.id.slidingDrawer1);// 自定义抽屉
		mSlidingDrawerHandle = (LinearLayout) findViewById(R.id.handle);
		mFrameLayoutContent = (FrameLayout) findViewById(android.R.id.tabcontent);
		mini_play = (ImageButton) findViewById(R.id.mini_play);// mini控制条的播放和暂停功能
		waitingPB = (LinearLayout) findViewById(R.id.waiting);
		waitingPB.setOnClickListener(waitingListener);
		if (PlayLogicManager.newInstance().getIsPlaying()) {// 如果正在播放设置背景为暂停
			mini_play
					.setImageResource(R.drawable.bottom_control_pause_btn_click);
		} else {// 如果暂停设置背景为开始播放
			mini_play
					.setImageResource(R.drawable.bottom_control_play_btn_click);
		}
		mini_next = (ImageButton) findViewById(R.id.mini_next);// mini控制条的下一首
		mini_singer_iamge = (ImageView) findViewById(R.id.mini_singer_iamge);// 歌手小图片
		mini_song_name = (TextView) findViewById(R.id.mini_song_name);// 歌曲名称
		mini_singer_name = (TextView) findViewById(R.id.mini_singer_name);// 歌手名称
		seekBar = (SeekBar) findViewById(R.id.seekBar);// 歌手名称
		songName = (TextView) findViewById(R.id.songName);// 抽屉拉伸后头部的歌曲名称
		singerName = (TextView) findViewById(R.id.singerName);// 抽屉拉伸后的头部歌手名称
	}

	private View.OnClickListener waitingListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			sendBroadcast(new Intent(
					IntentAction.INTENT_BROADCAST_CANLE_NET_MUSIC));
		}
	};

	class CancelNetMusicBroadCastReciever extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			waitingClick();
		}
	}

	public void waitingClick() {
		isStoping = true;
		waitingPB.setVisibility(View.GONE);
		mini_play.setVisibility(View.VISIBLE);
		mini_play.setImageResource(R.drawable.bottom_control_play_btn_click);
	}

	/**
	 * 初始化加载时播放列表数据
	 * 
	 * @author Erica
	 * @note 修改保存逻辑 10.8
	 * */
	private void loadMusicList() {
		new Thread() {
			@Override
			public void run() {
				List<MusicInfo> musicInfos;
				try {
					String last_sql = UserData.getInstence()
							.getCurrentPlayingMusicListSql();

					if (last_sql != null) {
						int last_PlayType = UserData.getInstence()
								.getCurrentPlayingType();
						int last_PlayPosition = UserData.getInstence()
								.getCurrentPlayingPosition();
						musicInfos = LocalMusicManager.getInstence()
								.getCurrentPlayingMusicList();

						if (musicInfos != null && musicInfos.size() > 0) {
							PlayLogicManager.newInstance().setMusicInfo(
									musicInfos, last_PlayPosition, last_sql);
							PlayLogicManager.newInstance().setPlayType(
									last_PlayType);
						}
					} else {
						// TODO 上次播放记录，以sql语句方式保存，可考虑修改成其它与数据库不相关的信息
						musicInfos = LocalMusicManager.getInstence()
								.getAllMusic();
						if (musicInfos != null && musicInfos.size() > 0) {
							PlayLogicManager
									.newInstance()
									.setMusicInfo(
											musicInfos,
											0,
											SqlString
													.getSqlForSelectAllMusicOrderByAddedDate());
							SPHelper.newInstance().setFoldPath(
									getString(R.string.all_music));
							TianlApp.currentPlayPath = getString(R.string.all_music);

						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
				}
			}
		}.start();
	}

	/**
	 * 初始化当前播放信息
	 */
	private void initPlayInfo() {
		MusicInfo currentMusicInfo = PlayLogicManager.newInstance()
				.getMusicInfo();// 获取当前整在播放的歌曲
		if (currentMusicInfo != null) {// 当前是否存在播放的歌曲 如果存在就设置信息
			seekBar.setMax((int) currentMusicInfo.getDuration());// 设置进度条最大值
			seekBar.setSecondaryProgress(0);
			seekBar.setProgress(0);
			mini_song_name.setText(currentMusicInfo.getTitle());// 设置mini播放条的歌曲名称
			mini_singer_name.setText(currentMusicInfo.getArtist());// 设置mini播放进度条的歌手
			songName.setText(currentMusicInfo.getTitle());// 设置抽屉头top标题的进度条
			singerName.setText(currentMusicInfo.getArtist());// 设置抽屉头的歌手top标题名称
		}
	}

	private View.OnClickListener listener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.mini_play) {// 播放和暂停事件
				if (PlayLogicManager.newInstance().getIsPlaying()) {// 如果在播放就暂停
					try {
						PlayLogicManager.newInstance().pause();
						mini_play
								.setImageResource(R.drawable.bottom_control_play_btn_click);// 设置背景为开始播放
					} catch (Exception e) {
						// e.printStackTrace();
					}
				} else {// 如果 暂停就播放
					try {
						boolean isSuccess = PlayLogicManager.newInstance()
								.play();
						if (isSuccess) {
							mini_play
									.setImageResource(R.drawable.bottom_control_pause_btn_click);
						}// 设置背景为暂停播放
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						// e.printStackTrace();
					}
				}
			} else if (v.getId() == R.id.mini_next) {// mini控制条的播放下一首
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
	private OnDrawerCloseListener drawerCloseListener = new OnDrawerCloseListener() {
		@Override
		public void onDrawerClosed() {
			mBottomRadioGroup.setVisibility(View.VISIBLE);// 设置底部导航栏可见
			mini_playControl.setVisibility(View.VISIBLE);// mini播放条可见
			play_view_top.setVisibility(View.GONE);// 播放的头部隐藏
			int level = TianlApp.newInstance().getPageLevel();
			if (level == 1) {
				mBottomRadioGroup.setVisibility(View.VISIBLE);
			} else {
				mBottomRadioGroup.setVisibility(View.GONE);
			}
			// 如果有音量弹出框 就隐藏 当抽屉收起来的时候
			if (PlayViewMainActivity.instance != null
					&& PlayViewMainActivity.instance.popupwindow != null) {
				PlayViewMainActivity.instance.popupwindow.dismiss();
			}
			// 如果相似歌曲页面在其他的页面 ,当抽屉收起的时候回退到住界面
			if (backListener != null) {
				backListener.onBackButtonClick();
			}
			/*
			 * handle.setBackgroundResource(R.drawable.
			 * bottom_control_background_img_repeat);
			 */
		}
	};
	private OnDrawerOpenListener drawerOpenListener = new OnDrawerOpenListener() {
		@Override
		public void onDrawerOpened() {
			mBottomRadioGroup.setVisibility(View.GONE);// 设置底部导航栏不可见
			mini_playControl.setVisibility(View.GONE);// mini播放条隐藏
			play_view_top.setVisibility(View.VISIBLE);// 播放的头部可见
			// 设置播放页歌手图片页始终是默认的页面当抽屉打开时候
			if (PlayViewActivity.instance != null) {
				PlayViewActivity.instance.getmPager().setCurrentItem(1);// 默认显示播放页中间那个
			}
			/* handle.setBackgroundResource(R.drawable.top_title_tv_color); */
		}
	};

	/**
	 * 初始化
	 */
	private void init() {
		PlayLogicManager.newInstance().addObserver(
				PlayerListener.class.toString(), new PlayerListener());// 注册播放引擎观察者

		loadMusicList();
		mBbaseActivity = this;
		mIntentAction = new HashMap<String, String>();
		Intent localIntent = new Intent(IntentAction.INTENT_LOCAL_ACTIVITY); // 本地
		localIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		Intent onlineIntent = new Intent(IntentAction.INTENT_ONLINE_ACTIVITY); // 在线
		onlineIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		Intent searchIntent = new Intent(IntentAction.INTENT_SEARCH_ACTIVITY); // 搜索
		searchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		// 向TabHost中添加选项卡
		mTabHost.addTab(mTabHost.newTabSpec(TAG_LOCAL_ACTIVITY)
				.setIndicator(TAG_LOCAL_ACTIVITY).setContent(onlineIntent));
		mTabHost.addTab(mTabHost.newTabSpec(TAG_ONLINE_ACTIVITY)
				.setIndicator(TAG_ONLINE_ACTIVITY).setContent(localIntent));
		mTabHost.addTab(mTabHost.newTabSpec(TAG_SEARCH_ACTIVITY)
				.setIndicator(TAG_SEARCH_ACTIVITY).setContent(searchIntent));
		mBottomRadioGroup
				.setOnCheckedChangeListener(new TabitemChangeedListener());
		boolean isFirst = UserData.getInstence().isFirstStartApp();
		System.out.println("isFirst");
		if (CommonUtils.isHasNetwork(this)) {
			local_rdo.setChecked(true);
		} else {
			online_rdo.setChecked(true);
		}
		UserData.getInstence().setFirstStartApp(false);
		mini_playControl = (LinearLayout) findViewById(R.id.mini_playControl);
		handle = (LinearLayout) findViewById(R.id.handle);
		play_view_top = (LinearLayout) findViewById(R.id.play_view_top);
		slidingDrawer.setTouchableIds(new int[] { R.id.mini_play,
				R.id.mini_next });// 设置头部的那些控件需要点击事件 控件ID
		slidingDrawer.setOnDrawerCloseListener(drawerCloseListener);// 关闭监听器
		slidingDrawer.setOnDrawerOpenListener(drawerOpenListener);// 打开监听器
		mini_play.setOnClickListener(listener);
		mini_next.setOnClickListener(listener);
		initPlayInfo();

		// 处理外部调用的音频播放
		if (getIntent().getBooleanExtra(KEY_IS_DEAL_INTENT_PLAY, false)) {
			PlayLogicManager.newInstance().dealIntent(
					PlayLogicManager.newInstance().getIntentData());
		}
	}

	/**
	 * 底部菜单切换
	 * 
	 * @author DK
	 * 
	 */
	private class TabitemChangeedListener implements OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch (checkedId) {
			case R.id.local:// 點擊底部
				// 其他的設置白色 ffffff
				local_rdo.setTextColor(0xff89b61d);// 本地的文字設置高亮
				online_rdo.setTextColor(0xffffffff);// 其他的設置白色
				search_rdo.setTextColor(0xffffffff);// 其他的設置白色
				mTabHost.setCurrentTabByTag(TAG_LOCAL_ACTIVITY);
				break;
			case R.id.online:
				local_rdo.setTextColor(0xffffffff);// 其他的設置白色
				online_rdo.setTextColor(0xff89b61d);// 本地的文字設置高亮
				search_rdo.setTextColor(0xffffffff);// 其他的設置白色
				mTabHost.setCurrentTabByTag(TAG_ONLINE_ACTIVITY);
				break;
			case R.id.search:
				local_rdo.setTextColor(0xffffffff);// 其他的設置白色
				online_rdo.setTextColor(0xffffffff);// 其他的設置白色
				search_rdo.setTextColor(0xff89b61d);// 本地的文字設置高亮
				mTabHost.setCurrentTabByTag(TAG_SEARCH_ACTIVITY);
				break;
			}
		}
	}

	/**
	 * 获取BaseActivity实例
	 * 
	 * @return
	 */
	public static BaseActivity newInstance() {
		return mBbaseActivity;
	}

	/**
	 * 显示Activity
	 * 
	 * @param intent
	 *            启动Activity要以action形式，并要有IINTENT_ACTIVITY_NAME数值（Activity类名）
	 * @param level
	 *            要显示界面的层级一级下显示底部菜单，二级三级下不显示底部菜单
	 */
	public void showActivity(Intent intent, int level) {
		if (mIntentAction == null)
			mIntentAction = new HashMap<String, String>();
		if (intent != null)
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		setContentBottomMargin(level == 1 ? true : false);
		String action = intent.getAction();
		String activityName = intent.getStringExtra(INTENT_ACTIVITY_NAME);
		if (action != null && action.length() > 0) {
			mTabHost.addTab(mTabHost.newTabSpec(activityName)
					.setIndicator(activityName).setContent(intent));
			mIntentAction.put(action, activityName);
		}
		if (level == 1) {
			this.mBottomRadioGroup.setVisibility(View.VISIBLE);
		} else {
			this.mBottomRadioGroup.setVisibility(View.GONE);
		}
		if (activityName != null && activityName.length() > 0)
			mTabHost.setCurrentTabByTag(activityName);
		TianlApp.newInstance().setPageLevel(level);
	}

	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN
				&& event.getRepeatCount() == 0 && mBottomRadioGroup.isShown()) {
			EasouDialog.Builder eb = new EasouDialog.Builder(this);
			eb.setTitle("天天酷音");
			eb.setMessage("是否要退出？");
			eb.setCheckBox(false);
			eb.setEditBox(false);
			eb.setPositiveButton("退出", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					TianlApp.newInstance().exit();
				}
			}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			eb.create().show();
			return true;
		} else if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN
				&& slidingDrawer.isOpened()) {// 如果抽屉是打开的
			// 抽屉里面需要back事件
			if (PlayViewActivity.page == PlayViewActivity.SIMILAR_PAGE
					&& PlayViewSimilarActivity.isBack) {// 在相似歌曲界面
														// 因为该界面有返回事件处理所以
				ViewPager viewPager = PlayViewActivity.instance.getmPager();
				View view = viewPager.getChildAt(0);// 获取相似歌曲界面view
				view.dispatchKeyEvent(event);// 分发事件 传递事件
				return true;
			}
			slidingDrawer.close();// 关闭抽屉
			int level = TianlApp.newInstance().getPageLevel();
			if (level == 1) {
				mBottomRadioGroup.setVisibility(View.VISIBLE);
			} else {
				mBottomRadioGroup.setVisibility(View.GONE);
			}
			return true;
		} else if (event.getKeyCode() == KeyEvent.KEYCODE_MENU
				&& event.getAction() == KeyEvent.ACTION_UP) {
			menuOpen();
			return true;
		}
		isMenuShow = false;
		return super.dispatchKeyEvent(event);
	}

	public class PlayerListener extends IRemotePlayerListener.Stub {
		@Override
		public void onError(int what, int extra) {

		}

		private void cancelLoading() {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					waitingPB.setVisibility(View.GONE);
					mini_play.setVisibility(View.VISIBLE);
					mini_play
							.setImageResource(R.drawable.bottom_control_play_btn_click);
				}
			});
		}

		 
		public void onPrepared() {
			if (isStoping) {// 已经取消了网络加载 暂停该次播放
				PlayLogicManager.newInstance().stop();
				isStoping = false;
				cancelLoading();
				return;
			}
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					waitingPB.setVisibility(View.GONE);
					mini_play.setVisibility(View.VISIBLE);
					mini_play
							.setImageResource(R.drawable.bottom_control_pause_btn_click);
				}
			});

		}
 
		public void onStartBuffer() {

		}

		 
		public void onBufferingUpdate(final int percent) {// 缓冲进度
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					freshSencondProgress(percent);
				}
			});
		}

		private void freshSencondProgress(int percent) {
			if (seekBar != null) {
				seekBar.setSecondaryProgress(percent * seekBar.getMax() / 100);
			}
		}

	 
		public void onStartPlay() {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					initPlayInfo();// 重新初始化播放信息
					setSingerImage();// 设置默认个手机图片
					mini_play
							.setImageResource(R.drawable.bottom_control_pause_btn_click);// 如果是播放的设置背景为暂停
					if (MusicListActivity.instance != null
							&& MusicListActivity.instance.getmAdapter() != null) {// 刷新播放状态
						MusicListActivity.instance.getmAdapter()
								.notifyDataSetChanged();
					}
					if (SongFoldersActivity.instance != null
							&& SongFoldersActivity.instance.getAdapter() != null) {// 刷新播放状态
						SongFoldersActivity.instance.getAdapter()
								.notifyDataSetChanged();
					}
					MusicInfo musicInfo = PlayLogicManager.newInstance()
							.getMusicInfo();// 获取当前正在播放的歌曲
					if (musicInfo != null) {
						mini_next_un.setVisibility(View.GONE);
						mini_next.setVisibility(View.VISIBLE);
						mini_play_un.setVisibility(View.GONE);
						mini_play.setVisibility(View.VISIBLE);
						SearchPictureAndLrcManager.getInstance()
								.getSingerImageUrlFromNet(BaseActivity.this,
										BaseActivity.this,
										musicInfo.getArtist(),
										musicInfo.getAlbum());// 初始化的时候获取歌手图片
					}
				}
			});
		}

	 
		public void onMusicPause() {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mini_play
							.setImageResource(R.drawable.bottom_control_play_btn_click);// 如果是播放的设置背景播放背景
					if (MusicListActivity.instance != null
							&& MusicListActivity.instance.getmAdapter() != null) {// 刷新播放状态
						MusicListActivity.instance.getmAdapter()
								.notifyDataSetChanged();
					}
					if (SongFoldersActivity.instance != null
							&& SongFoldersActivity.instance.getAdapter() != null) {// 刷新播放状态
						SongFoldersActivity.instance.getAdapter()
								.notifyDataSetChanged();
					}
				}
			});
		}

		 
		public void onProgressChanged(final int currentMilliseconds) {// 播放进度条
			runOnUiThread(new Runnable() {
				public void run() {
					freshPlaySeekbar(currentMilliseconds);
				}
			});
		}

		 
		public void onCompletion() {// 播放完成
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mini_play
							.setImageResource(R.drawable.bottom_control_play_btn_click);// 设置背景为暂停
				}
			});
		}

		 
		public void onMusicStop() {

		}

	 
		public void onBufferComplete() {
			// TODO Auto-generated method stub

		}

	 
		public void onBuffer() {
			// TODO Auto-generated method stub

		}

	 
		public void onCacheUpdate(long currentCache) {
			// TODO Auto-generated method stub
		}


		 
		public void onPreparing() {
			// 播放网络歌曲
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					initPlayInfo();// 重新初始化播放信息
					waitingPB.setVisibility(View.VISIBLE);
					mini_play.setVisibility(View.GONE);
					seekBar.setMax(0);
					seekBar.setProgress(0);
					seekBar.setSecondaryProgress(0);
				}
			});

		}

	}

	 
	public void onFinishLoaded(final SoftReference<Drawable> drawable,
			final String saveName) {
		if (drawable == null) {
			setSingerImage();
			return;
		}
		runOnUiThread(new Runnable() {
			@Override
			public void run() {// 刷新歌手图片
				MusicInfo musicInfo = PlayLogicManager.newInstance()
						.getmCurMusic();
				if (musicInfo != null && saveName != null) {
					String newName = saveName;
					if (saveName.contains("_")) {
						newName = saveName.split("_")[0];
					}
					if (newName.equals(musicInfo.getAlbum())
							|| newName.equals(musicInfo.getArtist())) {// 判斷下載的圖片跟
																		// 當前歌曲的對象是否一致
						Drawable d = drawable.get();
						if (d != null) {
							mini_singer_iamge.setImageDrawable(d);
						} else {
							mini_singer_iamge
									.setImageResource(R.drawable.list_item_artist_defalut_img);
						}
						Animation animation = AnimationUtils.loadAnimation(
								BaseActivity.this, R.anim.push_in);
						mini_singer_iamge.startAnimation(animation);
					}
				}
			}
		});

	}

 
	public void onError(Exception e) {

	}

	 
	public void onFinishLoadedLRC(String lrcPath, String songName) {

	}

	public void menuOpen() {
		if (mEasouMenu != null) {
			if (isMenuShow)
				mEasouMenu.dismiss();
			else {
				mEasouMenu.showMenu();
			}
		} else {
			mEasouMenu = new EasouMenu(this, getCurrentFocus());
			mEasouMenu.showMenu();
		}
		isMenuShow = !isMenuShow;
	}

	/**
	 * 设置歌手默认的图片
	 */
	private void setSingerImage() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mini_singer_iamge
						.setImageResource(R.drawable.list_item_artist_defalut_img);
				Animation animation = AnimationUtils.loadAnimation(
						BaseActivity.this, R.anim.push_in);
				mini_singer_iamge.startAnimation(animation);
			}
		});
	}

	/** 删除当前播放歌曲界面刷新的问题 
	 * */ 
	public class DeleteBroadCastRecieve extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (mini_singer_iamge != null && mini_song_name != null
					&& mini_singer_name != null
					&& PlayLogicManager.newInstance().isGetRefresh()) {
				initPlayInfo();// 重新初始化播放信息
				setSingerImage();// 设置默认个手机图片
				if (PlayViewActivity.instance != null) {
					PlayViewActivity.instance.initViewAndData();
				}
				MusicInfo musicInfo = PlayLogicManager.newInstance()
						.getMusicInfo();// 获取当前正在播放的歌曲
				if (musicInfo != null) {
					SearchPictureAndLrcManager.getInstance()
							.getSingerImageUrlFromNet(BaseActivity.this,
									BaseActivity.this, musicInfo.getArtist(),
									musicInfo.getAlbum());
				}
				PlayLogicManager.newInstance().setGetRefresh(false);
			}
		}
	}
}
