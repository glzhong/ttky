package com.tiantiankuyin.component.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import com.tiantiankuyin.R;

public class WebViewActivity extends Activity {

	private WebView webView;
	public static final String URL = "webview_url";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.online_recommend_plugin_webview);
		overridePendingTransition(R.anim.webview_in, R.anim.recommond_out);
		findview();
		init();
	}

	private void findview() {
		webView = (WebView) findViewById(R.id.webView);
	}

	private void init() {
		Intent intent = getIntent();
		String address = intent.getStringExtra(URL);
		webView.loadUrl(address);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(0, R.anim.webview_out);
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
	   // MobclickAgent.onPause(this);
	}
}
