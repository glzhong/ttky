package com.tiantiankuyin.component.activity.local;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.tiantiankuyin.adapter.AllMusicAdapter;
import com.tiantiankuyin.bean.MusicInfo;
import com.tiantiankuyin.component.activity.BaseActivity;
import com.tiantiankuyin.database.bll.LocalMusicManager;
import com.tiantiankuyin.database.bll.OnDataPreparedListener;
import com.tiantiankuyin.para.Constant;
import com.tiantiankuyin.para.IntentAction;
import com.tiantiankuyin.utils.CommonUtils;
import com.tiantiankuyin.utils.Lg;
import com.tiantiankuyin.R;
import com.tiantiankuyin.TianlApp;

public class SongListActivity extends Activity {
	private long songlistId;
	private String songlistName;
	private ListView listView;
	private AllMusicAdapter adapter;
	private List<MusicInfo> musicInfos;

	private View rootView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		rootView=LayoutInflater.from(this).inflate(R.layout.local_song_list, null);
		setContentView(rootView);
		songlistId=getIntent().getLongExtra(Constant.SONGLIST_ID,-1);
		songlistName=getIntent().getStringExtra(Constant.SONGLIST_NAME);
		
		initData();
		init();  
		
	}
	/**
	 * 初始化view
	 */
	private void init() {
		/*title=(TextView)findViewById(R.id.title);*/
		if(songlistName!=null){
			CommonUtils.setTitle(rootView, songlistName, true, false);
			//title.setText(songlistName);
		}
		listView=(ListView)findViewById(R.id.listView);
		adapter=new AllMusicAdapter(this, musicInfos);
		listView.setAdapter(adapter);
		
	}
	/**
	 * 初始化数据
	 */
	private void initData(){
		if(songlistId!=-1){
			LocalMusicManager.getInstence().getMusicDatasBySongListID(this,
					new OnDataPreparedListener<List<MusicInfo>>() {

						@Override
						public void onDataPrepared(List<MusicInfo> data) {
							if (data != null) {
								musicInfos = data;
								adapter.notifyDataSetChanged();
							} else {
								Lg.d("getAlbums() == null");
							}
						}
					},songlistId);
		}
	}
	@Override
	public void onBackPressed() {
		backToMainView();
	}
	/**
	 * 返回到首页的方法
	 */
	private void backToMainView(){
		Intent intent = new Intent(IntentAction.INTENT_LOCAL_ACTIVITY);
		intent.putExtra(BaseActivity.INTENT_ACTIVITY_NAME, "LocalActivity");
		int level = TianlApp.newInstance().getPageLevel();
		BaseActivity.newInstance().showActivity(intent,
				level - 1 == 0 ? 1 : level - 1);
	}
	
	@Override
	public void onResume() {
	    super.onResume();
		//友盟统计 日志 add by perry 2012-10-23
	    //MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
	    super.onPause();
		//友盟统计 日志 add by perry 2012-10-23
	   // MobclickAgent.onPause(this);
	}
}
