package com.tiantiankuyin.component.activity.local;

import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tiantiankuyin.adapter.AlbumsListViewAdapter;
import com.tiantiankuyin.bean.AlbumVO;
import com.tiantiankuyin.component.activity.BaseActivity;
import com.tiantiankuyin.database.bll.LocalMusicManager;
import com.tiantiankuyin.database.bll.OnDataPreparedListener;
import com.tiantiankuyin.para.Env;
import com.tiantiankuyin.para.IntentAction;
import com.tiantiankuyin.utils.CommonUtils;
import com.tiantiankuyin.utils.Lg;
import com.tiantiankuyin.view.EasouPinneListView;
import com.tiantiankuyin.view.EasouSideBar;
import com.tiantiankuyin.view.EasouSideBar.OnTouchingLetterChangedListener;
import com.tiantiankuyin.R;
import com.tiantiankuyin.TianlApp;

/**
 * 本地歌曲模块-专辑页
 * 
 * @author DK
 * 
 */
public class AlbumsActivity extends Activity implements
		OnTouchingLetterChangedListener, OnClickListener {

	private EasouPinneListView mListView; // 列表
	private AlbumsListViewAdapter sectionAdapter;
	private AlbumsStandardArrayAdapter mArrayAdapter;
	private List<AlbumVO> mArtistDatas; // 歌曲数据
	private AlbumsSectionListItem[] mItemsArray;
	private static Handler mHandler;
	private EasouSideBar mSideBar; // 侧边A-Z滑动控件
	private TextView mOverlay;// 首字母提示
	private OverlayThread mThread;

	public static final int ON_DATAS_READY = 0x1;
	private ImageButton backBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View rootView = LayoutInflater.from(this).inflate(
				R.layout.local_albums, null);
		setContentView(rootView);
		CommonUtils.setTitle(rootView, getString(R.string.album), true, false);
		findView();
		init();
//		System.out.println("create");
	}

	@Override
	protected void onResume() {
		super.onResume();
		//友盟统计 日志 add by perry 2012-10-23
	   // MobclickAgent.onResume(this);
//		System.out.println("resume");
	}
	
	@Override
	public void onPause() {
	    super.onPause();
		//友盟统计 日志 add by perry 2012-10-23
	    //MobclickAgent.onPause(this);
	}

	private void init() {
		mSideBar.setOnTouchingLetterChangedListener(this);
		mOverlay.setVisibility(View.INVISIBLE);
		mThread = new OverlayThread();
		backBtn.setOnClickListener(this);
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case ON_DATAS_READY:
					initDatas();
					break;
				}
			}
		};
		LocalMusicManager.getInstence().getAlbums(this,
				new OnDataPreparedListener<List<AlbumVO>>() {

					@Override
					public void onDataPrepared(List<AlbumVO> data) {
						if (data != null) {
							refreshList(data);
						} else {
							Lg.d("getAlbums() == null");
						}
					}
				});

	}

	/** 读取数据库后，刷新专辑列表 
	 * @param _data List<AlbumVO> 获取到的专辑列表对象
	 * @return null
	 * */
	private void refreshList(List<AlbumVO> _data){	
		try {
			mArtistDatas = _data;	
			Collections.sort(mArtistDatas);
			mItemsArray = new AlbumsSectionListItem[mArtistDatas.size()];
			for (int i = 0; i < mArtistDatas.size(); i++) {
				AlbumsSectionListItem item = new AlbumsSectionListItem(
						mArtistDatas.get(i), mArtistDatas.get(i)
								.getFirstLetter());
				mItemsArray[i] = item;
			}
			mHandler.sendEmptyMessage(ON_DATAS_READY);
		} catch (Exception e) {
//			e.printStackTrace();
		}
	}
	
	
	private void initDatas() {
		mArrayAdapter = new AlbumsStandardArrayAdapter(AlbumsActivity.this, -1,
				mItemsArray);
		sectionAdapter = new AlbumsListViewAdapter(this, this.mListView, mArrayAdapter);
		mListView.setAdapter(sectionAdapter);
		mListView.setOnScrollListener(sectionAdapter);
		mListView.setPinnedHeaderView(getLayoutInflater().inflate(
				R.layout.local_singers_list_item_header, mListView, false));
	}

	private void findView() {
		mListView = (EasouPinneListView) findViewById(R.id.albumsListView);
		mSideBar = (EasouSideBar) findViewById(R.id.sideBar);
		int screen_W = Env.getScreenWidth();
		if(screen_W <= 320)
			mSideBar.setVisibility(View.GONE);
		mOverlay = (TextView) findViewById(R.id.tvLetter);
		backBtn = (ImageButton) findViewById(R.id.back_btn);
	}

	@Override
	public void onBackPressed() {
		back();
	}

	private void back() {
		Intent intent = new Intent(IntentAction.INTENT_LOCAL_ACTIVITY);
		intent.putExtra(BaseActivity.INTENT_ACTIVITY_NAME, "LocalActivity");
		int level = TianlApp.newInstance().getPageLevel();
		BaseActivity.newInstance().showActivity(intent,
				level - 1 == 0 ? 1 : level - 1);
	}

	public class AlbumsSectionListItem {
		public AlbumVO albumVO;
		public String section;

		public AlbumsSectionListItem(AlbumVO albumVO, String section) {
			this.albumVO = albumVO;
			this.section = section;
		}

		public AlbumVO getAlbumVO() {
			return albumVO;
		}

		public void setAlbumVO(AlbumVO albumVO) {
			this.albumVO = albumVO;
		}
		
		
	}

	@Override
	public void onTouchingLetterChanged(String s) {
		mOverlay.setText(s);
		mOverlay.setVisibility(View.VISIBLE);
		mHandler.removeCallbacks(mThread);
		mHandler.postDelayed(mThread, 1000);
		if (alphaIndexer(s) > 0) {
			int position = alphaIndexer(s);
			mListView.setSelection(position);

		}
	}

	private class OverlayThread implements Runnable {

		public void run() {
			mOverlay.setVisibility(View.GONE);
		}
	}

	public int alphaIndexer(String s) {
		int position = 0;
		if (mArtistDatas == null)
			return 0;
		for (int i = 0; i < mArtistDatas.size(); i++) {
			if (mArtistDatas.get(i).getFirstLetter().startsWith(s)) {
				position = i;
				break;
			}
		}
		return position;
	}

	public static class AlbumsStandardArrayAdapter extends
			ArrayAdapter<AlbumsSectionListItem> {

		public AlbumsSectionListItem[] items;

		public AlbumsStandardArrayAdapter(Context context, int textViewResourceId,
				AlbumsSectionListItem[] items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}
	}

//	/**
//	 * 拦截MENU事件
//	 * 
//	 * @author Erica
//	 */
//	@Override
//	public boolean onMenuOpened(int featureId, Menu menu) {
//		BaseActivity.newInstance().menuOpen(featureId, menu);
//		return false;
//	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_btn:
			back();
			break;
		}
	}
}
