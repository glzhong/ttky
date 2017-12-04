package com.tiantiankuyin.adapter;

import java.lang.ref.SoftReference;
import java.util.List;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tiantiankuyin.bean.OlAlbumItem;
import com.tiantiankuyin.bean.OlSongVO;
import com.tiantiankuyin.net.EasouAsyncImageLoader;
import com.tiantiankuyin.net.EasouAsyncImageLoader.ILoadedImage;
import com.tiantiankuyin.utils.CommonUtils;
import com.tiantiankuyin.view.EasouOnlineDialog;
import com.tiantiankuyin.R;

public class EasouExpandableListAdapter extends BaseExpandableListAdapter {

	private List<String> groups;
	private List<List<Object>> musics;
	private  Activity activity;
	private ExpandableListView expandableListView;
	private View.OnClickListener listener;
	private boolean isSingerMore=false;//歌手是否 有加载更多
	private boolean isAlbumMore=false;//精选集是否
	private ImageView musicOperate;
	private LinearLayout lineLayout;
	
	public EasouExpandableListAdapter( Activity activity) {
		this.activity = activity;
	}
	
	public void setExpandableListView(ExpandableListView expandableListView) {
		this.expandableListView = expandableListView;
	}

	public void setSingerMore(boolean isSingerMore) {
		this.isSingerMore = isSingerMore;
	}

	public void setAlbumMore(boolean isAlbumMore) {
		this.isAlbumMore = isAlbumMore;
	}

	public void setListener(View.OnClickListener listener) {
		this.listener = listener;
	}

	public void setGroups(List<String> groups) {
		this.groups = groups;
	}

	public void setMusics(List<List<Object>> musics) {
		this.musics = musics;
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return groups.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		if(groupPosition<musics.size()){
			return musics.get(groupPosition).size();
		}
		return 0;
	}
	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return groups.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {

		return null;
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		TitleViewHolder holder;
		if(convertView==null){
			holder=new TitleViewHolder();
			convertView=LayoutInflater.from(activity).inflate(R.layout.online_similar_expandlistview_item_tilte_bg, null);
			holder.title=(TextView)convertView.findViewById(R.id.title);
			convertView.setTag(holder);
		}else{
			holder=(TitleViewHolder)convertView.getTag();
		}
		if(groups.size()>0 && groupPosition >= 0)
			holder.title.setText(groups.get(groupPosition));
		return convertView;
	}
	
	private LinearLayout getLoadindView(){
		LinearLayout layout=new LinearLayout(activity);
		/*LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,60);
		layout.setLayoutParams(params);*/
		layout.setOrientation(LinearLayout.HORIZONTAL);
		layout.setGravity(Gravity.CENTER);
		ProgressBar progressBar=new ProgressBar(activity);
		progressBar.setIndeterminateDrawable(activity.getResources().getDrawable(R.drawable.online_recommond_loading));
		TextView textView=new TextView(activity);
		textView.setText("加载中...");
		layout.addView(progressBar);
		layout.addView(textView);
		return  layout;
	}

	private DialogInterface.OnDismissListener dismissListener=new DialogInterface.OnDismissListener() {
		@Override
		public void onDismiss(DialogInterface dialog) {
			if(musicOperate!=null){
				musicOperate.setImageResource(R.drawable.list_item_more_btn_default);//设置背景箭头朝上
			}
			if(lineLayout!=null){
				lineLayout.setClickable(true);
			}
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
	
	private View.OnClickListener clickListener =new View.OnClickListener() {
		@Override
		public void onClick(View v) {
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
		}
	};
	
	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		if(musics != null && !musics.isEmpty() && musics.get(groupPosition).get(childPosition) instanceof OlSongVO){
			if(((OlSongVO)musics.get(groupPosition).get(childPosition)).getSong().equals("test")){//假数据测试用的
				return getLoadindView();
			}
		}else{
			if(musics != null && !musics.isEmpty() && ((OlAlbumItem)musics.get(groupPosition).get(childPosition)).getName().equals("test")){//假数据测试用的
				return getLoadindView();
			}
		}
		if(musics != null && !musics.isEmpty() &&  musics.get(groupPosition).get(childPosition) instanceof OlSongVO){
			ViewHolder holder = new ViewHolder();
			convertView=LayoutInflater.from(activity).inflate(R.layout.similar_music_list_item, null);
			holder.musicID = (TextView) convertView.findViewById(R.id.musicID);
			holder.musicName = (TextView) convertView
					.findViewById(R.id.musicName);
			holder.musicArtist = (TextView) convertView
					.findViewById(R.id.musicArtist);
			holder.musicOperate = (ImageView) convertView
					.findViewById(R.id.musicOperate);
			holder.layout_musicOperate=(LinearLayout)convertView.findViewById(R.id.layout_musicOperate);
			convertView.setTag(holder);
			holder.musicID.setTag(musics.get(groupPosition).get(childPosition));
			holder.musicID.setText(childPosition + 1 + "");
			holder.musicName.setText(((OlSongVO)musics.get(groupPosition).get(childPosition)).getSong());
			holder.musicArtist.setText(((OlSongVO)musics.get(groupPosition).get(childPosition)).getSinger());
			holder.layout_musicOperate.setOnClickListener(clickListener);
			if(isSingerMore){//有更多数据
				getViewByLoadingMore(convertView,groupPosition,childPosition);
			}
			convertView.setBackgroundResource(R.drawable.similar_item_bg_selector);
			if(childPosition==(musics.get(groupPosition).size()-1)){//最后一个不需要分割线
				ImageView imageView=(ImageView)convertView.findViewById(R.id.line);
				imageView.setVisibility(View.GONE);
			}
		}else{//所属精选集
			AlbumItemHolder holder = new AlbumItemHolder();
			convertView = LayoutInflater.from(activity).inflate(
					R.layout.similar_online_featured_set_item, null);
			holder.image = (ImageView) convertView
					.findViewById(R.id.online_featured_set_item_image);
			holder.title = (TextView) convertView
					.findViewById(R.id.online_select_item_title);
			holder.num = (TextView) convertView
					.findViewById(R.id.online_featured_set_num);
			holder.content = (TextView) convertView
					.findViewById(R.id.online_featured_set_item_content);
			convertView.setTag(holder);
			// 设置item的信息
			final OlAlbumItem olAlbumItem = (OlAlbumItem)musics.get(groupPosition).get(childPosition);
			String content;
			String title;
			if(olAlbumItem.getName()!=null){
				title = olAlbumItem.getName().trim();
				holder.title.setText(title);
			}
			if(olAlbumItem.getIntro()!=null){
				content = olAlbumItem.getIntro();
				holder.content.setText(content);
			}
			if(olAlbumItem.getMusicCount()>0){
				holder.num.setText("(" + olAlbumItem.getMusicCount() + ")");
			}else{
				holder.num.setText("(" + 0 + ")");
			}
			if(olAlbumItem.getImgUrl()!=null){
				String save_name = CommonUtils.MD5(olAlbumItem.getImgUrl());
				if(holder.image.getTag()==null){
					holder.image.setTag(save_name);
					EasouAsyncImageLoader.newInstance().loadImage(olAlbumItem.getImgUrl(), iLoadedImage,save_name);
				}else{
					if(!(holder.image.getTag().equals(save_name))){
						holder.image.setTag(save_name);
						EasouAsyncImageLoader.newInstance().loadImage(olAlbumItem.getImgUrl(), iLoadedImage, save_name);
					}
				}
			}
			if(isAlbumMore){//有更多数据
				getViewByLoadingMore(convertView,groupPosition,childPosition);
			}
			if(childPosition==(musics.get(groupPosition).size()-1)){//最后一个不需要分割线
				ImageView imageView=(ImageView)convertView.findViewById(R.id.line);
				imageView.setVisibility(View.GONE);
			}
		}
		return convertView;
	}
	
	private  void getViewByLoadingMore(View convertView,int groupPosition,int childPosition){
		if(childPosition==4&&childPosition==(musics.get(groupPosition).size()-1)){//最后一行了
			View view=LayoutInflater.from(activity).inflate(R.layout.online_similar_loading_more_item, null);
			view.setOnClickListener(listener);
			view.setTag(groupPosition+"_"+childPosition);
			((LinearLayout)convertView).addView(view);
		}
	}
	
	/** 加载图片对象
	 * @author Erica
	 *  */
	private ILoadedImage iLoadedImage = new ILoadedImage() {
		@Override
		public void onFinishLoadedLRC(String lrcPath, String songName) {
		}
		@Override
		public void onFinishLoaded(SoftReference<Drawable> drawable, String saveName) {
			ImageView image =(ImageView)expandableListView.findViewWithTag(saveName);
			if(image!=null&&drawable!=null){
				image.setImageDrawable(drawable.get());
				Animation animation = AnimationUtils.loadAnimation(activity, R.anim.push_in);
				image.startAnimation(animation);
			}
		}
		@Override
		public void onError(Exception e) {
		}
	};
	
	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	class TitleViewHolder {
		TextView title;
	}
	
	/** 精选集界面适配器控制对象
	 *  */
	class AlbumItemHolder {
		public ImageView image;
		public TextView title;
		public TextView num;
		public TextView content;
	}
	
	class ViewHolder {
		TextView musicID;
		TextView musicName;
		TextView musicArtist;
		ImageView musicOperate;
		LinearLayout layout_musicOperate;
	}
	
}
