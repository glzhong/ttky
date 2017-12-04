package com.tiantiankuyin.view;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tiantiankuyin.adapter.OnlineMusicListAdapter;
import com.tiantiankuyin.bean.OlAlbumList;
import com.tiantiankuyin.bean.OlSingerVO;
import com.tiantiankuyin.bean.OlSongVO;
import com.tiantiankuyin.database.bll.OnDataPreparedListener;
import com.tiantiankuyin.database.bll.OnlineMusicManager;
import com.tiantiankuyin.net.NetCache;
import com.tiantiankuyin.play.PlayLogicManager;
import com.tiantiankuyin.utils.CommonUtils;
import com.tiantiankuyin.utils.Lg;
import com.tiantiankuyin.R;

public class PlayViewSongDetailView extends LinearLayout{
	private Activity activity;
	private ListView musicList;
	private String singerName;
	private OnlineMusicListAdapter adapter;
	private int currentPage;//当前页
	private int albumId;
	private int countPage;
	public PlayViewSongDetailView(Activity activity,String singerName,int type,int albumId){
		super(activity);
		this.activity=activity;
		this.singerName=singerName;
		this.albumId=albumId;
		init();
		if(singerName!=null){
			initData(1);
		}
		if(albumId>0){
			initAlbumData(albumId,1);//精选集下面的歌曲
		}
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
	private OlSingerVO before;
	private void initAlbumData(int albumId,int pageNum){
		before=null;
		final String url = CommonUtils.getOlAlbumSongRequetURL(albumId,pageNum);
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
		OnlineMusicManager.getInstence().getOmnibusDetailData(activity,
				new OnDataPreparedListener<OlAlbumList>() {
					@Override
					public void onDataPrepared(OlAlbumList data) {
						if(data!=null&&data.getDataList().size()>0){
							OlSingerVO olSingerVO =new OlSingerVO();
							if(countPage==0){
								if(data.getMusicCount()<20){
									countPage=1;
								}else{
									if(data.getMusicCount()%20==0){
										countPage=(int)data.getMusicCount()/20;
									}else{
										countPage=(int)data.getMusicCount()/20+1;
									}
								}
								currentPage=1;
							}
							olSingerVO.setCountPage(countPage);
							olSingerVO.setThisPage(currentPage);
							olSingerVO.setDataList(data.getDataList());
							boolean flag=compatorHotSale(before,olSingerVO);
							if(flag){
								return ;
							}else{
								if(adapter!=null&&before!=null){
									adapter.getmMusicList().removeAll(before.getDataList());//删除以前老数据
								}
							}
							refreshSongListView(olSingerVO);
							try {
								NetCache.saveCache(olSingerVO, url);//保存缓存
							} catch (IOException e) {
								//e.printStackTrace();
							}
						}else {
							Lg.d("getOmnibusDetailData() == null");
							hasNoResultFresh();
							return;
						}
					}
				}, url);
		
	}
	private View view;
	private View imgDanceView;
	private void init(){
		imgDanceView=LayoutInflater.from(activity).inflate(R.layout.loading_layout,null);
		view=LayoutInflater.from(activity).inflate(R.layout.online_playview_similar_song_more, null);
		musicList=(ListView)view.findViewById(R.id.musicList);
		if(loading_layout==null){
			loading_layout=getMoreView(activity.getString(R.string.net_more_txt));
			musicList.addFooterView(loading_layout);
		}
		if(imgDanceView.getParent()==null){
			this.addView(imgDanceView);
		}
	}
	private void hasNoResultFresh(){
		if(imgDanceView!=null){
			this.removeView(imgDanceView);
			this.addView(view);
			imgDanceView=null;
		}
	}
	/**
	 * 加载歌手的其他歌曲
	 * 
	 * @param singerName
	 */
	private void initData(int pageNum) {
		String url = CommonUtils.getOlQueryRequestURL("sg", singerName, pageNum);
		OnlineMusicManager.getInstence().getSongListData(activity,
				new OnDataPreparedListener<OlSingerVO>() {
					@Override
					public void onDataPrepared(OlSingerVO data) {
						if(data!=null&&data.getDataList().size()>0){
							refreshSongListView(data);
						}else {
							Lg.d("getSongListData() == null");
							hasNoResultFresh();
							return;
						}
					}
				}, url);
	}

	private List<OlSongVO> musics;// 集合对象
	private void refreshSongListView(OlSingerVO olSingerVO){
		hasNoResultFresh();
		boolean isFinish=false;//是否已经 全部加载完成
		if(currentPage<countPage){//当前页小于  总页数 要加载分页
			more.setVisibility(View.VISIBLE);
			loadingMore.setVisibility(View.GONE);
			LinearLayout layout=(LinearLayout)loading_layout.getChildAt(0);
			TextView textView=(TextView)layout.getChildAt(0);
			textView.setText(activity.getString(R.string.net_more_txt));
		}else{
			isFinish=true;
		}
		if(adapter==null){
			adapter=new OnlineMusicListAdapter(activity);
			adapter.setActivity(activity);
			adapter.setDatas(olSingerVO.getDataList(), false);
			musicList.setAdapter(adapter);
			musicList.setOnItemClickListener(itemListener);
		}else{
			adapter.getmMusicList().addAll(olSingerVO.getDataList());
			adapter.notifyDataSetChanged();
		}
		musics=adapter.getmMusicList();
		if(isFinish){//已经加载完成
			more.setVisibility(View.VISIBLE);
			loadingMore.setVisibility(View.GONE);
			String tips=activity.getString(R.string.already_load)+musics.size()+"首歌曲";
			LinearLayout layout=(LinearLayout)loading_layout.getChildAt(0);
			TextView textView=(TextView)layout.getChildAt(0);
			textView.setText(tips);
			loading_layout.setOnClickListener(null);
		}
	}
	private AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			try {
					if (musics != null) {
						PlayLogicManager.newInstance().setOnlineMusicInfo(
								musics, position);
						PlayLogicManager.newInstance().play();
					}
			} catch (IllegalStateException e) {
				//e.printStackTrace();
			}
		}
	};

	private View.OnClickListener loadingMoreListener=new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			more.setVisibility(View.GONE);
			loadingMore.setVisibility(View.VISIBLE);
			currentPage=currentPage+1;
			initAlbumData(albumId,currentPage);
		}
	};
	/**
	 * 加载更多
	 */
	private LinearLayout more;
	/**
	 * 跟多旋转框
	 */
	private LinearLayout loadingMore;
	
	private LinearLayout loading_layout;
	
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
