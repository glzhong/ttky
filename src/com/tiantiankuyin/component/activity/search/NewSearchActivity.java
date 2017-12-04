package com.tiantiankuyin.component.activity.search;


import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.tiantiankuyin.component.activity.BaseActivity;
import com.tiantiankuyin.database.bll.OnDataPreparedListener;
import com.tiantiankuyin.database.bll.OnlineMusicManager;
import com.tiantiankuyin.para.Env;
import com.tiantiankuyin.para.IntentAction;
import com.tiantiankuyin.para.SPHelper;
import com.tiantiankuyin.utils.CommonUtils;
import com.tiantiankuyin.utils.Lg;
import com.tiantiankuyin.R;
import com.tiantiankuyin.TianlApp;


public class NewSearchActivity extends Activity {
	public static final String INTENT_ACTIVITY_NAME = "ActivityName"; // 用以提取intent数据
	private ScrollView scrollView;//搜索联想词
	private EditText new_search_editText;
	private Button new_search_btn;
	private InputMethodManager imm ;   
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_search);
		overridePendingTransition( R.anim.search_in_from_down,  R.anim.search_out_from_down);
		init();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		//友盟统计 日志 add by perry 2012-10-23
	   // MobclickAgent.onResume(this);
	}
	
	/**
	 * 初始化
	 */
	private void init(){
		imm =(InputMethodManager)  getSystemService(Context.INPUT_METHOD_SERVICE);   
		new_search_editText=(EditText)findViewById(R.id.new_search_editText);
		new_search_btn=(Button)findViewById(R.id.new_search_btn);
		new_search_btn.setOnClickListener(listener);
		scrollView =(ScrollView)findViewById(R.id.scrollView);
		LinearLayout.LayoutParams params=(LinearLayout.LayoutParams)scrollView.getLayoutParams();
		int screen_W = Env.getScreenWidth();
		int screen_H =Env.getScreenHeight();
		params.width=screen_W;
		params.height=screen_H;
		scrollView.setLayoutParams(params);
		limitTextSize();
		//获取以前保存的联想词
		String key=SPHelper.newInstance().getLenovKey();
		if(key!=null&&key.length()>0){
			String[] keys=key.split("_");
			showLenvo(Arrays.asList(keys),true);//显示默认的联想词
		}
		new_search_editText.requestFocus();
	}
	
	private void limitTextSize() {
		// 输入框限制输入字数
		new_search_editText.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int arg1, int arg2,
					int arg3) {
			}

			@Override
			public void onTextChanged(CharSequence s, int arg1, int arg2,
					int arg3) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				String value = s.toString();
				if (s.length() > 0) {
					new_search_btn.setText(getString(R.string.search_music));
					getLenvo(value);
				}else{
					new_search_btn.setText(getString(R.string.cancel));
				}
			}
		});
	}
	private void getLenvo(final String queryStr) {
		String url = CommonUtils.getOlLenvoWordRequestURL(queryStr);
		OnlineMusicManager.getInstence().getLenvo(this,
				new OnDataPreparedListener<List<String>>() {
					@Override
					public void onDataPrepared(final List<String> data) {
						if (data != null) {	
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									showLenvo(data,false);
								}
							});
						}else {
							Lg.d("getSingerListData() == null");
							return;
						}
					}
				}, url);
	}
	
	/**
	 * 把热词保存到本地
	 */
	/*private void saveLenvonKeyToLocal(String key){
		String oldLenov=SPHelper.newInstance().getLenovKey();
		if(oldLenov!=null&&oldLenov.length()>0){
			//去重
			if(oldLenov.contains(key)){//如果以前已经包含了这个搜索词了
				int index=oldLenov.indexOf(key);
				StringBuffer sb=new StringBuffer(oldLenov);
				sb.delete(index-1, index+ key.length());
				oldLenov=sb.toString();
			}
			String[] oldlenovs=oldLenov.split("_");
			if(oldlenovs.length<15){//未到15个
				oldLenov=oldLenov+"_"+key;
				SPHelper.newInstance().saveLenovKey(oldLenov);
			}else{//等于15个
				LinkedList<String> lenvons=new LinkedList<String>(Arrays.asList(oldlenovs));
				lenvons.removeFirst();//把第一个去掉
				lenvons.addLast(key);//增加最后一个
				StringBuffer sb=new StringBuffer();
				for(String s:lenvons){
					sb.append(s).append("_");
				}
				sb.delete(sb.length()-1, sb.length());
				SPHelper.newInstance().saveLenovKey(sb.toString());
			}
		}else{//为空 第一次保存
			SPHelper.newInstance().saveLenovKey(key);
		}
	}*/
	public void showLenvo(List<String> keys,boolean isClearHistory) {
		LinearLayout childView = getLenoLinelayout( keys,isClearHistory);
		scrollView.removeAllViews();
		LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		scrollView.addView(childView,params);
		scrollView.setOnTouchListener(ontouchListener);
	}
	private View.OnTouchListener ontouchListener=new View.OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if(imm!=null&&NewSearchActivity.this.getCurrentFocus()!=null){
				imm.hideSoftInputFromWindow(NewSearchActivity.this.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
			}
			return false;
		}
	};
	private View.OnClickListener listener=new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Button button=(Button)v;
			if(button.getText().equals(getString(R.string.cancel))){
				//返回
				finish();
			}else{
				if(!CommonUtils.isHasNetwork(NewSearchActivity.this)){//无网络
					Toast.makeText(NewSearchActivity.this, getString(R.string.has_no_net), Toast.LENGTH_SHORT).show();//提示当前无可用的网络
					return ;
				}
				String key=new_search_editText.getText().toString();
				if(key!=null&&key.length()>0){
					CommonUtils.saveLenvonKeyToLocal(key);//保存搜索词
					getSearchSongFromNet(key);
				}
			}
		}
	};
	/**
	 * 去网络获取热词的歌曲
	 */
	private void getSearchSongFromNet(final String key){
		Intent intent = new Intent(IntentAction.INTENT_ONLINE_SEARCH_RESULT_ACTIVITY);
		intent.putExtra(BaseActivity.INTENT_ACTIVITY_NAME, "SearchResultActivity");
		if (TianlApp.activityBundles != null) {
			Bundle bundle = new Bundle();
			bundle.putString(SearchResultActivity.KEY, key);
			bundle.putString(SearchResultActivity.KEY_BACK_ACTION,
					IntentAction.INTENT_SEARCH_ACTIVITY );
			bundle.putString(SearchResultActivity.KEY_ACTIVITY_NAME,
					"SearchActivity");
			TianlApp.activityBundles.put(IntentAction.INTENT_ONLINE_SEARCH_RESULT_ACTIVITY,
					bundle);
		}
		BaseActivity.newInstance().showActivity(intent, 2);
		finish();
	}
	/**
	 *高度适配
	 * @param screen_W
	 * @return
	 */
	private int getLevonHeight(int screen_W){
		int size=0;
		if(0<screen_W&&screen_W<=240){//小屏幕手机
			size=40;
		}else if(240<screen_W&&screen_W<=320){//中屏幕手机
			size=60;
		}else if(320<screen_W&&screen_W<=480){//大屏手机
			size=60;
		}else{//超大屏幕
			size=80;
		}
		return size;
	}
	/**
	 * 主拼 搜索连词词的方法 
	 * @param keys 联想词集合
	 * @param isClearHistory 是否需要显示清空历史 button
	 * @return
	 */
	private LinearLayout getLenoLinelayout(List<String> keys,boolean isClearHistory) {
		int screen_W = Env.getScreenWidth();
		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, getLevonHeight(screen_W));
		if(keys!=null&&keys.size()>0){
			for (int i=keys.size()-1;i>=0;i--) {
				String s= keys.get(i);
				View view=LayoutInflater.from(this).inflate(R.layout.online_search_lenvon_item, null);
				TextView textView =(TextView)view.findViewById(R.id.keyword);
				ImageView imageView=(ImageView)view.findViewById(R.id.history_img);
				textView.setText(s);
				textView.setGravity(Gravity.LEFT|Gravity.CENTER);
				textView.setLayoutParams(params);
				view.setBackgroundResource(R.drawable.similar_item_bg_selector);
				textView.setPadding(8, 5, 5, 5);
				view.setTag(s);
				view.setOnClickListener(keywordListener);
				layout.addView(view);
				if(!isClearHistory){
					imageView.setVisibility(View.GONE);
				}
			}
			//清空搜索历史
			if(isClearHistory){//是否需要清空搜索历史
				LinearLayout history_layout = new LinearLayout(this);
				history_layout.setGravity(Gravity.CENTER);
				Button button=new Button(this);
				button.setTextSize(16);
				button.setTextColor(0xff505050);
				button.setText(getString(R.string.clear_up_search_history));
				button.setGravity(Gravity.CENTER);
				LinearLayout.LayoutParams buttonParams=new LinearLayout.LayoutParams(
						LayoutParams.FILL_PARENT, 90);
				buttonParams.topMargin=getClearButtonHeight(screen_W);
				button.setPadding(15, 10, 15, 10);
				button.setBackgroundResource(R.drawable.dialog_button_bg);
				button.setOnClickListener(keywordListener);
				button.setTag(this.getString(R.string.clear_up_search_history));
				history_layout.addView(button);
				layout.addView(history_layout, buttonParams);
			}
		}
		return layout;
	}
	/**
	 *高度适配
	 * @param screen_W
	 * @return
	 */
	private int getClearButtonHeight(int screen_W){
		int size=0;
		if(0<screen_W&&screen_W<=240){//小屏幕手机
			size=20;
		}else if(240<screen_W&&screen_W<=320){//中屏幕手机
			size=20;
		}else if(320<screen_W&&screen_W<=480){//大屏手机
			size=20;
		}else{//超大屏幕
			size=40;
		}
		return size;
	}
	private View.OnClickListener keywordListener=new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			String key=v.getTag().toString();
			if(getString(R.string.clear_up_search_history).equals(key)){//清空搜索历史
				SPHelper.newInstance().saveLenovKey(null);
				//刷新
				if(scrollView!=null){
					scrollView.removeAllViews();
				}
				Toast.makeText(NewSearchActivity.this,getString(R.string.clear_up_search_history_success) , Toast.LENGTH_SHORT).show();
			}else{//访问网络去查询
				if(!CommonUtils.isHasNetwork(NewSearchActivity.this)){//无网络
					Toast.makeText(NewSearchActivity.this, getString(R.string.has_no_net), Toast.LENGTH_SHORT).show();//提示当前无可用的网络
					return ;
				}
				CommonUtils.saveLenvonKeyToLocal(key);//保存搜索词
				getSearchSongFromNet(key);
			}
		}
	};
	@Override
	protected void onPause() {
		super.onPause();
		//友盟统计 日志 add by perry 2012-10-23
	    //MobclickAgent.onPause(this);
	}
}
