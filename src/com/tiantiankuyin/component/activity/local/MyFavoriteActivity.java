package com.tiantiankuyin.component.activity.local;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.tiantiankuyin.component.activity.BaseActivity;
import com.tiantiankuyin.para.IntentAction;
import com.tiantiankuyin.utils.CommonUtils;
import com.tiantiankuyin.R;
import com.tiantiankuyin.TianlApp;

/**
 * 本地歌曲模块-我的最爱页
 * @author DK
 */
public class MyFavoriteActivity extends Activity {

	private View rootView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		rootView = LayoutInflater.from(this).inflate(
				R.layout.local_my_favorite, null);
		setContentView(rootView);
		CommonUtils.setTitle(rootView, getString(R.string.fav), true, false);

		findView();
		init();
	}

	private void findView() {
	}

	private void init() {
	}

	@Override
	public void onBackPressed() {
		back();
	}

	public void back() {
		Intent intent = new Intent(IntentAction.INTENT_LOCAL_ACTIVITY);
		intent.putExtra(BaseActivity.INTENT_ACTIVITY_NAME, "LocalActivity");
		int level = TianlApp.newInstance().getPageLevel();
		BaseActivity.newInstance().showActivity(intent,
				level - 1 == 0 ? 1 : level - 1);
	}
	
	@Override
	public void onResume() {
	    super.onResume();
		//友盟统计 日志 add by perry 2012-10-23
	   // MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
	    super.onPause();
		//友盟统计 日志 add by perry 2012-10-23
	   // MobclickAgent.onPause(this);
	}
}
