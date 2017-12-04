package com.tiantiankuyin.scan;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.tiantiankuyin.bean.MusicInfo;
import com.tiantiankuyin.utils.CommonUtils;
import com.tiantiankuyin.utils.Lg;
import com.tiantiankuyin.utils.PinYinUtil;

public class ScanUtil {

	/**客户端支持的音频格式*/
	public final static String[] SUPPORT_AUDIO_FORMAT = { "MP3", "AAC", "M4A",
			"OGG", "WAV", "WMA", "FLAC", "APE" }; 
	
	/**
	 * 扫描过滤歌曲的最短时长
	 * 
	 * @author Perry
	 */
	public final static long MIN_MEDIA_DURATION = 10000;

	/**
	 * 将媒体库查询的到的游标，转成符合宜搜数据库格式的MusicInfo集合。
	 * 
	 * @param cursor
	 * @return
	 */
	public static MusicInfo changToMusicInfo(Cursor cursor) {
		final int indexId = cursor
				.getColumnIndex(MediaStore.Audio.AudioColumns._ID);
		final int indexData = cursor
				.getColumnIndex(MediaStore.Audio.AudioColumns.DATA);
		final int indexDisplay_Name = cursor
				.getColumnIndex(MediaStore.Audio.AudioColumns.DISPLAY_NAME);
		final int indexSize = cursor
				.getColumnIndex(MediaStore.Audio.AudioColumns.SIZE);
		final int indexTitle = cursor
				.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE);
		final int indexDuration = cursor
				.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION);
		final int indexArtist_Id = cursor
				.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST_ID);
		final int indexArtist = cursor
				.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST);
		final int indexAlbum_Id = cursor
				.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID);
		final int indexAlbum = cursor
				.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM);
		final int indexDateModified = cursor
				.getColumnIndex(MediaStore.Audio.AudioColumns.DATE_MODIFIED);

		MusicInfo musicInfo = new MusicInfo();
		// 歌曲的系统ID
		musicInfo.setSystemID(cursor.getLong(indexId));
		// 歌曲所在的路径
		musicInfo.setLocalUrl(cursor.getString(indexData));
		// 歌曲所在的文件夹
		File file = new File(cursor.getString(indexData));
		musicInfo.setFolderUrl(file.getParent());
		// 歌曲的文件名
		musicInfo.setDisplayName(cursor.getString(indexDisplay_Name));
		// 歌曲所占的空间大小
		musicInfo.setSize(cursor.getInt(indexSize));

		// 歌曲ID3中的歌曲名。其中，若歌曲名字属于乱码，则使用歌曲的文件名取代其乱码。
		String title = cursor.getString(indexTitle);
		if (CommonUtils.isMessyCode(title)) {// 歌曲名为乱码
			String fileName = cursor.getString(indexDisplay_Name);
			if (fileName.contains(".")) {// 文件名包含后缀
				// 获取歌曲的文件名（去后缀）
				title = fileName.substring(0, fileName.lastIndexOf("."));
			} else {
				// 获取歌曲的文件名
				title = fileName;
			}
		}
		musicInfo.setTitle(title);

		// 获取歌曲的时长
		musicInfo.setDuration(cursor.getLong(indexDuration));
		// 设置歌曲的码率，默认为0
		musicInfo.setCodeRate(0);
		// 获取歌曲的歌手ID
		musicInfo.setArtistID(cursor.getLong(indexArtist_Id));

		// 获取歌曲的歌手名。其中，若歌手名属于乱码，则使用“未知歌手”填入，其对应的ID为“-1”
		String artist = cursor.getString(indexArtist).trim();
		long artistID = cursor.getLong(indexArtist_Id);
		if (CommonUtils.isMessyCode(artist) || artist.equals("<unknown>")) {// 歌手名为乱码。
			musicInfo.setArtist("未知歌手");
			musicInfo.setArtistID(-1);
			musicInfo.setArtistSortKey("#");
		} else { // 歌手名不为乱码
			musicInfo.setArtist(artist);
			musicInfo.setArtistID(artistID);
			char firstLetter = '#';
			if (artist.length() > 0) {
				firstLetter = PinYinUtil.getFirstLetter(artist.charAt(0));
			}
			musicInfo.setArtistSortKey(String.valueOf(firstLetter));
		}

		// 获取歌曲的专辑名。其中，若专辑名属于乱码，则使用“未知专辑”填入，其对应的ID为“-1”
		String album = cursor.getString(indexAlbum);
		long albumID = cursor.getLong(indexAlbum_Id);
		if (CommonUtils.isMessyCode(album)) {// 专辑名为乱码。
			musicInfo.setAlbum("未知专辑");
			musicInfo.setAlbumID(-1);
			musicInfo.setAlbumSortKey("#");
		} else { // 专辑名不为乱码
			musicInfo.setAlbum(album);
			musicInfo.setAlbumID(albumID);
			char firstLetter = '#';
			if (album.length() > 0) {
				firstLetter = PinYinUtil.getFirstLetter(album.charAt(0));
			}
			musicInfo.setAlbumSortKey(String.valueOf(firstLetter));
		}

		// 设置歌曲图片地址，默认为空。
		musicInfo.setImageUrl("");
		// 设置歌曲歌词地址，默认为空。
		musicInfo.setLrcUrl("");
		// 记录当前歌曲是否被添加到我的最爱。默认为0，未被收藏。
		musicInfo.setDateAddedFav(0);
		// 记录当前歌曲入库的时间，时间单位为秒。
		musicInfo.setDateAdded(System.currentTimeMillis() / 1000);
		// 记录歌曲被修改的时间（单位：秒）
		musicInfo.setDateModified(cursor.getLong(indexDateModified));
		// 设置当前歌曲的gid。本地歌曲默认为0
		musicInfo.setGid(0);
		// 设置当前歌曲的fileID。本地歌曲默认为0
		musicInfo.setFileID(null);

		// 人工处理，多米的歌曲入库信息。
		/*
		 * 规则如下： 若当前歌曲是在多米路径下，且歌手名字为“<unknown>”，且当前文件名能截取歌手名。 修改如下： 根据文件名截取
		 * 歌名和歌手名。并存进musicInfo中去。
		 */
		File folder = new File(musicInfo.getFolderUrl());
		// 在多米文件夹路径下
		if (folder.getParentFile().getName().equals("DUOMI")) {
			String fileName = musicInfo.getDisplayName();
			if (fileName.contains("_") && fileName.contains(".")) {
				String songName = fileName.substring(0, fileName.indexOf('_'));
				String artistName = fileName.substring(
						fileName.indexOf('_') + 1, fileName.lastIndexOf('.'));
				musicInfo.setTitle(songName);
				musicInfo.setArtist(artistName);

				char firstLetter = '#';
				if (artistName.length() > 0) {
					firstLetter = PinYinUtil.getFirstLetter(artistName
							.charAt(0));
				}
				musicInfo.setArtistSortKey(String.valueOf(firstLetter));
			}
		}
		return musicInfo;
	}

	/**
	 * 将媒体库查询的到的游标，转成符合宜搜数据库格式的MusicInfo集合。
	 * 
	 * @param cursor
	 * @return
	 * @author Perry
	 */
	public static List<MusicInfo> changToMusicInfos(Cursor cursor) {
		if (cursor.isAfterLast())
			return null;

		List<MusicInfo> musicInfos = new ArrayList<MusicInfo>();
		while (cursor.moveToNext()) {
			MusicInfo musicInfo = changToMusicInfo(cursor);
			musicInfos.add(musicInfo);
		}

		return musicInfos;
	}

	/**
	 * 获取媒体库中，有歌曲的文件夹路径String数组。
	 * 
	 * @param context
	 * @return 返回含有歌曲的文件夹路径String数组，若没有则返回null
	 * @author Perry
	 */
	public static String[] getMediaDirPaths(Context context) {
		String[] projection = new String[] { MediaStore.Audio.Media.DATA };
		String selection = null;
		String[] selectionArgs = null;

		String SDCardPath = getExternalStoragePath();
		selection = MediaStore.Audio.AudioColumns.DURATION + " > ?" + " AND "
				+ MediaStore.Audio.AudioColumns.DATA + " LIKE ?";
		selectionArgs = new String[] { String.valueOf(MIN_MEDIA_DURATION),
				SDCardPath + "%" };

		// 获取媒体库中所有的音频文件的文件路径
		Cursor cursor = context.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection,
				selection, selectionArgs, null);

		if (cursor==null) {
			return null;
		}

		if(cursor.isAfterLast()){
			cursor.close();
			return null;
		}
		
		final int indexData = cursor
				.getColumnIndex(MediaStore.Audio.Media.DATA);

		List<String> mediaDirList = new ArrayList<String>();
		while (cursor.moveToNext()) {
			String path = cursor.getString(indexData);
			File file = new File(path);
			String dir = file.getParent();
			if (!mediaDirList.contains(dir)) {
				mediaDirList.add(dir);
			}
		}
		cursor.close();
		int size = mediaDirList.size();
		String[] mediaDirFiles = (String[]) mediaDirList
				.toArray(new String[size]);
		return mediaDirFiles;

	}

	/**
	 * 根据音频文件路劲，获取系统数据库中的数据，并转换成MusicInfo
	 * 
	 * @param context
	 * @param path
	 * @return 若有数据，则返回MusicInfo；否则为null
	 */
	public static MusicInfo scanMediaStoreByPath(Context context, String path, boolean isLimitDuration) {
		String[] projection = new String[] { MediaStore.Audio.Media._ID,
				MediaStore.Audio.Media.DATA,
				MediaStore.Audio.Media.DISPLAY_NAME,
				MediaStore.Audio.Media.SIZE, MediaStore.Audio.Media.TITLE,
				MediaStore.Audio.Media.DURATION,
				MediaStore.Audio.Media.ARTIST_ID,
				MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM_ID,
				MediaStore.Audio.Media.ALBUM,
				MediaStore.Audio.Media.DATE_MODIFIED };
		String selection = null;
		String[] selectionArgs = null;

		String SDCardPath = getExternalStoragePath();
		selection = MediaStore.Audio.AudioColumns.DURATION + " > ? AND "
				+ MediaStore.Audio.AudioColumns.DATA + " = ? " + " AND "
				+ MediaStore.Audio.AudioColumns.DATA + " LIKE ?";
		if(isLimitDuration){
			selectionArgs = new String[] {
					String.valueOf(ScanUtil.MIN_MEDIA_DURATION), path,
					SDCardPath + "%" };
		} else {
			selectionArgs = new String[] {
					String.valueOf(1), path,
					SDCardPath + "%" };
		}

		Cursor cursor = context.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection,
				selection, selectionArgs, MediaStore.Audio.Media.DATE_ADDED);

		if (cursor == null) {
			return null;
		}
		MusicInfo musicInfo;
		if (cursor.moveToFirst()) {
			musicInfo = changToMusicInfo(cursor);
		} else {
			cursor.close();
			return null;
		}

		// 若当前文件不存在，则返回为空
		if (!new File(musicInfo.getLocalUrl()).exists()) {
			cursor.close();
			return null;
		}
		cursor.close();
		return musicInfo;
	}

	/**
	 * 获取系统媒体库中的数据，并转换成MusicInfo的集合
	 * 
	 * @param context
	 * @return 若有数据，则返回MusicInfos；否则为null
	 */
	public static List<MusicInfo> scanMediaStore(Context context) {
		String[] projection = new String[] { MediaStore.Audio.Media._ID,
				MediaStore.Audio.Media.DATA,
				MediaStore.Audio.Media.DISPLAY_NAME,
				MediaStore.Audio.Media.SIZE, MediaStore.Audio.Media.TITLE,
				MediaStore.Audio.Media.DURATION,
				MediaStore.Audio.Media.ARTIST_ID,
				MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM_ID,
				MediaStore.Audio.Media.ALBUM,
				MediaStore.Audio.Media.DATE_MODIFIED };
		String selection = null;
		String[] selectionArgs = null;

		String SDCardPath =getExternalStoragePath();
		selection = MediaStore.Audio.AudioColumns.DURATION + " > ?" + " AND "
				+ MediaStore.Audio.AudioColumns.DATA + " LIKE ?";
		selectionArgs = new String[] {
				String.valueOf(ScanUtil.MIN_MEDIA_DURATION), SDCardPath + "%" };

		Cursor cursor = context.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection,
				selection, selectionArgs, MediaStore.Audio.Media.DATE_ADDED);

		if (cursor == null) {
			return null;
		}
		Lg.d("test", "cursor.count() = " + cursor.getCount());
		List<MusicInfo> musicInfos = ScanUtil.changToMusicInfos(cursor);
		if (musicInfos != null) {
			// 查找出不存在的MusicInfo
			List<MusicInfo> nonexistentMediaDatas = ScanUtil
					.getNonexistentMediaDatas(musicInfos);
			// 清除不存在的MusicInfo
			musicInfos.removeAll(nonexistentMediaDatas);
			//查找出客户端不支持的MusicInfo
			List<MusicInfo> nonSupportMusicInfos = ScanUtil.getNonSupportMediaFormatDatas(musicInfos);
			//清除不支持的MusicInfo
			musicInfos.removeAll(nonSupportMusicInfos);
		}else{
			musicInfos = new ArrayList<MusicInfo>();
		}
		Lg.d("test", "musicInfos.size() = " + musicInfos.size());
		cursor.close();
		return musicInfos;
	}

	/**
	 * 获取系统媒体库中不满足10秒的数据，并转换成MusicInfo的集合
	 * 
	 * @param context
	 * @return 若有数据，则返回MusicInfos；否则为null
	 */
	public static List<String> scanMediaStoreDiscontentPaths(Context context) {
		String[] projection = new String[] { MediaStore.Audio.Media.DATA };
		String selection = null;
		String[] selectionArgs = null;
		String SDCardPath = getExternalStoragePath();
		selection = MediaStore.Audio.AudioColumns.DURATION + " <= ?" + " AND "
				+ MediaStore.Audio.AudioColumns.DATA + " LIKE ?";
		selectionArgs = new String[] {
				String.valueOf(ScanUtil.MIN_MEDIA_DURATION), SDCardPath + "%" };

		Cursor cursor = context.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection,
				selection, selectionArgs, MediaStore.Audio.Media.DATE_ADDED);
		if (cursor == null) {
			return null;
		}
		if (cursor.isAfterLast()) {
			return null;
		}
		List<String> paths = new ArrayList<String>();
		while (cursor.moveToNext()) {
			final int indexData = cursor
					.getColumnIndex(MediaStore.Audio.AudioColumns.DATA);
			String path = cursor.getString(indexData);
			paths.add(path);
		}
		cursor.close();
		return paths;
	}

	/**
	 * 遍历mediaDir的所有文件夹，
	 * 
	 * @param mediaDir
	 * @return 所有音频文件路径的集合。
	 * @author Perry
	 */
	public static List<String> getMediaPaths(String[] mediaDirs) {
		if (mediaDirs.length <= 0)
			return null;
		List<String> mediaPathList = new ArrayList<String>();
		for (String dir : mediaDirs) {
			File fileDir = new File(dir);
			FileFilter filter = new FileFilter() {

				@Override
				public boolean accept(File pathname) {
					if (isMediaFile(pathname))
						return true;
					return false;
				}
			};
			File[] files = fileDir.listFiles(filter);
			if (files == null)
				continue;
			for (File file : files) {
				mediaPathList.add(file.getAbsolutePath());
			}
		}
		if (mediaPathList.size() == 0)
			return null;
		return mediaPathList;
	}

	/**
	 * 判断是否为媒体文件
	 * 
	 * @param file
	 * @return
	 */
	public static boolean isMediaFile(File file) {
		String fileName = file.getName();
		if (!fileName.contains(".") || file.isDirectory())
			return false;
		String suffix = fileName.substring(fileName.lastIndexOf(".") + 1,
				fileName.length());
		
		for(String audioFormat: SUPPORT_AUDIO_FORMAT){
			if(suffix.equalsIgnoreCase(audioFormat))
				return true;
		}
		return false;
	}

	/**
	 * 查找传入的musicInfos，并返回其中已不存在的媒体信息。
	 * 
	 * @param musicInfos
	 * @return 其中不存的MusicInfo集合。
	 */
	public static List<MusicInfo> getNonexistentMediaDatas(
			List<MusicInfo> musicInfos) {
		List<MusicInfo> nonexistentMusicInfos = new ArrayList<MusicInfo>();
		if (musicInfos != null) {
			for (MusicInfo musicInfo : musicInfos) {
				File file = new File(musicInfo.getLocalUrl());
				if (!file.exists()) {
					nonexistentMusicInfos.add(musicInfo);
				}
			}
		}
		return nonexistentMusicInfos;
	}

	
	/**
	 * 查找传入的musicInfos，并返回其中不是宜搜客户端支持的的媒体信息。
	 * 
	 * @param musicInfos
	 * @return 其中不支持音频格式的的MusicInfo集合。
	 */
	private static List<MusicInfo> getNonSupportMediaFormatDatas(
			List<MusicInfo> musicInfos) {
		List<MusicInfo> nonSupportMusicInfos = new ArrayList<MusicInfo>();
		if (musicInfos != null) {
			for (MusicInfo musicInfo : musicInfos) {
				File file = new File(musicInfo.getLocalUrl());
				if (!ScanUtil.isMediaFile(file)) {
					nonSupportMusicInfos.add(musicInfo);
				}
			}
		}
		return nonSupportMusicInfos;
	}
	
	
	/**
	 * 通过Uri，查询系统数据库，并返回其对应的绝对文件路径。
	 * @param context
	 * @param contentUri
	 * @return
	 * @author Perry	
	 */
	public static String getRealPathFromURI(Context context, Uri contentUri) {
		if(contentUri==null){
			return null;
		}
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = context.getContentResolver().query(contentUri, proj, 
				null, // WHERE clause; which rows to return (all rows)
				null, // WHERE clause selection arguments (none)
				null); // Order-by clause (ascending by name)
		if (cursor == null) {
			return null;
		}
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}
	
	public static boolean isMusicInfoOutOfDate(MusicInfo musicInfo) {
		boolean isOutOfDate = false;
		File file = new File(musicInfo.getLocalUrl());
		if (file.lastModified() / 1000 == musicInfo.getDateModified() || file.lastModified() == musicInfo.getDateModified()) {
			isOutOfDate = false;
		} else {
			isOutOfDate = true;
		}
		return isOutOfDate;
	}

	private static String getExternalStoragePath() {
		File SDCardFile = Environment.getExternalStorageDirectory();
		if(SDCardFile.getParentFile() != null) {
			return SDCardFile.getParentFile().getAbsolutePath();
		} else {
			return SDCardFile.getAbsolutePath();
		}
	}

}
