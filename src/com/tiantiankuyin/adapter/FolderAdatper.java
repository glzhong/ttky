package com.tiantiankuyin.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tiantiankuyin.bean.FolderVO;
import com.tiantiankuyin.para.SPHelper;
import com.tiantiankuyin.R;
import com.tiantiankuyin.TianlApp;


public class FolderAdatper extends  BaseAdapter{
	private Context context;
	private List<FolderVO> folderVOs;
	
	public FolderAdatper(Context context,List<FolderVO> folderVOs){
		this.context=context;
		this.folderVOs=folderVOs;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return folderVOs.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return folderVOs.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView==null){
			holder=new ViewHolder();
			convertView=LayoutInflater.from(context).inflate(R.layout.local_song_folders_item, null);
			holder.name=(TextView)convertView.findViewById(R.id.name);
			holder.count=(TextView)convertView.findViewById(R.id.count);
			holder.path=(TextView)convertView.findViewById(R.id.path);
			holder.play_status=(ImageView)convertView.findViewById(R.id.play_status);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder)convertView.getTag();
		}
		holder.name.setText(folderVOs.get(position).getFolderName());
		holder.count.setText(folderVOs.get(position).getNumOfSongs()+context.getString(R.string.folder_count));
		String path=folderVOs.get(position).getFolderFullPath();
		String newPath=null;
		if(path.contains("/")){
			newPath=path.replaceAll("/","_");
		}
		String newCurrentPlayPath=TianlApp.currentPlayPath+"/"+newPath;
		if(newCurrentPlayPath!=null&&newCurrentPlayPath.equals(SPHelper.newInstance().getFoldPath())){//判断当前是否在播放这个歌曲
			holder.play_status.setVisibility(View.VISIBLE);
			/*if(PlayLogicManage.newInstance().getIsPlaying()){
				holder.play_status.setImageResource(R.drawable.list_item_play_img);
			}else{
				holder.play_status.setImageResource(R.drawable.list_item_pause_img);
			}*/
			holder.play_status.setImageResource(R.drawable.list_item_play_img);
		}else{
			holder.play_status.setVisibility(View.GONE);
		}
		if(path!=null&&path.contains("/mnt")){
			path=path.substring(4, path.length());
		}
		holder.path.setText(context.getString(R.string.folder_path)+path);
		return convertView;
	}
	class ViewHolder{
		TextView name;
		TextView count;
		TextView path;
		ImageView play_status;
	}
}
