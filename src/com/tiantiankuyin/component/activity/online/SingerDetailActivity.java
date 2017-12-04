package com.tiantiankuyin.component.activity.online;

import java.io.IOException;
import java.lang.ref.SoftReference;

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
import com.tiantiankuyin.bean.OlSingerVO;
import com.tiantiankuyin.bean.OlSongVO;
import com.tiantiankuyin.bean.SingerInfoVO;
import com.tiantiankuyin.component.activity.BaseActivity;
import com.tiantiankuyin.database.bll.OnDataPreparedListener;
import com.tiantiankuyin.database.bll.OnlineMusicManager;
import com.tiantiankuyin.net.EasouAsyncImageLoader;
import com.tiantiankuyin.net.NetCache;
import com.tiantiankuyin.net.EasouAsyncImageLoader.ILoadedImage;
import com.tiantiankuyin.para.IntentAction;
import com.tiantiankuyin.para.SPHelper;
import com.tiantiankuyin.play.PlayLogicManager;
import com.tiantiankuyin.utils.CommonUtils;
import com.tiantiankuyin.utils.Lg;
import com.tiantiankuyin.view.AnimImageView;
import com.tiantiankuyin.R;
import com.tiantiankuyin.TianlApp;

/**
 * 在线模块-歌手详细
 * @author Erica 
 */
public class SingerDetailActivity extends Activity implements OnClickListener,
		OnItemClickListener {
	/** 日志打印标志位 */
	public static final String ACTIVITY_ID = "SingerDetailActivity";
	/** 标题 索引 */
	public static final String KEY_TITLE = "TITLE_NAME";
	/** 搜索字符串索引 */
	public static final String KEY_SEARCH = "SEARCH_NAME";
	/** 歌手图片索引 */
	public static final String KEY_IMAGE = "IMAGE_NAME";
	/** 返回操作按钮索引 */
	public static final String KEY_BACK_BTN = "BACK_BTN";
	/** 返回按钮操作 */
	public static final String KEY_BACK_ACTION = "BACK_ACTION";
	/** 当前操作Activity对象 */
	public static final String KEY_ACTIVITY_NAME = "ACTIVITY_NAME";
	/** 加载完成标志 */
	public static final int LOAD_COMPLETE = 0x1;
	/** 信息加载完成标志 */
	public static final int LOAD_INFO_COMPLETE = 0x2;
	/** 动画显示完毕 */
	public static final int POPUP_WINDOW_DISMISS = 0x3;
	/** 界面控制对象 */
	private LocalActivityManager mLocalActivityManager;
	/** 搜索字符串 */
	private String mSearch_Id;
	/** 歌手名 */
	private String titleName;
	/** 歌手图片URL */
	private String mImage;
	/** 歌手图片 ImageView */
	private ImageView mDetailImage;
	/** 歌手歌曲List */
	private ListView musicLV;
	/** 歌手名 TextView */
	private TextView mSingerName;
	/** 血型 TextView */
	private TextView mbloodType;
	/** 星座 TextView */
	private TextView mConstellation;
	/** 生日 TextView */
	private TextView mBirthday;
	/** 身高 TextView */
	private TextView mStature;
	/** 人气按钮 Button */
	private Button onlineHotButton;
	/** 时间 Button */
	private Button onlineNewButton;
	/** 歌手详细页进入句柄 */
	private LinearLayout mControlLayout;
	/** 返回键 */
	private ImageButton backBtn;
	/** 在线音乐列表适配器 */
	private OnlineMusicListAdapter mAdapter;
	/** 消息处理对象 */
	private static Handler mHandler;
	/** 返回操作 */
	private String backAction;
	/** 当前操作Activity对象 */
	private String activityName;
	/** 基础View对象 */
	private static View rootView;
	/** 歌手数据集合 */
	private OlSingerVO mOlSingerVO;
	/** 歌手详细信息集合 */
	private SingerInfoVO mSingerInfoVO;
	/** 加载界面图片对象 */
	private ILoadedImage iLoadedImage;
	/** 是否是人气列表 */
	private boolean isShowHot = false;
	/** 是否为最新列表 */
	private boolean isShowNew = false;
	/** 显示加载更多 */ 
	private View footerView; 
	/** 是否需要尾部加载 */
	private boolean isAppend = false;
	/** 弹出Pop */
	private static PopupWindow pop;
	/** 歌手歌曲 */
	private String url = null;
	/** 歌手新消息 */
	private String info_url = null;
    /** 显示动画的组件   */ 
    private AnimImageView imgDance; 
	/** 界面精选集*/
	private LinearLayout singer_detail_layout;
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
				.get(IntentAction.INTENT_ONLINE_SINGER_DETAIL);
		if (bundle.getString(KEY_TITLE) != null) {
			titleName = bundle.getString(KEY_TITLE);
		}
		if (bundle.getString(KEY_SEARCH) != null) {
			mSearch_Id = bundle.getString(KEY_SEARCH);
		}
		if (bundle.getString(KEY_IMAGE) != null) {
			mImage = bundle.getString(KEY_IMAGE);
		}

		boolean hasBackBtn = bundle.getBoolean(KEY_BACK_BTN);
		backAction = bundle.getString(KEY_BACK_ACTION);
		activityName = bundle.getString(KEY_ACTIVITY_NAME);

		rootView = LayoutInflater.from(this).inflate(
				R.layout.online_singer_detail, null);
		setContentView(rootView);
		CommonUtils.setTitle(rootView, titleName, hasBackBtn, false);

		findView();
		init();

	}

	/**
	 * 获取界面控件对象
	 * */
	private void findView() {
		mControlLayout = (LinearLayout) findViewById(R.id.online_feature_set_more_lay);
		mDetailImage = (ImageView) findViewById(R.id.online_singer_detail_image);
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

		mSingerName = (TextView) findViewById(R.id.online_singername_txt);
		mbloodType = (TextView) findViewById(R.id.online_singerbloodType_txt);
		mConstellation = (TextView) findViewById(R.id.online_singerconstellation_txt);
		mBirthday = (TextView) findViewById(R.id.online_singerbirth_txt);
		mStature = (TextView) findViewById(R.id.online_singerstature_txt);
		onlineHotButton = (Button) findViewById(R.id.online_shot_musiclist);
		onlineHotButton.setBackgroundResource(R.drawable.online_singer_info_order_left_default_img);
		onlineNewButton = (Button) findViewById(R.id.online_snew_musiclist);
		onlineNewButton.setBackgroundResource(R.drawable.online_singer_info_order_right_press_img);
		musicLV = (ListView) findViewById(R.id.online_set_detail_listview);
		backBtn = (ImageButton) findViewById(R.id.back_btn);

		musicLV.setFooterDividersEnabled(true);
		footerView = LayoutInflater.from(this).inflate(
				R.layout.online_recommend_listview_footer, null);
		android.widget.AbsListView.LayoutParams lp = new android.widget.AbsListView.LayoutParams(
				-1, CommonUtils.dip2px(this, 55));
		footerView.setLayoutParams(lp);
		musicLV.addFooterView(footerView, null, true);
		footerView.setVisibility(View.GONE);
		singer_detail_layout = (LinearLayout) findViewById(R.id.online_singer_lay);
		//loading_layout = (LinearLayout) findViewById(R.id.loading_lay);
		singer_detail_layout.setVisibility(View.GONE);
		//loading_layout.setVisibility(View.VISIBLE);
        imgDance = (AnimImageView) super.findViewById(R.id.ImgDance); 
        initNetError();
	}

	/**
	 * 初始化界面控制对象，并加载数据
	 * */
	private void init() {

		mLocalActivityManager = new LocalActivityManager(this, true);
		mAdapter = new OnlineMusicListAdapter(this);
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case LOAD_COMPLETE:
					if (mOlSingerVO != null) {
						if(imgDance.getVisibility()== View.VISIBLE){
							imgDance.setVisibility(View.GONE);
							imgDance.stop(); 
						}
						singer_detail_layout.setVisibility(View.VISIBLE);
						if(footerView.getVisibility()==View.GONE)
							footerView.setVisibility(View.VISIBLE);
						mAdapter.setDatas(mOlSingerVO.getDataList(),isAppend);	
						if(mOlSingerVO.isHasNext()){
							setFootviewState(false, true);
						} else {
							setFootviewState(false, false);
						}
						mAdapter.notifyDataSetChanged();
						isAppend = false;
						saveLocalData(url,0);
						CommonUtils.setListViewHeightBasedOnChildren(musicLV,true,false);
					}
					break;
				case LOAD_INFO_COMPLETE:
					showSingerInfo();
					saveLocalData(info_url,1);
					break;
				case POPUP_WINDOW_DISMISS:
					if(pop.isShowing())
						pop.dismiss();
					break;
				}

			}
		};

		this.musicLV.setAdapter(mAdapter);
		musicLV.setOnItemClickListener(this);
		try {
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					onlineHotButton.setBackgroundResource(R.drawable.online_singer_info_order_left_default_img);
					isShowHot = getSearchSingerDetailData(mSearch_Id, "shot",1);			
					getSingerData(mSearch_Id, 150);
					onlineNewButton.setBackgroundResource(R.drawable.online_singer_info_order_right_press_img);
				}
			});
		} catch (Exception e) {
			//e.printStackTrace();
		}
		
		mControlLayout.setOnClickListener(this);
		onlineHotButton.setOnClickListener(this);
		onlineNewButton.setOnClickListener(this);
		backBtn.setOnClickListener(this);

	}

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
			if(mAdapter.getmMusicList()==null){
				return;
			}
			String result = getString(R.string.already_load)
					+ mAdapter.getmMusicList().size()+ getString(R.string.total_datas);
			//alertTV.setText(result);
		}else{
			//alertTV.setText(getString(R.string.loading_more));
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		//友盟统计 日志 add by perry 2012-10-23
	    //MobclickAgent.onResume(this);
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
			goToSingerIntro();
			break;
		case R.id.online_shot_musiclist:
			if (!isShowHot) {
				onlineHotButton.setBackgroundResource(R.drawable.online_singer_info_order_left_default_img);
				isShowHot = getSearchSingerDetailData(mSearch_Id, "shot",1);
				isShowNew = false;
				onlineNewButton.setBackgroundResource(R.drawable.online_singer_info_order_right_press_img);
			}

			break;
		case R.id.online_snew_musiclist:
			if (!isShowNew) {
				onlineNewButton.setBackgroundResource(R.drawable.online_singer_info_order_right_default_img);
				isShowNew = getSearchSingerDetailData(mSearch_Id, "snew",1);
				onlineHotButton.setBackgroundResource(R.drawable.online_singer_info_order_left_press_img);
				isShowHot = false;
			}

			break;
		case R.id.list_re_more:
			if(isShowHot){
				isAppend = true;
				isShowHot = getSearchSingerDetailData(mSearch_Id, "shot",mOlSingerVO.getNextPage());
			}else{
				isAppend = true;
				isShowNew = getSearchSingerDetailData(mSearch_Id, "snew",mOlSingerVO.getNextPage());
			}
			break;		
		}
	}

	@Override
	public void onBackPressed() {
		back();
	}

	/** 条目单击处理操作 */
	public  void itemClick(View view,int index) {
		if (index == mAdapter.getmMusicList().size() ) { // 如果是页脚
			if(mOlSingerVO.isHasNext()){
				setFootviewState(true, true);
				if(isShowHot){
					isAppend = true;
					isShowHot = getSearchSingerDetailData(mSearch_Id, "shot",mOlSingerVO.getNextPage());
				}else{
					isAppend = true;
					isShowNew = getSearchSingerDetailData(mSearch_Id, "snew",mOlSingerVO.getNextPage());
				}
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
				onlineHotButton.setBackgroundResource(R.drawable.online_singer_info_order_left_default_img);
				isShowHot = getSearchSingerDetailData(mSearch_Id, "shot",1);			
				getSingerData(mSearch_Id, 150);
				onlineNewButton.setBackgroundResource(R.drawable.online_singer_info_order_right_press_img);			}
			
		}
	};
	
	
	/**
	 * 加载歌手详细信息处理操作
	 * */
	public void showSingerInfo() {
		if(mSingerInfoVO ==null){
			return ;
		}
		
		if (mSingerInfoVO.getSinger() != null) {
			mSingerName.setText(mSingerInfoVO.getSinger());
		}
		if (mSingerInfoVO.getBloodType() != null) {
			mbloodType.setText(mSingerInfoVO.getBloodType());
		}
		if (mSingerInfoVO.getConstellation() != null) {
			mConstellation.setText(mSingerInfoVO.getConstellation());
		}
		if (mSingerInfoVO.getBirthday() != null) {
			mBirthday.setText(mSingerInfoVO.getBirthday());
		}
		if (mSingerInfoVO.getStature() != null) {
			mStature.setText(mSingerInfoVO.getStature());
		}
		if (mSingerInfoVO.getImgUrl() != null) {
			mDetailImage.setTag(CommonUtils.MD5(mSingerInfoVO.getImgUrl()));
			iLoadedImage = new ILoadedImage() {
				@Override
				public void onFinishLoadedLRC(String lrcPath, String songName) {
				}

				@Override
				public void onFinishLoaded(SoftReference<Drawable> drawable, String saveName) {

					ImageView imageview = (ImageView) rootView
							.findViewWithTag(CommonUtils.MD5(mSingerInfoVO
									.getImgUrl()));
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
			EasouAsyncImageLoader.newInstance().loadImage(
					mSingerInfoVO.getImgUrl(), iLoadedImage,
					CommonUtils.MD5(mSingerInfoVO.getImgUrl()));
		}

	}

	/**
	 * 返回按键处理操作
	 * */
	private void back() {
		TianlApp.isBacktoSingerList = true;
		Intent intent = new Intent(backAction);
		intent.putExtra(BaseActivity.INTENT_ACTIVITY_NAME, activityName);
		TianlApp.activityBundles.remove(IntentAction.INTENT_ONLINE_SINGER_DETAIL);
		int level = TianlApp.newInstance().getPageLevel();
		BaseActivity.newInstance().showActivity(intent,
				level - 1 == 0 ? 1 : level - 1);
	}

	/**
	 * 进入歌手简介页
	 * */
	private void goToSingerIntro() {
		Intent intent = new Intent(IntentAction.INTENT_ONLINE_SINGER_INTRO);
		intent.putExtra(BaseActivity.INTENT_ACTIVITY_NAME,
				"SingerIntroActivity");

		if (TianlApp.activityBundles != null) {
			Bundle bundle = new Bundle();
			bundle.putString(SingerIntroActivity.KEY_TITLE, titleName);
			bundle.putString(SingerIntroActivity.KEY_IMAGE, mImage);
			bundle.putString(SingerIntroActivity.KEY_SEARCH, mSearch_Id);
			bundle.putString(SingerIntroActivity.KEY_BACK_ACTION,
					IntentAction.INTENT_ONLINE_SINGER_DETAIL);
			bundle.putString(SingerIntroActivity.KEY_ACTIVITY_NAME,
					"SingerDetailActivity");
			bundle.putBoolean(SingerIntroActivity.KEY_BACK_BTN, true);
			TianlApp.activityBundles.put(IntentAction.INTENT_ONLINE_SINGER_INTRO,
					bundle);
		}
		int level = TianlApp.newInstance().getPageLevel();
		BaseActivity.newInstance().showActivity(intent, level + 1);
	}

	/**
	 * 获取歌手歌曲集合数据
	 * 
	 * @author Erica
	 * @param _name
	 *            String 歌手名
	 * @param _ty
	 *            String 请求类型 shot 人气 snew 最新
	 * @note
	 * */
	public boolean getSearchSingerDetailData(String _name, String _ty,int _page) {

		url = null;
		url = CommonUtils.getSearchSingerDetailDataURL(_name, _ty, _page);
		Lg.d("url  =======", "url = == " + url);
		if(readLocalData(url,0)){
			mHandler.sendEmptyMessage(LOAD_COMPLETE);
		}else{
			if(!CommonUtils.isHasNetwork(TianlApp.newInstance())){
				imgDance.setVisibility(View.GONE);
				imgDance.stop();
				singer_detail_layout.setVisibility(View.GONE);
				no_network_tips.setVisibility(View.VISIBLE);
				return false;
			}else{
				imgDance.setVisibility(View.VISIBLE);
				no_network_tips.setVisibility(View.GONE);
			}
		}
		OnlineMusicManager.getInstence().getSongListData(this,
				new OnDataPreparedListener<OlSingerVO>() {
					@Override
					public void onDataPrepared(OlSingerVO data) {
						if (data != null) {
							if (mOlSingerVO != null) {
								if (mOlSingerVO.equals(data)) {
									return;
								}
							}
							mOlSingerVO = data;

							Lg.d("mOlAlbumList  =======", "mOlAlbumList = == "
									+ mOlSingerVO.getDataList().size()+mOlSingerVO.getNextPage()+mOlSingerVO.isHasNext());
							mHandler.sendEmptyMessage(LOAD_COMPLETE);
						}else {
							Lg.d("getSingerDetailData() == null");
							hasNoResultFresh();
							return;
						}
					}
				}, url);
		return true;
	}

	/**
	 * 获取歌手信息数据
	 * 
	 * @author Erica
	 * @param _name
	 *            String 搜索歌手名
	 * @param _ty
	 *            int 搜索类型
	 * @note
	 * */
	public boolean getSingerData(String _name, int _ty) {

		info_url = null;
		info_url = CommonUtils.getSingerDataURL(_name, _ty);
		Lg.d("url  =======", "url = == " + info_url);
		if(readLocalData(info_url,1)){
			mHandler.sendEmptyMessage(LOAD_INFO_COMPLETE);
		}
		OnlineMusicManager.getInstence().getSingerData(this,
				new OnDataPreparedListener<SingerInfoVO>() {
					@Override
					public void onDataPrepared(SingerInfoVO data) {
						if (data != null) {
							if (mSingerInfoVO != null) {
								if (mSingerInfoVO.getSinger().equals(data)) {
									return;
								}
							}
							mSingerInfoVO = data;
							mHandler.sendEmptyMessage(LOAD_INFO_COMPLETE);
							SingerIntroActivity.setSingerInfoVO(mSingerInfoVO);
						}else {
							Lg.d("getSingerDetailData() == null");
							hasNoResultFresh();
							return;
						}
					}
				}, info_url);
		return true;
	}

	/** 获取数据异常时，界面刷新操作
	 *  */
	private void hasNoResultFresh(){
		singer_detail_layout.setVisibility(View.VISIBLE);
		if(imgDance.getVisibility()== View.VISIBLE){
			imgDance.setVisibility(View.GONE);
			imgDance.stop(); 
		}
	}
	
	private boolean saveLocalData(String url,int type){
		try {
			switch(type){
			case 0:
				NetCache.saveCache(mOlSingerVO, url);
				break;
			case 1:
				NetCache.saveCache(mSingerInfoVO, url);
				break;
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		return true;
	}
	
	private boolean readLocalData(String url,int type){
		try {
			switch(type){
			case 0:
				mOlSingerVO = NetCache.readCache(url);
				break;
			case 1:
				mSingerInfoVO = NetCache.readCache(url);
				break;
			}
			
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

	@Override
	protected void onPause() {
		super.onPause();
		//友盟统计 日志 add by perry 2012-10-23
	    //MobclickAgent.onPause(this);
	}
	
}
