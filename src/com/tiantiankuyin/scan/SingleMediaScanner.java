package com.tiantiankuyin.scan;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.OnScanCompletedListener;
import android.net.Uri;

import com.tiantiankuyin.bean.MusicInfo;
import com.tiantiankuyin.database.bll.LocalMusicManager;
import com.tiantiankuyin.scan.ISingleMediaScannerListener.SingleMediaScannerErrorType;
import com.tiantiankuyin.utils.CommonUtils;
import com.tiantiankuyin.utils.MusicOperate;
import com.tiantiankuyin.utils.PinYinUtil;
import com.tiantiankuyin.TianlApp;

/**
 * 单个音频扫描并入库操作。
 * 
 * @author Perry
 * 
 */
public class SingleMediaScanner {

	private ISingleMediaScannerListener singleMediaScannerListener; // 单个扫描监听器

	private Context context;

	public SingleMediaScanner(
			ISingleMediaScannerListener singleMediaScannerListener) {
		this.context = TianlApp.newInstance();
		this.singleMediaScannerListener = singleMediaScannerListener;
	}


	/**
	 * 单个扫描的歌曲
	 * 
	 * @param path
	 *            要扫描的文件路径
	 * @param gid
	 *            要扫描的歌曲gid
	 * @param fileID
	 *            要扫描的歌曲fileID
	 * @author Perry
	 */
	public void scanFile(String path, final String gid, final String fileID,
			final String title, final String artist) {

		singleMediaScannerListener.onSingleMediaScannerBegin();

		File file = new File(path);
		if (!file.exists()) {
			// 当前要扫描的文件不存在。
			singleMediaScannerListener.onSingleMediaFail(path,
					SingleMediaScannerErrorType.FILE_NOT_EXIST);
			return;
		}

		MediaScannerConnection.scanFile(context, new String[] { path }, null,
				new OnScanCompletedListener() {

					@Override
					public void onScanCompleted(String path, Uri uri) {// 系统解析完成后的回调

						if (uri != null && uri.getPath().length() > 0) {// 系统解析成功。

							// 根据path文件路径，查询系统数据库中对应的MusicInfo
							MusicInfo musicInfo = ScanUtil
									.scanMediaStoreByPath(context, path, true);
							// 判断musicInfo能否插入宜搜音乐数据库
							if (musicInfo != null) {// 当前符合插入宜搜音乐数据库

								musicInfo.setGid(Long.valueOf(gid));// 设置音频文件的gid
								musicInfo.setFileID(fileID);// 设置音频文件的fileID

								musicInfo.setTitle(title);// 设置歌曲名
								// 获取歌曲的歌手名。其中，若歌手名属于乱码，则使用“未知歌手”填入，其对应的ID为“-1”
								if (artist == null || artist.equals("")
										|| CommonUtils.isMessyCode(artist)
										|| artist.equals("<unknown>")) {// 歌手名为乱码。
									musicInfo.setArtist("未知歌手");
									musicInfo.setArtistID(-1);
									musicInfo.setArtistSortKey("#");
								} else { // 歌手名不为乱码
									musicInfo.setArtist(artist);
									char firstLetter = '#';
									if (artist.length() > 0) {
										firstLetter = PinYinUtil
												.getFirstLetter(artist
														.charAt(0));
									}
									musicInfo.setArtistSortKey(String
											.valueOf(firstLetter));
								}

								MusicOperate.newInstance().addMusicToLocal(
										musicInfo);
								List<MusicInfo> musicInfos = LocalMusicManager
										.getInstence().getMusicListByMusicPath(
												path);
								if (musicInfos != null && musicInfos.size() > 0) {
									musicInfo = musicInfos.get(musicInfos
											.size() - 1);
									singleMediaScannerListener
											.onSingleMediaCompleted(path,
													musicInfo);
								} else {
									singleMediaScannerListener
											.onSingleMediaFail(
													path,
													SingleMediaScannerErrorType.INSERT_FAIL);
								}
							} else {// 当前不符合插入宜搜音乐数据库
								singleMediaScannerListener
										.onSingleMediaFail(
												path,
												SingleMediaScannerErrorType.INSERT_FAIL);
							}
						} else {// 系统解析失败。
							singleMediaScannerListener.onSingleMediaFail(path,
									SingleMediaScannerErrorType.SCANNER_FAIL);
						}
					}
				});
	}

	/**
	 * 单个扫描从本地已经存在的歌曲
	 * 
	 * @param path
	 *            要扫描的文件路径
	 * @param gid
	 *            要扫描的歌曲gid
	 * @param fileID
	 *            要扫描的歌曲fileID
	 * @author Perry
	 */
	public void scanFile(String path) {

		singleMediaScannerListener.onSingleMediaScannerBegin();

		File file = new File(path);
		if (!file.exists()) {
			// 当前要扫描的文件不存在。
			singleMediaScannerListener.onSingleMediaFail(path,
					SingleMediaScannerErrorType.FILE_NOT_EXIST);
			return;
		}

		MediaScannerConnection.scanFile(context, new String[] { path }, null,
				new OnScanCompletedListener() {

					@Override
					public void onScanCompleted(String path, Uri uri) {// 系统解析完成后的回调

						if (uri != null && uri.getPath().length() > 0) {// 系统解析成功。

							// 根据path文件路径，查询系统数据库中对应的MusicInfo
							MusicInfo musicInfo = ScanUtil
									.scanMediaStoreByPath(context, path, false);
							// 判断musicInfo能否插入宜搜音乐数据库
							if (musicInfo != null) {// 当前符合插入宜搜音乐数据库

								MusicOperate.newInstance().addMusicToLocal(
										musicInfo);
								List<MusicInfo> musicInfos = LocalMusicManager
										.getInstence().getMusicListByFolderPath(
												path);
								if (musicInfos != null && musicInfos.size() > 0) {
									musicInfo = musicInfos.get(musicInfos
											.size() - 1);
									singleMediaScannerListener
											.onSingleMediaCompleted(path,
													musicInfo);
								} else {
									singleMediaScannerListener
											.onSingleMediaFail(
													path,
													SingleMediaScannerErrorType.INSERT_FAIL);
								}
							} else {// 当前不符合插入宜搜音乐数据库
								singleMediaScannerListener
										.onSingleMediaFail(
												path,
												SingleMediaScannerErrorType.INSERT_FAIL);
							}
						} else {// 系统解析失败。
							singleMediaScannerListener.onSingleMediaFail(path,
									SingleMediaScannerErrorType.SCANNER_FAIL);
						}
					}
				});
	}

}
