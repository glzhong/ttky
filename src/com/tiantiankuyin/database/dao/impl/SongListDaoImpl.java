package com.tiantiankuyin.database.dao.impl;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tiantiankuyin.bean.SongListInfo;
import com.tiantiankuyin.database.MusicDBHelper;
import com.tiantiankuyin.database.TableStructure;
import com.tiantiankuyin.database.dao.ISongListDao;

public class SongListDaoImpl implements ISongListDao {

	private MusicDBHelper dbHelper; // 音乐数据库

	public SongListDaoImpl(Context context) {
		dbHelper = new MusicDBHelper(context);
	}

	@Override
	public boolean insertSongListData(String songListName) {
		synchronized (MusicDBHelper.LOCK) {
			boolean isSuccess = false;

			if (songListName == null || songListName.length() <= 0) {
				return false;
			}

			SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDB();
			ContentValues values = new ContentValues();
			values.put(TableStructure.SongList.SONGLIST_NAME, songListName);
			values.put(TableStructure.SongList.SONGLIST_DATA_CREATED,
					System.currentTimeMillis());
			long i = sqLiteDatabase.insert(TableStructure.SongList.TABLE_NAME,
					TableStructure.SongList.SONGLIST_ID, values);
			if (i != -1)
				isSuccess = true;
			if (sqLiteDatabase != null && sqLiteDatabase.isOpen())
				sqLiteDatabase.close();

			return isSuccess;
		}
	}

	@Override
	public boolean deleteSongListDatas(long songListID) {
		synchronized (MusicDBHelper.LOCK) {
			boolean isSuccess = false;
			if (songListID <= 0)
				return false;
			SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDB();
			int count = sqLiteDatabase.delete(TableStructure.SongList.TABLE_NAME,
					TableStructure.SongList.SONGLIST_ID + " = ?",
					new String[] { String.valueOf(songListID) });
			sqLiteDatabase.delete(TableStructure.RelateList.TABLE_NAME,
					TableStructure.RelateList.RELATE_LIST_LIST_ID + " = ?",
					new String[] { String.valueOf(songListID) });
			if (count > 0)
				isSuccess = true;
			if (sqLiteDatabase != null && sqLiteDatabase.isOpen())
				sqLiteDatabase.close();
			return isSuccess;
		}
	}

	@Override
	public boolean updateSongListData(SongListInfo songListInfo) {
		synchronized (MusicDBHelper.LOCK) {
			boolean isSuccess = false;
			String songListID = String.valueOf(songListInfo.getId());
			if (songListID == null || songListID.length() <= 0)
				return false;
			SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDB();
			ContentValues values = new ContentValues();
			values.put(TableStructure.SongList.SONGLIST_NAME, songListInfo.getName());
			int count = sqLiteDatabase.update(TableStructure.SongList.TABLE_NAME,
					values, TableStructure.SongList.SONGLIST_ID + " = ?",
					new String[] { songListID });
			if (count > 0)
				isSuccess = true;
			if (sqLiteDatabase != null && sqLiteDatabase.isOpen())
				sqLiteDatabase.close();
			return isSuccess;
		}
	}

	@Override
	public List<SongListInfo> selectSongListDatas() {
		synchronized (MusicDBHelper.LOCK) {
			String selectSQL = "SELECT * FROM " + TableStructure.SongList.TABLE_NAME;
			return selcetSongListDatasBySQL(selectSQL, null);
		}
	}

	@Override
	public int selectSongListDatasCount() {
		synchronized (MusicDBHelper.LOCK) {
			// TODO Never used should be deleted
			Integer count = null;
			String selectSQL = "SELECT count(0) FROM "
					+ TableStructure.SongList.TABLE_NAME;
			SQLiteDatabase sqliteDatabase = null;
			Cursor cursor = null;
			try {
				sqliteDatabase = dbHelper.getReadableDB();
				cursor = sqliteDatabase.rawQuery(selectSQL, null);
				if (cursor != null && cursor.moveToFirst()) {
					count = cursor.getInt(0);
				}
			} catch (Exception e) {
				// e.printStackTrace();
			} finally {
				if (cursor != null)
					cursor.close();
				if (sqliteDatabase != null && sqliteDatabase.isOpen())
					sqliteDatabase.close();
			}
			return count == null ? 0 : count;
		}
	}

	@Override
	public List<SongListInfo> selcetSongListDatasBySQL(String selectSQL,
			String[] selectionArgs) {
		synchronized (MusicDBHelper.LOCK) {
			SQLiteDatabase sqliteDatabase = dbHelper.getReadableDB();
			Cursor cursor = sqliteDatabase.rawQuery(selectSQL, selectionArgs);
			int count = cursor.getCount();
			if (count <= 0) {
				cursor.close();
				if (sqliteDatabase != null && sqliteDatabase.isOpen())
					sqliteDatabase.close();
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
			if (sqliteDatabase != null && sqliteDatabase.isOpen())
				sqliteDatabase.close();
			return songListInfos;
		}
	}

}
