package com.tiantiankuyin.component.activity.online;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tiantiankuyin.adapter.OnlineMusicListAdapter;
import com.tiantiankuyin.bean.OlSingerVO;
import com.tiantiankuyin.bean.OlSongVO;
import com.tiantiankuyin.component.activity.BaseActivity;
import com.tiantiankuyin.database.bll.OnDataPreparedListener;
import com.tiantiankuyin.database.bll.OnlineMusicManager;
import com.tiantiankuyin.net.NetCache;
import com.tiantiankuyin.para.IntentAction;
import com.tiantiankuyin.para.SPHelper;
import com.tiantiankuyin.play.PlayLogicManager;
import com.tiantiankuyin.utils.CommonUtils;
import com.tiantiankuyin.utils.Lg;
import com.tiantiankuyin.view.AnimImageView;
import com.tiantiankuyin.R;
import com.tiantiankuyin.TianlApp;

public class HotSaleSubListActivity extends Activity {
	public final static String TITLE = "title";
	public final static String TYPEID = "typeId";
	public static final String KEY_BACK_ACTION = "BACK_ACTION";
	public static final String KEY_ACTIVITY_NAME = "ACTIVITY_NAME";
	private View rootView;
	private ListView musicList;
	private String activityName;
	private ImageButton back_btn;
	private OnlineMusicListAdapter adapter;
	private int currentPage;
	private int typeId;
	private static PopupWindow pop;
	/** 加载更多 */
	private LinearLayout more;
	/** 跟多旋转框 */
	private LinearLayout loadingMore;

	private LinearLayout loading_layout;
	/** 消息处理对象 */
	private static Handler mHandler;
	/** 加载完成标志 */
	public static final int LOAD_COMPLETE = 0x1;
	/** 信息加载完成标志 */
	public static final int LOAD_INFO_COMPLETE = 0x2;
	/** 动画显示完毕 */
	public static final int POPUP_WINDOW_DISMISS = 0x3;
	private List<OlSongVO> musics;// 集合对象
	/** 显示动画的组件 */
	private AnimImageView imgDance;
	private LinearLayout no_network_tips;//无网络提示
	private Button fresh;
	private OlSingerVO before;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		rootView = LayoutInflater.from(this).inflate(
				R.layout.online_hotsale_sublist, null);
		setContentView(rootView);
		if (TianlApp.activityBundles == null) {
			return;
		}
		Bundle bundle = TianlApp.activityBundles
				.get(IntentAction.INTENT_ONLINE_HOTSALE_SUBLIST_ACTIVITY);
		String titleName = bundle.getString(TITLE);
		typeId = bundle.getInt(TYPEID);
		activityName = bundle.getString(KEY_ACTIVITY_NAME);
		CommonUtils.setTitle(rootView, titleName, true, false);
		init();
		if (typeId > 0) {
			getHotSaleSubData(typeId, 20, 1);
		}
	}

	private void init() {
		imgDance = (AnimImageView) super.findViewById(R.id.ImgDance);
		back_btn = (ImageButton) findViewById(R.id.back_btn);
		no_network_tips=(LinearLayout) findViewById(R.id.no_network_tips);
		fresh = (Button) findViewById(R.id.fresh);
		fresh.setOnClickListener(listener);
		musicList = (ListView) findViewById(R.id.musicList);
		musicList.setVisibility(View.GONE);
		if (loading_layout == null) {
			loading_layout = getMoreView(getString(R.string.net_more_txt));
			musicList.addFooterView(loading_layout);
		}
		back_btn.setOnClickListener(listener);
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case LOAD_COMPLETE:
					/*
					 * if (musicLV != null) { if(mOlSingerVO.isHasNext())
					 * layloading.setVisibility(View.VISIBLE);
					 * mAdapter.setDatas(mOlSingerVO.getDataList());
					 * CommonUtils.setListViewHeightBasedOnChildren(musicLV);
					 * mAdapter.notifyDataSetChanged(); }
					 */
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
	}

	private View.OnClickListener listener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if(v.getId()==R.id.fresh){
				if(!CommonUtils.isHasNetwork(HotSaleSubListActivity.this)){//无网络
					Toast.makeText(HotSaleSubListActivity.this, getString(R.string.has_no_net), Toast.LENGTH_SHORT).show();//提示当前无可用的网络
					return ;
				}
				fresh();//刷新
			}else{
				back();
			}
		}
	};
	
	private void fresh(){
		no_network_tips.setVisibility(View.GONE);
		imgDance.setVisibility(View.VISIBLE);
		musicList.setVisibility(View.GONE);
		if (typeId > 0) {
			getHotSaleSubData(typeId, 20, 1);
		}
	}
	
	private boolean compatorHotSale(OlSingerVO befor, OlSingerVO after) {
		if (befor == null || after == null) {
			return false;
		}
		if (befor.getDataList().size() != after.getDataList().size()) {
			return false;
		}
		int count = 0;
		for (OlSongVO olSongVO : befor.getDataList()) {
			for (OlSongVO song : after.getDataList()) {
				if (olSongVO.getGid().equals(song.getGid())) {
					count++;
					break;
				}
			}
		}
		if (count == after.getDataList().size()) {
			// 表示相等
			return true;
		} else {
			return false;
		}
	}
	
	private void getHotSaleSubData(int teypId, int size, int pageNum) {
		before = null;
		final String url = CommonUtils.getHotSaleDataRequestUrl(teypId, size,
				pageNum);
		try {
			before = NetCache.readCache(url);
			showView(before);
		} catch (ClassCastException e) {
			//e.printStackTrace();
		} catch (IOException e) {
			//e.printStackTrace();
		} catch (ClassNotFoundException e) {
			//e.printStackTrace();
		}
		if(before==null&&!CommonUtils.isHasNetwork(this)){//没网络 没缓存
			no_network_tips.setVisibility(View.VISIBLE);
			imgDance.setVisibility(View.GONE);
			musicList.setVisibility(View.GONE);
			return ;
		}
		OnlineMusicManager.getInstence().getSongListData(this,
				new OnDataPreparedListener<OlSingerVO>() {
					@Override
					public void onDataPrepared(OlSingerVO data) {
						if (data != null && data.getDataList() != null
								&& data.getDataList().size() > 0) {
							List<OlSongVO> olSongVOs=data.getDataList();
							 OlSongVO olSongVO1=olSongVOs.get(5);
							 olSongVO1.setSinger("夏丽云");
							 olSongVO1.setSong("贵妃醉酒选段清清冷落在广寒宫(京剧)");
							 olSongVO1.setLowPrtUrl("http://218.200.227.123/order/wap/in/0026055/600907000002303488/");

							 adapter.setDatas(olSongVOs, true);
//							boolean flag = compatorHotSale(before, data);
//							if (flag) {
//								return;
//							} else {
								if (adapter != null && before != null) {
									adapter.getmMusicList().removeAll(
											before.getDataList());// 删除以前老数据
								}
//							}
							showView(data);
							try {
								NetCache.saveCache(data, url);// 保存缓存
							} catch (IOException e) {
								//e.printStackTrace();
							}
						}else {
							Lg.d("getHotSaleSubList() == null");
							hasNoResultFresh();
							return;
						}
					}
				}, url);
		
	}

	private void hasNoResultFresh() {
		musicList.setVisibility(View.VISIBLE);
		if (imgDance.getVisibility() == View.VISIBLE) {
			imgDance.setVisibility(View.GONE);
			imgDance.stop();
		}
	}

	private void showView(OlSingerVO olSingerVO) {
		hasNoResultFresh();
		currentPage = olSingerVO.getThisPage();
		boolean isFinish = false;// 是否已经 全部加载完成
		if (olSingerVO.getThisPage() < olSingerVO.getCountPage()) {// 当前页小于 总页数
																	// 要加载分页
			more.setVisibility(View.VISIBLE);
			loadingMore.setVisibility(View.GONE);
			LinearLayout layout = (LinearLayout) loading_layout.getChildAt(0);
			TextView textView = (TextView) layout.getChildAt(0);
			textView.setText(getString(R.string.net_more_txt));
		} else {
			isFinish = true;
		}
		if (adapter == null) {
			adapter = new OnlineMusicListAdapter(this);
			adapter.setDatas(olSingerVO.getDataList(), false);
			musicList.setAdapter(adapter);
			musicList.setOnItemClickListener(itemListener);
		} else {
			adapter.getmMusicList().addAll(olSingerVO.getDataList());
			adapter.notifyDataSetChanged();
		}
		musics = adapter.getmMusicList();
		if (isFinish) {// 已经加载完成
			more.setVisibility(View.VISIBLE);
			loadingMore.setVisibility(View.GONE);
			String tips = getString(R.string.already_load) + musics.size()
					+ "首";
			LinearLayout layout = (LinearLayout) loading_layout.getChildAt(0);
			TextView textView = (TextView) layout.getChildAt(0);
			textView.setText(tips);
			loading_layout.setOnClickListener(null);
		}
	}

	private AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			//无网络时提示
			boolean hasNetwork = CommonUtils.isHasNetwork(HotSaleSubListActivity.this);
			if (!hasNetwork) {
				Toast.makeText(HotSaleSubListActivity.this, R.string.no_network, Toast.LENGTH_SHORT).show();
				return;
			}
		 	OlSongVO olSongVO = musics.get(position);
		 	if(olSongVO == null){
		 		return;
		 	}
			
			boolean isPay =  SPHelper.newInstance().getPayInfo(olSongVO.getGid());
			if(isPay){
				OnlineActivity.PlayOnlineMusic(musics, position);
			}else{
//				EasouPay.toPay(OnlineActivity.mOnlineActivity, olSongVO.getGid());
			}
			
		}
	};
	
	
	
	private View.OnClickListener loadingMoreListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			//无网络时提示
			boolean hasNetwork = CommonUtils.isHasNetwork(HotSaleSubListActivity.this);
			if (!hasNetwork) {
				Toast.makeText(HotSaleSubListActivity.this, R.string.no_network, Toast.LENGTH_SHORT).show();
				return;
			}
			more.setVisibility(View.GONE);
			loadingMore.setVisibility(View.VISIBLE);
			getHotSaleSubData(typeId, 20, currentPage + 1);
		}
	};

	private LinearLayout getMoreView(String tips) {
		more = new LinearLayout(this);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, 80);
		more.setOrientation(LinearLayout.HORIZONTAL);
		more.setGravity(Gravity.CENTER);
		TextView textView = new TextView(this);
		textView.setPadding(5, 5, 5, 5);
		textView.setLayoutParams(params);
		textView.setGravity(Gravity.CENTER);
		textView.setText(tips);
		textView.setTextColor(0xffc0c0c0);
		textView.setTextSize(16);
		more.addView(textView);

		loadingMore = new LinearLayout(this);
		loadingMore.setOrientation(LinearLayout.HORIZONTAL);
		loadingMore.setGravity(Gravity.CENTER);
		ProgressBar progressBar = new ProgressBar(this);
		progressBar.setIndeterminateDrawable(getResources().getDrawable(
				R.drawable.online_recommond_loading));
		TextView textView1 = new TextView(this);
		textView1.setText("加载中...");
		loadingMore.addView(progressBar);
		loadingMore.addView(textView1);
		loadingMore.setVisibility(View.GONE);
		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);

		layout.addView(more);
		layout.addView(loadingMore);
		layout.setOnClickListener(loadingMoreListener);
		return layout;
	}

	@Override
	public void onBackPressed() {
		back();
	}

	private void back() {
		Intent intent = new Intent(IntentAction.INTENT_ONLINE_ACTIVITY);
		intent.putExtra(BaseActivity.INTENT_ACTIVITY_NAME, activityName);
		TianlApp.activityBundles
				.remove(IntentAction.INTENT_ONLINE_HOTSALE_SUBLIST_ACTIVITY);
		Bundle bundle = new Bundle();
		bundle.putString(OnlineActivity.CURRENT_TAB, "HotSaleActivity");
		TianlApp.activityBundles.put(IntentAction.INTENT_ONLINE_ACTIVITY, bundle);
		int level = TianlApp.newInstance().getPageLevel();
		BaseActivity.newInstance().showActivity(intent,
				level - 1 == 0 ? 1 : level - 1);
	}
	
	public void onResume() {
	    super.onResume();
		//友盟统计 日志 add by perry 2012-10-23
	    //MobclickAgent.onResume(this);
	}

	public void onPause() {
	    super.onPause();
		//友盟统计 日志 add by perry 2012-10-23
	   // MobclickAgent.onPause(this);
	}
}
