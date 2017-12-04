package com.tiantiankuyin.component.activity.online;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cmsc.cmmusic.common.data.QueryResult;
import com.tiantiankuyin.adapter.BannerAdapter;
import com.tiantiankuyin.adapter.OnlineMusicListAdapter;
import com.tiantiankuyin.adapter.OnlineMusicListAdapter.MusicHolder;
import com.tiantiankuyin.bean.Banner;
import com.tiantiankuyin.bean.OlRecommondSong;
import com.tiantiankuyin.bean.OlSingerVO;
import com.tiantiankuyin.bean.OlSongVO;
import com.tiantiankuyin.bean.RecommendBanner;
import com.tiantiankuyin.bean.Banner.BannerType;
import com.tiantiankuyin.bean.RecommendBanner.ServerBanner;
import com.tiantiankuyin.component.activity.BaseActivity;
import com.tiantiankuyin.component.activity.SongPayInfoAcitivty;
import com.tiantiankuyin.component.activity.WebViewActivity;
import com.tiantiankuyin.database.bll.OnDataPreparedListener;
import com.tiantiankuyin.database.bll.OnlineMusicManager;
import com.tiantiankuyin.net.CmMusicSearch;
import com.tiantiankuyin.net.NetCache;
import com.tiantiankuyin.para.IntentAction;
import com.tiantiankuyin.para.SPHelper;
import com.tiantiankuyin.para.UserData;
import com.tiantiankuyin.para.WebServiceUrl;
import com.tiantiankuyin.utils.CommonUtils;
import com.tiantiankuyin.utils.Lg;
import com.tiantiankuyin.view.EasouBannerGallery;
import com.tiantiankuyin.view.EasouOnlineDialog;
import com.tiantiankuyin.R;
import com.tiantiankuyin.TianlApp;
//import com.umeng.analytics.MobclickAgent;




import dalvik.system.DexClassLoader;

public class SimilarActivity extends Activity {
	public static final String ACTIVITY_ID = "SimilarActivity";
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
	/** 新歌推荐列表 */
	private ListView recommendLV;
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
	public static SimilarActivity instance;
	/** 用于保存页面数据 */
	public static OlRecommondSong pageData;
	/** 是否正在加载更多 */
	public static boolean isLoadingMore;
	
	public Context context;

	private static TextView alertTV;
	private static ProgressBar loadingPB;
	
	private ImageView mimuMusic;
	
	private int currentImgIndex=0;	
	
	private int[] imgageSourse = {R.drawable.baoyue_01};	
	
	public static String TEL_NO;

	//获取包月歌单时使用
	public static boolean NEXT_FLAG=false;
	
	public static String SYNC_LOCK="LOCK";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.online_recommend);
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
				currentImgIndex++;
				mHandler.postDelayed(this, 5000);
			}
		};
		mHandler.postDelayed(img, 5000);		
	}

	private void findView() {
		recommendLV = (ListView) findViewById(R.id.recommendLV);
		headerView = LayoutInflater.from(this).inflate(
				R.layout.online_recommend_listview_header, null);
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
		recommendLV.addHeaderView(headerView);
		recommendLV.setFocusableInTouchMode(true);
		android.widget.AbsListView.LayoutParams lp = new android.widget.AbsListView.LayoutParams(
				-1, CommonUtils.dip2px(this, 55));
		footerView.setLayoutParams(lp);
		recommendLV.addFooterView(footerView, null, true);
		//recommendLV.setOnItemClickListener(this);
		adapter = new OnlineMusicListAdapter(OnlineActivity.mOnlineActivity);
		recommendLV.setAdapter(adapter);
		boolean cacheflag = false;
		try {
			OlRecommondSong songBean2 = NetCache
					.readCache(WebServiceUrl.BAOYUE_URL_2);
			loadRecommendCache(songBean2, true);
			cacheflag = true;
		} catch (ClassCastException e) {
			// e.printStackTrace();
		} catch (IOException e) {
			// e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// e.printStackTrace();
		}
		
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
		if(!cacheflag){
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
		}
	}

	/**
	 * 从服务器加载新歌榜单数据
	 */
	private void loadRecommondDataFromServer(final boolean needNext) {
		isLoadingMore = true;
		CmMusicSearch search = CmMusicSearch.getInstance();
		QueryResult queryResult2 = search.queryCPMonth(context, EasouOnlineDialog.serviceId_four);
		
		if(StringUtils.equals("000000", queryResult2.getResCode())){
			if(StringUtils.isEmpty(TEL_NO)){
				TEL_NO = queryResult2.getMobile();
			}
			OnlineMusicManager.getInstence().getRecommondData(this,
					new OnDataPreparedListener<OlRecommondSong>() {
						@Override
						public void onDataPrepared(OlRecommondSong data) {
							if (data != null) {
								try {
									NetCache.saveCache(data,
											WebServiceUrl.BAOYUE_URL_2);
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
					}, "http://120.197.95.48:8083/Music?businessType=07&bygdType=4",EasouOnlineDialog.serviceId_four);
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
		public void onClick(View v) {
			alertTV.setVisibility(View.GONE);
			loadingPB.setVisibility(View.VISIBLE);
			//getSearchSongFromNet(key,currentPage+1);
			loadRecommondDataFromServer(true);
		}
	};	
}
