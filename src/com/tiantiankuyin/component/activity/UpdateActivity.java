package com.tiantiankuyin.component.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpStatus;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.tiantiankuyin.bean.Update;
import com.tiantiankuyin.notification.UpdateNotification;
import com.tiantiankuyin.para.Constant;
import com.tiantiankuyin.para.UserData;
import com.tiantiankuyin.utils.CommonUtils;
import com.tiantiankuyin.R;
import com.tiantiankuyin.TianlApp;

public class UpdateActivity extends Activity implements OnClickListener {

	public static boolean isUpdate; // 是否在更新
	
	public static final String UPDATE_BEAN = "UPDATE_BEAN";
	private TextView describe;
	private Button doUpdate;
	private Button cancleUpdate;
	private Update updateBean;
	private Handler mHandler;
	private CheckBox isNotShowAgain;
	public static final int UPGRESS = 0x1;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.update);
		Window dialogWindow = getWindow();
		WindowManager m = getWindowManager();
		Display d = m.getDefaultDisplay();
		WindowManager.LayoutParams p = dialogWindow.getAttributes();
		p.width = (int) (d.getWidth() * 0.8);
		dialogWindow.setAttributes(p);

		Intent intent = getIntent();
		updateBean = (Update) intent.getExtras().getSerializable(UPDATE_BEAN);
		describe = (TextView) findViewById(R.id.describe);
		describe.setText(updateBean.getDescribe());
		doUpdate = (Button) findViewById(R.id.doUpdate);
		cancleUpdate = (Button) findViewById(R.id.updateCancle);
		isNotShowAgain = (CheckBox) findViewById(R.id.isNotShowAgain);
		if(updateBean.isShowMsgAgain()){
			isNotShowAgain.setVisibility(View.GONE);
			isNotShowAgain.setChecked(false);
		}
		
		doUpdate.setOnClickListener(this);
		cancleUpdate.setOnClickListener(this);
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case UPGRESS:
					UpdateNotification.getInstence().updateNotify(msg.arg1, msg.arg2);
					break;
				}
			}
		};
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.doUpdate:
			finish();
			new Thread() {
				public void run() {
					if(isDownloadedUpdateAPK(updateBean.getVersion())){//当前已经下载最新包
						install(updateBean);
					}else {//当前未下载最新包，启动下载，并安装。
						download();
					}
				};
			}.start();
			break;
		case R.id.updateCancle:
			if(isNotShowAgain.isChecked()){
				UserData.getInstence().setNotShowUpdateMsg();
			}
			finish();
			break;
		}
	}
	
	/**
	 * 检测当前版本的apk是否存在。
	 * @param version
	 * @return
	 * @author Perry
	 */
	public static boolean isDownloadedUpdateAPK(String version){
		File file = new File(Constant.SdcardPath.UPDATE_APK_SAVEPATH + "/EasouMusic"
				+ version + ".apk");
		return file.exists();
	}

	/**
	 * 静默下载升级包
	 * @param updateBean
	 * @author Perry
	 */
	public static void downloadUpdateAPK(Update updateBean){
		if (updateBean == null || updateBean.getUrl() == null
				|| updateBean.getUrl().length() <= 0)
			return;
		try {
			isUpdate = true;
			URL url = new URL(updateBean.getUrl());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000);
			conn.setReadTimeout(10000);
			conn.connect();
			int responseCode = conn.getResponseCode();
			if (responseCode == HttpStatus.SC_OK
					|| responseCode == HttpStatus.SC_PARTIAL_CONTENT) {
				String contentRange = conn.getHeaderField("Content-Range"); // 用于提取文件大小
				String contentLength = conn.getHeaderField("Content-Length");// 如果Content-Range不存在时
				long fileTotalSize = CommonUtils.getFileTotalSize(contentRange,
						contentLength);
				File dir = new File(Constant.SdcardPath.UPDATE_APK_SAVEPATH);
				if (!dir.exists())
					dir.mkdir();
				File tempFile = new File(Constant.SdcardPath.UPDATE_APK_SAVEPATH + "/EasouMusic"
						+ updateBean.getVersion() + "_temp.apk");
				//在通知栏显示信息
				UpdateNotification.getInstence().setup("EasouMusic" + updateBean.getVersion()
						+ ".apk");
				if (tempFile.exists())
					tempFile.delete();
				FileOutputStream fos = new FileOutputStream(tempFile);
				InputStream inputStream = conn.getInputStream();
				int len = -1;
				long current = 0;
				byte[] bytes = new byte[1024];
				while ((len = inputStream.read(bytes)) != -1) {
					fos.write(bytes, 0, len);
					current += len;
				}
				fos.flush();
				fos.close();
				inputStream.close();
				if (current == fileTotalSize){
					File file = new File(Constant.SdcardPath.UPDATE_APK_SAVEPATH + "/EasouMusic"
							+ updateBean.getVersion() + ".apk");
					tempFile.renameTo(file);
				}
			}
		} catch (Exception e) {
		} finally {
			isUpdate = false;
		}
	}
	
	/**
	 * 升级包
	 * 
	 * @param bean
	 */
	public void download() {
		if (updateBean == null || updateBean.getUrl() == null
				|| updateBean.getUrl().length() <= 0)
			return;
		try {
			isUpdate = true;
			URL url = new URL(updateBean.getUrl());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000);
			conn.setReadTimeout(10000);
			conn.connect();
			int responseCode = conn.getResponseCode();
			if (responseCode == HttpStatus.SC_OK
					|| responseCode == HttpStatus.SC_PARTIAL_CONTENT) {
				String contentRange = conn.getHeaderField("Content-Range"); // 用于提取文件大小
				String contentLength = conn.getHeaderField("Content-Length");// 如果Content-Range不存在时
				long fileTotalSize = CommonUtils.getFileTotalSize(contentRange,
						contentLength);
				File dir = new File(Constant.SdcardPath.UPDATE_APK_SAVEPATH);
				if (!dir.exists())
					dir.mkdir();
				File file = new File(Constant.SdcardPath.UPDATE_APK_SAVEPATH + "/EasouMusic"
						+ updateBean.getVersion() + ".apk");
				//在通知栏显示信息
				UpdateNotification.getInstence().setup("EasouMusic" + updateBean.getVersion()
						+ ".apk");
				if (file.exists())
					file.delete();
				FileOutputStream fos = new FileOutputStream(file);
				InputStream inputStream = conn.getInputStream();
				int len = -1;
				long current = 0;
				byte[] bytes = new byte[1024];
				while ((len = inputStream.read(bytes)) != -1) {
					fos.write(bytes, 0, len);
					current += len;
				}
				if (current == fileTotalSize)
					install(updateBean);
			}
		} catch (Exception e) {
			// e.printStackTrace();
		} finally {
			isUpdate = false;
			UpdateNotification.getInstence().cancel();
		}
	}

	/**
	 * 引导安装
	 * 
	 */
	public void install(Update bean) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(
				Uri.parse("file://" + Constant.SdcardPath.UPDATE_APK_SAVEPATH + "/EasouMusic"
						+ bean.getVersion() + ".apk"),
				"application/vnd.android.package-archive");
		TianlApp.newInstance().startActivity(intent);
	}





	@Override
	public void onResume() {
		super.onResume();
		// 友盟统计 日志 add by perry 2012-10-23
		//MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		// 友盟统计 日志 add by perry 2012-10-23
		//MobclickAgent.onPause(this);
	}
}
