package com.tiantiankuyin.component.activity.online;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.List;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tiantiankuyin.adapter.OnlineMusicListAdapter;
import com.tiantiankuyin.bean.OlAlbumList;
import com.tiantiankuyin.bean.OlSongVO;
import com.tiantiankuyin.component.activity.BaseActivity;
import com.tiantiankuyin.component.service.DownloadFile;
import com.tiantiankuyin.component.service.DownloadService;
import com.tiantiankuyin.component.service.DownloadFile.DownloadType;
import com.tiantiankuyin.database.bll.LocalMusicManager;
import com.tiantiankuyin.database.bll.OnDataPreparedListener;
import com.tiantiankuyin.database.bll.OnlineMusicManager;
import com.tiantiankuyin.net.EasouAsyncImageLoader;
import com.tiantiankuyin.net.NetCache;
import com.tiantiankuyin.net.EasouAsyncImageLoader.ILoadedImage;
import com.tiantiankuyin.para.Env;
import com.tiantiankuyin.para.IntentAction;
import com.tiantiankuyin.para.SPHelper;
import com.tiantiankuyin.play.PlayLogicManager;
import com.tiantiankuyin.utils.CommonUtils;
import com.tiantiankuyin.utils.Lg;
import com.tiantiankuyin.view.AnimImageView;
import com.tiantiankuyin.view.EasouShareDialog;
import com.tiantiankuyin.R;
import com.tiantiankuyin.TianlApp;

/**
 * 在线模块-精选集详情页
 * 
 * @author Erica
 * 
 */
public class OmnibusDetailActivity extends Activity implements OnClickListener,
		OnItemClickListener , OnScrollListener{

	/** 日志打印标志位 */
	public static final String ACTIVITY_ID = "OmnibusDetailActivity";
	/** 绑定传输数据 标题名 */
	public static final String KEY_TITLE = "TITLE_NAME";
	/** 搜索关键字 */
	public static final String KEY_SEARCH = "SEARCH_NAME";
	/** 标签名 */
	public static final String KEY_TAG = "TAG_NAME";
	/** 精选集介绍 */
	public static final String KEY_INTRO = "INTRO_STRING";
	/** 精选集图片 */
	public static final String KEY_IMAGE = "IMAGE_NAME";
	/** 是否需要返回键 */
	public static final String KEY_BACK_BTN = "BACK_BTN";
	/** 返回键操作 */
	public static final String KEY_BACK_ACTION = "BACK_ACTION";
	/** 返回 搜索字符串*/
	public static final String KEY_BACK_SEARCH = "BACK_SEARCH";
	/** 当前操作Activity对象 */
	public static final String KEY_ACTIVITY_NAME = "ACTIVITY_NAME";
	/** 加载完成 */
	public static final int LOAD_COMPLETE = 0x1;
	/** 动画显示完毕 */
	public static final int POPUP_WINDOW_DISMISS = 0x2;

	/** 界面控制对象 */
	private LocalActivityManager mLocalActivityManager;
	/** 搜索字符串 */
	private int mSearch_Id;
	/** 返回 搜索字符串 */
	private String mSearch_Str;
	/** 标题名 */
	private String titleName;
	/** 标签对象 */
	private String mTagText;
	/** 简介 */
	private String mIntroText;
	/** 精选集图片 */
	private String mImage;
	/** 精选集图片对象 */
	private ImageView mDetailImage;
	/** 歌曲列表对象 */
	private ListView musicLV;
	/** 标签对象TextView */
	private TextView mTagTextView;
	/** 简介对象 TextView */
	private TextView mIntroContent;
	/** 当前精选集简介控制句柄 */
	private LinearLayout mControlLayout;
	/** 分享按钮 */
	private LinearLayout mShareLayout;
	/** 预约按钮 */
	private LinearLayout mCallforLayout;
	/** 返回键按钮 */
	private ImageButton backBtn;
	/** 歌曲列表适配对象 */
	private OnlineMusicListAdapter mAdapter;
	/** 消息处理对象 */
	private static Handler mHandler;
	/** 返回操作对象 */
	private String backAction;
	/** 当前操作Activity对象名 */
	private String activityName;
	/** 基础View对象 */
	private static View rootView;
	/** 当前精选集数据集合对象 */
	private OlAlbumList mOlAlbumList;
	/** 加载界面图片对象 */
	private ILoadedImage iLoadedImage;
	/** 当前显示页对象 */
	private int mPage;
    /** 显示动画的组件   */ 
    private AnimImageView imgDance; 
	/** 界面精选集*/
	private LinearLayout featured_detail_layout;

	/** 显示加载更多 */ 
	private View footerView;  
	
	private static PopupWindow pop;

	public boolean isAppend = false;
	
	private String url = null;
	
	private int screenWidth;
	private LinearLayout no_network_tips;//无网络提示
	private Button fresh;//无网络刷新button;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (TianlApp.activityBundles == null)
			return;
		if (TianlApp.newInstance().activityList != null
				&& !TianlApp.newInstance().activityList.contains(this)) {
			TianlApp.newInstance().activityList.add(this); // 将该Activity实例加入到Activity管理集合中
		}
		Bundle bundle = TianlApp.activityBundles
				.get(IntentAction.INTENT_ONLINE_FEATRUED_SET_MUSCI_LIST_ACTIVITY);
		if (bundle.getString(KEY_TITLE) != null) {
			titleName = bundle.getString(KEY_TITLE);
		}else{
			titleName = this.getResources().getString(R.string.online_featured_set_title);
		}
		if (bundle.getInt(KEY_SEARCH) > 0) {
			mSearch_Id = bundle.getInt(KEY_SEARCH);
		}
		if (bundle.getString(KEY_BACK_SEARCH) != null) {
			mSearch_Str = bundle.getString(KEY_BACK_SEARCH);
		}
		if (bundle.getString(KEY_IMAGE) != null) {
			mImage = bundle.getString(KEY_IMAGE);
		}
		if (bundle.getString(KEY_TAG) != null) {
			mTagText = bundle.getString(KEY_TAG);
		}
		if (bundle.getString(KEY_INTRO) != null) {
			mIntroText = bundle.getString(KEY_INTRO);
		}
		boolean hasBackBtn = bundle.getBoolean(KEY_BACK_BTN);
		backAction = bundle.getString(KEY_BACK_ACTION);
		activityName = bundle.getString(KEY_ACTIVITY_NAME);

		rootView = LayoutInflater.from(this).inflate(
				R.layout.online_featured_set_detail, null);
		setContentView(rootView);
		CommonUtils.setTitle(rootView, titleName, hasBackBtn, false);
		screenWidth = Env.getScreenWidth();
		findView();
		if(mTagText==null||mTagText.length()==0){
			mTagText = this.getResources().getString(R.string.no_tag);
		}
		showControlView(mImage,mTagText,mIntroText);
		init();
	}

	/** 初始化界面对象 */
	private void findView() {
		mControlLayout = (LinearLayout) findViewById(R.id.online_feature_set_more_lay);
		mShareLayout = (LinearLayout) findViewById(R.id.online_featrued_share_lay);
		mCallforLayout = (LinearLayout) findViewById(R.id.online_featrued_callfor_lay);
		mDetailImage = (ImageView) findViewById(R.id.online_featured_set_detail_image);
		musicLV = (ListView) findViewById(R.id.online_set_detail_listview);
		mTagTextView = (TextView) findViewById(R.id.online_tiplist_txt);
		mIntroContent = (TextView) findViewById(R.id.online_featured_set_detail_content);
		backBtn = (ImageButton) findViewById(R.id.back_btn);
		
		musicLV.setFooterDividersEnabled(true);

		footerView = LayoutInflater.from(this).inflate(
				R.layout.online_recommend_listview_footer, null);
		android.widget.AbsListView.LayoutParams lp = new android.widget.AbsListView.LayoutParams(
				-1, CommonUtils.dip2px(this, 55));
		footerView.setLayoutParams(lp);
		musicLV.addFooterView(footerView, null, true);
		footerView.setVisibility(View.GONE);
		featured_detail_layout = (LinearLayout) findViewById(R.id.online_featured_lay);
		//loading_layout = (LinearLayout) findViewById(R.id.loading_lay);
		featured_detail_layout.setVisibility(View.GONE);
		//loading_layout.setVisibility(View.VISIBLE);
        imgDance = (AnimImageView) super.findViewById(R.id.ImgDance); 
        initNetError();
		
	}
	
	/**
	 * 初始化网络异常控件
	 */
	private void initNetError(){
		no_network_tips=(LinearLayout)findViewById(R.id.no_network_tips);
		fresh=(Button) findViewById(R.id.fresh);
		fresh.setOnClickListener(freshListener);
	}
	
	private View.OnClickListener freshListener=new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if(!CommonUtils.isHasNetwork(TianlApp.newInstance())){//无网络
				Toast.makeText(TianlApp.newInstance(), getString(R.string.has_no_net), Toast.LENGTH_SHORT).show();//提示当前无可用的网络
				return ;
			}else{		
				getSearchOmnibusData(150, mPage,20,false);
			}
			
		}
	};
	/**
	 * 设置ListView Footer
	 * 
	 * @param isLoading
	 *            是否是加载数据
	 * @param isHaveData
	 *            是否还能加载下一页
	 */
	private void setFootviewState(boolean isLoading, boolean isHaveData) {
		//TextView alertTV = (TextView) footerView.findViewById(R.id.loadMore);
		ProgressBar loadingPB = (ProgressBar) footerView
				.findViewById(R.id.recommondDataLoading);
		if (isLoading) {
			//alertTV.setVisibility(View.GONE);
			loadingPB.setVisibility(View.VISIBLE);
		} else {
			//alertTV.setVisibility(View.VISIBLE);
			loadingPB.setVisibility(View.GONE);
		}
		if (!isHaveData) {
			String result = getString(R.string.already_load)
					+ mAdapter.getmMusicList().size()+ getString(R.string.total_datas);
			//alertTV.setText(result);
		}else{
			//alertTV.setText(getString(R.string.loading_more));
		}
	}
	
	/** 头部加载刷新
	 * @author Erica
	 *  */
	public void showControlView(String _image,String _tag,String _intro){
		mImage = _image;
		mTagText = _tag;
		mIntroText = _intro;
		if (mImage != null) {
			mDetailImage.setTag(CommonUtils.MD5(mImage));
			iLoadedImage = new ILoadedImage() {
				@Override
				public void onFinishLoadedLRC(String lrcPath, String songName) {
				}

				@Override
				public void onFinishLoaded(SoftReference<Drawable> drawable, String saveName) {

					ImageView imageview = (ImageView) rootView
							.findViewWithTag(CommonUtils.MD5(mImage));
					if (drawable != null && imageview != null) {
						imageview.setImageDrawable(drawable.get());
						/** 动画淡隐效果 */
						Animation animation = AnimationUtils.loadAnimation(
								TianlApp.newInstance(), R.anim.push_in);
						if (animation != null) {
							imageview.startAnimation(animation);
						}
					}
				}

				@Override
				public void onError(Exception e) {
				}
			};
			EasouAsyncImageLoader.newInstance().loadImage(mImage, iLoadedImage,
					CommonUtils.MD5(mImage));
		}
		mTagTextView.setText(mTagText);
		mIntroText = CommonUtils.mestrStr(mIntroText, 12, 3, screenWidth/5*2);
		mIntroContent.setText(mIntroText);
	}

	/** 初始化控制对象并加载数据 */
	private void init() {
		mLocalActivityManager = new LocalActivityManager(this, true);
		mAdapter = new OnlineMusicListAdapter(this);
		mPage = 1;
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case LOAD_COMPLETE:
					if (mOlAlbumList != null) {
						List<OlSongVO> olSongVOs=mOlAlbumList.getDataList();
						 OlSongVO olSongVO1=olSongVOs.get(1);
						 olSongVO1.setSinger("范青");
						 olSongVO1.setSong("坎坷路上的寂寞");
						 olSongVO1.setLowPrtUrl("http://218.200.227.123/order/wap/in/0026055/600907000002303488/");
						 OlSongVO olSongVO5=olSongVOs.get(5);
						 olSongVO5.setSinger("夏丽云");
						 olSongVO5.setSong("贵妃醉酒选段清清冷落在广寒宫(京剧)");
						 olSongVO5.setLowPrtUrl("http://218.200.227.123/order/wap/in/0026055/600907000002303488/");

						 mAdapter.setDatas(olSongVOs, true);;
						if(imgDance.getVisibility()== View.VISIBLE){
							imgDance.setVisibility(View.GONE);
							imgDance.stop(); 
						}
						featured_detail_layout.setVisibility(View.VISIBLE);
						if(footerView.getVisibility()==View.GONE)
							footerView.setVisibility(View.VISIBLE);
						mAdapter.setDatas(mOlAlbumList.getDataList(),isAppend);
						if(mOlAlbumList.getMusicCount()>mPage*20){
							setFootviewState(false, true);
							mPage = mPage+1;
						} else {
							setFootviewState(false, false);
							mPage = mPage+1;
						}			
						if(mImage==null||mTagText ==null||mIntroText==null){
							if(mOlAlbumList.getImgUrl()!=null){
								mImage = mOlAlbumList.getImgUrl();
							}
							if(mOlAlbumList.getTag()!=null&&mOlAlbumList.getTag().trim().length()>0){
								mTagText = mOlAlbumList.getTag();
							}else{
								mTagText = TianlApp.newInstance().getResources().getString(R.string.no_tag);
							}
							if(mOlAlbumList.getIntro()!=null){
								mIntroText = mOlAlbumList.getIntro();
							}
							showControlView(mImage,mTagText,mIntroText);
						}
						
						mAdapter.notifyDataSetChanged();
						CommonUtils.setListViewHeightBasedOnChildren(musicLV,true,false);
						saveLocalData(url);
						isAppend = false;
					}
					break;
				case POPUP_WINDOW_DISMISS:
					if(pop.isShowing())
						pop.dismiss();
					break;
				}
			}
		};

		try {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					getSearchOmnibusData(150, mPage,20,false);
				}
			});
		} catch (Exception e) {
			//e.printStackTrace();
		}

		backBtn.setOnClickListener(this);
		musicLV.setAdapter(mAdapter);
		musicLV.setOnItemClickListener(this);
		//musicLV.setOnTouchListener(this);
		
		musicLV.setOnScrollListener(this);
		//mControlLayout.setOnTouchListener(this);
		mControlLayout.setOnClickListener(this);
		mShareLayout.setOnClickListener(this);
		mCallforLayout.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		//友盟统计 日志 add by perry 2012-10-23
	   // MobclickAgent.onResume(this);
		mLocalActivityManager.dispatchResume();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		itemClick(arg1,arg2);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.back_btn:
			back();
			break;
		case R.id.online_feature_set_more_lay:
			goToFeatruedSetIntro();
			break;
		case R.id.online_featrued_share_lay:
			//无网络时提示
			boolean hasNetwork = CommonUtils.isHasNetwork(OmnibusDetailActivity.this);
			if (!hasNetwork) {
				Toast.makeText(OmnibusDetailActivity.this, R.string.no_network, 0).show();
				return;
			}
			EasouShareDialog easou_share_dialog = new EasouShareDialog(this, R.style.easouDialog,"精选集："+titleName,null);
			easou_share_dialog.show();	
			break;
		case R.id.online_featrued_callfor_lay:
			downloadAll();
			break;
		case R.id.list_re_more:
			isAppend = true;
			getSearchOmnibusData(150, mPage,20,true);
			break;
		}
	}

	@Override
	public void onBackPressed() {
		back();
	}

	/** 条目单击处理操作 */
	public  void itemClick(View view,int index) {
		//无网络时提示
		boolean hasNetwork = CommonUtils.isHasNetwork(OmnibusDetailActivity.this);
		if (!hasNetwork) {
			Toast.makeText(OmnibusDetailActivity.this, R.string.no_network, 0).show();
			return;
		}
		if (index == mAdapter.getmMusicList().size() ) { // 如果是页脚
			if(mOlAlbumList.getMusicCount()>(mPage-1)*20){
				setFootviewState(true, true);
				isAppend = true;
				getSearchOmnibusData(150, mPage,20,true);
			}	
		}else{
			final OlSongVO song = (OlSongVO) mAdapter.getmMusicList().get(index);
			if (song == null)
				return;

			boolean isPay =  SPHelper.newInstance().getPayInfo(song.getGid());
			if(isPay){
				OnlineActivity.PlayOnlineMusic(mAdapter.getmMusicList(), index);
			}else{
//				EasouPay.toPay(OnlineActivity.mOnlineActivity, song.getGid());
			}
		}
	}

	/** 全部预约
	 * @author Erica
	 * @note 修改预约逻辑  10.8
	 *  */
	private boolean downloadAll(){
		if (CommonUtils.isHasNetwork(this)) {
			if(mAdapter.getmMusicList() ==null)
				return false;	
			int size = mAdapter.getmMusicList().size();
			int num_exist = 0;
			TianlApp.newInstance().setFromAll(true);
			TianlApp.newInstance().setWifiDownloadLock(false);
			for(int i = 0;i < size ;i++){
				OlSongVO olSongVO = mAdapter.getmMusicList().get(i);
				int quality = SPHelper.newInstance().getQuality();
				String url = null;
				String song_id = null;
				if (quality == 1) {
					if (olSongVO.getHighId() != null
							&& olSongVO.getHighId().length() > 0) {
						final String highId = olSongVO
								.getHighId();
						try {
							//TODO MusicInfo中的 ID分为 music_file_id,music_id,music_gid,此处应写时是music_file_id
							boolean result = hasSongInLocal(highId);
							if (result) {
								// 本地已存在
								num_exist++;
								continue;
							}
						} catch (Exception e) {
							//e.printStackTrace();
						}
						url = olSongVO.getHighUrl();
						song_id = olSongVO.getHighId();
					}
				} else {
					if (olSongVO.getHighId() != null
							&& olSongVO.getHighId().length() > 0) {
						final String highId = olSongVO
								.getHighId();
						try {
							boolean result = hasSongInLocal(highId);
							if (result) {
								// 本地已存在
								num_exist++;
								continue;
							}
						} catch (Exception e) {
							//e.printStackTrace();
						}
						
					}
					if (olSongVO.getLowId() != null
							&& olSongVO.getLowId().length() > 0) {
						final String lowId = olSongVO
								.getLowId();
						try {
							boolean result = hasSongInLocal(lowId);
							if (result) {
								// 本地已存在
								num_exist++;
								continue;
							}
						} catch (Exception e) {
							//e.printStackTrace();
						}
						url = olSongVO.getLowUrl();
						song_id = olSongVO.getLowId();
					}
				}
				
				if (song_id == null) {
					if (olSongVO.getLowId() != null
							&& olSongVO.getLowId().length() > 0) {
						final String lowId = olSongVO
								.getLowId();
						try {
							boolean result = hasSongInLocal(lowId);
							if (result) {
								// 本地已存在
								num_exist++;
								continue;
							}
						} catch (Exception e) {
							//e.printStackTrace();
						}
						url = olSongVO.getLowUrl();
						song_id = olSongVO.getLowId();
					}
				}
				if (song_id == null) {
					if (olSongVO.getRingId() != null
							&& olSongVO.getRingId().length() > 0) {
						final String ringId = olSongVO
								.getRingId();
						try {
							boolean result = hasSongInLocal(ringId);
							if (result) {
								// 本地已存在
								num_exist++;
								continue;
							}
						} catch (Exception e) {
							//e.printStackTrace();
						}
						url = olSongVO.getRingUrl();
						song_id = olSongVO.getRingId();
					}
				}
				if (song_id == null) {
					continue;
				}	
				DownloadFile file = new DownloadFile(++TianlApp.i + "",olSongVO.getGid(),url, CommonUtils.getFileNameByUrl(url),
						olSongVO.getSong(),olSongVO.getSinger());
				file.setCreateTime(System.currentTimeMillis()); // 设置创建文件时间，用于文件的排序
				file.setFileType(DownloadType.DOWNLOAD_TYPE_MUSIC_APPOINTMENT);
				file.setDownloadListener(TianlApp.newInstance());
				file.setSongName(olSongVO.getSong());
				file.setFileName(olSongVO.getSong() +CommonUtils.getSuffixByUrl(url));
				file.setGid(olSongVO.getGid());
				file.setFileID(song_id);
				DownloadService.newInstance().binder.startDownloadTask(file, true);
			}
			if(num_exist>0){
				if(size != num_exist)
					Toast.makeText(this, num_exist+getResources().getString(R.string.total_datas)+getResources().getString(R.string.downloaded)+getResources().getString(R.string.download_other)+getResources().getString(R.string.download_task_all), Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(this, R.string.task_exit, Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(this, R.string.download_task_all, Toast.LENGTH_SHORT).show();
			}
			return true;
		} else {
			Toast.makeText(this, R.string.no_network, Toast.LENGTH_SHORT).show();
			return false;
		}
	}
	
	/**
	 * 返回键处理操作
	 * */
	private void back() {
		if (mSearch_Str != null && "recommond".equals(mSearch_Str)) {
			Intent intent = new Intent(backAction);
			intent.putExtra(BaseActivity.INTENT_ACTIVITY_NAME, activityName);
			TianlApp.activityBundles
					.remove(IntentAction.INTENT_ONLINE_FEATRUED_SET_MUSCI_LIST_ACTIVITY);
			Bundle bundle = new Bundle();
			bundle.putString(OnlineActivity.CURRENT_TAB, "RecommendActivity");
			TianlApp.activityBundles.put(IntentAction.INTENT_ONLINE_ACTIVITY,
					bundle);
			int level = TianlApp.newInstance().getPageLevel();
			BaseActivity.newInstance().showActivity(intent,
					level - 1 == 0 ? 1 : level - 1);
		} else {
			Intent intent = new Intent(backAction);
			intent.putExtra(BaseActivity.INTENT_ACTIVITY_NAME, activityName);
			TianlApp.activityBundles
					.remove(IntentAction.INTENT_ONLINE_FEATRUED_SET_MUSCI_LIST_ACTIVITY);
			Bundle bundle = new Bundle();
			bundle.putString(OnlineActivity.CURRENT_TAB, "OmnibusActivity");
			bundle.putString(KEY_BACK_SEARCH, mSearch_Str);
			TianlApp.activityBundles.put(IntentAction.INTENT_ONLINE_ACTIVITY,
					bundle);
			int level = TianlApp.newInstance().getPageLevel();
			BaseActivity.newInstance().showActivity(intent,
					level - 1 == 0 ? 1 : level - 1);
		}
	}

	/**
	 * 进入精选集简介界面
	 * */
	private void goToFeatruedSetIntro() {
		Intent intent = new Intent(
				IntentAction.INTENT_ONLINE_FEATRUED_SET_INTRO);
		intent.putExtra(BaseActivity.INTENT_ACTIVITY_NAME,
				"OmnibusIntroActivity");

		if (TianlApp.activityBundles != null) {
			Bundle bundle = new Bundle();
			bundle.putString(OmnibusDetailActivity.KEY_TITLE, titleName);
			bundle.putString(OmnibusDetailActivity.KEY_TAG, mTagText);
			bundle.putString(OmnibusDetailActivity.KEY_INTRO, mIntroText);
			bundle.putString(OmnibusDetailActivity.KEY_IMAGE, mImage);
			bundle.putString(OmnibusDetailActivity.KEY_BACK_ACTION,
					IntentAction.INTENT_ONLINE_FEATRUED_SET_MUSCI_LIST_ACTIVITY);
			bundle.putString(OmnibusDetailActivity.KEY_ACTIVITY_NAME,
					"OmnibusDetailActivity");
			bundle.putBoolean(OmnibusDetailActivity.KEY_BACK_BTN, true);
			TianlApp.activityBundles.put(
					IntentAction.INTENT_ONLINE_FEATRUED_SET_INTRO, bundle);
		}
		int level = TianlApp.newInstance().getPageLevel();
		BaseActivity.newInstance().showActivity(intent, level + 1);
	}

	/**
	 * 获取精选集数据
	 * 
	 * @author Erica
	 * @note
	 * */
	public boolean getSearchOmnibusData(int _ty, int _page,int _size,boolean isDerail) {

		url = null;
		url = CommonUtils.getOmnibusInfoURL(_ty, _page, _size, isDerail, mSearch_Id);
		if(readLocalData(url)){
			mHandler.sendEmptyMessage(LOAD_COMPLETE);
		}else{
			if(!CommonUtils.isHasNetwork(TianlApp.newInstance())){
				imgDance.setVisibility(View.GONE);
				imgDance.stop();
				featured_detail_layout.setVisibility(View.GONE);
				no_network_tips.setVisibility(View.VISIBLE);
				return false;
			}else{
				imgDance.setVisibility(View.VISIBLE);
				no_network_tips.setVisibility(View.GONE);
			}
		}
		Lg.d("url  =======", "mOlAlbumList url = == " + url);
		OnlineMusicManager.getInstence().getOmnibusDetailData(this,
				new OnDataPreparedListener<OlAlbumList>() {
					@Override
					public void onDataPrepared(OlAlbumList data) {
						if (data != null&& data.getDataList().size() > 0) {
							
							if (mOlAlbumList != null) {
								
								if (mOlAlbumList.equals(data)) {
									return;
								}
							}
							mOlAlbumList = data;
							List<OlSongVO> olSongVOs=data.getDataList();
							 OlSongVO olSongVO1=olSongVOs.get(1);
							 olSongVO1.setSinger("尔雅");
							 olSongVO1.setSong("童话故事里的眼泪");
							 olSongVO1.setLowPrtUrl("http://218.200.227.123/order/wap/in/0026055/600907000002303488/");

							 mAdapter.setDatas(olSongVOs, true);;
							mHandler.sendEmptyMessage(LOAD_COMPLETE);
						}else {
							Lg.d("getOmnibusDetailData() == null");
							hasNoResultFresh();
							return;
						}
					}
				}, url);
		return true;
	}
	
	/** 获取数据异常时，界面刷新操作
	 *  */
	private void hasNoResultFresh(){
		featured_detail_layout.setVisibility(View.VISIBLE);
		if(imgDance.getVisibility()== View.VISIBLE){
			imgDance.setVisibility(View.GONE);
			imgDance.stop(); 
		}
	}
	
//private int[] location = new int[2];
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		musicLV.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				
			}
		});
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		
	}
	
//float y1 = 0;
//float move = 0;
/*	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_MOVE){
//			mControlLayout.getLocationInWindow(location);
//			move =  y1 - event.getY();
//			y1 = event.getY();
//			if(move!=0){
//				
//			}
//			System.out.println(location[0]+"**"+location[1]+event.getY()+"&&&&"+move);
		}
		return false;
	}*/

	private boolean saveLocalData(String url){
		try {
			NetCache.saveCache(mOlAlbumList, url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		return true;
	}
	
	private boolean readLocalData(String url){
		try {
			mOlAlbumList = NetCache.readCache(url);		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return false;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return false;
		}catch (ClassCastException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				return false;
			}
		return true;
	}
	
	private boolean hasSongInLocal(String fileId){
		return LocalMusicManager.getInstence().existMusicByFileId(fileId);
	}
	
	public void onPause() {
	    super.onPause();
		//友盟统计 日志 add by perry 2012-10-23
	    //MobclickAgent.onPause(this);
	}
}
