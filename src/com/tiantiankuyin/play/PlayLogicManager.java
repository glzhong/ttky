package com.tiantiankuyin.play;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.http.HttpStatus;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.Toast; 

import com.tiantiankuyin.bean.MusicInfo;
import com.tiantiankuyin.bean.OlDownloadItem;
import com.tiantiankuyin.bean.OlDownloadVO;
import com.tiantiankuyin.bean.OlSongVO;
import com.tiantiankuyin.database.bll.LocalMusicManager;
import com.tiantiankuyin.database.bll.OnDataPreparedListener;
import com.tiantiankuyin.database.bll.OnlineMusicManager;
import com.tiantiankuyin.database.bll.SqlString;
import com.tiantiankuyin.notification.PlayerNotification;
import com.tiantiankuyin.para.IntentAction;
import com.tiantiankuyin.para.SPHelper;
import com.tiantiankuyin.para.UserData;
import com.tiantiankuyin.scan.ISingleMediaScannerListener;
import com.tiantiankuyin.scan.ScanUtil;
import com.tiantiankuyin.scan.SingleMediaScanner;
import com.tiantiankuyin.utils.CommonUtils;
import com.tiantiankuyin.utils.Lg;
import com.tiantiankuyin.component.service.IRemotePlayerListener;
import com.tiantiankuyin.R;
import com.tiantiankuyin.TianlApp;

/**
 * 播放逻辑控制类
 * 
 * @author Erica
 * @note 2012-9-7
 */
public class PlayLogicManager implements IRemotePlayerListener{

	private Hashtable<String, IRemotePlayerListener> mMediaPlayerObserver; // 播放器状态机观察者集合
	/** 当前临时存储的外部调用Uri */
	private Uri intentData = null;

	/** 单例对象 */
	private static PlayLogicManager mPlayLogicManager;

	/** 播放引擎 */
	private RemotePlayServiceManager mRemotePlayServiceManager;

	/** 随机播放序列 */
	private List<Integer> mRandomList;

	/** 当前播放对象 */
	private MusicInfo mCurMusic;
	/** 当前播放位置 */
	private int mCurPosition;
	/** 随机播放播放位置 */
	private int mCurRandomPosition;
	/** 播放模式 */
	private int mPlayType = 0;
	/** 循环播放 */
	public static final int TYPE_CYCLE = 3;
	/** 单曲播放 */
	public static final int TYPE_SINGLE = 1;
	/** 随机播放 */
	public static final int TYPE_RANDOM = 2;
	/** 顺序播放 */
	public static final int TYPE_ORDER = 0;
	/** 播放错误处理 */
	private static final long FAIL_TIME_FRAME = 1000;
	/** 最大播放失败次数 */
	public static final int ACCEPTABLE_FAIL_NUMBER = 2;
	/** build后，会变成准备状态(true)，但启动了播放器，准备状态结束为(false) */
	public boolean ispreparing = false;
	/** 未准备好状态 */
	public boolean isplayAfterPrepare = false;
	/** 是否需要刷新进度条 */
	public boolean isGetRefresh = false;

	/** 当前操作歌单SQL语句 */
	private String mSql = "";
	/** 当前播放列表 */
	private List<MusicInfo> mCurMusicList;
	/** 当前非本地播放列表 */
	private List<OlSongVO> mCurOnlineMusicList;
	/** 是否是非本地歌曲 */
	private boolean isNetData = false;
	/** 是否更新了CurMusic对象 */
	private boolean isNewCurMusic = false;
	/** 是否被影响到 */
	private boolean isNotifyByCurrent = false;

	/** 用户是否在拖动播放进度条，true则不会UI发送onProgressChange通知 */
	private boolean isDragingSeekBar = false;

	/** 从失败时间开始 */
	private long mLastFailTime;
	/** 统计失败的次数 */
	private long mTimesFailed;
	
	/**
	 * 初始化
	 * */
	private PlayLogicManager() {
		mLastFailTime = 0;
		mTimesFailed = 0;
		mCurPosition = 0;
		mMediaPlayerObserver = new Hashtable<String, IRemotePlayerListener>();
		mRemotePlayServiceManager = new RemotePlayServiceManager();
		addObserver(PlayLogicManager.class.toString(), this);
		getSqlData();
	}

	/**
	 * 获取单例对象
	 * 
	 * @author Erica
	 * @return PlayManage 播放控制类对象
	 * */
	public static PlayLogicManager newInstance() {
		if (mPlayLogicManager == null) {
			synchronized (PlayLogicManager.class) {
				if (mPlayLogicManager == null) {
					mPlayLogicManager = new PlayLogicManager();
				}
			}
		}
		return mPlayLogicManager;
	}

	public void getSqlData() {

		new Thread() {
			@Override
			public void run() {

				List<MusicInfo> musicInfos = LocalMusicManager.getInstence()
						.getCurrentPlayingMusicList();

				if (musicInfos != null && musicInfos.size() > 0) {
					setMusicInfo(musicInfos, mCurPosition, mSql);
				} else {
					musicInfos = LocalMusicManager.getInstence().getAllMusic();
					if (musicInfos != null && musicInfos.size() > 0) {
						setMusicInfo(musicInfos, mCurPosition, mSql);
					} else {
						// 属性全部置空
						mCurMusic = null;
						mCurMusicList = null;
						mRandomList = null;
						mCurPosition = -1;
						mCurRandomPosition = -1;
					}
				}

			}
		}.start();
	}

	/**
	 * 设置音乐对象、音乐列表
	 * 
	 * @author Erica
	 * @param list
	 *            List<MusicInfo> 当前播放歌单
	 * @param index
	 *            int 当前播放索引
	 * @return null
	 * */
	public void setMusicInfo(List<MusicInfo> list, int index, String _mSql) {
		// TODO Auto-generated method stub
		if (index != -1) {
			mCurMusic = list.get(index);

		}
		if (mSql != null && !mSql.equals(_mSql)) {
			mSql = _mSql;
			UserData.getInstence().setCurrentOperateMusicListSql(mSql);
		}
		isNetData = false;
		mCurPosition = index;
		mCurMusicList = list;
		makeRandomList();
	}
	
	/**
	 * 设置音乐对象、音乐列表
	 * 
	 * @author Erica
	 * @param list
	 *            List<MusicInfo> 当前播放歌单
	 * @param index
	 *            int 当前播放索引
	 * @return null
	 * */
	public void setMusicInfoAndPlay(List<MusicInfo> list, int index, String _mSql) {
		// TODO Auto-generated method stub
		if (index != -1) {
			mCurMusic = list.get(index);

		}
		if (mSql != null && !mSql.equals(_mSql)) {
			mSql = _mSql;
			UserData.getInstence().setCurrentOperateMusicListSql(mSql);
		}
		isNetData = false;
		mCurPosition = index;
		mCurMusicList = list;
		makeRandomList();
		play(mCurMusic);
	}

	/**
	 * 设置非本地歌曲列表方法
	 * 
	 * @author Erica
	 * @param list
	 *            List<OlSongVO> 当前播放歌单
	 * @param index
	 *            int 当前播放索引
	 * @return null
	 * */
	public void setOnlineMusicInfo(List<OlSongVO> list, int index) {
		try {
			mCurOnlineMusicList = list;
			isNetData = true;
			isNewCurMusic = true;
			mCurPosition = index;
			setOnlineMusicItem(mCurOnlineMusicList, mCurPosition);
			makeRandomList();
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	/**
	 * 获取当前播放对象
	 * 
	 * @author Erica
	 * @return mCurMusic
	 * */
	public MusicInfo getmCurMusic() {
		return mCurMusic;
	}

	/**
	 * 获取当前播放的时长。
	 * @return
	 * @author Perry
	 */
	public int getDuration() {
		return mRemotePlayServiceManager.getDuration();
	}
	
	/**
	 * 设置当前播放对象
	 * 
	 * @author Erica
	 * @param mCurMusic
	 *            MusicInfo 设置播放对象
	 * */
	public void setmCurMusic(MusicInfo mCurMusic) {
		this.mCurMusic = mCurMusic;
	}

	/**
	 * 获取当前播放位置
	 * 
	 * @author Erica
	 * @return mCurPosition
	 * */
	public int getmCurPosition() {
		return mCurPosition;
	}

	/**
	 * 设置当前播放位置
	 * 
	 * @author Erica
	 * @param mCurPosition
	 *            int 设置播放位置
	 * */
	public void setmCurPosition(int mCurPosition) {
		this.mCurPosition = mCurPosition;
	}

	/**
	 * 获取当前播放歌单SQL语句
	 * 
	 * @author Erica
	 * @return mSql
	 * */
	public String getmSql() {
		return mSql;
	}

	/**
	 * 设置当前播放歌单SQL语句
	 * 
	 * @author Erica
	 * @param _mSql
	 *            String SQL语句对象
	 * */
	public void setmSql(String _mSql) {
		this.mSql = _mSql;
	}

	/**
	 * 获取播放模式
	 * 
	 * @author Erica
	 * @return mPlayType
	 * */
	public int getPlayType() {
		return mPlayType;
	}

	public boolean isNetData() {
		return isNetData;
	}

	public void setNetData(boolean isNetData) {
		this.isNetData = isNetData;
	}

	/**
	 * 设置播放模式 *
	 * 
	 * @author Erica
	 * @param playType
	 *            int 设置播放类型
	 * */
	public void setPlayType(int playType) {
		this.mPlayType = playType;

		if ((!isNetData && mCurMusicList != null)
				|| (isNetData && mCurOnlineMusicList != null)) {
			if (mPlayType == 2) {
				makeRandomList();
			}
		}
	}

	/**
	 * 是否需要刷新播放数据
	 * */
	public boolean isGetRefresh() {
		return isGetRefresh;
	}

	/**
	 * 设置是否需要刷新播放数据
	 * */
	public void setGetRefresh(boolean isGetRefresh) {
		this.isGetRefresh = isGetRefresh;
	}

	public boolean play(MusicInfo musicInfo) {

		if (musicInfo != null) {
			
			mCurMusic = musicInfo;
			
			String path = "";
			if (mCurMusic.getLocalUrl() != null) {
				File music_temp = new File(mCurMusic.getLocalUrl());
				if (music_temp.exists()) {
					path = mCurMusic.getLocalUrl();
				} else {
					CommonUtils.showToast(TianlApp.newInstance(),
							R.string.string_nofile, Toast.LENGTH_SHORT);
					stop();
				}
			}
			if (mCurMusic.getFileID()!=null) {
				if (!(isCached(mCurMusic.getFileID()))) {
					isNetData = true;
				} else {
					isNetData = false;
				}
			}

			if (!isNetData) {
				mRemotePlayServiceManager.startPlayMusic(path);
			} else {
				//重新获取链接下载地址集合
				OnlineMusicManager.getInstence().getDownloadUrlData(TianlApp.newInstance(), new OnDataPreparedListener<String>(){

					@Override
					public void onDataPrepared(String data) {
						if (data != null) {	
							/*String url = data;
							if (url.startsWith("\"") && url.endsWith("\"")) {
								url = url.substring(url.indexOf("\"") + 1,
										url.lastIndexOf("\""));
							}
							mCurMusic.setNetUrl(url);*/
							try {
								mRemotePlayServiceManager.startPlayNetMusic(mCurMusic.getNetUrl(),
										mCurMusic.getTitle(),
										"" + mCurMusic.getFileID(),
										"" + mCurMusic.getGid(),
										mCurMusic.getArtist());
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							} catch (IllegalStateException e) {
								e.printStackTrace();
							}
						}else{
							Lg.d("getSongUrlData() == null");
							return;
						}
					}
				}, CommonUtils.getDownloadListUrl(""+mCurMusic.getGid()));
			}

		}
		return false;
	}

	/**
	 * 播放
	 * 
	 * @author Erica
	 * @throws IOException
	 * @throws IllegalStateException
	 */
	public boolean play() {
		if (mCurMusic != null){
			PlayerNotification.getInstence().update(mCurMusic.getTitle(),
					mCurMusic.getArtist());
			// 播放器已经启动
			boolean isPrepared = mRemotePlayServiceManager.isPrepared();
			if (isPrepared && !isNewCurMusic){
				mRemotePlayServiceManager.play();
				return true;
			}else{
				isNewCurMusic = false;
				play(mCurMusic);
			}
		}
		return false;
	}

	/**
	 * 暂停
	 * 
	 * @author Erica
	 */
	public boolean pause() {

		if (mCurMusic != null) {
			mRemotePlayServiceManager.pause();
			return true;
		}
		return false;
	}

	/**
	 * 下一首
	 * 
	 * @author Erica
	 * @param isClicked
	 *            boolean 是否是手动触发
	 * @return true 操作成功 false 操作失败
	 * @throws IOException
	 * @throws IllegalStateException
	 * @note 修改非本地歌曲播放下一首逻辑 10.8
	 */
	public boolean next(boolean isClicked) throws IllegalStateException,
			IOException {
		boolean isHaveNext = false;
		if (isNetData) {
			isHaveNext = checkOnlineNext(mCurOnlineMusicList, mCurPosition,
					isClicked);
		} else {
			isHaveNext = checkNext(mCurMusicList, mCurPosition, isClicked);
		}
		if (isHaveNext) {
			if (mCurMusic != null) {
				String path = "";
				if (mCurMusic.getLocalUrl() != null) {
					File music_temp = new File(mCurMusic.getLocalUrl());
					if (music_temp.exists()) {
						path = mCurMusic.getLocalUrl();
					} else {
						// Toast
						CommonUtils.showToast(TianlApp.newInstance(),
								R.string.string_nofile, Toast.LENGTH_SHORT);
						stop();
					}
				}
				if (mCurMusic.getFileID() !=null) {
					if (!(isCached("" + mCurMusic.getFileID()))) {
						isNetData = true;
					} else {
						isNetData = false;
					}
				}
				PlayerNotification.getInstence().update(mCurMusic.getTitle(),
						mCurMusic.getArtist());
				if (!isClicked) {
					if (isNetData) {
						//重新获取链接下载地址集合
						OnlineMusicManager.getInstence().getDownloadUrlData(
								TianlApp.newInstance(),
								new OnDataPreparedListener<String>() {

									@Override
									public void onDataPrepared(String data) {
										if (data != null) {
											/*String url = data;
											if (url.startsWith("\"")
													&& url.endsWith("\"")) {
												url = url.substring(
														url.indexOf("\"") + 1,
														url.lastIndexOf("\""));
											}
											mCurMusic.setNetUrl(url);*/
											try {
												if (CommonUtils.isWifiConnected(TianlApp
														.newInstance())) {
													mRemotePlayServiceManager
															.startPlayNetMusic(
																	mCurMusic.getNetUrl(),
																	mCurMusic.getTitle(),
																	""+ mCurMusic.getFileID(),
																	"" + mCurMusic.getGid(),
																	mCurMusic.getArtist());
												} else {
													if (SPHelper.newInstance()
															.getNetWorkPlayModel() == 1) {
														return;
													} else {
														mRemotePlayServiceManager
																.startPlayNetMusic(
																		mCurMusic.getNetUrl(),
																		mCurMusic.getTitle(),
																		""+ mCurMusic.getFileID(),
																		""+ mCurMusic.getGid(),
																		mCurMusic.getArtist());
													}
												}
											} catch (IllegalArgumentException e) {
												// TODO Auto-generated catch block
												// e.printStackTrace();
											} catch (IllegalStateException e) {
												// TODO Auto-generated catch block
												// e.printStackTrace();
											}
										} else {
											Lg.d("getSongUrlData() == null");
											return;
										}
									}
								},
								CommonUtils.getDownloadListUrl(mCurMusic
										.getGid() + ""));
					} else {
						mRemotePlayServiceManager.startPlayMusic(path);
					}

				} else {
					if (!isNetData) {
						mRemotePlayServiceManager.startPlayMusic(path);
					} else {
						//重新获取链接下载地址集合
						OnlineMusicManager.getInstence().getDownloadUrlData(
								TianlApp.newInstance(),
								new OnDataPreparedListener<String>() {

									@Override
									public void onDataPrepared(String data) {
										if (data != null) {
											/*String url = data;
											if (url.startsWith("\"")
													&& url.endsWith("\"")) {
												url = url.substring(
														url.indexOf("\"") + 1,
														url.lastIndexOf("\""));
											}
											mCurMusic.setNetUrl(url);*/
											try {
												mRemotePlayServiceManager
														.startPlayNetMusic(
																mCurMusic.getNetUrl(),
																mCurMusic.getTitle(),
																""+ mCurMusic.getFileID(),
																""+ mCurMusic.getGid(),
																mCurMusic.getArtist());
											} catch (IllegalArgumentException e) {
												// TODO Auto-generated catch
												// block
												// e.printStackTrace();
											} catch (IllegalStateException e) {
												// TODO Auto-generated catch
												// block
												// e.printStackTrace();
											}

										} else {
											Lg.d("getSongUrlData() == null");
											return;
										}
									}
								},
								CommonUtils.getDownloadListUrl(mCurMusic
										.getGid() + ""));
					}
				}

				return true;
			}
		} else {
			if (isClicked)
				CommonUtils.showToast(TianlApp.newInstance(),
						R.string.local_have_no_song_end, Toast.LENGTH_SHORT);
		}
		return false;
	}

	/**
	 * 获取下一首
	 * 
	 * @author Erica
	 * @param list
	 *            LinkedList<MusicInfo> 当前播放歌单
	 * @param index
	 *            int 当前播放索引
	 * @return true 操作成功 false 操作失败
	 * @note 随机获取播放完一首从列表中移除该条目，如果到达0条目，则重新获取随机队列 由于已对随机队列进行处理，所以每次获取队列中第一条
	 *       逻辑处理更改单曲循环，手动触发更改播放列表中位置 Edit by Erica on 9-17
	 */
	public boolean checkNext(List<MusicInfo> list, int index, boolean _isClicked) {
		if (list == null || list.size() == 0) {
			return false;
		}
		switch (mPlayType) {
		case TYPE_SINGLE:
			if (!_isClicked) {// 逻辑处理更改单曲循环，手动触发更改播放列表中位置 Edit by Erica on 9-17
				mCurPosition = index;
			} else {
				index = (index + 1) % list.size();
				mCurPosition = index;
			}
			break;
		case TYPE_CYCLE:
			if (list.size() == 1) {
				mCurPosition = index;
			} else {
				index = (index + 1) % list.size();
				mCurPosition = index;
			}
			break;
		case TYPE_RANDOM:
			if (list.size() == 1) {
				mCurPosition = index;
			} else {
				mCurPosition = mRandomList.get(mCurRandomPosition);
				mRandomList.remove(mCurRandomPosition);
				if (mRandomList.size() == 0) {
					makeRandomList();
				}
			}
			break;
		case TYPE_ORDER:
			if ((index + 1) < list.size()) {
				index = index + 1;
				mCurPosition = index;
			} else {
				return false;
			}
			break;
		}
		mCurMusic = list.get(mCurPosition);
		return true;
	}

	/**
	 * 获取下一首
	 * 
	 * @author Erica
	 * @param list
	 *            LinkedList<OlSongVO> 当前播放歌单
	 * @param index
	 *            int 当前播放索引
	 * @return true 操作成功 false 操作失败
	 * @note 随机获取播放完一首从列表中移除该条目，如果到达0条目，则重新获取随机队列 由于已对随机队列进行处理，所以每次获取队列中第一条
	 *       逻辑处理更改单曲循环，手动触发更改播放列表中位置 Edit by Erica on 9-17
	 */
	public boolean checkOnlineNext(List<OlSongVO> list, int index,
			boolean _isClicked) {
		if (list == null || list.size() == 0) {
			return false;
		}
		switch (mPlayType) {
		case TYPE_SINGLE:
			if (!_isClicked) {// 逻辑处理更改单曲循环，手动触发更改播放列表中位置 Edit by Erica on 9-17
				mCurPosition = index;
			} else {
				index = (index + 1) % list.size();
				mCurPosition = index;
			}
			break;
		case TYPE_CYCLE:
			if (list.size() == 1) {
				mCurPosition = index;
			} else {
				index = (index + 1) % list.size();
				mCurPosition = index;
			}
			break;
		case TYPE_RANDOM:
			if (list.size() == 1) {
				mCurPosition = index;
			} else {
				mCurPosition = mRandomList.get(mCurRandomPosition);
				mRandomList.remove(mCurRandomPosition);
				if (mRandomList.size() == 0) {
					makeRandomList();
				}
			}
			break;
		case TYPE_ORDER:
			if ((index + 1) < list.size()) {
				index = index + 1;
				mCurPosition = index;
			} else {
				return false;
			}
			break;
		}
		setOnlineMusicItem(list, mCurPosition);
		return true;
	}

	/**
	 * 设置当前非本地播放音乐对象
	 * 
	 * @author Erica
	 * */
	public void setOnlineMusicItem(List<OlSongVO> _list, int position) {
		final OlSongVO online_item = _list.get(position);
		if (online_item == null)
			return;
		MusicInfo play_item = new MusicInfo();
		if (online_item.getSinger() != null)
			play_item.setArtist(online_item.getSinger());
		else {
			play_item.setArtist("");
		}
		if (online_item.getSong() != null)
			play_item.setTitle(online_item.getSong());
		if (online_item.getTime() != null)
			play_item.setDuration(CommonUtils.timeStringTosecond(online_item
					.getTime()));
		if (online_item.getGid() != null)
			play_item.setGid(Long.parseLong(online_item.getGid()));
		if (online_item.getLowId() != null) {
			play_item.setFileID(online_item.getLowId());
		} else if (online_item.getRingId() != null) {
			play_item.setFileID(online_item.getRingId());
			if (online_item.getRingTime() != null)
				play_item.setDuration(CommonUtils
						.timeStringTosecond(online_item.getRingTime()));
		} else {
			play_item.setFileID(online_item.getHighId());
		}
		if (online_item.getLyricId() != null
				&& !online_item.getLyricId().equals("0")) {// 歌词id add by andrew
			play_item.setLyricId(online_item.getLyricId());
		}
		mCurMusic = play_item;
	}

	/**
	 * 上一首
	 * 
	 * @author Erica
	 * @return true 操作成功 false 操作失败
	 * @throws IOException
	 * @throws IllegalStateException
	 */
	public boolean prev() throws IllegalStateException, IOException {
		boolean isPrevious = false;
		if (isNetData) {
			isPrevious = checkOnlinePrevious(mCurOnlineMusicList, mCurPosition);
		} else {
			isPrevious = checkPrevious(mCurMusicList, mCurPosition);
		}
		if (isPrevious) {
			if (mCurMusic != null) {
				PlayerNotification.getInstence().update(mCurMusic.getTitle(),
						mCurMusic.getArtist());
				String path = "";
				if (mCurMusic.getLocalUrl() != null) {
					File music_temp = new File(mCurMusic.getLocalUrl());
					if (music_temp.exists()) {
						path = mCurMusic.getLocalUrl();
					} else {
						CommonUtils.showToast(TianlApp.newInstance(),
								R.string.string_nofile, Toast.LENGTH_SHORT);
						stop();
					}
				}
				if (mCurMusic.getFileID() !=null) {
					if (!(isCached("" + mCurMusic.getFileID()))) {
						isNetData = true;
					} else {
						isNetData = false;
					}
				}
				
				if (!isNetData) {
					
					mRemotePlayServiceManager.startPlayMusic(path);
				} else {
					//重新获取链接下载地址集合
					OnlineMusicManager.getInstence().getDownloadUrlData(TianlApp.newInstance(), new OnDataPreparedListener<String>() {

						@Override
						public void onDataPrepared(String data) {
							if (data != null) {	
								/*String url = data;
								if (url.startsWith("\"") && url.endsWith("\"")) {
									url = url.substring(url.indexOf("\"") + 1,
											url.lastIndexOf("\""));
								}
								mCurMusic.setNetUrl(url);*/
								try {
									mRemotePlayServiceManager.startPlayNetMusic(
											mCurMusic.getNetUrl(), mCurMusic.getTitle(), ""
													+ mCurMusic.getFileID(), ""
													+ mCurMusic.getGid(),
											mCurMusic.getArtist());
								} catch (IllegalArgumentException e) {
									// TODO Auto-generated catch block
									// e.printStackTrace();
								} catch (IllegalStateException e) {
									// TODO Auto-generated catch block
									// e.printStackTrace();
								}
								
							}else {
								Lg.d("getSongUrlData() == null");
								return;
							}
							
						}
					}, CommonUtils.getDownloadListUrl(""+mCurMusic.getGid()));
				}
				return true;
			}
		} else {
			CommonUtils.showToast(TianlApp.newInstance(),
					R.string.local_have_no_song_first, Toast.LENGTH_SHORT);
		}
		return false;
	}
	public void playNet(MusicInfo musicInfo){
		
		//makeRandomList();
		
		try{
		   //System.out.println(isCached(musicInfo.getFileID()));
			
			if(!isCached(musicInfo.getFileID())){
				mRemotePlayServiceManager.startPlayNetMusic(
						musicInfo.getNetUrl(), musicInfo.getTitle(), musicInfo.getFileID()+"", musicInfo.getGid()+"",musicInfo.getArtist());
				mRemotePlayServiceManager.stop();	
				musicInfo.setLocalUrl(getLocalUrlByFileId(musicInfo.getFileID()));
			}else{
				musicInfo.setLocalUrl(getLocalUrlByFileId(musicInfo.getFileID()));
			}
			play(musicInfo);
		}catch(Exception e){
			
		}
	}
	/**
	 * 获取上一首
	 * 
	 * @author Erica
	 * @param list
	 *            LinkedList<MusicInfo> 当前播放歌单
	 * @param index
	 *            int 当前播放索引
	 * @return true 操作成功 false 操作失败
	 * @note 随机获取播放完一首从列表中移除该条目，如果到达0条目，则重新获取随机队列 由于已对随机队列进行处理，所以每次获取队列中第一条
	 *       逻辑处理更改单曲循环，手动触发更改播放列表中位置 Edit by Erica on 9-17
	 */
	public boolean checkPrevious(List<MusicInfo> list, int index) {
		if (list == null || list.size() == 0) {
			return false;
		}
		switch (mPlayType) {
		case TYPE_SINGLE:
			index = (list.size() + index - 1) % list.size();// 逻辑处理更改单曲循环，手动触发更改播放列表中位置
															// Edit by Erica on
															// 9-17
			mCurPosition = index;
			break;
		case TYPE_CYCLE:
			if (list.size() == 1) {
				mCurPosition = index;
			} else {
				index = (list.size() + index - 1) % list.size();
				mCurPosition = index;
			}
			break;
		case TYPE_RANDOM:
			if (list.size() == 1) {
				mCurPosition = index;
			} else {
				mCurPosition = mRandomList.get(mCurRandomPosition);
				mRandomList.remove(mCurRandomPosition);
				if (mRandomList.size() == 0) {
					makeRandomList();
				}
			}
			Lg.d("curPosition = " + mCurPosition, "curPosition = "
					+ mCurPosition);
			break;
		case TYPE_ORDER:
			if (index > 0) {
				index = index - 1;
				mCurPosition = index;
			} else {
				return false;
			}
			break;
		}
		mCurMusic = list.get(mCurPosition);
		return true;
	}

	/**
	 * 获取非本地上一首
	 * 
	 * @author Erica
	 * @param list
	 *            LinkedList<OlSongVO> 当前播放歌单
	 * @param index
	 *            int 当前播放索引
	 * @return true 操作成功 false 操作失败
	 * @note 随机获取播放完一首从列表中移除该条目，如果到达0条目，则重新获取随机队列 由于已对随机队列进行处理，所以每次获取队列中第一条
	 *       逻辑处理更改单曲循环，手动触发更改播放列表中位置 Edit by Erica on 9-17
	 */
	public boolean checkOnlinePrevious(List<OlSongVO> list, int index) {
		if (list == null || list.size() == 0) {
			return false;
		}
		switch (mPlayType) {
		case TYPE_SINGLE:
			index = (list.size() + index - 1) % list.size();// 逻辑处理更改单曲循环，手动触发更改播放列表中位置
															// Edit by Erica on
															// 9-17
			mCurPosition = index;
			break;
		case TYPE_CYCLE:
			if (list.size() == 1) {
				mCurPosition = index;
			} else {
				index = (list.size() + index - 1) % list.size();
				mCurPosition = index;
			}
			break;
		case TYPE_RANDOM:
			if (list.size() == 1) {
				mCurPosition = index;
			} else {
				mCurPosition = mRandomList.get(mCurRandomPosition);
				mRandomList.remove(mCurRandomPosition);
				if (mRandomList.size() == 0) {
					makeRandomList();
				}
			}
			break;
		case TYPE_ORDER:
			if (index > 0) {
				index = index - 1;
				mCurPosition = index;
			} else {
				return false;
			}
			break;
		}
		setOnlineMusicItem(list, mCurPosition);
		return true;
	}

	/**
	 * 检测下载列表的有效性。
	 * @param downloadVO
	 * @return
	 * @author Perry
	 */
	public String checkDownloadUrls(OlDownloadVO downloadVO){
		String url = null;
		if(downloadVO == null || downloadVO.getDataList() ==null || downloadVO.getDataList().size()<=0) {
			return null;
		}
		List<OlDownloadItem> urlList = downloadVO.getDataList();
		for(OlDownloadItem item: urlList) {
			if(item.getUrl() !=null) {
				String tempUrl = item.getUrl();
				HttpURLConnection conn = CommonUtils.getConnection(tempUrl,
						TianlApp.newInstance());
				try {
					conn.setRequestMethod("GET"); // 设置请求方式
					conn.setDoInput(true);
					conn.setConnectTimeout(15000);
					conn.setReadTimeout(200000);
					conn.setRequestProperty("Accept-Encoding", "gzip");
					conn.connect();
					int responseCode = conn.getResponseCode();
					if (responseCode == HttpStatus.SC_OK
							|| responseCode == HttpStatus.SC_PARTIAL_CONTENT) {
						String type = conn.getContentType();
						//增加判断类型是否是音频类型，或流
						if(type == null || (type !=null && (type.toLowerCase().contains("audio") || type.toLowerCase().contains("stream")))){
							url = tempUrl;
							break;
						}
					}
				} catch (ProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}finally{
					if (conn != null) {
						conn.disconnect();
						conn = null;
					}
				}
			}
		}
		return url;
	}
	
	/**
	 * 停止播放器
	 * 
	 * @author Erica
	 * @return true 操作成功 false 操作失败
	 */
	public boolean stop() {
		Lg.e("PlayLogicManage.stop", "停止播放音乐");
		if (mCurMusic != null) {
			mRemotePlayServiceManager.stop();
			return true;
		}
		return false;
	}

	/**
	 * 设置歌曲播放位置
	 * 
	 * @author Erica
	 * @param time
	 *            int 指定的播放时间
	 */
	public void seekTo(int time) {
		if (mCurMusic != null) {
			mRemotePlayServiceManager.seekTo(time);
		}
	}

	/**
	 * 通过数据库接口查询数据库表中是否有该音乐对象
	 * 
	 * @author Erica
	 * @param fileId
	 *            String 文件ID（只有非本地数据此字段才不为空）
	 * @return true 已经存在缓存文件 false 不存在缓存文件
	 * */
	private boolean isCached(String fileId) {

		List<MusicInfo> datas = LocalMusicManager.getInstence()
				.getMusicListByFileID(fileId);
		if (datas != null && datas.size() > 0) {
			MusicInfo info=datas.get(0);
			if (info.getLocalUrl() != null) {
				File music_temp = new File(info.getLocalUrl());
				if (music_temp.exists()) {
					return true;
				}
			}
		}

		return false;
	}
	/**
	 * 根据fileId获取本地链接
	 * 
	 * @author Erica
	 * @param fileId
	 *            String 文件ID（只有非本地数据此字段才不为空）
	 * @return true 已经存在缓存文件 false 不存在缓存文件
	 * */
	private String getLocalUrlByFileId(String fileId) {

		List<MusicInfo> datas = LocalMusicManager.getInstence()
				.getMusicListByFileID(fileId);
		if (datas != null && datas.size() > 0) {
			MusicInfo info=datas.get(0);
			if (info.getLocalUrl() != null) {
				File music_temp = new File(info.getLocalUrl());
				if (music_temp.exists()) {
					return info.getLocalUrl();
				}
			}
		}

		return null;
	}
	/**
	 * 得到当前播放的歌曲
	 * 
	 * @author Erica
	 * @return MusicInfo 当前音乐对象
	 * */
	public MusicInfo getMusicInfo() {
		if (mCurMusic == null) {
			return null;
		} else {
			return mCurMusic;
		}
	}

	/**
	 * 获取当前播放进度
	 * 
	 * @author Erica
	 * @return int
	 * */
	public int getBufferingProcess() {
		return 0;
	}

	/**
	 * 对外删除接口通知
	 * 
	 * @author Erica
	 * @param list
	 *            List<MusicInfo> 当前操作歌单
	 * @param index
	 *            int[] 当前删除索引数组
	 * @return true 操作成功 false 操作失败
	 * */
	public boolean recieveDeleteDataChange(List<MusicInfo> list, int[] _index) {
		isNotifyByCurrent = false;
		if (isNetData) {
			return true;
		}
		int tempPosition = mCurPosition;
		for (int i = 0; i < _index.length; i++) {
			if (mCurMusicList == null || list == null)
				return false;
			int index = mCurMusicList.indexOf(list.get(_index[i]));
			if (index != -1) {
				if (index == tempPosition) {
					isNotifyByCurrent = true;
					String cur_Path = SPHelper.newInstance().getFoldPath();
					if (cur_Path.equals(TianlApp.currentPlayPath)) {
						stop();
					}
				}
				switch (mPlayType) {
				case TYPE_SINGLE:
					if (index == mCurPosition) {
						mCurPosition = (mCurMusicList.size() - 1 + mCurPosition + 1)
								% (mCurMusicList.size());
					} else {
						if (index < mCurPosition)
							mCurPosition = (mCurMusicList.size() + mCurPosition - 1)
									% (mCurMusicList.size());
					}
					break;
				case TYPE_CYCLE:
					if (index == mCurPosition) {
						mCurPosition = (mCurMusicList.size() + mCurPosition - 1)
								% (mCurMusicList.size());
					} else {
						if (index < mCurPosition)
							mCurPosition = (mCurMusicList.size() + mCurPosition - 1)
									% (mCurMusicList.size());
					}
					break;
				case TYPE_RANDOM:
					if (index == mCurPosition) {
						if (mRandomList != null) {
							mRandomList.remove(mCurRandomPosition);
						}
					} else {
						if (mRandomList != null) {
							mRandomList.remove(mRandomList.indexOf(index));
						}
					}

					if (mRandomList == null || mRandomList.size() <= 0) {
						makeRandomList();
					}
					break;
				case TYPE_ORDER:
					if (index == mCurPosition || index < mCurPosition) {
						if ((mCurPosition - 1) >= 0) {
							mCurPosition = mCurPosition - 1;
						} else {
							if ((mCurPosition == index) && (mCurPosition == 0)) {
								mCurPosition = -1;
							}
						}
					}
					break;
				}
				Lg.d("curPosition = " + mCurPosition, "curPosition = "
						+ mCurPosition);
			}
		}
		return true;
	}

	/**
	 * 对外添加接口通知
	 * 
	 * @author Erica
	 * @param list
	 *            List<MusicInfo> 当前操作歌单
	 * @param _num
	 *            int 插入个数
	 * @return true 操作成功 false 操作失败
	 * */
	public boolean recieveAddDataChange(/* List<MusicInfo> list, int _num */) {
		/*
		 * switch (mPlayType) { case TYPE_SINGLE: mCurPosition = mCurPosition +
		 * _num; break; case TYPE_CYCLE: mCurPosition = mCurPosition + _num;
		 * break; case TYPE_RANDOM: int size = mRandomList.size(); for (int i =
		 * 0; i < _num; i++) { mRandomList.add(size + i, list.size() + i);
		 * mCurPosition = mRandomList.get(mCurRandomPosition); } break; case
		 * TYPE_ORDER: mCurPosition = mCurPosition + _num; break; }
		 * Log.d("curPosition = " + mCurPosition, "curPosition = " +
		 * mCurPosition);
		 */
		return true;
	}

	/**
	 * 歌单删除操作回调函数，重新从数据库获取最新播放列表
	 * 
	 * @author Erica
	 * @return true 操作成功 false 操作失败
	 * */
	public boolean setCurMusicListInfoAfterDelete() {
		// 启动线程执行插入歌单
		if (isNetData) {
			return true;
		}
		new Thread() {
			@Override
			public void run() {
				try {
					List<MusicInfo> musicInfos = new ArrayList<MusicInfo>();
					musicInfos = LocalMusicManager.getInstence()
							.getMusicListBySql(mSql, null);
					if (musicInfos != null && musicInfos.size() > 0) {

						if (isNotifyByCurrent) {
							int index = musicInfos.indexOf(mCurMusic);
							if (index != -1) {
								setMusicInfo(musicInfos, index, mSql);
							} else {
								setMusicInfo(musicInfos, mCurPosition, mSql);
								if (musicInfos.size() >= 1) {
									next(false);
								} else {
									if (musicInfos.size() > 0)
										play();
								}
							}
						}

					} else {
						musicInfos = LocalMusicManager.getInstence()
								.getAllMusic();
						if (musicInfos != null && musicInfos.size() > 0) {
							mSql = SqlString
									.getSqlForSelectAllMusicOrderByAddedDate();
							setMusicInfo(musicInfos, 0, mSql);
						} else {
							// 属性全部置空
							mCurMusic = null;
							mCurMusicList = null;
							mRandomList = null;
							mCurPosition = -1;
							mCurRandomPosition = -1;
						}
						isGetRefresh = true;
						TianlApp.newInstance()
								.sendBroadcast(
										new Intent(
												IntentAction.INTENT_BROADCAST_UPDATE_MUSIC_VIEW));
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
				}
			}
		}.start();

		return true;
	}

	/**
	 * 歌单增加操作回调函数，重新从数据库获取最新播放列表
	 * 
	 * @author Erica
	 * @return true 操作成功 false 操作失败
	 * */
	public boolean setCurMusicListInfoAfterAdd() {
		// 启动线程执行插入歌单
		if (isNetData) {
			return true;
		}
		new Thread() {
			@Override
			public void run() {
				try {
					List<MusicInfo> musicInfos = new ArrayList<MusicInfo>();
					musicInfos = LocalMusicManager.getInstence()
							.getMusicListBySql(mSql, null);
					if (musicInfos != null && musicInfos.size() > 0) {
						setMusicInfo(musicInfos, mCurPosition, mSql);
					} else {
						musicInfos = LocalMusicManager.getInstence()
								.getAllMusic();
						if (musicInfos != null && musicInfos.size() > 0) {
							int index = musicInfos.indexOf(mCurMusic);
							setMusicInfo(musicInfos, index, mSql);
						} else {
							// 属性全部置空
							mCurMusic = null;
							mCurMusicList = null;
							mRandomList = null;
							mCurPosition = -1;
							mCurRandomPosition = -1;
						}
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
				}
			}
		}.start();

		return true;
	}

	/**
	 * 创建随机队列
	 * 
	 * @author Erica
	 * @param list
	 *            LinkedList<MusicInfo> 当前播放歌单
	 * @return null
	 * */
	public void makeRandomList() {
		mCurRandomPosition = 0;
		int size = 0;
		if (isNetData) {
			size = mCurOnlineMusicList.size();
		} else {
			size = mCurMusicList.size();
		}

		mRandomList = new ArrayList<Integer>();
		for (int i = 0; i < size; i++) {
			mRandomList.add(i);
		}
		// 打乱 洗牌
		Collections.shuffle(mRandomList);

	}

	/**
	 * 获取当前播放状态
	 * 
	 * @author Erica
	 * @return isPlaying
	 * */
	public boolean getIsPlaying() {
		boolean isPlaying = mRemotePlayServiceManager.isPlaying();
		return isPlaying;
	}

	// TODO以下代码段时有效的，但暂时被注释掉了，需进一步处理
	// @Override
	// public void onError(int what, int extra) {
	// // TODO Auto-generated method stub
	// if (what == MediaPlayer.MEDIA_ERROR_UNKNOWN) {
	// stop();
	// }
	// if (what == -1) {
	// long failTime = System.currentTimeMillis();
	// if (failTime - mLastFailTime > FAIL_TIME_FRAME) {
	// mTimesFailed = 1;
	// mLastFailTime = failTime;
	// } else {
	// mTimesFailed++;
	// if (mTimesFailed > ACCEPTABLE_FAIL_NUMBER) {
	// stop();
	// }
	// }
	// }
	// }

	public void dealIntent(Uri uri) {
		File sdCardFile = Environment.getExternalStorageDirectory();
		String musicPath = null;

		if (uri.getScheme().equals("content")) {// 处理Uri协议为content的
			musicPath = ScanUtil.getRealPathFromURI(TianlApp.newInstance(), uri);
		} else if (uri.getScheme().equals("file")) {// 处理Uri协议为file的
			musicPath = uri.getPath();
			String[] sdCardPaths = new String[] { "/mnt/sdcard", "/sdcard" };
			for (String sdcardPath : sdCardPaths) {
				if (musicPath.startsWith(sdcardPath)) {
					musicPath = musicPath.replace(sdcardPath,
							sdCardFile.getAbsolutePath());
				}
			}
		}

		MusicInfo music = null;

		// 根据音乐文件路径判断是否存在宜搜数据库中。
		List<MusicInfo> selectMusicInfos = LocalMusicManager.getInstence()
				.getMusicListByMusicPath(musicPath);
		if (selectMusicInfos != null && selectMusicInfos.size() > 0) {// 存在宜搜数据库中。
			music = selectMusicInfos.get(0);
			PlayIntentMusic(music);
		} else {// 歌曲不存在宜搜数据库中。
			ISingleMediaScannerListener singleMediaScannerListener = new ISingleMediaScannerListener() {

				@Override
				public void onSingleMediaScannerBegin() {

				}

				@Override
				public void onSingleMediaFail(String path,
						SingleMediaScannerErrorType errorType) {
					PlayIntentMusic(null);
				}

				@Override
				public void onSingleMediaCompleted(String path,
						MusicInfo musicInfo) {
					PlayIntentMusic(musicInfo);
				}
			};
			SingleMediaScanner scanner = new SingleMediaScanner(
					singleMediaScannerListener);
			scanner.scanFile(musicPath);
		}

	}

	/**
	 * 内部调用播放歌曲。
	 * 
	 * @param musicInfo
	 * @author Perry
	 */
	private void PlayIntentMusic(MusicInfo musicInfo) {
		if (musicInfo != null) {
			List<MusicInfo> musicInfos = LocalMusicManager.getInstence()
					.getAllMusic();
			String send_sql = SqlString
					.getSqlForSelectAllMusicOrderByAddedDate();
			int index = musicInfos.indexOf(musicInfo);
			try {
				PlayLogicManager.newInstance().setMusicInfo(musicInfos, index,
						send_sql);
				PlayLogicManager.newInstance().play();
				TianlApp.currentPlayPath = TianlApp.newInstance().getString(
						R.string.all_music);
				SPHelper.newInstance().setFoldPath(TianlApp.currentPlayPath);
			} catch (IllegalStateException e) {
				// e.printStackTrace();
			}
		} else {
			Toast.makeText(TianlApp.newInstance(), "暂不支持该播放格式，抱歉！",
					Toast.LENGTH_SHORT).show();
		}
	}

	public Uri getIntentData() {
		return intentData;
	}

	public void setIntentData(Uri intentData) {
		this.intentData = intentData;
	}

	public void addObserver(String observerName,
			IRemotePlayerListener playerListener) {
		mMediaPlayerObserver.put(observerName, playerListener);
	}

	public void deleteObserver(String observerName) {
		mMediaPlayerObserver.remove(observerName);
	}

	public void bindService() {
		mRemotePlayServiceManager.bind(TianlApp.newInstance(), new Runnable() {
			@Override
			public void run() {
				mRemotePlayServiceManager.addObserver(
						PlayerListener.class.toString(), new PlayerListener());

			}
		});
	}

	public void unbindService() {
		mRemotePlayServiceManager.unbind(TianlApp.newInstance());
	}

	public class PlayerListener extends IRemotePlayerListener.Stub {

		@Override
		public void onError(int what, int extra) throws RemoteException {
			Iterator<Entry<String, IRemotePlayerListener>> iterator = mMediaPlayerObserver
					.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, IRemotePlayerListener> entry = iterator.next();

				IRemotePlayerListener observer = entry.getValue();
				observer.onError(what, extra);
			}
		}

		@Override
		public void onPreparing() throws RemoteException {
			Iterator<Entry<String, IRemotePlayerListener>> iterator = mMediaPlayerObserver
					.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, IRemotePlayerListener> entry = iterator.next();

				IRemotePlayerListener observer = entry.getValue();
				observer.onPreparing();
			}
		}

		@Override
		public void onPrepared() throws RemoteException {
			Iterator<Entry<String, IRemotePlayerListener>> iterator = mMediaPlayerObserver
					.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, IRemotePlayerListener> entry = iterator.next();

				IRemotePlayerListener observer = entry.getValue();
				observer.onPrepared();
			}
		}

		@Override
		public void onStartBuffer() throws RemoteException {
			Iterator<Entry<String, IRemotePlayerListener>> iterator = mMediaPlayerObserver
					.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, IRemotePlayerListener> entry = iterator.next();

				IRemotePlayerListener observer = entry.getValue();
				observer.onStartBuffer();
			}
		}

		@Override
		public void onBufferingUpdate(int percent) throws RemoteException {
			Iterator<Entry<String, IRemotePlayerListener>> iterator = mMediaPlayerObserver
					.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, IRemotePlayerListener> entry = iterator.next();

				IRemotePlayerListener observer = entry.getValue();
				observer.onBufferingUpdate(percent);
			}
		}

		@Override
		public void onStartPlay() throws RemoteException {
			Iterator<Entry<String, IRemotePlayerListener>> iterator = mMediaPlayerObserver
					.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, IRemotePlayerListener> entry = iterator.next();

				IRemotePlayerListener observer = entry.getValue();
				observer.onStartPlay();
			}
		}

		@Override
		public void onMusicPause() throws RemoteException {
			Iterator<Entry<String, IRemotePlayerListener>> iterator = mMediaPlayerObserver
					.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, IRemotePlayerListener> entry = iterator.next();

				IRemotePlayerListener observer = entry.getValue();
				observer.onMusicPause();
			}
		}

		@Override
		public void onProgressChanged(int currentMilliseconds)
				throws RemoteException {
			// 用户是否在拖动播放进度条，true则不会UI发送onProgressChange通知
			if (!isDragingSeekBar) {
				Iterator<Entry<String, IRemotePlayerListener>> iterator = mMediaPlayerObserver
						.entrySet().iterator();
				while (iterator.hasNext()) {
					Entry<String, IRemotePlayerListener> entry = iterator
							.next();

					IRemotePlayerListener observer = entry.getValue();
					observer.onProgressChanged(currentMilliseconds);
				}
			}
		}

		@Override
		public void onCompletion() throws RemoteException {
			Iterator<Entry<String, IRemotePlayerListener>> iterator = mMediaPlayerObserver
					.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, IRemotePlayerListener> entry = iterator.next();

				IRemotePlayerListener observer = entry.getValue();
				observer.onCompletion();
			}
		}

		@Override
		public void onMusicStop() throws RemoteException {
			Iterator<Entry<String, IRemotePlayerListener>> iterator = mMediaPlayerObserver
					.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, IRemotePlayerListener> entry = iterator.next();

				IRemotePlayerListener observer = entry.getValue();
				observer.onMusicStop();
			}
		}

		@Override
		public void onBufferComplete() throws RemoteException {
			Iterator<Entry<String, IRemotePlayerListener>> iterator = mMediaPlayerObserver
					.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, IRemotePlayerListener> entry = iterator.next();

				IRemotePlayerListener observer = entry.getValue();
				observer.onBufferComplete();
			}
		}

		@Override
		public void onBuffer() throws RemoteException {
			Iterator<Entry<String, IRemotePlayerListener>> iterator = mMediaPlayerObserver
					.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, IRemotePlayerListener> entry = iterator.next();

				IRemotePlayerListener observer = entry.getValue();
				observer.onBuffer();
			}
		}

		@Override
		public void onCacheUpdate(long currentCache) throws RemoteException {
			Iterator<Entry<String, IRemotePlayerListener>> iterator = mMediaPlayerObserver
					.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, IRemotePlayerListener> entry = iterator.next();

				IRemotePlayerListener observer = entry.getValue();
				observer.onCacheUpdate(currentCache);
			}
		}

	}

	/** 用户是否在拖动播放进度条，true则不会UI发送onProgressChange通知 */
	public boolean isDragingSeekBar() {
		return isDragingSeekBar;
	}

	/** 用户是否在拖动播放进度条，true则不会UI发送onProgressChange通知 */
	public void setDragingSeekBar(boolean isDragingSeekBar) {
		this.isDragingSeekBar = isDragingSeekBar;
	}

	@Override
	public IBinder asBinder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onError(int what, int extra) throws RemoteException {
		// TODO Auto-generated method stub
		if (what == MediaPlayer.MEDIA_ERROR_UNKNOWN) {
			stop();
		}
		if (what == -1) {
			long failTime = System.currentTimeMillis();
			if (failTime - mLastFailTime > FAIL_TIME_FRAME) {
				mTimesFailed = 1;
				mLastFailTime = failTime;
			} else {
				mTimesFailed++;
				if (mTimesFailed > ACCEPTABLE_FAIL_NUMBER) {
					stop();
				}
			}
		}
	}

	@Override
	public void onPreparing() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPrepared() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStartBuffer() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onBufferingUpdate(int percent) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStartPlay() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMusicPause() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProgressChanged(int currentMilliseconds)
			throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCompletion() throws RemoteException {
		// TODO Auto-generated method stub
		try {
			next(false);
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onMusicStop() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onBufferComplete() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onBuffer() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCacheUpdate(long currentCache) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	
	
}
