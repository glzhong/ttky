package com.tiantiankuyin.component.activity;

import java.util.ArrayList;
import java.util.List;

import android.R.color;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.tiantiankuyin.R;

public class GuideActivity extends Activity implements OnClickListener,
		OnPageChangeListener {
	private ViewPager vp;

	private ViewPagerAdapter vpAdapter;

	private List<View> views;
	/** 单例对象 用于全局调用 */
	public static GuideActivity instance;// add by Erica on 8.22

	// 引导布局资源
	private static final int[] guideLayouts = { R.layout.guide_new_layout,
			R.layout.guide_similar_layout, R.layout.guide_wifi_layout };

	// 底部小点图片

	private ImageView[] dots;

	// 记录当前选中位置

	private int currentIndex;

	/**
	 * 引导界面右下角的 跳过按钮
	 * 
	 * @author Perry
	 */
	private ImageView imageSkip;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		goToBaseActivity();
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.guide);
		instance = this;

		views = new ArrayList<View>();

		imageSkip = (ImageView) findViewById(R.id.guide_button);
		imageSkip.setOnClickListener(this);

		// 初始化引导图片列表

		for (int i = 0; i < guideLayouts.length; i++) {
			LayoutInflater inflater = (LayoutInflater) this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(guideLayouts[i], null);
			views.add(layout);
		}
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT);
		View skipLayout = new LinearLayout(this);
		skipLayout.setBackgroundColor(color.black);
		skipLayout.setLayoutParams(params);
		views.add(skipLayout);
		vp = (ViewPager) findViewById(R.id.guide_viewpager);

		// 初始化Adapter
		vpAdapter = new ViewPagerAdapter(views);
		vp.setAdapter(vpAdapter);
		// 绑定回调
		vp.setOnPageChangeListener(this);
		// 初始化底部小点
		initDots();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**
	 * 跳BaseActivity
	 * 
	 * @author Perry
	 */
	private void goToBaseActivity() {

		GuideActivity.this.finish();
	}

	private void initDots() {
		LinearLayout ll = (LinearLayout) findViewById(R.id.ll);
		dots = new ImageView[guideLayouts.length];

		// 循环取得小点图片
		for (int i = 0; i < guideLayouts.length; i++) {

			// 得到一个LinearLayout下面的每一个子元素
			dots[i] = (ImageView) ll.getChildAt(i);
			dots[i].setEnabled(false);// 都设为灰色
			dots[i].setOnClickListener(this);
			dots[i].setTag(i);// 设置位置tag，方便取出与当前位置对应
		}
		currentIndex = 0;
		dots[currentIndex].setEnabled(true);// 设置为白色，即选中状态
	}

	private void setCurView(int position)

	{
		if (position < 0 || position >= guideLayouts.length) {
			return;
		}
		vp.setCurrentItem(position);
	}

	private void setCurDot(int positon)

	{
		if (positon < 0 || positon > guideLayouts.length - 1
				|| currentIndex == positon) {
			return;
		}
		// 修改图片状态
		if (positon < guideLayouts.length - 1) {
			imageSkip.setImageResource(R.drawable.guide_skip);
		} else if (positon == guideLayouts.length - 1) {
			imageSkip.setImageResource(R.drawable.guide_start);
		}
		dots[positon].setEnabled(true);
		dots[currentIndex].setEnabled(false);
		currentIndex = positon;
	}

	// 当滑动状态改变时调用
	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	// 当前页面被滑动时调用
	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		//当滑倒最后一页时。跳转到客户端主界面。
		if (position+1 == guideLayouts.length && positionOffset > 0.4) {
			goToBaseActivity();
			return;
		}
	}

	// 当新的页面被选中时调用

	@Override
	public void onPageSelected(int arg0) {
		// 设置底部小点选中状态
		setCurDot(arg0);
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == imageSkip.getId()) {
			goToBaseActivity();
		} else {
			int position = (Integer) v.getTag();
			setCurView(position);
			setCurDot(position);
		}
	}

	public class ViewPagerAdapter extends PagerAdapter {

		// 界面列表

		private List<View> views;

		public ViewPagerAdapter(List<View> views) {
			this.views = views;
		}

		// 销毁arg1位置的界面

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(views.get(arg1));
		}

		@Override
		public void finishUpdate(View arg0) {
			// TODO Auto-generated method stub
		}
		// 获得当前界面数
		@Override
		public int getCount() {

			if (views != null) {
				return views.size();
			}
			return 0;
		}

		// 初始化arg1位置的界面

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(views.get(arg1), 0);
			return views.get(arg1);
		}

		// 判断是否由对象生成界面

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return (arg0 == arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			goToBaseActivity();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
}