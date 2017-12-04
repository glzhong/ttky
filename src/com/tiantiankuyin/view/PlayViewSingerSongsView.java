package com.tiantiankuyin.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.tiantiankuyin.bean.OlSingerVO;
import com.tiantiankuyin.bean.OlSongVO;
import com.tiantiankuyin.database.bll.OnDataPreparedListener;
import com.tiantiankuyin.database.bll.OnlineMusicManager;
import com.tiantiankuyin.net.NetCache;
import com.tiantiankuyin.play.PlayLogicManager;
import com.tiantiankuyin.utils.CommonUtils;
import com.tiantiankuyin.utils.Lg;
import com.tiantiankuyin.R;
import com.tiantiankuyin.TianlApp;

public class PlayViewSingerSongsView extends LinearLayout {

	private String singerName;
	/** 当前页 */
	private int currentPage;
	private LinearLayout layout;
	/** 记录索引 */
	private int index=0; 
	private Activity activity;
	private View view;
	private OlSingerVO before;
	/** 加载更多 */
	private LinearLayout more;
	/**  跟多旋转框 */
	private LinearLayout loadingMore;
	private LinearLayout loading_layout;
	private ImageView musicOperate;
	private LinearLayout lineLayout;
	private ScrollView scrollView;
	/** 集合对象 */
	private List<OlSongVO> musics=new ArrayList<OlSongVO>(); 
	
	public PlayViewSingerSongsView(Activity activity,String singerName){
		super(activity);
		this.activity=activity;
		this.singerName=singerName;
		if(loading_layout==null){
			loading_layout=getMoreView(activity.getString(R.string.net_more_txt));
		}
		if(singerName!=null){
			initData(1);
		}
		init();
	}

	private void init(){
		view=LayoutInflater.from(activity).inflate(R.layout.loading_layout,null);
		this.addView(view);
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
	 * 加载歌手的其他歌曲
	 * 
	 * @param singerName
	 */
	private void initData(int pageNum) {
		before=null;
		final String url = CommonUtils.getOlQueryRequestURL("sg", singerName, pageNum);
		try {
			before =NetCache.readCache(url);
			refreshSongListView(before);
		} catch (ClassCastException e) {
			//e.printStackTrace();
		} catch (IOException e) {
			//e.printStackTrace();
		} catch (ClassNotFoundException e) {
			//e.printStackTrace();
		}
		
		OnlineMusicManager.getInstence().getSongListData(activity,
				new OnDataPreparedListener<OlSingerVO>() {
					@Override
					public void onDataPrepared(OlSingerVO data) {
						if(data!=null&&data.getDataList().size()>0){
							boolean flag=compatorHotSale(before,data);
							if(flag){
								return ;
							}else{
								if(musics!=null&&before!=null){
									musics.removeAll(before.getDataList());//删除以前老数据
								}
							}
							refreshSongListView(data);
							try {
								NetCache.saveCache(data, url);//保存缓存
							} catch (IOException e) {
								//e.printStackTrace();
							}
						}else {
							Lg.d("getSongListData() == null");
							return;
						}
					}
				}, url);
	}

	private void refreshSongListView(OlSingerVO olSingerVO){//分页数据加显示
		/*this.removeAllViews();*/
		if(view!=null){
			this.removeView(view);
			view=null;
		}
		musics.addAll(olSingerVO.getDataList());
		currentPage=olSingerVO.getThisPage();
		if(layout==null){
			scrollView=new ScrollView(activity);
			layout=new LinearLayout(activity);
			layout.setOrientation(LinearLayout.VERTICAL);
			LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
			layout.setLayoutParams(params);
			scrollView.setLayoutParams(params);
			scrollView.addView(layout);
			this.addView(scrollView);
		}else{
			layout.removeView(layout.getChildAt(layout.getChildCount()-1));
		}
		for(int i=0;i<olSingerVO.getDataList().size();i++){
			OlSongVO olSongVO=olSingerVO.getDataList().get(i);
			layout.addView(getItemView(olSongVO,index));
			index++;
		}
		boolean isFinish=false;//是否已经 全部加载完成
		if(olSingerVO.getThisPage()<olSingerVO.getCountPage()){//当前页小于  总页数 要加载分页
			more.setVisibility(View.VISIBLE);
			loadingMore.setVisibility(View.GONE);
			LinearLayout text_layout=(LinearLayout)loading_layout.getChildAt(0);
			TextView textView=(TextView)text_layout.getChildAt(0);
			textView.setText(activity.getString(R.string.net_more_txt));
			layout.addView(loading_layout);
		}else{
			isFinish=true;
		}
		if(isFinish){//已经加载完成
			more.setVisibility(View.VISIBLE);
			loadingMore.setVisibility(View.GONE);
			String tips=activity.getString(R.string.already_load)+musics.size()+"首";
			LinearLayout text_layout=(LinearLayout)loading_layout.getChildAt(0);
			TextView textView=(TextView)text_layout.getChildAt(0);
			textView.setText(tips);
			loading_layout.setOnClickListener(null);
			layout.addView(loading_layout);
		}
	}
	private View getItemView(OlSongVO olSongVO,int i){
		View convertView = LayoutInflater.from(TianlApp.newInstance()).inflate(
				R.layout.music_list_item, null);
		TextView musicID = (TextView) convertView.findViewById(R.id.musicID);
		TextView musicName = (TextView) convertView
				.findViewById(R.id.musicName);
		TextView musicArtist = (TextView) convertView
				.findViewById(R.id.musicArtist);
		LinearLayout layout_musicOperate=(LinearLayout)convertView.findViewById(R.id.layout_musicOperate);
		layout_musicOperate.setOnClickListener(itemClickListener);
		musicID.setText(i + 1 + "");
		musicID.setTag(olSongVO);
		musicName.setText(olSongVO.getSong());
		musicArtist.setText(olSongVO.getSinger());
		//分割线
		ImageView imageView=new ImageView(activity);
		imageView.setScaleType(ScaleType.FIT_XY);
		imageView.setImageResource(R.drawable.list_divider_line_img);
		convertView.setTag(olSongVO);
		((LinearLayout)convertView).addView(imageView);
		convertView.setOnClickListener(itemClickListener);
		convertView.setBackgroundResource(R.drawable.similar_item_bg_selector);
		return convertView;
	}

	private DialogInterface.OnDismissListener dismissListener=new DialogInterface.OnDismissListener() {
		@Override
		public void onDismiss(DialogInterface dialog) {
			if(musicOperate!=null){
				musicOperate.setImageResource(R.drawable.list_item_more_btn_default);//设置背景箭头朝上
			}
			lineLayout.setClickable(true);
			dialog.dismiss();
		}
	};
	private void showMusicOperatorDialog(OlSongVO musicInfo,ImageView musicOperate,LinearLayout lineLayout){
		this.musicOperate=musicOperate;
		this.lineLayout=lineLayout;
		EasouOnlineDialog.Builder builder = new EasouOnlineDialog.Builder(activity.getParent(), musicInfo);
		builder.setOnDismissListener(dismissListener);
		builder.create().show();
	}
	private View.OnClickListener itemClickListener=new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if(v.getId()==R.id.layout_musicOperate){//操作框的点击事件
				RelativeLayout layout=(RelativeLayout)v.getParent();
				TextView musicID=(TextView)layout.findViewById(R.id.musicID);
				ImageView musicOperate=(ImageView)layout.findViewById(R.id.musicOperate);
				musicOperate.setImageResource(R.drawable.list_item_more_btn_press);//设置背景箭头朝上
				LinearLayout lineLayout=(LinearLayout)musicOperate.getParent();
				lineLayout.setClickable(false);//防止连续两次按下点击事件
				OlSongVO musicInfo=(OlSongVO)musicID.getTag();
				if(musicInfo!=null){
					showMusicOperatorDialog(musicInfo, musicOperate,lineLayout);
				}
			}else{//播放歌曲
				TextView musicID = (TextView) v.findViewById(R.id.musicID);
				int index =Integer.parseInt(musicID.getText().toString())-1;
				if(index<0){
					index=0;
				}
				try {
					PlayLogicManager.newInstance().setOnlineMusicInfo(musics, index);
					PlayLogicManager.newInstance().play();
				} catch (IllegalStateException e) {
					//e.printStackTrace();
				}
			}
		}
	};
	private View.OnClickListener loadingMoreListener=new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			more.setVisibility(View.GONE);
			loadingMore.setVisibility(View.VISIBLE);
			initData(currentPage+1);
		}
	};

	private LinearLayout getMoreView(String tips){
		more=new LinearLayout(activity);
		LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,80);
		more.setOrientation(LinearLayout.HORIZONTAL);
		more.setGravity(Gravity.CENTER);
		TextView textView=new TextView(activity);
		textView.setPadding(5, 5, 5, 5);
		textView.setLayoutParams(params);
		textView.setGravity(Gravity.CENTER);
		textView.setText(tips);
		textView.setTextColor(0xffc0c0c0);
		textView.setTextSize(16);
		more.addView(textView);
		
		loadingMore=new LinearLayout(activity);
		loadingMore.setOrientation(LinearLayout.HORIZONTAL);
		loadingMore.setGravity(Gravity.CENTER);
		ProgressBar progressBar=new ProgressBar(activity);
		progressBar.setIndeterminateDrawable(getResources().getDrawable(R.drawable.online_recommond_loading));
		TextView textView1=new TextView(activity);
		textView1.setText("加载中...");
		loadingMore.addView(progressBar);
		loadingMore.addView(textView1);
		loadingMore.setVisibility(View.GONE);
		LinearLayout layout=new LinearLayout(activity);
		layout.setOrientation(LinearLayout.VERTICAL);
		
		layout.addView(more);
		layout.addView(loadingMore);
		layout.setOnClickListener(loadingMoreListener);
		return  layout;
	}

}
