package com.tiantiankuyin.component.activity.online;

import java.lang.ref.SoftReference;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.tiantiankuyin.component.activity.BaseActivity;
import com.tiantiankuyin.net.EasouAsyncImageLoader;
import com.tiantiankuyin.net.EasouAsyncImageLoader.ILoadedImage;
import com.tiantiankuyin.para.IntentAction;
import com.tiantiankuyin.utils.CommonUtils;
import com.tiantiankuyin.R;
import com.tiantiankuyin.TianlApp;

/**
 * 在线模块-精选集简介共用
 * @author Erica
 */
public class OmnibusIntroActivity extends Activity implements OnClickListener{
	
	/** 日志打印标志位 */
	public static final String ACTIVITY_ID = "OmnibusIntroActivity";
	/** 标题 索引 */
	public static final String KEY_TITLE = "TITLE_NAME";
	/** 标签索引 */
	public static final String KEY_TAG= "TAG_NAME";
	/** 简介索引 */
	public static final String KEY_INTRO= "INTRO_STRING";
	/** 图片索引 */
	public static final String KEY_IMAGE= "IMAGE_NAME";
	/** 返回键索引 */
	public static final String KEY_BACK_BTN = "BACK_BTN";
	/** 返回键操作索引 */
	public static final String KEY_BACK_ACTION = "BACK_ACTION";
	/** 当前操作界面对象索引 */
	public static final String KEY_ACTIVITY_NAME = "ACTIVITY_NAME";
	/** 精选集标题 */
	private String titleName;
	/** 精选集标签 */
	private String mTagText;
	/** 精选集简介 */
	private String mIntroText;
	/** 精选集图片 */
	private String mImage;
	/** 返回按钮 */
	private ImageButton backBtn;
	/** 精选集图片 ImageView*/
	private ImageView mDetailImage;
	/** 精选集标签 TextView*/
	private TextView mTagTextView;
	/** 精选集简介 TextView */
	private TextView mIntroContent;
	/** 加载图片对象 */
	private ILoadedImage iLoadedImage;
	/** 基础View对象 */
	private static View rootView;

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
				.get(IntentAction.INTENT_ONLINE_FEATRUED_SET_INTRO);
		if( bundle.getString(KEY_TITLE)!=null){
			titleName = bundle.getString(KEY_TITLE);
		}
		if( bundle.getString(KEY_IMAGE)!=null){
			mImage = bundle.getString(KEY_IMAGE);
		}
		if(bundle.getString(KEY_TAG)!=null){
			mTagText = bundle.getString(KEY_TAG);
		}else{
			mTagText = TianlApp.newInstance().getResources().getString(R.string.no_tag);
		}
		if(bundle.getString(KEY_INTRO)!=null){
			mIntroText = bundle.getString(KEY_INTRO);
		}
		
		rootView = LayoutInflater.from(this).inflate(R.layout.online_featured_set_intro, null);
		setContentView(rootView);
		CommonUtils.setTitle(rootView, titleName, bundle.getBoolean(KEY_BACK_BTN), false);
		
		findView();
		
	}
	/** 初始化界面对象 */
	private void findView() {
		mTagTextView = (TextView) findViewById(R.id.online_tiplist_txt);
		mIntroContent = (TextView) findViewById(R.id.online_featured_set_detail_content);
		backBtn = (ImageButton) findViewById(R.id.back_btn);
		mTagTextView.setText(mTagText);
		mIntroContent.setText(mIntroText);
		mDetailImage = (ImageView) findViewById(R.id.online_featured_set_detail_image);
		if(mImage!=null){
			mDetailImage.setTag(CommonUtils.MD5(mImage));
			iLoadedImage = new ILoadedImage() {
				@Override
				public void onFinishLoadedLRC(String lrcPath, String songName) {
				}

				@Override
				public void onFinishLoaded(SoftReference<Drawable> drawable, String saveName) {
				
					ImageView imageview = (ImageView)rootView.findViewWithTag(saveName);
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
			EasouAsyncImageLoader.newInstance().loadImage(mImage, iLoadedImage, CommonUtils.MD5(mImage));
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
	
	/** 返回操作处理 */
	private void back() {
		Intent intent = new Intent(IntentAction.INTENT_ONLINE_FEATRUED_SET);
		intent.putExtra(BaseActivity.INTENT_ACTIVITY_NAME, "OmnibusDetailActivity");
		TianlApp.activityBundles.remove(IntentAction.INTENT_ONLINE_FEATRUED_SET_INTRO);
		int level = TianlApp.newInstance().getPageLevel();
		BaseActivity.newInstance().showActivity(intent,
				level - 1 == 0 ? 1 : level - 1);
	}
	
	public void onResume() {
	    super.onResume();
		//友盟统计 日志 add by perry 2012-10-23
	    //MobclickAgent.onResume(this);
	}

	public void onPause() {
	    super.onPause();
		//友盟统计 日志 add by perry 2012-10-23
	    //MobclickAgent.onPause(this);
	}
	
}
