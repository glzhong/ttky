package com.tiantiankuyin.component.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.tiantiankuyin.component.activity.online.RecommendActivity;
import com.tiantiankuyin.component.activity.online.RecommendExActivity;
import com.tiantiankuyin.R;

/**
 * 进度展示页
 * 
 * @author DK
 * 
 */
public class DownloadActivity extends Activity implements OnCheckedChangeListener{

	private RadioButton downloadingBtn;// 正在进行
	private RadioButton downloadedBtn;// 已经完成
	private ViewPager viewPager;
	private ArrayList<View> mViewLists;
	private LocalActivityManager mLocalActivityManager;
	private View downloadingActivity;
	private View downdloadedActivity;
	private RadioGroup radioGroup;

	public static DownloadActivity instance;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.download);
		findview();
		init();
		instance = this;
	}

	@Override
	protected void onResume() {
		super.onResume();
		//友盟统计 日志 add by perry 2012-10-23
	    //MobclickAgent.onResume(this);
		mLocalActivityManager.dispatchResume();

		// 初始化子Activity
		// 正在进行
		if (downloadingActivity == null) {
			Intent intent = new Intent(this, DownloadingActivity.class);
			Window subActivity = mLocalActivityManager.startActivity(
					RecommendActivity.ACTIVITY_ID, intent);
			downloadingActivity = subActivity.getDecorView();
			downloadingActivity.setTag(DownloadingActivity.ACTIVITY_ID);
			// 添加到ViewPager数据集中
			this.mViewLists.add(downloadingActivity);
		}
		// 已完成
		if (downdloadedActivity == null) {
			Intent intent = new Intent(this, DownloadedActivity.class);
			Window subActivity = mLocalActivityManager.startActivity(
					RecommendExActivity.ACTIVITY_ID, intent);
			downdloadedActivity = subActivity.getDecorView();
			downdloadedActivity.setTag(DownloadedActivity.ACTIVITY_ID);
			// 添加到ViewPager数据集中
			this.mViewLists.add(downdloadedActivity);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		//友盟统计 日志 add by perry 2012-10-23
	    //MobclickAgent.onPause(this);
		mLocalActivityManager.dispatchPause(isFinishing());
	}

	private void findview() {
		radioGroup = (RadioGroup) findViewById(R.id.downloadRG);
		downloadingBtn = (RadioButton) findViewById(R.id.downloadingRB);
		downloadedBtn = (RadioButton) findViewById(R.id.downloadedRB);
		viewPager = (ViewPager) findViewById(R.id.viewPager);
	}

	private void init() {
		mLocalActivityManager = new LocalActivityManager(this, true);
		mViewLists = new ArrayList<View>();
		viewPager.setAdapter(new OnlinePagerAdapter(mViewLists));
		downloadingBtn.setChecked(true);
		radioGroup.setOnCheckedChangeListener(this);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				int currentItem = viewPager.getCurrentItem();
				switchOnlinePager(currentItem);
			}
		});
		downloadingBtn.setChecked(true);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN
				&& event.getRepeatCount() == 0) {
			finish();
			return true;
		} 
		return false;
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
	
	private void switchOnlinePager(int pagerIndex) {
		switch (pagerIndex) {
		case 0:
			this.downloadingBtn.setChecked(true);
			break;
		case 1:
			this.downloadedBtn.setChecked(true);
			break;
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.downloadingRB:
			viewPager.setCurrentItem(0);
			downloadingBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.line);
			downloadedBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			break;
		case R.id.downloadedRB:
			viewPager.setCurrentItem(1);
			downloadedBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.line);
			downloadingBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			break;
		}
	}
}
