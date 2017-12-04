package com.tiantiankuyin.database.dao.impl;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tiantiankuyin.bean.ArtistVO;
import com.tiantiankuyin.database.MusicDBHelper;
import com.tiantiankuyin.database.TableStructure;
import com.tiantiankuyin.database.dao.IArtistDao;

public class ArtistDaoImpl implements IArtistDao {

	private MusicDBHelper dbHelper; // 音乐数据库

	public ArtistDaoImpl(Context context) {
		dbHelper = new MusicDBHelper(context);
	}

	@Override
	public List<ArtistVO> selectArtistDatas() {
		synchronized (MusicDBHelper.LOCK) {
		SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDB();
		String selectSQL = "SELECT " + TableStructure.Music.MUSIC_ARTIST + " , "
				+ TableStructure.Music.MUSIC_ARTIST_ID + " , "
				+ TableStructure.Music.MUSIC_ARTIST_SORT_KEY + " , "
				+ " COUNT(*) AS numOfSong " + " FROM "
				+ TableStructure.Music.TABLE_NAME + " GROUP BY "
				+ TableStructure.Music.MUSIC_ARTIST + " ORDER BY "
				+ TableStructure.Music.MUSIC_ARTIST_SORT_KEY;
		String[] selectionArgs = null;
		Cursor cursor = sqLiteDatabase.rawQuery(selectSQL, selectionArgs);
		int fileCount = cursor.getCount();
		if (fileCount <= 0) {
			cursor.close();
			if (sqLiteDatabase != null && sqLiteDatabase.isOpen())
				sqLiteDatabase.close();
			return null;
		}
		List<ArtistVO> artistVOs = new ArrayList<ArtistVO>();
		while (cursor.moveToNext()) {
			long artistID = cursor.getLong(cursor
					.getColumnIndex(TableStructure.Music.MUSIC_ARTIST_ID));
			String artistName = cursor.getString(cursor
					.getColumnIndex(TableStructure.Music.MUSIC_ARTIST));
			String artistSortKey = cursor.getString(cursor
					.getColumnIndex(TableStructure.Music.MUSIC_ARTIST_SORT_KEY));
			int numOfSong = cursor.getInt(cursor.getColumnIndex("numOfSong"));
			ArtistVO artistVO = new ArtistVO(artistID, artistName, numOfSong,
					artistSortKey);
			artistVOs.add(artistVO);
		}
		cursor.close();
		if (sqLiteDatabase != null && sqLiteDatabase.isOpen())
			sqLiteDatabase.close();
		return artistVOs;
		}
	}
}
