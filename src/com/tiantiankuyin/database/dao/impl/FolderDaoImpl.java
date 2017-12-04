package com.tiantiankuyin.database.dao.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tiantiankuyin.bean.FolderVO;
import com.tiantiankuyin.database.MusicDBHelper;
import com.tiantiankuyin.database.TableStructure;
import com.tiantiankuyin.database.dao.IFolderDao;

public class FolderDaoImpl implements IFolderDao {

	private MusicDBHelper dbHelper; // 音乐数据库

	public FolderDaoImpl(Context context) {
		dbHelper = new MusicDBHelper(context);
	}

	@Override
	public List<FolderVO> selectFolderDatas() {
		synchronized (MusicDBHelper.LOCK) {
			SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDB();
			String selectSQL = "SELECT " + TableStructure.Music.MUSIC_FOLDER_URL
					+ ", COUNT(*) AS numOfSong " + " FROM "
					+ TableStructure.Music.TABLE_NAME + " GROUP BY "
					+ TableStructure.Music.MUSIC_FOLDER_URL + " ORDER BY numOfSong DESC";
			String[] selectionArgs = null;
			Cursor cursor = sqLiteDatabase.rawQuery(selectSQL, selectionArgs);
			int fileCount = cursor.getCount();
			if (fileCount <= 0) {
				cursor.close();
				if (sqLiteDatabase != null && sqLiteDatabase.isOpen())
					sqLiteDatabase.close();
				return null;
			}
			List<FolderVO> folderVOs = new ArrayList<FolderVO>();
			while (cursor.moveToNext()) {
				String folderFullPath = cursor.getString(cursor
						.getColumnIndex(TableStructure.Music.MUSIC_FOLDER_URL));
				File folder = new File(folderFullPath);
				String folderPath = folder.getParent();
				String folderName = folder.getName();
				int numOfSongs = cursor.getInt(cursor
						.getColumnIndex("numOfSong"));
				FolderVO folderVO = new FolderVO(folderName, folderPath,
						folderFullPath, numOfSongs);
				folderVOs.add(folderVO);
			}
			cursor.close();
			if (sqLiteDatabase != null && sqLiteDatabase.isOpen())
				sqLiteDatabase.close();

			return folderVOs;
		}
	}

}
