package com.tiantiankuyin.component.activity.local;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.tiantiankuyin.adapter.LocaMusicUIAdapter;
import com.tiantiankuyin.bean.LocalMusicUIBean;
import com.tiantiankuyin.bean.MusicInfo;
import com.tiantiankuyin.bean.SongListInfo;
import com.tiantiankuyin.component.activity.BaseActivity;
import com.tiantiankuyin.component.activity.MusicListActivity;
import com.tiantiankuyin.database.bll.LocalMusicManager;
import com.tiantiankuyin.database.bll.SqlString;
import com.tiantiankuyin.para.Constant;
import com.tiantiankuyin.para.IntentAction;
import com.tiantiankuyin.para.SPHelper;
import com.tiantiankuyin.utils.CommonUtils;
import com.tiantiankuyin.utils.Lg;
import com.tiantiankuyin.utils.MusicOperate;
import com.tiantiankuyin.view.EasouDialog;
import com.tiantiankuyin.view.OperateListDialog;
import com.tiantiankuyin.view.OperateDialog.OprateItem;
import com.tiantiankuyin.R;
import com.tiantiankuyin.TianlApp;

/**
 * 本地歌曲模块-本地歌曲主页
 * 
 * @author DK
 * 
 */
/**
 * @author Perry
 * 
 */
public class LocalActivity extends Activity {
	/** 九宫格 */
	private GridView gridView;
	private List<LocalMusicUIBean> localMusicUIbeans = new ArrayList<LocalMusicUIBean>();// 九宫格数据
	private LocaMusicUIAdapter adapter;
	/** LocalActivity的广播。用于接受广播并刷新界面。add by perry 2012-09-29  15:17:49
	 *  */
	private LocalActivityBroadcast activityBroadcast;
	/** 重命名 删除歌单弹出框 */
	private OperateListDialog operateListDialog;
	public static LocalActivity instance;
	private List<String> foldPath = new ArrayList<String>();
	private Handler mHandler ;

	/**
	 * 获取文件夹列表，此为耗时操作
	 */
	private void getAllFoldPath() {
		foldPath.clear();
		foldPath.add(getString(R.string.all_music));
		foldPath.add(getString(R.string.artist));
		foldPath.add(getString(R.string.album));
		foldPath.add(getString(R.string.folder));
		foldPath.add(getString(R.string.fav));
		/* foldPath.add(getString(R.string.recent)); */
		List<SongListInfo> songListInfos = LocalMusicManager.getInstence()
				.getSongList();// 查询数据库查询已经创建的歌单
		if (songListInfos != null && songListInfos.size() > 0) {
			for (SongListInfo songListInfo : songListInfos) {
				foldPath.add(songListInfo.getName());
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		mHandler =new Handler();
		
		View rootView = LayoutInflater.from(this).inflate(R.layout.local, null);
		setContentView(rootView);
		CommonUtils.setTitle(rootView, getString(R.string.local_music), false,
				false);

		if (activityBroadcast == null) {
			activityBroadcast = new LocalActivityBroadcast(instance);
		}
		init();
	}

	@Override
	protected void onResume() {
		Lg.d("life", "Local onResume");
		super.onResume();
		refreshMainViewAsync();
		TianlApp.currentPlayPath = null;
		// 友盟记录日志
		//MobclickAgent.onResume(this);

		if (activityBroadcast == null) {
			activityBroadcast = new LocalActivityBroadcast(instance);
		}
		if (!activityBroadcast.isRegister()) {
			activityBroadcast.registBroadcastReceiver();
		}

	}

	@Override
	public void onPause() {
		super.onPause();
		// 友盟记录日志
		//MobclickAgent.onPause(this);

		Lg.d("life", "Local onPause");
	}

	@Override
	protected void onDestroy() {
		Lg.d("life", "Local onDestroy");
		super.onDestroy();
		// 注销当前广播接收器
		if (activityBroadcast != null && activityBroadcast.isRegister()) {
			activityBroadcast.setRegister(false);
			unregisterReceiver(activityBroadcast);
		}
	}

	@Override
	protected void onStop() {
		Lg.d("life", "Local onStop");
		super.onStop();
	}

	private int getPos() {
		String path = SPHelper.newInstance().getFoldPath();
		if (path == null) {
			return -1;
		}
		for (int i = 0; i < foldPath.size(); i++) {
			if (path.contains(foldPath.get(i))) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 初始化view
	 */
	private void init() {
		gridView = (GridView) findViewById(R.id.gridView);
		adapter = new LocaMusicUIAdapter(this, localMusicUIbeans);// 适配器
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(itemClickListener);
		gridView.setOnItemLongClickListener(itemlongClickListener);// item长按事件
	}

	/**
	 * 主界面长按事件
	 */
	private AdapterView.OnItemLongClickListener itemlongClickListener = new AdapterView.OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			// 查询数据库查询已经创建的歌单
			List<SongListInfo> songListInfos = LocalMusicManager.getInstence()
					.getSongList();// 查询数据库查询已经创建的歌单
			if (songListInfos != null && songListInfos.size() > 0) {
				for (SongListInfo songListInfo : songListInfos) {
					if (localMusicUIbeans.get(position).getName()
							.equals(songListInfo.getName())) {

						// 过滤掉本地歌曲的 “最近添加”的长按事件 add by perry 2012-09-28 14:29:37
						if (localMusicUIbeans.get(position).getName()
								.equals(Constant.RECENT_NAME)) {
							break;
						}

						songlistOperatorDialog(songListInfo);
						break;
					}
				}
			}
			return false;
		}
	};
	
	/**
	 * 歌单操作 listener
	 */
	private AdapterView.OnItemClickListener itemclick = new AdapterView.OnItemClickListener() {//
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			SongListInfo songListInfo = (SongListInfo) parent.getTag();
			if (operateListDialog != null && operateListDialog.isShowing()) {
				operateListDialog.dismiss();// 关闭 歌单操作框
			}
			if (songListInfo != null) {
				if (position == 0) {// 重命名歌单
					renameSonglist(songListInfo);
				} else {// 删除歌单
					deleteSonglist(songListInfo);
				}
			}
		}
	};

	/**
	 * 删除歌单
	 * @param songListInfo
	 */
	private void deleteSonglist(final SongListInfo songListInfo) {
		final EasouDialog.Builder eb = new EasouDialog.Builder(this);
		eb.setTitle(getString(R.string.delete_songlist));
		eb.setMessage(getString(R.string.ask_delete_songlist));
		eb.setCheckBox(false);
		eb.setEditBox(false);
		eb.setPositiveButton(getString(R.string.confirm),
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						long songListID = songListInfo.getId();
						List<MusicInfo> musicList = LocalMusicManager
								.getInstence().getMusicListBySongListID(
										songListID);

						if (musicList != null && musicList.size() > 0) {
							int[] index = new int[musicList.size()];
							for (int i = 0; i < musicList.size(); i++) {
								index[i] = i;
							}
							MusicOperate.newInstance().deleteMusicInSonglist(
									songListInfo.getId(), musicList, index,
									false);
						}
						boolean isSuccess = LocalMusicManager.getInstence()
								.deleteSongList(songListID);
						String tips = null;
						if (isSuccess) {// 删除成功
							tips = getString(R.string.delete_songlist_success);
							// 刷新屏幕
							refreshMainViewAsync();
							// PlayLogicManage.newInstance().setCurMusicListInfoAfterDelete();
						} else {
							tips = getString(R.string.delete_songlist_fail);
						}
						// 歌单名称为空提示
						Toast.makeText(LocalActivity.this, tips,
								Toast.LENGTH_SHORT).show();
						dialog.dismiss();
					}
				}).setNegativeButton(getString(R.string.dialog_cancel),
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		eb.create().show();

	}

	/**
	 * 重命名歌单
	 * @param songListInfo
	 */
	private void renameSonglist(SongListInfo songListInfo) {
		showCreateSongListDialog(songListInfo);
	}

	/**
	 * 操作歌单dialog, 先这样用系统的dailog到时候再换dialgo统一的
	 */
	private void songlistOperatorDialog(SongListInfo songListInfo) {
		List<OprateItem> list = new ArrayList<OprateItem>();
		OprateItem item_rename = new OprateItem();
		item_rename.setShowImage(getResources().getDrawable(
				R.drawable.dialog_songlist_icon_img));
		item_rename.setShowText(getString(R.string.rename_songlist));
		OprateItem item_delete = new OprateItem();
		item_delete.setShowImage(getResources().getDrawable(
				R.drawable.dialog_songlist_icon_img));
		item_delete.setShowText(getString(R.string.delete_songlist));
		list.add(item_rename);
		list.add(item_delete);
		operateListDialog = new OperateListDialog(getParent(),
				R.style.easouDialog, list, itemclick);
		operateListDialog.setTitle(songListInfo.getName());
		operateListDialog.show();
		operateListDialog.setTag(songListInfo);
	}

	private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Intent intent = new Intent();
			if (localMusicUIbeans.get(position).getName()
					.equals(getString(R.string.all_music))) {// 点击全部歌曲文件
				jumpAllMusics(intent);
				TianlApp.currentPlayPath = getString(R.string.all_music);
			} else if (localMusicUIbeans.get(position).getName()
					.equals(getString(R.string.artist))) {// 点击歌手
				intent.setAction(IntentAction.INTENT_LOCAL_SINGERS_ACTIVITY);
				intent.putExtra(BaseActivity.INTENT_ACTIVITY_NAME,
						"SingersActivity");
				TianlApp.currentPlayPath = getString(R.string.artist);
			} else if (localMusicUIbeans.get(position).getName()
					.equals(getString(R.string.album))) {// 点击专辑
				intent.setAction(IntentAction.INTENT_LOCAL_ALBUMS_ACTIVITY);
				intent.putExtra(BaseActivity.INTENT_ACTIVITY_NAME,
						"AlbumsActivity");
				TianlApp.currentPlayPath = getString(R.string.album);
			} else if (localMusicUIbeans.get(position).getName()
					.equals(getString(R.string.folder))) {// 点击文件夹
				intent.setAction(IntentAction.INTENT_LOCAL_SONG_FOLDERS_ACTIVITY);
				intent.putExtra(BaseActivity.INTENT_ACTIVITY_NAME,
						"SongFoldersActivity");
				TianlApp.currentPlayPath = getString(R.string.folder);
			} else if (localMusicUIbeans.get(position).getName()
					.equals(getString(R.string.fav))) {// 点击我的最爱
				openMyLove();
				TianlApp.currentPlayPath = getString(R.string.fav);
				return;
			} else if (localMusicUIbeans.get(position).getName()
					.equals(getString(R.string.recent))) {// 点击最近添加
				intent = songlistIntent(intent, position);
				TianlApp.currentPlayPath = getString(R.string.recent);
				return;
			} else if (localMusicUIbeans.get(position).getName()
					.equals(getString(R.string.add_songlist))) {// 点击创建歌单
				showCreateSongListDialog(null);
				TianlApp.newInstance().setPageLevel(1);
				return;
			} else {
				intent = songlistIntent(intent, position);
				return;
			}
			int level = TianlApp.newInstance().getPageLevel();
			BaseActivity.newInstance().showActivity(intent, level + 1);
		}
	};

	/**
	 * 打开我的最爱
	 */
	private void openMyLove() {
		Intent intent = new Intent();
		intent.setAction(IntentAction.INTENT_MUSCI_LIST_ACTIVITY);
		intent.putExtra(BaseActivity.INTENT_ACTIVITY_NAME, "MusicListActivity");
		if (TianlApp.activityBundles != null) {
			Bundle bundle = new Bundle();

			bundle.putString(MusicListActivity.KEY_TITLE,
					getString(R.string.fav));

			bundle.putString(MusicListActivity.KEY_SQL, SqlString.getSqlForSelectFavMusic());
			bundle.putStringArray(MusicListActivity.KEY_ARGS, null);
			bundle.putString(MusicListActivity.KEY_BACK_ACTION,
					IntentAction.INTENT_LOCAL_ACTIVITY);
			bundle.putString(MusicListActivity.KEY_ACTIVITY_NAME,
					"LocalActivity");
			bundle.putBoolean(MusicListActivity.KEY_BACK_BTN, true);
			bundle.putBoolean(MusicListActivity.KEY_EDIT_BTN, true);
			TianlApp.activityBundles.put(IntentAction.INTENT_MUSCI_LIST_ACTIVITY,
					bundle);
		}
		int level = TianlApp.newInstance().getPageLevel();
		BaseActivity.newInstance().showActivity(intent, level + 1);
	}

	/**
	 * 去全部歌曲页
	 * @param intent
	 */
	private void jumpAllMusics(Intent intent) {
		intent.setAction(IntentAction.INTENT_MUSCI_LIST_ACTIVITY);
		intent.putExtra(BaseActivity.INTENT_ACTIVITY_NAME, "MusicListActivity");
		if (TianlApp.activityBundles != null) {
			Bundle bundle = new Bundle();
			bundle.putString(MusicListActivity.KEY_TITLE,
					getString(R.string.all_music));
			bundle.putString(MusicListActivity.KEY_SQL, SqlString.getSqlForSelectAllMusicOrderByAddedDate());
			bundle.putString(MusicListActivity.KEY_BACK_ACTION,
					IntentAction.INTENT_LOCAL_ACTIVITY);
			bundle.putString(MusicListActivity.KEY_ACTIVITY_NAME,
					"LocalActivity");
			bundle.putBoolean(MusicListActivity.KEY_BACK_BTN, true);
			bundle.putBoolean(MusicListActivity.KEY_EDIT_BTN, true);
			TianlApp.activityBundles.put(IntentAction.INTENT_MUSCI_LIST_ACTIVITY,
					bundle);
		}
	}

	/**
	 * 歌单点击事件
	 * @param intent
	 * @param position
	 * @return
	 */
	private Intent songlistIntent(Intent intent, int position) {
		// 查询数据库查询已经创建的歌单
		List<SongListInfo> songListInfos = LocalMusicManager.getInstence()
				.getSongList();// 查询数据库查询已经创建的歌单
		for (SongListInfo songListInfo : songListInfos) {
			if (songListInfo.getName().equals(
					localMusicUIbeans.get(position).getName())) {// 名字相同 就是点击该歌单
				/*
				 * intent.setAction(IntentAction.INTENT_LOCAL_SONG_LIST_ACTIVITY)
				 * ; intent.putExtra(GlobalHandlerVariable.SONGLIST_ID,
				 * songListInfo.getId());
				 * intent.putExtra(GlobalHandlerVariable.SONGLIST_NAME,
				 * songListInfo.getName());
				 * intent.putExtra(BaseActivity.INTENT_ACTIVITY_NAME,
				 * "SongListActivity");
				 */
				openSonglistSubItems(songListInfo);
				TianlApp.currentPlayPath = localMusicUIbeans.get(position)
						.getName();
				break;
			}
		}
		return intent;
	}

	/**
	 * 打开歌单的 item 歌曲
	 */
	public void openSonglistSubItems(SongListInfo songListInfo) {
		Intent intent = new Intent();
		intent.setAction(IntentAction.INTENT_MUSCI_LIST_ACTIVITY);
		intent.putExtra(BaseActivity.INTENT_ACTIVITY_NAME, "MusicListActivity");
		if (TianlApp.activityBundles != null) {
			Bundle bundle = new Bundle();
			/* String path = folderVOs.get(position).getFolderFullPath(); */
			bundle.putString(MusicListActivity.KEY_TITLE,
					songListInfo.getName());

			/*
			 * SELECT m.* FROM ( SELECT * FROM relate_list WHERE "_listID" = 2 )
			 * AS r LEFT JOIN music AS m ON m."_id" = r."_songID" ORDER BY
			 * r."_dataAdded" DESC;
			 */
			String selectSQL = SqlString.getSqlForSelectMusicInSongList();

			String[] selectionArgs = new String[] { String.valueOf(songListInfo
					.getId()) };
			bundle.putString(MusicListActivity.KEY_SQL, selectSQL);
			bundle.putStringArray(MusicListActivity.KEY_ARGS, selectionArgs);//弹性
			bundle.putString(MusicListActivity.KEY_BACK_ACTION,
					IntentAction.INTENT_LOCAL_ACTIVITY);
			bundle.putString(MusicListActivity.KEY_ACTIVITY_NAME,
					"LocalActivity");
			bundle.putBoolean(MusicListActivity.KEY_BACK_BTN, true);

			bundle.putBoolean(MusicListActivity.KEY_EDIT_BTN, true);

			// 往MusicListActivity传歌单ID。若歌单ID为空，则当前页面不是歌单。 add by perry
			// 2012-09-24 19:46:46
			bundle.putLong(MusicListActivity.KEY_SONG_LIST_ID,
					songListInfo.getId());
			// 开启歌单页下的 批量操作 edit by perry 2012-09-24
			bundle.putBoolean(MusicListActivity.KEY_EDIT_BTN, true);

			TianlApp.activityBundles.put(IntentAction.INTENT_MUSCI_LIST_ACTIVITY,
					bundle);
		}
		int level = TianlApp.newInstance().getPageLevel();
		BaseActivity.newInstance().showActivity(intent, level + 1);
	}

	/**
	 * 展示最近进入的歌单（用于添加完歌曲后，进入最近进入的歌单） add by perry 2012-10-09 16:24:32
	 * @author Perry
	 */
	private void showCurrentSongList() {
		// 查询数据库查询已经创建的歌单
		List<SongListInfo> songListInfos = LocalMusicManager.getInstence()
				.getSongList();// 查询数据库查询已经创建的歌单
		for (SongListInfo songListInfo : songListInfos) {
			if (songListInfo.getId() == TianlApp.newInstance()
					.getCurrentSonglistID()) {// 名字相同 就是点击该歌单
				openSonglistSubItems(songListInfo);
				TianlApp.currentPlayPath = songListInfo.getName();
				break;
			}
		}
	}

	/**
	 * 创建歌单dialog,
	 */
	public void showCreateSongListDialog(final SongListInfo songListInfo) {
		if (songListInfo == null) {
			int count = getSonglistCount();
			if (count >= 21) {
				Toast.makeText(this, getString(R.string.songlist_count_exceed),
						Toast.LENGTH_SHORT).show();
				return;
			}
		}
		final EasouDialog.Builder eb = new EasouDialog.Builder(this);
		if (songListInfo != null) {// 重命名歌单的头
			EasouDialog.type = EasouDialog.RENAME_SONGLIST_DELETE;
			eb.setTitle(getString(R.string.rename_songlist));
		} else {// 新建歌单的头
			EasouDialog.type = -1;
			eb.setTitle(getString(R.string.add_songlist));
		}
		eb.setEditMaxLength(1000);
		eb.setEditBox(true).setCheckBox(false);
		if (songListInfo != null) {// 重命名歌单
			eb.setEditTextMessage(songListInfo.getName());
		} else {// 新创建歌单
			eb.setEditHitMessage(getString(R.string.add_songlist_hint));
		}
		eb.setPositiveButton(getString(R.string.confirm),
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String editValue = EasouDialog.getEditValue();
						EditText editText = EasouDialog.getEditText();
						if (editValue != null && editValue.trim().length() > 0) {
							createSonglist(editValue, songListInfo, eb);
						} else {
							if (songListInfo != null) {// 重命名歌单的头
								editText.setHint(getString(R.string.songlist_rename_null));
							}
							editText.setHintTextColor(Color.RED);
						}
					}
				}).setNegativeButton(getString(R.string.dialog_cancel),
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		eb.create().show();
	}

	/**
	 * 获取歌单总数
	 * @return
	 */
	private int getSonglistCount() {
		return LocalMusicManager.getInstence().countSongList();
	}

	/**
	 * 创建歌单包括一系列逻辑
	 * 
	 * @param songlistName
	 */
	private void createSonglist(String songlistName, SongListInfo listInfo,
			EasouDialog.Builder eb) {
		int resultInt = MusicOperate.newInstance().createSonglist(songlistName,
				listInfo, true);
		String show_tip = "";
		switch (resultInt) {
		case 0:
			show_tip = getString(R.string.songlist_exsit);
			break;
		case 1:
			show_tip = getString(R.string.rename_songlist_success);
			eb.dismiss();
			break;
		case 2:
			show_tip = getString(R.string.songlist_create_success);
			eb.dismiss();
			break;
		}
		// 提示操作
		Toast.makeText(LocalActivity.this, show_tip, Toast.LENGTH_SHORT).show();
		// 刷新屏幕
		refreshMainViewAsync();
	}

	/**
	 * 异步刷新界面
	 */
	private void refreshMainViewAsync() {
		new Thread() {
			public void run() {
				prepareLocalMusicUIData();
				getAllFoldPath();
				adapter.setPlayCurrentFolder(getPos());// 设置当前正在播放的文件夹
				mHandler.post(new Runnable() {
					
					@Override
					public void run() {
						adapter.notifyDataSetChanged();
					}
				});
				
			};
		}.start();
	}
	

	public int getMusicList(){		
		File file=new File(Environment.getExternalStorageDirectory()+"/NewHighMusic/music/");
		File [] files=file.listFiles();
		
	   if(files==null){
		   return 0;
	   }
		 return files.length;		 
	}

	/**
	 * 本地九宫格UI数据,此为耗时操作
	 */
	private void prepareLocalMusicUIData() {

		localMusicUIbeans.clear();// 清空数据
		LocalMusicUIBean allMusic = new LocalMusicUIBean(getResources()
				.getString(R.string.all_music),
				R.drawable.local_allmusic_icon_img, LocalMusicManager
						.getInstence().countAllMusic()+getMusicList());
		localMusicUIbeans.add(allMusic);
		// 歌手
		LocalMusicUIBean artist = new LocalMusicUIBean(getResources()
				.getString(R.string.artist), R.drawable.local_artist_icon_img,
				LocalMusicManager.getInstence().countSinger()+getMusicList());
		localMusicUIbeans.add(artist);
		// 专辑
		LocalMusicUIBean album = new LocalMusicUIBean(getResources().getString(
				R.string.album), R.drawable.local_album_icon_img,
				LocalMusicManager.getInstence().countAlbum());
		localMusicUIbeans.add(album);
		// 文件夹
		LocalMusicUIBean folder = new LocalMusicUIBean(getResources()
				.getString(R.string.folder), R.drawable.local_folder_icon_img,
				LocalMusicManager.getInstence().countFolder());
		localMusicUIbeans.add(folder);
		// 我的最爱
		LocalMusicUIBean fav = new LocalMusicUIBean(getResources().getString(
				R.string.fav), R.drawable.local_fav_icon_img, LocalMusicManager
				.getInstence().countFav()+getMusicList());
		localMusicUIbeans.add(fav);
		// 最近添加
		LocalMusicUIBean recent = new LocalMusicUIBean(getResources()
				.getString(R.string.recent), R.drawable.local_recent_icon_img,
				LocalMusicManager.getInstence().countRecentMusic()+getMusicList());
		localMusicUIbeans.add(recent);
		// 获取创建的歌单信息
		getSongListInfo();
		// 创建歌单
		LocalMusicUIBean add_songlist = new LocalMusicUIBean(getResources()
				.getString(R.string.add_songlist),
				R.drawable.local_add_songlist_icon_img, -1);
		localMusicUIbeans.add(add_songlist);
	}

	/**
	 * 获取歌单的信息显示
	 */
	private void getSongListInfo() {
		// 查询数据库查询已经创建的歌单
		List<SongListInfo> songListInfos = LocalMusicManager.getInstence()
				.getSongList();// 查询数据库查询已经创建的歌单
		if (songListInfos != null && songListInfos.size() > 0) {
			LocalMusicUIBean songlist;
			for (SongListInfo songListInfo : songListInfos) {
				// 若当前歌单SongListInfo为“最近添加”则过滤掉。 add by perry 2012-09-28
				// 14:13:18
				if (songListInfo.getName().equals(Constant.RECENT_NAME)) {
					continue;
				}
				int count = LocalMusicManager.getInstence()
						.countMusicBySongListID(
								String.valueOf(songListInfo.getId()));
				songlist = new LocalMusicUIBean(songListInfo.getName(),
						R.drawable.local_songlist_icon_img, count);
				localMusicUIbeans.add(songlist);
			}
		}
	}

	public class LocalActivityBroadcast extends BroadcastReceiver {

		private Context context;

		public LocalActivityBroadcast(Context context) {
			this.context = context;
		}

		private boolean isRegister = false;

		// 注册广播接受器
		public void registBroadcastReceiver() {
			IntentFilter filter = new IntentFilter();
			filter.addAction(IntentAction.INTENT_BROADCAST_UPDATE_LOCAL_ACTIVITY);
			filter.addAction(IntentAction.INTENT_BROADCAST_SHOW_CURRENT_SONGLIST);
			setRegister(true);
			context.registerReceiver(this, filter);
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (IntentAction.INTENT_BROADCAST_UPDATE_LOCAL_ACTIVITY
					.equals(action)) {
				refreshMainViewAsync();
			} else if (IntentAction.INTENT_BROADCAST_SHOW_CURRENT_SONGLIST
					.equals(action)) {
				int level = TianlApp.newInstance().getPageLevel();
				if ((level - 1) > 0) {
					level = level - 1;//
				}
				TianlApp.newInstance().setPageLevel(level);//
				showCurrentSongList();
			}
		}

		public boolean isRegister() {
			return isRegister;
		}

		public void setRegister(boolean isRegister) {
			this.isRegister = isRegister;
		}//

	}

	// /**
	// * 拦截MENU事件
	// *
	// * @author Erica
	// */
	// @Override
	// public boolean onMenuOpened(int featureId, Menu menu) {
	// BaseActivity.newInstance().menuOpen(featureId, menu);
	// return false;
	// }
}