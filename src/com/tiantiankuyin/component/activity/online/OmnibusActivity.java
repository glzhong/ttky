package com.tiantiankuyin.component.activity.online;
//package com.haige.demo.component.activity.online;
//
//import java.io.IOException;
//import java.util.List;
//
//import android.app.Activity;
//import android.app.LocalActivityManager;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.drawable.Drawable;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.View.OnTouchListener;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.BaseAdapter;
//import android.widget.Button;
//import android.widget.GridView;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.PopupWindow;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//
//import com.haige.demo.Easou;
//import com.haige.demo.R;
//import com.haige.demo.adapter.OnlineAlbumAdapter;
//import com.haige.demo.bean.OlAlbumVO;
//import com.haige.demo.bean.OmnibusTip;
//import com.haige.demo.bean.OmnibusTipList;
//import com.haige.demo.component.activity.BaseActivity;
//import com.haige.demo.database.bll.OnDataPreparedListener;
//import com.haige.demo.database.bll.OnlineMusicManager;
//import com.haige.demo.net.NetCache;
//import com.haige.demo.para.Env;
//import com.haige.demo.para.IntentAction;
//import com.haige.demo.utils.CommonUtils;
//import com.haige.demo.utils.Lg;
//import com.haige.demo.view.AnimImageView;
//
///**
// * 在线模块-精选集
// * 
// * @author Erica
// * 
// */
//public class OmnibusActivity extends Activity implements OnClickListener{
//
//	/** 日志打印标志位 */
//	public static final String ACTIVITY_ID = "OmnibusActivity";
//	/** 当前Activity管理对象 */
//	private LocalActivityManager mLocalActivityManager;
//	/** 当前界面精选集对象 */
//	private OlAlbumVO mOlAlbumVO;
//	/** 界面精选集title */
//	private TextView title;
//	/** 界面精选集标签按钮 */
//	private LinearLayout showTip_Button;
//	/** 精选集列表对象 */
//	private ListView mFeaturedSetList;
//	/** 当前操作精选集名称 默认为推荐 */
//	private String showTipName;
//	/** 精选集列表适配器 */
//	private OnlineAlbumAdapter mOnlineAlbumAdapter;
//	/** 精选集标签Grid适配器 */
//	private OnlineTipAdapter mOnlineTipAdapter;
//	/** 精选集标签队列 */
//	private OmnibusTipList mOmnibusTipList;
//	/** 弹出标签操作框 */
//	private PopupWindow mTipPop;
//	/** 弹出菜单GridView */
//	private GridView mTipPopGridView;
//	/** 消息处理对象 */
//	private static Handler mHandler;
//	/** 加载完成 */
//	public static final int LOAD_COMPLETE = 0x1;
//	/** 弹出框数据加载完成 */
//	public static final int LOAD_POPUP = 0x2;
//	/** 显示加载更多 */ 
//	private View footerView;
//	/** 是否需要尾部加载 */
//	private boolean isAppend;
//	/** 搜索精选集 */
//	private boolean isSearchData;
//	/** 精选集 */
//	private String url = null;
//	/** 精选集标签 */
//	private String tip_url = null;
//	/** 精选集 标签 */
//	private boolean isClickTip;
//	/** 返回 搜索字符串*/
//	public static final String KEY_BACK_SEARCH = "BACK_SEARCH";
//	/** 返回 搜索字符串 */
//	private String mSearch_Str;
//    /** 显示动画的组件   */ 
//    private AnimImageView imgDance; 
//	/** 界面精选集标签按钮 */
//	private LinearLayout featured_set_layout;
//	/** 界面精选集标签按钮 */
//	//private LinearLayout loading_layout;
//	/** 当前是否已加载标签数据 */
//	public static boolean isHaveGetTipData = false;
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.online_featured_set);
//		if (Easou.newInstance().activityList != null
//				&& !Easou.newInstance().activityList.contains(this)) {
//			Easou.newInstance().activityList.add(this); // 将该Activity实例加入到Activity管理集合中
//		}
//		Bundle bundle = Easou.activityBundles
//				.get(IntentAction.INTENT_ONLINE_ACTIVITY);
//
//		if (bundle!=null && bundle.getString(KEY_BACK_SEARCH) != null) {
//			mSearch_Str = bundle.getString(KEY_BACK_SEARCH);
//		}
//		mLocalActivityManager = new LocalActivityManager(this, true);
//		findView();
//		init();
//	}
//
//	/** 加载界面控件 */
//	private void findView() {
//		title = (TextView) findViewById(R.id.online_featured_set_title);
//		title.setText(this.getResources().getString(R.string.no_tag));
//		showTip_Button = (LinearLayout) findViewById(R.id.online_featured_set_tip_lay);
//		showTip_Button.setClickable(true);
//		mFeaturedSetList = (ListView) findViewById(R.id.online_featured_set_list);
//		mOnlineTipAdapter = new OnlineTipAdapter(this);
//		mOnlineAlbumAdapter = new OnlineAlbumAdapter(this, mFeaturedSetList);
//		mFeaturedSetList.setFooterDividersEnabled(true);
//		footerView = LayoutInflater.from(this).inflate(
//				R.layout.online_recommend_listview_footer, null);
//		android.widget.AbsListView.LayoutParams lp = new android.widget.AbsListView.LayoutParams(
//				-1, CommonUtils.dip2px(this, 55));
//		footerView.setLayoutParams(lp);
//		mFeaturedSetList.addFooterView(footerView, null, true);
//		footerView.setVisibility(View.GONE);
//		featured_set_layout = (LinearLayout) findViewById(R.id.featured_set_layout);
//		//loading_layout = (LinearLayout) findViewById(R.id.loading_lay);
//		featured_set_layout.setVisibility(View.GONE);
//		//loading_layout.setVisibility(View.VISIBLE);
//        imgDance = (AnimImageView) super.findViewById(R.id.ImgDance); 
//        if(mSearch_Str!=null){
//        	if(imgDance.getVisibility()== View.VISIBLE){
//				imgDance.setVisibility(View.GONE);
//				imgDance.stop(); 
//			}
//        }
//	}
//
//	/** 初始化界面对象 */
//	public void init() {
//		mHandler = new Handler() {
//			@Override
//			public void handleMessage(Message msg) {
//				switch (msg.what) {
//				case LOAD_COMPLETE:
//					if(mOlAlbumVO == null)
//						return;
//					if(imgDance.getVisibility()== View.VISIBLE){
//						imgDance.setVisibility(View.GONE);
//						imgDance.stop(); 
//					}
//					featured_set_layout.setVisibility(View.VISIBLE);
//					mOnlineAlbumAdapter.setOlAlbumVO(mOlAlbumVO,isAppend);
//					if(footerView.getVisibility()==View.GONE)
//						footerView.setVisibility(View.VISIBLE);
//					if(mOlAlbumVO.isHasNext()){
//						setFootviewState(false, true);
//					} else {
//						setFootviewState(false, false);
//					}
//					mOnlineAlbumAdapter.notifyDataSetChanged();	
//					if(!isAppend&&mFeaturedSetList.getChildCount()>0){
//						mFeaturedSetList.setSelection(0);
//					}
//					saveLocalData(url,0);
//					break;
//				case LOAD_POPUP:
//					mOnlineTipAdapter.setOmnibusTipList(mOmnibusTipList);
//					mOnlineTipAdapter.notifyDataSetChanged();
//					if(mTipPop==null||!mTipPop.isShowing()&&isClickTip){
//						showPopUpWindow();
//						isClickTip = false;
//					}		
//					saveLocalData(tip_url,1);
//					break;
//				}
//				
//			}
//		};
//		showTip_Button.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//
//				try {
//					isClickTip = true;
//					mHandler.post(new Runnable() {
//						@Override
//						public void run() {
//							if (mTipPop != null && mTipPop.isShowing()) { 
//								Lg.d("showPopUpWindow", "mTipPop.dismiss();");
//								mTipPop.dismiss();
//							} else {
//								getOmnibusTipData(15);
//							}
//						}
//					});
//				} catch (Exception e) {
//					//e.printStackTrace();
//				}
//
//			}
//		});
//		try {
//			
//			mHandler.postDelayed(new Runnable() {
//				@Override
//				public void run() {
//					isSearchData = false;
//					isAppend = false;
//					if(mSearch_Str!=null&&mSearch_Str.length()>0){
//						if(mSearch_Str.equals(Easou.newInstance().getResources().getString(R.string.no_tag))){
//							getOmnibusData(15, 1, 180);
//						}else{
//							isSearchData = true;
//							getSearchOmnibusData(mSearch_Str,1, 180);
//						}
//					}else{
//						getOmnibusData(15, 1, 180);
//					}
//				}
//			},100);
//		} catch (Exception e) {
//			//e.printStackTrace();
//		}
//		mFeaturedSetList.setAdapter(mOnlineAlbumAdapter);
//		mFeaturedSetList.setOnItemClickListener(mListViewItemClick);
//	}
//
//	/**
//	 * 设置ListView Footer
//	 * 
//	 * @param isLoading
//	 *            是否是加载数据
//	 * @param isHaveData
//	 *            是否还能加载下一页
//	 */
//	private void setFootviewState(boolean isLoading, boolean isHaveData) {
//		TextView alertTV = (TextView) footerView.findViewById(R.id.loadMore);
//		ProgressBar loadingPB = (ProgressBar) footerView
//				.findViewById(R.id.recommondDataLoading);
//		if (isLoading) {
//			alertTV.setVisibility(View.GONE);
//			loadingPB.setVisibility(View.VISIBLE);
//		} else {
//			alertTV.setVisibility(View.VISIBLE);
//			loadingPB.setVisibility(View.GONE);
//		}
//		if (!isHaveData) {
//			if(mOnlineAlbumAdapter.getOlAlbumItemList()!=null){
//				String result = getString(R.string.already_load)
//					+ mOnlineAlbumAdapter.getOlAlbumItemList().size()+ getString(R.string.total_string);
//				alertTV.setText(result);
//			}else{
//				alertTV.setText(getString(R.string.loading_more));
//			}
//			
//		}else{
//			alertTV.setText(getString(R.string.loading_more));
//		}
//	}
//	
//	@Override
//	protected void onResume() {
//		super.onResume();
//		mLocalActivityManager.dispatchResume();
//		if (mTipPop != null && mTipPop.isShowing()) { 
//			mTipPop.dismiss();
//		}
//	}
//
//	/** 显示标签弹出框 */
//	public void showPopUpWindow() {
//		Lg.d("showPopUpWindow", "showPopUpWindow");
//			View extralView = getLayoutInflater().inflate(
//					R.layout.online_featured_popup, null);
//			mTipPop = new PopupWindow(extralView);			
////			 mTipPopGridView.setOnItemClickListener(mGridViewItemClick);
//			int screen_W = Env.getScreenWidth();
//			int screen_H = Env.getScreenHeight();
//			int totalLine = mOmnibusTipList.getAdList().size()/4;
//			mTipPop.setWidth(screen_W);
//			mTipPop.setHeight(totalLine*(screen_H/15)+3*(totalLine-1));
//			Drawable win_bg = this.getResources().getDrawable(R.drawable.online_album_more_label_bg_img);
//			mTipPop.setBackgroundDrawable(win_bg);
//			
//			mTipPopGridView = (GridView) extralView
//					.findViewById(R.id.popup_gridview);
//			mTipPopGridView.setAdapter(mOnlineTipAdapter);
//
//			mTipPop.setOutsideTouchable(true);
//			mTipPop.setFocusable(true);
//			/* 设置点击popupView之外的事件。收起“更多操作” */
//			mTipPop.setTouchInterceptor(new OnTouchListener() {
//
//				@Override
//				public boolean onTouch(View v, MotionEvent event) {
//					if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
//						mTipPop.dismiss();
//						isClickTip = false;
//						return true;
//					}
//					return false;
//				}
//			});
//			mTipPop.showAsDropDown(findViewById(R.id.online_featured_set_tip),
//					0, 5);
//
//	}
//
//	/**
//	 * 获取精选集数据
//	 * 
//	 * @author Erica
//	 * @note
//	 * */
//	public boolean getOmnibusData(int _size, int _page, int _ty) {	
//		url = null;
//		url =  CommonUtils.getOmnibusDataURL(_size, _page, _ty);
//		if(readLocalData(url,0)){
//			mHandler.sendEmptyMessage(LOAD_COMPLETE);
//		}
////		System.out.println("url  ======="+url);
//		OnlineMusicManager.getInstence().getOmnibusData(this,
//				new OnDataPreparedListener<OlAlbumVO>() {
//					@Override
//					public void onDataPrepared(OlAlbumVO data) {
//						if (data != null && data.getDataList().size() > 0) {
//							if (showTipName != null)
//								title.setText(showTipName);	
//							if (data != null) {
//								if (mOlAlbumVO != null) {
//									if (mOlAlbumVO.equals(data)) {
//										return;
//									}
//								}
//								mOlAlbumVO = data;
//								mHandler.sendEmptyMessage(LOAD_COMPLETE);
//							}
//						} else {
//							Lg.d("getOmnibusData() == null");
//							hasNoResultFresh();
//							return;
//						}
//					}
//				}, url);
//		return true;
//	}
//
//	/**
//	 * 获取精选集标签数据
//	 * 
//	 * @author Erica
//	 * */
//	public boolean getOmnibusTipData(int _ty) {
//		tip_url = null;
//		tip_url = CommonUtils.getOmnibusTipDataURL(_ty) ;
//		if (readLocalData(tip_url, 1)) {
//			if(isHaveGetTipData){
//				mHandler.sendEmptyMessage(LOAD_POPUP);
//				return true;
//			}		
//		} else {
//			isHaveGetTipData = false;
//		}
//		if (!isHaveGetTipData) {
//			OnlineMusicManager.getInstence().getOmnibusTipData(this,
//					new OnDataPreparedListener<OmnibusTipList>() {
//						@Override
//						public void onDataPrepared(OmnibusTipList data) {
//							if (data != null) {
//								if (mOmnibusTipList != null) {
//									if (mOmnibusTipList.equals(data)) {
//										return;
//									}
//								}
//								mOmnibusTipList = data;
//								isHaveGetTipData = true;
//								mHandler.sendEmptyMessage(LOAD_POPUP);
//
//							} else {
//								Lg.d("getOmnibusTipData() == null");
//								hasNoResultFresh();
//								return;
//							}
//						}
//					}, tip_url);
//		}
//		return true;
//	}
//	
//	/**
//	 * 获取搜索精选集数据
//	 * 
//	 * @author Erica
//	 * @note
//	 * */
//	public boolean getSearchOmnibusData(final String _name, int _page, int _ty) {
//		url = null;
//		if (_name != null)
//			title.setText(_name);
//		url = CommonUtils.getSearchOmnibusDataURL(_name, _page, _ty);
//		Lg.d("url  =======", "url = == " + url);
//		if(readLocalData(url,0)){
//			mHandler.sendEmptyMessage(LOAD_COMPLETE);
//		}
//		OnlineMusicManager.getInstence().getSearchOmnibusData(this,
//				new OnDataPreparedListener<OlAlbumVO>() {
//					@Override
//					public void onDataPrepared(OlAlbumVO data) {
//						if (data != null) {
//							if(mOlAlbumVO!=null){
//								if(mOlAlbumVO.equals(data)){
//									return;
//								}	
//							}
//							mOlAlbumVO = data;
//							mHandler.sendEmptyMessage(LOAD_COMPLETE);				
//						}else {
//							Lg.d("getOmnibusTipData() == null");
//							hasNoResultFresh();
//							return;
//						}
//					}
//				}, url);
//		return true;
//	}
//
//	/** 获取数据异常时，界面刷新操作
//	 *  */
//	private void hasNoResultFresh(){
//		featured_set_layout.setVisibility(View.VISIBLE);
//		if(imgDance.getVisibility()== View.VISIBLE){
//			imgDance.setVisibility(View.GONE);
//			imgDance.stop(); 
//		}
//	}
//	
//	/** 主界面listview 点击事件 */
//	private AdapterView.OnItemClickListener mListViewItemClick = new OnItemClickListener() {
//		@Override
//		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//				long arg3) {
//			// 默认的文件夹
//			if(mOnlineAlbumAdapter.getOlAlbumItemList()==null)
//				return;
//			if ( arg2 == mOnlineAlbumAdapter.getOlAlbumItemList().size() ) { // 如果是页脚
//				if(mOlAlbumVO.isHasNext()){
//					setFootviewState(true, true);
//					if(!isSearchData)
//						getOmnibusData(15, mOlAlbumVO.getNextPage(), 180);
//					else
//						getSearchOmnibusData(title.getText().toString(), mOlAlbumVO.getNextPage(), 180);
//					isAppend = true;
//				}	
//			}else{
//				goToOmnibusDetailActivity(arg2);
//
//			}	
//		}
//	};
//
//	/**
//	 * 进入精选集详细界面 二级界面
//	 * 
//	 * @author Erica
//	 * @param arg2
//	 *            int 当前操作索引
//	 * */
//	public void goToOmnibusDetailActivity(int arg2) {
//		Intent intent = new Intent(
//				IntentAction.INTENT_ONLINE_FEATRUED_SET_MUSCI_LIST_ACTIVITY);
//		intent.putExtra(BaseActivity.INTENT_ACTIVITY_NAME,
//				"OmnibusDetailActivity");
//
//		if (Easou.activityBundles != null&&mOnlineAlbumAdapter.getOlAlbumItemList().get(arg2)!=null) {
//			Bundle bundle = new Bundle();
//			bundle.putString(OmnibusDetailActivity.KEY_TITLE, mOnlineAlbumAdapter.getOlAlbumItemList().get(arg2).getName());
//			bundle.putInt(OmnibusDetailActivity.KEY_SEARCH, mOnlineAlbumAdapter.getOlAlbumItemList().get(arg2).getId());
//			bundle.putString(OmnibusDetailActivity.KEY_BACK_SEARCH, title.getText().toString());
//			
//			if (mOnlineAlbumAdapter.getOlAlbumItemList().get(arg2).getTag() != null) {
//				bundle.putString(OmnibusDetailActivity.KEY_TAG, mOnlineAlbumAdapter.getOlAlbumItemList().get(arg2).getTag());
//			}
//			if (mOnlineAlbumAdapter.getOlAlbumItemList().get(arg2).getIntro() != null) {
//				bundle.putString(OmnibusDetailActivity.KEY_INTRO, mOnlineAlbumAdapter.getOlAlbumItemList().get(arg2).getIntro());
//			}
//			if (mOnlineAlbumAdapter.getOlAlbumItemList().get(arg2).getImgUrl() != null) {
//				bundle.putString(OmnibusDetailActivity.KEY_IMAGE, mOnlineAlbumAdapter.getOlAlbumItemList().get(arg2).getImgUrl());
//			}
//			bundle.putString(OmnibusDetailActivity.KEY_BACK_ACTION,
//					IntentAction.INTENT_ONLINE_ACTIVITY);
//			bundle.putString(OmnibusDetailActivity.KEY_ACTIVITY_NAME,
//					"OnlineActivity");
//			bundle.putBoolean(OmnibusDetailActivity.KEY_BACK_BTN, true);
//			Easou.activityBundles
//					.put(IntentAction.INTENT_ONLINE_FEATRUED_SET_MUSCI_LIST_ACTIVITY,
//							bundle);
//		}
//		int level = Easou.newInstance().getPageLevel();
//		BaseActivity.newInstance().showActivity(intent, level + 1);
//	}
//
//	
//	
//	@Override
//	public void onClick(View v) {
//		// TODO Auto-generated method stub
//		switch (v.getId()) {
//		case R.id.list_re_more:
//			if(!isSearchData)
//				getOmnibusData(15, mOlAlbumVO.getNextPage(), 180);
//			else
//				getSearchOmnibusData(title.getText().toString(), mOlAlbumVO.getNextPage(), 180);
//			isAppend = true;
//			break;		
//		}
//	}
//
//	/** 当前弹出Popup适配对象 */
//	public class OnlineTipAdapter extends BaseAdapter {
//
//		/** 当前操作界面对象 */
//		private Context context;
//		/** 标签数据集合对象 */
//		private OmnibusTipList mOmnibusTipList;
//		/** 标签列表集合对象 */
//		private List<OmnibusTip> mOmnibusTip;
//		private LayoutInflater mInflater;
//
//		/** 动态获取屏幕分辨率 宽 */
//		private int screen_W = Env.getScreenWidth();
//
//		public OnlineTipAdapter(Context context) {
//			this.context = context;
//
//			mInflater = (LayoutInflater) this.context
//					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		}
//
//		/** 设置当前操作列表对象 */
//		public void setOmnibusTipList(OmnibusTipList _mOmnibusTipList) {
//			mOmnibusTipList = _mOmnibusTipList;
//			if(mOmnibusTipList!=null&&!mOmnibusTipList.getAdList().get(0).getName().equals(context.getResources().getString(R.string.no_tag))){
//				OmnibusTip idex = new OmnibusTip();
//				idex.setId(0);
//				idex.setName(context.getResources().getString(R.string.no_tag));
//				mOmnibusTipList.getAdList().add(0, idex);
//			}
//			mOmnibusTip = mOmnibusTipList.getAdList();
//		}
//
//		@Override
//		public int getCount() {
//			if (mOmnibusTip != null)
//				return mOmnibusTip.size();
//			return 0;
//		}
//
//		@Override
//		public Object getItem(int position) {
//			if (mOmnibusTip != null)
//				return mOmnibusTip.get(position);
//			return null;
//		}
//
//		@Override
//		public long getItemId(int position) {
//			return position;
//		}
//
//		@Override
//		public View getView(final int position, View convertView,
//				ViewGroup parent) {
//			View view = null;
//			if (convertView != null) {
//				view = convertView;
//			} else {
//				convertView = mInflater.inflate(
//						R.layout.online_featured_popup_item, null);
//				view = convertView;
//			}
//			Button button = (Button) view.findViewById(R.id.tip_btn);
//			button.setSingleLine();
//			button.setWidth(screen_W / 4);
//			
//			if(title.getText().equals(mOmnibusTip.get(position).getName())){
//				button.setTextColor(0xff89b61d);
//			}
//			button.setText(mOmnibusTip.get(position).getName());
//			button.setClickable(true);
//			button.setOnClickListener(new OnClickListener() {
//			
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					mTipPop.dismiss();
//					isClickTip = false;
//					isSearchData = true;
//					isAppend = false;
//					if( position == 0){
//						isSearchData = false;
//						getOmnibusData(15,1 , 180);
//						title.setText(Easou.newInstance().getResources().getString(R.string.no_tag));
//					}else{
//						getSearchOmnibusData(
//							mOmnibusTipList.getAdList().get(position).getName(),
//							1, 180);
//					}
//					
//				}
//			});
//			return view;
//		}
//	}
//	
//	private boolean saveLocalData(String url,int type){
//		try {
//			switch(type){
//			case 0:
//				NetCache.saveCache(mOlAlbumVO, url);
//				break;
//			case 1:
//				NetCache.saveCache(mOmnibusTipList, url);
//				break;
//			}
//			
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			//e.printStackTrace();
//		}
//		return true;
//	}
//	
//	private boolean readLocalData(String url,int type){
//		try {
//			switch(type){
//			case 0:
//				mOlAlbumVO = NetCache.readCache(url);
//				break;
//			case 1:
//				mOmnibusTipList = NetCache.readCache(url);
//				break;
//			}
//			
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			//e.printStackTrace();
//			return false;
//		} catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			//e.printStackTrace();
//			return false;
//		}catch (ClassCastException e) {
//				// TODO Auto-generated catch block
//				//e.printStackTrace();
//				return false;
//			}
//		return true;
//	}
//
//	public void onPause() {
//	    super.onPause();
//	}
//}
