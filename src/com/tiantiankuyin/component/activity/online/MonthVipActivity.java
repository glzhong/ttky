package com.tiantiankuyin.component.activity.online;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cmsc.cmmusic.common.CPManagerInterface;
import com.cmsc.cmmusic.common.MusicQueryInterface;
import com.cmsc.cmmusic.common.data.MusicInfo;
import com.cmsc.cmmusic.common.data.MusicInfoResult;
import com.cmsc.cmmusic.common.data.QueryResult;
 
import com.tiantiankuyin.adapter.BannerAdapter;
import com.tiantiankuyin.adapter.OnlineMusicListAdapter;
import com.tiantiankuyin.bean.Banner;
import com.tiantiankuyin.bean.Banner.BannerType;
import com.tiantiankuyin.bean.OlRecommondSong;
import com.tiantiankuyin.bean.OlSongVO;
import com.tiantiankuyin.bean.RecommendBanner;
import com.tiantiankuyin.bean.RecommendBanner.ServerBanner;
import com.tiantiankuyin.component.activity.BaseActivity;
import com.tiantiankuyin.component.activity.WebViewActivity;
import com.tiantiankuyin.database.bll.OnDataPreparedListener;
import com.tiantiankuyin.database.bll.OnlineMusicManager;
import com.tiantiankuyin.net.CmMusicSearch;
import com.tiantiankuyin.net.NetCache;
import com.tiantiankuyin.para.IntentAction;
import com.tiantiankuyin.para.UserData;
import com.tiantiankuyin.para.WebServiceUrl;
import com.tiantiankuyin.play.PlayLogicManager;
import com.tiantiankuyin.utils.CommonUtils;
import com.tiantiankuyin.utils.HttpUtils;
import com.tiantiankuyin.utils.Lg;
import com.tiantiankuyin.view.EasouBannerGallery;
import com.tiantiankuyin.view.EasouOnlineDialog;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.tiantiankuyin.R;
import com.tiantiankuyin.TianlApp;

import dalvik.system.DexClassLoader;

/**
 * 在线模块-歌手
 * @author Erica
 */
public class MonthVipActivity extends Activity implements OnClickListener {

	/** 日志打印标志位 */
	public static final String ACTIVITY_ID = "MonthVipActivity";
	/** banner请求服务器成功，已返回数据 */
	private static final int BANNER_RESPONSE = 0x1;
	/** banner图片请求服务器成功，已返回数据 */
	private static final int BANNER_IMAGE_RESPONSE = 0x2;

	private LocalActivityManager mLocalActivityManager;
	/** Banner Gallery */
	private EasouBannerGallery mBannerGallery;
	/** Banner Gallery适配器 */
	private BannerAdapter mBannerAdapter;
	/** 用于显示Banner张数的小圆点 */
	private LinearLayout pointContainer;
	/** 在线推荐页头部，包含Banner Gallery和新歌推荐标题 */
	private View headerView;
	/** 在线推荐页下推荐歌曲适配器（id=76为新歌推荐榜单） */
	private OnlineMusicListAdapter adapter;
	/** 服务器返回的banner信息 */
	private Handler mHandler;
	/** 显示banner的集合 */
	private List<Banner> bannerView;
	/** 显示加载更多 */
	private View footerView;
	public static MonthVipActivity instance;
	/** 用于保存页面数据 */
	public static OlRecommondSong pageData;
	/** 是否正在加载更多 */
	public static boolean isLoadingMore;
	private LinearLayout centerContainer;
	private LinearLayout one,four,six,eight,ten,mianze;
	private LinearLayout oneList,fourList,sixList,eightList,tenList;
	public Context context;
	private AlertDialog dialog;
	private static TextView alertTV;
	private static ProgressBar loadingPB;
	private Handler reqHandler;
	private ImageView mimuMusic;
	private TextView textView;
	private int currentImgIndex=0;	
	private int[] imgageSourse = {R.drawable.baoyue_01};	
	
	public static String TEL_NO;

	//获取包月歌单时使用
	public static boolean NEXT_FLAG=false;
	
	public static String SYNC_LOCK="LOCK";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.online_month_vip);
		context = this;
		if (TianlApp.newInstance().activityList != null
				&& !TianlApp.newInstance().activityList.contains(this)) {
			TianlApp.newInstance().activityList.add(this); // 将该Activity实例加入到Activity管理集合中
		}
		findView();
		init();
		Runnable img = new Runnable() {
			public void run() {
//				if(currentImgIndex>2){
//					currentImgIndex = 0;
//				}
				mimuMusic.setImageResource(imgageSourse[0]);
				mimuMusic.setScaleType(ScaleType.FIT_XY);
				currentImgIndex++;
				mHandler.postDelayed(this, 5000);
			}
		};
		mHandler.postDelayed(img, 5000);		
	}

	private void findView() {
		centerContainer = (LinearLayout)findViewById(R.id.center_scroll_content);
		headerView = LayoutInflater.from(this).inflate(
				R.layout.online_month_vip_header, null);
		mimuMusic = (ImageView)headerView.findViewById(R.id.mimu_music);
		footerView = LayoutInflater.from(this).inflate(
				R.layout.online_recommend_listview_footer, null);
		alertTV = (TextView) footerView.findViewById(R.id.loadMoreText);
		loadingPB = (ProgressBar) footerView
				.findViewById(R.id.recommondDataLoading);
		footerView.setOnClickListener(loadingMoreListener);		
		headerView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				loadPlugin();
			}
		});
		mBannerGallery = (EasouBannerGallery) headerView
				.findViewById(R.id.banner_gallery);
		pointContainer = (LinearLayout) headerView
				.findViewById(R.id.point_container);
	}

	private void init() {
		instance = this;
		mLocalActivityManager = new LocalActivityManager(this, true);
		mBannerAdapter = new BannerAdapter(this);
		mBannerGallery.setAdapter(mBannerAdapter);
		centerContainer.addView(headerView);
		LinearLayout listView=(LinearLayout)LayoutInflater.from(this).inflate(
				R.layout.online_month_list, null);
		android.widget.AbsListView.LayoutParams lp = new android.widget.AbsListView.LayoutParams(
				-1, CommonUtils.dip2px(this, 55));
		footerView.setLayoutParams(lp);
		mianze=(LinearLayout)listView.findViewById(R.id.mianze);
		mianze.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				 Uri uri = Uri.parse("http://218.98.35.163/mp3/wzdmse.htm");  
				 Intent it = new Intent(Intent.ACTION_VIEW, uri);  
				 startActivity(it);
			}
		});
		one=(LinearLayout)listView.findViewById(R.id.one_vip);
		four=(LinearLayout)listView.findViewById(R.id.four_vip);
		six=(LinearLayout)listView.findViewById(R.id.six_vip);
		eight=(LinearLayout)listView.findViewById(R.id.eight_vip);
		ten=(LinearLayout)listView.findViewById(R.id.ten_vip);
		oneList=(LinearLayout)listView.findViewById(R.id.one_vip_list);
		fourList=(LinearLayout)listView.findViewById(R.id.four_vip_list);
		sixList=(LinearLayout)listView.findViewById(R.id.six_vip_list);
		eightList=(LinearLayout)listView.findViewById(R.id.eight_vip_list);
		tenList=(LinearLayout)listView.findViewById(R.id.ten_vip_list);
		one.setOnClickListener(this);
		four.setOnClickListener(this);
		six.setOnClickListener(this);
		eight.setOnClickListener(this);
		ten.setOnClickListener(this);
		centerContainer.addView(listView);
		centerContainer.addView(footerView);
		
		//recommendLV.addFooterView(footerView, null, true);
		//recommendLV.setOnItemClickListener(this);
		//adapter = new OnlineMusicListAdapter(OnlineActivity.mOnlineActivity);
		//recommendLV.setAdapter(adapter);
		boolean cacheflag = false;
		/*try {
			OlRecommondSong songBean1 = NetCache
					.readCache(WebServiceUrl.BAOYUE_URL_1);
			loadRecommendCache(songBean1, true);
			cacheflag = true;
		} catch (ClassCastException e) {
			// e.printStackTrace();
		} catch (IOException e) {
			// e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// e.printStackTrace();
		}*/
		reqHandler=new ReqHandler();
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case BANNER_RESPONSE:
					break;
				case BANNER_IMAGE_RESPONSE:
					break;
				}
			}
		};		
		/*if(!cacheflag){
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
						boolean created = UserData.getInstence()
								.isLoadedRecommondDataFromServer();
						loadRecommondDataFromServer(!created);
						UserData.getInstence().setLoadedRecommondDataFromServer(true);
					}
				
			};
			mHandler.post(runnable);
		}*/
	}
	
	public class ReqHandler extends Handler{
		 public void handleMessage(Message msg){  
			LinearLayout currentLin=oneList;
			String desc="";
			String serviceId=msg.obj.toString();
			if(serviceId.equals(EasouOnlineDialog.serviceId_one)){
				desc="一元包月列表";
				currentLin=oneList;
			}else if(serviceId.equals(EasouOnlineDialog.serviceId_four)){
				desc="四元包月列表";
				currentLin=fourList;
			}else if(serviceId.equals(EasouOnlineDialog.serviceId_six)){
				desc="六元包月列表";
				currentLin=sixList;
			}else if(serviceId.equals(EasouOnlineDialog.serviceId_eight)){
				desc="八元包月列表";
				currentLin=eightList;
			}else if(serviceId.equals(EasouOnlineDialog.serviceId_ten)){
				desc="十元包月列表";
				currentLin=tenList;
			}
			
			switch (msg.what) {
			case 1:
				String str = msg.getData().getString("array");
				try {
					JSONArray array = new JSONArray(str);
					ListView listView = new ListView(MonthVipActivity.this);
					listView.setLayoutParams(new LayoutParams(
							LayoutParams.MATCH_PARENT,
							LayoutParams.WRAP_CONTENT));
					List<OlSongVO> alist = new ArrayList<OlSongVO>();
					OnlineMusicListAdapter onlineAdapter = new OnlineMusicListAdapter(
							OnlineActivity.mOnlineActivity);
					
					
					for (int i = 0; i < array.length(); i++) {
						try {
							JSONObject jsonObject = array.getJSONObject(i);
							OlSongVO olSongVO = new OlSongVO();
							olSongVO.setSinger(jsonObject.getString("singer"));
							olSongVO.setSong(jsonObject.getString("songName"));
							olSongVO.setGid(jsonObject
									.getString("songCode"));
							olSongVO.setServiceId(serviceId);
							alist.add(olSongVO);
						} catch (JSONException e) {
						}
					}
					onlineAdapter.setDatas(alist, false);
					listView.setAdapter(onlineAdapter);
					listView.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent, View view,
								int position, long id) {
							PlayLogicManager.newInstance().stop();
							TextView songId=(TextView)view.findViewById(R.id.songId);
							TextView songName=(TextView)view.findViewById(R.id.musicName);
							TextView artView=(TextView)view.findViewById(R.id.musicArtist);
						
 							MusicInfoResult  infoRes=MusicQueryInterface.getMusicInfoByMusicId(MonthVipActivity.this,songId.getText().toString());
							//QueryResult  infoRes=CPManagerInterface.queryCPMonth(MonthVipActivity.this,"632555Z01000100002");
							 
							if(infoRes!=null){
								MusicInfo info=infoRes.getMusicInfo();
								if(info!=null){
									com.tiantiankuyin.bean.MusicInfo music=new com.tiantiankuyin.bean.MusicInfo();
									music.setArtist(info.getSingerName());
									music.setFileID(info.getMusicId());
									music.setGid(Long.parseLong(info.getSongValidity()));
									music.setTitle(songName.getText().toString());
									music.setNetUrl(info.getSongListenDir());
									music.setAlbum(artView.getText().toString());
									PlayLogicManager.newInstance().playNet(music);
								}
							}
							
						}
					});
					fixListViewHeight(listView);
					currentLin.removeAllViews();
					currentLin.addView(listView);
				} catch (JSONException e1) {
					currentLin.removeAllViews();
					currentLin.setVisibility(View.GONE);
					Toast.makeText(MonthVipActivity.this, "加载"+desc+"失败",
							Toast.LENGTH_LONG).show();
				}
				break;
			case 0:
//				currentLin.removeAllViews();
//				currentLin.setVisibility(View.GONE);
				Toast.makeText(MonthVipActivity.this, "加载"+desc+"失败",
						Toast.LENGTH_LONG).show();
				break;
			}
		 }
	}
	public class BackGroundThread implements Runnable {
		private String url;
		private String serviceId;
		public BackGroundThread(String url,String serviceId){
			this.url=url;
			this.serviceId=serviceId;
		}
		@Override
		public void run() {
			Message message=new Message();
			message.obj=serviceId;
			try {
				//String str=HttpUtils.req(url);
				Bundle b=new Bundle();
				String str ="";
				 JSONArray js= new JSONArray();
				 
				 js.put(0, new JSONObject().put("singer", "美衫儿").put("songName", "最美的知己").put("songCode", "63357400101"));//"singer"
				 js.put(1, new JSONObject().put("singer", "美衫儿").put("songName", "爱情守护者").put("songCode", "63357400100"));
				 js.put(2, new JSONObject().put("singer", "美衫儿").put("songName", "多想徜徉在大海边").put("songCode", "63357400099"));
				 js.put(3, new JSONObject().put("singer", "美衫儿").put("songName", "一点温暖").put("songCode", "63357400098"));//"singer"
				 js.put(4, new JSONObject().put("singer", "美衫儿").put("songName", "与众不同").put("songCode", "63357400097"));
				 js.put(5, new JSONObject().put("singer", "美衫儿").put("songName", "躲过").put("songCode", "63357400096"));
				// JSONArray jsonArray = JSONArray.fromObject(js.toString());
				b.putString("array",js.toString());
				message.setData(b);
				message.what=1;
			} catch (Exception e) {
				message.what=0;
				
				e.printStackTrace();
			}
			reqHandler.sendMessage(message);
		}
		
	}
	/**
	 * 从服务器加载新歌榜单数据
	 */
	private void loadRecommondDataFromServer(final boolean needNext) {
		isLoadingMore = true;
		CmMusicSearch search = CmMusicSearch.getInstance();
		QueryResult queryResult1 = search.queryCPMonth(context, EasouOnlineDialog.serviceId_one);
		
		if(StringUtils.equals("000000", queryResult1.getResCode())){
			if(StringUtils.isEmpty(TEL_NO)){
				TEL_NO = queryResult1.getMobile();
			}
			OnlineMusicManager.getInstence().getRecommondData(this,
					new OnDataPreparedListener<OlRecommondSong>() {
						@Override
						public void onDataPrepared(OlRecommondSong data) {
							if (data != null) {
								try {
									NetCache.saveCache(data,
											WebServiceUrl.BAOYUE_URL_1);
								} catch (IOException e) {
									// e.printStackTrace();
								}
								//synchronized (SYNC_LOCK) {
									loadRecommendCache(data, NEXT_FLAG);
									NEXT_FLAG = true;
								//}
							} else {
								Lg.d("loadRecommondDataFromServer() == null");
								isLoadingMore = false;
								return;
							}
						}
					}, "http://120.197.95.48:8083/Music?businessType=07&bygdType=1",EasouOnlineDialog.serviceId_one);
		}
	}

	private void loadRecommendCache(OlRecommondSong bean, boolean isNextPage) {
		pageData = bean;
		if (pageData == null)
			return;
		isLoadingMore = false;

		adapter.setDatas(pageData.getDataList(), isNextPage);
		adapter.notifyDataSetChanged();
		if(CmMusicSearch.getInstance().isLastPage("by")){
			setFootviewState(false, false);
		}else{
			setFootviewState(false, true);
		}
	}

	/**
	 * 从服务器加载Banner数据
	 */
	private void loadBannerDataFromServer() {
		OnlineMusicManager.getInstence().getBannerData(this,
				new OnDataPreparedListener<RecommendBanner>() {
					@Override
					public void onDataPrepared(RecommendBanner data) {
						if (data != null) {
							loadBannerCache(data);
							try {
								NetCache.saveCache(data, WebServiceUrl.BANNER);
							} catch (IOException e) {
								// e.printStackTrace();
							}
						} else {
							Lg.d("loadBannerDataFromServer() == null");
							return;
						}
					}
				}, WebServiceUrl.BANNER);
	}

	private void loadBannerCache(RecommendBanner bean) {
		if (bean == null || bean.getAdList() == null
				|| bean.getAdList().size() <= 0)
			return;
		bannerView = new ArrayList<Banner>();
		for (ServerBanner serverBanner : bean.getAdList()) {
			Banner banner = new Banner();
			banner.setOrder(serverBanner.getOrderNum());
			banner.setImageURL(serverBanner.getImg());
			if (serverBanner.getType() == 3) {
				banner.setBannerType(BannerType.Omnibus);
				banner.setResource(serverBanner.getTyId() + ""); // 资源为精选集id
			} else if (serverBanner.getType() == 4) {
				banner.setBannerType(BannerType.POSTER);
				banner.setResource(serverBanner.getUrl()); // 资源为广告链接
			} else if (serverBanner.getType() == 5) {
				banner.setBannerType(BannerType.PLUGIN);
				banner.setResource(serverBanner.getUrl()); // 资源为插件地址
			}
			bannerView.add(banner);
		}
		mBannerAdapter.setData(bannerView);
		UserData.getInstence().setBannerCount(bannerView.size());
		initPointer(bannerView.size());
	}

	private void initPointer(final int count) {
		final ImageView[] imageViews = new ImageView[count];
		pointContainer.removeAllViews();
		for (int i = 0; i < imageViews.length; i++) {
			imageViews[i] = new ImageView(this);
			imageViews[i]
					.setBackgroundResource(R.drawable.online_recommend_pot_default_img);
			LayoutParams lp = new LayoutParams(-2, -2);
			lp.rightMargin = 10;
			pointContainer.addView(imageViews[i], lp);
		}
		if (imageViews.length > 0 && imageViews[0] != null) {
			imageViews[0]
					.setBackgroundResource(R.drawable.online_recommend_pot_bright_img);
		}
		mBannerGallery.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				imageViews[position % count == 0 ? count - 1 : position % count
						- 1]
						.setBackgroundResource(R.drawable.online_recommend_pot_default_img);
				imageViews[position % count]
						.setBackgroundResource(R.drawable.online_recommend_pot_bright_img);
				imageViews[position % count == count - 1 ? 0 : position % count
						+ 1]
						.setBackgroundResource(R.drawable.online_recommend_pot_default_img);
			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		mBannerGallery.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Banner banner = (Banner) view.getTag();
				operateBannerClick(banner);
				mBannerGallery.cancelAutoSlide();
				mBannerGallery.startAutoSlide();
			}
		});
		mBannerGallery.setSelection((int) (Integer.MAX_VALUE / 2)
				- (Integer.MAX_VALUE / 2) % count);
		mBannerGallery.cancelAutoSlide();
		mBannerGallery.startAutoSlide();
		mBannerAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mLocalActivityManager.dispatchResume();

		// if (mBannerAdapter != null) {
		// mBannerGallery.startAutoSlide();
		// mBannerAdapter.notifyDataSetChanged();
		// }
	}

	@Override
	protected void onPause() {
		super.onPause();
		mLocalActivityManager.dispatchPause(isFinishing());
		if (mBannerAdapter != null) {
			mBannerGallery.cancelAutoSlide();
			mBannerAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * 点击banner后的处理动作
	 * 
	 * @param type
	 */
	public void operateBannerClick(Banner banner) {
		boolean hasNetwork = CommonUtils.isHasNetwork(this);
		if (!hasNetwork) {
			Toast.makeText(this, R.string.no_network, 0).show();
			return;
		}
		switch (banner.getBannerType()) {
		case Omnibus: // 精选集
			Intent omnibusIntent = new Intent(
					IntentAction.INTENT_ONLINE_FEATRUED_SET_MUSCI_LIST_ACTIVITY);
			omnibusIntent.putExtra(BaseActivity.INTENT_ACTIVITY_NAME,
					"OmnibusDetailActivity");

			if (TianlApp.activityBundles != null) {
				Bundle bundle = new Bundle();
				bundle.putInt(OmnibusDetailActivity.KEY_SEARCH,
						Integer.valueOf(banner.getResource()));
				bundle.putString(OmnibusDetailActivity.KEY_BACK_ACTION,
						IntentAction.INTENT_ONLINE_ACTIVITY);
				bundle.putString(OmnibusDetailActivity.KEY_ACTIVITY_NAME,
						"OnlineActivity");
				bundle.putString(OmnibusDetailActivity.KEY_BACK_SEARCH,
						"recommond");
				bundle.putBoolean(OmnibusDetailActivity.KEY_BACK_BTN, true);
				TianlApp.activityBundles
						.put(IntentAction.INTENT_ONLINE_FEATRUED_SET_MUSCI_LIST_ACTIVITY,
								bundle);
			}
			int level = TianlApp.newInstance().getPageLevel();
			BaseActivity.newInstance().showActivity(omnibusIntent, level + 1);
			break;
		case POSTER: // 广告
			Intent adIntent = new Intent(
					IntentAction.INTENT_ONLINE_WEBVIEW_ACTIVITY);
			adIntent.putExtra(WebViewActivity.URL, banner.getResource());
			startActivity(adIntent);
			break;
		case PLUGIN: // 插件

			break;
		}
	}

	public void loadPlugin() {

		AssetManager asset = getAssets();
		String[] list = null;
		try {
			list = asset.list("apks");
			for (String apkName : list) {
				File dex = getDir("dex", Context.MODE_PRIVATE);
				dex.mkdir();
				File apkFile = new File(dex, apkName);
				InputStream fis = getAssets().open("apks/" + apkName);
				FileOutputStream fos = new FileOutputStream(apkFile);
				byte[] buffer = new byte[1024];
				int len;
				while ((len = fis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}
				fis.close();
				fos.close();
				File outdex = getDir("outdex", Context.MODE_PRIVATE);
				outdex.mkdir();
				DexClassLoader mDexClassLoader = new DexClassLoader(
						apkFile.getAbsolutePath(), outdex.getAbsolutePath(),
						null, TianlApp.originalClassLoader.getParent());
				TianlApp.easouClassLoader = mDexClassLoader;
				Intent intent1 = new Intent("插件意图，要以Activity的className结尾");
				intent1.putExtra(BaseActivity.INTENT_ACTIVITY_NAME,
						"SampleActivity");
			}
		} catch (IOException e) {
			// e.printStackTrace();
			TianlApp.easouClassLoader = null;
		}
	}

	/**
	 * 设置ListView Footer
	 * 
	 * @param isLoading
	 *            是否是加载数据
	 * @param isHaveData
	 *            是否还能加载下一页
	 */
	private static void setFootviewState(boolean isLoading, boolean isHaveData) {
//		if (isLoading) {
//			//alertTV.setVisibility(View.GONE);
//			loadingPB.setVisibility(View.VISIBLE);
//		} else {
//			//alertTV.setVisibility(View.VISIBLE);
//			loadingPB.setVisibility(View.GONE);
//		}
//		if (!isHaveData) {
//			alertTV.setVisibility(View.GONE);
//		}else{
//			alertTV.setVisibility(View.VISIBLE);
//		}
	}


	private View.OnClickListener loadingMoreListener=new View.OnClickListener() {
		@Override
		public void onClick(View v){
			alertTV.setVisibility(View.GONE);
			loadingPB.setVisibility(View.VISIBLE);
			//getSearchSongFromNet(key,currentPage+1);
			loadRecommondDataFromServer(true);
		}
	};

	@Override
	public void onClick(View v) {
		//return;
		 
		switch(v.getId()){
		case R.id.one_vip:
			if (oneList.getVisibility() == View.VISIBLE) {
				oneList.setVisibility(View.GONE);
			} else {
				initNav();
				oneList.setVisibility(View.VISIBLE);
				if (oneList.getChildCount() == 0) {
					addProgress(oneList);
					Thread t = new Thread(
							new BackGroundThread(
									"http://120.197.95.48:8083/Music?businessType=07&bygdType=1",
									EasouOnlineDialog.serviceId_one));
					t.start();
				}
			}
			break;
		case R.id.four_vip:
			if (fourList.getVisibility() == View.VISIBLE) {
				fourList.setVisibility(View.GONE);
			} else {
				initNav();
				fourList.setVisibility(View.VISIBLE);
				if (fourList.getChildCount() == 0) {
					addProgress(fourList);
					Thread t = new Thread(
							new BackGroundThread(
									"http://120.197.95.48:8083/Music?businessType=07&bygdType=4",
									EasouOnlineDialog.serviceId_four));
					t.start();
				}
			}
			break;
		case R.id.six_vip:
			if (sixList.getVisibility() == View.VISIBLE) {
				sixList.setVisibility(View.GONE);
			} else {
				initNav();
				sixList.setVisibility(View.VISIBLE);
				if (sixList.getChildCount() == 0) {
					addProgress(sixList);
					Thread t = new Thread(
							new BackGroundThread(
									"http://120.197.95.48:8083/Music?businessType=07&bygdType=6",
									EasouOnlineDialog.serviceId_six));
					t.start();
				}
			}
			break;
		case R.id.eight_vip:
			if (eightList.getVisibility() == View.VISIBLE) {
				eightList.setVisibility(View.GONE);
			} else {
				initNav();
				eightList.setVisibility(View.VISIBLE);
				if (eightList.getChildCount() == 0) {
					addProgress(eightList);
					Thread t = new Thread(
							new BackGroundThread(
									"http://120.197.95.48:8083/Music?businessType=07&bygdType=8",
									EasouOnlineDialog.serviceId_eight));
					t.start();
				}
			}
			break;
		case R.id.ten_vip:
			if (tenList.getVisibility() == View.VISIBLE) {
				tenList.setVisibility(View.GONE);
			} else {
				initNav();
				tenList.setVisibility(View.VISIBLE);
				if (tenList.getChildCount() == 0) {
					addProgress(tenList);
					Thread t = new Thread(
							new BackGroundThread(
									"http://120.197.95.48:8083/Music?businessType=07&bygdType=10",
									EasouOnlineDialog.serviceId_ten));
					t.start();
				}
			}
			break;
		}
		
	}	
	public void initNav(){
		oneList.setVisibility(View.GONE);
		fourList.setVisibility(View.GONE);
		sixList.setVisibility(View.GONE);
		eightList.setVisibility(View.GONE);
		tenList.setVisibility(View.GONE);
	}
		
	public void fixListViewHeight(ListView listView) {  
	        // 如果没有设置数据适配器，则ListView没有子项，返回。  
	        ListAdapter listAdapter = listView.getAdapter();  
	        int totalHeight = 0;   
	        if (listAdapter == null) {   
	            return;   
	        }   
	        for (int i=0; i <listAdapter.getCount(); i++) {     
	            View listViewItem = listAdapter.getView(i , null, listView);
	            // 计算子项View 的宽高   
	            listViewItem.measure(0, 0);    
	            // 计算所有子项的高度和
	            totalHeight += listViewItem.getMeasuredHeight();    
	        }   
	        ViewGroup.LayoutParams params = listView.getLayoutParams();   
	        // listView.getDividerHeight()获取子项间分隔符的高度   
	        // params.height设置ListView完全显示需要的高度    
	        params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));   
	        listView.setLayoutParams(params);   
	    }   
	  public void addProgress(LinearLayout lay){
		  	LinearLayout line = new LinearLayout(this);
			line.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT,CommonUtils.dip2px(MonthVipActivity.this,60)));
			line.setBackgroundColor(Color.rgb(234,246,254));
			line.setGravity(Gravity.CENTER_VERTICAL);
			ProgressBar bar = new ProgressBar(MonthVipActivity.this,
					null, android.R.attr.progressBarStyle);
			bar.setLayoutParams(new LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT, CommonUtils.dip2px(MonthVipActivity.this,60)));
			line.addView(bar);
			lay.addView(line);
	  }
}
