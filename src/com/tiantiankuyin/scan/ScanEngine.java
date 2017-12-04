package com.tiantiankuyin.scan;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;

import com.tiantiankuyin.bean.MusicInfo;
import com.tiantiankuyin.database.bll.LocalMusicManager;
import com.tiantiankuyin.utils.Lg;
import com.tiantiankuyin.utils.MusicOperate;
import com.tiantiankuyin.TianlApp;
/**
 * @author P
 * 
 */
public class ScanEngine {

	private final int MIN_FOLDER_NUM = 20; // 显示扫描文件夹进度的最少文件夹个数。

	private static ScanEngine mScanEngine;
	private IMediaScannerListener mMediaScannerListener;// 扫描监听器

	public static int scanViewState = -1; // 扫描页面 当前扫描引擎的状态。0为未扫描，1为正在扫描，2为扫描已结束
	public static int scanViewInsertNum = 0; // 扫描页面 插入数据库的歌曲数量
	public static int scanViewBarMax = 0; // 扫描页面 进度条最大值
	public static int scanViewBarCurrent = 0; // 扫描页面 进度条当前值
	// public static int scanViewScannedNum = 0; // 扫描页面 扫描到的音频文件数量。

	private List<String> insertedMusicPaths; // 已经插入到宜搜媒体库的音频文件路径

	private List<String> allScanMediaPathList; // 遍历的到的音频文件路径集合

	private List<String> needUpateMusicPaths; // 已经需要更新宜搜数据库的音频文件路径

	private int scannedMediaCount; // 计数用的
									// 扫描音频文件个数（包括入库的和不入库）。用于检测全部音频文件是否已经扫描过。

	private int insertedMediaCount;// 计数用的，扫描到并入库的的音频文件数量。

	private List<MusicInfo> systemMediaMusicInfos; // 媒体数据库中的歌曲信息集合。

	private List<String> systemDiscontentMediaPaths; // 媒体库中不满足标准的 歌曲路径集合。

	private List<MusicInfo> previousMusicInfos;// 当前已经存在宜搜数据库的 歌曲信息集合

	private List<MusicInfo> willInsertMusicInfos; // 将要插入数据库的 歌曲信息集合

	private List<MusicInfo> willUpdateMusicInfos; // 将要更新数据库的歌曲信息集合

	private boolean isShowFolderProgress = false;// 是否展示文件夹进度。

	private synchronized void addScannedMediaCount() {
		scannedMediaCount++;
	}

	private synchronized void addInsertedMediaCount() {
		insertedMediaCount++;
	}


	/**
	 * 遍历文件夹是否完成
	 * 
	 * @author Perry
	 */
	private boolean isErgodicCompleted;

	private Context context;

	/**
	 * 扫描前的数据库里的歌曲数量。
	 * 
	 * @author Perry
	 */
	private int oldSongNum;


	/**
	 * 扫描引擎的构造方法
	 * 
	 * @param context
	 * @param mediaScannerListener
	 * @author Perry
	 */
	private ScanEngine() {
		this.context = TianlApp.newInstance();
		try {
			init();
		} catch (Exception e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
	}

	public static ScanEngine newInstance() {
		if (mScanEngine == null) {
			synchronized (ScanEngine.class) {
				if (mScanEngine == null) {
					mScanEngine = new ScanEngine();
				}
			}
		}
		return mScanEngine;
	}

	private void init(){
		oldSongNum = LocalMusicManager.getInstence().countAllMusic();
	}
	
	public int getOldSongNum() {
		return oldSongNum;
	}

	public void setOldSongNum(int oldSongNum) {
		this.oldSongNum = oldSongNum;
	}

	/**
	 * 做扫描前的准备。 1、删除宜搜音乐数据库中音频文件已不存在的歌曲信息。 2、获取不需更新数据的音频文件路径集合 List<String>
	 * insertedMusicInfos;
	 */
	private void prepareScan() {
		// 当前数据库中的MusicInfo集合
		previousMusicInfos = LocalMusicManager.getInstence().getAllMusic();
		if (previousMusicInfos == null)
			previousMusicInfos = new ArrayList<MusicInfo>();

		// 音频文件已经不存在的MusicInfo集合
		List<MusicInfo> nonexistentMediaDatas = ScanUtil
				.getNonexistentMediaDatas(previousMusicInfos);

		/* 删除宜搜音乐数据库中音频文件不存在的歌曲信息。 */
		int[] nonexistentMediaIndexs = new int[nonexistentMediaDatas.size()];
		for (int i = 0; i < nonexistentMediaDatas.size(); i++) {
			nonexistentMediaIndexs[i] = previousMusicInfos
					.indexOf(nonexistentMediaDatas.get(i));
		}
		MusicOperate.newInstance().deleteMusicInLocal(previousMusicInfos,
				nonexistentMediaIndexs, false);
		previousMusicInfos.removeAll(nonexistentMediaDatas);

		/* 获取不需更新数据的音频文件路径集合 */
		insertedMusicPaths = new ArrayList<String>();
		/* 获取需要更新数据的音频文件路径集合 */
		needUpateMusicPaths = new ArrayList<String>();
		for (MusicInfo musicInfo : previousMusicInfos) {
			if (!ScanUtil.isMusicInfoOutOfDate(musicInfo)) {
				insertedMusicPaths.add(musicInfo.getLocalUrl());
			} else {
				needUpateMusicPaths.add(musicInfo.getLocalUrl());
			}
		}

	}

	/**
	 * 快速扫描
	 * 
	 * @return
	 * @author Perry
	 */
	public boolean quickScan() {
		// 初始化 扫描页面的状态值
		scanViewState = 1; // 当前为正在扫描
		scanViewBarMax = 100;
		scanViewBarCurrent = 0;
		scanViewInsertNum = 0;
		mMediaScannerListener.onScanMediaBegin();
		
		prepareScan();
		
		boolean isSuccess = false;
		Lg.d("test", "begin quick Scan");
		String[] mediaDirPaths = ScanUtil.getMediaDirPaths(context);
		// 若当前获取的音乐文件夹为空，或个数为0
		if (mediaDirPaths == null || mediaDirPaths.length <= 0) {
			scanViewState = 2; // 当前为扫描后
			scanViewInsertNum = 0;
			mMediaScannerListener.onScanMediaAllCompleted(0);
			return true;
		}

		// 判断当前的文件夹数量是否达到按文件夹扫描的进度刷新
		if (mediaDirPaths.length < MIN_FOLDER_NUM) {
			isShowFolderProgress = false;

			FileFilter mediaFileter = new FileFilter() {

				@Override
				public boolean accept(File pathname) {
					return ScanUtil.isMediaFile(pathname);
				}
			};
			int mediaFileNum = 0;
			for (String path : mediaDirPaths) {
				File file = new File(path);
				File[] files = file.listFiles(mediaFileter);
				if (files != null) {
					mediaFileNum += files.length;
				}
			}
			scanViewBarMax = mediaFileNum;

		} else {
			isShowFolderProgress = true;
			scanViewBarMax = mediaDirPaths.length;
		}

		allScanMediaPathList = Collections
				.synchronizedList(new ArrayList<String>());
		willInsertMusicInfos = Collections
				.synchronizedList(new ArrayList<MusicInfo>());
		willUpdateMusicInfos = Collections
				.synchronizedList(new ArrayList<MusicInfo>());
		scannedMediaCount = 0;
		insertedMediaCount = 0;
		MediaScanner scanner = new MediaScanner(context);
		// 设置是否遍历完成的boolean为false
		setErgodicCompleted(false);
		for (String path : mediaDirPaths) {
			if (isShowFolderProgress) {
				scanViewBarCurrent++;
				mMediaScannerListener.onScanningMediaCountChanged(
						scanViewBarMax, scanViewBarCurrent, scanViewInsertNum);
			}
			scanDirEngin(path, scanner, false);// 仅对其目录下的所有音频文件扫描，不对其目录下的文件夹再次扫描。
		}
		// 设置是否遍历完成的boolean为true
		setErgodicCompleted(true);

		// 全部扫描完成
		if (scannedMediaCount == allScanMediaPathList.size()) {
			Lg.i("test", "All scan Completed");

			MusicOperate.newInstance().addMusicToLocal(willInsertMusicInfos);

			LocalMusicManager.getInstence().updateMusic(willUpdateMusicInfos);
			// musicDao.insertAllMusicDatas(willInsertMusicInfos);
			scanViewState = 2; // 当前为扫描后

			scanViewInsertNum = insertedMediaCount;
			mMediaScannerListener.onScanMediaAllCompleted(scanViewInsertNum);
			
		}

		return isSuccess;
	}

	/**
	 * 自定义文件夹遍历
	 * 
	 * @param dirs
	 * @return
	 * @author Perry
	 */
	public boolean dirScan(String[] dirs) {
		// 初始化扫描页面的数据
		scanViewState = 1; // 当前为正在扫描
		scanViewBarMax = 100;
		scanViewBarCurrent = 0;
		scanViewInsertNum = 0;
		mMediaScannerListener.onScanMediaBegin();

		prepareScan();

		boolean isSuccess = false;

		// 若当前自定义的音乐文件夹为空，或个数为0
		if (dirs == null || dirs.length <= 0) {
			scanViewState = 2; // 当前为扫描后
			scanViewInsertNum = 0;
			mMediaScannerListener.onScanMediaAllCompleted(0);
			return true;
		}

		// 若当前要扫描的文件夹数量少于按文件夹扫描的标志。则计算其子文件夹的数量
		if (dirs.length < MIN_FOLDER_NUM) {
			new FileFilter() {

				@Override
				public boolean accept(File pathname) {
					return pathname.isDirectory();
				}
			};
			List<String> dirPaths = new ArrayList<String>();
			for (String path : dirs) {
				File dir = new File(path);
				File[] files = dir.listFiles();
				if (files != null) {
					for (File file : files) {
						dirPaths.add(file.getAbsolutePath());
					}
				}
			}
			dirs = dirPaths.toArray(new String[dirPaths.size()]);
		}

		// 再次判断当前的文件夹数量是否达到按文件夹扫描的进度刷新
		if (dirs.length < MIN_FOLDER_NUM) {
			isShowFolderProgress = false;

			FileFilter mediaFileter = new FileFilter() {

				@Override
				public boolean accept(File pathname) {
					return ScanUtil.isMediaFile(pathname);
				}
			};
			int mediaFileNum = 0;
			for (String path : dirs) {
				File file = new File(path);
				File[] files = file.listFiles(mediaFileter);
				if (files != null) {
					mediaFileNum += files.length;
				}
			}
			scanViewBarMax = mediaFileNum;

		} else {
			isShowFolderProgress = true;
			scanViewBarMax = dirs.length;
		}

		allScanMediaPathList = Collections
				.synchronizedList(new ArrayList<String>());
		willInsertMusicInfos = Collections
				.synchronizedList(new ArrayList<MusicInfo>());
		willUpdateMusicInfos = Collections
				.synchronizedList(new ArrayList<MusicInfo>());
		scannedMediaCount = 0;
		insertedMediaCount = 0;
		MediaScanner scanner = new MediaScanner(context);
		// 设置是否遍历完成的boolean为false
		setErgodicCompleted(false);
		for (String path : dirs) {
			if (isShowFolderProgress) {
				scanViewBarCurrent++;
			}
			mMediaScannerListener.onScanningMediaCountChanged(scanViewBarMax,
					scanViewBarCurrent, scanViewInsertNum);
			scanDirEngin(path, scanner, true);
		}
		// 设置是否遍历完成的boolean为true
		setErgodicCompleted(true);

		// 自定义文件夹扫描完成
		if (scannedMediaCount == allScanMediaPathList.size()) {
			Lg.i("test", "All scan Completed");
			scanViewState = 2; // 当前为扫描后
			MusicOperate.newInstance().addMusicToLocal(willInsertMusicInfos);

			LocalMusicManager.getInstence().updateMusic(willUpdateMusicInfos);

			scanViewInsertNum = insertedMediaCount;
			mMediaScannerListener.onScanMediaAllCompleted(scanViewInsertNum);
			
		}

		return isSuccess;
	}

	/**
	 * 遍历并扫描文件夹下的所有音频文件
	 * 
	 * @param path
	 *            要扫描的文件夹路径
	 * @param scanner
	 *            MediaScanner扫描类
	 * @param isErgodic
	 *            是否都其文件夹下的文件夹进行扫描
	 * @author Perry
	 */
	private void scanDirEngin(String path, MediaScanner scanner,
			boolean isErgodic) // 搜索目录，扩展名，是否进入子文件夹
	{
		File file = new File(path);
		if (!file.isDirectory()) {
			// 判断当前是否音频文件，并做对应处理
			if (ScanUtil.isMediaFile(file)) {
				scanSingleMediaOperate(file, scanner);
			}
		} else {
			File[] files = file.listFiles();
			if (files == null)
				return;
			for (int i = 0; i < files.length; i++) {
				File f = files[i];
				if (f.isFile()) {// 若当前文件是文件
					// 判断当前是否音频文件，并做对应处理
					if (ScanUtil.isMediaFile(f)) {
						scanSingleMediaOperate(f, scanner);
					}
					// 是文件夹 && 遍历子文件夹 && 不是点文件（隐藏文件/文件夹）
				} else if (f.isDirectory() && isErgodic
						&& f.getPath().indexOf("/.") == -1)
					scanDirEngin(f.getPath(), scanner, isErgodic);
			}
		}
	}

	/**
	 * 内部调用的 执行单个扫描操作
	 * 
	 * @param mediaPathList
	 * @param file
	 * @param scanner
	 */
	private void scanSingleMediaOperate(File file, MediaScanner scanner) {
		String absolutePath = file.getAbsolutePath();
		// 插入遍历得到的媒体文件路径到媒体地址集合中
		allScanMediaPathList.add(absolutePath);

		//根据当前路径判断，音频是否不符合入库信息。
		if(isSystemDiscontentMediaPath(absolutePath)){
			addScannedMediaCount();
			scanViewInsertNum = insertedMediaCount;// 已插入的歌曲数量。
			// 按歌曲数量刷新进度条
			if (!isShowFolderProgress) {
				scanViewBarCurrent++;// 累计扫描进度条
				// 回调扫描进度条
			}
			mMediaScannerListener.onScanningMediaCountChanged(scanViewBarMax,
					scanViewBarCurrent, scanViewInsertNum);
			return;
		}
		
		// 根据当前文件路径，判断宜搜数据库中是否已经有该数据。
		if (!insertedMusicPaths.contains(absolutePath)) { // 当前文件信息不存在无需更新信息的音频文件列表，或不存在宜搜数据库的信息。。

			// 判断是否是宜搜数据库中需要更新的歌曲信息
			boolean isUpdateMusicInfo = false;
			long musicID = 0;
			for (MusicInfo previousMusicInfo : previousMusicInfos) {
				if (previousMusicInfo.getLocalUrl().equals(absolutePath)) { // 旧有的数据库信息中，含有当前歌曲的信息。
					isUpdateMusicInfo = true;
					musicID = previousMusicInfo.getId();
					break;
				}
			}

			if (isUpdateMusicInfo) {// 宜搜数据库中需要更新的歌曲信息
				// 根据路径，从系统数据库中获取其对应的媒体信息。
				MusicInfo musicInfo = getSystemMediaMusicInfoByPath(file
						.getAbsolutePath());

				// 判断系统数据库中是否有该信息。
				if (musicInfo != null) {// 系统数据库中有该信息
					if (!ScanUtil.isMusicInfoOutOfDate(musicInfo)) { // 是最新的，就直接更新
						musicInfo.setId(musicID);
						willUpdateMusicInfos.add(musicInfo);
						addScannedMediaCount();
						addInsertedMediaCount();
						scanViewInsertNum = insertedMediaCount;// 已插入的歌曲数量。
						// 按歌曲数量刷新进度条
						if (!isShowFolderProgress) {
							scanViewBarCurrent++;// 累计扫描进度条
							// 回调扫描进度条
						}
						mMediaScannerListener.onScanningMediaCountChanged(
								scanViewBarMax, scanViewBarCurrent,
								scanViewInsertNum);
					} else {// 不是最新的，调用系统的解析，并进行更新操作。
						// 根据文件路径，调用系统进行扫描,并更新
						scanner.scanMediaPath(absolutePath);
					}
				} else {// 系统数据库中没有该信息。直接调用系统解析，并进行插入操作
					// 根据文件路径，调用系统进行扫描,并插入
					scanner.scanMediaPath(absolutePath);
				}
			} else { // 宜搜数据库中没有的信息，获取系统数据，并检查其是否为最新。
				// 根据路径，从系统数据库中获取其对应的媒体信息。
				MusicInfo musicInfo = getSystemMediaMusicInfoByPath(file
						.getAbsolutePath());
				// 判断系统数据库中是否有该信息。
				if (musicInfo != null) {// 系统数据库中有该信息
					if (!ScanUtil.isMusicInfoOutOfDate(musicInfo)) { // 是最新的，就直接更新
						willInsertMusicInfos.add(musicInfo);
						addScannedMediaCount();
						addInsertedMediaCount();
						scanViewInsertNum = insertedMediaCount;// 已插入的歌曲数量。
						// 按歌曲数量刷新进度条
						if (!isShowFolderProgress) {
							scanViewBarCurrent++;// 累计扫描进度条
							// 回调扫描进度条
						}
						mMediaScannerListener.onScanningMediaCountChanged(
								scanViewBarMax, scanViewBarCurrent,
								scanViewInsertNum);
					} else {// 不是最新的，调用系统的解析，并进行更新操作。
						// 根据文件路径，调用系统进行扫描,并更新
						scanner.scanMediaPath(absolutePath);
					}
				} else {// 系统数据库中没有该信息。直接调用系统解析，并进行插入操作
					// 根据文件路径，调用系统进行扫描,并插入
					scanner.scanMediaPath(absolutePath);
				}
			}

		} else {
			// 根据文件路径，得知当前文件已被扫描入库，则增加已扫描的数量值。
			addScannedMediaCount();
			addInsertedMediaCount();
			scanViewInsertNum = insertedMediaCount;// 已插入的歌曲数量。
			// 按歌曲数量刷新进度条
			if (!isShowFolderProgress) {
				scanViewBarCurrent++;// 累计扫描进度条
				// 回调扫描进度条
			}
			mMediaScannerListener.onScanningMediaCountChanged(scanViewBarMax,
					scanViewBarCurrent, scanViewInsertNum);
		}
	}

	/**
	 * 根据音频文件路径，返回当前系统数据库中对应的MusicInfo
	 * 
	 * @param path
	 *            音频文件路径
	 * @return 若有则返回对应的MusicInfo，否则为null
	 */
	private MusicInfo getSystemMediaMusicInfoByPath(String path) {
		if (systemMediaMusicInfos == null) {
			systemMediaMusicInfos = ScanUtil.scanMediaStore(context);
			if (systemMediaMusicInfos == null) {
				return null;
			}
		}
		for (MusicInfo musicInfo : systemMediaMusicInfos) {
			if (musicInfo.getLocalUrl().equals(path)) {
				return musicInfo;
			}
		}
		return null;
	}

	/**
	 * 判断当前路径是否是不满足的音频地址
	 * 
	 * @param path
	 * @return
	 */
	private boolean isSystemDiscontentMediaPath(String path) {
		if (systemDiscontentMediaPaths == null) {
			systemDiscontentMediaPaths = ScanUtil.scanMediaStoreDiscontentPaths(context);
			if (systemDiscontentMediaPaths == null) {
				systemDiscontentMediaPaths = new ArrayList<String>();
			}
		}
		return systemDiscontentMediaPaths.contains(path);
	}

	public boolean isErgodicCompleted() {
		return isErgodicCompleted;
	}

	public void setErgodicCompleted(boolean isErgodicCompleted) {
		this.isErgodicCompleted = isErgodicCompleted;
	}

	public class MediaScanner implements MediaScannerConnectionClient {

		private MediaScannerConnection mConnection;
		private List<String> tempMediaPathList; // 用于临时缓存将要扫描的音频路径集合

		public MediaScanner(Context context) {
			mConnection = new MediaScannerConnection(context, this);
			this.tempMediaPathList = Collections
					.synchronizedList(new LinkedList<String>());
			mConnection.connect();
		}

		public void scanMediaPath(String path) {
			if (!mConnection.isConnected()) {
				tempMediaPathList.add(path);
			} else {
				mConnection.scanFile(path, "audio/*");
			}
		}

		@Override
		public void onMediaScannerConnected() {
			if (tempMediaPathList.size() > 0) {
				for (String path : tempMediaPathList) {
					mConnection.scanFile(path, null);
				}
			}
		}

		@Override
		public void onScanCompleted(String path, Uri uri) {
			Lg.v("test", "mediaPath = " + path);
			Lg.v("test", "sys uri =" + uri);
			Lg.v("test", "allnum = " + allScanMediaPathList.size());

			addScannedMediaCount();
			scanViewInsertNum = insertedMediaCount;// 已插入的歌曲数量。
			// 按歌曲数量刷新进度条
			if (!isShowFolderProgress) {
				scanViewBarCurrent++;// 累计扫描进度条
				// 回调扫描进度条
			}
			mMediaScannerListener.onScanningMediaCountChanged(scanViewBarMax,
					scanViewBarCurrent, scanViewInsertNum);

			if (uri != null && uri.getPath().length() > 0) {
				MusicInfo musicInfo = ScanUtil.scanMediaStoreByPath(context,
						path, true);
				if (musicInfo != null) {
					addInsertedMediaCount();

					// 判断是否需要更新歌曲信息
					boolean isUpdateMusicInfo = false;
					for (MusicInfo previousMusicInfo : previousMusicInfos) {
						if (previousMusicInfo.getLocalUrl().equals(
								musicInfo.getLocalUrl())) {
							musicInfo.setId(previousMusicInfo.getId());
							isUpdateMusicInfo = true;
							break;
						}
					}

					if (isUpdateMusicInfo) {// 执行更新操作
						willUpdateMusicInfos.add(musicInfo);
						Lg.v("test", "update =" + musicInfo.getLocalUrl());
					} else {// 执行插入 等待插入队列。
						willInsertMusicInfos.add(musicInfo);
					}
				}
			}
			if (isErgodicCompleted()) {// 遍历文件夹是否全部完成。
				// 全部扫描完成
				if (scannedMediaCount == allScanMediaPathList.size()) {
					Lg.i("test", "All scan Completed");

					MusicOperate.newInstance().addMusicToLocal(
							willInsertMusicInfos);

					LocalMusicManager.getInstence().updateMusic(willUpdateMusicInfos);

					scanViewState = 2; // 当前为扫描后

					scanViewInsertNum = insertedMediaCount;
					mMediaScannerListener
							.onScanMediaAllCompleted(scanViewInsertNum);
				}
			}
		}

	}

	public IMediaScannerListener getmMediaScannerListener() {
		return mMediaScannerListener;
	}

	public void setmMediaScannerListener(
			IMediaScannerListener mMediaScannerListener) {
		this.mMediaScannerListener = mMediaScannerListener;
	}
}
