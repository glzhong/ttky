package com.tiantiankuyin.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tiantiankuyin.component.activity.DownloadActivity;
import com.tiantiankuyin.component.activity.DownloadingActivity;
import com.tiantiankuyin.component.service.DownloadFile;
import com.tiantiankuyin.component.service.DownloadService;
import com.tiantiankuyin.component.service.DownloadFile.DownloadType;
import com.tiantiankuyin.download.DownloadEngine;
import com.tiantiankuyin.download.IDownloadFileListener.DownloadState;
import com.tiantiankuyin.utils.CommonUtils;
import com.tiantiankuyin.view.EasouDialog;
import com.tiantiankuyin.R;

/**
 * 正在进行页Listview适配器
 * 
 * @author DK
 * 
 */
public class DownloadingAdapter extends BaseAdapter implements OnClickListener,
		android.content.DialogInterface.OnClickListener {

	public static List<DownloadFile> downloadingFiles;

	public DownloadingAdapter(List<DownloadFile> files) {
		downloadingFiles = Collections
				.synchronizedList(new ArrayList<DownloadFile>());
		if (DownloadEngine.downloadingFiles != null && files != null)
			downloadingFiles.addAll(DownloadEngine.downloadingFiles);
		// downloadingFiles = DownloadEngine.downloadingFiles;
	}

	//
	// @Override
	// public void notifyDataSetChanged() {
	// Iterator<DownloadFile> iterator = downloadingFiles.iterator();
	// while(iterator.hasNext()){
	// DownloadFile file = iterator.next();
	// if(file.getState() == DownloadState.STATE_DOWNCOMPLETE)
	// iterator.remove();
	// }
	// super.notifyDataSetChanged();
	// }

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

	public void notifyDataSetChangedWithCleanData() {

		downloadingFiles.clear();
		downloadingFiles.addAll(DownloadEngine.downloadingFiles);
		super.notifyDataSetChanged();
	}

	public void setData() {
		downloadingFiles.clear();
		downloadingFiles.addAll(DownloadEngine.downloadingFiles);
	}

	@Override
	public int getCount() {
		return downloadingFiles == null ? 0 : downloadingFiles.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		DownloadFile file = downloadingFiles.get(position);
		DownloadingHolder holder;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) DownloadingActivity
					.newInstance().getParent()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.downloading_item, null);
			holder = new DownloadingHolder();
			holder.fileStateIV = (ImageView) convertView
					.findViewById(R.id.stateImg);
			holder.songNameTV = (TextView) convertView
					.findViewById(R.id.songeName);
			holder.rateTV = (TextView) convertView.findViewById(R.id.progress);
			holder.percentTV = (TextView) convertView
					.findViewById(R.id.percent);
			holder.retryTV = (TextView) convertView.findViewById(R.id.retry);
			holder.wifiTV = (TextView) convertView.findViewById(R.id.wifi);
			holder.deleteLayout = (LinearLayout) convertView
					.findViewById(R.id.delete);
			holder.deleteLayout.setOnClickListener(this);
			holder.deleteIV = (ImageView) convertView
					.findViewById(R.id.deleteImg);
			holder.progressBar = (ProgressBar) convertView
					.findViewById(R.id.downing_item_progress);
			convertView.setTag(holder);
		} else {
			holder = (DownloadingHolder) convertView.getTag();
		}
		if (file != null) {
			holder.rateTV.setTag(file.getFileID() + "rateTV");
			holder.percentTV.setTag(file.getFileID() + "percentTV");
			holder.progressBar.setTag(file.getFileID() + "progressBar");
			holder.deleteLayout.setTag(file);
			if (file.getState() != null) {
				if (file.getState() == DownloadState.STATE_DOWNING) {// 正在进行
					holder.fileStateIV
							.setImageResource(R.drawable.download_status_start);
				} else if (file.getState() == DownloadState.STATE_FAILED) {// 失败
					holder.fileStateIV
							.setImageResource(R.drawable.download_status_fail);
				} else if (file.getState() == DownloadState.STATE_PAUSED) {// 暂停
					holder.fileStateIV
							.setImageResource(R.drawable.download_status_pause);
				} else if (file.getState() == DownloadState.STATE_WAITING) {// 等待
					holder.fileStateIV
							.setImageResource(R.drawable.download_status_wait);
				}
			}
			if (file.getFileName() != null) {
				String fileName = file.getFileName();
				if (fileName.length() > 20)
					fileName = fileName.substring(0, 19) + "...";
				holder.songNameTV.setText(file.getSongName());
			}
			String fileSizeFormate = CommonUtils.fileSizeFormate(file
					.getFileCurrentSize() == file.getFileTotalSize() ? file
					.getFileTotalSize() : file.getFileCurrentSize());
			String fileTotalSizeFormate = CommonUtils.fileSizeFormate(file
					.getFileTotalSize());
			String rate = fileSizeFormate + "/" + fileTotalSizeFormate;
			holder.rateTV.setText(rate);
			long fileCurrentSize = file.getFileCurrentSize();
			long fileTotalSize = file.getFileTotalSize();
			fileCurrentSize = fileCurrentSize == fileTotalSize ? fileTotalSize
					: fileCurrentSize;

			if (fileTotalSize != 0) {
				int percent = ((int) (fileCurrentSize * 100 / fileTotalSize) >= 100 ? 100
						: (int) (fileCurrentSize * 100 / fileTotalSize));
				holder.percentTV.setText(percent + "%");
			} else {
				holder.percentTV.setText("0%");
			}

			// 判断当前是否为失败
			if (file.getState() == DownloadState.STATE_FAILED) {// 当前为失败状态，隐藏完成了的
				holder.retryTV.setVisibility(View.VISIBLE);
				holder.percentTV.setVisibility(View.INVISIBLE);
				holder.rateTV.setVisibility(View.INVISIBLE);
			} else {
				holder.retryTV.setVisibility(View.INVISIBLE);
				holder.percentTV.setVisibility(View.VISIBLE);
				holder.rateTV.setVisibility(View.VISIBLE);
			}

			if (fileTotalSize != 0) {
				holder.progressBar.setVisibility(View.VISIBLE);
			} else {
				holder.progressBar.setVisibility(View.INVISIBLE);
			}

			if (file.getFileType() == DownloadType.DOWNLOAD_TYPE_MUSIC_APPOINTMENT)
				holder.wifiTV.setVisibility(View.VISIBLE);
			else
				holder.wifiTV.setVisibility(View.INVISIBLE);

			holder.progressBar.setTag(file);
			holder.progressBar.setMax((int) file.getFileTotalSize());
			holder.progressBar.setProgress((int) file.getFileCurrentSize());
		}
		return convertView;
	}

	public class DownloadingHolder {
		public ImageView fileStateIV;
		public TextView songNameTV;
		public TextView rateTV;
		public TextView percentTV;
		public TextView retryTV;
		public TextView wifiTV;
		public ImageView deleteIV;
		public ProgressBar progressBar;
		public LinearLayout deleteLayout;
	}

	// 删除点击事件处理
	@Override
	public void onClick(View v) {
		EasouDialog.Builder builder = new EasouDialog.Builder(
				DownloadActivity.instance);
		builder.setMessage(R.string.ask_delete);
		builder.setPositiveButton(R.string.delete_sure, this);
		builder.setNegativeButton(R.string.delete_negative, this);
		builder.setTitle(DownloadingActivity.newInstance().getString(
				R.string.local_batch_edit_music_delete)
				+ "\'" + ((DownloadFile) v.getTag()).getSongName() + "\'");
		EasouDialog dialog = builder.create();
		dialog.tag = v.getTag();
		dialog.show();
	}

	// 删除对话框确认与取消
	@Override
	public void onClick(DialogInterface dialog, int which) {
		EasouDialog easouDialog = (EasouDialog) dialog;
		DownloadFile file = (DownloadFile) easouDialog.tag;
		switch (which) {
		case DialogInterface.BUTTON_POSITIVE:
			DownloadService.newInstance().binder.deleteDownloadTask(file);
			Iterator<DownloadFile> iterator = DownloadEngine.downloadingFiles
					.iterator();
			while (iterator.hasNext()) {
				DownloadFile next = iterator.next();
				if (next.equals(file)) {
					iterator.remove();
					DownloadService.newInstance().deleteLocalFile(file);
				}
			}
			notifyDataSetChanged();
			dialog.dismiss();
			break;
		case DialogInterface.BUTTON_NEGATIVE:
			dialog.dismiss();
			break;
		}
	}

}
