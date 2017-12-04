package com.tiantiankuyin.component.activity.local;

import java.lang.ref.SoftReference;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PowerManager;

import com.tiantiankuyin.bean.MusicInfo;
import com.tiantiankuyin.net.EasouAsyncImageLoader.ILoadedImage;
import com.tiantiankuyin.play.PlayLogicManager;
import com.tiantiankuyin.utils.Lg;
import com.tiantiankuyin.utils.SearchPictureAndLrcManager;
import com.tiantiankuyin.view.LrcTimeLine;
import com.tiantiankuyin.view.LrcView;
import com.tiantiankuyin.view.LrcView.OnLrcHeightLightListener;
import com.tiantiankuyin.component.service.IRemotePlayerListener;
import com.tiantiankuyin.R;

/**
 * 
 *  播放歌词详情页面
 *
 */
public class PlayViewLrcActivity extends Activity {
	public static final String ACTIVITY_ID = "PlayViewLrcActivity";
	private LrcView playview_detail_lyric;
	/**  */
	private LrcTimeLine olv;
	/** 图片地址和歌词地址搜索管理器 */
	private SearchPictureAndLrcManager searchPictureAndLrcManager; 
	public PowerManager.WakeLock mWakeLock;
	public static PlayViewLrcActivity instance;
	public static int status = -1;
	/** 正在搜索 */
	public static final int LRC_SEARCH_ING = 0; 
	/** 搜索歌词结束 */
	public static final int LRC_SEARCH_FINISH = 1;
	/** 没有歌词  */
	public static final int LRC_SEARCH_ERROR = 2;
	private boolean isPause = false;
	/**
	 * 歌词及封面图片加载监听
	 */
	private ILoadedImage loadedImageListener = new ILoadedImage() {
		@Override
		public void onFinishLoaded(SoftReference<Drawable> drawable,
				String saveName) {
			Lg.d("onFinishLoaded");
		}

		@Override
		public void onError(Exception e) {
			status = LRC_SEARCH_ERROR;

			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					playview_detail_lyric.searchFail();
				}
			});
		}

		@Override
		public void onFinishLoadedLRC(final String lrcPath,
				final String songName) {
			Lg.d("onFinishLoadedLRC");
			if (lrcPath == null) {
				status = LRC_SEARCH_ERROR;
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						playview_detail_lyric.searchFail();
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
						status = LRC_SEARCH_FINISH;
						// playview_detail_lyric.renewLrc(lrcPath);
						playview_detail_lyric.clear();
						playview_detail_lyric.setLyric(lrcPath);
						playview_detail_lyric.measure(0, 0);
						// playview_detail_lyric.layout(0, 0,
						// playview_detail_lyric.getMeasuredWidth(),
						// playview_detail_lyric.getMeasuredHeight());
						// olv.measure(0, 0);
					} else {
						status = LRC_SEARCH_ERROR;
						playview_detail_lyric.searchFail();
					}
				}
			});
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		setContentView(R.layout.local_play_view_lrc);
		searchPictureAndLrcManager = SearchPictureAndLrcManager.getInstance();

		PlayLogicManager.newInstance().addObserver(
				PlayerListener.class.toString(), new PlayerListener());// 注册播放引擎观察者

		init();
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK,
				"XYTEST");

		Lg.d("id:" + Thread.currentThread().getId());
	}

	@Override
	protected void onStop() {
		Lg.d("PlayViewLrc", "-------->onStop");
		super.onStop();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		Lg.d("PlayViewLrc", "-------->onPause");
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		Lg.d("PlayViewLrc", "-------->onDestroy");
		if (mWakeLock != null) {
			mWakeLock.release();
		}
		super.onDestroy();
	}

	/**
	 * 初始化参数
	 */
	public void init() {
		playview_detail_lyric = (LrcView) findViewById(R.id.playview_detail_lyric);
		olv = (LrcTimeLine) findViewById(R.id.olv);
		playview_detail_lyric.setLightListener(lightListener);
		MusicInfo musicInfo = PlayLogicManager.newInstance().getMusicInfo();// 获取当前正在播放的歌曲
		if (musicInfo == null)
			return;
		searchPictureAndLrcManager.getLrcUrlFromNet(loadedImageListener, this,
				musicInfo.getTitle(), musicInfo.getArtist(),
				musicInfo.getLyricId());// 初始化的时候获取歌词
		status = LRC_SEARCH_ING;

		// playview_detail_lyric.setLine(true);
		// playview_detail_lyric.setNeedTouch(true);
		// playview_detail_lyric.setShowLines(false);
		// playview_detail_lyric.setClickable(true);
	}

	private OnLrcHeightLightListener lightListener = new OnLrcHeightLightListener() {

		@Override
		public void showLine(String time) {
			olv.setShow(true);
			olv.setTime(time);
		}

		@Override
		public void hide() {
			olv.setShow(false);
		}

	};

	public class PlayerListener extends IRemotePlayerListener.Stub implements IRemotePlayerListener {
		 
		public void onError(int what, int extra) {
			Lg.d("PlayViewLrc", "onError");
			status = LRC_SEARCH_ERROR;
		}

		 
		public void onPrepared() {
			Lg.d("onPrepared");
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					playview_detail_lyric.clear();
				}
			});
		}

	 
		public void onStartBuffer() {
			Lg.d("onStartBuffer");
		}

		 
		public void onBufferingUpdate(int percent) {
			Lg.d("onBufferingUpdate");
		}

	 
		public void onStartPlay() {
			Lg.d("onStartPlay");
			MusicInfo musicInfo = PlayLogicManager.newInstance().getMusicInfo();// 获取当前正在播放的歌曲
			if (musicInfo == null)
				return;
			if (!isPause) {
				playview_detail_lyric.clear();
				playview_detail_lyric.loading();
				searchPictureAndLrcManager.getLrcUrlFromNet(
						loadedImageListener, PlayViewLrcActivity.this,
						musicInfo.getTitle(), musicInfo.getArtist(),
						musicInfo.getLyricId());// 初始化的时候获取歌词
				status = LRC_SEARCH_ING;
			} else {
				isPause = false;
			}
		}

		 
		public void onMusicPause() {
			Lg.d("onMusicPause");
			isPause = true;
		}

		 
		public void onProgressChanged(final int currentMilliseconds) {
			runOnUiThread(new Runnable() {
				 
				public void run() {
					playview_detail_lyric.updateIndex(currentMilliseconds);
					playview_detail_lyric.postInvalidate();
				}
			});
		}

		 
		public void onCompletion() {
			Lg.d("id:" + Thread.currentThread().getId());
			Lg.d("onCompletion");
			// playview_detail_lyric.clear();
		}

	 
		public void onMusicStop() {
			Lg.d("onMusicStop");
		}

	 
		public void onBufferComplete() {
			Lg.d("onBufferComplete");
		}

		 
		public void onBuffer() {
			Lg.d("onBuffer");
		}

 
		public void onCacheUpdate(long currentCache) {
			Lg.d("onCacheUpdate");
		}

	 
		public void onPreparing() {

			Lg.d("id:" + Thread.currentThread().getId());
			Lg.d("onPreparing");
			// playview_detail_lyric.clear();
			isPause = false;
		}
	}

	public LrcView getPlayview_detail_lyric() {
		return playview_detail_lyric;
	}

}
