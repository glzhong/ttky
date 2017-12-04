package com.tiantiankuyin.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tiantiankuyin.bean.MusicInfo;
import com.tiantiankuyin.para.SPHelper;
import com.tiantiankuyin.play.PlayLogicManager;
import com.tiantiankuyin.view.OperateDialog;
import com.tiantiankuyin.R;
import com.tiantiankuyin.TianlApp;

public class MusicListAdapter extends BaseAdapter implements OnTouchListener {

	private List<MusicInfo> mMusicList;
	private long songListId = -1;
	private Context context;
	
	public MusicListAdapter(Context context){
		this.context=context;
	}
	public void setDatas(List<MusicInfo> musics) {
		this.mMusicList = musics;
		notifyDataSetChanged();
	}
	
	public long getSongListId() {
		return songListId;
	}
	public void setSongListId(long songListId) {
		this.songListId = songListId;
	}
	@Override
	public int getCount() {
		if (mMusicList != null)
			return mMusicList.size();
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (mMusicList != null)
			return mMusicList.get(position);
		return null;
	}

	@Override
	public long getItemId(int position) {
		if (mMusicList != null)
			return mMusicList.get(position).getId();
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		MusicHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(TianlApp.newInstance()).inflate(
					R.layout.music_list_item, null);
			holder = new MusicHolder();
			holder.musicID = (TextView) convertView.findViewById(R.id.pid);
			holder.musicName = (TextView) convertView
					.findViewById(R.id.musicName);
			holder.musicArtist = (TextView) convertView
					.findViewById(R.id.musicArtist);
			holder.musicOperate = (ImageView) convertView
					.findViewById(R.id.musicOperate);
			holder.play_status = (ImageView) convertView
					.findViewById(R.id.play_status);
			holder.layout_musicOperate=(LinearLayout)convertView.findViewById(R.id.layout_musicOperate);
			convertView.setTag(holder);
		}
		holder = (MusicHolder) convertView.getTag();
		holder.musicID.setText(position + 1 + "");
		holder.musicName.setText(mMusicList.get(position).getTitle());
		holder.musicArtist.setText(mMusicList.get(position).getArtist());
//		convertView.setOnTouchListener(this);
		/*holder.musicOperate.setOnClickListener(listener);*/
		holder.layout_musicOperate.setOnClickListener(listener);
		holder.musicID.setTag(mMusicList.get(position));
		if(TianlApp.currentPlayPath!=null&&TianlApp.currentPlayPath.equals(SPHelper.newInstance().getFoldPath())&&position==PlayLogicManager.newInstance().getmCurPosition()){//判断当前是否在播放这个歌曲
			holder.play_status.setVisibility(View.VISIBLE);
			if(PlayLogicManager.newInstance().getIsPlaying()){
				holder.play_status.setImageResource(R.drawable.list_item_play_img);
			}else{
				holder.play_status.setImageResource(R.drawable.list_item_pause_img);
			}
		}else{
			holder.play_status.setVisibility(View.GONE);
		}
		return convertView;
	}
	/**
	 * 全部歌曲后面的点击事件
	 */
	private View.OnClickListener listener=new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			RelativeLayout layout=(RelativeLayout)v.getParent();
			TextView musicID=(TextView)layout.findViewById(R.id.pid);
			int index=Integer.parseInt(musicID.getText().toString())-1;
			ImageView musicOperate=(ImageView)layout.findViewById(R.id.musicOperate);
			musicOperate.setImageResource(R.drawable.list_item_more_btn_press);//设置背景箭头朝上
			LinearLayout lineLayout=(LinearLayout)musicOperate.getParent();
			lineLayout.setClickable(false);//防止连续两次按下点击事件
			MusicInfo musicInfo=(MusicInfo)musicID.getTag();
			if(musicInfo!=null){
				showMusicOperatorDialog(musicInfo,musicOperate,index);
			}
		}
	};
	/**
	 * 歌曲操作弹出框
	 */
	private void showMusicOperatorDialog(MusicInfo musicInfo,ImageView musicOperate,int index){
		OperateDialog operateDialog=new OperateDialog(context, R.style.easouDialog);
		operateDialog.setMusicInfo(musicInfo);
		operateDialog.setSongListId(songListId);
		operateDialog.setImageView(musicOperate);
		operateDialog.setMusicInfos(mMusicList);
		operateDialog.setAdapter(this);
		operateDialog.setIndex(index);
		operateDialog.show();
	}
	public class MusicHolder {
		public TextView musicID;
		public TextView musicName;
		public TextView musicArtist;
		public ImageView musicOperate;
		public ImageView play_status;
		public LinearLayout layout_musicOperate;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			v.setBackgroundColor(Color.rgb(247, 247, 247));
			return true;
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			v.performClick();
//			MusicListActivity.itemClick(v);
		}
		v.setBackgroundColor(Color.WHITE);
		return true;
	}
}
