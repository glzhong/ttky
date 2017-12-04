package com.tiantiankuyin.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tiantiankuyin.bean.LocalMusicUIBean;
import com.tiantiankuyin.para.SPHelper;
import com.tiantiankuyin.R;
/**
 * 用来显示主界面九宫格的adapter
 * @author easou
 *
 */
public class LocaMusicUIAdapter extends BaseAdapter {
	private Context context;
	private List<LocalMusicUIBean> localMusicUIbeans;
	private int pos=-1;
	
	public LocaMusicUIAdapter(Context context,List<LocalMusicUIBean> localMusicUIbeans){
		this.context=context;
		this.localMusicUIbeans=localMusicUIbeans;
	}
	@Override
	public int getCount() {
		return localMusicUIbeans.size();
	}

	@Override
	public Object getItem(int position) {
		return localMusicUIbeans.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	public void setPlayCurrentFolder(int postion){
		this.setPos(postion);
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView==null){
			holder=new ViewHolder();
			convertView=LayoutInflater.from(context).inflate(R.layout.loca_music_gridview_item, null);
			holder.imageView=(ImageView)convertView.findViewById(R.id.local_music_gridview_item_all_music_icon);
			holder.name=(TextView)convertView.findViewById(R.id.local_music_gridview_item_all_music_name);
			holder.count=(TextView)convertView.findViewById(R.id.local_music_gridview_item_all_music_count);
			holder.play_tips=(ImageView)convertView.findViewById(R.id.play_tips);
			convertView.setTag(holder);
		}else{
			holder =(ViewHolder)convertView.getTag();
		}
		holder.imageView.setImageResource(localMusicUIbeans.get(position).getResId());//设置图片
		holder.name.setText(localMusicUIbeans.get(position).getName());//设置名称
		if(localMusicUIbeans.get(position).getCount()>=0){//因为创建歌单文件是没有数量的 所以做个判断
			holder.count.setVisibility(View.VISIBLE);
			holder.count.setText(String.valueOf(localMusicUIbeans.get(position).getCount()));//设置数量
		}else{
			holder.count.setText("0");
			holder.count.setVisibility(View.INVISIBLE);
		}
		if(position==0){//默认全部歌曲为 播放状态
			holder.play_tips.setVisibility(View.VISIBLE);
		}
		boolean isSingle=true;//是奇数
		if(localMusicUIbeans.size()%2==0){//偶数
			isSingle=false;
		}
		if(!isSingle){//偶数个 
			if((position+1)%2==0){
				if(position!=(localMusicUIbeans.size()-1)){//不是最后一个
					convertView.setBackgroundResource(R.drawable.local_main_l);
				}else{
					convertView.setBackgroundDrawable(null);
				}
			}else{
				if(position!=(localMusicUIbeans.size()-2)){
					convertView.setBackgroundResource(R.drawable.local_main_l);	
				}else{
					convertView.setBackgroundResource(R.drawable.local_main_vertical);
				}
			}
		}else{//奇数个
			if((position+1)%2==0){
				convertView.setBackgroundResource(R.drawable.local_main_l);
			}else{
				if(position!=(localMusicUIbeans.size()-1)){
					convertView.setBackgroundResource(R.drawable.local_main_l);	
				}else{
					convertView.setBackgroundResource(R.drawable.local_main_vertical);
				}
			}
		}
		String newFoldPath=null;
		if(SPHelper.newInstance().getFoldPath()!=null&&SPHelper.newInstance().getFoldPath().contains("/")){
			int index=SPHelper.newInstance().getFoldPath().lastIndexOf("/");
			newFoldPath=SPHelper.newInstance().getFoldPath().substring(0, index);
		}else{
			newFoldPath=SPHelper.newInstance().getFoldPath();
		}
		if(newFoldPath!=null&&newFoldPath.equals(localMusicUIbeans.get(position).getName())){
			holder.play_tips.setVisibility(View.VISIBLE);
		}else{
			holder.play_tips.setVisibility(View.GONE);
		}
		/*if(position==pos){
			holder.play_tips.setVisibility(View.VISIBLE);
		}else{
			holder.play_tips.setVisibility(View.GONE);
		}*/
		return convertView;
	}
	public int getPos() {
		return pos;
	}
	public void setPos(int pos) {
		this.pos = pos;
	}
	class ViewHolder{
		ImageView imageView;
		TextView name;
		TextView count;
		ImageView play_tips;
	}
}
