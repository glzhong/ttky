package com.tiantiankuyin.database.dao.impl;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tiantiankuyin.bean.MusicInfo;
import com.tiantiankuyin.database.MusicDBHelper;
import com.tiantiankuyin.database.TableStructure;
import com.tiantiankuyin.database.dao.IMusicDao;
import com.tiantiankuyin.utils.Lg;

public class MusicDaoImpl implements IMusicDao {

	private MusicDBHelper dbHelper; // 音乐数据库

	public MusicDaoImpl(Context context) {
		dbHelper = new MusicDBHelper(context);
	}

	// 临时判断当前歌曲是否存在数据库。
	public static boolean isMusicExist(String musicID) {
		return false;
	}

	@Override
	public boolean insertMusicData(MusicInfo song) {
		synchronized (MusicDBHelper.LOCK) {
			boolean isSuccess = false;
			if (song == null)
				return false;
			SQLiteDatabase sqliteDatabase = dbHelper.getWritableDB();
			ContentValues values = new ContentValues();
			values.put(TableStructure.Music.MUSIC_SYSTEM_ID, song.getSystemID());
			values.put(TableStructure.Music.MUSIC_LOCAL_URL, song.getLocalUrl());
			values.put(TableStructure.Music.MUSIC_FOLDER_URL, song.getFolderUrl());
			values.put(TableStructure.Music.MUSIC_DISPLAY_NAME, song.getDisplayName());
			values.put(TableStructure.Music.MUSIC_SIZE, song.getSize());
			values.put(TableStructure.Music.MUSIC_TITLE, song.getTitle());
			values.put(TableStructure.Music.MUSIC_DURATION, song.getDuration());
			values.put(TableStructure.Music.MUSIC_CODE_RATE, song.getCodeRate());
			values.put(TableStructure.Music.MUSIC_ARTIST_ID, song.getArtistID());
			values.put(TableStructure.Music.MUSIC_ARTIST, song.getArtist());
			values.put(TableStructure.Music.MUSIC_ARTIST_SORT_KEY,
					song.getArtistSortKey());
			values.put(TableStructure.Music.MUSIC_ALBUM_ID, song.getAlbumID());
			values.put(TableStructure.Music.MUSIC_ALBUM, song.getAlbum());
			values.put(TableStructure.Music.MUSIC_ALBUM_SORT_KEY, song.getAlbumSortKey());
			values.put(TableStructure.Music.MUSIC_IMAGE_URL, song.getImageUrl());
			values.put(TableStructure.Music.MUSIC_LRC_URL, song.getLrcUrl());
			values.put(TableStructure.Music.MUSIC_DATE_ADDED_FAV, song.getDateAddedFav());
			values.put(TableStructure.Music.MUSIC_DATE_ADDED, song.getDateAdded());
			values.put(TableStructure.Music.MUSIC_DATE_MODIFIED, song.getDateModified());
			values.put(TableStructure.Music.MUSIC_GID, song.getGid());
			values.put(TableStructure.Music.MUSIC_FILE_ID, song.getFileID());
			long i = sqliteDatabase.insert(TableStructure.Music.TABLE_NAME,
					TableStructure.Music.MUSIC_ID, values);
			Lg.d("test", "单个插入 i" + i);
			if (i != -1)
				isSuccess = true;
			if (sqliteDatabase != null && sqliteDatabase.isOpen())
				sqliteDatabase.close();
			return isSuccess;
		}
	}

	@Override
	public boolean deleteMusicData(String songID) {
		synchronized (MusicDBHelper.LOCK) {
			boolean isSuccess = false;
			if (songID == null || songID.length() <= 0)
				return false;
			SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDB();
			int count = sqLiteDatabase.delete(TableStructure.Music.TABLE_NAME,
					TableStructure.Music.MUSIC_ID + " = ?", new String[] { songID });
			sqLiteDatabase.delete(TableStructure.RelateList.TABLE_NAME,
					TableStructure.RelateList.RELATE_LIST_SONG_ID + " = ?",
					new String[] { songID });
			if (count > 0)
				isSuccess = true;
			if (sqLiteDatabase != null && sqLiteDatabase.isOpen())
				sqLiteDatabase.close();
			return isSuccess;
		}
	}

	@Override
	public boolean updateMusicData(MusicInfo song) {
		synchronized (MusicDBHelper.LOCK) {
			boolean isSuccess = false;
			String songID = String.valueOf(song.getId());
			if (songID == null || songID.length() <= 0)
				return false;

			SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDB();
			ContentValues values = new ContentValues();
			values.put(TableStructure.Music.MUSIC_ID, song.getId());
			values.put(TableStructure.Music.MUSIC_SYSTEM_ID, song.getSystemID());
			values.put(TableStructure.Music.MUSIC_LOCAL_URL, song.getLocalUrl());
			values.put(TableStructure.Music.MUSIC_FOLDER_URL, song.getFolderUrl());
			values.put(TableStructure.Music.MUSIC_DISPLAY_NAME, song.getDisplayName());
			values.put(TableStructure.Music.MUSIC_SIZE, song.getSize());
			values.put(TableStructure.Music.MUSIC_TITLE, song.getTitle());
			values.put(TableStructure.Music.MUSIC_DURATION, song.getDuration());
			values.put(TableStructure.Music.MUSIC_CODE_RATE, song.getCodeRate());
			values.put(TableStructure.Music.MUSIC_ARTIST_ID, song.getArtistID());
			values.put(TableStructure.Music.MUSIC_ARTIST, song.getArtist());
			values.put(TableStructure.Music.MUSIC_ARTIST_SORT_KEY,
					song.getArtistSortKey());
			values.put(TableStructure.Music.MUSIC_ALBUM_ID, song.getAlbumID());
			values.put(TableStructure.Music.MUSIC_ALBUM, song.getAlbum());
			values.put(TableStructure.Music.MUSIC_ALBUM_SORT_KEY, song.getAlbumSortKey());
			values.put(TableStructure.Music.MUSIC_IMAGE_URL, song.getImageUrl());
			values.put(TableStructure.Music.MUSIC_LRC_URL, song.getLrcUrl());
			values.put(TableStructure.Music.MUSIC_DATE_ADDED_FAV, song.getDateAddedFav());
			values.put(TableStructure.Music.MUSIC_DATE_ADDED, song.getDateAdded());
			values.put(TableStructure.Music.MUSIC_DATE_MODIFIED, song.getDateModified());
			values.put(TableStructure.Music.MUSIC_GID, song.getGid());
			values.put(TableStructure.Music.MUSIC_FILE_ID, song.getFileID());
			int count = sqLiteDatabase.update(TableStructure.Music.TABLE_NAME,
					values, TableStructure.Music.MUSIC_ID + " = ?",
					new String[] { songID });
			if (count > 0)
				isSuccess = true;
			if (sqLiteDatabase != null && sqLiteDatabase.isOpen())
				sqLiteDatabase.close();
			return isSuccess;
		}
	}

	@Override
	public boolean updateMusicDatas(List<MusicInfo> songs) {
		synchronized (MusicDBHelper.LOCK) {
			boolean isSuccess = false;
			// dbHelper.createTable(true);
			SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDB();
			sqLiteDatabase.beginTransaction();
			for (MusicInfo song : songs) {
				String songID = String.valueOf(song.getId());
				if (songID == null || songID.length() <= 0) {
					isSuccess = false;
					break;
				}
				ContentValues values = new ContentValues();
				values.put(TableStructure.Music.MUSIC_ID, song.getId());
				values.put(TableStructure.Music.MUSIC_SYSTEM_ID, song.getSystemID());
				values.put(TableStructure.Music.MUSIC_LOCAL_URL, song.getLocalUrl());
				values.put(TableStructure.Music.MUSIC_FOLDER_URL, song.getFolderUrl());
				values.put(TableStructure.Music.MUSIC_DISPLAY_NAME,
						song.getDisplayName());
				values.put(TableStructure.Music.MUSIC_SIZE, song.getSize());
				values.put(TableStructure.Music.MUSIC_TITLE, song.getTitle());
				values.put(TableStructure.Music.MUSIC_DURATION, song.getDuration());
				values.put(TableStructure.Music.MUSIC_CODE_RATE, song.getCodeRate());
				values.put(TableStructure.Music.MUSIC_ARTIST_ID, song.getArtistID());
				values.put(TableStructure.Music.MUSIC_ARTIST, song.getArtist());
				values.put(TableStructure.Music.MUSIC_ARTIST_SORT_KEY,
						song.getArtistSortKey());
				values.put(TableStructure.Music.MUSIC_ALBUM_ID, song.getAlbumID());
				values.put(TableStructure.Music.MUSIC_ALBUM, song.getAlbum());
				values.put(TableStructure.Music.MUSIC_ALBUM_SORT_KEY,
						song.getAlbumSortKey());
				values.put(TableStructure.Music.MUSIC_IMAGE_URL, song.getImageUrl());
				values.put(TableStructure.Music.MUSIC_LRC_URL, song.getLrcUrl());
				values.put(TableStructure.Music.MUSIC_DATE_ADDED_FAV,
						song.getDateAddedFav());
				values.put(TableStructure.Music.MUSIC_DATE_ADDED, song.getDateAdded());
				values.put(TableStructure.Music.MUSIC_DATE_MODIFIED,
						song.getDateModified());
				values.put(TableStructure.Music.MUSIC_GID, song.getGid());
				values.put(TableStructure.Music.MUSIC_FILE_ID, song.getFileID());
				int count = sqLiteDatabase.update(TableStructure.Music.TABLE_NAME,
						values, TableStructure.Music.MUSIC_ID + " = ?",
						new String[] { songID });
				isSuccess = count > 0 ? true : false;
				if (!isSuccess)
					break;
			}
			if (isSuccess) {
				sqLiteDatabase.setTransactionSuccessful();
			}
			sqLiteDatabase.endTransaction();
			if (sqLiteDatabase != null && sqLiteDatabase.isOpen())
				sqLiteDatabase.close();
			return isSuccess;
		}
	}

	@Override
	public List<MusicInfo> selectMusicDatas() {
		synchronized (MusicDBHelper.LOCK) {
			String selectSQL = "SELECT * FROM " + TableStructure.Music.TABLE_NAME
					+ " ORDER BY " + TableStructure.Music.MUSIC_DATE_ADDED + " DESC";
			return selectMusicDatasBySQL(selectSQL, null);
		}
	}

	//TODO 所有直接通过传sql语言的查询方法，都要封装成单独的函数
	@Override
	public List<MusicInfo> selectMusicDatasBySQL(String selectSQL,
			String[] selectionArgs) {
		synchronized (MusicDBHelper.LOCK) {
			Cursor cursor = null;
			SQLiteDatabase sqliteDatabase = null;
			try {
				sqliteDatabase = dbHelper.getReadableDB();
				cursor = sqliteDatabase.rawQuery(selectSQL, selectionArgs);
				int fileCount = cursor.getCount();
				if (fileCount <= 0) {
					cursor.close();
					if (sqliteDatabase != null && sqliteDatabase.isOpen())
						sqliteDatabase.close();
					return null;
				}
				List<MusicInfo> musicInfos = new ArrayList<MusicInfo>();

				final int idIndex = cursor.getColumnIndex(TableStructure.Music.MUSIC_ID);
				final int systemIDIndex = cursor
						.getColumnIndex(TableStructure.Music.MUSIC_SYSTEM_ID);
				final int localUrlIndex = cursor
						.getColumnIndex(TableStructure.Music.MUSIC_LOCAL_URL);
				final int folderUrlIndex = cursor
						.getColumnIndex(TableStructure.Music.MUSIC_FOLDER_URL);
				final int displayNameIndex = cursor
						.getColumnIndex(TableStructure.Music.MUSIC_DISPLAY_NAME);
				final int titleIndex = cursor
						.getColumnIndex(TableStructure.Music.MUSIC_TITLE);
				final int durationIndex = cursor
						.getColumnIndex(TableStructure.Music.MUSIC_DURATION);
				final int sizeIndex = cursor
						.getColumnIndex(TableStructure.Music.MUSIC_SIZE);
				final int codeRateIndex = cursor
						.getColumnIndex(TableStructure.Music.MUSIC_CODE_RATE);
				final int artistIDIndex = cursor
						.getColumnIndex(TableStructure.Music.MUSIC_ARTIST_ID);
				final int artistIndex = cursor
						.getColumnIndex(TableStructure.Music.MUSIC_ARTIST);
				final int artistSortKeyIndex = cursor
						.getColumnIndex(TableStructure.Music.MUSIC_ARTIST_SORT_KEY);
				final int albumIDIndex = cursor
						.getColumnIndex(TableStructure.Music.MUSIC_ALBUM_ID);
				final int albumIndex = cursor
						.getColumnIndex(TableStructure.Music.MUSIC_ALBUM);
				final int albumSortKeyIndex = cursor
						.getColumnIndex(TableStructure.Music.MUSIC_ALBUM_SORT_KEY);
				final int lrcUrlIndex = cursor
						.getColumnIndex(TableStructure.Music.MUSIC_LRC_URL);
				final int imageUrlIndex = cursor
						.getColumnIndex(TableStructure.Music.MUSIC_IMAGE_URL);
				final int dateAddedFavIndex = cursor
						.getColumnIndex(TableStructure.Music.MUSIC_DATE_ADDED_FAV);
				final int dateAddedIndex = cursor
						.getColumnIndex(TableStructure.Music.MUSIC_DATE_ADDED);
				final int dateModifiedIndex = cursor
						.getColumnIndex(TableStructure.Music.MUSIC_DATE_MODIFIED);
				final int gidIndex = cursor
						.getColumnIndex(TableStructure.Music.MUSIC_GID);
				final int fileIDIndex = cursor
						.getColumnIndex(TableStructure.Music.MUSIC_FILE_ID);

				while (cursor.moveToNext()) {
					long id = cursor.getLong(idIndex);
					long systemID = cursor.getLong(systemIDIndex);
					String localUrl = cursor.getString(localUrlIndex);
					String folderUrl = cursor.getString(folderUrlIndex);
					String displayName = cursor.getString(displayNameIndex);
					String title = cursor.getString(titleIndex);
					long duration = cursor.getLong(durationIndex);
					int size = cursor.getInt(sizeIndex);
					long codeRate = cursor.getLong(codeRateIndex);
					long artistID = cursor.getLong(artistIDIndex);
					String artist = cursor.getString(artistIndex);
					String artistSortKey = cursor.getString(artistSortKeyIndex);
					long albumID = cursor.getLong(albumIDIndex);
					String album = cursor.getString(albumIndex);
					String albumSortKey = cursor.getString(albumSortKeyIndex);
					String lrcUrl = cursor.getString(lrcUrlIndex);
					String imageUrl = cursor.getString(imageUrlIndex);
					long dateAddedFav = cursor.getLong(dateAddedFavIndex);
					long dateAdded = cursor.getLong(dateAddedIndex);
					long dateModified = cursor.getLong(dateModifiedIndex);
					long gid = cursor.getLong(gidIndex);
					String fileID = cursor.getString(fileIDIndex);
					MusicInfo musicInfo = new MusicInfo(id, systemID, localUrl,
							folderUrl, displayName, title, duration, size,
							codeRate, artistID, artist, artistSortKey, albumID,
							album, albumSortKey, lrcUrl, imageUrl,
							dateAddedFav, dateAdded, dateModified, gid, fileID);
					musicInfos.add(musicInfo);
				}
				if (sqliteDatabase != null && sqliteDatabase.isOpen())
					sqliteDatabase.close();
				return musicInfos;
			} catch (Exception e) {
				//e.printStackTrace();
			} finally {
				if (cursor != null)
					cursor.close();
				if (sqliteDatabase != null && sqliteDatabase.isOpen())
					sqliteDatabase.close();
			}
			return null;
		}
	}

	@Override
	public boolean insertAllMusicDatas(List<MusicInfo> songs) {
		synchronized (MusicDBHelper.LOCK) {
			boolean isSuccess = false;
			 //dbHelper.createTable(true);
			SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDB();
			sqLiteDatabase.beginTransaction();
			for (MusicInfo song : songs) {
				ContentValues values = new ContentValues();
				values.put(TableStructure.Music.MUSIC_SYSTEM_ID, song.getSystemID());
				values.put(TableStructure.Music.MUSIC_LOCAL_URL, song.getLocalUrl());
				values.put(TableStructure.Music.MUSIC_FOLDER_URL, song.getFolderUrl());
				values.put(TableStructure.Music.MUSIC_DISPLAY_NAME,
						song.getDisplayName());
				values.put(TableStructure.Music.MUSIC_SIZE, song.getSize());
				values.put(TableStructure.Music.MUSIC_TITLE, song.getTitle());
				values.put(TableStructure.Music.MUSIC_DURATION, song.getDuration());
				values.put(TableStructure.Music.MUSIC_CODE_RATE, song.getCodeRate());
				values.put(TableStructure.Music.MUSIC_ARTIST_ID, song.getArtistID());
				values.put(TableStructure.Music.MUSIC_ARTIST, song.getArtist());
				values.put(TableStructure.Music.MUSIC_ARTIST_SORT_KEY,
						song.getArtistSortKey());
				values.put(TableStructure.Music.MUSIC_ALBUM_ID, song.getAlbumID());
				values.put(TableStructure.Music.MUSIC_ALBUM, song.getAlbum());
				values.put(TableStructure.Music.MUSIC_ALBUM_SORT_KEY,
						song.getAlbumSortKey());
				values.put(TableStructure.Music.MUSIC_IMAGE_URL, song.getImageUrl());
				values.put(TableStructure.Music.MUSIC_LRC_URL, song.getLrcUrl());
				values.put(TableStructure.Music.MUSIC_DATE_ADDED_FAV,
						song.getDateAddedFav());
				values.put(TableStructure.Music.MUSIC_DATE_ADDED, song.getDateAdded());
				values.put(TableStructure.Music.MUSIC_DATE_MODIFIED,
						song.getDateModified());
				values.put(TableStructure.Music.MUSIC_GID, song.getGid());
				values.put(TableStructure.Music.MUSIC_FILE_ID, song.getFileID());
				long i = sqLiteDatabase.insert(TableStructure.Music.TABLE_NAME,
						TableStructure.Music.MUSIC_ID, values);
				isSuccess = i != -1 ? true : false;
				if (!isSuccess)
					break;
			}
			if (isSuccess) {
				sqLiteDatabase.setTransactionSuccessful();
			}
			sqLiteDatabase.endTransaction();
			if (sqLiteDatabase != null && sqLiteDatabase.isOpen())
				sqLiteDatabase.close();
			return isSuccess;
		}
	}

	@Override
	public boolean deleteAllMusicDatas(String... songIDs) {
		synchronized (MusicDBHelper.LOCK) {
			boolean isSuccess = false;
			SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDB();
			sqLiteDatabase.beginTransaction();
			//TODO 此处可通过一条sql语言实现
			for (String songID : songIDs) {

				int count = sqLiteDatabase.delete(TableStructure.Music.TABLE_NAME,
						TableStructure.Music.MUSIC_ID + " = ?", new String[] { songID });
				sqLiteDatabase.delete(TableStructure.RelateList.TABLE_NAME,
						TableStructure.RelateList.RELATE_LIST_SONG_ID + " = ?",
						new String[] { songID });
				if (count > 0)
					isSuccess = true;

				if (!isSuccess)
					break;
			}
			if (isSuccess) {
				sqLiteDatabase.setTransactionSuccessful();
			}
			sqLiteDatabase.endTransaction();
			if (sqLiteDatabase != null && sqLiteDatabase.isOpen())
				sqLiteDatabase.close();
			return isSuccess;
		}
	}

	@Override
	public boolean isMusicDataExist(String songID) {
		synchronized (MusicDBHelper.LOCK) {
			SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDB();
			Cursor cursor = sqLiteDatabase.query(TableStructure.Music.TABLE_NAME,
					null, TableStructure.Music.MUSIC_ID + " = ?",
					new String[] { songID }, null, null, null);
			boolean exist = cursor.moveToFirst();
			cursor.close();
			if (sqLiteDatabase != null && sqLiteDatabase.isOpen())
				sqLiteDatabase.close();
			return exist;
		}
	}

	//TODO 本类负责管理本地歌曲，但此处为netMusice，需进一步重构
	@Override
	public boolean isNetMusicDataExist(String fileID) {
		synchronized (MusicDBHelper.LOCK) {
			SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDB();
			Cursor cursor = sqLiteDatabase.query(TableStructure.Music.TABLE_NAME,
					null, TableStructure.Music.MUSIC_FILE_ID + " = ?",
					new String[] { fileID }, null, null, null);
			boolean exist = cursor.moveToFirst();
			cursor.close();
			if (sqLiteDatabase != null && sqLiteDatabase.isOpen())
				sqLiteDatabase.close();
			return exist;
		}
	}
	
	//TODO 所有直接通过传sql语言的查询方法，都要封装成单独的函数
	@Override
	public int selectMusicDataCountBySQL(String selectSQL,
			String[] selectionArgs) {
		synchronized (MusicDBHelper.LOCK) {
			Integer count = null;
			Cursor cursor = null;
			SQLiteDatabase sqliteDatabase = null;
			try {
				sqliteDatabase = dbHelper.getReadableDB();
				cursor = sqliteDatabase.rawQuery(selectSQL, selectionArgs);
				if(cursor != null && cursor.moveToFirst()){
					count = cursor.getInt(0);
				}
			} catch (Exception e) {
				//e.printStackTrace();
			} finally {
				if (cursor != null)
					cursor.close();
				if (sqliteDatabase != null && sqliteDatabase.isOpen())
					sqliteDatabase.close();
			}
			return count == null ? 0 : count;
		}
	}



}
