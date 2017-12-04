package com.tiantiankuyin.component.activity.local;

import java.net.URLEncoder;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tiantiankuyin.database.bll.OnDataPreparedListener;
import com.tiantiankuyin.database.bll.OnlineMusicManager;
import com.tiantiankuyin.net.EasouHttpResponse;
import com.tiantiankuyin.para.Env;
import com.tiantiankuyin.para.WebServiceUrl;
import com.tiantiankuyin.utils.CommonUtils;
import com.tiantiankuyin.R;


public class FeedBackActivity extends Activity {
	private EditText feedback;
	private Button confirm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feedback);
		init();
	}
	
	private void init() {
		confirm=(Button)findViewById(R.id.confirm);
		feedback=(EditText)findViewById(R.id.feedback);
		confirm.setOnClickListener(listener);
		Window window = getWindow();
		WindowManager.LayoutParams wl = window.getAttributes();
		//wl.y=30 ;//why 这里因为gravity为BOTTOM，所以可以将y想成是离底部的高度
		int screen_W =Env.getScreenWidth();
		int screen_H = Env.getScreenHeight();
		wl.width=(int) (screen_W * 0.8);
		wl.height=screen_H/2-getWindowSize(screen_W);
		wl.gravity=Gravity.CENTER;
		window.setAttributes(wl);
		window.setBackgroundDrawableResource(R.drawable.playview_function_background_img);
	}
	/**
	 * popupwindow离底部的距离
	 * @param screen_W
	 * @return
	 */
	private int getWindowSize(int screen_W){
		int size=0;
		if(0<screen_W&&screen_W<=240){//小屏幕手机
			size=20;
		}else if(240<screen_W&&screen_W<=320){//中屏幕手机
			size=40;
		}else if(320<screen_W&&screen_W<=480){//大屏手机
			size=60;
		}else{//超大屏幕
			size=60;
		}
		return size;
	}
	
	private View.OnClickListener listener=new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			doFeedBack();
		}
	};
	
	private void doFeedBack(){
		if(feedback.getText()!=null&&feedback.getText().toString().length()>0){
			if(CommonUtils.isHasNetwork(this)){
				sendFeedBack(feedback.getText().toString());
			}else{
				Toast.makeText(FeedBackActivity.this, "当前无可用的网络!", Toast.LENGTH_SHORT).show();
			}
		}else{//提示意见不能为空
			Toast.makeText(this, getString(R.string.feedback_easou), Toast.LENGTH_SHORT).show();
		}
	}
	
	private void sendFeedBack(String msg){
		if(msg.length()>300){
			Toast.makeText(FeedBackActivity.this,getString(R.string.feedback_text_count), Toast.LENGTH_SHORT).show();
			return ;
		}
		
		String url=WebServiceUrl.EASOU_MUSIC_FEEDBACK
				+"?usid=1010&ipAddress=&phoneModel="+Env.getModel()
				+"&question="+URLEncoder.encode(msg+"_"+"宜搜音乐客户端"+Env.getVersion());
		sendFeedBackByNet(url);
	}
	
	/** 意见反馈
	 * @param url String 意见反馈服务器链接
	 *  */
	private void sendFeedBackByNet(String url){
		OnlineMusicManager.getInstence().sendFeedBackByNet(this,
				new OnDataPreparedListener<EasouHttpResponse>() {
					@Override
					public void onDataPrepared(EasouHttpResponse data) {
						if (data != null) {
							if(data.getResponseCode()==200 ){
								Toast.makeText(FeedBackActivity.this, "意见反馈成功!", Toast.LENGTH_SHORT).show();
							}else{
								Toast.makeText(FeedBackActivity.this, "意见反馈失败!", Toast.LENGTH_SHORT).show();
							}
						}else{
							Toast.makeText(FeedBackActivity.this, "意见反馈失败!", Toast.LENGTH_SHORT).show();
						}
						finish();
					}
				}, url);
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
