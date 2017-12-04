package com.tiantiankuyin.component.activity.search;

import java.util.List;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
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
import com.tiantiankuyin.view.AnimImageView;
import com.tiantiankuyin.R;
import com.tiantiankuyin.TianlApp;

public class SearchActivity extends Activity {
	
	/**
	 * 搜索词的热词 如果有数据的话先记录下来不要返回每次都加载
	 */
	public static List<String> hotKeys;
	
	private EditText editText;
	private LocalActivityManager mLocalActivityManager;
	private FrameLayout rootView;
	/*private NewSearchView searchView ;*/
	/*private ListView musicList;*/
	/*private MusicListAdapter adapter;*/
	/*private List<OlSongVO> songs;*/
	private LinearLayout hot_key_layout;
	private LinearLayout top_layout;
	/*private LinearLayout ask_wrong;//纠错词父类容器
	private TextView error_word;//纠错词
*/	/** 显示动画的组件   */ 
    private AnimImageView imgDance; 
    private LinearLayout no_network_tips;//无网络提示
	private Button fresh;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		rootView=(FrameLayout)LayoutInflater.from(this).inflate(R.layout.search, null);
		setContentView(rootView);
		mLocalActivityManager = new LocalActivityManager(this, true);
		CommonUtils.setTitle(rootView,getString(R.string.search_music), false, false);
		init();
	}


	/**
	 * 初始化界面view 
	 */
	private void init() {
		imgDance = (AnimImageView) super.findViewById(R.id.ImgDance); 
		editText = (EditText) rootView.findViewById(R.id.editText);
		no_network_tips=(LinearLayout)findViewById(R.id.no_network_tips);
		fresh=(Button)findViewById(R.id.fresh);
		fresh.setOnClickListener(freshListener);
		/*ask_wrong=(LinearLayout)rootView.findViewById(R.id.ask_wrong);
		error_word=(TextView)rootView.findViewById(R.id.error_word);
		ask_wrong.setVisibility(View.GONE);*/
		/*musicList=(ListView)rootView.findViewById(R.id.musicList);*/
		editText.setInputType(InputType.TYPE_NULL);// 不弹出键盘
		editText.setOnClickListener(listener);     
		editText.setOnFocusChangeListener(focusChangerListener);
		hot_key_layout=(LinearLayout)rootView.findViewById(R.id.hot_key_layout);
		top_layout=(LinearLayout)rootView.findViewById(R.id.top_layout);
		FrameLayout.LayoutParams params=(FrameLayout.LayoutParams)top_layout.getLayoutParams();
		int screen_W = Env.getScreenWidth();
		int screen_H = Env.getScreenHeight();
		params.width=screen_W;
		params.height=screen_H;
		top_layout.setLayoutParams(params);//主要目的是为了撑开整个屏幕
		getHotkeys();//网络请求获取热词
	}
	private View.OnClickListener freshListener=new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if(!CommonUtils.isHasNetwork(SearchActivity.this)){//无网络
				Toast.makeText(SearchActivity.this, getString(R.string.has_no_net), Toast.LENGTH_SHORT).show();//提示当前无可用的网络
				return ;
			}
			fresh();//刷新
		}
	};
	private void fresh(){
		no_network_tips.setVisibility(View.GONE);
		imgDance.setVisibility(View.VISIBLE);
		getHotkeys();//网络请求获取热词
	}
	/**
	 * 获取热词
	 */
	private void getHotkeys(){
		String url=CommonUtils.getOlHotKeyRequestURL(10);
		//获取本地热词
		if(hotKeys!=null&&hotKeys.size()>0){//查看以前有保存没 增加热词缓存功能
			refreshHotKeys(hotKeys);
			return;
		}
		if(hotKeys==null&&!CommonUtils.isHasNetwork(this)){//沒緩存也沒網絡
			no_network_tips.setVisibility(View.VISIBLE);
			imgDance.setVisibility(View.GONE);
			return;
		}
		OnlineMusicManager.getInstence().getHotkeys(this,
				new OnDataPreparedListener<List<String>>() {
					@Override
					public void onDataPrepared(final List<String> data) {
						if (data != null) {	
							//把热词保留下来
							hotKeys=data;
							//保存到本地
							saveHotKeysTolocal(data);
							//更新UI
							refreshHotKeys(data);
						}else {
							Lg.d("getSingerListData() == null");
							return;
						}
					}
				}, url);
	}
	
	/**
	 * 把热词保存放本地
	 */
	private void saveHotKeysTolocal(List<String> keys){
		StringBuffer sb =new StringBuffer();
		for(String s:keys){
			sb.append(s).append("_");
		}
		sb.delete(sb.length()-1, sb.length());
		SPHelper.newInstance().setHotKeys(sb.toString());
	}
	/**
	 * Paint paint = new Paint();
		paint.setTextSize(tv.getTextSize());
		float size = paint.measureText(tv.getText().toString());
		动态计算关键字显示多少行
	 * @param keys
	 */
	private void refreshHotKeys(List<String> keys){
		if(imgDance.getVisibility()== View.VISIBLE){
			imgDance.setVisibility(View.GONE);
			imgDance.stop(); 
		}
		if (hot_key_layout != null) {//清除当前搜索热词。
			hot_key_layout.removeAllViews();
		}
		int textSize=14;//字体大小
		int totalWidth=0;//当前这行的总长度
		int screen_W = Env.getScreenWidth();//屏幕宽度
		float density= getResources().getDisplayMetrics().density;//屏幕密度
		LinearLayout layout= new LinearLayout(this);
		LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(screen_W,LayoutParams.FILL_PARENT);
		layout.setLayoutParams(params);
		layout.setOrientation(LinearLayout.HORIZONTAL);
		layout.setGravity(Gravity.CENTER);
		for(int i=0;i<keys.size();i++){
			totalWidth=totalWidth+getTextViewWidth(textSize,keys.get(i))+10;
			TextView textView=new TextView(this);
			textView.setTextSize(textSize);
			textView.setText(keys.get(i));
			textView.setGravity(Gravity.CENTER);
			textView.setPadding(5, 5, 5, 5);
			textView.setTextColor(0xff7b7b7b);
			LinearLayout.LayoutParams textParams=new LinearLayout.LayoutParams((int)(getTextViewWidth(textSize,keys.get(i))*density)+30,LayoutParams.WRAP_CONTENT);
			textParams.leftMargin=20;
			textView.setLayoutParams(textParams);
			textView.setBackgroundResource(R.drawable.search_key_btn_click);
			textView.setOnClickListener(textListener);
			if(totalWidth*density>screen_W*8/12){//
				hot_key_layout.addView(layout);
				TextView text=new TextView(this);
				hot_key_layout.addView(text);
				totalWidth=getTextViewWidth(textSize,keys.get(i));
				layout= new LinearLayout(this);
				layout.setOrientation(LinearLayout.HORIZONTAL);
				layout.setLayoutParams(params);
				layout.setGravity(Gravity.CENTER);
			}
			layout.addView(textView);
			if(i==(keys.size()-1)){
				hot_key_layout.addView(layout);
			}
		}
	}
	private View.OnClickListener textListener=new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if(!CommonUtils.isHasNetwork(SearchActivity.this)){//无网络
				Toast.makeText(SearchActivity.this, getString(R.string.has_no_net), Toast.LENGTH_SHORT).show();//提示当前无可用的网络
				return ;
			}
			String key=((TextView)v).getText().toString();
			if(key!=null&&key.length()>0){//
				CommonUtils.saveLenvonKeyToLocal(key);//保存用户点击搜索过的关键字
				getSearchSongFromNet(key);
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
		int level = TianlApp.newInstance().getPageLevel();
		BaseActivity.newInstance().showActivity(intent, level+1);
		/*finish();*/
	}
	private int getTextViewWidth(int textSize,String key){
		Paint paint = new Paint();
		paint.setTextSize(textSize);
		int size =(int ) paint.measureText(key);
		return size;
	}

	@Override
	protected void onResume() {
		Lg.e("life", "SearchActivity onResume");
		super.onResume();
		//友盟统计 日志 add by perry 2012-10-23
	   // MobclickAgent.onResume(this);
		mLocalActivityManager.dispatchResume();
	}
	
	@Override
	protected void onStop() {
		Lg.e("life", "SearchActivity onStop");
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		Lg.e("life", "SearchActivity onDestroy");
		super.onDestroy();
	}
	
	@Override
	protected void onPause() {
		Lg.e("life", "SearchActivity onPause");
		super.onPause();
		//友盟统计 日志 add by perry 2012-10-23
	    //MobclickAgent.onPause(this);
		mLocalActivityManager.dispatchPause(isFinishing());
	}
	private View.OnClickListener listener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			openActivityAnimation();
		}
	};
	private View.OnFocusChangeListener focusChangerListener = new View.OnFocusChangeListener() {
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (hasFocus) { // 获得焦点
				openActivityAnimation();
			}
		}
	};
	/**
	 * x显示新的搜索页
	 */
	private void openActivityAnimation() {
		Intent intent=new Intent();//com.tiantiankuyin.intent.NEW_SEARCH_ACTIVITY
		intent.setAction(IntentAction.INTENT_NEW_SEARCH_ACTIVITY);
		startActivity(intent);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		return super.dispatchKeyEvent(event);
	}
}
