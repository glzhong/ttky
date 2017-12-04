package com.tiantiankuyin.database.dao.impl;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tiantiankuyin.component.service.DownloadFile;
import com.tiantiankuyin.component.service.DownloadFile.DownloadType;
import com.tiantiankuyin.database.DownloadDBHelper;
import com.tiantiankuyin.database.dao.IDownloadDao;
import com.tiantiankuyin.download.IDownloadFileListener.DownloadState;

public class DownloadDaoImpl implements IDownloadDao {

	private DownloadDBHelper dbHelper; // 任务数据库

	public DownloadDaoImpl(Context context) {
		dbHelper = new DownloadDBHelper(context);
	}

	@Override
	public boolean insertDownloadData(DownloadFile file) {
		synchronized (DownloadDBHelper.LOCK) {

			dbHelper.createTable(false);
			boolean isSuccess = false;
			if (file == null)
				return false;
			SQLiteDatabase sqliteDatabase = dbHelper.getDatabase();
			ContentValues values = new ContentValues();
			values.put(DownloadDBHelper._FILEID, file.getFileID());
			values.put(DownloadDBHelper._URL, file.getUrl());
			values.put(DownloadDBHelper._FILENAME, file.getFileName());
			values.put(DownloadDBHelper._STATE, file.getState().toString());
			values.put(DownloadDBHelper._TOTALSIZE, file.getFileTotalSize());
			values.put(DownloadDBHelper._CURRSIZE, file.getFileCurrentSize());
			values.put(DownloadDBHelper._FILETYPE, file.getFileType()
					.toString());
			values.put(DownloadDBHelper._CREATETIME, file.getCreateTime() + "");
			if (file.getGid() != null)
				values.put(DownloadDBHelper._GID, file.getGid().toString());
			values.put(DownloadDBHelper._SINGERNAME, file.getSingerName());
			values.put(DownloadDBHelper._SONGNAME, file.getSongName());
			values.put(DownloadDBHelper._SUFFIX, file.getSuffix());
			long i = sqliteDatabase.insert(DownloadDBHelper.TABLE_NAME, null,
					values);
			if (i != -1)
				isSuccess = true;
			if (sqliteDatabase != null && sqliteDatabase.isOpen())
				sqliteDatabase.close();
			return isSuccess;
		}
	}

	@Override
	public  boolean deleteDownloadData(String fileID) {
		synchronized (DownloadDBHelper.LOCK) {
			dbHelper.createTable(false);
			boolean isSuccess = false;
			if (fileID == null || fileID.length() <= 0)
				return false;
			SQLiteDatabase sqliteDatabase = dbHelper.getDatabase();
			int count = sqliteDatabase.delete(DownloadDBHelper.TABLE_NAME,
					DownloadDBHelper._FILEID + " = ?", new String[] { fileID });
			if (count > 0)
				isSuccess = true;
			if (sqliteDatabase != null && sqliteDatabase.isOpen())
				sqliteDatabase.close();
			return isSuccess;
		}
	}

	@Override
	public  boolean updateDownloadData(DownloadFile file) {
		synchronized (DownloadDBHelper.LOCK) {
			dbHelper.createTable(false);
			boolean isSuccess = false;
			String fileID = file.getFileID();
			if (fileID == null || fileID.length() <= 0)
				return false;
			SQLiteDatabase sqliteDatabase = dbHelper.getDatabase();
			ContentValues values = new ContentValues();
			values.put(DownloadDBHelper._FILEID, file.getFileID());
			values.put(DownloadDBHelper._URL, file.getUrl());
			values.put(DownloadDBHelper._FILENAME, file.getFileName());
			values.put(DownloadDBHelper._STATE, file.getState().toString());
			values.put(DownloadDBHelper._TOTALSIZE, file.getFileTotalSize());
			values.put(DownloadDBHelper._CURRSIZE, file.getFileCurrentSize());
			values.put(DownloadDBHelper._FILETYPE, file.getFileType()
					.toString());
			values.put(DownloadDBHelper._GID, file.getGid().toString());
			values.put(DownloadDBHelper._SINGERNAME, file.getSingerName());
			values.put(DownloadDBHelper._SONGNAME, file.getSongName());
			values.put(DownloadDBHelper._CREATETIME, file.getCreateTime() + "");
			values.put(DownloadDBHelper._SUFFIX, file.getSuffix());
			int count = sqliteDatabase.update(DownloadDBHelper.TABLE_NAME,
					values, DownloadDBHelper._FILEID + " = ? ",
					new String[] { fileID });
			if (count > 0)
				isSuccess = true;
			if (sqliteDatabase != null && sqliteDatabase.isOpen())
				sqliteDatabase.close();
			return isSuccess;
		}
	}

	@Override
	public  Set<DownloadFile> selectDownloadDatas() {
		// TODO Never used should be deleted
		synchronized (DownloadDBHelper.LOCK) {
			dbHelper.createTable(false);
			String selectSQL = "SELECT * FROM " + DownloadDBHelper.TABLE_NAME;
			Cursor cursor = null;
			SQLiteDatabase sqliteDatabase = null;
			try {
				sqliteDatabase = dbHelper.getDatabase();
				cursor = sqliteDatabase.rawQuery(selectSQL, null);
				int fileCount = cursor.getCount();
				if (fileCount <= 0) {
					cursor.close();
					return null;
				}
				Set<DownloadFile> downloadFiles = new TreeSet<DownloadFile>();
				while (cursor.moveToNext()) {
					String fileID = cursor.getString(cursor
							.getColumnIndex(DownloadDBHelper._FILEID));
					String url = cursor.getString(cursor
							.getColumnIndex(DownloadDBHelper._URL));
					String fileName = cursor.getString(cursor
							.getColumnIndex(DownloadDBHelper._FILENAME));
					String singerName = cursor.getString(cursor
							.getColumnIndex(DownloadDBHelper._SINGERNAME));
					String songName = cursor.getString(cursor
							.getColumnIndex(DownloadDBHelper._SONGNAME));
					String gid = cursor.getString(cursor
							.getColumnIndex(DownloadDBHelper._GID));
					String suffix = cursor.getString(cursor
							.getColumnIndex(DownloadDBHelper._SUFFIX));
					long createTime = cursor.getLong(cursor
							.getColumnIndex(DownloadDBHelper._CREATETIME));
					DownloadState state = DownloadState.valueOf(cursor
							.getString(cursor
									.getColumnIndex(DownloadDBHelper._STATE)));
					long totalSize = cursor.getLong(cursor
							.getColumnIndex(DownloadDBHelper._TOTALSIZE));
					long currentSize = cursor.getLong(cursor
							.getColumnIndex(DownloadDBHelper._CURRSIZE));
					DownloadType type = DownloadType
							.valueOf(cursor.getString(cursor
									.getColumnIndex(DownloadDBHelper._FILETYPE)));
					DownloadFile file = new DownloadFile(fileID, gid, url,
							fileName, songName, singerName);
					file.setState(state);
					file.setFileTotalSize(totalSize);
					file.setCreateTime(createTime);
					file.setFileCurrentSize(currentSize);
					file.setFileType(type);
					file.setSuffix(suffix);
					downloadFiles.add(file);
				}
				return downloadFiles;
			} catch (Exception e) {
//				e.printStackTrace();
			} finally {
				if (cursor != null)
					cursor.close();
				if (sqliteDatabase != null)
					sqliteDatabase.close();
			}
			return null;
		}
	}

	public  boolean isDownloadFileExist(String fileID) {
		synchronized (DownloadDBHelper.LOCK) {
			if (fileID == null)
				return false;
			dbHelper.createTable(false);
			SQLiteDatabase sqliteDatabase = dbHelper.getDatabase();
			Cursor cursor = sqliteDatabase.query(DownloadDBHelper.TABLE_NAME,
					null, DownloadDBHelper._FILEID + " = ?",
					new String[] { fileID }, null, null, null);
			boolean exist = cursor.moveToFirst();
			cursor.close();
			if (sqliteDatabase != null && sqliteDatabase.isOpen())
				sqliteDatabase.close();
			return exist;
		}
	}

	@Override
	public  Set<DownloadFile> selectDownloadDatasBySQL(
			String selectSQL, String[] selectionArgs) {
		synchronized (DownloadDBHelper.LOCK) {
			if (selectionArgs == null)
				return null;
			Cursor cursor = null;
			SQLiteDatabase sqliteDatabase = null;
			try {
				sqliteDatabase = dbHelper.getDatabase();
				cursor = sqliteDatabase.rawQuery(selectSQL, selectionArgs);
				int fileCount = cursor.getCount();
				if (fileCount <= 0) {
					cursor.close();
					return null;
				}
				Set<DownloadFile> downloadFiles = new TreeSet<DownloadFile>();
				while (cursor.moveToNext()) {
					String fileID = cursor.getString(cursor
							.getColumnIndex(DownloadDBHelper._FILEID));
					String url = cursor.getString(cursor
							.getColumnIndex(DownloadDBHelper._URL));
					String fileName = cursor.getString(cursor
							.getColumnIndex(DownloadDBHelper._FILENAME));
					String singerName = cursor.getString(cursor
							.getColumnIndex(DownloadDBHelper._SINGERNAME));
					long createTime = cursor.getLong(cursor
							.getColumnIndex(DownloadDBHelper._CREATETIME));
					String songName = cursor.getString(cursor
							.getColumnIndex(DownloadDBHelper._SONGNAME));
					String gid = cursor.getString(cursor
							.getColumnIndex(DownloadDBHelper._GID));
					String suffix = cursor.getString(cursor
							.getColumnIndex(DownloadDBHelper._SUFFIX));
					DownloadState state = DownloadState.valueOf(cursor
							.getString(cursor
									.getColumnIndex(DownloadDBHelper._STATE)));
					long totalSize = cursor.getLong(cursor
							.getColumnIndex(DownloadDBHelper._TOTALSIZE));
					long currentSize = cursor.getLong(cursor
							.getColumnIndex(DownloadDBHelper._CURRSIZE));
					DownloadType type = DownloadType
							.valueOf(cursor.getString(cursor
									.getColumnIndex(DownloadDBHelper._FILETYPE)));
					DownloadFile file = new DownloadFile(fileID, gid, url,
							fileName, songName, singerName);
					file.setState(state);
					file.setFileTotalSize(totalSize);
					file.setFileCurrentSize(currentSize);
					file.setFileType(type);
					file.setCreateTime(createTime);
					file.setSuffix(suffix);
					downloadFiles.add(file);
				}
				return downloadFiles;
			} catch (Exception e) {
//				e.printStackTrace();
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
	public  boolean insertAllDownloadDatas(
			Set<DownloadFile> downloadFiles, boolean dropTable) {
		synchronized (DownloadDBHelper.LOCK) {
			// TODO Never used should be deleted
			dbHelper.createTable(dropTable);
			SQLiteDatabase sqliteDatabase = dbHelper.getDatabase();
			Iterator<DownloadFile> iterator = downloadFiles.iterator();
			boolean isSuccess = false;
			sqliteDatabase.beginTransaction();
			while (iterator.hasNext()) {
				DownloadFile file = iterator.next();
				isSuccess = insertDownloadData(file);
				if (!isSuccess)
					break;
			}
			if (isSuccess) {
				sqliteDatabase.setTransactionSuccessful();
			}
			sqliteDatabase.endTransaction();
			if (sqliteDatabase != null && sqliteDatabase.isOpen())
				sqliteDatabase.close();
			return isSuccess;
		}
	}

	@Override
	public  boolean deleteAllDownloadDatas(boolean dropTable,
			String... fileIDs) {
		synchronized (DownloadDBHelper.LOCK) {
			// TODO Never used should be deleted
			if (fileIDs == null)
				return false;
			dbHelper.createTable(dropTable);
			SQLiteDatabase sqliteDatabase = dbHelper.getDatabase();
			boolean isSuccess = false;
			sqliteDatabase.beginTransaction();
			for (String fileID : fileIDs) {
				isSuccess = deleteDownloadData(fileID);
				if (!isSuccess)
					break;
			}
			if (isSuccess) {
				sqliteDatabase.setTransactionSuccessful();
			}
			sqliteDatabase.endTransaction();
			if (sqliteDatabase != null && sqliteDatabase.isOpen())
				sqliteDatabase.close();
			return isSuccess;
		}
	}

	@Override
	public  boolean deleteAllDownloadDatasBySQL(String selectSQL,
			String[] selectionArgs) {
		synchronized (DownloadDBHelper.LOCK) {
			// TODO Never used should be deleted
			if (selectionArgs == null)
				return false;
			dbHelper.createTable(false);
			SQLiteDatabase sqliteDatabase = dbHelper.getDatabase();
			sqliteDatabase.beginTransaction();
			int count = sqliteDatabase.delete(DownloadDBHelper.TABLE_NAME,
					selectSQL, selectionArgs);
			if (count <= 0) {
				return false;
			} else {
				sqliteDatabase.setTransactionSuccessful();
			}
			sqliteDatabase.endTransaction();
			if (sqliteDatabase != null && sqliteDatabase.isOpen())
				sqliteDatabase.close();
			return true;
		}
	}
}
