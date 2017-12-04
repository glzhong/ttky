package com.tiantiankuyin.database.bll;

import java.util.List;
import java.util.Set;

import android.content.Context;

import com.tiantiankuyin.bean.AlbumVO;
import com.tiantiankuyin.bean.ArtistVO;
import com.tiantiankuyin.bean.FolderVO;
import com.tiantiankuyin.bean.MusicInfo;
import com.tiantiankuyin.bean.RelateInfo;
import com.tiantiankuyin.bean.SongListInfo;
import com.tiantiankuyin.component.service.DownloadFile;
import com.tiantiankuyin.database.DownloadDBHelper;
import com.tiantiankuyin.database.TableStructure;
import com.tiantiankuyin.database.dao.IAlbumDao;
import com.tiantiankuyin.database.dao.IArtistDao;
import com.tiantiankuyin.database.dao.IDownloadDao;
import com.tiantiankuyin.database.dao.IMusicDao;
import com.tiantiankuyin.database.dao.IRelateDao;
import com.tiantiankuyin.database.dao.ISongListDao;
import com.tiantiankuyin.database.dao.factory.DatabaseDaoFactory;
import com.tiantiankuyin.database.dao.impl.AlbumDaoImpl;
import com.tiantiankuyin.database.dao.impl.ArtistDaoImpl;
import com.tiantiankuyin.database.dao.impl.DownloadDaoImpl;
import com.tiantiankuyin.database.dao.impl.FolderDaoImpl;
import com.tiantiankuyin.database.dao.impl.MusicDaoImpl;
import com.tiantiankuyin.database.dao.impl.RelateDaoImpl;
import com.tiantiankuyin.database.dao.impl.SongListDaoImpl;
import com.tiantiankuyin.download.IDownloadFileListener;
import com.tiantiankuyin.para.Constant;
import com.tiantiankuyin.para.UserData;
import com.tiantiankuyin.TianlApp;

/**
 * 作用：管理本地音乐数据库，1.提供对本地音乐记录的增删改查统计 2.提供对自定义歌单的增删改查
 * 
 * @author looming
 */

public class LocalMusicManager {
	private Context mContext = TianlApp.newInstance();
	private static LocalMusicManager mLocalMusicManager = new LocalMusicManager();
	public static final int DOWNLOADING = 100;// 进行中状态值
	public static final int DOWNLOADED = 101;// 任务完成状态值

	private LocalMusicManager() {
	};

	public static LocalMusicManager getInstence() {
		return mLocalMusicManager;
	}

	/**
	 * 获取专辑列表数据，返回值可能为null
	 * @author Erica
	 * @return
	 */
	public List<AlbumVO> getAlbums() {
		try {
			IAlbumDao dao = DatabaseDaoFactory.newInstance().createDao(
					AlbumDaoImpl.class, new Object[] { mContext },
					Context.class);
			return dao.selectAlbumDatas();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 异步获取专辑列表数据，返回值可能为null
	 * @author Erica
	 * @return
	 */
	public void getAlbums(final Context context,
			final OnDataPreparedListener<List<AlbumVO>> l) {
		new Thread() {
			@Override
			public void run() {
				List<AlbumVO> data = getAlbums();
				l.callback(context, data);
			}
		}.start();
	}

	/**
	 * 获取歌手列表数据，返回值可能为null
	 * @author Erica
	 * @return
	 */
	public List<ArtistVO> getArtists() {
		try {
			IArtistDao dao = DatabaseDaoFactory.newInstance().createDao(
					ArtistDaoImpl.class, new Object[] { mContext },
					Context.class);
			return dao.selectArtistDatas();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 异步获取专辑列表数据，返回值可能为null
	 * @author Erica
	 * @return
	 */
	public void getArtists(final Context context,
			final OnDataPreparedListener<List<ArtistVO>> l) {
		new Thread() {
			@Override
			public void run() {
				List<ArtistVO> data = getArtists();
				l.callback(context, data);
			}
		}.start();
	}

	/**
	 * 获取文件夹列表数据，返回值可能为null
	 * @author Erica
	 * @return
	 */
	public List<FolderVO> getFolderList() {
		try {
			FolderDaoImpl dao = DatabaseDaoFactory.newInstance().createDao(
					FolderDaoImpl.class, new Object[] { mContext },
					Context.class);
			return dao.selectFolderDatas();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 异步获取文件夹列表数据，返回值可能为null
	 * @author Erica
	 * @return
	 */
	public void getFolderList(final Context context,
			final OnDataPreparedListener<List<FolderVO>> l) {
		new Thread() {
			@Override
			public void run() {
				List<FolderVO> data = getFolderList();
				l.callback(context, data);
			}
		}.start();
	}

	/**
	 * 获取任务列表数据，返回值可能为null
	 * @author Erica
	 * @return
	 */
	public Set<DownloadFile> getDownloadDatasByState(int _type) {
		try {
			String selectSQL = null;
			switch (_type) {
			case DOWNLOADING:
				selectSQL = "SELECT * FROM " + DownloadDBHelper.TABLE_NAME
						+ " WHERE " + DownloadDBHelper._STATE + " <> ?";
				break;
			case DOWNLOADED:
				selectSQL = "SELECT * FROM " + DownloadDBHelper.TABLE_NAME
						+ " WHERE " + DownloadDBHelper._STATE + " = ?";

				break;
			default:
				break;
			}

			IDownloadDao dao = DatabaseDaoFactory.newInstance().createDao(
					DownloadDaoImpl.class, new Object[] { mContext },
					Context.class);
			return dao
					.selectDownloadDatasBySQL(
							selectSQL,
							new String[] { IDownloadFileListener.DownloadState.STATE_DOWNCOMPLETE
									.toString() });

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取文件夹列表数据，返回值可能为null
	 * @author Erica
	 * @return
	 */
	public Set<DownloadFile> getDownloadDatasByFileId(String _fileId) {
		try {
			String selectSQL = " select * from " + DownloadDBHelper.TABLE_NAME
					+ " where " + DownloadDBHelper._FILEID + " = ?";

			IDownloadDao dao = DatabaseDaoFactory.newInstance().createDao(
					DownloadDaoImpl.class, new Object[] { mContext },
					Context.class);
			return dao.selectDownloadDatasBySQL(selectSQL,
					new String[] { _fileId });

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 添加歌曲
	 * @author Erica
	 * @param song
	 * @return
	 */
	public boolean addMusic(MusicInfo song) {
		try {
			IMusicDao dao = DatabaseDaoFactory.newInstance().createDao(
					MusicDaoImpl.class, new Object[] { mContext },
					Context.class);
			return dao.insertMusicData(song);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 异步添加歌曲
	 * @author Erica
	 * @param song
	 * @return
	 */
	public void addMusic(final MusicInfo song, final Context context,
			final OnDataPreparedListener<Boolean> l) {
		new Thread() {
			@Override
			public void run() {
				Boolean isSucc = addMusic(song);
				l.callback(context, isSucc);
			}
		}.start();

	}

	/**
	 * 删除歌曲
	 * @author Erica
	 * @param song
	 * @return
	 */
	public boolean deleteMusic(String musicID) {
		try {
			IMusicDao dao = DatabaseDaoFactory.newInstance().createDao(
					MusicDaoImpl.class, new Object[] { mContext },
					Context.class);
			return dao.deleteMusicData(musicID);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 异步删除歌曲
	 * @author Erica
	 * @param song
	 * @return
	 */
	public void deleteMusic(final String musicID, final Context context,
			final OnDataPreparedListener<Boolean> l) {
		new Thread() {
			@Override
			public void run() {
				Boolean isSucc = deleteMusic(musicID);
				l.callback(context, isSucc);
			}
		}.start();

	}

	/**
	 * 更新歌曲
	 * @author Erica
	 * @param song
	 * @return
	 */
	public boolean updateMusic(MusicInfo song) {
		try {
			IMusicDao dao = DatabaseDaoFactory.newInstance().createDao(
					MusicDaoImpl.class, new Object[] { mContext },
					Context.class);
			return dao.updateMusicData(song);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 异步更新歌曲
	 * 
	 * @param song
	 * @return
	 */
	public void updateMusic(final MusicInfo song, final Context context,
			final OnDataPreparedListener<Boolean> l) {
		new Thread() {
			@Override
			public void run() {
				Boolean isSucc = updateMusic(song);
				l.callback(context, isSucc);
			}
		}.start();

	}

	/**
	 * 批量更新歌曲
	 * @author Erica
	 * @param songs
	 * @return
	 */
	public boolean updateMusic(List<MusicInfo> songs) {
		try {
			IMusicDao dao = DatabaseDaoFactory.newInstance().createDao(
					MusicDaoImpl.class, new Object[] { mContext },
					Context.class);
			return dao.updateMusicDatas(songs);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 异步批量更新歌曲
	 * @author Erica
	 * @param songs
	 * @return
	 */
	public void updateMusic(final List<MusicInfo> songs, final Context context,
			final OnDataPreparedListener<Boolean> l) {
		new Thread() {
			@Override
			public void run() {
				Boolean isSucc = updateMusic(songs);
				l.callback(context, isSucc);
			}
		}.start();
	}

	/**
	 * 获取所有歌曲,返回值可能为null
	 * @author Erica
	 * @return
	 */
	public List<MusicInfo> getAllMusic() {
		try {
			IMusicDao dao = DatabaseDaoFactory.newInstance().createDao(
					MusicDaoImpl.class, new Object[] { mContext },
					Context.class);
			return dao.selectMusicDatas();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 异步获取所有歌曲
	 * @author Erica
	 * @return
	 */
	public void getAllMusic(final Context context,
			final OnDataPreparedListener<List<MusicInfo>> l) {
		new Thread() {
			@Override
			public void run() {
				List<MusicInfo> data = getAllMusic();
				l.callback(context, data);
			}
		}.start();
	}

	/**
	 * 批量添加歌曲
	 * @author Erica
	 * @param songs
	 * @return
	 */
	public boolean addMusic(List<MusicInfo> songs) {
		try {
			IMusicDao dao = DatabaseDaoFactory.newInstance().createDao(
					MusicDaoImpl.class, new Object[] { mContext },
					Context.class);
			return dao.insertAllMusicDatas(songs);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 异步批量添加歌曲
	 * @author Erica
	 * @param songs
	 * @return
	 */
	public void addMusic(final List<MusicInfo> songs, final Context context,
			final OnDataPreparedListener<Boolean> l) {
		new Thread() {
			@Override
			public void run() {
				Boolean isSucc = addMusic(songs);
				l.callback(context, isSucc);
			}
		}.start();
	}

	/**
	 * 批量删除歌曲
	 * @author Erica
	 * @param song
	 * @return
	 */
	public boolean deleteMusic(String[] musicIDs) {
		try {
			IMusicDao dao = DatabaseDaoFactory.newInstance().createDao(
					MusicDaoImpl.class, new Object[] { mContext },
					Context.class);
			return dao.deleteAllMusicDatas(musicIDs);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 异步批量删除歌曲
	 * @author Erica
	 * @param song
	 * @return
	 */
	public void deleteMusic(final String[] musicIDs, final Context context,
			final OnDataPreparedListener<Boolean> l) {
		new Thread() {
			@Override
			public void run() {
				Boolean isSucc = deleteMusic(musicIDs);
				l.callback(context, isSucc);
			}
		}.start();

	}

	/**
	 * 查询某歌曲是否存在
	 * @author Erica
	 * @param songs
	 * @return
	 */
	public boolean exist(String musicID) {
		try {
			IMusicDao dao = DatabaseDaoFactory.newInstance().createDao(
					MusicDaoImpl.class, new Object[] { mContext },
					Context.class);
			return dao.isMusicDataExist(musicID);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 异步查询某歌曲是否存在
	 * @author Erica
	 * @param songs
	 * @return
	 */
	public void exist(final String musicID, final Context context,
			final OnDataPreparedListener<Boolean> l) {
		new Thread() {
			@Override
			public void run() {
				Boolean isSucc = exist(musicID);
				l.callback(context, isSucc);
			}
		}.start();
	}

	/**
	 * 根据fileID查询歌曲是否存在
	 * @author Erica
	 * @param songs
	 * @return
	 */
	public boolean existMusicByFileId(String fileID) {
		try {
			IMusicDao dao = DatabaseDaoFactory.newInstance().createDao(
					MusicDaoImpl.class, new Object[] { mContext },
					Context.class);
			return dao.isNetMusicDataExist(fileID);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 异步查询某网络歌曲是否存在
	 * @author Erica
	 * @param songs
	 * @return
	 */
	public void existMusicByFileId(final String fileID, final Context context,
			final OnDataPreparedListener<Boolean> l) {
		new Thread() {
			@Override
			public void run() {
				Boolean isSucc = existMusicByFileId(fileID);
				l.callback(context, isSucc);
			}
		}.start();
	}

	/**
	 * 根据音乐文件路径获取音乐
	 * @author Erica
	 * @param musicPath
	 * @return
	 */
	public List<MusicInfo> getMusicListByMusicPath(String musicPath) {
		IMusicDao dao;
		try {
			dao = DatabaseDaoFactory.newInstance().createDao(
					MusicDaoImpl.class, new Object[] { mContext },
					Context.class);
			String selectSQL = "SELECT * FROM " + TableStructure.Music.TABLE_NAME
					+ " WHERE " + TableStructure.Music.MUSIC_LOCAL_URL + " = ?";
			String[] selectionArgs = new String[] { musicPath };
			return dao.selectMusicDatasBySQL(selectSQL, selectionArgs);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 根据音乐文件id获取音乐
	 * @author Erica
	 * @param musicPath
	 * @return
	 */
	public List<MusicInfo> getMusicListByFileID(String fileID) {
		IMusicDao dao;
		try {
			dao = DatabaseDaoFactory.newInstance().createDao(
					MusicDaoImpl.class, new Object[] { mContext },
					Context.class);
			String selectSQL = " select * from " + TableStructure.Music.TABLE_NAME
					+ " where " + TableStructure.Music.MUSIC_FILE_ID + " = ? ";
			String[] selectionArgs = new String[] { fileID };
			
			return dao.selectMusicDatasBySQL(selectSQL, selectionArgs);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	

	/**
	 * 异步根据音乐文件id获取音乐
	 * @author Erica
	 * @param musicPath
	 * @return
	 */
	public void getMusicListByFileID(final String fileID, final Context context,
			final OnDataPreparedListener<List<MusicInfo>> l) {
		new Thread() {
			@Override
			public void run() {
				List<MusicInfo> data = getMusicListByFileID(fileID);
				l.callback(context, data);
			}
		}.start();
	}

	/**
	 * 根据音乐id获取音乐
	 * @author Erica
	 * @param musicPath
	 * @return
	 */
	public List<MusicInfo> getMusicListByMusicID(long musicID) {
		IMusicDao dao;
		try {
			dao = DatabaseDaoFactory.newInstance().createDao(
					MusicDaoImpl.class, new Object[] { mContext },
					Context.class);
			String selectSQL = "SELECT * FROM " + TableStructure.Music.TABLE_NAME
					+ " WHERE " + TableStructure.Music.MUSIC_ID + " = ? ";
			String[] selectionArgs = new String[] { String.valueOf(musicID) };
			return dao.selectMusicDatasBySQL(selectSQL, selectionArgs);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 异步根据音乐id获取音乐
	 * @author Erica
	 * @param musicPath
	 * @return
	 */
	public void getMusicListByMusicID(final long musicID, final Context context,
			final OnDataPreparedListener<List<MusicInfo>> l) {
		new Thread() {
			@Override
			public void run() {
				List<MusicInfo> data = getMusicListByMusicID(musicID);
				l.callback(context, data);
			}
		}.start();
	}

	/**
	 * 根据音乐gid获取音乐
	 * @author Erica
	 * @param musicPath
	 * @return
	 */
	public List<MusicInfo> getMusicListByMusicGID(String musicGID) {
		IMusicDao dao;
		try {
			dao = DatabaseDaoFactory.newInstance().createDao(
					MusicDaoImpl.class, new Object[] { mContext },
					Context.class);
			String selectSQL = "SELECT * FROM " + TableStructure.Music.TABLE_NAME
					+ " WHERE " + TableStructure.Music.MUSIC_GID + " = ? ";
			String[] selectionArgs = new String[] { musicGID };
			return dao.selectMusicDatasBySQL(selectSQL, selectionArgs);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 获取最近添加的音乐列表
	 * @author Erica
	 * @param musicPath
	 * @return
	 */
	public List<MusicInfo> getLatestAddedMusicList(long startDatetime) {
		IMusicDao dao;
		try {
			dao = DatabaseDaoFactory.newInstance().createDao(
					MusicDaoImpl.class, new Object[] { mContext },
					Context.class);
			// 获取最近添加的 歌单列表
			String selectSQL = "SELECT * FROM " + TableStructure.Music.TABLE_NAME
					+ " WHERE " + TableStructure.Music.MUSIC_DATE_ADDED + " > ?"
					+ " ORDER BY " + TableStructure.Music.MUSIC_DATE_ADDED + " DESC";
			String[] selectionArgs = new String[] { String
					.valueOf(startDatetime) };
			return dao.selectMusicDatasBySQL(selectSQL, selectionArgs);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 根据folderPath获取音乐列表
	 * @author Erica
	 * @param musicPath
	 * @return
	 */
	public List<MusicInfo> getMusicListByFolderPath(String folderPath) {
		IMusicDao dao;
		try {
			dao = DatabaseDaoFactory.newInstance().createDao(
					MusicDaoImpl.class, new Object[] { mContext },
					Context.class);
			String selectSQL = "SELECT * FROM " + TableStructure.Music.TABLE_NAME
					+ " where " + TableStructure.Music.MUSIC_FOLDER_URL
					+ " =? order by " + TableStructure.Music.MUSIC_DATE_ADDED + " desc";
			String[] selectionArgs = new String[] { folderPath };
			return dao.selectMusicDatasBySQL(selectSQL, selectionArgs);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 异步根据folderPath获取音乐列表
	 * @author Erica
	 * @param musicPath
	 * @return
	 */
	public void getMusicListByFolderPath(final String folderPath,
			final Context context,
			final OnDataPreparedListener<List<MusicInfo>> l) {
		new Thread() {
			@Override
			public void run() {
				List<MusicInfo> data = getMusicListByFolderPath(folderPath);
				l.callback(context, data);
			}
		}.start();
	}

	/**
	 * 获取当前正在操作的音乐列表
	 * @author Erica
	 * @param musicPath
	 * @return
	 */
	public List<MusicInfo> getCurrentOperateMusicList() {
		IMusicDao dao;
		try {
			dao = DatabaseDaoFactory.newInstance().createDao(
					MusicDaoImpl.class, new Object[] { mContext },
					Context.class);

			String selectSQL = UserData.getInstence()
					.getCurrentOperateMusicListSql();
			if (selectSQL == null || selectSQL.length() == 0) {
				selectSQL = " select * from " + TableStructure.Music.TABLE_NAME
						+ " order by " + TableStructure.Music.MUSIC_DATE_ADDED + " desc";
			}

			String[] selectionArgs = new String[] {};
			return dao.selectMusicDatasBySQL(selectSQL, selectionArgs);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 获取最后一次播放的音乐列表
	 * @author Erica
	 * @param musicPath
	 * @return
	 */
	public List<MusicInfo> getCurrentPlayingMusicList() {
		IMusicDao dao;
		try {
			dao = DatabaseDaoFactory.newInstance().createDao(
					MusicDaoImpl.class, new Object[] { mContext },
					Context.class);

			String selectSQL = UserData.getInstence()
					.getCurrentPlayingMusicListSql();
			if (selectSQL == null || selectSQL.length() == 0) {
				return null;
			}

			String[] selectionArgs = new String[] {};
			return dao.selectMusicDatasBySQL(selectSQL, selectionArgs);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 通过sql语句，获取最后一次播放的音乐列表
	 * @author Erica
	 * @param musicPath
	 * @return
	 */
	public List<MusicInfo> getMusicListBySql(String selectSQL,
			String[] selectionArgs) {
		IMusicDao dao;
		try {
			dao = DatabaseDaoFactory.newInstance().createDao(
					MusicDaoImpl.class, new Object[] { mContext },
					Context.class);
			return dao.selectMusicDatasBySQL(selectSQL, selectionArgs);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 异步通过sql语句，获取最后一次播放的音乐列表
	 * @author Erica
	 * @param musicPath
	 * @return
	 */
	public void getMusicListBySql(final String selectSQL,
			final String[] selectionArgs, final Context context,
			final OnDataPreparedListener<List<MusicInfo>> l) {
		new Thread() {
			@Override
			public void run() {
				List<MusicInfo> data = getMusicListBySql(selectSQL, selectionArgs);
				l.callback(context, data);
			}
		}.start();
	}

	/**
	 * 统计全部歌曲
	 * @author Erica
	 * @return
	 */
	public int countAllMusic() {
		IMusicDao dao;
		try {
			dao = DatabaseDaoFactory.newInstance().createDao(
					MusicDaoImpl.class, new Object[] { mContext },
					Context.class);
			String selectSQL = "SELECT count(0) FROM "
					+ TableStructure.Music.TABLE_NAME;
			return dao.selectMusicDataCountBySQL(selectSQL, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 按歌手统计歌曲
	 * @author Erica
	 * @return
	 */
	public int countSinger() {
		IMusicDao dao;
		try {
			dao = DatabaseDaoFactory.newInstance().createDao(
					MusicDaoImpl.class, new Object[] { mContext },
					Context.class);
			String selectSQL = "SELECT count(0) FROM (SELECT * FROM "
					+ TableStructure.Music.TABLE_NAME + " GROUP BY "
					+ TableStructure.Music.MUSIC_ARTIST + ") AS T";
			return dao.selectMusicDataCountBySQL(selectSQL, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 按专辑统计歌曲
	 * @author Erica
	 * @return
	 */
	public int countAlbum() {
		IMusicDao dao;
		try {
			dao = DatabaseDaoFactory.newInstance().createDao(
					MusicDaoImpl.class, new Object[] { mContext },
					Context.class);
			String selectSQL = "SELECT count(0) FROM (SELECT * FROM "
					+ TableStructure.Music.TABLE_NAME + " GROUP BY "
					+ TableStructure.Music.MUSIC_ALBUM + ") AS T";
			return dao.selectMusicDataCountBySQL(selectSQL, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 按专辑统计歌曲
	 * @author Erica
	 * @return
	 */
	public int countFolder() {
		IMusicDao dao;
		try {
			dao = DatabaseDaoFactory.newInstance().createDao(
					MusicDaoImpl.class, new Object[] { mContext },
					Context.class);
			String selectSQL = "SELECT count(0) FROM (SELECT * FROM "
					+ TableStructure.Music.TABLE_NAME + " GROUP BY "
					+ TableStructure.Music.MUSIC_FOLDER_URL + ") AS T";
			return dao.selectMusicDataCountBySQL(selectSQL, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 按专辑统计歌曲
	 * @author Erica
	 * @return
	 */
	public int countFav() {
		IMusicDao dao;
		try {
			dao = DatabaseDaoFactory.newInstance().createDao(
					MusicDaoImpl.class, new Object[] { mContext },
					Context.class);
			String selectSQL = "SELECT count(0) FROM "
					+ TableStructure.Music.TABLE_NAME + " WHERE "
					+ TableStructure.Music.MUSIC_DATE_ADDED_FAV + " != 0";
			return dao.selectMusicDataCountBySQL(selectSQL, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * 统计最近添加的歌曲
	 * @author Erica
	 * @return
	 */
	public int countRecentMusic() {
		long recentListID = LocalMusicManager.getInstence()
				.getRecentSongListId();
		if (recentListID != -1) {
			return LocalMusicManager.getInstence().countMusicBySongListID(
					String.valueOf(recentListID));
		}
		return 0;
	}

	/**
	 * 更新任务列表数据
	 * @author Erica
	 * @return
	 */
	public boolean updateDownloadData(DownloadFile downloadingFile) {
		try {
			IDownloadDao dao = DatabaseDaoFactory.newInstance().createDao(
					DownloadDaoImpl.class, new Object[] { mContext },
					Context.class);
			return dao.updateDownloadData(downloadingFile);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 查询是否存在于任务列表
	 * @author Erica
	 * @return
	 */
	public boolean isDownloadFileExist(String fileId) {
		try {
			IDownloadDao dao = DatabaseDaoFactory.newInstance().createDao(
					DownloadDaoImpl.class, new Object[] { mContext },
					Context.class);
			return dao.isDownloadFileExist(fileId);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 插入数据至任务列表
	 * @author Erica
	 * @return
	 */
	public boolean addDownloadData(DownloadFile downloadingFile) {
		try {
			IDownloadDao dao = DatabaseDaoFactory.newInstance().createDao(
					DownloadDaoImpl.class, new Object[] { mContext },
					Context.class);
			return dao.insertDownloadData(downloadingFile);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 从任务列表中删除数据
	 * @author Erica
	 * @return
	 */
	public boolean deleteDownloadData(String fileId) {
		try {
			IDownloadDao dao = DatabaseDaoFactory.newInstance().createDao(
					DownloadDaoImpl.class, new Object[] { mContext },
					Context.class);
			return dao.deleteDownloadData(fileId);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 添加关联数据到关联表
	 * @author Erica
	 * @return
	 */
	public boolean addRelateData(RelateInfo relateInfo) {
		try {
			IRelateDao dao = DatabaseDaoFactory.newInstance().createDao(
					RelateDaoImpl.class, new Object[] { mContext },
					Context.class);
			return dao.insertRelateData(relateInfo);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 添加关联数据列表到关联表
	 * @author Erica
	 * @return
	 */
	public boolean addRelateDatas(List<RelateInfo> _relateInfos) {
		try {
			IRelateDao dao = DatabaseDaoFactory.newInstance().createDao(
					RelateDaoImpl.class, new Object[] { mContext },
					Context.class);
			return dao.insertRelateDatas(_relateInfos);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 从关联表中删除数据
	 * @author Erica
	 * @return
	 */
	public boolean deleteAllRelateDatas(List<RelateInfo> _relateInfos) {
		// TODO 也许需要异步处理
		try {
			IRelateDao dao = DatabaseDaoFactory.newInstance().createDao(
					RelateDaoImpl.class, new Object[] { mContext },
					Context.class);
			return dao.deleteAllRelateDatas(_relateInfos);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 获取音乐列表数据，返回值可能为null
	 * @author Erica
	 * @return
	 */
	public List<MusicInfo> getMusicListBySongListID(long _songListID) {
		try {
			IRelateDao dao = DatabaseDaoFactory.newInstance().createDao(
					RelateDaoImpl.class, new Object[] { mContext },
					Context.class);
			return dao.selectMusicDatasBySongListID(_songListID);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 异步获取音乐列表数据，返回值可能为null
	 * @author Erica
	 * @return
	 */
	public void getMusicDatasBySongListID(final Context context,
			final OnDataPreparedListener<List<MusicInfo>> l,
			final long songListID) {
		new Thread() {
			@Override
			public void run() {
				List<MusicInfo> data = getMusicListBySongListID(songListID);
				l.callback(context, data);
			}
		}.start();
	}

	/**
	 * 获取音乐列表大小数据，返回值可能为-1
	 * @author Erica
	 * @return
	 */
	public int countMusicBySongListID(String songListID) {
		try {
			IRelateDao dao = DatabaseDaoFactory.newInstance().createDao(
					RelateDaoImpl.class, new Object[] { mContext },
					Context.class);
			return dao.selectMusicDatasCountBySongListID(songListID);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * 更新音乐列表数据，返回值可能为false
	 * @author Erica
	 * @return
	 */
	public boolean updateSongList(SongListInfo songListInfo) {
		try {
			ISongListDao dao = DatabaseDaoFactory.newInstance().createDao(
					SongListDaoImpl.class, new Object[] { mContext },
					Context.class);
			return dao.updateSongListData(songListInfo);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 添加歌曲列表数据到歌单列表
	 * @author Erica
	 * @return
	 */
	public boolean addSongList(String songListName) {
		try {
			ISongListDao dao = DatabaseDaoFactory.newInstance().createDao(
					SongListDaoImpl.class, new Object[] { mContext },
					Context.class);
			return dao.insertSongListData(songListName);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 从歌单表中删除数据
	 * @author Erica
	 * @return
	 */
	public boolean deleteSongList(long songListID) {
		try {
			ISongListDao dao = DatabaseDaoFactory.newInstance()
					.createDao(SongListDaoImpl.class,
							new Object[] { mContext }, Context.class);

			return dao.deleteSongListDatas(songListID);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 获取歌单列表数据，返回值可能为rull
	 * @author Erica
	 * @return
	 */
	public List<SongListInfo> getSongList() {
		try {
			ISongListDao dao = DatabaseDaoFactory.newInstance()
					.createDao(SongListDaoImpl.class,
							new Object[] { mContext }, Context.class);

			return dao.selectSongListDatas();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 获取“最近添加”歌单ID
	 * @author Erica
	 * @return
	 */
	public long getRecentSongListId() {
		try {
			ISongListDao dao = DatabaseDaoFactory.newInstance()
					.createDao(SongListDaoImpl.class,
							new Object[] { mContext }, Context.class);

			String selectSQL = "SELECT * FROM " + TableStructure.SongList.TABLE_NAME + " where " + TableStructure.SongList.SONGLIST_NAME + "=?";
			String[] selectionArgs = new String[]{Constant.RECENT_NAME};
			List<SongListInfo> list = dao.selcetSongListDatasBySQL(selectSQL,selectionArgs);
			if(list!=null && list.size()>0){
				return list.get(0).getId();
			}
			

		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1L;
	}
	

	
	/**
	 * 统计歌单列表
	 * @author Erica
	 * @return
	 */
	public int countSongList() {
		try {
			ISongListDao dao = DatabaseDaoFactory.newInstance()
					.createDao(SongListDaoImpl.class,
							new Object[] { mContext }, Context.class);

			return dao.selectSongListDatasCount();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	

}
