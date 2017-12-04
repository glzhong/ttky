package com.tiantiankuyin.component.activity.local;

import java.io.File;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tiantiankuyin.adapter.SingersListViewAdapter;
import com.tiantiankuyin.adapter.SingersListViewAdapter.StandardArrayAdapter;
import com.tiantiankuyin.bean.ArtistVO;
import com.tiantiankuyin.bean.MusicInfo;
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
 * 本地歌曲模块-歌手列表页
 * 
 * @author DK
 * 
 */
public class SingersActivity extends Activity implements
		OnTouchingLetterChangedListener, OnClickListener {
	/** 列表  */
	private EasouPinneListView mListView;  
	private SingersListViewAdapter sectionAdapter;
	private StandardArrayAdapter mArrayAdapter;
	/** 歌曲数据 */
	private List<ArtistVO> mArtistDatas;  
	private SingerSectionListItem[] mItemsArray;
	private static Handler mHandler;
	/** 侧边A-Z滑动控件 */
	private EasouSideBar mSideBar;  
	/** 首字母提示 */
	private TextView mOverlay; 
	private OverlayThread mThread;

	public static final int ON_DATAS_READY = 0x1;
	private ImageButton backBtn;
	public static SingersActivity instance;
	public SingersListViewAdapter getSectionAdapter() {
		return sectionAdapter;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance=this;
		View rootView = LayoutInflater.from(this).inflate(
				R.layout.local_singers, null);
		setContentView(rootView);
		CommonUtils.setTitle(rootView, getString(R.string.artist), true, false);
		findView();
		init();
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

		LocalMusicManager.getInstence().getArtists(this,
				new OnDataPreparedListener<List<ArtistVO>>() {
					@Override
					public void onDataPrepared(List<ArtistVO> data) {
						if (data != null) {
							refreshList(data);
						} else {
							Lg.d("getArtists() == null");
						}
					}
				});

	}

	/** 读取数据库后，刷新歌手列表 
	 * @param _data List<ArtistVO> 获取到的歌手列表对象
	 * @return null
	 * */
	private void refreshList(List<ArtistVO> _data){	
		try {
			mArtistDatas = _data;
			Collections.sort(mArtistDatas);
			mItemsArray = new SingerSectionListItem[mArtistDatas.size()];
			for (int i = 0; i < mArtistDatas.size(); i++) {
				SingerSectionListItem item = new SingerSectionListItem(
						mArtistDatas.get(i), mArtistDatas.get(i)
								.getFirstLetter());
				mItemsArray[i] = item;
			}
			mHandler.sendEmptyMessage(ON_DATAS_READY);
		} catch (Exception e) {
//			e.printStackTrace();
		}
	}
	
 
	
	/** 初始化列表适配器
	 *  */
	private void initDatas() {
		mArrayAdapter = new StandardArrayAdapter(SingersActivity.this, -1,
				mItemsArray);
		sectionAdapter = new SingersListViewAdapter(this, mListView,
				getLayoutInflater(), mArrayAdapter);
		mListView.setAdapter(sectionAdapter);
		mListView.setOnScrollListener(sectionAdapter);
		mListView.setPinnedHeaderView(getLayoutInflater().inflate(
				R.layout.local_singers_list_item_header, mListView, false));
	}

	private void findView() {
		mListView = (EasouPinneListView) findViewById(R.id.singesListView);
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

	public class SingerSectionListItem {
		public ArtistVO artistVO;
		public String section;

		public SingerSectionListItem(ArtistVO artistVO, String section) {
			this.artistVO = artistVO;
			this.section = section;
		}

		public ArtistVO getArtistVO() {
			return artistVO;
		}

		public void setArtistVO(ArtistVO artistVO) {
			this.artistVO = artistVO;
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_btn:
			back();
			break;
		}
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
	    //MobclickAgent.onPause(this);
	}
}
