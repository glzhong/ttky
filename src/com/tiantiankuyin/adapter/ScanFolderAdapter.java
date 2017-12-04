package com.tiantiankuyin.adapter;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.tiantiankuyin.utils.Lg;
import com.tiantiankuyin.R;

public class ScanFolderAdapter extends BaseAdapter {

	public Context context;
	public List<File> fileList;
	public CheckBox checkAllCb;
	public static List<File> currentSelected;
	public List<File> totalSelected;
	public Button startScanBtn;

	public ScanFolderAdapter(Context context, List<File> fileList,
			CheckBox checkAllCb, List<File> isSelected,
			List<File> totalSelected, Button startScanBtn) {
		this.context = context;
		this.fileList = fileList;
		this.checkAllCb = checkAllCb;
		this.startScanBtn = startScanBtn;
		currentSelected = isSelected;
		this.totalSelected = totalSelected;
	}

	/** 全选 */ 
	public void setCheckAll() {
		currentSelected.clear();
		currentSelected.addAll(fileList);
		this.notifyDataSetChanged();
	}

	/** 全不选 */ 
	public void setCheckNone() {
		currentSelected.clear();
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return fileList.size();
	}

	@Override
	public Object getItem(int position) {
		return fileList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final int pos = position;
		ViewHolder holder;
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(R.layout.local_scan_folder_item,
					null);
			holder = new ViewHolder();
			holder.fileImg = (ImageView) convertView
					.findViewById(R.id.local_scan_folder_item_img);
			holder.fileName = (TextView) convertView
					.findViewById(R.id.local_scan_folder_item_text);
			holder.fileCheck = (CheckBox) convertView
					.findViewById(R.id.local_scan_folder_item_cb);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		File file = fileList.get(position);
		if (file.isDirectory()) {
			holder.fileImg.setImageResource(R.drawable.scan_folder_icon_img);
		} else {
			holder.fileImg.setImageResource(R.drawable.scan_music_icon_img);
		}
		holder.fileName.setText(file.getName());
		holder.fileCheck.setChecked(currentSelected.contains(fileList
				.get(position)));
		holder.fileCheck.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Lg.d("test", "CheckBox changed");
				CheckBox cb = (CheckBox) v;
				if (cb.isChecked()) {
					currentSelected.add(fileList.get(pos));
				} else {
					currentSelected.remove(fileList.get(pos));
				}
				if (currentSelected.size() == fileList.size()) {
					checkAllCb.setChecked(true);
				} else {
					checkAllCb.setChecked(false);
				}

				if (currentSelected.size() > 0 || totalSelected.size() > 0) {
					startScanBtn.setEnabled(true);
				} else {
					startScanBtn.setEnabled(false);
				}
			}
		});

		return convertView;
	}

	private static class ViewHolder {
		ImageView fileImg;
		TextView fileName;
		CheckBox fileCheck;
	}

}
