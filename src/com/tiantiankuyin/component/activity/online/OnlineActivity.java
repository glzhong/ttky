package com.tiantiankuyin.component.activity.online;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

/*import com.android.easou.epay.EpayInit;*/




import com.tiantiankuyin.bean.OlSongVO;
import com.tiantiankuyin.component.activity.BaseActivity;
import com.tiantiankuyin.para.IntentAction;
import com.tiantiankuyin.para.UserData;
import com.tiantiankuyin.play.PlayLogicManager;
import com.tiantiankuyin.utils.CommonUtils;
import com.tiantiankuyin.utils.Lg;
//import com.umeng.analytics.MobclickAgent;
import com.tiantiankuyin.R;
import com.tiantiankuyin.TianlApp;

/**
 * 在线模块管理，用于管理在线模块所有Activity（推荐、榜单、分类、歌手、精选集）
 * 
 * @author DK
 * 
 */
public class OnlineActivity extends Activity {
	/** 当前显示的tab，此类为static，在程序生命周期内，具有记忆功能 */
	private static String mCurrentTab = "recommend_rdo";

	private LocalActivityManager mLocalActivityManager;
	public static ViewPager mPager;
	/** 用于存放5个Activity对应的View（推荐、榜单、分类、歌手、精选集） */
	private List<View> mViewLists;
	/** ViewPager数据适配器 */
	private OnlinePagerAdapter mPageraAdapter;
	/** 推荐页面 */
	private View recommendActivity;
	/** 榜单页面 */
	private View hotSaleActivity;
	
	private View monthVipActivity;
	/** 分类页面 */
	private View categoryActivity;
	/** 歌手页面 */
	//private View singerActivity;
	/** 精选集页面 */
	private View omnibusActivity;
	/** 顶部导航按钮组 */
	private RadioGroup mRadioGroup;
	/** 推荐按钮 */
	private RadioButton recommend_rdo;
	/** 榜单按钮 */
	private RadioButton hotsale_rdo;
	
	private RadioButton monthVip_rdo;
	/** 分类按钮 */
 	private RadioButton category_rdo;//会员专区
	/** 歌手按钮 */
//	private RadioButton singer_rdo;
	/** 精选集按钮 */
	private RadioButton omnibus_rdo;
	private ImageView floatTip;
	/** 无网络提示 */
	private LinearLayout no_network_tips;
	/** 无网络刷新button */
	private Button fresh;
	public static final String CURRENT_TAB = "CURRENT_TAB";

	public static OnlineActivity mOnlineActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mOnlineActivity = this;
		setContentView(R.layout.online);
		init();
		
	}

	@Override
	protected void onResume() {

		super.onResume();
		// 友盟统计 日志 add by perry 2012-10-23
		//MobclickAgent.onResume(this);
		initNetData();
		if(mCurrentTab.equals("category_rdo")){
			category_rdo.setChecked(true);
			mCurrentTab = "category_rdo";
		}else{
			mCurrentTab="recommend_rdo";
			this.recommend_rdo.setChecked(true);
		}
		

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 友盟统计 日志 add by perry 2012-10-23
		//MobclickAgent.onPause(this);
		mLocalActivityManager.dispatchPause(isFinishing());
	}

	/**
	 * 初始化
	 */
	
	private void init() {
		findView();
		mLocalActivityManager = new LocalActivityManager(this, true);
		mPager = (ViewPager) findViewById(R.id.viewPager); // 获取viewPager
		mViewLists = new ArrayList<View>(); // 初始化Activity集合
		mPageraAdapter = new OnlinePagerAdapter(mViewLists); // 初始化viewPager适配器
		mPager.setAdapter(mPageraAdapter);// 为viewPager添加适配器
		mRadioGroup.setOnCheckedChangeListener(new TabitemChangeedListener());
		mPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int state) {
			}

			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
			}

			@Override
			public void onPageScrollStateChanged(int position) {
				int currentItem = mPager.getCurrentItem();
				switchOnlinePager(currentItem);
			}
		});
		if (!UserData.getInstence().isSavedOnlieMsg()) {
			if (CommonUtils.isHasNetwork(this)) {
				mPager.setVisibility(View.VISIBLE);
				no_network_tips.setVisibility(View.GONE);
			} else {
				mPager.setVisibility(View.GONE);
				mRadioGroup.setVisibility(View.GONE);
				no_network_tips.setVisibility(View.VISIBLE);
			}
		}

	}

	/**
	 * 初始化网络异常控件
	 */
	private void initNetError() {
		no_network_tips = (LinearLayout) findViewById(R.id.no_network_tips);
		fresh = (Button) findViewById(R.id.fresh);
		fresh.setOnClickListener(freshListener);
	}

	/**
	 * 初始化网络异常控件
	 */
	private void initNetData() {
		mLocalActivityManager.dispatchResume();
		if ((!UserData.getInstence().isSavedOnlieMsg() && CommonUtils
				.isHasNetwork(this))
				|| (UserData.getInstence().isSavedOnlieMsg())) {
			if (!UserData.getInstence().isSavedOnlieMsg()) {
				UserData.getInstence().setSavedOnlineMsg(true);
			}
			if (recommendActivity == null) {
				Intent intent = new Intent(this, RecommendActivity.class);
				Window subActivity = mLocalActivityManager.startActivity(
						RecommendActivity.ACTIVITY_ID, intent);
				recommendActivity = subActivity.getDecorView();
				recommendActivity.setTag(RecommendActivity.ACTIVITY_ID);
				// 添加到ViewPager数据集中
				this.mViewLists.add(recommendActivity);
			}
			// 榜单
			if (hotSaleActivity == null) {
				Intent intent = new Intent(this, RecommendExActivity.class);
				Window subActivity = mLocalActivityManager.startActivity(
						RecommendExActivity.ACTIVITY_ID, intent);
				hotSaleActivity = subActivity.getDecorView();
				hotSaleActivity.setTag(RecommendExActivity.ACTIVITY_ID);
				// 添加到ViewPager数据集中
				this.mViewLists.add(hotSaleActivity);
			}
			// 精选集
			if (omnibusActivity == null) {
				Intent intent = new Intent(this, RecommendEExActivity.class);
				Window subActivity = mLocalActivityManager.startActivity(
						RecommendEExActivity.ACTIVITY_ID, intent);
				omnibusActivity = subActivity.getDecorView();
				omnibusActivity.setTag(RecommendEExActivity.ACTIVITY_ID);
				// 添加到ViewPager数据集中
				this.mViewLists.add(omnibusActivity);
			}
	
//			if (monthVipActivity == null) {
//							Intent intent = new Intent(this, MonthVipActivity.class);
//							Window subActivity = mLocalActivityManager.startActivity(
//									MonthVipActivity.ACTIVITY_ID, intent);
//							monthVipActivity = subActivity.getDecorView();
//							monthVipActivity.setTag(MonthVipActivity.ACTIVITY_ID);
//							 //添加到ViewPager数据集中
//							this.mViewLists.add(monthVipActivity);
//			}	
			/*//1元包月
			if (singerActivity == null) {
				Intent intent = new Intent(this, SingerActivity.class);
				Window subActivity = mLocalActivityManager.startActivity(
						SingerActivity.ACTIVITY_ID, intent);
				singerActivity = subActivity.getDecorView();
				singerActivity.setTag(SingerActivity.ACTIVITY_ID);
				//添加到ViewPager数据集中
				this.mViewLists.add(singerActivity);
			}*/
			// 4元包月
			if (categoryActivity == null) {
				Intent intent = new Intent(this, CategoryActivity.class);
				Window subActivity = mLocalActivityManager.startActivity(
						CategoryActivity.ACTIVITY_ID, intent);
				categoryActivity = subActivity.getDecorView();
				categoryActivity.setTag(CategoryActivity.ACTIVITY_ID);
				 //添加到ViewPager数据集中
				this.mViewLists.add(categoryActivity);
			}		
			Bundle bundle = TianlApp.activityBundles
					.get(IntentAction.INTENT_ONLINE_ACTIVITY);
			if (bundle != null) {
				String currentTab = bundle.getString(CURRENT_TAB);
				if (mPager != null && currentTab != null
						&& !"".equals(currentTab)) {
					if ("HotSaleActivity".equals(currentTab)) {
						hotsale_rdo.setChecked(true);
						mCurrentTab = "hotsale_rdo";
					} else if ("OmnibusActivity".equals(currentTab)) {
						omnibus_rdo.setChecked(true);
						mCurrentTab = "omnibus_rdo";
					}else if("OmnibusActivity".equals(currentTab)){
						monthVip_rdo.setChecked(true);
						mCurrentTab="monthVip_rdo";
					}else if("CategoryActivity".equals(currentTab)){
						category_rdo.setChecked(true);
						mCurrentTab = "category_rdo";
					}else {// 默认选中“推荐”页
						recommend_rdo.setChecked(true);
						mCurrentTab = "recommend_rdo";
					}
					
					/* else if("SingerActivity".equals(currentTab)){
							singer_rdo.setChecked(true);
							mCurrentTab = "singer_rdo";
							*/
						
				}
			}

			if (mCurrentTab != null && !"".equals(mCurrentTab)) {
				if ("hotsale_rdo".equals(mCurrentTab)) {
					hotsale_rdo.setChecked(true);
				}  else if ("omnibus_rdo".equals(mCurrentTab)) {
					omnibus_rdo.setChecked(true);
				} else if ("recommend_rdo".equals(mCurrentTab)) {
					recommend_rdo.setChecked(true);
				}else if("monthVip_rdo".equals(mCurrentTab)){
					monthVip_rdo.setChecked(true);
				}
				else if("category_rdo".equals(mCurrentTab)){
					category_rdo.setChecked(true);
				}
				/*else if("singer_rdo".equals(mCurrentTab)){
					singer_rdo.setChecked(true);
				}*/
			}
		}
	}

	/**
	 * 获取控件
	 */
	private void findView() {
		mRadioGroup = (RadioGroup) findViewById(R.id.tab_group_online_menu);
		recommend_rdo = (RadioButton) findViewById(R.id.recommend);
		hotsale_rdo = (RadioButton) findViewById(R.id.hotsale);
		monthVip_rdo=(RadioButton)findViewById(R.id.monthVip);
		category_rdo = (RadioButton) findViewById(R.id.category);
//		singer_rdo = (RadioButton) findViewById(R.id.singer);
		omnibus_rdo = (RadioButton) findViewById(R.id.omnibus);
		floatTip = new ImageView(this);
		initNetError();
	}

	/**
	 * ViewPager数据适配器
	 * 
	 * @author DK
	 * 
	 */
	private class OnlinePagerAdapter extends PagerAdapter {
		private List<View> viewList;

		public OnlinePagerAdapter(List<View> viewList) {
			this.viewList = viewList;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(viewList.get(arg1));
		}

		@Override
		public int getCount() {
			return viewList.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(viewList.get(arg1), 0);
			return viewList.get(arg1);
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (arg1);
		}

		@Override
		public Parcelable saveState() {
			return null;
		}
	}

	private void recordCurrentTab(String tabActivityName) {
		Bundle bundle = new Bundle();
		bundle.putString(OnlineActivity.CURRENT_TAB, tabActivityName);
		TianlApp.activityBundles.put(IntentAction.INTENT_ONLINE_ACTIVITY, bundle);
	}

	/**
	 * 顶部导航切换
	 * 
	 * @author DK
	 * 
	 */
	private class TabitemChangeedListener implements OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			int startLeft = 5;
			switch (checkedId) {
			case R.id.recommend:
				mPager.setCurrentItem(0);
				recommend_rdo.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
						R.drawable.line);
				hotsale_rdo.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
						R.drawable.online_top_current_pag);
				
				omnibus_rdo.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
						R.drawable.online_top_current_pag);
				monthVip_rdo.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
						R.drawable.online_top_current_pag);
				/*singer_rdo.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
						R.drawable.online_top_current_pag);*/
				category_rdo.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
						R.drawable.online_top_current_pag);
				CommonUtils.moveFrontBg(floatTip, startLeft, 20, 0, 0);
				startLeft = 20;
				recordCurrentTab("RecommendActivity");
				break;
			case R.id.hotsale:
				mPager.setCurrentItem(1);
				recommend_rdo.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
						R.drawable.online_top_current_pag);
				hotsale_rdo.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
						R.drawable.line);
				omnibus_rdo.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
						R.drawable.online_top_current_pag);
				monthVip_rdo.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
						R.drawable.online_top_current_pag);
				/*singer_rdo.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
						R.drawable.online_top_current_pag);	*/	
				category_rdo.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
						R.drawable.online_top_current_pag);		
				CommonUtils.moveFrontBg(floatTip, startLeft,
						floatTip.getWidth() + 20, 0, 0);
				startLeft = floatTip.getWidth();
				recordCurrentTab("HotSaleActivity");
				break;
			case R.id.monthVip:
				mPager.setCurrentItem(3);
				recommend_rdo.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
						R.drawable.online_top_current_pag);
				hotsale_rdo.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
						R.drawable.online_top_current_pag);
				monthVip_rdo.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
						R.drawable.line);
				category_rdo.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
						R.drawable.online_top_current_pag);	
				/*singer_rdo.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
						R.drawable.online_top_current_pag);*/
				omnibus_rdo.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
						R.drawable.online_top_current_pag);
				CommonUtils.moveFrontBg(floatTip, startLeft,
						floatTip.getWidth() * 2 + 20, 0, 0);
				startLeft = floatTip.getWidth() * 2;
				recordCurrentTab("MonthVipActivity");
				break;
		 
			case R.id.category:
				mPager.setCurrentItem(4);
				recommend_rdo.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
						R.drawable.online_top_current_pag);
				hotsale_rdo.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
						R.drawable.online_top_current_pag);
				category_rdo.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
						R.drawable.line);
				monthVip_rdo.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
						R.drawable.online_top_current_pag);
////				singer_rdo.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
////						R.drawable.online_top_current_pag);
				omnibus_rdo.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
						R.drawable.online_top_current_pag);
				CommonUtils.moveFrontBg(floatTip, startLeft,
						floatTip.getWidth() * 3 + 20, 0, 0);
				startLeft = floatTip.getWidth() * 3;
				recordCurrentTab("CategoryActivity");
				break;
			/*case R.id.singer:
				mPager.setCurrentItem(3);
				recommend_rdo.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
						R.drawable.online_top_current_pag);
				hotsale_rdo.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
						R.drawable.online_top_current_pag);
				singer_rdo.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
						R.drawable.line);
				omnibus_rdo.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
				R.drawable.online_top_current_pag);
				category_rdo.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
						R.drawable.online_top_current_pag);
				CommonUtils.moveFrontBg(floatTip, startLeft,
					floatTip.getWidth() * 3 + 20, 0, 0);
				startLeft = floatTip.getWidth() * 3;
				recordCurrentTab("SingerActivity");
				break;*/
			case R.id.omnibus:
				mPager.setCurrentItem(2);
				recommend_rdo.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
						R.drawable.online_top_current_pag);
				hotsale_rdo.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
						R.drawable.online_top_current_pag);				 				 
				omnibus_rdo.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
						R.drawable.line);
				monthVip_rdo.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
						R.drawable.online_top_current_pag);	
				category_rdo.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
						R.drawable.online_top_current_pag);
				CommonUtils.moveFrontBg(floatTip, startLeft,
						floatTip.getWidth() * 4 + 20, 0, 0);
				startLeft = floatTip.getWidth() * 4;
				recordCurrentTab("OmnibusActivity");
				break;
			}
		}
	}

	/**
	 * 用于在线页面切换tab时改变顶部导航栏
	 * 
	 * @param pagerID
	 *            当前要显示的页面ActivityID
	 */
	private void switchOnlinePager(int pagerIndex) {
		switch (pagerIndex) {
		case 0:
			mCurrentTab = "recommend_rdo";
			this.recommend_rdo.setChecked(true);
			break;
		case 1:
			mCurrentTab = "hotsale_rdo";
			this.hotsale_rdo.setChecked(true);
			break;
		case 2:
			mCurrentTab = "omnibus_rdo";
			this.omnibus_rdo.setChecked(true);
			break;
		case 3:
			mCurrentTab = "category_rdo";
			this.category_rdo.setChecked(true);
//			mCurrentTab = "monthVip_rdo";
//			this.monthVip_rdo.setChecked(true);
			break;		
		case 4:
			mCurrentTab = "category_rdo";
			this.category_rdo.setChecked(true);
			break;
		}
	}

	private View.OnClickListener freshListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (!CommonUtils.isHasNetwork(TianlApp.newInstance())) {// 无网络
				Toast.makeText(TianlApp.newInstance(),
						getString(R.string.has_no_net), Toast.LENGTH_SHORT)
						.show();// 提示当前无可用的网络
				return;
			} else {
				if (!UserData.getInstence().isSavedOnlieMsg()) {
					mPager.setVisibility(View.VISIBLE);
					mRadioGroup.setVisibility(View.VISIBLE);
					no_network_tips.setVisibility(View.GONE);
					initNetData();
				}
			}

		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		PayOnActivityResult(this, requestCode, resultCode, data);
	};

	public static void PayOnActivityResult(Activity activity, int requestCode,
			int resultCode, Intent data) {
		Lg.e("requestCode is " + requestCode + " , " + resultCode);
	}
/*
	public void getResult(Context context) {
		EpayInit.getInstance().getFeeResult(context);
		System.out.println("付费结果是："
				+ EpayInit.getInstance().getFeeResult(context));
	}
*/
	public static void PlayOnlineMusic(List<OlSongVO> olSongVOs, int position) {

		try {
			if (olSongVOs != null) {
				PlayLogicManager.newInstance().setOnlineMusicInfo(olSongVOs,
						position);
				PlayLogicManager.newInstance().play();
			}
		} catch (IllegalStateException e) {
			// e.printStackTrace();
		}
	}

}
