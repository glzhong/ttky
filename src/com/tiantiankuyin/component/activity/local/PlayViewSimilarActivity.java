package com.tiantiankuyin.component.activity.local;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.tiantiankuyin.adapter.EasouExpandableListAdapter;
import com.tiantiankuyin.bean.MusicInfo;
import com.tiantiankuyin.bean.OlAlbumItem;
import com.tiantiankuyin.bean.OlAlbumVO;
import com.tiantiankuyin.bean.OlSingerVO;
import com.tiantiankuyin.bean.OlSongVO;
import com.tiantiankuyin.component.activity.BaseActivity;
import com.tiantiankuyin.database.bll.OnDataPreparedListener;
import com.tiantiankuyin.database.bll.OnlineMusicManager;
import com.tiantiankuyin.net.NetCache;
import com.tiantiankuyin.play.PlayLogicManager;
import com.tiantiankuyin.utils.BackKeyDownListener;
import com.tiantiankuyin.utils.CommonUtils;
import com.tiantiankuyin.utils.Lg;
import com.tiantiankuyin.view.PlayViewSimilarAlbumView;
import com.tiantiankuyin.view.PlayViewSingerSongsView;
import com.tiantiankuyin.view.PlayViewSongDetailView;
import com.tiantiankuyin.component.service.IRemotePlayerListener;
import com.tiantiankuyin.R;

/**
 * 相似推荐界面
 *
 */
public class PlayViewSimilarActivity extends Activity {
	public static final String ACTIVITY_ID = "PlayViewSimilarActivity";
	private ExpandableListView expandableListView;
	private EasouExpandableListAdapter adapter;
	/** 存放三快数据的集合对象 */
	private List<List<Object>> allMusics = new ArrayList<List<Object>>(); 
	/** 三块数据titile */
	private List<String> titles = new ArrayList<String>(); 
	/** 相似歌曲更多 */
	private LinearLayout playview_similar_more_layout; 
	/** 当前正在播放的歌曲 */
	private MusicInfo musicInfo; 
	/** 精选集的歌曲列表 */
	public static final int ALBUM_SONG = 0; 
	/** 精选集的歌曲列表 */
	public static final int ALBUM_MORE_SONG = 2; 
	/** 歌手的饿过去列表 两个共用一个列表 */
	public static final int SINGER_SONG = 1; 
	public static int page;
	/** 相似歌曲歌手的其他歌曲view */
	private PlayViewSongDetailView playViewSongDetailView; 
	/** 相似歌曲所属精选集 view */
	private PlayViewSimilarAlbumView playViewSimilarAlbumView; 
	private static PopupWindow pop;
	/** 加载完成标志 */
	public static final int LOAD_COMPLETE = 0x1;
	/** 信息加载完成标志 */
	public static final int LOAD_INFO_COMPLETE = 0x2;
	/** 动画显示完毕 */
	public static final int POPUP_WINDOW_DISMISS = 0x3;
	/** 用来记录 是否需要返回 */
	public static boolean isBack = false; 
	/** 无网络提示 */
	private LinearLayout no_network_tips; 
	/** 无网络刷新button */
	private Button fresh;

	interface ViewPagerListener {
		void onPage(int currentItem);
	}

	private static Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case LOAD_COMPLETE:
				break;
			case LOAD_INFO_COMPLETE:
				/* showSingerInfo(); */
				break;
			case POPUP_WINDOW_DISMISS:
				if (pop.isShowing())
					pop.dismiss();
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.local_play_view_similar);

		PlayLogicManager.newInstance().addObserver(
				PlayerListener.class.toString(), new PlayerListener());// 注册播放引擎观察者
		PlayViewActivity.instance.setPageListener(new MyPagerListener());// 设置翻页监听器
		BaseActivity.newInstance().setBackListener(new MyBackDownListener());// 设置监听器
	}

	/**
	 * 监听抽屉头上面的返回键
	 * 
	 * @author easou
	 * 
	 */
	public class MyBackDownListener implements BackKeyDownListener {
		@Override
		public void onBackButtonClick() {
			quickBack();
		}
	}

	private int pageIndex;

	class MyPagerListener implements ViewPagerListener {
		@Override
		public void onPage(int currentItem) {
			pageIndex = currentItem;
			if (currentItem == 0) {
				init();
			}
		}
	}

	/**
	 * 假数据 用来初始化adapter的作用的
	 */
	private void initData(String tips) {
		titles.clear();
		allMusics.clear();
		titles.add(0, tips);

		List<Object> musicTest = new ArrayList<Object>();
		OlSongVO musicInfo = new OlSongVO();
		musicInfo.setSong("test");
		musicTest.add(musicInfo);

		List<Object> items = new ArrayList<Object>();
		OlAlbumItem item = new OlAlbumItem();
		item.setName("test");
		items.add(item);
		if (tips.equals(getString(R.string.similar_tips))
				|| tips.equals(getString(R.string.singer_tips))) {
			allMusics.add(0, musicTest);
		} else {
			allMusics.add(0, items);
		}
		adapter.notifyDataSetChanged();
	}

	/**
	 * 获取相似歌曲信息
	 */
	private void getSimilarData() {
		if (musicInfo != null) {// 获取数据
			if (musicInfo.getArtist() != null
					&& (!"<unknown>".equals(musicInfo.getArtist()))
					&& (!"<未知歌曲>".equals(musicInfo.getArtist()))) {
				loadSimilarData(musicInfo.getTitle());// 加载歌曲的相似歌曲
			} else if (musicInfo.getArtist() != null
					&& (!"<unknown>".equals(musicInfo.getArtist()))
					&& (!"<未知歌手>".equals(musicInfo.getArtist()))) {
				loadSingerSongs(musicInfo.getArtist());// 加载歌手的其他歌曲数据
			} else if (musicInfo.getFileID()!=null) {
				if (musicInfo.getGid() > 0) {
					loadSelectView(musicInfo.getGid(), 1);// 加载歌曲所属精选集
				}
			}
		}
	}

	private void getSimilarTitles() {
		if (musicInfo != null) {// 获取数据
			if (musicInfo.getArtist() != null
					&& (!"<unknown>".equals(musicInfo.getArtist()))
					&& (!"<未知歌曲>".equals(musicInfo.getArtist()))) {
				initData(getString(R.string.similar_tips));
			} else if (musicInfo.getArtist() != null
					&& (!"<unknown>".equals(musicInfo.getArtist()))
					&& (!"<未知歌手>".equals(musicInfo.getArtist()))) {
				initData(getString(R.string.singer_tips));
			} else if (musicInfo.getFileID() != null) {
				if (musicInfo.getGid() > 0) {
					loadSelectView(musicInfo.getGid(), 1);// 加载歌曲所属精选集
				}
			}
		}
	}

	/**
	 * 初始化view
	 */
	private void init() {
		expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
		no_network_tips = (LinearLayout) findViewById(R.id.no_network_tips);
		no_network_tips.setVisibility(View.GONE);
		fresh = (Button) findViewById(R.id.fresh);
		fresh.setOnClickListener(freshListener);
		playview_similar_more_layout = (LinearLayout) findViewById(R.id.playview_similar_more_layout);
		adapter = new EasouExpandableListAdapter(this);
		musicInfo = PlayLogicManager.newInstance().getmCurMusic();
		if (!CommonUtils.isHasNetwork(this)) {// 无网络
			no_network_tips.setVisibility(View.VISIBLE);
			return;
		}
		getSimilarTitles();
		adapter.setGroups(titles);
		adapter.setListener(listener);
		adapter.setMusics(allMusics);
		expandableListView.setAdapter(adapter);
		adapter.setExpandableListView(expandableListView);
		expandableListView.setOnGroupClickListener(onGroupListener);
		expandableListView.setOnChildClickListener(childListener);// 设置点击事件
		for (int i = 0; i < adapter.getGroupCount(); i++) {
			expandableListView.expandGroup(i);
		}
		getSimilarData();
	}

	private View.OnClickListener freshListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (!CommonUtils.isHasNetwork(PlayViewSimilarActivity.this)) {// 无网络
				Toast.makeText(PlayViewSimilarActivity.this,
						getString(R.string.has_no_net), Toast.LENGTH_SHORT)
						.show();// 提示当前无可用的网络
				return;
			}
			init();
		}
	};
	
	private ExpandableListView.OnChildClickListener childListener = new ExpandableListView.OnChildClickListener() {
		@Override
		public boolean onChildClick(ExpandableListView parent, View v,
				int groupPosition, int childPosition, long id) {

			if (allMusics.get(groupPosition).get(childPosition) instanceof OlSongVO) {// 相似歌曲
																						// 和
																						// 歌手的其他歌曲点击事件
				try {
					if (allMusics != null && allMusics.size() > 0) {
						List<OlSongVO> olsongvos = new ArrayList<OlSongVO>();
						for (Object obj : allMusics.get(groupPosition)) {// 转换下对象
							olsongvos.add((OlSongVO) obj);
						}
						PlayLogicManager.newInstance().setOnlineMusicInfo(
								olsongvos, childPosition);
						PlayLogicManager.newInstance().play();
					}
				} catch (IllegalStateException e) {
					// e.printStackTrace();
				}
			} else {// 精选集的点击事件
				OlAlbumItem item = (OlAlbumItem) allMusics.get(groupPosition)
						.get(childPosition);
				isBack = true;
				getViewByLoadingMoreSong(null, ALBUM_MORE_SONG, item.getId(),
						true);
			}
			return false;
		}
	};
	
	private ExpandableListView.OnGroupClickListener onGroupListener = new ExpandableListView.OnGroupClickListener() {
		@Override
		public boolean onGroupClick(ExpandableListView parent, View v,
				int groupPosition, long id) {
			return true;
		}
	};
	
	/**
	 * 更多精选集点击事件
	 */
	private AdapterView.OnItemClickListener itemClick = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			TextView textView = (TextView) view.findViewById(R.id.name);
			page = ALBUM_MORE_SONG;
			if (Integer.parseInt(textView.getTag().toString()) > 0) {
				getViewByLoadingMoreSong(null, ALBUM_SONG,
						Integer.parseInt(textView.getTag().toString()), false);
			}
		}
	};
	
	private View.OnClickListener listener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			String value = v.getTag().toString();
			int groupPosition = Integer.parseInt(value.split("_")[0]);
			int childPosition = Integer.parseInt(value.split("_")[1]);
			isBack = true;
			if (allMusics.get(groupPosition).get(childPosition) instanceof OlSongVO) {// 歌手的其他歌曲
				page = SINGER_SONG;// 歌手的其他歌曲页面
				getViewByLoadingMoreSong(musicInfo.getArtist(), SINGER_SONG,
						-1, false);
			} else if (allMusics.get(groupPosition).get(childPosition) instanceof OlAlbumItem) {// 精选集的更多歌曲
				page = ALBUM_SONG;
				getAlbumViewBySongId(musicInfo.getGid());
			}
		}
	};

	/**
	 * 加载歌手的其他歌曲
	 * 
	 * @param artist
	 */
	private void getViewByLoadingMoreSong(String artist, int type, int albumId,
			boolean isSecond) {
		playview_similar_more_layout.removeAllViews();
		expandableListView.setVisibility(View.GONE);
		if (type == PlayViewSimilarActivity.SINGER_SONG) {
			PlayViewSingerSongsView view = new PlayViewSingerSongsView(this,
					artist);
			playview_similar_more_layout.addView(view);
		} else {
			playViewSongDetailView = new PlayViewSongDetailView(this, artist,
					type, albumId);
			playview_similar_more_layout.addView(playViewSongDetailView);
			if (isSecond) {// 返回键盘处理
				page = SINGER_SONG;
			}
		}
	}

	/**
	 * 加载精选集的其他精选集
	 * @param songId
	 */
	private void getAlbumViewBySongId(long songId) {
		playview_similar_more_layout.removeAllViews();
		expandableListView.setVisibility(View.GONE);
		playViewSimilarAlbumView = new PlayViewSimilarAlbumView(this, songId,
				itemClick);
		playview_similar_more_layout.addView(playViewSimilarAlbumView);
	}

	/**
	 * 加载歌手的其他歌曲
	 * @param singerName
	 */
	private void loadSingerSongs(String singerName) {
		final String url = CommonUtils.getOlQueryRequestURL("s", singerName, 1);
		try {
			OlSingerVO olSingerVO = NetCache.readCache(url);
			refreshSingerView(olSingerVO);
		} catch (ClassCastException e) {
			// e.printStackTrace();
		} catch (IOException e) {
			// e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// e.printStackTrace();
		}
		OnlineMusicManager.getInstence().getSongListData(this,
				new OnDataPreparedListener<OlSingerVO>() {
					@Override
					public void onDataPrepared(OlSingerVO data) {
						if (data != null
								&& data.getDataList() != null
								&& data.getDataList().size() > 0) {
							refreshSingerView(data);
							try {
								NetCache.saveCache(data, url);// 保存缓存
							} catch (IOException e) {
								// e.printStackTrace();
							}
						}else {
							Lg.d("getSongListData() == null");
							if (titles.size() > 0) {
								titles.remove(titles.size() - 1);
								adapter.notifyDataSetChanged();
							}
//							return;
						}
						// 加载精选集
						if (musicInfo != null && musicInfo.getGid() > 0) {
							if (!titles.contains(getString(R.string.selected_tips))) {
								titles.add(getString(R.string.selected_tips));
							}
							adapter.notifyDataSetChanged();
							loadSelectView(musicInfo.getGid(), 1);
						}
					}
				}, url);
	}

	/**
	 * 刷新歌手的其他歌曲界面
	 */
	private void refreshSingerView(OlSingerVO olSingerVO) {
		List<Object> objs = new ArrayList<Object>();
		for (OlSongVO olSongVO : olSingerVO.getDataList()) {
			objs.add(olSongVO);
		}
		if (objs.size() > 5) {// 数据大于5只取前面5个
			adapter.setSingerMore(true);
			List<Object> object = new ArrayList<Object>();
			for (int i = 0; i < 5; i++) {
				object.add(objs.get(i));
			}
			objs.clear();
			objs.addAll(object);
		}
		if (allMusics.size() > 1) {
			return;
		}
		allMusics.add(objs);
		adapter.notifyDataSetChanged();
		for (int i = 0; i < adapter.getGroupCount(); i++) {
			expandableListView.expandGroup(i);
		}
		adapter.notifyDataSetChanged();
	}

	/**
	 * 加载相似歌曲数据
	 * @param queryStr
	 */
	private void loadSimilarData(String queryStr) {
		final String url = CommonUtils.getSimilarOlQueryRequestURL(queryStr, 1);
		try {
			OlSingerVO olSingerVO = NetCache.readCache(url);
			refreshSimiarView(olSingerVO);
		} catch (ClassCastException e) {
			// e.printStackTrace();
		} catch (IOException e) {
			// e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// e.printStackTrace();
		}
		OnlineMusicManager.getInstence().getSongListData(this,
				new OnDataPreparedListener<OlSingerVO>() {
					@Override
					public void onDataPrepared(OlSingerVO data) {
						if (data != null
								&& data.getDataList() != null
								&& data.getDataList().size() > 0) {

							refreshSimiarView(data);
							try {
								NetCache.saveCache(data, url);// 保存缓存
							} catch (IOException e) {
								// e.printStackTrace();
							}
						} else {
							Lg.d("getSongListData() == null");
							titles.clear();
							allMusics.clear();
							adapter.notifyDataSetChanged();
//							return;
						}
						// 加载 第二个数据
						if (!titles.contains(getString(R.string.singer_tips))) {
							titles.add(getString(R.string.singer_tips));
						}
						adapter.notifyDataSetChanged();
						loadSingerSongs(musicInfo.getArtist());// 加载歌手的其他歌曲数据
					}
				}, url);
	}

	/**
	 * 刷新相似歌曲界面
	 * @param songs
	 */
	private void refreshSimiarView(OlSingerVO olSingerVO) {
		List<Object> objs = new ArrayList<Object>();
		for (OlSongVO olSongVO : olSingerVO.getDataList()) {
			objs.add(olSongVO);
		}
		allMusics.clear();
		allMusics.add(objs);
		adapter.notifyDataSetChanged();
		for (int i = 0; i < adapter.getGroupCount(); i++) {
			expandableListView.expandGroup(i);
		}
	}

	/**
	 * 请求歌曲所属精选集的方法
	 */
	private void loadSelectView(long gid, int pageNum) {
		final String url = CommonUtils.getSongBelongToSelect(gid, 20, 400,
				pageNum);
		try {
			OlAlbumVO mOlAlbumVO = NetCache.readCache(url);
			refreshAlbum(mOlAlbumVO);
		} catch (ClassCastException e) {
			// e.printStackTrace();
		} catch (IOException e) {
			// e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// e.printStackTrace();
		}
		OnlineMusicManager.getInstence().getOmnibusData(this,
				new OnDataPreparedListener<OlAlbumVO>() {
					@Override
					public void onDataPrepared(OlAlbumVO data) {
						if (data != null
								&& data.getDataList() != null
								&& data.getDataList().size() > 0) {
							refreshAlbum(data);
							try {
								NetCache.saveCache(data, url);// 保存缓存
							} catch (IOException e) {
								// e.printStackTrace();
							}
						}else {
							Lg.d("getOmnibusData() == null");
							albumHasNoData();// 没有返回数据
							return;
						}
					}
				}, url);
	}

	private void albumHasNoData() {
		try {
			if (titles.size() > 0) {
				titles.remove(titles.size() - 1);
			}
		} catch (Exception e) {
			// e.printStackTrace();
		}
		adapter.notifyDataSetChanged();
	}

	/**
	 * 刷新相似歌曲界面
	 * @param songs
	 */
	private void refreshAlbum(OlAlbumVO mOlAlbumVO) {
		List<Object> musics = new ArrayList<Object>();
		if (mOlAlbumVO.getDataList().size() > 5) {
			adapter.setAlbumMore(true);
			for (int i = 0; i < 5; i++) {
				OlAlbumItem olAlbumItem = mOlAlbumVO.getDataList().get(i);
				musics.add(olAlbumItem);
			}
		} else {
			for (OlAlbumItem olAlbumItem : mOlAlbumVO.getDataList()) {
				musics.add(olAlbumItem);
			}
		}
		if (allMusics.size() > 2) {
			return;
		}
		allMusics.add(musics);
		adapter.notifyDataSetChanged();
		for (int i = 0; i < adapter.getGroupCount(); i++) {
			expandableListView.expandGroup(i);
		}
	}

	public class PlayerListener extends IRemotePlayerListener.Stub {
		@Override
		public void onError(int what, int extra) {

		}

		@Override
		public void onPrepared() {
		}

		@Override
		public void onStartBuffer() {

		}

		@Override
		public void onBufferingUpdate(int percent) {

		}

		@Override
		public void onStartPlay() {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (pageIndex == 0) {
						quickBack();
						init();
					}
				}
			});
		}

		@Override
		public void onMusicPause() {

		}

		@Override
		public void onProgressChanged(int currentMilliseconds) {

		}

		@Override
		public void onCompletion() {

		}

		@Override
		public void onMusicStop() {

		}

		@Override
		public void onBufferComplete() {

		}

		@Override
		public void onBuffer() {

		}

		@Override
		public void onCacheUpdate(long currentCache) {

		}

		@Override
		public void onPreparing() {
		}
	}

	/**
	 * 返回到一级界面
	 */
	private void backToFirstPage() {
		if (playview_similar_more_layout != null && expandableListView != null) {
			playview_similar_more_layout.removeAllViews();
			expandableListView.setVisibility(View.VISIBLE);
		}
		isBack = false;
		page = -1;
	}

	/**
	 * 返回到二级界面
	 */
	private void backToSecondPage() {
		if (playview_similar_more_layout != null && expandableListView != null) {
			playview_similar_more_layout.removeAllViews();
			expandableListView.setVisibility(View.GONE);
			playview_similar_more_layout.addView(playViewSimilarAlbumView);
		}
		isBack = true;
		page = ALBUM_SONG;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {// 返回键
			back();
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	private void back() {
		if (page == SINGER_SONG || page == ALBUM_SONG) {// 在 歌手的其他歌曲列表
			backToFirstPage();
		} else if (page == ALBUM_MORE_SONG) {// 在精选集的列表的其他歌曲类别
			backToSecondPage();
		}
	}

	/**
	 * 快速返回 从第三个 界面返回到第一个界面
	 */
	private void quickBack() {
		if (page == ALBUM_MORE_SONG) {// 在精选集的列表的其他歌曲类别
			backToSecondPage();
		}
		backToFirstPage();
	}
}
