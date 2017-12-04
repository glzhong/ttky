package com.tiantiankuyin.component.activity.local;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.tiantiankuyin.adapter.ScanFolderAdapter;
import com.tiantiankuyin.scan.ScanUtil;
import com.tiantiankuyin.utils.CommonUtils;
import com.tiantiankuyin.utils.Lg;
import com.tiantiankuyin.R;

/**
 * 自定义文件夹扫描页面
 * 
 */
/**
 * @author Perry
 * 
 */
public class ScanFolderActivity extends Activity {

	/** 目录列表适配对象 */
	private ScanFolderAdapter folderAdapter;

	/** 当前目录下的 文件集合 */
	private List<File> fileList;

	/** 当前目录下的选中的文件集合 */
	private List<File> currentSelected;

	/** 已选中的文件集合 */
	private List<File> totalSelected;

	/** 当前的目录列表对象 */
	private ListView listView;

	/** 启动自定义文件夹扫描 */ 
	private final static int RESULT_FOR_DIR_SCAN = 100;

	private TextView currentFileNameTv;
	/** 顶部返回键 */ 
	private ImageButton topBackBtn;
	/** 顶部全选框 */ 
	private CheckBox checkAllCb;
	/** 底部返回键 */ 
	private Button goBackBtn;
	/** 开始扫描按钮 */ 
	private Button startScanBtn;
	/** 向上文件夹 按钮 */ 
	private LinearLayout goParentFolderLayout;
	private TextView currentFolderPathTv;

	private FileFilter dirFilter;
	private FileFilter mediaFilter;
	/** 当前文件路径 */
	private File currentFile; 

	/** 头部视图 */ 
	private static View rootView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		rootView = LayoutInflater.from(this).inflate(
				R.layout.local_scan_folder, null);
		setContentView(rootView);
		findView();
		init();
	}

	public void findView() {
		listView = (ListView) rootView
				.findViewById(R.id.local_scan_folder_list);
		checkAllCb = (CheckBox) rootView
				.findViewById(R.id.local_scan_folder_checkAll);
		currentFileNameTv = (TextView) rootView.findViewById(R.id.title_text);
		topBackBtn = (ImageButton) rootView.findViewById(R.id.back_btn);
		goBackBtn = (Button) rootView
				.findViewById(R.id.local_scan_folder_goBack);
		startScanBtn = (Button) rootView
				.findViewById(R.id.local_scan_folder_startScan);
		goParentFolderLayout = (LinearLayout) rootView
				.findViewById(R.id.local_scan_folder_goParentFolder_layout);
		currentFolderPathTv = (TextView) rootView
				.findViewById(R.id.local_scan_folder_current_path);

	}

	public void init() {
		CommonUtils.setTitle(rootView, "选择文件或文件夹", true, false);
		// 文件夹过滤器
		dirFilter = new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				return (pathname.canRead() && pathname.isDirectory());
			}
		};
		// 音频文件过滤器
		mediaFilter = new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				return (pathname.canRead() && ScanUtil.isMediaFile(pathname));
			}
		};
		// 初始化第一次要展示的文件路径
		currentFile = getRootFile();
		currentFileNameTv.setText(currentFile.getName());
		currentFolderPathTv.setText(currentFile.getAbsolutePath());
		// 获取当前路径下的文件集合。
		fileList = getFileList(currentFile);
		totalSelected = new ArrayList<File>();
		currentSelected = new ArrayList<File>();
		folderAdapter = new ScanFolderAdapter(this, fileList, checkAllCb,
				currentSelected, totalSelected, startScanBtn);

		checkAllCb.setOnClickListener(listener);
		goBackBtn.setOnClickListener(listener);
		topBackBtn.setOnClickListener(listener);
		startScanBtn.setOnClickListener(listener);
		startScanBtn.setEnabled(false);
		goParentFolderLayout.setOnClickListener(listener);
		// 若当前目录为根目录，没有上层文件夹，或为MNT目录，则隐藏 向上 按钮；否则显示。
		if (currentFile.getParent() == null || currentFile.getParentFile().getAbsolutePath().equals("/")) {
			goParentFolderLayout.setVisibility(View.GONE);
		} else {
			goParentFolderLayout.setVisibility(View.VISIBLE);
		}
		listView.setAdapter(folderAdapter);
		listView.setOnItemClickListener(itemClickListener);

	}

	/**
	 * 列表单元的点击事件。
	 * @author Perry
	 */
	private OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (fileList.get(position).isDirectory()) {
				currentFile = fileList.get(position);
				totalSelected.addAll(currentSelected);
				showSubFolderView(currentFile);
			}
		}

	};

	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// 全选框
			if (v.getId() == R.id.local_scan_folder_checkAll) {
				if (checkAllCb.isChecked())
					// 全选
					folderAdapter.setCheckAll();
				else
					// 全不选
					folderAdapter.setCheckNone();
				if (currentSelected.size() > 0 || totalSelected.size() > 0) {
					startScanBtn.setEnabled(true);
				} else {
					startScanBtn.setEnabled(false);
				}
			} else if (v.getId() == R.id.back_btn) {// 底部返回
				backToScanView();
			} else if (v.getId() == R.id.local_scan_folder_goBack) {// 顶部返回
				backToScanView();
			} else if (v.getId() == R.id.local_scan_folder_startScan) {
				Lg.d("test", "show all File Name");
				for (File file : totalSelected) {
					Lg.d("test", file.getPath());
				}
				for (File file : currentSelected) {
					Lg.d("test", file.getPath());
				}

				totalSelected.addAll(currentSelected);

				List<String> pathList = new ArrayList<String>();
				for (File file : totalSelected) {
					pathList.add(file.getAbsolutePath());
				}

				String[] mediaPaths = (String[]) pathList
						.toArray(new String[pathList.size()]);

				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putStringArray("mediaPaths", mediaPaths);
				intent.putExtras(bundle);
				ScanFolderActivity.this.setResult(RESULT_FOR_DIR_SCAN, intent);
				ScanFolderActivity.this.finish();

				// “向上”按钮的响应事件
			} else if (v.getId() == R.id.local_scan_folder_goParentFolder_layout) {
				totalSelected.addAll(currentSelected);
				currentFile = new File(currentFile.getParent());
				showParentFolderView(currentFile);
			}
		}
	};

	/**
	 * 展示子文件夹dirFile下的文件列表。
	 * @param dirFile
	 */
	private void showSubFolderView(File dirFile) {
		fileList.clear();
		fileList.addAll(getFileList(dirFile));
		currentSelected.clear();
		if (totalSelected.contains(dirFile)) {
			totalSelected.remove(dirFile);
			currentSelected.addAll(fileList);
		} else {
			for (File file : totalSelected) {
				if (file.getParentFile().equals(dirFile)) {
					currentSelected.add(file);
				}
			}
		}
		totalSelected.removeAll(currentSelected);

		 currentFileNameTv.setText(dirFile.getName());
		currentFolderPathTv.setText(currentFile.getAbsolutePath());
		folderAdapter.notifyDataSetChanged();
		checkAllCb.setChecked(fileList.size() == currentSelected.size()
				&& fileList.size() != 0);
		// 若当前目录为根目录，没有上层文件夹，或为MNT目录，则隐藏 向上 按钮；否则显示。
		if (currentFile.getParent() == null || currentFile.getParentFile().getAbsolutePath().equals("/")) {
			goParentFolderLayout.setVisibility(View.GONE);
		} else {
			goParentFolderLayout.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 展示子文件夹dirFile下的文件列表。
	 * @param dirFile
	 */
	private void showParentFolderView(File dirFile) {
		// 若当前文件列表全选
		if (fileList.size() == currentSelected.size() && fileList.size() != 0) {
			totalSelected.removeAll(fileList);
			// 添加这文件列表的父文件夹路径进 已选中文件集合中
			totalSelected.add(fileList.get(0).getParentFile());
		}
		fileList.clear();
		fileList.addAll(getFileList(dirFile));
		currentSelected.clear();
		for (File file : fileList) {
			if (totalSelected.contains(file)) {
				currentSelected.add(file);
				totalSelected.remove(file);
			}
		}
		 currentFileNameTv.setText(dirFile.getName());
		currentFolderPathTv.setText(currentFile.getAbsolutePath());
		folderAdapter.notifyDataSetChanged();
		checkAllCb.setChecked(fileList.size() == currentSelected.size()
				&& fileList.size() != 0);
		// 若当前目录为根目录，没有上层文件夹，或为MNT目录，则隐藏 向上 按钮；否则显示。
		if (currentFile.getParent() == null || currentFile.getParentFile().getAbsolutePath().equals("/")) {
			goParentFolderLayout.setVisibility(View.GONE);
		} else {
			goParentFolderLayout.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 获取当可以展示的文件集合
	 * @param currentFile
	 *            当前文件夹路径
	 * @return 当前可以展示的文件集合。
	 * @author Perry
	 */
	private List<File> getFileList(File currentFile) {
		// 获取当前目录下的所有文件夹
		File[] dirFiles = currentFile.listFiles(dirFilter);
		Arrays.sort(dirFiles);
		// 获取当前目录下的所有音频文件
		File[] mediaFiles = currentFile.listFiles(mediaFilter);
		Arrays.sort(mediaFiles);
		// 当前要展现的文件集合
		List<File> fileList = new ArrayList<File>();
		if (dirFiles != null && dirFiles.length > 0) {
			for (File file : dirFiles) {
				fileList.add(file);
			}
		}
		if (mediaFiles != null && mediaFiles.length > 0) {
			for (File file : mediaFiles) {
				fileList.add(file);
			}
		}

		return fileList;
	}

	/** 获取手机上的 根目录
	 *  */
	private File getRootFile() {
		File file = Environment.getRootDirectory();
		if(CommonUtils.isSDCardAvailable()) {
			file = Environment.getExternalStorageDirectory();//获取存储卡目录
		}
		return file;
	}

	@Override
	public void onBackPressed() {
		if (currentFile.getParent() != null) {
			totalSelected.addAll(currentSelected);
			currentFile = new File(currentFile.getParent());
			showParentFolderView(currentFile);
		} else {
			backToScanView();
		}
	}

	private void backToScanView() {
		this.finish();
	}

	@Override
	public void onResume() {
	    super.onResume();
		//友盟统计 日志 add by perry 2012-10-23
	    //MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
	    super.onPause();
		//友盟统计 日志 add by perry 2012-10-23
	    //MobclickAgent.onPause(this);
	}
}
