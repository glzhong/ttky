package com.tiantiankuyin.adapter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.tiantiankuyin.bean.MusicInfo;
import com.tiantiankuyin.R;

public class BatchEditMusicAdapter extends BaseAdapter {

	private Context context;
	private List<MusicInfo> musicInfos;
	public static HashMap<Integer, Boolean> isSelected;
	public CheckBox checkAllCb;
	private Button addToBtn;
	private Button deleteBtn;
	private Button okBtn;

	public BatchEditMusicAdapter(Context context, List<MusicInfo> musicInfos,
			CheckBox checkAllCb, Button addToBtn, Button deleteBtn, Button okBtn) {
		this.context = context;
		this.musicInfos = musicInfos;
		this.checkAllCb = checkAllCb;
		this.addToBtn = addToBtn;
		this.deleteBtn = deleteBtn;
		this.okBtn = okBtn;
		init();
	}

	private void init() {
		isSelected = new HashMap<Integer, Boolean>();
		for (int i = 0; i < musicInfos.size(); i++) {
			isSelected.put(i, false);
		}
	}

	/** 全选
	 *  */ 
	public void setCheckAll() {
		for (int i = 0; i < musicInfos.size(); i++) {
			isSelected.put(i, true);
		}
		this.notifyDataSetChanged();
	}

	/** 全不选 
	 * */ 
	public void setCheckNone() {
		for (int i = 0; i < musicInfos.size(); i++) {
			isSelected.put(i, false);
		}
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return musicInfos.size();
	}

	@Override
	public Object getItem(int position) {
		return musicInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return musicInfos.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final int pos = position;
		ViewHolder holder;

		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(
					R.layout.local_batch_edit_music_item, null);
			holder = new ViewHolder();
			holder.num = (TextView) convertView
					.findViewById(R.id.local_batch_edit_music_item_textNum);
			holder.songName = (TextView) convertView
					.findViewById(R.id.local_batch_edit_music_item_song_name);
			holder.artistName = (TextView) convertView
					.findViewById(R.id.local_batch_edit_music_item_artist_name);
			holder.checkBox = (CheckBox) convertView
					.findViewById(R.id.local_batch_edit_music_item_checkbox);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		MusicInfo musicInfo = musicInfos.get(position);
		holder.num.setText(String.valueOf(position + 1));
		holder.songName.setText(musicInfo.getTitle());
		holder.artistName.setText(musicInfo.getArtist());
		holder.checkBox.setChecked(isSelected.get(position));
		holder.checkBox.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CheckBox cb = (CheckBox) v;
				isSelected.put(pos, cb.isChecked());
				if (cb.isChecked()) {
					// 如果item都勾选了，全选checkbox应该勾选上
					if (isCheckedAll())
						checkAllCb.setChecked(true);
				} else {
					// 如果任何一个item没有勾选，且全部勾选的状态为勾选了，那么取消全部勾选的状态
					if (checkAllCb.isChecked())
						checkAllCb.setChecked(false);
				}

				if (isSelectedMusic()) {
					addToBtn.setEnabled(true);
					deleteBtn.setEnabled(true);
					okBtn.setEnabled(true);
				} else {
					addToBtn.setEnabled(false);
					deleteBtn.setEnabled(false);
					okBtn.setEnabled(false);
				}
			}
		});

		return convertView;
	}

	public static class ViewHolder {
		public TextView num;
		public TextView songName;
		public TextView artistName;
		public CheckBox checkBox;
	}

	/** 判断当前item项是否全部勾选了 
	 * */ 
	private boolean isCheckedAll() {
		boolean result = true;
		Map<Integer, Boolean> map = isSelected;
		Iterator it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			if ((Boolean) entry.getValue() == false) {
				result = false;
				break;
			}
		}
		return result;
	}

	/** 检查是否已经勾选了 
	 * */ 
	private boolean isSelectedMusic() {
		boolean isSelected = false;
		Iterator it = BatchEditMusicAdapter.isSelected.entrySet().iterator();

		while (it.hasNext()) {
			Map.Entry<Integer, Boolean> entry = (Map.Entry<Integer, Boolean>) it
					.next();
			// 如果打钩了
			if (entry.getValue() == true) {
				isSelected = true;
				break;
			}
		}

		return isSelected;
	}

	/**
	 * 将勾选的项，从musicInfos中删除，并从isSelected中删除相应的项
	 * @param removeList
	 */
	public void removeSelected(List<MusicInfo> removeList) {
		if(removeList==null){
			return;
		}
		for(MusicInfo musicInfo : removeList){
			isSelected.remove(musicInfos.indexOf(musicInfo));
		}
		musicInfos.removeAll(removeList);
	}
	
	public void notifyDataSetChanged(){
		super.notifyDataSetChanged();
	}
}
