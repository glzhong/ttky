package com.tiantiankuyin.component.activity.local;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tiantiankuyin.adapter.LocalScanBannerAdapter;
import com.tiantiankuyin.bean.Banner;
import com.tiantiankuyin.para.IntentAction;
import com.tiantiankuyin.scan.IMediaScannerListener;
import com.tiantiankuyin.scan.ScanEngine;
import com.tiantiankuyin.utils.CommonUtils;
import com.tiantiankuyin.view.LocalScanBannerGallery;
import com.tiantiankuyin.R;
import com.tiantiankuyin.TianlApp;


public class ScanActivity extends Activity implements IMediaScannerListener {

	private final static int UPDATE_SCAN_MEDIA_BEGIN = 1;
	private final static int UPDATE_SCANNING_MEDIA_COUNT = 2;
	private final static int UPDATE_SCAN_MEDIA_COMPLETED = 3;

	/** 启动自定义文件夹扫描 */
	private final static int RESULT_FOR_DIR_SCAN = 100;
	/** 扫描前的布局 */
	private static LinearLayout scanLayout; 
	/** 扫描时的布局 */
	private static LinearLayout scanningLayout; 
	/** 扫描后的布局 */
	private static LinearLayout scannedLayout; 
	/** 快速扫描按钮 */
	private Button quickScanBtn; 
	/** 自定义文件夹扫描按钮 */
	private Button dirScanBtn; 
	/** 扫描时的提示 */
	private static TextView scanningMsgTv; 
	/** 扫描进度条按钮 */
	private static ProgressBar scanningBar; 
	/** 后台扫描按钮 */
	private Button goBackgroundBtn; 
	/** 扫描结束的提示  */
	private static TextView completedMsgTv; 
	/** 去听歌的按钮 */
	private Button goPlayBtn; 

	private LocalScanBannerGallery scanBannerGallery;
	private LinearLayout bannerPointContainer;
	private LocalScanBannerAdapter scanBannerAdapter;
	/** 扫描页面中的手机 */
	private ImageView mobileImg;  
	/** 扫描页面的 放大镜 默认显示状态为 隐藏 */
	private static ImageView glassImg;  
	/** 扫描中的手机动画 */
	private static AnimationDrawable mobileAnim;
	private TranslateAnimation glassAnim;
	private static View rootView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		rootView = LayoutInflater.from(this).inflate(R.layout.local_scan, null);
		setContentView(rootView);
		findView();
		init();
	}

	@Override
	protected void onResume() {
		super.onResume();
		//友盟统计 日志 add by perry 2012-10-23
	    //MobclickAgent.onResume(this);
		if (scanBannerAdapter != null) {
			scanBannerGallery.startAutoSlide();
			scanBannerAdapter.notifyDataSetChanged();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		//友盟统计 日志 add by perry 2012-10-23
	    //MobclickAgent.onPause(this);
		if (scanBannerAdapter != null) {
			scanBannerGallery.cancelAutoSlide();
		}
	}

	/** 初始化控件 */
	public void findView() {
		scanLayout = (LinearLayout) findViewById(R.id.local_scan_scan_layout);
		quickScanBtn = (Button) findViewById(R.id.local_scan_quickScan);
		dirScanBtn = (Button) findViewById(R.id.local_scan_dirScan);

		scanningLayout = (LinearLayout) findViewById(R.id.local_scan_scanning_layout);
		scanningMsgTv = (TextView) findViewById(R.id.local_scan_scanning_msg);
		scanningBar = (ProgressBar) findViewById(R.id.local_scan_scanningProgress);
		goBackgroundBtn = (Button) findViewById(R.id.local_scan_goBackground);

		scannedLayout = (LinearLayout) findViewById(R.id.local_scan_scanned_layout);
		completedMsgTv = (TextView) findViewById(R.id.local_scan_completed_msg);
		goPlayBtn = (Button) findViewById(R.id.local_scan_goPlay);

		mobileImg = (ImageView) findViewById(R.id.local_scan_mobile);
		mobileImg.setImageResource(R.anim.local_scan_mobile_anim);
		mobileAnim = (AnimationDrawable) mobileImg.getDrawable();
		glassImg = (ImageView) findViewById(R.id.local_scan_glass);
		

		// ScanBanner 引导页
		scanBannerGallery = (LocalScanBannerGallery) findViewById(R.id.local_scan_banner_gallery);
		bannerPointContainer = (LinearLayout) findViewById(R.id.local_scan_point_container);

	}

	/** 初始化数据 */
	public void init() {
		// 头部初始化
		CommonUtils.setTitle(rootView, "本地扫描", false, false);

		quickScanBtn.setOnClickListener(listener);
		dirScanBtn.setOnClickListener(listener);
		goBackgroundBtn.setOnClickListener(listener);
		goPlayBtn.setOnClickListener(listener);
		ScanEngine.newInstance().setmMediaScannerListener(ScanActivity.this);

		switch (ScanEngine.scanViewState) {
		case 1:
			showScanningLayout();
			break;
		case 2:
			showScannedLayout();
			break;
		default:
			showScanLayout();
			break;
		}

		scanBannerAdapter = new LocalScanBannerAdapter(this);
		scanBannerGallery.setAdapter(scanBannerAdapter);
		initBanner(); // 初始化扫描Banner的图片等等。

	}

	private void initGlassAnimation() {
		glassAnim = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0f,
				Animation.RELATIVE_TO_PARENT, 0f, Animation.RELATIVE_TO_PARENT,
				-0.25f, Animation.RELATIVE_TO_PARENT, 0.3f);
		glassAnim.setInterpolator(new AccelerateDecelerateInterpolator());
		glassAnim.setRepeatCount(Animation.INFINITE);
		glassAnim.setRepeatMode(Animation.REVERSE);
		glassAnim.setDuration(1000);// 动画耗时 1秒;
	}

	private void mobileAnimationBegin() {
		// 初始化查询放大镜的动画
		 if (glassAnim == null) {
		initGlassAnimation();
		 }
		glassImg.startAnimation(glassAnim);
		mobileAnim.start();

	}

	private static void mobileAnimationStop() {
		glassImg.clearAnimation();
		mobileAnim.stop();
	}

	private void initBanner() {
		int[] drawableIDs = new int[] { R.drawable.local_scan_banner_1_img,
				R.drawable.local_scan_banner_2_img,
				R.drawable.local_scan_banner_3_img,
				R.drawable.local_scan_banner_4_img };
		List<Banner> bannerList = new ArrayList<Banner>();
		for (int id : drawableIDs) {
			Banner banner = new Banner();
			banner.setBannerImage(getResources().getDrawable(id));
			bannerList.add(banner);
		}
		scanBannerAdapter.setData(bannerList);

		// Banner 头部的 选中点
		final int bannerCount = bannerList.size();
		final ImageView[] imageViews = new ImageView[bannerCount];
		for (int i = 0; i < imageViews.length; i++) {
			imageViews[i] = new ImageView(this);
			imageViews[i].setPadding(5, 0, 5, 0);
			imageViews[i]
					.setImageResource(R.drawable.playview_point_img_default);// 设置选中点的背景图
			bannerPointContainer.addView(imageViews[i]);
		}
		if (imageViews.length > 0 && imageViews[0] != null) {
			imageViews[0]
					.setImageResource(R.drawable.playview_point_img_bright);
		}
		scanBannerGallery
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						imageViews[position % bannerCount == 0 ? bannerCount - 1
								: position % bannerCount - 1]
								.setImageResource(R.drawable.playview_point_img_default);
						imageViews[position % bannerCount]
								.setImageResource(R.drawable.playview_point_img_bright);
						imageViews[position % bannerCount == bannerCount - 1 ? 0
								: position % bannerCount + 1]
								.setImageResource(R.drawable.playview_point_img_default);
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {

					}
				});
		scanBannerGallery.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				scanBannerGallery.cancelAutoSlide();
				scanBannerGallery.startAutoSlide();
			}
		});
		scanBannerGallery.setSelection((int) (Integer.MAX_VALUE / 2)
				- (Integer.MAX_VALUE / 2) % bannerCount);
		scanBannerAdapter.notifyDataSetChanged();
	}

	private View.OnClickListener listener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.local_scan_quickScan) {
				//判断当前SD卡是否可用
				if(CommonUtils.isSDCardAvailable()){ //sd卡可用 
					showScanningLayout();
					new Thread() {
						public void run() {
							ScanEngine.newInstance().quickScan();
						};
					}.start();
				}else { //sd卡不可用
					CommonUtils.showToast(ScanActivity.this, "当前SD卡不可用",Toast.LENGTH_SHORT);
				}

			} else if (v.getId() == R.id.local_scan_dirScan) {
				if (CommonUtils.isSDCardAvailable()) { // sd卡可用
					Intent intent = new Intent();
					intent.setAction(IntentAction.INTENT_LOCAL_SCAN_FOLDER_ACTIVITY);
					startActivityForResult(intent, RESULT_FOR_DIR_SCAN);
				} else { // sd卡不可用
					CommonUtils.showToast(ScanActivity.this, "当前SD卡不可用",
							Toast.LENGTH_SHORT);
				}
			} else if (v.getId() == R.id.local_scan_goBackground) {
				finish();
			} else if (v.getId() == R.id.local_scan_goPlay) {
				ScanEngine.scanViewState = -1;
				finish();
			}
		}
	};

	/** 处理ScanFolderActivity 返回来的操作。 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case RESULT_FOR_DIR_SCAN:
			showScanningLayout();
			Bundle bundle = data.getExtras();
			final String[] mediaPaths = bundle.getStringArray("mediaPaths");
			if (CommonUtils.isSDCardAvailable()) { // sd卡可用
				new Thread() {
					public void run() {
						ScanEngine.newInstance().setmMediaScannerListener(
								ScanActivity.this);
						ScanEngine.newInstance().dirScan(mediaPaths);
					};
				}.start();
			} else { // sd卡不可用
				CommonUtils.showToast(ScanActivity.this, "当前SD卡不可用",
						Toast.LENGTH_SHORT);
			}
			break;

		default:
			break;
		}
	}

	/**
	 * 隐藏正在扫描界面和已扫描界面，展示扫描界面。
	 */
	private void showScanLayout() {
		scanLayout.setVisibility(View.VISIBLE);
		scanningLayout.setVisibility(View.GONE);
		scannedLayout.setVisibility(View.GONE);
	}

	/**
	 * 隐藏扫描界面和已扫描界面，展示正在扫描界面。
	 */
	private void showScanningLayout() {
		scanLayout.setVisibility(View.GONE);
		scanningLayout.setVisibility(View.VISIBLE);
		scannedLayout.setVisibility(View.GONE);
		scanningBar.setMax(ScanEngine.scanViewBarMax);
		scanningBar.setProgress(ScanEngine.scanViewBarCurrent);
		scanningMsgTv.setText(String.valueOf(ScanEngine.scanViewInsertNum));
		mobileAnimationBegin();
	}

	/**
	 * 隐藏扫描界面和正在扫描界面，展示已扫描界面。
	 */
	private static void showScannedLayout() {
		scanLayout.setVisibility(View.GONE);
		scanningLayout.setVisibility(View.GONE);
		scannedLayout.setVisibility(View.VISIBLE);
		completedMsgTv.setText(String.valueOf(ScanEngine.scanViewInsertNum));
		ScanEngine.scanViewState = -1;
		mobileAnimationStop();
	}

	private static Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UPDATE_SCAN_MEDIA_BEGIN:
				// 初始化进度条
				scanningBar.setMax(100);
				scanningBar.setProgress(0);

				// 初始化提示语
				scanningMsgTv.setText("0");
				break;
			case UPDATE_SCANNING_MEDIA_COUNT:
				int maxLen = msg.arg1;
				int currLen = msg.arg2;
				maxLen = maxLen > currLen ? maxLen : currLen + 2;// 若总长度少于100，则设置为100
				int songNum = (Integer) msg.obj;
				scanningBar.setMax(maxLen);
				scanningBar.setProgress(currLen);
				scanningMsgTv.setText(String.valueOf(songNum));
				break;
			case UPDATE_SCAN_MEDIA_COMPLETED:
				int totalSongNum = msg.arg1;
				completedMsgTv.setText(String.valueOf(totalSongNum));
				showScannedLayout();
				Toast.makeText(TianlApp.newInstance(), "扫描完成，共扫描到歌曲" + totalSongNum + "首", Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	@Override
	public void onBackPressed() {
		this.finish();
	}

	@Override
	public void onScanMediaAllCompleted(int songNum) {
		Message msg = new Message();
		msg.what = UPDATE_SCAN_MEDIA_COMPLETED;
		msg.arg1 = songNum;
		mHandler.sendMessage(msg);
		TianlApp.newInstance().sendBroadcast(new Intent(IntentAction.INTENT_BROADCAST_UPDATE_LOCAL_ACTIVITY));
	}

	@Override
	public void onScanningMediaCountChanged(int maxLen, int currLen, int songNum) {
		Message msg = new Message();
		msg.what = UPDATE_SCANNING_MEDIA_COUNT;
		msg.arg1 = maxLen;
		msg.arg2 = currLen;
		msg.obj = songNum;
		mHandler.sendMessage(msg);
	}

	@Override
	public void onScanMediaBegin() {
		Message msg = new Message();
		msg.what = UPDATE_SCAN_MEDIA_BEGIN;
		mHandler.sendMessage(msg);
	}

}
