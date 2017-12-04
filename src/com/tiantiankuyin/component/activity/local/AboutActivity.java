package com.tiantiankuyin.component.activity.local;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

import com.tiantiankuyin.utils.CommonUtils;
import com.tiantiankuyin.R;

public class AboutActivity extends Activity {
	private View rootView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		rootView = LayoutInflater.from(this).inflate(R.layout.about, null);
		setContentView(rootView);
		init();
	}

	private void init() {
		CommonUtils.setTitle(rootView, "关于我们", false, false);

		findViewById(R.id.textView).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				toWap("http://218.98.35.163/mp3/wzdmse.htm");
			}
		});

		findViewById(R.id.textView1).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				toWap("http://218.98.35.163/mp3/wzdmse.htm");
			}
		});
	}
	
	public void toWap(String url)
	{
		Uri uri = Uri.parse(url);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
	}

	@Override
	public void onResume() {
		super.onResume();
		// 友盟统计 日志 add by perry 2012-10-23
		//MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		// 友盟统计 日志 add by perry 2012-10-23
		//MobclickAgent.onPause(this);
	}
}
