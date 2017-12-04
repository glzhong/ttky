package com.tiantiankuyin.component.activity;

import java.io.ByteArrayInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.cmsc.cmmusic.init.InitCmmInterface;
import com.tiantiankuyin.para.Env;
import com.tiantiankuyin.para.IntentAction;
import com.tiantiankuyin.para.SPHelper;
import com.tiantiankuyin.para.SharedPreferencesManager;
import com.tiantiankuyin.para.UserData;
import com.tiantiankuyin.play.PlayLogicManager;
import com.tiantiankuyin.scan.AutoScan;
import com.tiantiankuyin.utils.Lg;
import com.tiantiankuyin.R;



/**
 * 欢迎页面（在欢迎页面处理：有网络带到在线推荐页，无网络带到本地首页。还会在子线程中处理初始化逻辑）
 * 
 * @author DK
 * 
 */
public class SplashActivity extends Activity {

	private final long MIN_DELATE = 2000;//欢迎页面最短跳转时间为 2秒
	
	private long startTime = 0;
	
	private Timer timer;
	
	private Uri data;//外部调用的数据。
	
	private Context context;
	private SharedPreferencesManager mSharedPreferencesManager = new SharedPreferencesManager(); 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		data = getIntent().getData();
	    

		if(data!= null) {
			PlayLogicManager.newInstance().setIntentData(data);
		}
		
		if(UserData.getInstence().isShowedWelcomActivity()){
			goBaseActivityImmediately();
			
			if (PlayLogicManager.newInstance().getIntentData() != null) {
				PlayLogicManager.newInstance().dealIntent(PlayLogicManager.newInstance().getIntentData());
				PlayLogicManager.newInstance().setIntentData(null);
			}
			return;
		}
		UserData.getInstence().setShowedWelcomActivity(true);
		
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); 
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash);
		//友盟错误日志
//		MobclickAgent.onError(this);
		
		startTime = System.currentTimeMillis();
		context = this;
		new Thread(){
			@Override
			public void run() {
				initData();
				int isty= mSharedPreferencesManager.readIntPreferences("mzsm");
				if(isty==0){
				Intent intent = new Intent(context , WebViewActivityMzsm.class);
				context.startActivity(intent);
				SplashActivity.this.finish();
				return ;
				}else{
					goBaseActivity();
				}
				
				try {	
					String result = InitCmmInterface.initCheck(context);
					if(!"1".equals(result.trim())){
						long startTime = System.currentTimeMillis();
						//初始化
						Hashtable<String, String> initResult = InitCmmInterface.initCmmEnv(context);
						if(null!=initResult && StringUtils.equals(initResult.get("code"), "0")){
							result = "1";
						}
					}
				} catch (Exception e) {
					SplashActivity.this.finish();
					e.printStackTrace();
				}
			};
		}.start();
		InitCmmInterface.initSDK(this);
	}
	
	//初始化数据。
	private void initData() {
		// 临时插入系统媒体库数据方法。	
		//判断是否子安装后初次启动。若是则展示引导页面。
		if (!UserData.getInstence().isSavedLocalMsg()) {
			UserData.getInstence().setSavedLocalMsg(true);
			scanAndInsert();
			Lg.d("test", "启动自动扫描");
			SPHelper.newInstance().setIsFirstLogin();
			
			//写入XML中的首次启动的时间
			UserData.getInstence().setFirstStartTime(System.currentTimeMillis()/1000);
			//增加屏幕分辨率存取XML操作     by Erica 
			DisplayMetrics dm = new DisplayMetrics();  
			getWindowManager().getDefaultDisplay().getMetrics(dm);  
			int screen_W =  dm.widthPixels;
			int screen_H =  dm.heightPixels;
			int screen_densityDpi =  dm.densityDpi;
			
			Env.setScreenDensity(screen_densityDpi);
			Env.setScreenHeight(screen_H);
			Env.setScreenWidth(screen_W);
		}
	}
	
	private void goBaseActivityImmediately(){
		SplashActivity.this.finish();
		Intent intent = null;
		intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		intent.setClass(this, BaseActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		startActivity(intent);
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
				SplashActivity.this.finish();
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
	

	private void scanAndInsert() {

		try {
			AutoScan autoScan = new AutoScan(this);
			
			//若当前数据库没有音频数据。需要爬取系统数据库。则爬取。
			if (autoScan.isNeedInsert()) {
				autoScan.scanAndInsert();//爬取系统数据库。
			}
		} catch (Exception e) {
//			e.printStackTrace();
		}
	}
	
	@Override
	public void onResume() {
	    super.onResume();
		//友盟统计 日志 add by perry 2012-10-23
//	    MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
	    super.onPause();
		//友盟统计 日志 add by perry 2012-10-23
//	    MobclickAgent.onPause(this);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		//System.out.println("onDestory============================");
		//InitCmmInterface.exitApp(this);
	}
	
	public class GetPublicKey {
		
		/**
		 * 获取签名公钥
		 * @param mContext
		 * @return
		 */
		protected String getSignInfo(Context mContext) {
			String signcode = "";
			try {
				PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(
						mContext.getPackageName(), PackageManager.GET_SIGNATURES);
				Signature[] signs = packageInfo.signatures;
				Signature sign = signs[0];

				signcode = parseSignature(sign.toByteArray());
				signcode = signcode.toLowerCase();
			} catch (Exception e) {
				Log.e("", e.getMessage(), e);
			}
			return signcode;
		}

		protected String parseSignature(byte[] signature) {
			String sign = "";
			try {
				CertificateFactory certFactory = CertificateFactory
						.getInstance("X.509");
				X509Certificate cert = (X509Certificate) certFactory
						.generateCertificate(new ByteArrayInputStream(signature));
				String pubKey = cert.getPublicKey().toString();
				String ss = subString(pubKey);
				ss = ss.replace(",", "");
				ss = ss.toLowerCase();
				int aa = ss.indexOf("modulus");
				int bb = ss.indexOf("publicexponent");
				sign = ss.substring(aa + 8, bb);
			} catch (CertificateException e) {
				Log.e("", e.getMessage(), e);
			}
			return sign;
		}

		public String subString(String sub) {
			Pattern pp = Pattern.compile("\\s*|\t|\r|\n");
			Matcher mm = pp.matcher(sub);
			return mm.replaceAll("");
		}
	}

}
