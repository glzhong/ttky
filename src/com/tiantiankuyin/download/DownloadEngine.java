package com.tiantiankuyin.download;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.http.HttpStatus;

import android.content.Context;

import com.tiantiankuyin.bean.MusicInfo;
import com.tiantiankuyin.component.service.DownloadFile;
import com.tiantiankuyin.component.service.DownloadService;
import com.tiantiankuyin.component.service.DownloadFile.DownloadType;
import com.tiantiankuyin.component.service.DownloadService.DownloadBinder;
import com.tiantiankuyin.database.bll.LocalMusicManager;
import com.tiantiankuyin.database.dao.impl.MusicDaoImpl;
import com.tiantiankuyin.download.IDownloadFileListener.DownloadState;
import com.tiantiankuyin.download.IDownloadListener.DownloadErrorType;
import com.tiantiankuyin.para.Constant;
import com.tiantiankuyin.para.Env;
import com.tiantiankuyin.scan.ISingleMediaScannerListener;
import com.tiantiankuyin.scan.SingleMediaScanner;
import com.tiantiankuyin.utils.CommonUtils;
import com.tiantiankuyin.TianlApp;

/**
 * 引擎 该类主要用于文件任务，使用该类时只需要得到单例对象，并调用newDownloadTask即可开始一个任务。
 * newDownloadTask的第二个形参isNewDownloadFile作用为：如果是新文件，则为true，如果是已经开
 * 始过的任务并希望断点续传时，则为false。 该引擎区别判定任务文件重复是使用从服务器获取的FileID作为
 * 唯一标识，在判断文件是否存在时，需要查询音乐数据库，所以音乐数据库Dao中必须提供MusicDAO.isMusicExist(String
 * fileID)方法。 如需获取正在任务队列和已完成的任务，可以使用getDownloadingFiles和getDownloadedFiles方法，
 * 这两个方法返回的是不重复的Set数据结构。为避免ConcurrentModificationException异常，在迭代
 * 数据集时不建议使用foreach进行遍历，应使用Iterator并对遍历代码进行synchronized(返回的Set数据集)进行包裹
 * 
 * @author DK
 * 
 */
public class DownloadEngine {

	private static final int CONN_TIME_OUT = 30000;
	private static final int REQUEST_TIME_OUT = 30000;
	public static Set<DownloadFile> downloadingFiles; // 正在进行的文件
	public static Set<DownloadFile> downloadedFiles; // 已完成的文件
	private static Set<DownloadFile> downloadingThreadFiles;// 已有线程在进行的文件列表
	private static DownloadEngine downloadManager;// 任务管理器实例
	public static int downloadingFileCount; // 当前正在现在的文件数
	private IDownloadListener mDownloadListener; // 任务监听器
	private Context mContext;

	private DownloadEngine(Context context, IDownloadListener downloadListener,
			Set<DownloadFile> downloadings, Set<DownloadFile> downloadeds)
			throws Exception {
		this.mDownloadListener = downloadListener;
		this.mContext = context;
		init(context, downloadings, downloadeds);
	}

	/**
	 * 数据初始化
	 * 
	 * @param context
	 * @param downloadings
	 *            从数据库中读取到的正在进行的文件
	 * @param downloadeds
	 *            从数据库中读取到的已完成的文件
	 * @throws Exception
	 */
	private void init(Context context, Set<DownloadFile> downloadingList,
			Set<DownloadFile> downloadedList) throws Exception {
		if (downloadingFiles == null) {
			downloadingFiles = Collections
					.synchronizedSet(new TreeSet<DownloadFile>());
			if (downloadingList != null)
				downloadingFiles.addAll(downloadingList);
		}

		if (downloadedFiles == null) {
			downloadedFiles = Collections
					.synchronizedSet(new TreeSet<DownloadFile>());
			if (downloadedList != null)
				downloadedFiles.addAll(downloadedList);
		}

		if (downloadingThreadFiles == null)
			downloadingThreadFiles = Collections
					.synchronizedSet((new TreeSet<DownloadFile>()));
	}

	/**
	 * 获取任务管理器实例
	 * 
	 * @param context
	 * @param downloadListener
	 *            任务监听器，用于监听任务文件总数、单个文件任务完成
	 * @return
	 * @throws Exception
	 *             如果Dao工厂创建实例失败，例如class找不到
	 */
	public static DownloadEngine newInstance(Context context,
			IDownloadListener downloadListener, Set<DownloadFile> downloadings,
			Set<DownloadFile> downloadeds) throws Exception {
		if (downloadManager == null) {
			synchronized (DownloadEngine.class) {
				if (downloadManager == null) {
					downloadManager = new DownloadEngine(context,
							downloadListener, downloadings, downloadeds);
				}
			}
		}
		return downloadManager;
	}

	/**
	 * 新建任务
	 * 
	 * @param file
	 *            任务文件bean
	 * @throws DownloadException
	 * @param isNewDownloadFile
	 *            是否是新任务
	 */
	public void newDownloadTask(DownloadFile file, boolean isNewDownloadFile) {
		if (file == null) { // 如果文件对象不存在
			return;
		}
//		String url = file.getUrl();
//		String fileName = file.getFileName();
//		 if (url == null || url.length() <= 0 || fileName == null
//		 || fileName.length() <= 0) { // 如果文件url或文件名不存在，则资源不存在
//		 downloadError(file, DownloadErrorType.RESOURCE_NONEXIST);
//		 file.setState(DownloadState.STATE_FAILED);
//		 return;
//		 }
		if (!CommonUtils.isExternalStorageAvailable()) { // 外部存储器不可用
			downloadError(file, DownloadErrorType.NOSDCARD);
			changeFileState(file, DownloadState.STATE_FAILED);
			return;
		}

		/*
		 * if (isNewDownloadFile && downloadingFiles.contains(file) || list !=
		 * null && list.size() > 0) {// 判断当前正在进行的任务队列中是否有此文件 downloadError(file,
		 * DownloadErrorType.TASKEXIST); return; }
		 */
		changeFileState(file, DownloadState.STATE_WAITING);// 将文件任务状态置为等待
		addDownloadFileOperate(file); // 增加任务文件
		DownloadThreadPool.execute(new DownloadFileTask(file)); // 向线程池中增加任务
	}

	/**
	 * 任务
	 * 
	 * @author DK
	 */
	private class DownloadFileTask extends DownloadTask {

		public DownloadFileTask(DownloadFile file) {
			super(file);
		}

		@Override
		public void run() {
			// 根据服务器歌曲文件ID判断当前歌曲是否已存在
			if (MusicDaoImpl.isMusicExist(file.getFileID())) {
				downloadError(file, DownloadErrorType.FILE_EXIST);
				return;
			}
			try {
				startDownloadFile(file);
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}
	}

	/**
	 * 开始单个任务文件 (文件时临时文件名为文件ID的MD5，临时文件会暂存在tmp目录下，任务完成后会移植相应目录)
	 * 
	 * @param file
	 * @throws Exception
	 */
	private synchronized void startDownloadFile(DownloadFile file)
			throws Exception {
		if (file == null) {
			reduceDownloadFileOperate(file);
			return;
		}
		if (!checkTMPDir()) {// 如果临时目录不可写或者创建失败
			downloadError(file, DownloadErrorType.SAVEPATH_ILLEGAL);
			reduceDownloadFileOperate(file);
			return;
		}
		IDownloadFileListener downloadFileListener = file.getDownloadListener();
		String downloadUrl = file.getUrl();
		if (downloadUrl == null || downloadUrl.length() <= 0) { // 如果url不存在
			downloadError(file, DownloadErrorType.RESOURCE_NONEXIST);
			reduceDownloadFileOperate(file);
			return;
		}
		String fileID = file.getFileID();
		if (fileID == null) {// 文件ID不存在
			downloadError(file, DownloadErrorType.NO_FILE_ID);
			reduceDownloadFileOperate(file);
			return;
		}
		String filePath = Constant.SdcardPath.DOWNLOAD_TMP_SAVEPATH + "/"
				+ CommonUtils.MD5(fileID); // 临时文件路径
		File tempFile = new File(filePath); // 临时文件
		HttpURLConnection conn = null; // HTTP连接
		InputStream inputStream = null;
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		BufferedInputStream bis = null;
		try {
			if (!tempFile.exists()) { // 如果临时文件不存在
				tempFile.createNewFile();
			}
			long currentSize = tempFile.length();// 已完成的大小
			URL url = new URL(downloadUrl);
			// if (CommonUtils.isWapConnected(mContext)) { // 如果当前是wap连接
			// Proxy proxy = new Proxy(java.net.Proxy.Type.HTTP,
			// new InetSocketAddress(Easou.proxyHost, Easou.proxyPort));
			// conn = (HttpURLConnection) url.openConnection(proxy);
			// } else {
			// conn = (HttpURLConnection) url.openConnection();
			// }
			conn = CommonUtils.getConnection(downloadUrl, mContext);
			conn.setDoInput(true);
			conn.setConnectTimeout(CONN_TIME_OUT);
			conn.setReadTimeout(REQUEST_TIME_OUT);
			conn.setRequestMethod("GET");
			if (currentSize > 0) { // 将已完成的文件长度告知服务器
				conn.addRequestProperty("RANGE", "bytes=" + currentSize + "-");
			}
			if (file.getFileType() != null // 如果任务文件为歌词类型则更改编码方式
					&& DownloadFile.DownloadType.DOWNLOAD_TYPE_LRC == file
							.getFileType()) {
				conn.addRequestProperty("Charset", "GBK");
			}
			conn.connect();
			int responseCode = conn.getResponseCode();
			if (responseCode == HttpStatus.SC_OK
					|| responseCode == HttpStatus.SC_PARTIAL_CONTENT) { // 如果结果码为200或是请求RANGE成功
				inputStream = conn.getInputStream();
				String contentRange = conn.getHeaderField("Content-Range"); // 用于提取文件大小
				String contentLength = conn.getHeaderField("Content-Length");// 如果Content-Range不存在时
				long totalSize = CommonUtils.getFileTotalSize(contentRange,
						contentLength);
				long availableExternalMemorySize = CommonUtils // 获取存储器可用空间大小
						.getAvailableExternalMemorySize();
				if (availableExternalMemorySize < totalSize) { // SD卡空间不足
					downloadError(file, DownloadErrorType.NO_MEMORYSIZE);
					reduceDownloadFileOperate(file);
					return;
				}
				if (totalSize == 0) {
					totalSize = file.getFileTotalSize();
				}
				if (totalSize != 0) {

					
					Set<DownloadFile> result = LocalMusicManager.getInstence().getDownloadDatasByFileId(file.getFileID());
					if(result != null && result.size() > 0)

						LocalMusicManager.getInstence().updateDownloadData(file);
					else
						LocalMusicManager.getInstence().addDownloadData(file);
				}
				if (downloadFileListener != null && currentSize > 0) {
					file.setFileCurrentSize(currentSize);
					downloadFileListener.onDownloadProgressChanged(file,
							totalSize, currentSize);
				}
				file.setFileTotalSize(totalSize);
				file.setFileCurrentSize(currentSize);
				String acceptRanges = conn.getHeaderField("Accept-Ranges");
				boolean isAcceptRanges = false;
				if ((acceptRanges != null && acceptRanges
						.equalsIgnoreCase("bytes"))
						|| responseCode == HttpStatus.SC_PARTIAL_CONTENT) {
					isAcceptRanges = true;
				}
				if (isAcceptRanges) {
					fos = new FileOutputStream(tempFile, true); // 如果服务器支持断点，则追加到临时文件
				} else {
					fos = new FileOutputStream(tempFile);
				}
				bos = new BufferedOutputStream(fos);
				bis = new BufferedInputStream(inputStream);
				int length = 0;
				byte[] buffer = new byte[4096];
				if (file.getFileType() == DownloadType.DOWNLOAD_TYPE_MUSIC_APPOINTMENT) {
					boolean lock = TianlApp.newInstance().isWifiDownloadLock();
					boolean wifiAvaliable = Env.isWifiAvaliable();
					boolean fromUser = TianlApp.newInstance()
							.isDownloadFromeUserOperate();
					if (!fromUser) {
						if (lock
								|| DownloadService.newInstance()
										.hasNormalDownloadFile()
								|| !wifiAvaliable) {// wifi不可用、有普通任务、已经全部暂停。都不进行
							changeFileState(file, DownloadState.STATE_PAUSED);
							reduceDownloadFileOperate(file);
							return;
						}
					} else {
						DownloadBinder binder = DownloadService.newInstance().binder;
						// if (binder != null){
						// 暂停其它的任务
						// binder.pauseAllWifiDownloadTask();
						// }
					}
				}
				TianlApp.newInstance().setDownloadFromeUserOperate(false);
				changeFileState(file, DownloadState.STATE_DOWNING); // 将文件任务状态置为正在进行

				long perTime = System.currentTimeMillis();
				while ((length = bis.read(buffer)) != -1
						&& file.getState() != DownloadState.STATE_PAUSED) {
					if (file.getFileType() == DownloadType.DOWNLOAD_TYPE_LRC) {
						String temp = new String(buffer, 0, length, "GBK");
						bos.write(temp.getBytes("UTF-8"));
					} else {
						bos.write(buffer, 0, length);
					}
					currentSize += length;
					if (downloadFileListener != null) {
						file.setFileCurrentSize(currentSize);
						long curTime = System.currentTimeMillis();
						if (curTime - perTime > 1000) {// 每隔1秒
							perTime = curTime;
							downloadFileListener.onDownloadProgressChanged(
									file, totalSize, currentSize);
						}
					}
					// changeFileState(file, DownloadState.STATE_DOWNING); //
					// 将文件任务状态置为正在进行
				}

				bos.flush();
				if (file.getState() != DownloadState.STATE_PAUSED) { // 如果不是因为暂停
					transferFile(file); // 将文件根据fileName属性命名移植相应文件夹，并删除临时文件
				}
			} else {
				downloadError(file, DownloadErrorType.CONNECT_ERROR); // 如果服务器连接失败
			}
		} catch (IOException e) {
			downloadError(file, DownloadErrorType.IOERROR);
		} finally {
			reduceDownloadFileOperate(file);
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
					// e.printStackTrace();
				}
			}
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
					// e.printStackTrace();
				}
			}
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					// e.printStackTrace();
				}
			}
			if (conn != null) {
				conn.disconnect();
			}
			// startNextTask();
		}
	}

	// private void startNextTask() {
	// for(DownloadFile dFile : DownloadEngine.downloadingFiles){
	// if(dFile.getState() == DownloadState.STATE_WAITING){
	// DownloadThreadPool.execute(new DownloadFileTask(dFile)); // 向线程池中增加任务
	// break;
	// }
	// }
	// }

	/**
	 * 获取未完成的文件（此方法会查询数据库，请在子线程中运行）
	 * (在遍历集合时不建议使用foreach方式，因为会造成ConcurrentModificationException异常)
	 * 
	 * @return
	 */
	public Set<DownloadFile> getDownloadingFiles() {
		Set<DownloadFile> selectDatas = LocalMusicManager.getInstence().getDownloadDatasByState(LocalMusicManager.DOWNLOADING);
		if (downloadingFiles != null) {
			downloadingFiles.addAll(selectDatas);
		}
		return downloadingFiles;
	}

	/**
	 * 获取已完成的文件（此方法会查询数据库，请在子线程中运行）
	 * (在遍历集合时不建议使用foreach方式，因为会造成ConcurrentModificationException异常)
	 * 
	 * @return
	 */
	public Set<DownloadFile> getDownloadedFiles() {		
		Set<DownloadFile> selectDatas = LocalMusicManager.getInstence().getDownloadDatasByState(LocalMusicManager.DOWNLOADED);
		if (downloadedFiles != null) {
			downloadedFiles.addAll(selectDatas);
		}
		return downloadingFiles;
	}

	/**
	 * 检查临时目录
	 */
	private boolean checkTMPDir() {
		File tmpDir = new File(Constant.SdcardPath.DOWNLOAD_TMP_SAVEPATH);
		if (!tmpDir.exists()) {
			tmpDir.mkdirs();
		}
		if (!tmpDir.exists() || !tmpDir.canWrite()) {
			return false;
		}
		return true;
	}

	/**
	 * 暂停单个任务文件
	 * 
	 * @param file
	 */
	public void pauseDownloadFile(DownloadFile file) {
		changeFileState(file, DownloadState.STATE_PAUSED);
		reduceDownloadFileOperate(file);
	}

	/**
	 * 改变状态
	 * 
	 * @param file
	 * @param state
	 */
	private void changeFileState(DownloadFile file, DownloadState state) {
		if (file == null || state == null) {
			return;
		}
		file.setState(state);
		IDownloadFileListener downloadListener = file.getDownloadListener();
		if (downloadListener != null) {
			downloadListener.onDownloadStateChanged(file);
		}
	}

	/**
	 * 任务文件增加时的操作
	 * 
	 * @param file
	 */
	private void addDownloadFileOperate(DownloadFile file) {
		downloadingFileCount++;// 正在进行的文件数加1
		downloadingFiles.add(file);// 增加到正在进行的文件集合
		downloadingThreadFiles.add(file);// 增加到正在进行文件线程
		if (mDownloadListener != null)// 通知正在进行文件数改变
			mDownloadListener
					.onDownloadingFileCountChanged(downloadingFileCount);
	}

	/**
	 * 任务文件减少时的操作
	 * 
	 * @param file
	 */
	private void reduceDownloadFileOperate(DownloadFile file) {
		if (downloadingFileCount > 0)// 正在进行的文件数减1
			downloadingFileCount--;
		downloadingThreadFiles.remove(file);// 从正在进行文件线程集合中移除
		if (mDownloadListener != null)// 通知正在进行文件数改变
			mDownloadListener
					.onDownloadingFileCountChanged(downloadingFileCount);
	}

	/**
	 * 异常处理
	 * 
	 * @param file
	 * @param listener
	 * @param errorType
	 */
	private void downloadError(DownloadFile file, DownloadErrorType errorType) {
		if (file != null) {
			file.setState(DownloadState.STATE_FAILED);
			IDownloadFileListener downloadListener = file.getDownloadListener();
			if (downloadListener != null) {
				downloadListener.onDownloadStateChanged(file);
			}
		}
		if (mDownloadListener != null) {
			mDownloadListener.onDownloadError(file, errorType);
		}
	}

	/**
	 * 将已完成的临时文件转移到相应目录中
	 * 
	 * @param file
	 * @return
	 */
	private void transferFile(DownloadFile file) {

		boolean isCover = false;
		String fileName = "";
		if (file.getFileName().contains("铃声"))
			fileName = file.getSongName()
					+ "_铃声"
					+ file.getFileName().substring(
							file.getFileName().lastIndexOf("."),

							file.getFileName().length());
		else
			fileName = file.getSongName()
					+ file.getFileName().substring(
							file.getFileName().lastIndexOf("."),

							file.getFileName().length());
		if (fileName == null || fileName.length() <= 0) {
			return;
		}
		DownloadType fileType = file.getFileType();
		String filePath = null;
		switch (fileType) {
		case DOWNLOAD_TYPE_MUSIC: // 歌曲类型
		case DOWNLOAD_TYPE_MUSIC_APPOINTMENT:
			filePath = Constant.SdcardPath.SONG_SAVEPATH;
			break;
		case DOWNLOAD_TYPE_LRC: // 歌词类型
			filePath = Constant.SdcardPath.LYRIC_SAVEPATH;
			break;
		case DOWNLOAD_TYPE_APK: // apk类型
			filePath = Constant.SdcardPath.UPDATE_APK_SAVEPATH;
			break;
		case DOWNLOAD_TYPE_PLUGIN: // apk类型
			filePath = mContext.getFilesDir().getAbsolutePath();
			break;
		}
		File dir = new File(filePath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File srcFile = new File(Constant.SdcardPath.DOWNLOAD_TMP_SAVEPATH + "/"
				+ CommonUtils.MD5(file.getFileID()));
		File desFile = new File(filePath + "/" + fileName);
		if (desFile.exists()) {
			isCover = true;
			desFile.delete();
		}
		File dirs = new File(filePath);
		File[] listFiles = dirs.listFiles();
		if (listFiles != null) {
			for (File file2 : listFiles) {
				if (file2
						.getName()
						.substring(0, file2.getName().lastIndexOf("."))
						.equals(fileName.substring(0, fileName.lastIndexOf(".")))) {
					Iterator<DownloadFile> iterator = downloadedFiles
							.iterator();
					while (iterator.hasNext()) {
						DownloadFile next = iterator.next();
						if (next.getFileTotalSize() == file2.length())
							iterator.remove();
					}
					file2.delete();
				}
			}
		}
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			bis = new BufferedInputStream(new FileInputStream(srcFile));
			bos = new BufferedOutputStream(new FileOutputStream(desFile));
			int length = 0;
			byte[] buffer = new byte[1024];
			while ((length = bis.read(buffer)) != -1) {
				bos.write(buffer, 0, length);
				bos.flush();
			}
			downloadingFiles.remove(file);
			downloadedFiles.add(file);
			srcFile.delete();
			if (isCover) {

				List<MusicInfo> list = LocalMusicManager.getInstence()
						.getMusicListByMusicPath(filePath + "/" + fileName);
				if (list != null && list.size() > 0) {
					MusicInfo musicInfo = list.get(0);
					musicInfo.setFileID(file.getFileID());
					long lastModified = desFile.lastModified();
					musicInfo.setDateModified(lastModified);
					LocalMusicManager.getInstence().updateMusic(musicInfo);
				}

			}
		} catch (FileNotFoundException e) {
			downloadError(file, DownloadErrorType.TEMP_FILE_NOT_EXIST);
			return;
		} catch (IOException e) {
			downloadError(file, DownloadErrorType.IOERROR);
			return;
		} finally {
			reduceDownloadFileOperate(file);
			try {
				if (bos != null)
					bos.close();
			} catch (IOException e) {
				// e.printStackTrace();
			}
			try {
				if (bis != null)
					bis.close();
			} catch (IOException e) {
				// e.printStackTrace();
			}
		}
		insertDatabse(file, desFile.getAbsolutePath(), file.getGid(),
				file.getFileID());
	}

	/**
	 * 入库
	 * 
	 * @param file
	 */
	private void insertDatabse(final DownloadFile file, String path,
			String gid, String fileID) {
		SingleMediaScanner scan = new SingleMediaScanner(
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
						file.setSongName(musicInfo.getTitle());
						file.setSingerName(musicInfo.getArtist());
						DownloadEngine.downloadedFiles.remove(file);
						DownloadEngine.downloadedFiles.add(file);
						file.setMusicInfo(musicInfo);
						changeFileState(file, DownloadState.STATE_DOWNCOMPLETE);
						TianlApp.newInstance().onDownloadFileCompleted(file);
					}
				});
		scan.scanFile(path, gid, fileID, file.getSongName(),
				file.getSingerName());
	}

	/**
	 * 保存任务状态到数据库，请在子线程中运行
	 */
	public void saveDownloadInfo() {
		synchronized (downloadingFiles) {
			Iterator<DownloadFile> downloadingIterator = downloadingFiles
					.iterator();
			while (downloadingIterator.hasNext()) {
				DownloadFile downloadingFile = downloadingIterator.next();
				boolean exist = LocalMusicManager.getInstence().isDownloadFileExist(downloadingFile
						.getFileID());
				if (exist) {
					LocalMusicManager.getInstence().updateDownloadData(downloadingFile);
				} else {
					LocalMusicManager.getInstence().addDownloadData(downloadingFile);
				}
			}
		}
		synchronized (downloadedFiles) {
			Iterator<DownloadFile> downloadeIterator = downloadedFiles
					.iterator();
			while (downloadeIterator.hasNext()) {
				DownloadFile downloadedFile = downloadeIterator.next();
				boolean exist = LocalMusicManager.getInstence().isDownloadFileExist(downloadedFile
						.getFileID());
				if (exist) {
					LocalMusicManager.getInstence().updateDownloadData(downloadedFile);
				} else {
					LocalMusicManager.getInstence().addDownloadData(downloadedFile);
				}
			}
		}
	}
}
