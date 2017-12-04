package com.tiantiankuyin.component.activity.search;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cmsc.cmmusic.common.MusicQueryInterface;
import com.cmsc.cmmusic.common.data.MusicInfo;
import com.cmsc.cmmusic.common.data.MusicInfoResult;
import com.cmsc.cmmusic.common.data.MusicListRsp;
import com.tiantiankuyin.adapter.OnlineMusicListAdapter;
import com.tiantiankuyin.bean.OlSingerVO;
import com.tiantiankuyin.bean.OlSongVO;
import com.tiantiankuyin.component.activity.BaseActivity;
import com.tiantiankuyin.component.activity.online.RecommendActivity;
import com.tiantiankuyin.net.NetCache;
import com.tiantiankuyin.para.IntentAction;
import com.tiantiankuyin.para.WebServiceUrl;
import com.tiantiankuyin.play.PlayLogicManager;
import com.tiantiankuyin.utils.BeanUtils;
import com.tiantiankuyin.utils.CommonUtils;
import com.tiantiankuyin.utils.Lg;
import com.tiantiankuyin.view.AnimImageView;
//import com.umeng.analytics.MobclickAgent;
import com.tiantiankuyin.R;
import com.tiantiankuyin.TianlApp;

public class SearchResultActivity extends Activity {
	public final static String KEY = "search_key";
	public static final String KEY_BACK_ACTION = "BACK_ACTION";
	public static final String KEY_ACTIVITY_NAME = "ACTIVITY_NAME";
	private EditText editText;
	private String key;
	private ListView musicList;
	private OnlineMusicListAdapter adapter;
	/** 纠错词 */
	private TextView error_word;
	/** 纠错词父类容器 */
	private LinearLayout ask_wrong; 
	private ImageButton backBtn;
	private View rootView;
	private String  backAction;
	private String activityName;
	private int currentPage;
	/** 集合对象 */
	private List<OlSongVO> musics;
	private TextView search_results_tips;
	private TextView search_result_tips_after;
	/** 显示动画的组件   */ 
    private AnimImageView imgDance; 
	private  OlSingerVO before;
	/** 纠错次 */
	private String errorKey;
	/** 加载更多  */
	private LinearLayout more;
	/** 跟多旋转框  */
	private LinearLayout loadingMore;
	
	private LinearLayout loading_layout;
	
	private Handler mHandler = new Handler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		rootView = LayoutInflater.from(this).inflate(R.layout.search_result,
				null);
		setContentView(rootView);
		CommonUtils.setTitle(rootView, "搜索结果", true, false);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		//友盟统计 日志 add by perry 2012-10-23
	    //MobclickAgent.onResume(this);
		if (TianlApp.activityBundles == null){
			return;
		}
		Bundle bundle = TianlApp.activityBundles
				.get(IntentAction.INTENT_ONLINE_SEARCH_RESULT_ACTIVITY);	
		backAction = bundle.getString(KEY_BACK_ACTION);
		activityName = bundle.getString(KEY_ACTIVITY_NAME);
		//判断两次是否搜索同一个关键字
		String newKey=null;
		if(key==null){
			key = bundle.getString(KEY);
		}else{
			newKey= bundle.getString(KEY);
		}
		if(key.equals(newKey)){//表示搜索同一关键字就不往下执行了
			return ;
		}else{
			if(newKey==null){
				newKey=key;
			}else{
				key=newKey;
			}
			reStart();//不是同一个关键字要清空以前的数据
		}
		init();
		if(key!=null){
			editText.setText(key);
			getSearchSongFromNet(key,1);
		}
	}
	
	private void init() {
		imgDance = (AnimImageView) super.findViewById(R.id.ImgDance); 
		musicList = (ListView) findViewById(R.id.musicList);
		musicList.setVisibility(View.GONE);
		if(loading_layout==null){
			loading_layout=getMoreView(getString(R.string.net_more_txt));
			loading_layout.setOnClickListener(loadingMoreListener);
			musicList.addFooterView(loading_layout);
		}
		musicList.setVisibility(View.VISIBLE);
		editText = (EditText) findViewById(R.id.editText);
		search_results_tips=(TextView) findViewById(R.id.search_result_tips);
		search_result_tips_after=(TextView) findViewById(R.id.search_result_tips_after);
		editText.setInputType(InputType.TYPE_NULL);// 不弹出键盘
		editText.setOnClickListener(listener);     
		editText.setOnFocusChangeListener(focusChangerListener);
		ask_wrong = (LinearLayout) findViewById(R.id.ask_wrong);
		error_word = (TextView) findViewById(R.id.error_word);
		backBtn = (ImageButton) rootView.findViewById(R.id.back_btn);
		backBtn.setOnClickListener(errorTextListener);
	}
	
	private View.OnFocusChangeListener focusChangerListener = new View.OnFocusChangeListener() {
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (hasFocus) { // 获得焦点
				openActivityAnimation();
			}
		}
	};
	
	private View.OnClickListener listener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			openActivityAnimation();
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
	
	/**
	 * 纠错词的点击事件
	 */
	private View.OnClickListener errorTextListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.back_btn) {// 点击返回键
				back();
			} else {
				String erroKey = ((TextView) v).getText().toString();
				if (erroKey != null && erroKey.length() > 0) {//
					erroKey = erroKey.substring(1, erroKey.length() - 1);// 去掉头部和尾部引号
					key=erroKey;
					reStart();//初始化数据
					CommonUtils.saveLenvonKeyToLocal(erroKey);//保存搜索词
					editText.setText(erroKey);
					getSearchSongFromNet(erroKey,1);
				}
			}
		}
	};
	
	/**
	 * 显示没搜索到结果的提示
	 * @param key
	 */
	private void showNoResultsTips(String key){
		musicList.setVisibility(View.VISIBLE);
		if(imgDance.getVisibility()== View.VISIBLE){
			imgDance.setVisibility(View.GONE);
			imgDance.stop(); 
		}
		ask_wrong.setVisibility(View.VISIBLE);
		musicList.setVisibility(View.INVISIBLE);
		search_results_tips.setVisibility(View.VISIBLE);
		search_result_tips_after.setVisibility(View.VISIBLE);
		search_results_tips.setText("抱歉!未找到");
		String keyTips =key;
		ask_wrong.setVisibility(View.VISIBLE);
		error_word.setText(keyTips);
		search_result_tips_after.setText("相关歌曲 ");
		error_word.getPaint().setFlags(0);
		error_word.getPaint().setFlags( Paint.ANTI_ALIAS_FLAG);// 设置下划线
	}
	
	private boolean  compatorHotSale(OlSingerVO befor,OlSingerVO after){
		if(befor==null||after==null){
			return false;
		}
		if(befor.getDataList().size()!=after.getDataList().size()){
			return false;
		}
		int count=0;
		for(OlSongVO olSongVO:befor.getDataList()){
			for(OlSongVO song:after.getDataList()){
				if(olSongVO.getGid().equals(song.getGid())){
					count++;
					break;
				}
			}
		}
		if(count==after.getDataList().size()){
			//表示相等
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 去网络获取热词的歌曲
	 */
	private void getSearchSongFromNet(final String key,int pageNum) {
		try {
			before=NetCache.readCache(WebServiceUrl.SEARCH_MUSIC_KEY_URL+key);
			showSong(before);
		} catch (ClassCastException e) {
			//e.printStackTrace();
		} catch (IOException e) {
			//e.printStackTrace();
		} catch (ClassNotFoundException e) {
			//e.printStackTrace();
		}
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				String encodeKey = key;
				try {
					encodeKey = URLEncoder.encode(key,"UTF-8");
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					Lg.e(" URLEncoder encode search key to utf-8 error", e1);
				}
				MusicListRsp musicListRsp = MusicQueryInterface.getMusicsByKey(SearchResultActivity.this,encodeKey , "0", 1, 30);
				int resCounter = 0;
				if(null!=musicListRsp){
					resCounter = Integer.parseInt(musicListRsp.getResCounter());
				}
				if(resCounter > 0){
					List<OlSongVO> songVoes = BeanUtils.convenrtMusicInfoTOOlSongVO(musicListRsp.getMusics());
					OlSingerVO olSingerVO = new OlSingerVO();
					olSingerVO.setThisPage(1);
					olSingerVO.setEveryPageRow(30);
					olSingerVO.setCountRow(resCounter);
					int countPage = resCounter%olSingerVO.getEveryPageRow()==0?resCounter/olSingerVO.getEveryPageRow():resCounter/olSingerVO.getEveryPageRow()+1;
					olSingerVO.setCountPage(countPage);
					olSingerVO.setNextPage(2);
					olSingerVO.setDataList(songVoes);
					showSong(olSingerVO);
					try {
						NetCache.saveCache(olSingerVO, WebServiceUrl.SEARCH_MUSIC_KEY_URL+key);//保存缓存
					} catch (IOException e) {
						//e.printStackTrace();
					}
				}else{
					Lg.d("getSimilarData() == null");
					showNoResultsTips(key);
					return;
				}
			}
		}, 1000);
	}

	/**
	 * 搜索歌曲回调
	 */
	private void showSong(OlSingerVO olSingerVO) {
		musicList.setVisibility(View.VISIBLE);
		if(imgDance.getVisibility()== View.VISIBLE){
			imgDance.setVisibility(View.GONE);
			imgDance.stop(); 
		}
		currentPage=olSingerVO.getThisPage();
		if(currentPage==1){//只有第一页才有纠错词
			errorKey=olSingerVO.getPyKey();
		}
		showErrorKey(errorKey);//显示纠错词
		boolean isLoadingFinish=false;
		if(currentPage<olSingerVO.getCountPage()){//需要加载更多
			more.setVisibility(View.VISIBLE);
			loadingMore.setVisibility(View.GONE);
			LinearLayout layout=(LinearLayout)loading_layout.getChildAt(0);
			TextView textView=(TextView)layout.getChildAt(0);
			textView.setText(getString(R.string.net_more_txt));
			loading_layout.setOnClickListener(loadingMoreListener);
		}else{
			isLoadingFinish=true;
		}
		if(adapter==null){//第一次加载数据
			adapter=new OnlineMusicListAdapter(this);
			adapter.setmMusicList(olSingerVO.getDataList());
			musicList.setAdapter(adapter);
			musicList.setOnItemClickListener(itemListener);
		}else{//分页数据
			adapter.getmMusicList().addAll(olSingerVO.getDataList());
			adapter.notifyDataSetChanged();
		}
		musics=adapter.getmMusicList();//所有已经加载的歌曲
		if(isLoadingFinish){
			more.setVisibility(View.VISIBLE);
			loadingMore.setVisibility(View.GONE);
			String tips=getString(R.string.already_load)+musics.size()+"首";
			LinearLayout layout=(LinearLayout)loading_layout.getChildAt(0);
			TextView textView=(TextView)layout.getChildAt(0);
			textView.setText(tips);
			loading_layout.setOnClickListener(null);
		}
	}
	
	private void showErrorKey(String errorKey){
		// 先隐藏一些按钮
		search_result_tips_after.setVisibility(View.GONE);
		if (errorKey != null && errorKey.length() > 0) {// 设置纠错词
			errorKey = "“" + errorKey + "”";
			ask_wrong.setVisibility(View.VISIBLE);
			search_results_tips.setVisibility(View.VISIBLE);
			search_results_tips.setText(getString(R.string.ask_search_wrong));
			error_word.setText(errorKey);
			error_word.getPaint().setFlags(
					Paint.UNDERLINE_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);// 设置下划线
																		// 去锯齿
			error_word.setOnClickListener(errorTextListener);// 设置纠错词的点击事件
		}else{
			ask_wrong.setVisibility(View.GONE);
			search_results_tips.setVisibility(View.GONE);
		}
	}
	public void initPlayState(){
		for(int i=0;i<musicList.getChildCount();i++){
			View view=musicList.getChildAt(i).findViewById(R.id.play_status);
			if(view!=null){
				view.setVisibility(View.GONE);
			}
		}
	}
	private AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			initPlayState();
			PlayLogicManager.newInstance().stop();
			TextView songId=(TextView)view.findViewById(R.id.songId);
			TextView songName=(TextView)view.findViewById(R.id.musicName);
			TextView artView=(TextView)view.findViewById(R.id.musicArtist);
			ImageView playState=(ImageView)view.findViewById(R.id.play_status);
			playState.setVisibility(View.VISIBLE);
			MusicInfoResult  infoRes=MusicQueryInterface.getMusicInfoByMusicId(SearchResultActivity.this,songId.getText().toString());
			if(infoRes!=null){
				MusicInfo info=infoRes.getMusicInfo();
				
				if(info!=null){
					com.tiantiankuyin.bean.MusicInfo music=new com.tiantiankuyin.bean.MusicInfo();
					music.setArtist(info.getSingerName());
					music.setFileID(info.getMusicId());
					music.setGid(Long.parseLong(info.getSongValidity()));
					music.setTitle(songName.getText().toString());
					music.setNetUrl(info.getSongListenDir());
					music.setAlbum(artView.getText().toString());
					PlayLogicManager.newInstance().playNet(music);
				}
			}
			
		}
	};
	
	private void reStart(){
		adapter=null;
		currentPage=0;
		musics=null;
		errorKey=null;
	}
	
	private View.OnClickListener loadingMoreListener=new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			more.setVisibility(View.GONE);
			loadingMore.setVisibility(View.VISIBLE);
			getSearchSongFromNet(key,currentPage+1);
		}
	};
	
	private LinearLayout getMoreView(String tips){
		more=new LinearLayout(this);
		LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,80);
		more.setOrientation(LinearLayout.HORIZONTAL);
		more.setGravity(Gravity.CENTER);
		TextView textView=new TextView(this);
		textView.setPadding(5, 5, 5, 5);
		textView.setLayoutParams(params);
		textView.setGravity(Gravity.CENTER);
		textView.setText(tips);
		textView.setTextColor(0xffc0c0c0);
		textView.setTextSize(16);
		more.addView(textView);
		
		loadingMore=new LinearLayout(this);
		loadingMore.setOrientation(LinearLayout.HORIZONTAL);
		loadingMore.setGravity(Gravity.CENTER);
		ProgressBar progressBar=new ProgressBar(this);
		progressBar.setIndeterminateDrawable(getResources().getDrawable(R.drawable.online_recommond_loading));
		TextView textView1=new TextView(this);
		textView1.setText("加载中...");
		loadingMore.addView(progressBar);
		loadingMore.addView(textView1);
		loadingMore.setVisibility(View.GONE);
		LinearLayout layout=new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		
		layout.addView(more);
		layout.addView(loadingMore);
		layout.setOnClickListener(loadingMoreListener);
		return  layout;
	}
	
	@Override
	public void onBackPressed() {
		back();
	}
	
	private void back(){
		Intent intent = new Intent(backAction);
		intent.putExtra(BaseActivity.INTENT_ACTIVITY_NAME, activityName);
		TianlApp.activityBundles.remove(IntentAction.INTENT_ONLINE_SEARCH_RESULT_ACTIVITY);
		BaseActivity.newInstance().showActivity(intent,1);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		//友盟统计 日志 add by perry 2012-10-23
	   // MobclickAgent.onPause(this);
	}
}
