package com.tiantiankuyin.component.activity.online;

import java.io.IOException;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tiantiankuyin.adapter.OnlineSingerListAdapter;
import com.tiantiankuyin.bean.SingerListBean;
import com.tiantiankuyin.component.activity.BaseActivity;
import com.tiantiankuyin.database.bll.OnDataPreparedListener;
import com.tiantiankuyin.database.bll.OnlineMusicManager;
import com.tiantiankuyin.net.NetCache;
import com.tiantiankuyin.para.IntentAction;
import com.tiantiankuyin.utils.CommonUtils;
import com.tiantiankuyin.utils.Lg;
import com.tiantiankuyin.view.AnimImageView;
import com.tiantiankuyin.R;
import com.tiantiankuyin.TianlApp;

/**
 * 在线模块-歌手列表页
 * @author Erica
 */
public class SingerListActivity extends Activity implements OnClickListener,
		OnItemClickListener {

	/** 日志打印标志位 */
	public static final String ACTIVITY_ID = "SingerListActivity";
	/** 标题 索引 */
	public static final String KEY_TITLE = "TITLE_NAME";
	/** 搜索关键字 索引 */
	public static final String KEY_SEARCH = "SEARCH_NAME";
	/** 是否需要返回 索引 */
	public static final String KEY_BACK_BTN = "BACK_BTN";
	/** 返回操作对象 索引 */
	public static final String KEY_BACK_ACTION = "BACK_ACTION";
	/** 当前操作Activity 索引 */
	public static final String KEY_ACTIVITY_NAME = "ACTIVITY_NAME";
	/** 加载完成标志位 */
	public static final int LOAD_COMPLETE = 0x1;
	/** 当前界面控制对象 */
	private LocalActivityManager mLocalActivityManager;
	/** 搜索关键字 */
	private int mSearch_Id;
	/** 标题名 所属类别名 */
	private String titleName;
	/** 歌手列表 ListView */
	private ListView mSingerListLV;
	/** 歌手列表数据适配器 */
	private OnlineSingerListAdapter mAdapter;
	/** 界面消息处理类 */
	private static Handler mHandler;
	/** 返回操作字符串 */
	private String backAction;
	/** 当前操作Activity对象 */
	private String activityName;
	/** 界面基础View */
	private static View rootView;
	/** 返回按钮 */
	private ImageButton backBtn;
	/** 歌手列表解析对象 */
	private SingerListBean mSingerListBean;
	/** 显示加载更多 */ 
	private View footerView;
	/** 请求URL */
	private String url = null;
	
	 /** 显示动画的组件   */ 
    private AnimImageView imgDance; 
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
				.get(IntentAction.INTENT_ONLINE_SINGER_LIST);
		if (bundle.getString(KEY_TITLE) != null) {
			titleName = bundle.getString(KEY_TITLE);
		}
		if (bundle.getInt(KEY_SEARCH) > 0) {
			mSearch_Id = bundle.getInt(KEY_SEARCH);
		}

		boolean hasBackBtn = bundle.getBoolean(KEY_BACK_BTN);
		backAction = bundle.getString(KEY_BACK_ACTION);
		activityName = bundle.getString(KEY_ACTIVITY_NAME);

		rootView = LayoutInflater.from(this).inflate(
				R.layout.online_singer_list, null);
		setContentView(rootView);
		CommonUtils.setTitle(rootView, titleName, hasBackBtn, false);

		findView();
		init();

	}

	/** 加载界面控件 */
	private void findView() {

		mSingerListLV = (ListView) findViewById(R.id.online_singer_listview);

		backBtn = (ImageButton) findViewById(R.id.back_btn);

		mSingerListLV.setFooterDividersEnabled(true);
		footerView = LayoutInflater.from(this).inflate(
				R.layout.online_recommend_listview_footer, null);
		android.widget.AbsListView.LayoutParams lp = new android.widget.AbsListView.LayoutParams(
				-1, CommonUtils.dip2px(this, 55));
		footerView.setLayoutParams(lp);
		mSingerListLV.addFooterView(footerView, null, true);
		footerView.setVisibility(View.GONE);
		
		imgDance = (AnimImageView) super.findViewById(R.id.ImgDance); 
		if(TianlApp.isBacktoSingerList){
			TianlApp.isBacktoSingerList = false;
			if(imgDance.getVisibility()== View.VISIBLE){
				imgDance.setVisibility(View.GONE);
				imgDance.stop(); 
			}
		}
		initNetError();
	}

	/**
	 * 初始化界面控制对象，并加载数据
	 * */
	private void init() {
		mLocalActivityManager = new LocalActivityManager(this, true);
		mAdapter = new OnlineSingerListAdapter(this, mSingerListLV);
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case LOAD_COMPLETE:
					if (mSingerListBean != null) {
						if(imgDance.getVisibility()== View.VISIBLE){
							imgDance.setVisibility(View.GONE);
							imgDance.stop(); 
						}
						if(footerView.getVisibility()==View.GONE)
							footerView.setVisibility(View.VISIBLE);
						mAdapter.setDatas(mSingerListBean.getDataList());
						if(mSingerListBean.isHasNext()){
							setFootviewState(false, true);
						} else {
							setFootviewState(false, false);
						}	
						mAdapter.notifyDataSetChanged();
						saveLocalData(url);
					}
					break;
				}
			}
		};
		mSingerListLV.setAdapter(mAdapter);
		mSingerListLV.setOnItemClickListener(this);
		try {
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					getSingerListData(80, 1);
				}
			});
		} catch (Exception e) {
			//e.printStackTrace();
		}

		backBtn.setOnClickListener(this);

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
				getSingerListData(80, 1);
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
					+ mAdapter.getmMusicList().size()+ getString(R.string.total_string);
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
		itemClick(arg2);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.back_btn:
			back();
			break;	
		}
	}

	@Override
	public void onBackPressed() {
		back();
	}

	/**
	 * 当前界面条目点击出发事件
	 * 
	 * @param arg2
	 *            int 当前操作Item索引
	 * */
	public void itemClick(int arg2) {
		//无网络时提示
		boolean hasNetwork = CommonUtils.isHasNetwork(SingerListActivity.this);
		if (!hasNetwork) {
			Toast.makeText(SingerListActivity.this, R.string.no_network, 0).show();
			return;
		}
		
		if (arg2 == mAdapter.getmMusicList().size() ) { // 如果是页脚
			if(mSingerListBean.isHasNext()){
				setFootviewState(true, true);
				getSingerListData(80, mSingerListBean.getNextPage());
			}	
		}else{
			goToSingerDetailActivity(arg2);
		}	
	}

	/**
	 * 返回按钮触发事件
	 * */
	private void back() {
		Intent intent = new Intent(backAction);
		intent.putExtra(BaseActivity.INTENT_ACTIVITY_NAME, activityName);
		TianlApp.activityBundles.remove(IntentAction.INTENT_ONLINE_SINGER_LIST);
		int level = TianlApp.newInstance().getPageLevel();
		Bundle bundle = new Bundle();
		bundle.putString(OnlineActivity.CURRENT_TAB, "SingerActivity");
		TianlApp.activityBundles.put(IntentAction.INTENT_ONLINE_ACTIVITY, bundle);
		BaseActivity.newInstance().showActivity(intent,
				level - 1 == 0 ? 1 : level - 1);
	}

	/**
	 * 获取歌手列表数据
	 * 
	 * @author Erica
	 * @param _ty
	 *            int 请求类型
	 * @param _page
	 *            int 请求页面
	 * @note
	 * */
	public boolean getSingerListData(int _ty, int _page) {

		url = null;
		url = CommonUtils.getSingerListDataURL(_ty, _page, mSearch_Id);
		Lg.d("url  =======", "url = == " + url);
		if(readLocalData(url)){
			mHandler.sendEmptyMessage(LOAD_COMPLETE);
		}else{
			if(!CommonUtils.isHasNetwork(TianlApp.newInstance())){
				imgDance.setVisibility(View.GONE);
				imgDance.stop();
				no_network_tips.setVisibility(View.VISIBLE);
				return false;
			}else{
				imgDance.setVisibility(View.VISIBLE);
				no_network_tips.setVisibility(View.GONE);
			}
		}
		OnlineMusicManager.getInstence().getSingerListData(this,
				new OnDataPreparedListener<SingerListBean>() {
					@Override
					public void onDataPrepared(SingerListBean data) {
						if (data != null) {
							
							if (mSingerListBean != null) {
								if (mSingerListBean.equals(data)) {
									return;
								}
							}
							mSingerListBean = data;
							mHandler.sendEmptyMessage(LOAD_COMPLETE);
							
							Lg.d("mSingerListBean  =======",
									"mSingerListBean = == "
											+ mSingerListBean.getDataList().size());
						}else {
							Lg.d("getSingerListData() == null");
							return;
						}
					}
				}, url);
		return true;
	}

	/**
	 * 进入歌手详细信息界面
	 * 
	 * @param arg2
	 *            int 当前选择歌手对象索引
	 * */
	public void goToSingerDetailActivity(int arg2) {
		Intent intent = new Intent(IntentAction.INTENT_ONLINE_SINGER_DETAIL);
		intent.putExtra(BaseActivity.INTENT_ACTIVITY_NAME,
				"SingerDetailActivity");
		if (TianlApp.activityBundles != null) {
			Bundle bundle = new Bundle();
			bundle.putString(SingerDetailActivity.KEY_TITLE, mAdapter.getmMusicList().get(arg2).getAuthor());
			bundle.putString(SingerDetailActivity.KEY_SEARCH, mAdapter.getmMusicList().get(arg2).getAuthor());
			if (mAdapter.getmMusicList().get(arg2).getImgUrl() != null) {
				bundle.putString(SingerDetailActivity.KEY_IMAGE,
						mAdapter.getmMusicList().get(arg2).getImgUrl());
			}
			bundle.putString(SingerDetailActivity.KEY_BACK_ACTION,
					IntentAction.INTENT_ONLINE_SINGER_LIST);
			bundle.putString(SingerDetailActivity.KEY_ACTIVITY_NAME,
					"SingerListActivity");
			bundle.putBoolean(SingerDetailActivity.KEY_BACK_BTN, true);
			TianlApp.activityBundles.put(IntentAction.INTENT_ONLINE_SINGER_DETAIL,
					bundle);
		}
		int level = TianlApp.newInstance().getPageLevel();
		BaseActivity.newInstance().showActivity(intent, level + 1);
	}
	
	private boolean saveLocalData(String url){
		try {
			NetCache.saveCache(mSingerListBean, url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		return true;
	}
	
	private boolean readLocalData(String url){
		try {
			mSingerListBean = NetCache.readCache(url);		
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
	
	public void onPause() {
	    super.onPause();
		//友盟统计 日志 add by perry 2012-10-23
	   // MobclickAgent.onPause(this);
	}
}
