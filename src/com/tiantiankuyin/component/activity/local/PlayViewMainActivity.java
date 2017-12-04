package com.tiantiankuyin.component.activity.local;

import java.lang.ref.SoftReference;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.tiantiankuyin.bean.MusicInfo;
import com.tiantiankuyin.database.bll.LocalMusicManager;
import com.tiantiankuyin.database.bll.OnDataPreparedListener;
import com.tiantiankuyin.net.EasouAsyncImageLoader.ILoadedImage;
import com.tiantiankuyin.para.Constant;
import com.tiantiankuyin.para.Env;
import com.tiantiankuyin.para.IntentAction;
import com.tiantiankuyin.para.SPHelper;
import com.tiantiankuyin.play.PlayLogicManager;
import com.tiantiankuyin.utils.SearchPictureAndLrcManager;
import com.tiantiankuyin.utils.SettingChangeBroadcastReceiver;
import com.tiantiankuyin.view.EasouShareDialog;
import com.tiantiankuyin.view.EasouViewPager;
import com.tiantiankuyin.view.LrcView;
import com.tiantiankuyin.component.service.IRemotePlayerListener;
import com.tiantiankuyin.R;

/**
 * 
 * 播放主页面
 * @author andrew
 * 
 */
public class PlayViewMainActivity extends Activity implements ILoadedImage,
		OnClickListener {
	public static final String ACTIVITY_ID = "PlayViewMainActivity";
	/** 搜索歌手图片结束 */
	public static final int SEARCH_SINGER_URL_FINISH = 6; 
	/** 搜索歌手图片出错 */
	public static final int SEARCH_SINGER_URL_ERROR = 7; 
	/** 通知隐藏 播放页操作框 */
	public static final int PLAYVIEW_DISMISS_OPERATOR = 8; 
	/** 声音seekbar */
	private SeekBar soundSeekBar; 
	/** 声音控制管理器 */
	private AudioManager mAudioManager; 
	/** 最大音量 */
	private int maxVolume; 
	/** 当前音量 */
	private int currentVolume; 
	/** 定义手势动作两点之间的最小距离 */
	public static final int FLIP_DISTANCE = 150;
	public static PlayViewMainActivity instance;
	public static EasouViewPager viewPager;
	/** 歌手图片 */
	private ImageView singer_image; 
	/** 图片地址和歌词地址搜索管理器 */
	private SearchPictureAndLrcManager searchPictureAndLrcManager; 
	/** 分享 */
	private ImageButton playview_share; 
	/** 播放模式 */
	private ImageButton playview_model; 
	/** 我的收藏 */
	private ImageButton playview_fav; 
	/** 播放页面 mini歌词 */
	private LrcView mini_lyric; 
	/** 监听设置的广播 */
	private SettingChangeBroadcastReceiver receiver; 

	public LrcView getMini_lyric() {
		return mini_lyric;
	}

	public ImageView getSinger_image() {
		return singer_image;
	}

	public void setSinger_image(ImageView singer_image) {
		this.singer_image = singer_image;
	}

	public PopupWindow popupwindow;

	/** 监听viewpager翻页
	 *   */
	interface MyPagerSwitchListener {
		void onPage(int currentItem);
	}

	/** 监听用户是否点击了 播放 上一首 还是下一首歌 和播放暂停的按钮 */
	interface PlayButtonListener {
		void onButtonClick();
	}

	public int curPos;
	private boolean isLrcPage = false;

	class MyPlayButtonListener implements PlayButtonListener {
		@Override
		public void onButtonClick() {
			if (popupwindow != null && popupwindow.isShowing()) {
				popupwindow.dismiss();
			}
		}
	}

	class MainPagerListener implements MyPagerSwitchListener {
		@Override
		public void onPage(int currentItem) {
			curPos = currentItem;
			if (popupwindow != null) {
				popupwindow.dismiss();
			}
			if (curPos != 2) {// 在歌詞頁 屏幕常亮关闭
				if (PlayViewLrcActivity.instance != null
						&& PlayViewLrcActivity.instance.mWakeLock != null) {
					if (isLrcPage) {
						PlayViewLrcActivity.instance.mWakeLock.release();
						isLrcPage = false;
					}
				}
			} else {// 根据参数开启常量
				if (PlayViewLrcActivity.instance != null
						&& PlayViewLrcActivity.instance.mWakeLock != null) {
					if (SPHelper.newInstance().getLrcScreenOn()) {// 根据设置需求设置歌词也常亮
						isLrcPage = true;
						PlayViewLrcActivity.instance.mWakeLock
								.setReferenceCounted(false);
						PlayViewLrcActivity.instance.mWakeLock.acquire();
					}
				}
			}
		}
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SEARCH_SINGER_URL_FINISH:
				break;
			case SEARCH_SINGER_URL_ERROR:// 图片加载出错 设置默认的
				singer_image.setImageDrawable(getResources().getDrawable(
						R.drawable.playview_main_singer_img));
				mini_lyric.searchFail();
				break;
			case PLAYVIEW_DISMISS_OPERATOR:// 隐藏操作框
				if (popupwindow != null && popupwindow.isShowing()) {
					popupwindow.dismiss();
				}
				break;
			}
		}
	};
	private RelativeLayout parent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		setContentView(R.layout.local_play_view_main);
		searchPictureAndLrcManager = SearchPictureAndLrcManager.getInstance();

		PlayLogicManager.newInstance().addObserver(
				PlayerListener.class.toString(), new PlayerListener());// 注册播放引擎观察者

		registerReciver();// 註冊設置監聽廣播
		init();// 初始化参数
	}

	private void registerReciver() {
		receiver = new SettingChangeBroadcastReceiver();
		IntentFilter filter = new IntentFilter(
				Constant.SETTING_CHANGE_BROADCAST);
		registerReceiver(receiver, filter);
	}

	@Override
	protected void onDestroy() {
		unRegisterReciver();
		super.onDestroy();
	}

	private void unRegisterReciver() {
		if (receiver != null) {
			unregisterReceiver(receiver);
		}
	}

	/**
	 * 设置播放音量
	 */
	private void setVolume() {
		// 设置音乐的声音大小，nextVolume为根据SeekBar得到的值计算而来的将要改变的声音的大小
		int nextVolume = soundSeekBar.getProgress();
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, nextVolume, 0);
	}

	private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			if (viewPager != null) {
				viewPager.invalidate();
			}
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			setVolume();// 设置音量
		}
	};

	private void init() {
		// 音乐音量管理器实例
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		playview_share = (ImageButton) findViewById(R.id.playview_share);
		playview_model = (ImageButton) findViewById(R.id.playview_model);// 播放模式
		parent = (RelativeLayout) findViewById(R.id.parent);
		parent.setOnClickListener(this);
		mini_lyric = (LrcView) findViewById(R.id.mini_lyric);// mini歌词
		mini_lyric.setTouchable(false);
		mini_lyric.setMiniModel(true);
		mini_lyric.setClickable(true);// 获取焦点的问题
		mini_lyric.setOnClickListener(this);
		singer_image = (ImageView) findViewById(R.id.singer_image);
		setSingerPicSize(singer_image);// 歌手图片大小适配
		MusicInfo musicInfo = PlayLogicManager.newInstance().getMusicInfo();// 获取当前正在播放的歌曲
		if (musicInfo != null) {
			searchPictureAndLrcManager.getLrcUrlFromNet(
					PlayViewMainActivity.this, this, musicInfo.getTitle(),
					musicInfo.getArtist(), musicInfo.getLyricId());// 初始化的时候获取歌词
			searchPictureAndLrcManager.getSingerImageUrlFromNet(this, this,
					musicInfo.getArtist(), musicInfo.getAlbum());// 初始化的时候获取歌手图片
		}
		viewPager = PlayViewActivity.instance.getmPager();// 获取viewpager对象
		PlayViewActivity.instance.setMainListener(new MainPagerListener());// 设置viewpager翻页监听器
		PlayViewActivity.instance.setButtonListener(new MyPlayButtonListener());// 设置按键监听事件

	}

	/**
	 * 播放页面歌手 图片适配
	 */
	private void setSingerPicSize(ImageView singer_image) {
		int screen_W = Env.getScreenWidth();
		int size = 0;
		if (0 < screen_W && screen_W <= 240) {// 小屏幕手机
			size = 120;
		} else if (240 < screen_W && screen_W <= 320) {// 中屏幕手机
			size = 150;
		} else if (320 < screen_W && screen_W <= 480) {// 大屏手机
			size = 300;
		} else {// 超大屏幕
			size = 400;
		}
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) singer_image
				.getLayoutParams();
		params.width = size;
		params.height = size;
		singer_image.setLayoutParams(params);
	}

	private void dismissOperatorPopupWindow() {
		if (popupwindow != null && popupwindow.isShowing()) {
			popupwindow.dismiss();
		}
	}

	private void showOperatorPopupWindow() {
		View view = LayoutInflater.from(this).inflate(
				R.layout.playview_operator_view, null);
		playview_share = (ImageButton) view.findViewById(R.id.playview_share);
		playview_fav = (ImageButton) view.findViewById(R.id.playview_fav);
		playview_model = (ImageButton) view.findViewById(R.id.playview_model);
		initPlayModel();
		soundSeekBar = (SeekBar) view.findViewById(R.id.soundSeekBar);// 调节音量seekbar
		soundSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
		initMyFav();
		maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);// 获取音量的最大值
		currentVolume = mAudioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC);// 获取当前的音量大小
		soundSeekBar.setMax(maxVolume);// 设置seekbar最大值
		soundSeekBar.setProgress(currentVolume);// 设置seekbar当前值
		playview_share.setOnClickListener(listener);
		playview_model.setOnClickListener(listener);
		if (PlayLogicManager.newInstance().getMusicInfo() != null
				&& PlayLogicManager.newInstance().getMusicInfo()
						.getDateAddedFav() > 0)
			playview_fav.setImageResource(R.drawable.dialog_added_fav_icon_img);
		else
			playview_fav.setImageResource(R.drawable.dialog_fav_icon_img);
		playview_fav.setOnClickListener(listener);
		int screen_W = Env.getScreenWidth();
		// 初始化popupMenu
		popupwindow = new PopupWindow(view, screen_W - 80,
				LayoutParams.WRAP_CONTENT);
		popupwindow.setAnimationStyle(R.style.AnimationFade);
		popupwindow.update();
		popupwindow.showAtLocation(mini_lyric, Gravity.BOTTOM, 0,
				getPopupWindowHeight(screen_W));
	}

	/**
	 * popupwindow离底部的距离
	 * 
	 * @param screen_W
	 * @return
	 */
	private int getPopupWindowHeight(int screen_W) {
		int size = 0;
		if (0 < screen_W && screen_W <= 240) {// 小屏幕手机
			size = 40;
		} else if (240 < screen_W && screen_W <= 320) {// 中屏幕手机
			size = 60;
		} else if (320 < screen_W && screen_W <= 480) {// 大屏手机
			size = 120;
		} else {// 超大屏幕
			size = 120;
		}
		return size;
	}

	/**
	 * 初始化我的收藏按鈕
	 */
	private void initMyFav() {
		MusicInfo musicInfo = PlayLogicManager.newInstance().getmCurMusic();
		if (musicInfo == null) {
			return;
		}
		// 先判断是否是 网络歌曲
		if (musicInfo.getFileID()!=null) {// 网络歌曲
			LocalMusicManager.getInstence().getMusicListByFileID(
					String.valueOf(musicInfo.getFileID()), this,
					new MyOnDataPreparedListener());
		} else {// 本地歌曲
			LocalMusicManager.getInstence().getMusicListByMusicID(
					musicInfo.getId(), this, new MyOnDataPreparedListener());
		}

	}

	private class MyOnDataPreparedListener extends
			OnDataPreparedListener<List<MusicInfo>> {

		@Override
		public void onDataPrepared(List<MusicInfo> data) {
			setFavIcon(data);
		}

	}

	private void setFavIcon(List<MusicInfo> musicInfos) {
		if (musicInfos != null && musicInfos.size() > 0) {
			MusicInfo newMusicInfo = musicInfos.get(0);
			if (newMusicInfo != null) {
				if (newMusicInfo.getDateAddedFav() > 0) {// 已经收藏 显示红心
					playview_fav
							.setImageResource(R.drawable.playview_function_fav_icon_img_added);
				} else {// 没收藏
					playview_fav
							.setImageResource(R.drawable.playview_function_fav_icon_img_default);
				}
			}
		}
	}

	/** 初始化播放模式 */
	private void initPlayModel() {
		int type = PlayLogicManager.newInstance().getPlayType();
		switch (type) {
		case PlayLogicManager.TYPE_CYCLE:
			playview_model
					.setImageResource(R.drawable.playview_function_repeat_all_btn_click);
			break;
		case PlayLogicManager.TYPE_SINGLE:
			playview_model
					.setImageResource(R.drawable.playview_function_repeat_ione_btn_click);
			break;
		case PlayLogicManager.TYPE_RANDOM:
			playview_model
					.setImageResource(R.drawable.playview_function_random_btn_click);
			break;
		case PlayLogicManager.TYPE_ORDER:
			playview_model
					.setImageResource(R.drawable.playview_function_order_btn_click);
			break;
		}
	}

	/**
	 * 点击事件
	 */
	private View.OnClickListener listener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.playview_share) {
				if (popupwindow != null) {
					popupwindow.dismiss();
				}
				shareDialog();
			} else if (v.getId() == R.id.playview_model) {
				// 当前播放模式+1 除 4取余数
				int value = (PlayLogicManager.newInstance().getPlayType() + 1) % 4;
				showTypeTips(value);// 显示播放模式提示
				PlayLogicManager.newInstance().setPlayType(value);
			} else if (v.getId() == R.id.playview_fav) {// 收藏 或者取消收藏
				myFavButtonClick();
			}
		}
	};

	/**
	 * 处理收藏的点击事件
	 */
	private void myFavButtonClick() {
		MusicInfo newMusicInfo = null;
		MusicInfo musicInfo = PlayLogicManager.newInstance().getmCurMusic();
		List<MusicInfo> musicInfos = null;
		if (musicInfo != null) {
			// 先判断是否是 网络歌曲
			if (musicInfo.getFileID()!=null) {// 网络歌曲
				musicInfos = LocalMusicManager.getInstence()
						.getMusicListByFileID(
								String.valueOf(musicInfo.getFileID()));
			} else {// 本地歌曲
				musicInfos = LocalMusicManager.getInstence()
						.getMusicListByMusicID(musicInfo.getId());
			}
			if (musicInfos != null && musicInfos.size() > 0) {
				newMusicInfo = musicInfos.get(0);
				if (newMusicInfo.getDateAddedFav() > 0) {// 取消收藏
					newMusicInfo.setDateAddedFav(0);
					boolean flag = LocalMusicManager.getInstence().updateMusic(
							newMusicInfo);
					if (flag) {
						PlayLogicManager.newInstance().setmCurMusic(
								newMusicInfo);
						playview_fav
								.setImageResource(R.drawable.playview_function_fav_icon_img_default);
						Toast.makeText(PlayViewMainActivity.this,
								R.string.cancel_love, Toast.LENGTH_SHORT)
								.show();
					}
				} else {// 收藏
					newMusicInfo.setDateAddedFav(System.currentTimeMillis());
					boolean flag = LocalMusicManager.getInstence().updateMusic(
							newMusicInfo);
					if (flag) {
						PlayLogicManager.newInstance().setmCurMusic(
								newMusicInfo);
						playview_fav
								.setImageResource(R.drawable.playview_function_fav_icon_img_added);
						Toast.makeText(PlayViewMainActivity.this,
								R.string.love_success, Toast.LENGTH_SHORT)
								.show();
					}
				}
				PlayViewMainActivity.this.sendBroadcast(new Intent(
						IntentAction.INTENT_BROADCAST_UPDATE_LOCAL_ACTIVITY));// 发送广播，更新本地音乐的歌曲数量。
			} else {
				Toast.makeText(PlayViewMainActivity.this,
						R.string.love_buffer_finish, Toast.LENGTH_SHORT).show();
			}
		}
		// 一秒之后隐藏 功能框
		dismissOperatorByOne();
	}

	private void dismissOperatorByOne() {
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				sendMessage(PLAYVIEW_DISMISS_OPERATOR,
						null);
			}
		}, 1000);
	}

	/**
	 * 播放模式提示
	 */
	private void showTypeTips(int type) {
		switch (type) {
		case PlayLogicManager.TYPE_CYCLE:
			Toast.makeText(getParent(), getString(R.string.cycle),
					Toast.LENGTH_SHORT).show();
			playview_model
					.setImageResource(R.drawable.playview_function_repeat_all_btn_click);
			break;
		case PlayLogicManager.TYPE_SINGLE:
			Toast.makeText(getParent(), getString(R.string.single),
					Toast.LENGTH_SHORT).show();
			playview_model
					.setImageResource(R.drawable.playview_function_repeat_ione_btn_click);
			break;
		case PlayLogicManager.TYPE_RANDOM:
			Toast.makeText(getParent(), getString(R.string.random),
					Toast.LENGTH_SHORT).show();
			playview_model
					.setImageResource(R.drawable.playview_function_random_btn_click);
			break;
		case PlayLogicManager.TYPE_ORDER:
			Toast.makeText(getParent(), getString(R.string.order),
					Toast.LENGTH_SHORT).show();
			playview_model
					.setImageResource(R.drawable.playview_function_order_btn_click);
			break;
		}
	}

	/**
	 * 弹出分享框
	 */
	private void shareDialog() {
		EasouShareDialog easou_share_dialog = new EasouShareDialog(getParent(),
				R.style.easouDialog, PlayLogicManager.newInstance()
						.getMusicInfo().getTitle(), PlayLogicManager
						.newInstance().getMusicInfo().getLocalUrl());
		easou_share_dialog.show();
	}

	public class PlayerListener extends IRemotePlayerListener.Stub {
		 
		public void onError(int what, int extra) {

		}

		 
		public void onPrepared() {
			runOnUiThread(new Runnable() {
			 
				public void run() {
					mini_lyric.clear();
				}
			});
		}

		 
		public void onStartBuffer() {
			// TODO Auto-generated method stub

		}

		 
		public void onBufferingUpdate(int percent) {
			// TODO Auto-generated method stub

		}

		private void freshPicAndLrc() {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					singer_image.setImageDrawable(getResources().getDrawable(
							R.drawable.playview_main_singer_img));
					// 刷新收藏的红心
					if (playview_fav != null) {
						initMyFav();
					}
				}
			});
		}
 
		public void onStartPlay() {
			freshPicAndLrc();
			MusicInfo musicInfo = PlayLogicManager.newInstance().getMusicInfo();// 获取当前正在播放的歌曲
			if (musicInfo != null) {
				mini_lyric.clear();
				mini_lyric.loading();
				searchPictureAndLrcManager.getLrcUrlFromNet(
						PlayViewMainActivity.this, PlayViewMainActivity.this,
						musicInfo.getTitle(), musicInfo.getArtist(),
						musicInfo.getLyricId());// 初始化的时候获取歌词
				searchPictureAndLrcManager.getSingerImageUrlFromNet(
						PlayViewMainActivity.this, PlayViewMainActivity.this,
						musicInfo.getArtist(), musicInfo.getAlbum());// 初始化的时候获取歌手图片
			}
		}
 
		public void onMusicPause() {
			// TODO Auto-generated method stub

		}

	 
		public void onProgressChanged(final int currentMilliseconds) {
			// TODO Auto-generated method stub
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mini_lyric.updateIndex(currentMilliseconds);
					mini_lyric.invalidate();
					if (viewPager != null) {
						viewPager.invalidate();
					}
				}
			});
		}
 
		public void onCompletion() {
			// TODO Auto-generated method stub

		}

	 
		public void onMusicStop() {
			// TODO Auto-generated method stub

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
			MusicInfo musicInfo = PlayLogicManager.newInstance().getMusicInfo();// 获取当前正在播放的歌曲
			if (musicInfo != null) {
				SearchPictureAndLrcManager.getInstance()
						.getSingerImageUrlFromNet(PlayViewMainActivity.this,
								PlayViewMainActivity.this,
								musicInfo.getArtist(), musicInfo.getAlbum());
			}
			freshPicAndLrc();// 刷新
			// TODO Auto-generated method stub
		}

	}

	 
	public void onError(Exception e) {
		sendMessage(SEARCH_SINGER_URL_ERROR, null);// 图片加载出错
	}

	 
	public void onFinishLoaded(final SoftReference<Drawable> drawable,
			final String saveName) {
		if (drawable == null) {
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
							singer_image.setImageDrawable(d);
						} else {
							singer_image
									.setImageDrawable(getResources()
											.getDrawable(
													R.drawable.playview_main_singer_img));
						}
					}
				}
			}
		});

	}

	@Override
	public void onFinishLoadedLRC(final String lrcPath, final String songName) {
		if (lrcPath == null) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mini_lyric.searchFail();
				}
			});
			return;
		}
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				MusicInfo musicInfo = PlayLogicManager.newInstance()
						.getMusicInfo();// 获取当前正在播放的歌曲
				if (musicInfo == null)
					return;
				String song = musicInfo.getTitle();
				if (song == null) {
					return;
				}
				if (song.contains("_")) {// 做個 處理對歌詞精選拆分
					song = song.split("_")[0];
				}
				if (song.equals(songName)) {// 表示是当前这首歌曲的歌词
					// playview_detail_lyric.renewLrc(lrcPath);
					mini_lyric.clear();
					mini_lyric.setLyric(lrcPath);
					mini_lyric.measure(0, 0);
					// playview_detail_lyric.layout(0, 0,
					// playview_detail_lyric.getMeasuredWidth(),
					// playview_detail_lyric.getMeasuredHeight());
					// olv.measure(0, 0);
				} else {
					mini_lyric.searchFail();
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		/*
		 * if (viewPager!=null&&viewPager.getCurrentItem() == 1) {//
		 * 只有当前页面是播放页主界面触摸事件才有效，其他页面触摸时间没效
		 */if (popupwindow != null && popupwindow.isShowing()) {
			dismissOperatorPopupWindow();
		} else {
			showOperatorPopupWindow();
		}
		// }
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	private void sendMessage(int type, String url) {
		Message msg = new Message();
		msg.what = type;
		msg.obj = url;
		handler.sendMessage(msg);
	}
}
