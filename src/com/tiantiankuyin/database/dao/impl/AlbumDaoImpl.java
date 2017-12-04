package com.tiantiankuyin.database.dao.impl;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tiantiankuyin.bean.AlbumVO;
import com.tiantiankuyin.database.MusicDBHelper;
import com.tiantiankuyin.database.TableStructure;
import com.tiantiankuyin.database.dao.IAlbumDao;

public class AlbumDaoImpl implements IAlbumDao {

	private MusicDBHelper dbHelper; // 音乐数据库

	public AlbumDaoImpl(Context context) {
		dbHelper = new MusicDBHelper(context);
	}

	@Override
	public List<AlbumVO> selectAlbumDatas() {
		synchronized (MusicDBHelper.LOCK) {

			SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDB();
			String selectSQL = "SELECT " + TableStructure.Music.MUSIC_ALBUM + " , "
					+ TableStructure.Music.MUSIC_ALBUM_ID + " , "
					+ TableStructure.Music.MUSIC_ALBUM_SORT_KEY + " , "
					+ " COUNT(*) AS numOfSong " + " FROM "
					+ TableStructure.Music.TABLE_NAME + " GROUP BY "
					+ TableStructure.Music.MUSIC_ALBUM + " ORDER BY "
					+ TableStructure.Music.MUSIC_ALBUM_SORT_KEY;
			String[] selectionArgs = null;
			Cursor cursor = sqLiteDatabase.rawQuery(selectSQL, selectionArgs);
			int fileCount = cursor.getCount();
			if (fileCount <= 0) {
				cursor.close();
				if (sqLiteDatabase != null && sqLiteDatabase.isOpen())
					sqLiteDatabase.close();
				return null;
			}
			List<AlbumVO> albumVOs = new ArrayList<AlbumVO>();
			while (cursor.moveToNext()) {
				long albumID = cursor.getLong(cursor
						.getColumnIndex(TableStructure.Music.MUSIC_ALBUM_ID));
				String albumName = cursor.getString(cursor
						.getColumnIndex(TableStructure.Music.MUSIC_ALBUM));
				String albumSortKey = cursor.getString(cursor
						.getColumnIndex(TableStructure.Music.MUSIC_ALBUM_SORT_KEY));
				int numOfSong = cursor.getInt(cursor
						.getColumnIndex("numOfSong"));
				AlbumVO albumVO = new AlbumVO(albumID, albumName, numOfSong,
						albumSortKey);
				albumVOs.add(albumVO);
			}
			cursor.close();
			if (sqLiteDatabase != null && sqLiteDatabase.isOpen())
				sqLiteDatabase.close();
			return albumVOs;
		}
	}

}
