package com.tiantiankuyin.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
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

import com.tiantiankuyin.bean.OlSongVO;
import com.tiantiankuyin.view.EasouOnlineDialog;
import com.tiantiankuyin.R;
import com.tiantiankuyin.TianlApp;

/** 在线音乐列表适配器
 *  */
public class OnlineMusicListAdapter extends BaseAdapter implements OnTouchListener {

	/** 在线歌曲列表对象 */
	private List<OlSongVO>  mMusicList;
	/** 当前操作Context */
	private Context context;
	private Activity activity;
	private ImageView musicOperate;
	private LinearLayout lineLayout;
	
	public OnlineMusicListAdapter(Context context){
		this.context=context;
	}
	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	/** 设置当前操作在线歌曲列表对象 
	 * */
	public void setDatas(List<OlSongVO>  musics,boolean isAppend) {
		if(!isAppend)
			mMusicList = musics;
		else{
			if(mMusicList!=null){
				mMusicList.addAll(musics);
			}else {
				mMusicList = musics;
			}
		}
	}

	public void setmMusicList(List<OlSongVO> mMusicList) {
		this.mMusicList = mMusicList;
	}
	public List<OlSongVO> getmMusicList() {
		return mMusicList;
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
			return position;
		//Long.parseLong(mMusicList.get(position).getGid())
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		MusicHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(TianlApp.newInstance()).inflate(
					R.layout.music_list_item, null);
			holder = new MusicHolder();
			holder.pid = (TextView) convertView.findViewById(R.id.pid);
			holder.musicName = (TextView) convertView
					.findViewById(R.id.musicName);
			holder.songId=(TextView)convertView.findViewById(R.id.songId);
			holder.musicArtist = (TextView) convertView
					.findViewById(R.id.musicArtist);
			holder.playStatus=(ImageView)convertView.findViewById(R.id.play_status);
			holder.musicOperate = (ImageView) convertView
					.findViewById(R.id.musicOperate);
			holder.layout_musicOperate=(LinearLayout)convertView.findViewById(R.id.layout_musicOperate);
			convertView.setTag(holder);
			
		}
		holder = (MusicHolder) convertView.getTag();
		holder.pid.setText(position + 1 + "");
		holder.musicName.setText(mMusicList.get(position).getSong());
		if(mMusicList.get(position).getSinger()==null||mMusicList.get(position).getSinger().length()==0)
			holder.musicArtist.setText(context.getResources().getString(R.string.no_singer));
		else
			holder.musicArtist.setText(mMusicList.get(position).getSinger());
//		convertView.setOnTouchListener(this);
		/*holder.musicOperate.setOnClickListener(listener);*/
		holder.layout_musicOperate.setOnClickListener(listener);
		holder.pid.setTag(mMusicList.get(position));
		holder.songId.setText(mMusicList.get(position).getGid());
		return convertView;
	}

	/**
	 * 歌曲后面的点击事件
	 */
	private View.OnClickListener listener=new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			RelativeLayout layout=(RelativeLayout)v.getParent();
			TextView pid=(TextView)layout.findViewById(R.id.pid);
			ImageView musicOperate=(ImageView)layout.findViewById(R.id.musicOperate);
			musicOperate.setImageResource(R.drawable.list_item_more_btn_press);//设置背景箭头朝上
			LinearLayout lineLayout=(LinearLayout)musicOperate.getParent();
			lineLayout.setClickable(false);//防止连续两次按下点击事件
			OlSongVO musicInfo=(OlSongVO)pid.getTag();
			if(musicInfo!=null){
				showMusicOperatorDialog(musicInfo, musicOperate,lineLayout);
			}
		}
	};
	/**
	 * 歌曲操作弹出框
	 */
	private void showMusicOperatorDialog(OlSongVO musicInfo,ImageView musicOperate,LinearLayout lineLayout){
		EasouOnlineDialog.Builder builder=null;
		this.musicOperate=musicOperate;
		this.lineLayout=lineLayout;
		if(activity!=null){
			builder = new EasouOnlineDialog.Builder(activity.getParent(), musicInfo);
		}else{
			builder = new EasouOnlineDialog.Builder(context, musicInfo);
		}
		
		builder.setOnDismissListener(dismissListener);	
		builder.create().show();
	}
	/** 歌曲界面适配器
	 *  */
	public class MusicHolder {
		public TextView pid;
		public TextView musicName;
		public TextView musicArtist;
		public TextView songId;
		public ImageView playStatus;
		public ImageView musicOperate;
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
}
