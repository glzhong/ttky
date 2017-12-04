package com.tiantiankuyin.component.activity;

import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang.StringUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import com.cmsc.cmmusic.init.InitCmmInterface;
import com.tiantiankuyin.R;
import com.tiantiankuyin.TianlApp;
import com.tiantiankuyin.para.IntentAction;
import com.tiantiankuyin.para.SharedPreferencesManager;

public class WebViewActivityMzsm extends Activity {
    private final long MIN_DELATE = 2000;//欢迎页面最短跳转时间为 2秒
	
	private long startTime = 0;
	private Uri data;//外部调用的数据。
	private Timer timer;
	private WebView webView;
	public static final String URL = "file:///android_asset/MZSM.html";
	private SharedPreferencesManager mSharedPreferencesManager = new SharedPreferencesManager(); 
	public Activity ctx;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mzsm_webview);
//		overridePendingTransition(R.anim.webview_in, R.anim.recommond_out);
		ctx = this;
		findview();
		init();
	}

	private void findview() {
		webView = (WebView) findViewById(R.id.webView);
		Button bt1= (Button) this.findViewById(R.id.bt1);
		Button bt2= (Button) this.findViewById(R.id.bt2);
		bt1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mSharedPreferencesManager.writeIntPreferences("mzsm", 0);
				WebViewActivityMzsm.this.finish();
				System.exit(0);
			}
		});
	    bt2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mSharedPreferencesManager.writeIntPreferences("mzsm", 1);
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						 
						goBaseActivity();
						
						try {	
							String result = InitCmmInterface.initCheck(ctx);
							if(!"1".equals(result.trim())){
								long startTime = System.currentTimeMillis();
								//初始化
								Hashtable<String, String> initResult = InitCmmInterface.initCmmEnv(ctx);
								if(null!=initResult && StringUtils.equals(initResult.get("code"), "0")){
									result = "1";
								}
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}).start();
				
			}
		});
	}
	private void goBaseActivity(){
		
		TimerTask task = new TimerTask() {
			public void run() {
				Intent intent = new Intent();
				intent.setAction(IntentAction.INTENT_BASE_ACTIVITY);//设置跳BaseActivity意图
				if (data != null) {
//					Log.d("test", "Uri = " + data.toString());
//					Easou.newInstance().dealIntent(data);
					intent.putExtra(BaseActivity.KEY_IS_DEAL_INTENT_PLAY, true);
				}
				startActivity(intent);
				WebViewActivityMzsm.this.finish();
			}
		};
		timer = new Timer();//初始化定时跳转的Timer
		long delay = 0;
		delay = System.currentTimeMillis() - startTime;
		// 延时至少2秒后执行。
		if (delay < MIN_DELATE) {
			delay = MIN_DELATE - delay;
		} else {
			delay = 0;
		}
		timer.schedule(task, delay);
	}
	private void init() {
		 
		 
		webView.loadUrl(URL);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		//overridePendingTransition(0, R.anim.webview_out);
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
