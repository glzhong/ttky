package com.tiantiankuyin.view;


import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tiantiankuyin.adapter.SimilarAlbumAdatper;
import com.tiantiankuyin.bean.OlAlbumItem;
import com.tiantiankuyin.bean.OlAlbumVO;
import com.tiantiankuyin.database.bll.OnDataPreparedListener;
import com.tiantiankuyin.database.bll.OnlineMusicManager;
import com.tiantiankuyin.net.NetCache;
import com.tiantiankuyin.utils.CommonUtils;
import com.tiantiankuyin.utils.Lg;
import com.tiantiankuyin.R;

public class PlayViewSimilarAlbumView extends LinearLayout{
	private Context context;
	private ListView listView;
	private long fileId;
	/** 精选集列表适配器 */
	private SimilarAlbumAdatper adatper;
	private int currentPage;//当前页
	private AdapterView.OnItemClickListener itemClick;
	public PlayViewSimilarAlbumView(Context context,long fileId,AdapterView.OnItemClickListener itemClick ){
		super(context);
		this.context=context;
		this.fileId=fileId;
		this.itemClick=itemClick;
		init();
		if(fileId>0){
			initData(fileId,1);
		}
	}
	private void init(){
		View view=LayoutInflater.from(context).inflate(R.layout.online_playview_similar_album, null);
		listView=(ListView)view.findViewById(R.id.online_featured_set_list);
		listView.setOnItemClickListener(itemClick);
		if(loading_layout==null){
			loading_layout=getMoreView(context.getString(R.string.net_more_txt));
			listView.addFooterView(loading_layout);
		}
		this.addView(view);
	}
	private boolean  compatorHotSale(OlAlbumVO befor,OlAlbumVO after){
		if(befor==null||after==null){
			return false;
		}
		if(befor.getDataList().size()!=after.getDataList().size()){
			return false;
		}
		int count=0;
		for(OlAlbumItem olSongVO:befor.getDataList()){
			for(OlAlbumItem song:after.getDataList()){
				if(olSongVO.getId()==song.getId()){
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
	private OlAlbumVO before ;
	private void initData(long id,int pageNum) {
		final String url=CommonUtils.getSongBelongToSelect(id,15, 120,pageNum);
		try {
			before=NetCache.readCache(url);
			refreshAlbum(before);
		} catch (ClassCastException e) {
			//e.printStackTrace();
		} catch (IOException e) {
			//e.printStackTrace();
		} catch (ClassNotFoundException e) {
			//e.printStackTrace();
		}
		OnlineMusicManager.getInstence().getOmnibusData(context,
				new OnDataPreparedListener<OlAlbumVO>() {
					@Override
					public void onDataPrepared(OlAlbumVO data) {
						if(data!=null&&data.getDataList()!=null&&data.getDataList().size()>0){
							boolean flag=compatorHotSale(before,data);
							if(flag){
								return ;
							}else{
								if(adatper!=null&&before!=null){
									adatper.getmOlAlbumVO().getDataList().removeAll(before.getDataList());//删除以前老数据
								}
							}
							refreshAlbum(data);
							try {
								NetCache.saveCache(data, url);//保存缓存
							} catch (IOException e) {
								//e.printStackTrace();
							}
						} else {
							Lg.d("getOmnibusData() == null");
							return;
						}
					}
				}, url);
	}

	/**
	 * 刷新相似歌曲界面
	 * @param songs
	 */
	private  List<OlAlbumItem> items;
	private void refreshAlbum(OlAlbumVO mOlAlbumVO){
		currentPage=mOlAlbumVO.getThisPage();
		boolean isFinish=false;//是否已经 全部加载完成
		if(mOlAlbumVO.getThisPage()<mOlAlbumVO.getCountPage()){//当前页小于  总页数 要加载分页
			more.setVisibility(View.VISIBLE);
			loadingMore.setVisibility(View.GONE);
			LinearLayout layout=(LinearLayout)loading_layout.getChildAt(0);
			TextView textView=(TextView)layout.getChildAt(0);
			textView.setText(context.getString(R.string.net_more_txt));
		}else{
			isFinish=true;
		}
		if(adatper==null){
			adatper=new SimilarAlbumAdatper(context, mOlAlbumVO);
			listView.setAdapter(adatper);
			adatper.setListView(listView);
		}else{
			adatper.getmOlAlbumVO().getDataList().addAll(mOlAlbumVO.getDataList());
			adatper.notifyDataSetChanged();
		}
		items=adatper.getmOlAlbumVO().getDataList();
		if(isFinish){//已经加载完成
			more.setVisibility(View.VISIBLE);
			loadingMore.setVisibility(View.GONE);
			String tips=context.getString(R.string.already_load)+items.size()+"个精选集";
			LinearLayout layout=(LinearLayout)loading_layout.getChildAt(0);
			TextView textView=(TextView)layout.getChildAt(0);
			textView.setText(tips);
			loading_layout.setOnClickListener(null);
		}
	} 
	private View.OnClickListener loadingMoreListener=new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			more.setVisibility(View.GONE);
			loadingMore.setVisibility(View.VISIBLE);
			initData(fileId,currentPage+1);
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
		more=new LinearLayout(context);
		LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,80);
		more.setOrientation(LinearLayout.HORIZONTAL);
		more.setGravity(Gravity.CENTER);
		TextView textView=new TextView(context);
		textView.setPadding(5, 5, 5, 5);
		textView.setLayoutParams(params);
		textView.setGravity(Gravity.CENTER);
		textView.setText(tips);
		textView.setTextColor(0xffc0c0c0);
		textView.setTextSize(16);
		more.addView(textView);
		
		loadingMore=new LinearLayout(context);
		loadingMore.setOrientation(LinearLayout.HORIZONTAL);
		loadingMore.setGravity(Gravity.CENTER);
		ProgressBar progressBar=new ProgressBar(context);
		progressBar.setIndeterminateDrawable(getResources().getDrawable(R.drawable.online_recommond_loading));
		TextView textView1=new TextView(context);
		textView1.setText("加载中...");
		loadingMore.addView(progressBar);
		loadingMore.addView(textView1);
		loadingMore.setVisibility(View.GONE);
		LinearLayout layout=new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);
		
		layout.addView(more);
		layout.addView(loadingMore);
		layout.setOnClickListener(loadingMoreListener);
		return  layout;
	}

} 
