package com.tiantiankuyin.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.tiantiankuyin.para.Constant;

/** 音乐数据库 
 * @author Perry
 *  */
public class MusicDBHelper extends SQLiteOpenHelper {
	/** 同步锁 */
	public final static byte[] LOCK= new byte[]{};
	/** 数据库名 */
	private final static String DATABASE_NAME = "EasouMusic.db";  
	/** 当前数据库版本 */
	private final static int DATABASE_VERSION = 4; 

	/** 创建音乐表的SQL语句 */
	public static final String DATABASES_CREATE_MUSIC = "CREATE TABLE IF NOT EXISTS "
			+ TableStructure.Music.TABLE_NAME
			+ " ( "
			+ TableStructure.Music.MUSIC_ID
			+ " integer primary key AUTOINCREMENT,"
			+ TableStructure.Music.MUSIC_SYSTEM_ID
			+ " integer,"
			+ TableStructure.Music.MUSIC_LOCAL_URL
			+ " text,"
			+ TableStructure.Music.MUSIC_FOLDER_URL
			+ " text,"
			+ TableStructure.Music.MUSIC_DISPLAY_NAME
			+ " text,"
			+ TableStructure.Music.MUSIC_SIZE
			+ " integer,"
			+ TableStructure.Music.MUSIC_TITLE
			+ " text,"
			+ TableStructure.Music.MUSIC_DURATION
			+ " integer,"
			+ TableStructure.Music.MUSIC_CODE_RATE
			+ " integer,"
			+ TableStructure.Music.MUSIC_ARTIST_ID
			+ " integer,"
			+ TableStructure.Music.MUSIC_ARTIST
			+ " text,"
			+ TableStructure.Music.MUSIC_ARTIST_SORT_KEY
			+ " text,"
			+ TableStructure.Music.MUSIC_ALBUM_ID
			+ " integer,"
			+ TableStructure.Music.MUSIC_ALBUM
			+ " text,"
			+ TableStructure.Music.MUSIC_ALBUM_SORT_KEY
			+ " text,"
			+ TableStructure.Music.MUSIC_IMAGE_URL
			+ " text,"
			+ TableStructure.Music.MUSIC_LRC_URL
			+ " text,"
			+ TableStructure.Music.MUSIC_DATE_ADDED_FAV
			+ " integer,"
			+ TableStructure.Music.MUSIC_DATE_ADDED
			+ " integer,"
			+ TableStructure.Music.MUSIC_DATE_MODIFIED
			+ " integer,"
			+ TableStructure.Music.MUSIC_GID
			+ " integer,"
			+ TableStructure.Music.MUSIC_FILE_ID
			+ " text);";

	/** 创建歌单表的SQL语句 */
	public static final String DATABASE_CREATE_SONGLIST = "CREATE TABLE IF NOT EXISTS "
			+ TableStructure.SongList.TABLE_NAME
			+ " ( "
			+ TableStructure.SongList.SONGLIST_ID
			+ " integer primary key AUTOINCREMENT,"
			+ TableStructure.SongList.SONGLIST_NAME
			+ " text," + TableStructure.SongList.SONGLIST_DATA_CREATED + " integer);";

	/** 创建歌曲与歌单关联表的SQL语句  */
	public static final String DATABASE_CREATE_RELATE_LIST = "CREATE TABLE IF NOT EXISTS "
			+ TableStructure.RelateList.TABLE_NAME
			+ " ( "
			+ TableStructure.RelateList.RELATE_LIST_ID
			+ " integer primary key AUTOINCREMENT,"
			+ TableStructure.RelateList.RELATE_LIST_LIST_ID
			+ " integer,"
			+ TableStructure.RelateList.RELATE_LIST_SONG_ID
			+ " integer,"
			+ TableStructure.RelateList.RELATE_LIST_DATA_ADDED + " integer);";

	public static MusicDBHelper dbHelper;

	public static MusicDBHelper newInstance(Context context) {
		if (dbHelper == null) {
			synchronized (MusicDBHelper.class) {
				if (dbHelper == null) {
					dbHelper = new MusicDBHelper(context);
				}
			}
		}
		return dbHelper;
	}

	/**
	 * MusicDBHelper 的构造方法。
	 * @param context
	 * @author Perry
	 */
	public MusicDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASES_CREATE_MUSIC);
		db.execSQL(DATABASE_CREATE_SONGLIST);
		db.execSQL(DATABASE_CREATE_RELATE_LIST);
		
		//插入“最近添加”的歌单
		insertRecentSongList(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//修改旧有music表为临时表
		db.execSQL("ALTER TABLE music RENAME TO __temp__music");
		//修改旧有songlist表为临时表
		db.execSQL("ALTER TABLE songlist RENAME TO __temp__songlist");
		//修改旧有realte_list表为临时表
		db.execSQL("ALTER TABLE realte_list RENAME TO __temp__relate_list");
		
		//创建新数据库 的表
		db.execSQL(DATABASES_CREATE_MUSIC);
		db.execSQL(DATABASE_CREATE_SONGLIST);
		db.execSQL(DATABASE_CREATE_RELATE_LIST);
		
		//清空新的数据库表数据
		db.execSQL("DELETE FROM " + TableStructure.Music.TABLE_NAME);
		db.execSQL("DELETE FROM " + TableStructure.SongList.TABLE_NAME);
		db.execSQL("DELETE FROM " + TableStructure.RelateList.TABLE_NAME);
		
		//把旧有数据插入到新数据库的music表
		db.execSQL("INSERT INTO "
				+ TableStructure.Music.TABLE_NAME
				+ " ( "
				+ TableStructure.Music.MUSIC_ID
				+ ", "
				+ TableStructure.Music.MUSIC_SYSTEM_ID
				+ ", "
				+ TableStructure.Music.MUSIC_LOCAL_URL
				+ ", "
				+ TableStructure.Music.MUSIC_FOLDER_URL
				+ ", "
				+ TableStructure.Music.MUSIC_DISPLAY_NAME
				+ ", "
				+ TableStructure.Music.MUSIC_SIZE
				+ ", "
				+ TableStructure.Music.MUSIC_TITLE
				+ ", "
				+ TableStructure.Music.MUSIC_DURATION
				+ ", "
				+ TableStructure.Music.MUSIC_CODE_RATE
				+ ", "
				+ TableStructure.Music.MUSIC_ARTIST_ID
				+ ", "
				+ TableStructure.Music.MUSIC_ARTIST
				+ ", "
				+ TableStructure.Music.MUSIC_ARTIST_SORT_KEY
				+ ", "
				+ TableStructure.Music.MUSIC_ALBUM_ID
				+ ", "
				+ TableStructure.Music.MUSIC_ALBUM
				+ ", "
				+ TableStructure.Music.MUSIC_ALBUM_SORT_KEY
				+ ", "
				+ TableStructure.Music.MUSIC_IMAGE_URL
				+ ", "
				+ TableStructure.Music.MUSIC_LRC_URL
				+ ", "
				+ TableStructure.Music.MUSIC_DATE_ADDED_FAV
				+ ", "
				+ TableStructure.Music.MUSIC_DATE_ADDED
				+ ", "
				+ TableStructure.Music.MUSIC_DATE_MODIFIED
				+ ", "
				+ TableStructure.Music.MUSIC_GID
				+ ", "
				+ TableStructure.Music.MUSIC_FILE_ID
				+ " ) SELECT _id, _sys_id, _data, _folder, _display_name, _size, title, duration, 0, artist_id, artist, sort_key_artist, album_id, album, sort_key_album, pic_path, lrc_path, is_fav, date_added, 0, 0, 0 FROM __temp__music");
		//按照升级数据库逻辑，更新新的music表中 添加“我的最爱”的插入时间为 旧表中的歌曲插入时间。
		db.execSQL("UPDATE music SET " + TableStructure.Music.MUSIC_DATE_ADDED_FAV + " = " + TableStructure.Music.MUSIC_DATE_ADDED + " WHERE " + TableStructure.Music.MUSIC_DATE_ADDED_FAV + " = 1");
		//把旧有的数据插入到新数据库的songlist表中
		db.execSQL("INSERT INTO "
				+ TableStructure.SongList.TABLE_NAME
				+ " ("
				+ TableStructure.SongList.SONGLIST_ID
				+ ", "
				+ TableStructure.SongList.SONGLIST_NAME
				+ ", "
				+ TableStructure.SongList.SONGLIST_DATA_CREATED
				+ ") SELECT __temp__songlist._id, __temp__songlist.name, __temp__songlist.create_time FROM __temp__songlist");
		//把旧有的数据插入到新数据库的relaite表中
		db.execSQL("INSERT INTO "
				+ TableStructure.RelateList.TABLE_NAME
				+ " ( "
				+ TableStructure.RelateList.RELATE_LIST_ID
				+ ", "
				+ TableStructure.RelateList.RELATE_LIST_LIST_ID
				+ ", "
				+ TableStructure.RelateList.RELATE_LIST_SONG_ID
				+ ", "
				+ TableStructure.RelateList.RELATE_LIST_DATA_ADDED
				+ " ) SELECT __temp__relate_list._id, __temp__relate_list._list_id, __temp__relate_list._song_id, 0 FROM __temp__relate_list");
		//按照升级数据库逻辑，更新新的relate_list表中 添加“添加到歌单”的插入时间为 旧表中的歌曲插入时间。
		db.execSQL("UPDATE " + TableStructure.RelateList.TABLE_NAME + " SET "
				+ TableStructure.RelateList.RELATE_LIST_DATA_ADDED + " = ( SELECT "
				+ TableStructure.Music.TABLE_NAME + "." + TableStructure.Music.MUSIC_DATE_ADDED + " FROM "
				+ TableStructure.Music.TABLE_NAME + " WHERE " + TableStructure.Music.TABLE_NAME + "."
				+ TableStructure.Music.MUSIC_ID + " = " + TableStructure.RelateList.TABLE_NAME + "."
				+ TableStructure.RelateList.RELATE_LIST_SONG_ID + " )");
		
		//删除以前旧有数据库的表
		db.execSQL("DROP TABLE IF EXISTS play_queue");
		db.execSQL("DROP TABLE IF EXISTS recent_queue");
		db.execSQL("DROP TABLE IF EXISTS __temp__music");
		db.execSQL("DROP TABLE IF EXISTS __temp__relate_list");
		db.execSQL("DROP TABLE IF EXISTS __temp__songlist");
		
		//插入“最近添加”的歌单
		insertRecentSongList(db);
	}

	private void insertRecentSongList(SQLiteDatabase db){
		//插入“最近添加”的歌单
		ContentValues values = new ContentValues();
		values.put(TableStructure.SongList.SONGLIST_NAME, Constant.RECENT_NAME);
		values.put(TableStructure.SongList.SONGLIST_DATA_CREATED,
				System.currentTimeMillis());
		db.insert(TableStructure.SongList.TABLE_NAME, null, values);
	}
	
	public void createTable(boolean isForceCreate) {
		if (isForceCreate) {
			getWritableDB().execSQL("DROP TABLE IF EXISTS " + TableStructure.Music.TABLE_NAME + " ;");
			getWritableDB().execSQL("DROP TABLE IF EXISTS " + TableStructure.SongList.TABLE_NAME + " ;");
			getWritableDB().execSQL("DROP TABLE IF EXISTS " + TableStructure.RelateList.TABLE_NAME + " ;");
		}
		getWritableDB().execSQL(DATABASES_CREATE_MUSIC);
		getWritableDB().execSQL(DATABASE_CREATE_SONGLIST);
		getWritableDB().execSQL(DATABASE_CREATE_RELATE_LIST);
	}
		
	/**
	 * 释放数据库连接
	 */
	public void closeDatabase() {
		close();
	}

	/**
	 * 获取数据库写对象
	 * 
	 * @return
	 */
	public SQLiteDatabase getWritableDB() {
		return getWritableDatabase();
	}

	/**
	 * 获取数据库读对象
	 * 
	 * @return
	 */
	public SQLiteDatabase getReadableDB() {
		return getReadableDatabase();
	}

}
