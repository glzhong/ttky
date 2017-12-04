package com.tiantiankuyin.component.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.tiantiankuyin.adapter.DownloadedAdapter;
import com.tiantiankuyin.adapter.DownloadedAdapter.DownloadedHolder;
import com.tiantiankuyin.bean.MusicInfo;
import com.tiantiankuyin.component.service.DownloadFile;
import com.tiantiankuyin.component.service.DownloadService;
import com.tiantiankuyin.component.service.DownloadService.DownloadBinder;
import com.tiantiankuyin.database.bll.SqlString;
import com.tiantiankuyin.download.DownloadEngine;
import com.tiantiankuyin.play.PlayLogicManager;
import com.tiantiankuyin.R;

/**
 * 已完成页
 * 
 * @author DK
 * 
 */
public class DownloadedActivity extends Activity implements OnClickListener,
		OnItemClickListener {

	public static final String ACTIVITY_ID = "DownloadedActivity";
	public static final int DOWNLOAD_FILE_COMPLETED = 0x1;

	private LinearLayout clearLayout; // 清空列表
	private Set<DownloadFile> filesSet; // 已完成
	private static DownloadedAdapter adapter;
	private static Handler mHandler;
	private ListView downloadedLV;

	private static DownloadedActivity downloadedActivity;

	public static DownloadedActivity newInstance() {
		return downloadedActivity;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.downloaded);
		findview();
		init();
		downloadedActivity = this;
	}

	private void findview() {
		clearLayout = (LinearLayout) findViewById(R.id.clearAllDownloaded);
		downloadedLV = (ListView) findViewById(R.id.downloadedListview);
	}

	private void init() {
		clearLayout.setOnClickListener(this);
		filesSet = DownloadEngine.downloadedFiles;
		if (filesSet == null)
			adapter = new DownloadedAdapter(null);
		else
			getMusicList();
			adapter = new DownloadedAdapter(new ArrayList<DownloadFile>(
					filesSet));
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case DOWNLOAD_FILE_COMPLETED:
					adapter.notifyDataSetChanged();
					break;
				}
			}
		};
		downloadedLV.setAdapter(adapter);
		downloadedLV.setOnItemClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.clearAllDownloaded:
			List<DownloadFile> downloadedFiles = DownloadedAdapter.downloadedFiles;
			Iterator<DownloadFile> iterator = downloadedFiles.iterator();
			while (iterator.hasNext()) {
				DownloadFile nextFile = iterator.next();
				DownloadBinder binder = DownloadService.newInstance().binder;
				if (binder != null)
					binder.deleteDownloadedTask(nextFile);
			}
			adapter.notifyDataSetChanged();
			break;
		}
	}

	public void onDownloadFileCompleted(DownloadFile file) {
		mHandler.sendEmptyMessage(DOWNLOAD_FILE_COMPLETED);
	}
	

	public void getMusicList(){
		
		File file=new File(Environment.getExternalStorageDirectory()+"/HighMusic/music/");
		File [] files=file.listFiles();
		for (int i = 0; i < files.length; i++) {
			System.out.println("=========================文件"+files[i].getPath());
			DownloadFile downFile=new DownloadFile();
			downFile.setFileName(files[i].getName().substring(0,files[i].getName().indexOf("-")));
			downFile.setSavePath(Environment.getExternalStorageDirectory()+"/HighMusic/music/"+files[i].getName());
			downFile.setSingerName(files[i].getName().substring(files[i].getName().indexOf("-")+1, files[i].getName().indexOf(".")));
			filesSet.add(downFile);			 
		}
		 
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		try {
			DownloadedHolder holder = (DownloadedHolder) view.getTag();
			DownloadFile file = (DownloadFile) holder.operatMusicLayout.getTag();
			MusicInfo musicInfo = file.getMusicInfo();
			List<MusicInfo> musicInfos = new ArrayList<MusicInfo>();
			List<DownloadFile> downloadedFiles = DownloadedAdapter.downloadedFiles;
			Iterator<DownloadFile> iterator = downloadedFiles.iterator();
//			StringBuilder sb = new StringBuilder();
			while(iterator.hasNext()){
				DownloadFile next = iterator.next();
				musicInfos.add(next.getMusicInfo());
//				sb.append(next.getFileID()).append(",");
			}
//			if(sb.length() > 0);
//				sb.deleteCharAt(sb.length() - 1);
				if (musicInfo != null) {
					int indexOf = musicInfos.indexOf(musicInfo);
					PlayLogicManager.newInstance().setMusicInfoAndPlay(musicInfos,
							indexOf, SqlString.getSqlForSelectMusicByFileId(String.valueOf(musicInfo.getFileID())));
				}
		} catch (IllegalStateException e) {
//			e.printStackTrace();
		} 
	}

	public static void delete(MusicInfo info) {
		List<DownloadFile> downloadedFiles = DownloadedAdapter.downloadedFiles;
		if (downloadedFiles == null)
			return;
		Iterator<DownloadFile> iterator = downloadedFiles.iterator();
		while (iterator.hasNext()) {
			DownloadFile next = iterator.next();
			MusicInfo musicInfo = next.getMusicInfo();
			if (musicInfo != null && info != null
					&& musicInfo.getFileID() == info.getFileID()) {
				DownloadBinder binder = DownloadService.newInstance().binder;
				if (binder != null)
					binder.deleteDownloadedTask(next);
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
