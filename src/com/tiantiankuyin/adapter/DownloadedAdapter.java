package com.tiantiankuyin.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tiantiankuyin.bean.MusicInfo;
import com.tiantiankuyin.component.activity.DownloadActivity;
import com.tiantiankuyin.component.activity.DownloadingActivity;
import com.tiantiankuyin.component.service.DownloadFile;
import com.tiantiankuyin.database.bll.LocalMusicManager;
import com.tiantiankuyin.download.DownloadEngine;
import com.tiantiankuyin.view.OperateDialog;
import com.tiantiankuyin.R;

/**
 * 已完成页Listview适配器
 * 
 * @author DK
 * 
 */
public class DownloadedAdapter extends BaseAdapter implements OnClickListener {

	public static List<DownloadFile> downloadedFiles;

	public DownloadedAdapter(List<DownloadFile> files) {
		downloadedFiles = Collections
				.synchronizedList(new ArrayList<DownloadFile>());
		if (downloadedFiles != null)
			downloadedFiles.addAll(files);
	}
	
	@Override
	public void notifyDataSetChanged() {
		Set<DownloadFile> files = DownloadEngine.downloadedFiles;
		if (files != null) {
			downloadedFiles.clear();
			downloadedFiles.addAll(files);
			super.notifyDataSetChanged();
		}
	}

	@Override
	public int getCount() {
		return downloadedFiles.size();
	}

	@Override
	public Object getItem(int position) {
		return downloadedFiles.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		DownloadFile file = downloadedFiles.get(position);
		DownloadedHolder holder;
		if (convertView == null) {
			holder = new DownloadedHolder(file);
			LayoutInflater inflater = (LayoutInflater) DownloadingActivity.newInstance().getParent().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.downloaded_item, null);
			holder.textNumTV = (TextView) convertView
					.findViewById(R.id.textNum);
			holder.songNameTV = (TextView) convertView
					.findViewById(R.id.plist_song_name);
			holder.singerNameTV = (TextView) convertView
					.findViewById(R.id.plist_artist_name);
			holder.operatMusicLayout = (LinearLayout) convertView
					.findViewById(R.id.pl_operat_music);
			convertView.setTag(holder);
		} else {
			holder = (DownloadedHolder) convertView.getTag();
		}
		holder.file = file;
		holder.textNumTV.setText((position + 1) + "");
		String songName = file.getSongName();
		holder.songNameTV.setText(songName == null ? file.getFileName() : songName);
		String singerName = file.getSingerName();
		holder.singerNameTV.setText(songName == null ? file.getSingerName() : singerName);
		holder.operatMusicLayout.setOnClickListener(this);
		holder.operatMusicLayout.setTag(file);
		return convertView;
	}

	public class DownloadedHolder {

		public DownloadedHolder(DownloadFile file) {
			this.file = file;
		}

		public TextView textNumTV;
		public TextView songNameTV;
		public TextView singerNameTV;
		public LinearLayout operatMusicLayout;
		public DownloadFile file;

		@Override
		public boolean equals(Object o) {
			if (o != null) {
				DownloadedHolder another = (DownloadedHolder) o;
				if (another != null && another.file != null
						&& this.file != null && this.file.equals(another.file))
					return true;
			}
			return false;
		}

		@Override
		public int hashCode() {
			return Integer.valueOf(file.getFileID());
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pl_operat_music:
			DownloadFile file = (DownloadFile) v.getTag();
			List<MusicInfo> list = LocalMusicManager.getInstence().getMusicListByFileID(file.getFileID());
			MusicInfo info = list == null || list.size() <= 0 ? null : list.get(0);
			if (info != null) {
				OperateDialog dialog = new OperateDialog(
						DownloadActivity.instance, R.style.easouDialog);
//				dialog.setImageView((ImageView) v
//						.findViewById(R.id.pl_opeart_img));
				dialog.setMusicInfo(info);
				dialog.setMusicInfos(list);
				dialog.setIndex(0);
				dialog.show();
			}
			break;
		}
	}
}
