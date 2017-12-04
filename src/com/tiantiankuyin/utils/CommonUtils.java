package com.tiantiankuyin.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tiantiankuyin.bean.Update;
import com.tiantiankuyin.database.bll.OnDataPreparedListener;
import com.tiantiankuyin.database.bll.OnlineMusicManager;
import com.tiantiankuyin.net.EasouAsyncImageLoader;
import com.tiantiankuyin.net.EasouAsyncImageLoader.ILoadedImage;
import com.tiantiankuyin.para.Constant;
import com.tiantiankuyin.para.Env;
import com.tiantiankuyin.para.SPHelper;
import com.tiantiankuyin.para.UserData;
import com.tiantiankuyin.para.WebServiceUrl;
import com.tiantiankuyin.R;
import com.tiantiankuyin.TianlApp;
 

/**
 * 公共工具类
 * 
 * @author DK
 * 
 */
public class CommonUtils {
	private static final String TAG = "CommonUtils";

	/**
	 * 判断当前是否有网络
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isHasNetwork(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm == null)
			return false;
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		if (networkInfo == null || !networkInfo.isAvailable()) {
			return false;
		}
		return true;
	}
	
	/**
	 * 判断当前网络环境是否为wifi
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isWifiConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (ni.getState() == NetworkInfo.State.CONNECTED) {
			return true;
		}
		return false;
	}

	/**
	 * 判断当前网络环境是否为wap
	 * 
	 * @param context
	 * @return
	 */
	/*public static boolean isWapConnected(Context context) {
		if (isWifiConnected(context)) {
			return false;
		}
		Uri contentUri = Uri.parse("content://telephony/carriers/preferapn");
		Cursor cursor = null;
		ContentResolver resolver = context.getContentResolver();
		cursor = resolver.query(contentUri, new String[] { "proxy", "port" },
				null, null, null);
		if (cursor != null) {
			String proxy = null;
			int port = 0;
			if (cursor.moveToFirst()) {
				proxy = cursor.getString(0);
				String strPort = cursor.getString(1);
				if (strPort != null && strPort.length() > 0)
					port = Integer.valueOf(strPort);
			}
			if (!TextUtils.isEmpty(proxy)) {
				if (port <= 0) {
					port = 80;
				}
				UserData.getInstence().setProxyHost(proxy);
				UserData.getInstence().setProxyPort(port);
				return true;
			}
		}
		return false;
	}*/

	/**
	 * 生成32位MD5
	 * 
	 * @param content
	 * @return
	 */
	public static String MD5(String content) {
		String result = null;
		if(content != null){
			try {
				MessageDigest digest = MessageDigest.getInstance("MD5");
				digest.update(content.getBytes());
				byte buffer[] = digest.digest();
				int i = 0;
				StringBuffer buf = new StringBuffer();
				for (int offset = 0; offset < buffer.length; offset++) {
					i = buffer[offset];
					if (i < 0)
						i += 256;
					if (i < 16)
						buf.append("0");
					buf.append(Integer.toHexString(i));
				}
				result = buf.toString().toUpperCase();
			} catch (NoSuchAlgorithmException e) {
				//e.printStackTrace();
			}
		}	
		return result;
	}

	/**
	 * 检测SD卡是否可用
	 * 
	 * @return
	 */
	public static boolean isExternalStorageAvailable() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED))
			return true;
		return false;
	}

	/**
	 * 手机内存的可用空间大小
	 * 
	 * @return
	 */
	public static long getAvailableInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return availableBlocks * blockSize;
	}

	/**
	 * 手机内存的总空间大小
	 * 
	 * @return
	 */
	public static long getTotalInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return totalBlocks * blockSize;
	}

	/**
	 * sdcard的可用空间大小
	 * 
	 * @return
	 */
	public static long getAvailableExternalMemorySize() {
		if (isExternalStorageAvailable()) {
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long availableBlocks = stat.getAvailableBlocks();
			return availableBlocks * blockSize;
		}
		return 0;
	}

	/**
	 * 获取sdcard的总空间大小
	 * 
	 * @return
	 */
	public static long getTotalExternalMemorySize() {
		if (isExternalStorageAvailable()) {
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long totalBlocks = stat.getBlockCount();
			return totalBlocks * blockSize;
		}
		return 0;
	}

	/**
	 * 根据链接获取文件名
	 * 
	 * @param url
	 * @return 文件名
	 */
	public static String getFileNameByUrl(String url) {
		if (url == null || (url = url.trim()).length() <= 0) {
			return null;
		}
		int pos = url.indexOf("?");
		if (pos > 0) {
			url = url.substring(0, pos);
		}
		if (url.endsWith("/") && url.length() > 2) {
			url = url.substring(0, url.length() - 1);
		}
		pos = url.lastIndexOf("/");
		if (pos >= 0) {
			return url.substring(pos + 1);
		}
		return null;
	}

	/**
	 * 根据文件名获取文件后缀
	 * 
	 * @param fileName
	 *            文件名
	 * @return 文件后缀
	 */
	public static String getSuffixByFileName(String fileName) {
		if (fileName == null || (fileName = fileName.trim()).length() <= 0) {
			return null;
		}
		int pos = fileName.lastIndexOf(".");
		if (pos >= 0 && fileName.length() - 1 > pos) {
			return fileName.substring(pos);
		}
		return null;
	}

	/**
	 * 根据链接返回文件名后缀
	 * 
	 * @param url
	 * @return
	 */
	public static String getSuffixByUrl(String url) {
		String fileName = getFileNameByUrl(url);
		return getSuffixByFileName(fileName);
	}

	/**
	 * 判断字符串中，乱码是否占总字符串长度的60%以上。
	 * 
	 * @param strName
	 *            需要判断的字符串
	 * @return boolean 当乱码的长度 大于总长度的40%时，则返回true，否则返回false。
	 * 
	 * @author Perry
	 */
	public static boolean isMessyCode(String strName) {
		Pattern p = Pattern.compile("\\s*|\t*|\r*|\n*");
		Matcher m = p.matcher(strName);
		String after = m.replaceAll("");
		char[] ch = after.trim().toCharArray();
		float chLength = ch.length;
		float count = 0;
		for (int i = 0; i < ch.length; i++) {
			char c = ch[i];
			if (!Character.isDigit(c)) {
				if (!isChineseOrLetter(c)) {
					count = count + 1;
				}
			}
		}
		float result = count / chLength;
		// 当乱码的长度 大于总长度的40%时，则返回true，否则返回false。
		if (result > 0.4) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * 把热词保存到本地
	 */
	public static void saveLenvonKeyToLocal(String key){
		String oldLenov=SPHelper.newInstance().getLenovKey();
		if(oldLenov!=null&&oldLenov.length()>0){
			//去重
/*			if(oldLenov.contains(key)){//如果以前已经包含了这个搜索词了
				int index=oldLenov.indexOf(key);
				StringBuffer sb=new StringBuffer(oldLenov);
				if((index+ key.length())<=sb.length()){
					if(index>0){
						index=index-1;
					}
					sb.delete(index, index+ key.length());
				}
				oldLenov=sb.toString();
			}*/
			LinkedList<String> lenvons = new LinkedList<String>();
			String[] oldlenovs=oldLenov.split("_");
			if(oldlenovs.length > 0){
				lenvons = new LinkedList<String>(Arrays.asList(oldlenovs));
				if(lenvons.contains(key)){
					lenvons.remove(key);
				}
			}
			if(oldlenovs.length<15){//未到15个
				lenvons.addLast(key);
			}else{//等于15个
				lenvons.removeFirst();//把第一个去掉
				lenvons.addLast(key);//增加最后一个
			}

			StringBuffer sb=new StringBuffer();
			for(String s:lenvons){
				sb.append(s).append("_");
			}
			sb.delete(sb.length()-1, sb.length());
			SPHelper.newInstance().saveLenovKey(sb.toString());
		}else{//为空 第一次保存
			SPHelper.newInstance().saveLenovKey(key);
		}
	}
	/**
	 * 判断是否为中文字或英文字母。
	 * 
	 * @param c
	 * 
	 * @return 是则返回true 不是则返回false
	 * 
	 * @author Perry
	 */
	public static boolean isChineseOrLetter(char c) {
		int charCode = (int) c;

		if (c == '！' || c == '￥' || c == '…' && c == '（' || c == '）'
				|| c == '—' || c == '、' || c == '；' || c == '：' || c == '？'
				|| c == '。' || c == '》' || c == '《' || c == '，') {
			return false;
		}
		// ａ~ｚ 双字节字母
		if (charCode >= 65345 && charCode <= 65370) {
			return false;
		} else {
			Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
			if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
					|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
					|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
					|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
					|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
					|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
				return true;
			} else if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
				return true;
			}
		}
		return false;
	}

	// 时间格式化
	public static String timeFormate(int seconds) {
		if (seconds <= 0) {
			return "00:00";
		}
		seconds = seconds / 1000;
		int hour = seconds / 3600;
		seconds = seconds % 3600;
		int minute = seconds / 60;
		seconds = seconds % 60;
		if (hour <= 0) {
			return intToStrFormate(minute) + ":" + intToStrFormate(seconds);
		} else {
			return intToStrFormate(hour) + ":" + intToStrFormate(minute) + ":"
					+ intToStrFormate(seconds);
		}
	}

	/**
	 * 时间字符串格式化为毫秒
	 * 
	 * @author Erica
	 * */
	public static long timeStringTosecond(String _time) {
		if (_time.indexOf(":") == -1) {
			return 0;
		}
		String fen = _time.substring(0, _time.indexOf(":"));
		int fen_s = Integer.parseInt(fen);
		String sec = _time.substring(_time.indexOf(":") + 1, _time.length());
		int sec_s = Integer.parseInt(sec);
		long millisecond = (fen_s * 60 + sec_s) * 1000;

		return millisecond;

	}

	// 对时间进行处理.返回都 以SS形式.位数不够用0填充

	private static String intToStrFormate(int time) {
		if (time >= 0 && time <= 9) {
			return "0" + time;
		} else {
			return time + "";

		}
	}



	/**
	 * 获取屏幕分辨率
	 * 
	 * @author Erica
	 * */
	public static int[] readWindowMetrics(WindowManager _windowManager) {
		WindowManager manager = _windowManager;
		int hight = manager.getDefaultDisplay().getHeight();
		// 屏幕的高度
		int width = manager.getDefaultDisplay().getWidth();
		int screen_wh[] = new int[2];
		screen_wh[0] = hight;
		screen_wh[1] = width;
		return screen_wh;
	}

	/**
	 * dip2px
	 * 
	 * @param context
	 * @param dipValue
	 * @return
	 */
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * 判断一个字符串的首字母是否是中文
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isChinaLetter(String str) {
		String regEx = "[\\u4e00-\\u9fa5]";
		Pattern p = Pattern.compile(regEx);
		char c = str.charAt(0);
		str = new String(new char[] { c });
		Matcher m = p.matcher(str);
		if (m.find()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 设置页面标题
	 * 
	 * @param rootView
	 *            布局文件
	 * @param titleName
	 *            要设置的标题名
	 * @param hasBackBtn
	 *            是否需要返回按钮
	 * @param hasEditBtn
	 *            是否需要编辑按钮
	 */
	public static void setTitle(View rootView, String titleName,
			boolean hasBackBtn, boolean hasEditBtn) {
		TextView titleNameTV = (TextView) rootView
				.findViewById(R.id.title_text);
		ImageButton backBtn = (ImageButton) rootView
				.findViewById(R.id.back_btn);
		ImageButton editBtn = (ImageButton) rootView
				.findViewById(R.id.edit_btn);
		backBtn.setVisibility(hasBackBtn ? View.VISIBLE : View.INVISIBLE);
		editBtn.setVisibility(hasEditBtn ? View.VISIBLE : View.INVISIBLE);
		titleNameTV.setText(titleName);
	}

	// 获取imei串码信息
	public static String getIMEI(Context context) {
		String imei = ((TelephonyManager) context
				.getSystemService(context.TELEPHONY_SERVICE)).getDeviceId();
		return imei;
	}

	/**
	 * 获取歌手小图片用于歌手ListView
	 * 
	 * @param singerName
	 * @param iLoadedImage
	 */
	public static void getSingerImageDrawable(final String singerName,
			final ILoadedImage iLoadedImage) {
		if (singerName == null || "<unknown>".equals(singerName))
			return;
		String url = null;
		StringBuilder sb = new StringBuilder(WebServiceUrl.EASOU_SERVER + "aai.e?");
		sb.append("type=sp").append("&singer=")
				.append(URLEncoder.encode(singerName)).append("&ver=4").append("&vs=2")
				.append("&esid=").append("&im=")
				.append(CommonUtils.getIMEI(TianlApp.newInstance()))
				.append("&encrypt=ENCRYPT_1_PARAMS_")
				.append("&cid=").append(Env.getChannel());
		url = sb.toString();
		
		OnlineMusicManager.getInstence().getSongUrlData(TianlApp.newInstance(),
				new OnDataPreparedListener<String>() {
					@Override
					public void onDataPrepared(String data) {
						if (data != null) {	
							String json = data;
							String resultURL = null;
							//looming 2012-10-18 11:16:19
							if(json==null || json.length()==0 || json.indexOf("url_s") == -1){
								Lg.e(TAG,"getSingerImageDrawable() -> 从网络请求返回值中，未能获取到歌手小图片的URL");
								return;
							}
							
							try {
								JSONObject jsonObject = new JSONObject(json);
								resultURL = jsonObject.getString("url_s");
								EasouAsyncImageLoader.newInstance().loadImage(resultURL,
										iLoadedImage, singerName);
							} catch (JSONException e) {
								//e.printStackTrace();
							}
							
						}else {
							Lg.d("getresultURL() == null");
							return;
						}
					}
				}, url,false);
	}

	public static void getAlbumImageDrawable(final String albumName,
			final ILoadedImage iLoadedImage) {
		if (albumName == null)
			return;
		String url = null;
		StringBuilder sb = new StringBuilder(WebServiceUrl.EASOU_SERVER + "aai.e?");
		sb.append("type=cp").append("&query=")
				.append(URLEncoder.encode(albumName)).append("&ver=4").append("&vs=2")
				.append("&encrypt=ENCRYPT_1_PARAMS_")
				.append("&esid=").append("&im=")				
				.append(CommonUtils.getIMEI(TianlApp.newInstance()))
				.append("&cid=").append(Env.getChannel());
		url = sb.toString();
		OnlineMusicManager.getInstence().getSongUrlData(TianlApp.newInstance(),
				new OnDataPreparedListener<String>() {
					@Override
					public void onDataPrepared(String data) {
						if (data != null) {	
							String json = data;
							String resultURL = null;
							//looming 2012-10-18 11:16:19
							if(json==null || json.length()==0 || json.indexOf("url_s") == -1){
								Lg.e(TAG,"getAlbumImageDrawable() -> 从网络请求返回值中，未能获取到Album小图片的URL");
								return;
							}
							
							try {
								JSONObject jsonObject = new JSONObject(json);
								resultURL = jsonObject.getString("url_s");
								EasouAsyncImageLoader.newInstance().loadImage(resultURL,
										iLoadedImage, albumName);
							} catch (JSONException e) {
								//e.printStackTrace();
							}
							
						}else {
							Lg.d("getresultURL() == null");
							return;
						}
					}
				}, url,false);
	}

	/**
	 * 返回请求在线联想词的链接
	 * 
	 * @param s
	 * @return
	 */
	public static String getOlLenvoWordRequestURL(String queryStr) {
		String esid = null;
		String imei = null;
		String str = null;
		try {
			str = URLEncoder.encode(queryStr, "utf-8");
		} catch (UnsupportedEncodingException e) {
			//e.printStackTrace();
		}
		String url = WebServiceUrl.EASOU_SERVER + "aai.e?q=" + str
				+ "&size=15&type=rela&ver=4&vs=2&esid=" + esid + "&im=" + imei
				+ "&cid=" + Env.getChannel() + "&encrypt=ENCRYPT_1_PARAMS_";
		return url;
	}

	

	/**
	 * 返回搜索请求链接，适用于搜索页面、歌手列表对应的歌曲页面
	 * 
	 * @param type
	 *            Contants.OL_QUERY_BY_SONGNAME 按歌曲搜 Contants.OL_QUERY_BY_ARTIST
	 *            按歌手搜
	 * @param queryStr
	 *            搜索的关键字
	 * @param pageNum
	 *            页码
	 * @return
	 */
	public static String getOlQueryRequestURL(String type, String queryStr,
			int pageNum) {
		String str = null;
		if (queryStr == null) {
			return null;
		}
		try {
			str = URLEncoder.encode(queryStr, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		String esid = null;
		String imei = null;
		// http://service.mp3.easou.com:82/aai.e?type=sg&q=beyond&size=10&page=1&ver=4&esid=&im=&cid=&ma=&encrypt=ENCRYPT_1_PARAMS_
		String url =  WebServiceUrl.EASOU_SERVER + "aai.e?type="
				+ type
				+ "&q="
				+ str
				+ "&size=20&page="
				+ pageNum
				+ "&ver=4"
				+ "&esid="
				+ esid
				+ "&im="
				+ imei
				+ "&cid=" + Env.getChannel()
				+ "&vs=2&encrypt=ENCRYPT_1_PARAMS_&ma=&du=du";
		return url;
	}

	/**
	 * 返回请求搜索热词的链接
	 * 
	 * @param size
	 *            代表需要返回热词的数量
	 * @return
	 */
	public static String getOlHotKeyRequestURL(int size) {
		String esid = null;
		String imei = null;
//		http://service.mp3.easou.com:82/aai.e?type=hkey&size=10&ver=4&esid=&im=&cid=&vs=2&encrypt=ENCRYPT_1_PARAMS_
		String url = WebServiceUrl.EASOU_SERVER + "aai.e?type=hkey&size="
				+ size + "&ver=4&&vs=2&esid=" + esid + "&im=" + imei + "&cid="
				+ Env.getChannel() + "&encrypt=ENCRYPT_1_PARAMS_";
		return url;
	}
	
	/**
	 */
	public static String getOmnibusDataURL(int _size, int _page, int _ty)  {
		StringBuilder sb = new StringBuilder(WebServiceUrl.EASOU_SERVER+"aai.e?");
		sb.append("type=omidx").append("&size=").append(_size).append("&page=")
				.append(_page).append("&ty=").append(_ty).append("&ver=4")
				/* .append("&esid=") */.append("&im=")
				.append(CommonUtils.getIMEI(TianlApp.newInstance()))
				.append("&cid=").append(Env.getChannel())
				.append("&vs=2").append("&encrypt=ENCRYPT_1_PARAMS_");
		return sb.toString();
	}
	
	/**
	 */
	public static String getOmnibusTipDataURL(int _ty) {
		StringBuilder sb = new StringBuilder(WebServiceUrl.EASOU_SERVER+"aai.e?");
		sb.append("type=omtag").append("&ty=").append(_ty).append("&ver=4")
		/* .append("&esid=") */.append("&im=")
				.append(CommonUtils.getIMEI(TianlApp.newInstance()))
				.append("&cid=").append(Env.getChannel())
				.append("&vs=2").append("&encrypt=ENCRYPT_1_PARAMS_");
		return sb.toString();
	}
	
	/**
	 */
	public static String getOmnibusInfoURL(int _ty, int _page, int _size,
			boolean isDerail, int mSearch_Id) {
		StringBuilder sb = new StringBuilder(WebServiceUrl.EASOU_SERVER + "aai.e?");
		sb.append("type=ominfo").append("&id=").append(mSearch_Id)
				.append("&ty=").append(_ty).append("&page=").append(_page)
				.append("&size=").append(_size).append("&ver=4")
				/* .append("&esid=") */.append("&im=")
				.append(CommonUtils.getIMEI(TianlApp.newInstance()))
				.append("&cid=").append(Env.getChannel())
				.append("&vs=2").append("&du=du").append("&encrypt=ENCRYPT_1_PARAMS_");
		if (isDerail) {
			sb.append("&derail=derail");
		}
		if (Env.isAACCompatiblity()) {
			sb.append("&ma=aac");
		}
		return sb.toString();
	}
	
	/**
	 */
	public static String getSearchOmnibusDataURL(String _name, int _page, int _ty) {
		StringBuilder sb = new StringBuilder(WebServiceUrl.EASOU_SERVER+"aai.e?");
		try {
			sb.append("type=omsch").append("&q=")
					.append(URLEncoder.encode(_name, "UTF-8")).append("&page=")
					.append(_page).append("&size=").append(15).append("&ty=")
					.append(_ty).append("&ver=4")
					/* .append("&esid=") */.append("&im=")
					.append(CommonUtils.getIMEI(TianlApp.newInstance()))
					.append("&cid=")
					.append(Env.getChannel())
					.append("&vs=2").append("&encrypt=ENCRYPT_1_PARAMS_");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return sb.toString();
	}

	/**
	 */
	public static String getSingerDataURL(String _name, int _ty) {
		StringBuilder sb = new StringBuilder(WebServiceUrl.EASOU_SERVER+"aai.e?");
		try {
			sb.append("type=snginfo").append("&q=")
					.append(URLEncoder.encode(_name, "UTF-8")).append("&ty=")
					.append(_ty).append("&ver=4")
					/* .append("&esid=") */.append("&im=")
					.append(CommonUtils.getIMEI(TianlApp.newInstance()))
					.append("&cid=")
					.append(Env.getChannel())
					.append("&vs=2").append("&encrypt=ENCRYPT_1_PARAMS_");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return sb.toString();
	}
	
	/**
	 */
	public static String getSingerListDataURL(int _ty, int _page,int mSearch_Id) {
		StringBuilder sb = new StringBuilder(WebServiceUrl.EASOU_SERVER+"aai.e?");
		sb.append("type=sngsub").append("&id=").append(mSearch_Id)
		.append("&ty=").append(_ty).append("&page=").append(_page)
		.append("&size=").append(15).append("&ver=4")
		/* .append("&esid=") */.append("&im=")
		.append(CommonUtils.getIMEI(TianlApp.newInstance()))
		.append("&cid=").append(Env.getChannel())
		.append("&vs=2").append("&encrypt=ENCRYPT_1_PARAMS_");
		return sb.toString();
	}
	
	/**
	 */
	public static String getSearchSingerDetailDataURL(String _name, String _ty,int _page) {
		StringBuilder sb = new StringBuilder(WebServiceUrl.EASOU_SERVER+"aai.e?");
		try {
			sb.append("type=sg").append("&q=")
					.append(URLEncoder.encode(_name, "UTF-8")).append("&size=")
					.append(20).append("&page=").append(_page).append("&ty=")
					.append(_ty).append("&ver=4")
					/* .append("&esid=") */.append("&im=")
					.append(CommonUtils.getIMEI(TianlApp.newInstance()))
					.append("&cid=")
					.append(Env.getChannel())
					.append("&vs=2").append("&du=du").append("&encrypt=ENCRYPT_1_PARAMS_");
			if(Env.isAACCompatiblity()){
				sb.append("&ma=aac");
			}
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return sb.toString();
	}
	
	/**
	 * 动态设置listView高度
	 * 
	 * @author Erica
	 * */
	public static void setListViewHeightBasedOnChildren(ListView listView,
			boolean isShowFooter,boolean isShowHeader) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		int totalHeight = 0;
		int size = 0;
		if (!isShowFooter) {
			size = listView.getCount() - 1;
		} else {
			if(isShowHeader)
			 size = listView.getCount()+2;
			else
			 size = listView.getCount();
		}
		for (int i = 0; i < size; i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}

	/**
	 * 获取相似歌曲的链接
	 * 
	 * @param type
	 * @param queryStr
	 * @param pageNum
	 * @return
	 */
	public static String getSimilarOlQueryRequestURL(String queryStr,
			int pageNum) {
		if (queryStr == null) {
			return null;
		}
		String str = null;
		try {
			str = URLEncoder.encode(queryStr, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		String esid = null;
		String imei = null;

		String url =  WebServiceUrl.EASOU_SERVER+"aai.e?type=sim" + "&q=" + str
				+ "&size=5&ver=4" + "&esid=" + esid + "&im=" + imei + "&cid="
				+ Env.getChannel() + "&vs=2&encrypt=ENCRYPT_1_PARAMS_&ma=&du=du";
		return url;
	}

	/**
	 * 歌曲所属精选集
	 * 
	 * @param queryStr
	 * @param pageNum
	 * @return
	 */
	public static String getSongBelongToSelect(long songId, int size,
			int picSize, int pageNum) {
		// http://service.mp3.easou.com:82/aai.e?type=omsong&id=277052&size=5&ty=120&page=5&ver=4&esid=&im=&cid=&vs=2&encrypt=ENCRYPT_1_PARAMS_
		String url = WebServiceUrl.EASOU_SERVER+"aai.e?type=omsong&id="
				+ songId + "&size=" + size + "&ty=" + picSize + "&page="
				+ pageNum
				+ "&ver=4&esid=&im=&cid="+Env.getChannel()+"&vs=2&encrypt=ENCRYPT_1_PARAMS_";
		return url;
	}

	public static String getCategoryUrl() {
		String url = WebServiceUrl.EASOU_SERVER+"aai.e?type=newcollist&ver=4&esid=&im=&cid="+Env.getChannel()+"&vs=2&encrypt=ENCRYPT_1_PARAMS_&ty=2,3,5,4";
		return url;
	}

	/**
	 * 返回某个精选集中歌曲列表的请求链接
	 * 
	 * @return
	 */
	public static String getOlAlbumSongRequetURL(int olAlbumId, int pageNum) { // 760109
																				// http://service.mp3.easou.com:82/aai.e?type=ominfo&page=2&id=760109&ty=100&ver=4&esid=&im=&cid=&ma=&vs=2&encrypt=ENCRYPT_1_PARAMS_
//		String esid = null;
//		String imei = null;
		String url = WebServiceUrl.EASOU_SERVER+"aai.e?type=ominfo&id="
				+ olAlbumId
				+ "&page="
				+ pageNum
				+ "&ty=100&ver=4&esid=&im=&cid="+Env.getChannel()+"&ma=&vs=2&encrypt=ENCRYPT_1_PARAMS_";
		return url;
	}

	/**
	 * 移动方法
	 * 
	 * @param v
	 *            需要移动的View
	 * @param startX
	 *            起始x坐标
	 * @param toX
	 *            终止x坐标
	 * @param startY
	 *            起始y坐标
	 * @param toY
	 *            终止y坐标
	 */
	public static void moveFrontBg(View v, int startX, int toX, int startY,
			int toY) {
		TranslateAnimation anim = new TranslateAnimation(startX, toX, startY,
				toY);
		anim.setDuration(200);
		anim.setFillAfter(true);
		v.startAnimation(anim);
	}

	/**
	 * 根据分类id，和页码来返回一个请求地址
	 * 
	 * @param typeId
	 * @param pageNum
	 * @return
	 */
	public static String getOlTypeChildRequestURL(int typeId, int pageNum) {
		String esid = null;
		String imei = null;
		String url = WebServiceUrl.EASOU_SERVER+"aai.e?type=col&id=" + typeId
				+ "&size=20&page=" + pageNum + "&ver=4&esid=" + esid + "&im="
				+ imei + "&cid=" +Env.getChannel() + "&ma="
				+ "&vs=2&encrypt=ENCRYPT_1_PARAMS_&du=du";
		return url;
	}

	// http://service.mp3.easou.com:82/aai.e?type=sidx&ty=sidx&ver=4&esid=&im=&cid=&vs=2
	public static String getHotSaleRequestUrl() {
		// http://service.mp3.easou.com:82/aai.e?type=sidx&ty=sidx&ver=4&esid=&im=&cid=&vs=2&encrypt=ENCRYPT_1_PARAMS_
		String url = WebServiceUrl.EASOU_SERVER+"aai.e?type=sidx&ty=sidx&ver=4&esid=&im=&cid="+Env.getChannel()+"&vs=2&encrypt=ENCRYPT_1_PARAMS_";
		return url;
	}

	/**
	 * 获取榜单下的歌曲列表
	 * 
	 * @return
	 */
	public static String getHotSaleDataRequestUrl(int typeId, int size,
			int pageNum) {
		// http://service.mp3.easou.com:82/aai.e?type=top&id=76&size=20&page=1&ver=4&esid=&im=&cid=&ma=&vs=2&encrypt=ENCRYPT_1_PARAMS_
		String url =WebServiceUrl.EASOU_SERVER+"aai.e?type=top&id=" + typeId
				+ "&size=" + size + "&page=" + pageNum
				+ "&ver=4&esid=&im=&cid="+Env.getChannel()+"&ma=&vs=2&encrypt=ENCRYPT_1_PARAMS_&du=du";
		return url;
	}

	public static String fileSizeFormate(long size) {
		if (size <= 0) {
			return "0kB";
		}
		float kb = (float) size / 1024;
		if (kb < 1024) {
			return floatToString(kb) + "KB";
		} else {
			kb = kb / 1024;
			return floatToString(kb) + "MB";
		}
	}


	/**
	 * 公用的toast
	 */
	private static Toast toast;
	
	/**
	 * 公用的Toast 方法
	 * @param context
	 * @param title
	 * @param time
	 */
	public static void showToast(Context context, int title, int time){
		if(toast == null){
			toast = Toast.makeText(context, title, time);
			toast.show();
		}else {
			toast.setText(title);
			toast.setDuration(time);
			toast.show();
		}
	}
	
	/**
	 * 公用的Toast 方法
	 * 
	 * @param context
	 * @param title
	 * @param time
	 */
	public static void showToast(Context context, String title, int time){
		if(toast == null){
			toast = Toast.makeText(context, title, time);
			toast.show();
		}else {
			toast.setText(title);
			toast.setDuration(time);
			toast.show();
		}
	}
	
	/**
	 * 当前SD卡是否可用
	 * @return
	 * 		true 则可用；false 不可以。
	 * @author Perry	
	 */
	public static boolean isSDCardAvailable() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED))
			return true;
		else
			return false;
	}
	
	/**
	 * 将float转成字符串，只保留最多两位小数的
	 * 
	 * @param size
	 * @return
	 */
	public static String floatToString(float size) {
		String str = "" + size;
		int index = str.indexOf(".");
		if (index > 0 && index + 3 < str.length()) {
			str = str.substring(0, index + 3);
		}
		return str;
	}

	/**
	 * 获取文件的总长度，断点续传时，总长度包含在Content-Ranges中，如Content-Ranges: bytes 0-499/1234
	 * 
	 * @return
	 */
	public static long getFileTotalSize(String contentRanges,
			String contentLength) {
		if (contentRanges != null) {
			int pos = contentRanges.lastIndexOf("/");
			if (pos > 0) {
				return Long.parseLong(contentRanges.substring(pos + 1));
			}
		} else if (contentLength != null) {
			return Long.parseLong(contentLength);
		}
		return 0;
	}

	/**
	 * 获取升级信息
	 * 
	 * @param bts
	 * @return
	 */
	public static Update buildXmlUploadInf(byte[] bts) {
		Update bean = null;
		try {
			XmlPullParser xmlParser = XmlPullParserFactory.newInstance()
					.newPullParser();
			// 解析器加载输入流
			ByteArrayInputStream bais = new ByteArrayInputStream(bts);
			xmlParser.setInput(bais, "UTF-8");
			int eventType = XmlPullParser.END_DOCUMENT;
			eventType = xmlParser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					bean = new Update();
					break;
				case XmlPullParser.START_TAG:
					if ("version".equalsIgnoreCase(xmlParser.getName())) {
						bean.setVersion(xmlParser.nextText());
					}
					if ("url".equalsIgnoreCase(xmlParser.getName())) {
						bean.setUrl(xmlParser.nextText());
					}
					if ("describe".equalsIgnoreCase(xmlParser.getName())) {
						bean.setDescribe(xmlParser.nextText().replace("\\n", "\n"));
					}
					if("showMsgAgain".equalsIgnoreCase(xmlParser.getName())){
						String showMsgAgain = xmlParser.nextText();
						if(showMsgAgain.length()>0 && showMsgAgain.equalsIgnoreCase("true")){
							bean.setShowMsgAgain(true);
						}else {
							bean.setShowMsgAgain(false);
						}
					}
					break;
				}
				eventType = xmlParser.next();
			}
			bais.close();
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return bean;
	}


	
	/** 计算字符串长度并适当截取
	 * @author Erica
	 * @param _dtr String 需要操作的字符串
	 * @param size int 需要处理文字的大小
	 * @param lin int 最大显示行数
	 * @param width int 最大显示宽度
	 *  */
	   public static String mestrStr(String _dtr,int size ,int lin,int width){
		   if(_dtr == null||_dtr.length() == 0)
			   return "";
//		   System.out.println(_dtr);
//		   width = 200;
		   Paint temp = new Paint();
		   temp.setTextSize(size);
		   String now_str;
		   float len = temp.measureText(_dtr);
		   if(width * lin <= len){
			   float f = len- (width * lin);
			   float f_t = f/len;
			   now_str = _dtr.substring(0, (int)(_dtr.length()*(1-f_t)));
			   now_str = now_str.substring(0, now_str.length()- 3);
			   now_str = now_str+"...";
		   }else{
			   now_str = _dtr;
		   }
//		   System.out.println(now_str);
		   return now_str;
	   }
	   
	public static String getDownloadUrl(String gid, String rid) {
		StringBuilder sb = new StringBuilder();
		sb.append(WebServiceUrl.EASOU_SERVER).append("aai.e?type=cdwn&id=")
				.append(gid).append("&rid=").append(rid)
				.append("&ver=4&esid=&im=&cid="+Env.getChannel()+"&vs=").append(Env.getVersion()).append("&du=du")
				.append("&encrypt=ENCRYPT_1_PARAMS_");
		return sb.toString();
	}
	
	public static String getDownloadListUrl(String gid) {
		StringBuilder sb = new StringBuilder();
		sb.append(WebServiceUrl.EASOU_SERVER)
				.append("aai.e?type=cdl&id=")
				.append(gid)
				.append("&size=4")
				.append("&ver=4&esid=&im=&cid=" + Env.getChannel() + "&vs="
						+ Env.getVersion())
				.append("&encrypt=ENCRYPT_1_PARAMS_");
		return sb.toString();
	}
	
	/** 拼接Sql字符串
	 * @param sql String 原始字符串
	 * @param args String[] 占位符
	 * @return 拼接好的sql 
	 * @note 增加  10.8
	 *  */
	public static String putArgsIntoSqlString(String sql,String[] args ){
		String sql_c = "";
		sql_c = sql;
		boolean isHaveArgs = (sql_c.indexOf("?")!=-1);
		if(!isHaveArgs)
			return "";
		for(int i = 0 ;i <args.length;i++){
			int temp = sql_c.indexOf("?");
			if(temp == -1)
				break;
			String begin = sql_c.substring(0, temp);
			String end = sql_c.substring(temp+1, sql_c.length());
			sql_c = begin+"'"+args[i]+"'"+end;
		}
		return sql_c;
	}
	
	/**
	 * 获取网络连接 ，包含处理代理等
	 * @param url
	 * @return
	 */
	public static HttpURLConnection getConnection(String url, Context context) {
		
		String newUrl=url;
		try {
			// 1.根据是否需要代理，转换服务器地址
			URL urlobj = new URL(url);
			String host = urlobj.getHost();
			String path = urlobj.getPath();
			int port = urlobj.getPort();
		/*	if (isWapConnected(context)) {
				StringBuffer urlBuffer = new StringBuffer();
				urlBuffer.append("http://");
				urlBuffer.append(UserData.getInstence().getProxyHost());
				urlBuffer.append(":");
				urlBuffer.append(UserData.getInstence().getProxyPort());
				urlBuffer.append(path);
				url = urlBuffer.toString();
				urlBuffer = null;
			}*/
			String suffix=null;
			/*if(isWapConnected(context)){
				if(newUrl.contains("?")){
					int index=newUrl.indexOf("?");
					if(index!=-1){
						suffix=newUrl.substring(index+1, newUrl.length());
						if(suffix!=null){
							url=url+"?"+suffix;
						}
					}	
				}
			}*/
			urlobj = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) urlobj
					.openConnection();
			/*if (isWapConnected(context)) {
				if (port < 0) {
					port = 80;
				}
				conn.setRequestProperty(Constant.PROXY_ONLINE_HOST, host + ":"
						+ port);
			}*/
			return conn;
		} catch (MalformedURLException e) {
			//e.printStackTrace();
		} catch (IOException e) {
			//e.printStackTrace();
		}
		return null;
	}
	/**
	 * 获取歌词检索接口
	 * @param gid
	 * @param rid
	 * @return
	 */
	public static String getLrcSearchUrl(String lyricId) {
		String url=WebServiceUrl.EASOU_SERVER+"aai.e?type=cld&id="+lyricId+"&ver=4&esid=&im=&cid="+Env.getChannel()+"&vs=2&encrypt=ENCRYPT_1_PARAMS_";
		return url;
	}

	/**
	 * 判断当前客户端是否在前端运行
	 * 
	 * @author DK
	 * @return
	 */
	public static boolean isAppInfront1(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		String packageName = am.getRunningTasks(1).get(0).topActivity
				.getPackageName();
		if ("com.tiantiankuyin".equals(packageName)) {
			return true;
		}
		return false;
	}
	
}
