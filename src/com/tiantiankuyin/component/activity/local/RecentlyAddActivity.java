package com.tiantiankuyin.component.activity.local;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tiantiankuyin.component.activity.BaseActivity;
import com.tiantiankuyin.para.IntentAction;
import com.tiantiankuyin.R;
import com.tiantiankuyin.TianlApp;

/**
 * 本地歌曲模块-最近添加页
 * 
 * @author DK
 * 
 */
public class RecentlyAddActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.local_recentle_add);
	}

	@Override
	public void onBackPressed() {
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
	    //MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
	    super.onPause();
		//友盟统计 日志 add by perry 2012-10-23
	    //MobclickAgent.onPause(this);
	}
}
