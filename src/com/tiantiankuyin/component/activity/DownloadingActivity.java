package com.tiantiankuyin.component.activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tiantiankuyin.adapter.DownloadingAdapter;
import com.tiantiankuyin.adapter.DownloadingAdapter.DownloadingHolder;
import com.tiantiankuyin.component.service.DownloadFile;
import com.tiantiankuyin.component.service.DownloadService;
import com.tiantiankuyin.component.service.DownloadFile.DownloadType;
import com.tiantiankuyin.download.DownloadEngine;
import com.tiantiankuyin.download.IDownloadFileListener.DownloadState;
import com.tiantiankuyin.download.IDownloadListener.DownloadErrorType;
import com.tiantiankuyin.para.Env;
import com.tiantiankuyin.utils.CommonUtils;
import com.tiantiankuyin.utils.Lg;
import com.tiantiankuyin.R;
import com.tiantiankuyin.TianlApp;

/**
 * 正在进行页
 * 
 * @author DK
 * 
 */
public class DownloadingActivity extends Activity implements OnClickListener,
		OnItemClickListener {

	private static final long serialVersionUID = 1L;
	public static final String ACTIVITY_ID = "DownloadingActivity";

	private LinearLayout startDownloadAllTV; // 开始全部歌曲
	private LinearLayout pauseDownloadAll; // 暂停全部歌曲
	private ListView downloadingListView;
	public DownloadingAdapter adapter;
	private Set<DownloadFile> filesSet;
	private static Handler mHandler;

	private static DownloadingActivity downloadingActivity;

	private static final int PROGRESS_CHANGE = 0x1; // 进度
	private static final int DOWNLOAD_STATE_CHANGED = 0x2; // 文件状态改变
	private static final int DOWNLOAD_ERROR = 0x3; // 失败
	private static final int DOWNLOADING_FILE_COUNT_CHANGED = 0x4; // 文件数改变
	private static final int DOWNLOAD_FILE_COMPLETED = 0x5; // 完成
	public static final String WIFI_DOWNLOAD_FROM_USER = "WIFI_DOWNLOAD_FROM_USER";

	public static DownloadingActivity newInstance() {
		return downloadingActivity;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.downloading);
		findview();
		init();
		downloadingActivity = this;
	}
	
	private void findview() {
		startDownloadAllTV = (LinearLayout) findViewById(R.id.startDownloadAll);
		pauseDownloadAll = (LinearLayout) findViewById(R.id.pauseDownloadAll);
		downloadingListView = (ListView) findViewById(R.id.downloadingListview);

	}

	private void init() {
		startDownloadAllTV.setOnClickListener(this);
		pauseDownloadAll.setOnClickListener(this);
		downloadingListView.setOnItemClickListener(this);
		Iterator<DownloadFile> iterator = DownloadEngine.downloadingFiles.iterator();
		while(iterator.hasNext()){
			DownloadFile file = iterator.next();
			file.setDownloadListener(TianlApp.newInstance());
		}
		filesSet = DownloadEngine.downloadingFiles;
		if (filesSet == null)
			adapter = new DownloadingAdapter(null);
		else
			adapter = new DownloadingAdapter(new ArrayList<DownloadFile>(
					filesSet));
		downloadingListView.setAdapter(adapter);
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
//				Log.e("---"+msg.what + "---msg"+System.currentTimeMillis());
				//Toast.makeText(DownloadingActivity.this, "下载完成111111", Toast.LENGTH_LONG).show();
				switch (msg.what) {
				case PROGRESS_CHANGE:
				case DOWNLOAD_STATE_CHANGED:
				case DOWNLOAD_ERROR:
//					updateProgress((DownloadFile) msg.obj, msg.arg1, msg.arg2);
//					break;
					adapter.notifyDataSetChanged();
					break;
				case DOWNLOADING_FILE_COUNT_CHANGED:
				case DOWNLOAD_FILE_COMPLETED:
					adapter.notifyDataSetChangedWithCleanData();
					//Toast.makeText(DownloadingActivity.this, "下载完成", Toast.LENGTH_LONG).show();
					break;
				}
			}
		};
	}

	//TODO
	protected void updateProgress(final DownloadFile file, int currLen, int maxLen) {
		Lg.e("updateProgress");
		new Thread(){
			@Override
			public void run() {
				final TextView rateTV = (TextView)downloadingListView.findViewWithTag(file.getFileID()+"rateTV");
				TextView percentTV = (TextView)downloadingListView.findViewWithTag(file.getFileID()+"percentTV");
				final ProgressBar progressBar = (ProgressBar)downloadingListView.findViewWithTag(file.getFileID() + "progressBar");
				
				String fileSizeFormate = CommonUtils.fileSizeFormate(file.getFileCurrentSize() == file.getFileTotalSize() ? file.getFileTotalSize() : file.getFileCurrentSize());
				String fileTotalSizeFormate = CommonUtils.fileSizeFormate(file.getFileTotalSize());
				String rate = fileSizeFormate + "/" + fileTotalSizeFormate;
				if(rateTV == null){
					return;
				}
				
				
				long fileCurrentSize = file.getFileCurrentSize();
				long fileTotalSize = file.getFileTotalSize();
				fileCurrentSize = fileCurrentSize == fileTotalSize ? fileTotalSize
						: fileCurrentSize;
				
//				if (fileTotalSize != 0) {
//					int percent = ((int) (fileCurrentSize * 100 / fileTotalSize) >= 100 ? 100
//							: (int) (fileCurrentSize * 100 / fileTotalSize));
//					percentTV.setText(percent + "%");
//				} else{
//					percentTV.setText("0%");
//				}
				
				final String _rate = rate;
				runOnUiThread(new Runnable(){

					@Override
					public void run() {
						rateTV.setText(_rate);
				
					}
					
				});

			}
		}.start();

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.startDownloadAll:
			startAll();
			break;
		case R.id.pauseDownloadAll:
			pauseAll();
			break;
		}
	}

	private void startAll() {
		Iterator<DownloadFile> iterator = DownloadEngine.downloadingFiles
				.iterator();
		while (iterator.hasNext()) {
			DownloadFile file = iterator.next();
			if (file.getState() != DownloadState.STATE_DOWNING
					|| file.getState() != DownloadState.STATE_DOWNCOMPLETE) {
				DownloadService.newInstance().binder.startDownloadTask(file,
						false);
				if(file.getFileType() == DownloadType.DOWNLOAD_TYPE_MUSIC_APPOINTMENT){
					TianlApp.newInstance().setDownloadFromeUserOperate(true);
				}
			}
		}
		TianlApp.newInstance().setDownloadFromeUserOperate(false);
		Toast.makeText(this, "已全部开始", 0).show();
	}

	private void pauseAll() {
		for (DownloadFile file : DownloadEngine.downloadingFiles) {
			if (file.getState() != DownloadState.STATE_PAUSED
					|| file.getState() != DownloadState.STATE_DOWNCOMPLETE) {
				DownloadService.newInstance().binder.pauseDownloadTask(file);
			}
		}
		TianlApp.newInstance().setDownloadFromeUserOperate(true);
		Toast.makeText(this, "已全部暂停", 0).show();
	}

	public void onDownloadStateChanged(DownloadFile file) {
		mHandler.sendEmptyMessage(DOWNLOAD_STATE_CHANGED);
	}

	public void onDownloadProgressChanged(DownloadFile file, long maxLen,
			long currLen) {
		Message msg = new Message();
		msg.what = PROGRESS_CHANGE;
		msg.obj = file;
		msg.arg1 = (int) currLen;
		msg.arg2 = (int) maxLen;
		mHandler.removeMessages(PROGRESS_CHANGE);
		mHandler.sendMessage(msg);
	}

	public void onDownloadFileNameChanged(DownloadFile file, String fileName) {

	}

	public void onDownloadFileCompleted(DownloadFile file) {
		mHandler.sendEmptyMessage(DOWNLOAD_FILE_COMPLETED);
	}

	public void onDownloadingFileCountChanged(int count) {
		mHandler.sendEmptyMessage(DOWNLOADING_FILE_COUNT_CHANGED);
	}

	public void onDownloadError(DownloadFile file, DownloadErrorType error) {
		mHandler.sendEmptyMessage(DOWNLOAD_ERROR);
	}

	//TODO
	//列表条目点击事件处理
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		DownloadingHolder holder = (DownloadingHolder) view.getTag();
		if (holder == null)
			return;
		DownloadFile file = (DownloadFile) holder.progressBar.getTag();
		if (file == null)
			return;
		//普通 && 正在 || 等待
		if (file.getFileType() == DownloadType.DOWNLOAD_TYPE_MUSIC && file.getState() == DownloadState.STATE_DOWNING
				|| file.getState() == DownloadState.STATE_WAITING) {
			file.setState(DownloadState.STATE_PAUSED);
			boolean lock = TianlApp.newInstance().isWifiDownloadLock();
			boolean wifiAvaliable = Env.isAACCompatiblity();
//			if(!DownloadService.newInstance().hasNormalDownloadFile() && !lock && wifiAvaliable){
//				new Thread(){
//					public void run() {
//						DownloadService.newInstance().binder.startALlWifiDownloadTask();
//					};
//				}.start();
//			}
		//已完成
		} else if (file.getState() == DownloadState.STATE_DOWNCOMPLETE) {
			return;
		//未完成 && (正在 || 等待)
		} else if(file.getState() != DownloadState.STATE_DOWNCOMPLETE && (file.getState() == DownloadState.STATE_WAITING || file.getState() == DownloadState.STATE_DOWNING)){
			DownloadService.newInstance().binder.pauseDownloadTask(file);
		} else {
		//其它
			DownloadService.newInstance().binder.startDownloadTask(file, false);
			if(file.getFileType() == DownloadType.DOWNLOAD_TYPE_MUSIC_APPOINTMENT){
				TianlApp.newInstance().setDownloadFromeUserOperate(true);
			}
		}
		adapter.notifyDataSetChanged();
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	}

	@Override
	public void onPause() {
	    super.onPause();
	}
}
