package com.tiantiankuyin.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DownloadDBHelper extends SQLiteOpenHelper {
	
	public final static byte[] LOCK = new byte[0];
	/** 数据库名 */
	private final static String DATABASE_NAME = "DOWNLOAD_DB";   
	/** 当前数据库版本 */
	private final static int DATABASE_VERSION = 4;  

	/** 表名 */
	public static final String TABLE_NAME = "DOWNLOADFILE_TABLE";

	/** 表中字段:文件ID */
	public static final String _FILEID = "_fileID";

	/** gid */
	public static final String _GID = "_gid";

	/** 表中字段:文件ID */
	public static final String _CREATETIME = "_createTime";

	/** 表中字段:文件任务地址 */
	public static final String _URL = "_url";

	/** 表中字段:文件名 */
	public static final String _FILENAME = "_fileName";

	/** 表中字段:文件名 */
	public static final String _TEMP_FILE_NAME = "_tempFileName";

	/** 表中字段:后缀 */
	public static final String _SUFFIX = "_suffix";

	/** 表中字段:文件任务状态 */
	public static final String _STATE = "_state";

	/** 表中字段:文件总大小 */
	public static final String _TOTALSIZE = "_totalSize";

	/** 表中字段:文件已任务大小 */
	public static final String _CURRSIZE = "_currSize";

	/** 表中字段:文件类型 */
	public static final String _FILETYPE = "_fileType";

	public static final String _SINGERNAME = "_singerName";

	public static final String _SONGNAME = "_songName";

	public DownloadDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		String create_table = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
				+ _FILEID + " Text PRIMARY KEY, " + _GID + " Text, " + _CREATETIME + " Text, "
				+ _URL + " Text, " + _FILENAME + " Text," + _TEMP_FILE_NAME + " Text, "
				+ _SUFFIX + " Text, " + _STATE + " Text,"
				+ _TOTALSIZE + " Text," + _SINGERNAME + " Text, " + _SONGNAME + " Text, " + _CURRSIZE
				+ " Text, " + _FILETYPE + " Text )";
		db.execSQL(create_table);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);// 清除旧有的数据库任务表。

		String create_table = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
				+ _FILEID + " Text PRIMARY KEY, " + _GID + " Text, "
				+ _CREATETIME + " Text, " + _URL + " Text, " + _FILENAME
				+ " Text," + _TEMP_FILE_NAME + " Text, " + _SUFFIX + " Text, "
				+ _STATE + " Text," + _TOTALSIZE + " Text," + _SINGERNAME
				+ " Text, " + _SONGNAME + " Text, " + _CURRSIZE + " Text, "
				+ _FILETYPE + " Text )";
		db.execSQL(create_table); // 重新建表。
	}

	/**
	 * 创建表
	 * 
	 * @param isForceCreate
	 *            表存在时是否删除后新建
	 */
	public void createTable(boolean isForceCreate) {
		if (isForceCreate) {
			getDatabase().execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		}
		String create_table = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
				+ _FILEID + " Text PRIMARY KEY, " + _GID + " Text, " + _CREATETIME + " Text, "
				+ _URL + " Text, " + _FILENAME + " Text," + _TEMP_FILE_NAME + " Text, "
				+ _SUFFIX + " Text, " + _STATE + " Text,"
				+ _TOTALSIZE + " Text," + _SINGERNAME + " Text, " + _SONGNAME + " Text, " + _CURRSIZE
				+ " Text, " + _FILETYPE + " Text )";
		getDatabase().execSQL(create_table);
	}

	/**
	 * 获取数据库对象
	 * 
	 * @return
	 */
	public SQLiteDatabase getDatabase() {
		return getWritableDatabase();
	}

	/**
	 * 释放数据库连接
	 */
	public void closeDatabase() {
		close();
	}
}
