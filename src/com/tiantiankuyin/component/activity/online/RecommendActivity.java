package com.tiantiankuyin.component.activity.online;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.LocalActivityManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cmsc.cmmusic.common.MusicQueryInterface;
import com.cmsc.cmmusic.common.data.MusicInfo;
import com.cmsc.cmmusic.common.data.MusicInfoResult;
import com.tiantiankuyin.adapter.BannerAdapter;
import com.tiantiankuyin.adapter.OnlineMusicListAdapter;
import com.tiantiankuyin.bean.Banner;
import com.tiantiankuyin.bean.Banner.BannerType;
import com.tiantiankuyin.bean.OlRecommondSong;
import com.tiantiankuyin.bean.OlSongVO;
import com.tiantiankuyin.bean.PagerBean;
import com.tiantiankuyin.bean.RecommendBanner;
import com.tiantiankuyin.bean.RecommendBanner.ServerBanner;
import com.tiantiankuyin.component.activity.BaseActivity;
import com.tiantiankuyin.component.activity.WebViewActivity;
import com.tiantiankuyin.component.activity.local.cache.MusicLocalCache;
import com.tiantiankuyin.database.bll.OnDataPreparedListener;
import com.tiantiankuyin.database.bll.OnlineMusicManager;
import com.tiantiankuyin.net.CmMusicSearch;
import com.tiantiankuyin.net.NetCache;
import com.tiantiankuyin.para.IntentAction;
import com.tiantiankuyin.para.UserData;
import com.tiantiankuyin.para.WebServiceUrl;
import com.tiantiankuyin.play.PlayLogicManager;
import com.tiantiankuyin.utils.BeanUtils;
import com.tiantiankuyin.utils.CommonUtils;
import com.tiantiankuyin.utils.Lg;
import com.tiantiankuyin.view.EasouBannerGallery;
import com.tiantiankuyin.R;
import com.tiantiankuyin.TianlApp;

import dalvik.system.DexClassLoader;

/**
 * 在线模块-推荐
 * 
 * @author DK
 * 
 */
public class RecommendActivity extends Activity {

	public static final String ACTIVITY_ID = "RecommendActivity";
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
	private static View footerView;
	public static RecommendActivity instance;
	/** 是否正在加载更多 */
	public static boolean isLoadingMore=false;
	public Context context;
	public static final String PATH="/HighMusic/music"	;

	private static TextView alertTV;
	private static ProgressBar loadingPB;
	
	private ImageView mimuMusic;
	private TextView textView,mianze;
	
	private int currentImgIndex=0;	
	
	private int[] imgageSourse = {R.drawable.baoyue_01};
	
	private static PagerBean cachePagerBean = new PagerBean();
	
	private static boolean isSearchMusicing = false;
	
	private static final String LOCKED = "LOCKED";
	
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
		
		 File file=new File(Environment.getExternalStorageDirectory()+PATH);
		 try {
			 if(!file.exists()){
             	file.mkdirs();
             }
			file.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Runnable img = new Runnable() {
			public void run() {
				//if(currentImgIndex>2){
					currentImgIndex = 0;
				//}
				mimuMusic.setImageResource(imgageSourse[currentImgIndex]);
				currentImgIndex++;
				mHandler.postDelayed(this, 5000);
			}
		};
		mHandler.postDelayed(img, 5000);
	}
	 private void showNormalDialog(){
	        /* @setIcon 设置对话框图标
	         * @setTitle 设置对话框标题
	         * @setMessage 设置对话框消息提示
	         * setXXX方法返回Dialog对象，因此可以链式设置属性
	         */
	         AlertDialog.Builder normalDialog = 
	            new AlertDialog.Builder(OnlineActivity.mOnlineActivity);
	        normalDialog.setIcon(R.drawable.logo);
	        normalDialog.setTitle("咪咕音乐"); 
	        normalDialog.setMessage("下载咪咕音乐客户端?");
	        normalDialog.setPositiveButton("确定", 
	            new DialogInterface.OnClickListener() {
	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	            	downLoadApk(context,"http://120.76.46.35:8181/entertain/MobileMusic503_014732W_cp95.apk");
	            }
	        });
	        normalDialog.setNegativeButton("关闭", 
	            new DialogInterface.OnClickListener() {
	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	            	
	            }
	        });
	        // 显示
	        normalDialog.show();
	    }
	 /**
	     * 该方法是调用了系统的下载管理器
	     */
	    @SuppressLint("NewApi")
		public void downLoadApk(Context context,String url){
	        /**
	         * 在这里返回的 reference 变量是系统为当前的下载请求分配的一个唯一的ID，
	         * 我们可以通过这个ID重新获得这个下载任务，进行一些自己想要进行的操作
	         * 或者查询下载的状态以及取消下载等等
	         */
	        Uri uri = Uri.parse(url);        //下载连接
	        DownloadManager manager = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);  //得到系统的下载管理
	        Request requestApk = new DownloadManager.Request(uri);  //得到连接请求对象
	        requestApk.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI); //指定在什么网络下进行下载，这里我指定了WIFI网络
	        requestApk.setDestinationInExternalPublicDir(context.getPackageName()+"/myDownLoad","xiaoyuantong.apk");  //制定下载文件的保存路径，我这里保存到根目录
	        requestApk.setVisibleInDownloadsUi(true);  //设置显示下载界面
	        requestApk.allowScanningByMediaScanner();  //表示允许MediaScanner扫描到这个文件，默认不允许。
	        requestApk.setTitle("咪咕音乐");      //设置下载中通知栏的提示消息
	        requestApk.setDescription("咪咕音乐");//设置设置下载中通知栏提示的介绍
	        long downLoadId = manager.enqueue(requestApk);               //启动下载,该方法返回系统为当前下载请求分配的一个唯一的ID
	    }
	private void findView() {
		recommendLV = (ListView) findViewById(R.id.recommendLV);
		headerView = LayoutInflater.from(this).inflate(
				R.layout.online_recommend_listview_header, null);
		mimuMusic = (ImageView)headerView.findViewById(R.id.mimu_music);
		mimuMusic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				  //System.out.println("mimuMusic ==================");
				showNormalDialog();
			}
		});
		textView = (TextView)headerView.findViewById(R.id.recommend_text_view);
		mianze = (TextView)headerView.findViewById(R.id.mianze);
		mianze.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				 Uri uri = Uri.parse("http://218.98.35.163/mp3/wzdmse.htm");  
				 Intent it = new Intent(Intent.ACTION_VIEW, uri);  
				 startActivity(it);
			}
		});
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
		//recommendLV.setOnItemClickListener(null);
		adapter = new OnlineMusicListAdapter(OnlineActivity.mOnlineActivity);
		recommendLV.setAdapter(adapter);
		recommendLV.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				initPlayState();
				PlayLogicManager.newInstance().stop();
				TextView songId=(TextView)view.findViewById(R.id.songId);
				TextView songName=(TextView)view.findViewById(R.id.musicName);
				TextView artView=(TextView)view.findViewById(R.id.musicArtist);
				ImageView playState=(ImageView)view.findViewById(R.id.play_status);
				playState.setVisibility(View.VISIBLE);
				MusicInfoResult  infoRes=MusicQueryInterface.getMusicInfoByMusicId(RecommendActivity.this,songId.getText().toString());
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
		boolean cacheflag = false;
		try {
			OlRecommondSong songBean = NetCache
					.readCache(WebServiceUrl.RECOMMEND_URL);
			if(null!=songBean && null!=songBean.getDataList()){
				MusicLocalCache.tjPageData = songBean;
				cachePagerBean = new PagerBean();
				cachePagerBean.setCountRowNumber(MusicLocalCache.tjPageData.getDataList().size());
				if(cachePagerBean.getCountPageNumber()>1){
					loadRecommendCache(getCacheDataPager(cachePagerBean), true);
				}else{
					loadRecommendCache(songBean, false);
				}
				cacheflag = true;
			}
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
			mHandler.post(new Runnable() {
				public void run() {
					boolean created = UserData.getInstence()
							.isLoadedRecommondDataFromServer();
					loadRecommondDataFromServer(!created);
					UserData.getInstence().setLoadedRecommondDataFromServer(true);
				}
			});
		}		
	}

	public void initPlayState(){
		for(int i=0;i<recommendLV.getChildCount();i++){
			View view=recommendLV.getChildAt(i).findViewById(R.id.play_status);
			if(view!=null){
				view.setVisibility(View.GONE);
			}
		}
	}
	/**
	 * 从服务器加载新歌榜单数据
	 */
	private void loadRecommondDataFromServer(final boolean needNext) {
		
		CmMusicSearch search = CmMusicSearch.getInstance();
		//缓存数据
		try {
				List<MusicInfo> musics = search.getMusicInfos(getApplicationContext(),"tj");
				List<OlSongVO> songVoes = BeanUtils.convenrtMusicInfoTOOlSongVO(musics);			
				if(null==MusicLocalCache.tjPageData.getDataList()){
					MusicLocalCache.tjPageData.setDataList(songVoes);
				}else{					
					MusicLocalCache.tjPageData.getDataList().addAll(songVoes);
				}
				NetCache.saveCache(MusicLocalCache.tjPageData, WebServiceUrl.RECOMMEND_URL);
		} catch (Exception e) {
			e.printStackTrace();
		}
		loadRecommendCache(MusicLocalCache.tjPageData, false);
	}

	private void loadRecommendCache(OlRecommondSong bean, boolean isNextPage) {
		try{
			if (bean == null || null==bean.getDataList() || 0>=bean.getDataList().size()){
				setFootviewState(false, false);
				return;
			}
			isLoadingMore = false;
			adapter.setDatas(bean.getDataList(), isNextPage);
			adapter.notifyDataSetChanged();
			//如果是最后一页则不再显示加载更多
			if((cachePagerBean.getCountPageNumber()-1)<=cachePagerBean.getCurrentPageNumber()&&
					(CmMusicSearch.getInstance().isLastPage("tj") || MusicLocalCache.tjPageData.isLastPage())){
				setFootviewState(false, false);
			}else{
				setFootviewState(false, true);
			}
		}catch(Exception ex){
			
		}finally{
			isSearchMusicing = false;
		}
	}
	
	//从缓存集合中进行分页
	private OlRecommondSong getCacheDataPager(PagerBean pagerBean){
		OlRecommondSong cacheSong = new OlRecommondSong();
		List<OlSongVO> songList = new ArrayList<OlSongVO>();
		cacheSong.setDataList(songList);
		int startIndex = pagerBean.getCurrentPageNumber()*pagerBean.getPageRowNumber();
		int endIndex = pagerBean.getCurrentPageNumber()*pagerBean.getPageRowNumber()+pagerBean.getPageRowNumber();
		if(MusicLocalCache.tjPageData.getDataList().size()<=startIndex){
			return cacheSong;
		}
		for (int i = 0; i < MusicLocalCache.tjPageData.getDataList().size(); i++) {
			if((i+1)>startIndex && (i+1)<=endIndex){
				songList.add(MusicLocalCache.tjPageData.getDataList().get(i));
			}
		}
		return cacheSong;
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
		if (isLoading) {
			//alertTV.setVisibility(View.GONE);
			loadingPB.setVisibility(View.VISIBLE);
		} else {
			//alertTV.setVisibility(View.VISIBLE);
			loadingPB.setVisibility(View.GONE);
		}
		if (!isHaveData) {
			alertTV.setVisibility(View.GONE);
		}else{
			alertTV.setVisibility(View.VISIBLE);
		}
		
		if(!isLoading && !isHaveData){
			footerView.setVisibility(View.GONE);
		}else{
			footerView.setVisibility(View.VISIBLE);
		}
	}

	/*@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// 无网络时提示
		boolean hasNetwork = CommonUtils.isHasNetwork(this);
		if (!hasNetwork) {
			Toast.makeText(this, R.string.no_network, 0).show();
		
			return;
		}
		if (adapter.getmMusicList() == null || pageData == null)
			return;
		if (position == adapter.getmMusicList().size() + 1) { // 如果是页脚
			if (Integer.valueOf(pageData.getThisPage()) >= Integer // 如果是最后一页了
					.valueOf(pageData.getCountPage())) {
				setFootviewState(true, false);
			} else {
				setFootviewState(true, true);
			}
		if (!isLoadingMore)
				loadRecommondDataFromServer(true);
		} else {

			MusicHolder holder = (MusicHolder) view.getTag();
			if (holder == null)
				return;
			final OlSongVO song = (OlSongVO) holder.musicID.getTag();
			if (song == null)
				return;

			boolean isPay = SPHelper.newInstance().getPayInfo(song.getGid());
			if (isPay) {
				Intent intent=new Intent();
				intent.setClass(RecommendActivity.this, SongPayInfoAcitivty.class);
				intent.putExtra("songName",song.getSong() );
				startActivity(intent);
				OnlineActivity.PlayOnlineMusic(adapter.getmMusicList(),
						position);
			} else {
				Intent intent=new Intent();
				intent.setClass(RecommendActivity.this, SongPayInfoAcitivty.class);
				intent.putExtra("songName",song.getSong() );
				startActivity(intent);
				EasouPay.toPay(OnlineActivity.mOnlineActivity, song.getGid());
			}
		}
	}*/

	private View.OnClickListener loadingMoreListener=new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			synchronized (LOCKED) {
				if(isSearchMusicing){
					return;
				}
			}
			isSearchMusicing = true;		
			if((cachePagerBean.getCountPageNumber()-1)<=cachePagerBean.getCurrentPageNumber()&&
					(CmMusicSearch.getInstance().isLastPage("tj") || MusicLocalCache.tjPageData.isLastPage())){
				return;
			}			
			alertTV.setVisibility(View.GONE);
			loadingPB.setVisibility(View.VISIBLE);
			//因为从缓存分页时 从0页开始所以这里判断是否已经在最后一页需要在总页数上减1
			if((cachePagerBean.getCountPageNumber()-1)>cachePagerBean.getCurrentPageNumber()){
				cachePagerBean.setCurrentPageNumber(cachePagerBean.getCurrentPageNumber()+1);
				loadRecommendCache(getCacheDataPager(cachePagerBean), true);
			}else{
				loadRecommondDataFromServer(true);
			}
			
		}
	};	
}
