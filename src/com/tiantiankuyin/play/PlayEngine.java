package com.tiantiankuyin.play;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpStatus;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.PowerManager; 

import com.tiantiankuyin.bean.MusicInfo;
import com.tiantiankuyin.database.bll.LocalMusicManager;
import com.tiantiankuyin.para.Constant;
import com.tiantiankuyin.para.SPHelper;
import com.tiantiankuyin.scan.ISingleMediaScannerListener;
import com.tiantiankuyin.scan.SingleMediaScanner;
import com.tiantiankuyin.utils.CommonUtils;
import com.tiantiankuyin.utils.Lg;
import com.tiantiankuyin.component.service.IRemotePlayerListener;
import com.tiantiankuyin.TianlApp;

/**
 * 播放器引擎 如果想获得播放器状态，请先实现IObserver并通过播放逻辑将观察者添加到观察者队列中
 * 
 * @author DK
 * 
 */
// TODO 建议：音乐播放器在播放非本地歌曲时，MediaPlay需要加载一次，存储需要再次加载，建议优化
public class PlayEngine implements OnErrorListener, OnPreparedListener,
		OnBufferingUpdateListener, OnCompletionListener {
	/** 播放器 */
	private MediaPlayer mEasouMediaPlayer; 
	/** 播放器状态机观察者集合 */
	private Hashtable<String,IRemotePlayerListener> mMediaPlayerObserver;  
	private Timer mTimer;
	private boolean isPrepared = false;
	public boolean isLocalMusic;
	private String currentURL;
	private int buf;

	public PlayEngine() {
		mEasouMediaPlayer = new MediaPlayer();
		mEasouMediaPlayer.setOnErrorListener(this);
		mEasouMediaPlayer.setOnPreparedListener(this);
		mEasouMediaPlayer.setOnBufferingUpdateListener(this);
		mEasouMediaPlayer.setOnCompletionListener(this);
		mMediaPlayerObserver = new Hashtable<String,IRemotePlayerListener>();
		mEasouMediaPlayer.setWakeMode(TianlApp.newInstance()
				.getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK); // 确保播放器在播放时使用可以获得CPU
		mTimer = new Timer();
		TimerTask mTask = new TimerTask() {

			@Override
			public void run() {
				nofifyOnProgressChanged();
			}
		};
		mTimer.schedule(mTask, 0, 1000);
	}


	private void nofifyOnProgressChanged() {
		if (mEasouMediaPlayer != null && isPrepared
				&& mEasouMediaPlayer.isPlaying()) {
			int currentPosition = mEasouMediaPlayer.getCurrentPosition();
			notifyObservers("onProgressChanged",
					new Object[] { currentPosition },
					new Class<?>[] { int.class }); // 通知观察者已经开始播放音乐
		}
	}

	public boolean isPrepared() {
		return isPrepared;
	}

	/**
	 * 开始播放音乐
	 * 
	 * @param musicPath
	 *            歌曲路径
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	public void startPlayMusic(String musicPath) {
		// TODO 目前不区分是否为同一音乐，重新播放，不知需求是否为这样。
		if (mEasouMediaPlayer == null || musicPath == null) {
			return;
		}
		if (musicPath != null && musicPath.length() > 0) {
			isLocalMusic = true;
			mEasouMediaPlayer.reset();

			try {
				mEasouMediaPlayer.setDataSource(musicPath);
				prepare();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	/**
	 * 播放非本地歌曲
	 * 
	 * @param url
	 *            歌曲路径
	 * @throws IOException
	 * @throws IllegalStateException
	 * @throws IllegalArgumentException
	 */
	public void startPlayNetMusic(final String url, final String songName,
			final String fileID, final String gid, final String singerName) {
		if (mEasouMediaPlayer == null) {
			return;
		}
		if (currentURL != null && url != null && url.equals(currentURL)
				&& isPrepared && isPlaying())
			return;
		
		
		if (url != null && url.length() > 0) {
			mEasouMediaPlayer.reset();
			isLocalMusic = false;
			currentURL = url;
			
			//if (SPHelper.newInstance().getListenDownload()) {
				notifyObservers("onPreparing", null, null); // 开始准备
				new Thread() {
					public void run() {
						try {
							mEasouMediaPlayer.setDataSource(url);
							prepare();
							bufferMusic(url, songName, fileID, gid,
									singerName);
							/*List<MusicInfo> datas = LocalMusicManager
									.getInstence().getMusicListByMusicGID(gid);
							if (datas != null && datas.size() > 0) {
								System.out.println(url+"-------------2");
								MusicInfo info = datas.get(0);
								if (datas.size() == 1
										&& info.getDisplayName().contains("铃声")) {
									System.out.println(url+"-------------3");
									// FIXME
									// 此处涉及多线程，setDataSource方法，可能会出现IllegalStateException
									// - called in an invalid state
									mEasouMediaPlayer.setDataSource(url);
									prepare();
									bufferMusic(url, songName, fileID, gid,
											singerName);
								} else {
									System.out.println(url+"-------------4");
									for (MusicInfo musicInfo : datas) {
										String displayName = musicInfo
												.getDisplayName();
										if (!displayName.contains("铃声")) {
											File musicFile = new File(
													musicInfo.getLocalUrl());
											if (musicFile.exists()) {
												// FIXME
												// 此处涉及多线程，setDataSource方法，可能会出现IllegalStateException
												// - called in an invalid state
												mEasouMediaPlayer
														.setDataSource(musicInfo
																.getLocalUrl());
												prepare();
											} else {
												// FIXME
												// 此处涉及多线程，setDataSource方法，可能会出现IllegalStateException
												// - called in an invalid state
												mEasouMediaPlayer
														.setDataSource(url);
												prepare();
												bufferMusic(url, songName,
														fileID, gid, singerName);
											}
											break;
										}
									}
								}
							} else {
								System.out.println(url+"-------------5");
								// FIXME
								// 此处涉及多线程，setDataSource方法，可能会出现IllegalStateException
								// - called in an invalid state
								mEasouMediaPlayer.setDataSource(url);
								prepare();
								bufferMusic(url, songName, fileID, gid,
										singerName);
							}*/
						} catch (Exception e) {
							// e.printStackTrace();
						}
					}
				}.start();
			/*} else {
				// FIXME
				// 此处涉及多线程，setDataSource方法，可能会出现IllegalStateException
				// - called in an invalid state
				try {
					System.out.println(url+"-------------6");
					mEasouMediaPlayer.setDataSource(url);
					prepare();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}*/
		}
	}

	/**
	 * 缓冲音乐音频
	 * 
	 * @param url
	 * @throws IOException
	 */
	private void bufferMusic(String url, String songName, String fileID,
			String gid, String singerName) {
		InputStream inputStream = null;
		FileOutputStream fos = null;
		FileOutputStream fosDes = null;
		FileInputStream fis = null;
		try {
			URL musicUrl = new URL(url);
			HttpURLConnection con = (HttpURLConnection) musicUrl
					.openConnection();
			con.setReadTimeout(10000);
			con.setRequestMethod("GET");
			con.connect();
			int responseCode = con.getResponseCode();
			if (responseCode == HttpStatus.SC_OK
					|| responseCode == HttpStatus.SC_PARTIAL_CONTENT) {
				File dir = new File(Constant.SdcardPath.MUSIC_CACHE_SAVEPATH);
				if (!dir.exists())
					dir.mkdir();
				String fileName = songName + CommonUtils.getSuffixByUrl(url);
				File file = new File(Constant.SdcardPath.MUSIC_CACHE_SAVEPATH
						+ "/" + fileName);
				if (file.exists())
					file.delete();
				file.createNewFile();
				String contentRange = con.getHeaderField("Content-Range"); // 用于提取文件大小
				String contentLength = con.getHeaderField("Content-Length");// 如果Content-Range不存在时
				long fileTotalSize = CommonUtils.getFileTotalSize(contentRange,
						contentLength);
				inputStream = con.getInputStream();
				fos = new FileOutputStream(file);
				long total = 0;
				int len = -1;
				byte[] buffer = new byte[1024];
				// 创建一个数值格式化对象
				NumberFormat numberFormat = NumberFormat.getInstance();
				// 设置精确到小数点后2位
				numberFormat.setMaximumFractionDigits(0);
				while ((len = inputStream.read(buffer)) != -1
						&& url.equals(currentURL)) {
					fos.write(buffer, 0, len);
					total += len;
					String result = numberFormat.format((float) total
							/ (float) fileTotalSize * 100);
					Integer value = Integer.valueOf(result);
					if (value > buf) {
						notifyObservers("onBufferingUpdate",
								new Object[] { value },
								new Class<?>[] { int.class }); //
					}
					buf = value;
				}
				if (total == fileTotalSize) {
					File desDir = new File(Constant.SdcardPath.SONG_SAVEPATH);
					if (!desDir.exists())
						desDir.mkdir();
					File des = new File(Constant.SdcardPath.SONG_SAVEPATH + "/"
							+ fileName);
					if (!des.exists())
						des.createNewFile();
					fosDes = new FileOutputStream(des);
					fis = new FileInputStream(file);
					int length = 0;
					byte[] bytes = new byte[1024];
					while ((length = fis.read(bytes)) != -1) {
						fosDes.write(bytes, 0, length);
					}
					file.delete();
					SingleMediaScanner scann = new SingleMediaScanner(
							new ISingleMediaScannerListener() {

								@Override
								public void onSingleMediaScannerBegin() {

								}

								@Override
								public void onSingleMediaFail(String path,
										SingleMediaScannerErrorType errorType) {

								}

								@Override
								public void onSingleMediaCompleted(String path,
										MusicInfo musicInfo) {

								}
							});
					scann.scanFile(des.getAbsolutePath(), gid, fileID,
							songName, singerName);
					startPlayMusic(des.getAbsolutePath());
				} else {
					file.delete();
				}
			}
		} catch (Exception e) {
			// e.printStackTrace();
		} finally {
			buf = 0;
			if (fis != null)
				try {
					fis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (fosDes != null)
				try {
					fosDes.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (fos != null)
				try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (inputStream != null)
				try {
					inputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}

	/**
	 * 播放准备
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	private void prepare() throws IllegalStateException, IOException {
		if (mEasouMediaPlayer == null)
			return;
		isPrepared = false;
		mEasouMediaPlayer.prepareAsync(); // 异步处理准备工作
		notifyObservers("onPreparing", null, null); // 开始准备
	}

	/**
	 * 开始播放
	 */
	public void play() {
		if (mEasouMediaPlayer == null || !isPrepared)
			return;
		mEasouMediaPlayer.start(); // 开始播放音乐

		notifyObservers("onStartPlay", null, null); // 通知观察者已经开始播放音乐
	}

	/**
	 * 暂停播放
	 */
	public void pause() {
		if (mEasouMediaPlayer == null || !isPrepared)
			return;
		mEasouMediaPlayer.pause(); // 暂停音乐
		notifyObservers("onMusicPause", null, null); // 通知观察者已经暂停播放音乐
	}

	/**
	 * 停止播放
	 */
	public void stop() {
		if (mEasouMediaPlayer == null || !isPrepared)
			return;
		mEasouMediaPlayer.stop();
		isPrepared = false;

		notifyObservers("onMusicStop", null, null); // 通知观察者已经停止播放音乐
		release();
	}

	/**
	 * 释放资源
	 */
	// TODO 建议：因mEasouMediaPlayer,仅在
	// PlayEngine初始化时，才会生成实例。而PlayEngine采用单例模式，所以PlayEngine只会被初始化一次。如果调用此方法，会导致播放器失效，无法再播放音乐.建议在每次检查mEasouMediaPlayer==null为真时，重新创建一个MediaPlayer实例
	// 跟踪代码引用，此处仅被stop()引用，但stop()方法中将isPrepared置为false了，故mEasouMediaPlayer.release()不会被调用
	// 建议将mEasouMediaPlayer的生命周期，与PlayEngine实例的生命周期绑定。
	// 即，不提供release()外部调用。
	public void release() {
		if (mEasouMediaPlayer == null || !isPrepared)
			return;
		mEasouMediaPlayer.release();
	}

	/**
	 * 将播放器移动至
	 * @param milliseconds
	 *            移动以后的位置
	 */
	public void seekTo(int milliseconds) {
		if (mEasouMediaPlayer == null || !isPrepared)
			return;
		if (isLocalMusic) {
			mEasouMediaPlayer.seekTo(milliseconds);
			nofifyOnProgressChanged();
		} else {
			try {
				mEasouMediaPlayer.seekTo(milliseconds);
				notifyObservers("onStartBuffer", null, null); // 通知观察者开始seek缓冲
			} catch (IllegalArgumentException e) {
				// e.printStackTrace();
			} catch (IllegalStateException e) {
				// e.printStackTrace();
			}
		}

	}

	/**
	 * 获取当前播放进度
	 * @return 返回毫秒值
	 */
	public int getCurrentPosition() {
		if (mEasouMediaPlayer == null || !isPrepared)
			return 0;
		return mEasouMediaPlayer.getCurrentPosition();
	}

	/**
	 * 获取多媒体总时长
	 * 
	 * @return 返回毫秒值
	 */
	public int getDuration() {
		if (mEasouMediaPlayer == null || !isPrepared)
			return 0;
		return mEasouMediaPlayer.getDuration();
	}

	/**
	 * 判断播放器是否正在播放
	 * 
	 * @return
	 */
	public boolean isPlaying() {
		if (mEasouMediaPlayer == null || !isPrepared)
			return false;
		return mEasouMediaPlayer.isPlaying();
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		notifyObservers("onError", new Object[] { what, extra },
				new Class<?>[] { int.class, int.class }); // 通知观察者播放器出错
		return false;
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		isPrepared = true;
		notifyObservers("onPrepared", null, null); // 通知观察者播放器已经准备完毕
		play();
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {

	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		if (isPrepared) {
			notifyObservers("onCompletion", null, null); // 通知观察者播放器已经播放完某个媒体文件
		}
	}

	/**
	 * 添加一个播放器状态机观察者，如果当前状态机集合为空，则抛出空指针异常
	 * 
	 * @param observer
	 */
	public void addObserver(String observerName,IRemotePlayerListener observer) {
		synchronized (mMediaPlayerObserver) {
			mMediaPlayerObserver.put(observerName,observer);
		}
	}

	/**
	 * 删除一个播放器状态机观察者，如果当前状态机集合为空，则抛出空指针异常
	 * 
	 * @param observer
	 */
	public void deleteObserver(String observerName) {
		synchronized (mMediaPlayerObserver) {
			mMediaPlayerObserver.remove(observerName);
		}
	}

	/**
	 * 删除一个播放器状态机观察者，如果当前状态机集合为空，则抛出空指针异常
	 * 
	 * @param observer
	 */
	public void deleteObservers() {
		synchronized (mMediaPlayerObserver) {
			mMediaPlayerObserver.clear();
		}
	}

	/**
	 * 通知所有播放器状态机观察者，如果当前状态机集合为空，则抛出空指针异常
	 * 
	 * @param methodName
	 *            方法名
	 * @param parameterValues
	 *            方法参数值
	 * @param parameterTypes
	 *            方法参数类型
	 */
	private void notifyObservers(String methodName, Object[] parameterValues,
			Class<?>[] parameterTypes) {
		if (mMediaPlayerObserver == null)
			throw new NullPointerException();
		synchronized (mMediaPlayerObserver) {
			Iterator<Entry<String, IRemotePlayerListener>> iterator = mMediaPlayerObserver.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, IRemotePlayerListener> entry = iterator.next();
				//Lg.e(entry.getKey());
				IRemotePlayerListener observer = entry.getValue();
				try {
					Method method = observer.getClass().getMethod(methodName,
							parameterTypes);
					method.invoke(observer, parameterValues);
				} catch (SecurityException e) {
					Lg.d("播放引擎通知回调失败",e);
				} catch (NoSuchMethodException e) {
					Lg.d("播放引擎通知回调失败",e);
				} catch (IllegalArgumentException e) {
					Lg.d("播放引擎通知回调失败",e);
				} catch (IllegalAccessException e) {
					Lg.d("播放引擎通知回调失败",e);
				} catch (InvocationTargetException e) {
					Lg.d("播放引擎通知回调失败",e);
				}
			}
		}
	}

}
