package com.tiantiankuyin.database.dao.impl;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tiantiankuyin.bean.MusicInfo;
import com.tiantiankuyin.bean.RelateInfo;
import com.tiantiankuyin.bean.SongListInfo;
import com.tiantiankuyin.database.MusicDBHelper;
import com.tiantiankuyin.database.TableStructure;
import com.tiantiankuyin.database.dao.IRelateDao;

public class RelateDaoImpl implements IRelateDao {

	private MusicDBHelper dbHelper; // 音乐数据库

	public RelateDaoImpl(Context context) {
		dbHelper = new MusicDBHelper(context);
	}

	@Override
	public boolean insertRelateData(RelateInfo relateInfo) {
		synchronized (MusicDBHelper.LOCK) {
			boolean isSuccess = false;
			if (relateInfo == null)
				return false;
			SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDB();

			ContentValues values = new ContentValues();

			String selectSQL = "SELECT * FROM "
					+ TableStructure.RelateList.TABLE_NAME + " WHERE "
					+ TableStructure.RelateList.RELATE_LIST_LIST_ID + " = ? AND "
					+ TableStructure.RelateList.RELATE_LIST_SONG_ID + " = ?";
			String[] selectionArgs = new String[] {
					String.valueOf(relateInfo.getListID()),
					String.valueOf(relateInfo.getSongID()) };
			List<RelateInfo> relateInfos = selectRelateDatasBySQL(
					sqLiteDatabase, selectSQL, selectionArgs);
			if (relateInfos != null && relateInfos.size() != 0) {
				values.put(TableStructure.RelateList.RELATE_LIST_ID, relateInfos.get(0)
						.getId());
			}
			values.put(TableStructure.RelateList.RELATE_LIST_LIST_ID, relateInfo.getListID());
			values.put(TableStructure.RelateList.RELATE_LIST_SONG_ID, relateInfo.getSongID());
			if (relateInfo.getDataAdded() > 0) {
				values.put(TableStructure.RelateList.RELATE_LIST_DATA_ADDED,
						relateInfo.getDataAdded());
			} else {
				values.put(TableStructure.RelateList.RELATE_LIST_DATA_ADDED,
						System.currentTimeMillis());
			}
			long i = sqLiteDatabase.replace(TableStructure.RelateList.TABLE_NAME,
					TableStructure.RelateList.RELATE_LIST_ID, values);
			if (i != -1)
				isSuccess = true;
			if (sqLiteDatabase != null && sqLiteDatabase.isOpen())
				sqLiteDatabase.close();
			return isSuccess;
		}
	}

	/**
	 * 根据SQL语句查询关联表。 仅供内部使用
	 */
	private List<RelateInfo> selectRelateDatasBySQL(
			SQLiteDatabase sqliteDatabase, String selectSQL,
			String[] selectionArgs) {
		synchronized (MusicDBHelper.LOCK) {
			// SQLiteDatabase sqliteDatabase = dbHelper.getReadableDB();
			Cursor cursor = sqliteDatabase.rawQuery(selectSQL, selectionArgs);
			int count = cursor.getCount();
			if (count <= 0) {
				cursor.close();
				return null;
			}

			List<RelateInfo> relateInfos = new ArrayList<RelateInfo>();
			while (cursor.moveToNext()) {
				long id = cursor.getLong(cursor
						.getColumnIndex(TableStructure.RelateList.RELATE_LIST_ID));
				long listID = cursor.getLong(cursor
						.getColumnIndex(TableStructure.RelateList.RELATE_LIST_LIST_ID));
				long songID = cursor.getLong(cursor
						.getColumnIndex(TableStructure.RelateList.RELATE_LIST_SONG_ID));
				long dataAdded = cursor.getLong(cursor
						.getColumnIndex(TableStructure.RelateList.RELATE_LIST_DATA_ADDED));
				RelateInfo relateInfo = new RelateInfo(id, listID, songID,
						dataAdded);
				relateInfos.add(relateInfo);
			}
			cursor.close();
			return relateInfos;
		}
	}

	@Override
	public boolean insertRelateDatas(List<RelateInfo> relateInfos) {
		synchronized (MusicDBHelper.LOCK) {
			boolean isSuccess = false;
			SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDB();
			sqLiteDatabase.beginTransaction();
			for (int i = relateInfos.size() - 1; i >= 0; i--) {
				RelateInfo relateInfo = relateInfos.get(i);
				ContentValues values = new ContentValues();

				String selectSQL = "SELECT * FROM "
						+ TableStructure.RelateList.TABLE_NAME + " WHERE "
						+ TableStructure.RelateList.RELATE_LIST_LIST_ID + " = ? AND "
						+ TableStructure.RelateList.RELATE_LIST_SONG_ID + " = ?";
				String[] selectionArgs = new String[] {
						String.valueOf(relateInfo.getListID()),
						String.valueOf(relateInfo.getSongID()) };
				List<RelateInfo> selectRelateInfos = selectRelateDatasBySQL(
						sqLiteDatabase, selectSQL, selectionArgs);
				if (selectRelateInfos != null && selectRelateInfos.size() != 0) {
					values.put(TableStructure.RelateList.RELATE_LIST_ID, selectRelateInfos
							.get(0).getId());
				}
				values.put(TableStructure.RelateList.RELATE_LIST_LIST_ID,
						relateInfo.getListID());
				values.put(TableStructure.RelateList.RELATE_LIST_SONG_ID,
						relateInfo.getSongID());
				if (relateInfo.getDataAdded() > 0) {
					values.put(TableStructure.RelateList.RELATE_LIST_DATA_ADDED,
							relateInfo.getDataAdded());
				} else {
					values.put(TableStructure.RelateList.RELATE_LIST_DATA_ADDED,
							System.currentTimeMillis());
				}
				long count = sqLiteDatabase.replace(
						TableStructure.RelateList.TABLE_NAME,
						TableStructure.RelateList.RELATE_LIST_ID, values);
				isSuccess = count != -1 ? true : false;
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
	public boolean deleteRelateData(RelateInfo relateInfo) {
		synchronized (MusicDBHelper.LOCK) {
			// TODO Never used should be deleted
			boolean isSuccess = false;
			if (relateInfo == null) {
				return false;
			}
			SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDB();
			int count = sqLiteDatabase.delete(TableStructure.RelateList.TABLE_NAME,
					TableStructure.RelateList.RELATE_LIST_LIST_ID + " = ? , "
							+ TableStructure.RelateList.RELATE_LIST_SONG_ID + " = ?",
					new String[] { String.valueOf(relateInfo.getListID()),
							String.valueOf(relateInfo.getSongID()) });
			if (count > 0)
				isSuccess = true;
			if (sqLiteDatabase != null && sqLiteDatabase.isOpen())
				sqLiteDatabase.close();
			return isSuccess;
		}
	}

	@Override
	public boolean deleteRelateDatasBySQL(String selectSQL,
			String[] selectionArgs) {
		synchronized (MusicDBHelper.LOCK) {
			// TODO Never used should be deleted
			boolean isSuccess = true;
			SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDB();
			sqLiteDatabase.beginTransaction();
			int count = sqLiteDatabase.delete(TableStructure.RelateList.TABLE_NAME,
					selectSQL, selectionArgs);
			if (count <= 0) {
				isSuccess = false;
			} else {
				sqLiteDatabase.setTransactionSuccessful();
			}
			sqLiteDatabase.endTransaction();
			if (sqLiteDatabase != null && sqLiteDatabase.isOpen())
				sqLiteDatabase.close();
			return isSuccess;
		}
	}

	@Override
	public List<MusicInfo> selectMusicDatasBySongListID(long songListID) {
		synchronized (MusicDBHelper.LOCK) {
			SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDB();

			/*
			 * SELECT m.* FROM ( SELECT * FROM relate_list WHERE "_listID" = 2 )
			 * AS r LEFT JOIN music AS m ON m."_id" = r."_songID" ORDER BY
			 * r."_dataAdded" DESC;
			 */
			String selectSQL = "SELECT m.* FROM ( SELECT * FROM "
					+ TableStructure.RelateList.TABLE_NAME + " WHERE "
					+ TableStructure.RelateList.RELATE_LIST_LIST_ID
					+ " = ? ) AS r LEFT JOIN " + TableStructure.Music.TABLE_NAME
					+ " AS m  ON m." + TableStructure.Music.MUSIC_ID + " = r."
					+ TableStructure.RelateList.RELATE_LIST_SONG_ID + " ORDER BY r."
					+ TableStructure.RelateList.RELATE_LIST_DATA_ADDED + " DESC";
			String[] selectionArgs = new String[] { String.valueOf(songListID) };
			Cursor cursor = sqLiteDatabase.rawQuery(selectSQL, selectionArgs);
			int fileCount = cursor.getCount();
			if (fileCount <= 0) {
				cursor.close();
				if (sqLiteDatabase != null && sqLiteDatabase.isOpen())
					sqLiteDatabase.close();
				return null;
			}
			List<MusicInfo> musicInfos = new ArrayList<MusicInfo>();
			while (cursor.moveToNext()) {
				long id = cursor.getLong(cursor
						.getColumnIndex(TableStructure.Music.MUSIC_ID));
				long systemID = cursor.getLong(cursor
						.getColumnIndex(TableStructure.Music.MUSIC_SYSTEM_ID));
				String localUrl = cursor.getString(cursor
						.getColumnIndex(TableStructure.Music.MUSIC_LOCAL_URL));
				String folderUrl = cursor.getString(cursor
						.getColumnIndex(TableStructure.Music.MUSIC_FOLDER_URL));
				String displayName = cursor.getString(cursor
						.getColumnIndex(TableStructure.Music.MUSIC_DISPLAY_NAME));
				String title = cursor.getString(cursor
						.getColumnIndex(TableStructure.Music.MUSIC_TITLE));
				long duration = cursor.getLong(cursor
						.getColumnIndex(TableStructure.Music.MUSIC_DURATION));
				int size = cursor.getInt(cursor
						.getColumnIndex(TableStructure.Music.MUSIC_SIZE));
				long codeRate = cursor.getLong(cursor
						.getColumnIndex(TableStructure.Music.MUSIC_CODE_RATE));
				long artistID = cursor.getLong(cursor
						.getColumnIndex(TableStructure.Music.MUSIC_ARTIST_ID));
				String artist = cursor.getString(cursor
						.getColumnIndex(TableStructure.Music.MUSIC_ARTIST));
				String artistSortKey = cursor.getString(cursor
						.getColumnIndex(TableStructure.Music.MUSIC_ARTIST_SORT_KEY));
				long albumID = cursor.getLong(cursor
						.getColumnIndex(TableStructure.Music.MUSIC_ALBUM_ID));
				String album = cursor.getString(cursor
						.getColumnIndex(TableStructure.Music.MUSIC_ALBUM));
				String albumSortKey = cursor.getString(cursor
						.getColumnIndex(TableStructure.Music.MUSIC_ALBUM_SORT_KEY));
				String lrcUrl = cursor.getString(cursor
						.getColumnIndex(TableStructure.Music.MUSIC_LRC_URL));
				String imageUrl = cursor.getString(cursor
						.getColumnIndex(TableStructure.Music.MUSIC_IMAGE_URL));
				long dateAddedFav = cursor.getLong(cursor
						.getColumnIndex(TableStructure.Music.MUSIC_DATE_ADDED_FAV));
				long dateAdded = cursor.getLong(cursor
						.getColumnIndex(TableStructure.Music.MUSIC_DATE_ADDED));
				long dateModified = cursor.getLong(cursor
						.getColumnIndex(TableStructure.Music.MUSIC_DATE_MODIFIED));
				long gid = cursor.getLong(cursor
						.getColumnIndex(TableStructure.Music.MUSIC_GID));
				String fileID = cursor.getString(cursor
						.getColumnIndex(TableStructure.Music.MUSIC_FILE_ID));
				MusicInfo musicInfo = new MusicInfo(id, systemID, localUrl,
						folderUrl, displayName, title, duration, size,
						codeRate, artistID, artist, artistSortKey, albumID,
						album, albumSortKey, lrcUrl, imageUrl, dateAddedFav,
						dateAddedFav, dateModified, gid, fileID);
				musicInfos.add(musicInfo);
			}
			cursor.close();
			if (sqLiteDatabase != null && sqLiteDatabase.isOpen())
				sqLiteDatabase.close();
			return musicInfos;
		}
	}
	
	@Override
	public int selectMusicDatasCountBySongListID(String songListID) {
		synchronized (MusicDBHelper.LOCK) {
			Integer count = null;
			SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDB();

			String selectSQL = "SELECT count(0) FROM " + TableStructure.RelateList.TABLE_NAME
					+ " WHERE "	+ TableStructure.RelateList.RELATE_LIST_LIST_ID + " = ?";
			String[] selectionArgs = new String[] { songListID };
			Cursor cursor = sqLiteDatabase.rawQuery(selectSQL, selectionArgs);
			if(cursor != null){
				int fileCount = cursor.getCount();
				if (fileCount <= 0) {
					cursor.close();
					if (sqLiteDatabase != null && sqLiteDatabase.isOpen())
						sqLiteDatabase.close();
					return 0;
				}
				if(cursor.moveToFirst()){
					count = cursor.getInt(0);
				}
				cursor.close();
				if (sqLiteDatabase != null && sqLiteDatabase.isOpen())
					sqLiteDatabase.close();
			}
			return count == null ? 0 : count;
		}
	}

	@Override
	public List<SongListInfo> selectSongListDatasByMusicID(String songID) {
		synchronized (MusicDBHelper.LOCK) {
			// TODO Never used should be deleted
			SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDB();

			/*
			 * SELECT s.* FROM songlist AS s LEFT JOIN relate_list AS r ON
			 * s."_id" = r."_listID" WHERE r."_songID" = 1 ORDER BY
			 * s."_dataCreated";
			 */
			String selectSQL = "SELECT s.* FROM "
					+ TableStructure.SongList.TABLE_NAME + " AS s " + " LEFT JOIN "
					+ TableStructure.RelateList.TABLE_NAME + " AS r ON s."
					+ TableStructure.SongList.SONGLIST_ID + " = r."
					+ TableStructure.RelateList.RELATE_LIST_LIST_ID + " WHERE r."
					+ TableStructure.RelateList.RELATE_LIST_SONG_ID + " = ? ORDER BY s."
					+ TableStructure.SongList.SONGLIST_DATA_CREATED;
			String[] selectionArgs = new String[] { songID };
			Cursor cursor = sqLiteDatabase.rawQuery(selectSQL, selectionArgs);
			int count = cursor.getCount();
			if (count <= 0) {
				cursor.close();
				if (sqLiteDatabase != null && sqLiteDatabase.isOpen())
					sqLiteDatabase.close();
				return null;
			}
			List<SongListInfo> songListInfos = new ArrayList<SongListInfo>();
			while (cursor.moveToNext()) {
				long id = cursor.getLong(cursor
						.getColumnIndex(TableStructure.SongList.SONGLIST_ID));
				String name = cursor.getString(cursor
						.getColumnIndex(TableStructure.SongList.SONGLIST_NAME));
				long dataCreated = cursor.getLong(cursor
						.getColumnIndex(TableStructure.SongList.SONGLIST_DATA_CREATED));
				SongListInfo songListInfo = new SongListInfo(id, name,
						dataCreated);
				songListInfos.add(songListInfo);
			}
			cursor.close();
			if (sqLiteDatabase != null && sqLiteDatabase.isOpen())
				sqLiteDatabase.close();
			return songListInfos;
		}
	}

	@Override
	public boolean deleteAllRelateDatas(List<RelateInfo> relateInfos) {
		synchronized (MusicDBHelper.LOCK) {
			boolean isSuccess = false;
			SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDB();
			sqLiteDatabase.beginTransaction();
			for (RelateInfo relateInfo : relateInfos) {
				int count = sqLiteDatabase.delete(
						TableStructure.RelateList.TABLE_NAME,
						TableStructure.RelateList.RELATE_LIST_LIST_ID + " = ?  AND "
								+ TableStructure.RelateList.RELATE_LIST_SONG_ID + " = ? ",
						new String[] { String.valueOf(relateInfo.getListID()),
								String.valueOf(relateInfo.getSongID()) });
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
}
