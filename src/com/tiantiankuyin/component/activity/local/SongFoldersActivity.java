package com.tiantiankuyin.component.activity.local;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.tiantiankuyin.adapter.FolderAdatper;
import com.tiantiankuyin.bean.FolderVO;
import com.tiantiankuyin.component.activity.BaseActivity;
import com.tiantiankuyin.component.activity.MusicListActivity;
import com.tiantiankuyin.database.bll.LocalMusicManager;
import com.tiantiankuyin.database.bll.OnDataPreparedListener;
import com.tiantiankuyin.database.bll.SqlString;
import com.tiantiankuyin.para.IntentAction;
import com.tiantiankuyin.utils.CommonUtils;
import com.tiantiankuyin.utils.Lg;
import com.tiantiankuyin.R;
import com.tiantiankuyin.TianlApp;

/**
 * 本地模块-文件夹列表页
 * 
 * @author DK
 * 
 */
public class SongFoldersActivity extends Activity {
	private ListView listView;
	private ImageButton back_btn;
	/** 所有文件夹 */
	private List<FolderVO> folderVOs = new ArrayList<FolderVO>(); 
	private FolderAdatper adapter;
	public static SongFoldersActivity instance;
	
	public FolderAdatper getAdapter() {
		return adapter;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance=this;
		View rootView = LayoutInflater.from(this).inflate(
				R.layout.local_song_folders, null);
		setContentView(rootView);
		CommonUtils.setTitle(rootView, getString(R.string.folder), true, false);

		initFolderDatas();// 初始化文件夹数据
	}

	/**
	 * 初始化文件夹数据
	 */
	private void initFolderDatas() {
		folderVOs.clear();// 清理先前的数据
		LocalMusicManager.getInstence().getFolderList(this,
				new OnDataPreparedListener<List<FolderVO>>() {
					@Override
					public void onDataPrepared(List<FolderVO> data) {
						if (data != null) {
							refreshList(data);
						} else {
							Lg.d("getFolderDatas() == null");
						}
					}
				});		
	}

	/** 读取数据库后，刷新文件夹列表 
	 * @param _data List<FolderVO> 获取到的文件夹列表对象
	 * @return null
	 * */
	private void refreshList(List<FolderVO> _data){	
		try {
			folderVOs = _data;
			if (folderVOs != null && folderVOs.size() > 0) {
				List<FolderVO> chinFolderVOs = new ArrayList<FolderVO>();// 中文名称的文件夹
				List<FolderVO> otherFolderVOs = new ArrayList<FolderVO>();// 其他名称的文件夹
				for (FolderVO folderVO : folderVOs) {// 判断首字母是否是正文 正文排前面
					if (CommonUtils.isChinaLetter(folderVO.getFolderName())) {
						chinFolderVOs.add(folderVO);// 加入到中文集合中
					} else {
						otherFolderVOs.add(folderVO);// 加入到其他集合中
					}
				}
				folderVOs.clear();// 清理先前的数据
				folderVOs.addAll(chinFolderVOs);// 排序
				folderVOs.addAll(otherFolderVOs);// 排序
			}
			
			init();	
		} catch (Exception e) {
//			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * 初始化view
	 */
	private void init() {
		listView = (ListView) findViewById(R.id.listView);
		back_btn = (ImageButton) findViewById(R.id.back_btn);
		back_btn.setOnClickListener(listener);
		adapter = new FolderAdatper(this, folderVOs);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(itemClickListener);// 点击事件
	}

	/**
	 * 点击列表播放歌曲
	 */
	private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (folderVOs != null && folderVOs.size() > position) {// 非空判断
																	// monkey测试这个点崩溃
																	// 数组越界
				Intent intent = new Intent();
				intent.setAction(IntentAction.INTENT_MUSCI_LIST_ACTIVITY);
				intent.putExtra(BaseActivity.INTENT_ACTIVITY_NAME,
						"MusicListActivity");
				if (TianlApp.activityBundles != null) {
					Bundle bundle = new Bundle();
					String path = folderVOs.get(position).getFolderFullPath();
					TianlApp.currentPlayPath=	TianlApp.currentPlayPath+"/"+path.replaceAll("/", "_");
					bundle.putString(MusicListActivity.KEY_TITLE, folderVOs.get(position).getFolderName());
					String selectSQL = SqlString.getSqlForSelectMusicByFolderUrl();
					String[] selectionArgs = new String[] { path };
					bundle.putString(MusicListActivity.KEY_SQL, selectSQL);
					bundle.putStringArray(MusicListActivity.KEY_ARGS,
							selectionArgs);
					bundle.putString(MusicListActivity.KEY_BACK_ACTION,
							IntentAction.INTENT_LOCAL_SONG_FOLDERS_ACTIVITY);
					bundle.putString(MusicListActivity.KEY_ACTIVITY_NAME,
							"SongFoldersActivity");
					bundle.putBoolean(MusicListActivity.KEY_BACK_BTN, true);
					bundle.putBoolean(MusicListActivity.KEY_EDIT_BTN, true);
					TianlApp.activityBundles.put(
							IntentAction.INTENT_MUSCI_LIST_ACTIVITY, bundle);
				}
				int level = TianlApp.newInstance().getPageLevel();
				BaseActivity.newInstance().showActivity(intent, level + 1);
			}
		}
	};
	private View.OnClickListener listener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			backToMainView();
		}
	};

	@Override
	public void onBackPressed() {
		backToMainView();
	}

	/**
	 * 返回到首页的方法
	 */
	private void backToMainView() {
		Intent intent = new Intent(IntentAction.INTENT_LOCAL_ACTIVITY);
		intent.putExtra(BaseActivity.INTENT_ACTIVITY_NAME, "LocalActivity");
		int level = TianlApp.newInstance().getPageLevel();
		BaseActivity.newInstance().showActivity(intent,
				level - 1 == 0 ? 1 : level - 1);
		;
	}
	
	@Override
	public void onResume() {
	    super.onResume();
		//友盟统计 日志 add by perry 2012-10-23
	   // MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
	    super.onPause();
		//友盟统计 日志 add by perry 2012-10-23
	   // MobclickAgent.onPause(this);
	}
}
