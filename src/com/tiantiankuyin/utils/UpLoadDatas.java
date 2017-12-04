package com.tiantiankuyin.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPOutputStream;

import org.apache.http.HttpStatus;
import org.apache.http.conn.util.InetAddressUtils;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.database.Cursor;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.tiantiankuyin.bean.AudioInfo;
import com.google.gson.Gson;
import com.tiantiankuyin.TianlApp;

public class UpLoadDatas {

	private Context mContext;

	private static UpLoadDatas instance;

	private String userID;

	public static synchronized UpLoadDatas getInstance() {
		if (instance == null) {
			instance = new UpLoadDatas();
		}
		return instance;
	}

	private UpLoadDatas() {
		mContext = TianlApp.newInstance();
		userID = getUniqueID();
	}

	public void UpLoad() {
	/*	if (CommonUtils.isHasNetwork(mContext)&&check()) {
			List<AudioInfo> audioInfos = getAudioInfos();
//			Log.d("test", audioInfos.size() + "");
			Gson gson = new Gson();
			String jsonString = gson.toJson(audioInfos);
//			Log.d("test", "jsonString.length()" + jsonString.length());
			
			Map<String, String> parameters = new HashMap<String, String>();
			parameters.put("listinfo", jsonString);
			doPost("http://120.197.85.217:8088/listinfoput", parameters, "gbk");
		}*/
	}
	/*
	private boolean check() {
		boolean flags = false;
		String serviceUrl = "http://120.197.85.217:8088/userget?userid="
				+ userID;
		System.out.println(serviceUrl);
		try {
			HttpURLConnection conn = CommonUtils.getConnection(serviceUrl, mContext);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			conn.connect();
			int responseCode = conn.getResponseCode();
			if (responseCode == HttpStatus.SC_OK
					|| responseCode == HttpStatus.SC_PARTIAL_CONTENT) {
				InputStream is = conn.getInputStream();
				BufferedInputStream bis = new BufferedInputStream(is);
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int len = -1;
				while ((len = bis.read(buffer)) != -1) {
					bos.write(buffer, 0, len);
				}
				String result = new String(bos.toByteArray(), "GBK");
				if (result.equalsIgnoreCase("userdi1")) {
					flags = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flags;
	}*/

	private String doPost(String reqUrl, Map parameters, String recvEncoding) {
		HttpURLConnection url_con = null;
		String responseContent = null;
		try {
			StringBuffer params = new StringBuffer();
			for (Iterator iter = parameters.entrySet().iterator(); iter
					.hasNext();) {
				Entry element = (Entry) iter.next();
				params.append(element.getKey().toString());
				params.append("=");
				params.append(element.getValue().toString());
				params.append("&");
			}

			if (params.length() > 0) {
				params = params.deleteCharAt(params.length() - 1);
			}
			String paramsStr = URLEncoder.encode(params.toString(),
					recvEncoding);
			url_con = CommonUtils.getConnection(reqUrl, mContext);
			url_con.setRequestMethod("POST");
			// url_con.setConnectTimeout(5000);//（单位：毫秒）jdk
			// 1.5换成这个,连接超时
			// url_con.setReadTimeout(5000);//（单位：毫秒）jdk 1.5换成这个,读操作超时
			url_con.setDoOutput(true);
			byte[] b = paramsStr.toString().getBytes();
			// url_con.getOutputStream().write(b, 0, b.length);
			// url_con.getOutputStream().flush();
			// url_con.getOutputStream().close();
			GZIPOutputStream gos = new GZIPOutputStream(
					url_con.getOutputStream());
			gos.write(b);
			gos.flush();
			gos.close();

			InputStream in = url_con.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(in,
					recvEncoding));
			String tempLine = rd.readLine();
			StringBuffer tempStr = new StringBuffer();
			String crlf = System.getProperty("line.separator");
			while (tempLine != null) {
				tempStr.append(tempLine);
				tempStr.append(crlf);
				tempLine = rd.readLine();
			}
			responseContent = tempStr.toString();
			rd.close();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (url_con != null) {
				url_con.disconnect();
			}
		}
		return responseContent;
	}

	/**
	 * 获取所需要的AudioInfos
	 * 
	 * @return
	 * @author Perry
	 */
	private List<AudioInfo> getAudioInfos() {
		ArrayList<AudioInfo> audioInfos = new ArrayList<AudioInfo>();

		String[] projection = new String[] { MediaStore.Audio.Media.DATA,
				MediaStore.Audio.Media.DISPLAY_NAME,
				MediaStore.Audio.Media.SIZE, MediaStore.Audio.Media.MIME_TYPE,
				MediaStore.Audio.Media.DATE_ADDED,
				MediaStore.Audio.Media.DATE_MODIFIED,
				MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DURATION,
				MediaStore.Audio.Media.IS_RINGTONE,
				MediaStore.Audio.Media.IS_MUSIC,
				MediaStore.Audio.Media.IS_ALARM,
				MediaStore.Audio.Media.IS_NOTIFICATION,
				MediaStore.Audio.Media.IS_PODCAST,
				MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM };
		String selection = null;
		String[] selectionArgs = null;

		String SDCardPath = getExternalStoragePath();
		selection = MediaStore.Audio.AudioColumns.DATA + " LIKE ?";
		selectionArgs = new String[] { SDCardPath + "%" };

		Cursor cursor = mContext.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection,
				selection, selectionArgs, MediaStore.Audio.Media.DATE_ADDED);

		if (cursor == null) {
			return audioInfos;
		}

		final int indexData = cursor
				.getColumnIndex(MediaStore.Audio.AudioColumns.DATA);
		final int indexDisplay_Name = cursor
				.getColumnIndex(MediaStore.Audio.AudioColumns.DISPLAY_NAME);
		final int indexSize = cursor
				.getColumnIndex(MediaStore.Audio.AudioColumns.SIZE);
		final int indexMimeType = cursor
				.getColumnIndex(MediaStore.Audio.AudioColumns.MIME_TYPE);
		final int indexDateAdded = cursor
				.getColumnIndex(MediaStore.Audio.AudioColumns.DATE_ADDED);
		final int indexDateModified = cursor
				.getColumnIndex(MediaStore.Audio.AudioColumns.DATE_MODIFIED);
		final int indexTitle = cursor
				.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE);
		final int indexDuration = cursor
				.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION);
		final int indexIsRingtone = cursor
				.getColumnIndex(MediaStore.Audio.AudioColumns.IS_RINGTONE);
		final int indexIsMusic = cursor
				.getColumnIndex(MediaStore.Audio.AudioColumns.IS_MUSIC);
		final int indexIsAlarm = cursor
				.getColumnIndex(MediaStore.Audio.AudioColumns.IS_ALARM);
		final int indexIsNotification = cursor
				.getColumnIndex(MediaStore.Audio.AudioColumns.IS_NOTIFICATION);
		final int indexIsPodcast = cursor
				.getColumnIndex(MediaStore.Audio.AudioColumns.IS_PODCAST);
		final int indexArtist = cursor
				.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST);
		final int indexAlbum = cursor
				.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM);

		String appId = getLocalIpAddressIPV4();

		while (cursor.moveToNext()) {
			try {
				AudioInfo audioInfo = new AudioInfo();
				audioInfo.setUserid(userID);
				audioInfo.setAppip(appId);
				audioInfo.setDate(cursor.getString(indexData));
				audioInfo.setDiplayName(cursor.getString(indexDisplay_Name));
				audioInfo.setSize(Integer.valueOf(cursor.getString(indexSize)));
				audioInfo.setMimeType(cursor.getString(indexMimeType));
				audioInfo.setDateAdded(Long.valueOf(cursor
						.getString(indexDateAdded)));
				audioInfo.setDateModified(Long.valueOf(cursor
						.getString(indexDateModified)));
				audioInfo.setTitle(cursor.getString(indexTitle));
				audioInfo.setDuration(Integer.valueOf(cursor
						.getString(indexDuration)));
				audioInfo.setIsRingtone(Integer.valueOf(cursor
						.getString(indexIsRingtone)));
				audioInfo.setIsMusic(Integer.valueOf(cursor
						.getString(indexIsMusic)));
				audioInfo.setIsAlarm(Integer.valueOf(cursor
						.getString(indexIsAlarm)));
				audioInfo.setIsNotification(Integer.valueOf(cursor
						.getString(indexIsNotification)));
				audioInfo.setIsPodcast(Integer.valueOf(cursor
						.getString(indexIsPodcast)));
				audioInfo.setArtist(cursor.getString(indexArtist));
				audioInfo.setAlbum(cursor.getString(indexAlbum));
				audioInfos.add(audioInfo);
			} catch (Exception e) {
			}
		}
		cursor.close();

		return audioInfos;
	}

	/**
	 * 获取当前状态的本机IP
	 * 
	 * @return
	 * @author Perry
	 */
	private String getLocalIpAddressIPV4() {
		try {
			String ipv4;

			ArrayList<NetworkInterface> mylist = Collections
					.list(NetworkInterface.getNetworkInterfaces());

			for (NetworkInterface ni : mylist) {

				ArrayList<InetAddress> ialist = Collections.list(ni
						.getInetAddresses());
				for (InetAddress address : ialist) {
					if (!address.isLoopbackAddress()
							&& InetAddressUtils.isIPv4Address(ipv4 = address
									.getHostAddress())) {
						return ipv4;
					}
				}

			}

		} catch (SocketException ex) {

		}
		return null;
	}

	/**
	 * 获取本机唯一标识
	 * 
	 * @return
	 * @author Perry
	 */
	private String getUniqueID() {
		TelephonyManager TelephonyMgr = (TelephonyManager) mContext
				.getSystemService(mContext.TELEPHONY_SERVICE);
		String m_szImei = TelephonyMgr.getDeviceId(); // Requires
														// READ_PHONE_STATE

		String m_szDevIDShort = "35"
				+ // we make this look like a valid IMEI
				Build.BOARD.length() % 10 + Build.BRAND.length() % 10
				+ Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10
				+ Build.DISPLAY.length() % 10 + Build.HOST.length() % 10
				+ Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10
				+ Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10
				+ Build.TAGS.length() % 10 + Build.TYPE.length() % 10
				+ Build.USER.length() % 10; // 13 digits

		String m_szAndroidID = Secure.getString(mContext.getContentResolver(),
				Secure.ANDROID_ID);

		WifiManager wm = (WifiManager) mContext
				.getSystemService(Context.WIFI_SERVICE);
		String m_szWLANMAC = wm.getConnectionInfo().getMacAddress();

		String m_szBTMAC = "";
		try {
			BluetoothAdapter m_BluetoothAdapter = null; // Local Bluetooth
														// adapter
			m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
			m_szBTMAC = m_BluetoothAdapter.getAddress();
		} catch (Exception e) {
		}

		String m_szLongID = m_szImei + m_szDevIDShort + m_szAndroidID
				+ m_szWLANMAC + m_szBTMAC;
		// compute md5
		MessageDigest m = null;
		try {
			m = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		m.update(m_szLongID.getBytes(), 0, m_szLongID.length());
		// get md5 bytes
		byte p_md5Data[] = m.digest();
		// create a hex string
		String m_szUniqueID = new String();
		for (int i = 0; i < p_md5Data.length; i++) {
			int b = (0xFF & p_md5Data[i]);
			// if it is a single digit, make sure it have 0 in front (proper
			// padding)
			if (b <= 0xF)
				m_szUniqueID += "0";
			// add number to string
			m_szUniqueID += Integer.toHexString(b);
		} // hex string to uppercase
		m_szUniqueID = m_szUniqueID.toUpperCase();
		return m_szUniqueID;
	}

	/**
	 * 获取外部存储的路径。
	 * 
	 * @return
	 * @author Perry
	 */
	private String getExternalStoragePath() {
		File SDCardFile = Environment.getExternalStorageDirectory();
		if (SDCardFile.getParentFile() != null) {
			return SDCardFile.getParentFile().getAbsolutePath();
		} else {
			return SDCardFile.getAbsolutePath();
		}
	}
}
