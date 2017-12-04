/**
 * 
 */
package com.tiantiankuyin.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tiantiankuyin.bean.MusicInfo;
import com.tiantiankuyin.R;

/**
 * @author Erica
 * 
 */
public class AllMusicAdapter extends BaseAdapter implements OnClickListener {

	public Context context;
	/** 全部歌曲列表 */
	public List<MusicInfo> mList;
	/** 当前的位置 */
	public int point; 

	public AllMusicAdapter(Context mContext, List<MusicInfo> list) {
		context = mContext;
		mList = list;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(R.layout.musiclist_item, null);
			holder = new ViewHolder();
			holder.songName = (TextView) convertView
					.findViewById(R.id.plist_song_name);
			holder.artistName = (TextView) convertView
					.findViewById(R.id.plist_artist_name);
			holder.textNum = (TextView) convertView.findViewById(R.id.textNum);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.songName.setText(mList.get(position).getTitle());
		holder.artistName.setText(mList.get(position).getArtist());
		holder.textNum.setText(position + 1 + "");

		return convertView;
	}

	private View.OnClickListener listener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			LinearLayout layout = (LinearLayout) (v.getParent().getParent()
					.getParent().getParent());
			TextView textNum = (TextView) layout.findViewById(R.id.textNum);
			point = Integer.parseInt(textNum.getText().toString()) - 1;// 获取当前点击的
																		// 是那个item

//			try {
//					PlayLogicManage.newInstance().setMusicInfo(mList, point);
//					PlayLogicManage.newInstance().play();
//			} catch (IllegalStateException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
	};

	private static class ViewHolder {
		TextView songName;
		TextView artistName;
		TextView textNum;
	}

	@Override
	public void onClick(View v) {

	}

}
