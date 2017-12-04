package com.tiantiankuyin.component.activity.online;

import java.io.IOException;
import java.lang.ref.SoftReference;

import android.app.Activity;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.tiantiankuyin.bean.SingerInfoVO;
import com.tiantiankuyin.component.activity.BaseActivity;
import com.tiantiankuyin.database.bll.OnDataPreparedListener;
import com.tiantiankuyin.database.bll.OnlineMusicManager;
import com.tiantiankuyin.net.EasouAsyncImageLoader;
import com.tiantiankuyin.net.NetCache;
import com.tiantiankuyin.net.EasouAsyncImageLoader.ILoadedImage;
import com.tiantiankuyin.para.IntentAction;
import com.tiantiankuyin.utils.CommonUtils;
import com.tiantiankuyin.utils.Lg;
import com.tiantiankuyin.R;
import com.tiantiankuyin.TianlApp;

/**
 * 在线模块-歌手简介
 * @author Erica
 */
public class SingerIntroActivity extends Activity implements OnClickListener {
	/** 日志打印标志位 */
	public static final String ACTIVITY_ID = "SingerIntroActivity";
	/** 标题索引 */
	public static final String KEY_TITLE = "TITLE_NAME";
	/** 图片索引 */
	public static final String KEY_IMAGE = "IMAGE_NAME";
	/** 搜索关键字索引 */
	public static final String KEY_SEARCH = "SEARCH_NAME";
	/** 是否有返回操作 索引 */
	public static final String KEY_BACK_BTN = "BACK_BTN";
	/** 返回操作索引 */
	public static final String KEY_BACK_ACTION = "BACK_ACTION";
	/** 当前操作Activity对象索引 */
	public static final String KEY_ACTIVITY_NAME = "ACTIVITY_NAME";
	/** 歌手名 */
	private String titleName;
	/** 歌手头像URL */
	private String mImage;
	/** 歌手详细信息搜索关键字 */
	private String mSearch_Id;
	/** 返回操作标志 */
	private String backAction;
	/** 当前操作Activity对象 */
	private String activityName;
	/** 返回按钮 ImageButton */
	private ImageButton backBtn;
	/** 歌手头像 ImageView */
	private ImageView mDetailImage;
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
	/** 简介 TextView */
	private TextView mIntroTxt;
	/** 加载界面图片类 */
	private ILoadedImage iLoadedImage;
	/** 歌手详细信息集合 */
	public static SingerInfoVO mSingerInfoVO;
	/** 界面消息处理类 */
	private static Handler mHandler;
	/** 加载完成标志位 */
	public static final int LOAD_COMPLETE = 0x1;
	/** 界面基础View */
	private static View rootView;
	private String url = null;

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
				.get(IntentAction.INTENT_ONLINE_SINGER_INTRO);
		if (bundle.getString(KEY_TITLE) != null) {
			titleName = bundle.getString(KEY_TITLE);
		}
		if (bundle.getString(KEY_IMAGE) != null) {
			mImage = bundle.getString(KEY_IMAGE);
		}
		if (bundle.getString(KEY_SEARCH) != null) {
			mSearch_Id = bundle.getString(KEY_SEARCH);
		}

		boolean hasBackBtn = bundle.getBoolean(KEY_BACK_BTN);
		backAction = bundle.getString(KEY_BACK_ACTION);
		activityName = bundle.getString(KEY_ACTIVITY_NAME);

		rootView = LayoutInflater.from(this).inflate(
				R.layout.online_singer_intro, null);
		setContentView(rootView);
		CommonUtils.setTitle(rootView, titleName, hasBackBtn, false);
		findView();

	}

	/**
	 * 设置界面数据 歌手简介集合
	 * 
	 * @param _mSingerInfoVO
	 *            SingerInfoVO 歌手简介集合对象
	 * */
	public static void setSingerInfoVO(SingerInfoVO _mSingerInfoVO) {
		mSingerInfoVO = _mSingerInfoVO;
	}

	/**
	 * 加载界面控件
	 * */
	private void findView() {

		mSingerName = (TextView) findViewById(R.id.online_singername_txt);
		mbloodType = (TextView) findViewById(R.id.online_singerbloodType_txt);
		mConstellation = (TextView) findViewById(R.id.online_singerconstellation_txt);
		mBirthday = (TextView) findViewById(R.id.online_singerbirth_txt);
		mStature = (TextView) findViewById(R.id.online_singerstature_txt);
		mIntroTxt = (TextView) findViewById(R.id.online_singerintro_content);

		backBtn = (ImageButton) findViewById(R.id.back_btn);
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case LOAD_COMPLETE:
					showSingerInfo();
					saveLocalData(url);
					break;
				}

			}
		};
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
							.findViewWithTag(saveName);
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

		try {
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					if (mSingerInfoVO != null&&mSingerInfoVO.getSinger().equals(mSearch_Id)) {
						showSingerInfo();
					} else {
						getSingerData(mSearch_Id, 150);
					}
				}
			});
		} catch (Exception e) {
			//e.printStackTrace();
		}

		backBtn.setOnClickListener(this);
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
	 * 返回按钮操作
	 * */
	private void back() {
		Intent intent = new Intent(backAction);
		intent.putExtra(BaseActivity.INTENT_ACTIVITY_NAME, activityName);
		TianlApp.activityBundles.remove(IntentAction.INTENT_ONLINE_SINGER_INTRO);
		int level = TianlApp.newInstance().getPageLevel();
		BaseActivity.newInstance().showActivity(intent,
				level - 1 == 0 ? 1 : level - 1);
	}

	/**
	 * 获取歌手信息数据
	 * 
	 * @author Erica
	 * @note
	 * */
	public boolean getSingerData(String _name, int _ty) {

		url = null;
		url = CommonUtils.getSingerDataURL(_name, _ty);
		Lg.d("url  =======", "url = == " + url);
		if(readLocalData(url)){
			mHandler.sendEmptyMessage(LOAD_COMPLETE);
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
							mHandler.sendEmptyMessage(LOAD_COMPLETE);
							SingerIntroActivity.setSingerInfoVO(mSingerInfoVO);
						}else {
							Lg.d("getSingerDetailData() == null");
							return;
						}
					}
				}, url);
		return true;
	}

	/**
	 * 加载界面歌手信息数据
	 * */
	public void showSingerInfo() {
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
		if (mSingerInfoVO.getIntro() != null) {
			mIntroTxt.setText(mSingerInfoVO.getIntro());
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
	
	private boolean saveLocalData(String url){
		try {
			NetCache.saveCache(mSingerInfoVO, url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		return true;
	}
	
	private boolean readLocalData(String url){
		try {
			mSingerInfoVO = NetCache.readCache(url);		
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
	
	public void onResume() {
	    super.onResume();
		//友盟统计 日志 add by perry 2012-10-23
	    //MobclickAgent.onResume(this);
	}

	public void onPause() {
	    super.onPause();
		//友盟统计 日志 add by perry 2012-10-23
	   // MobclickAgent.onPause(this);
	}
}
