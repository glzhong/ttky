package com.tiantiankuyin.component.activity.local;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.tiantiankuyin.adapter.AllMusicAdapter;
import com.tiantiankuyin.bean.MusicInfo;
import com.tiantiankuyin.component.activity.BaseActivity;
import com.tiantiankuyin.database.bll.LocalMusicManager;
import com.tiantiankuyin.database.bll.OnDataPreparedListener;
import com.tiantiankuyin.para.Constant;
import com.tiantiankuyin.para.IntentAction;
import com.tiantiankuyin.play.PlayLogicManager;
import com.tiantiankuyin.R;
import com.tiantiankuyin.TianlApp;

public class SongFolderSubActivity extends Activity {
	
	private String folderPath;
	private String selectSQL;
	private TextView folder_path;
	/** 全部音乐列表适配对象 */
	private AllMusicAdapter allMusicAdapter;
	/** 当前显示列表集合 */
	private List<MusicInfo> musicInfos;
	/** 音乐列表对象 */
	private ListView listView;
	/** 返回上一页按钮 */
	private ImageButton mBackButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.local_song_sub_folders);
		if (TianlApp.activityBundles != null) {
			folderPath = TianlApp.activityBundles.get(
					IntentAction.INTENT_LOCAL_SONG_SUB_FOLDERS_ACTIVITY)
					.getString(Constant.FOLDER_PATH);
		}

		init();
		refreshList();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 友盟统计 日志 add by perry 2012-10-23
		//MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		// 友盟统计 日志 add by perry 2012-10-23
		//MobclickAgent.onPause(this);
	}

	/**
	 * 初始化界面
	 */
	private void init() {
		musicInfos = new ArrayList<MusicInfo>();
		listView = (ListView) findViewById(R.id.listView);
		mBackButton = (ImageButton) findViewById(R.id.back_btn);
		folder_path = (TextView) findViewById(R.id.folder_path);
		if (folderPath != null) {
			folder_path.setText(folderPath);
		}
		if (musicInfos != null && musicInfos.size() > 0) {
			allMusicAdapter = new AllMusicAdapter(this, musicInfos);
			listView.setAdapter(allMusicAdapter);
			listView.setOnItemClickListener(itemClickListener);
		}
		mBackButton.setOnClickListener(listener);
	}

	/**
	 * 异步刷新列表
	 */
	private void refreshList() {
		LocalMusicManager.getInstence().getMusicListByFolderPath(folderPath,
				this, new OnDataPreparedListener<List<MusicInfo>>() {

					@Override
					public void onDataPrepared(List<MusicInfo> data) {
						musicInfos.clear();
						musicInfos.addAll(data);
						allMusicAdapter.toString();
					}

				});
	}

	private View.OnClickListener listener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			backToSongFolderView();
		}
	};
	/**
	 * 点击列表播放歌曲
	 */
	private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			try {
				PlayLogicManager.newInstance().setMusicInfoAndPlay(musicInfos,
						position, selectSQL);
			} catch (IllegalStateException e) {
				// e.printStackTrace();
			}
		}
	};

	@Override
	public void onBackPressed() {
		backToSongFolderView();
	}

	/**
	 * 返回到首页的方法
	 */
	private void backToSongFolderView() {
		Intent intent = new Intent(
				IntentAction.INTENT_LOCAL_SONG_FOLDERS_ACTIVITY);
		intent.putExtra(BaseActivity.INTENT_ACTIVITY_NAME,
				"SongFoldersActivity");
		int level = TianlApp.newInstance().getPageLevel();
		BaseActivity.newInstance().showActivity(intent,
				level - 1 == 0 ? 1 : level - 1);
	}
}
