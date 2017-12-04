package com.tiantiankuyin.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.tiantiankuyin.database.bll.OnDataPreparedListener;
import com.tiantiankuyin.database.bll.OnlineMusicManager;
import com.tiantiankuyin.net.EasouAsyncImageLoader;
import com.tiantiankuyin.net.EasouAsyncImageLoader.ILoadedImage;
import com.tiantiankuyin.para.Constant;
import com.tiantiankuyin.para.Env;
import com.tiantiankuyin.para.SPHelper;
import com.tiantiankuyin.para.WebServiceUrl;
import com.tiantiankuyin.TianlApp;

/**
 * 工具类用来搜索歌手或者专辑图片或者歌词地址的
 * 
 * @author easou
 * 
 */
public class SearchPictureAndLrcManager {
	private static SearchPictureAndLrcManager instance;
	private Context context;
	
	public static final int SEARCH_SINGER_URL = 4;// 搜索歌手图片
	public static final int SEARCH_ALBUM_URL = 5;// 搜索专辑图片
	public static final int SEARCH_SINGER_URL_FINISH = 6;// 搜索歌手图片结束
	public static final int SEARCH_SINGER_URL_ERROR = 7;// 搜索歌手图片出错
	
	public static SearchPictureAndLrcManager getInstance() {
		if (instance == null) {
			instance = new SearchPictureAndLrcManager();
			initLrcFilePath();
		}
		return instance;
	}
	/**
	 * 初始化歌词文件夹
	 */
	private static void initLrcFilePath(){
		File f=new File(Constant.SdcardPath.LYRIC_SAVEPATH);
		if(!f.exists()){
			f.mkdirs();	
		}
	}
	/**
	 * 获取歌手的pic的url
	 */
	public void getSingerImageUrlFromNet(ILoadedImage iLoadedImage,
			Context context,String singerName,String albumName) {// 先查询歌手图片如果歌手图片不存 再查询专辑图片
		this.context = context;
		if (singerName != null) {
			if (!(singerName.contains("<unknown>") || singerName.contains("<未知歌手>"))) {
				getImageUrlFromNet(SEARCH_SINGER_URL,
						singerName, iLoadedImage);
				return;// 如果歌手名称存在的话就不用下面的专辑名称了
			}
		}
		if (albumName != null) {
			if (!(albumName.contains("<unknown>") || albumName.contains("<未知专辑>"))) {
				getImageUrlFromNet(SEARCH_ALBUM_URL,
						albumName, iLoadedImage);
			}
		}
	}

	/**
	 * {"singer":"六哲","url_s":
	 * "http://221.181.66.49:82/151/picture/singer/stdCompressPic/2012/07/12/13/1532371373_80x80.jpeg"
	 * , "url_m":
	 * "http://221.181.66.49:82/151/picture/singer/stdCompressPic/2012/07/12/13/1532371373_250x250.jpeg"
	 * , "url_b":
	 * "http://221.181.66.49:82/151/picture/singer/stdCompressPic/2012/07/12/13/1532371373_400x400.jpeg"
	 * }
	 * 
	 * @param type
	 * @param name
	 * @param listener
	 * @param context
	 */
	private void getImageUrlFromNet(int type, final String name,
			final ILoadedImage iLoadedImage) {
		Drawable d = getLocalImage(name);// 先判断本地是否存在
		if (d != null) {
			iLoadedImage.onFinishLoaded(new SoftReference<Drawable>(d),name);// 如果存在就返回
			return;
		}
		if(!SPHelper.newInstance().getAutoDownloadPic()){
			iLoadedImage.onFinishLoaded(null,name);// 如果存在就返回
			return ;
		}
		String spiltName=name;
		if(spiltName.contains("/")){//合唱歌手  拿出第一个歌手的照片 刘德华/郭富城 刘德华,郭富城  刘德华_郭富城
			spiltName=spiltName.split("/")[0];
		}else if(spiltName.contains(",")){
			spiltName=spiltName.split(",")[0];
		}else if(spiltName.contains("_")){
			spiltName=spiltName.split("_")[0];
		}
		String url = getImageRequestUrl(type, spiltName);
		
		OnlineMusicManager.getInstence().getSongUrlData(TianlApp.newInstance(),
				new OnDataPreparedListener<String>() {
					@Override
					public void onDataPrepared(String data) {
						if (data != null) {	
							String json = data;
							if(json!=null&&json.length()>0){
								String imageUrl = parserImageUrl(json,name);
								if (imageUrl != null && imageUrl.length() > 6) {
									if(newName!=null){//用新名字保存图片  区别图片清晰度
										getBitmapByUrl(imageUrl, iLoadedImage, newName);
									}else{
										getBitmapByUrl(imageUrl, iLoadedImage, name);
									}
								}	
							}
							
						}else {
							Lg.d("getresultURL() == null");
							return;
						}
					}
				}, url,false);
	}

	/**
	 * 获取本地图片
	 * 
	 * @param saveName
	 * @return
	 */
	private Drawable getLocalImage(String saveName) {
		String filePath=null;
		// 先取清晰的
		filePath = Constant.SdcardPath.IMAGE_SAVEPATH + "/"
				+ CommonUtils.MD5(saveName+"_big");
		File imageFile = new File(filePath);
		if (imageFile.exists()) {
			return Drawable.createFromPath(filePath);
		}
		// 中等清晰的
		filePath = Constant.SdcardPath.IMAGE_SAVEPATH + "/"
				+ CommonUtils.MD5(saveName+"_middle");
		File imageFileMiddle = new File(filePath);
		if (imageFileMiddle.exists()) {
			return Drawable.createFromPath(filePath);
		}
		// 低等不清晰的
		filePath = Constant.SdcardPath.IMAGE_SAVEPATH + "/"
				+ CommonUtils.MD5(saveName);
		File imageFileSmall = new File(filePath);
		if (imageFileSmall.exists()) {
			return Drawable.createFromPath(filePath);
		}
		return null;
	}

	/**
	 * 获取歌词
	 * 
	 * @param type
	 * @param name
	 * @param iLoadedImage
	 * @param context
	 */
	public void getLrcUrlFromNet(final ILoadedImage iLoadedImage,
			Context context, String songName,String singerName,String lyricId) {
		if(songName.contains("_")){//做个处理对精选集拆分
			songName=songName.split("_")[0];
		}
		String savePath = Constant.SdcardPath.LYRIC_SAVEPATH + "/" + songName + ".lrc";
		File file = new File(savePath);
		if (file.exists()) {// 如果本地存在
			// 给出任务完成通知刷新
			iLoadedImage.onFinishLoadedLRC(savePath, songName);
			return;
		}
		if(!SPHelper.newInstance().getAutoDownloadLrc()){
			iLoadedImage.onFinishLoadedLRC(null, songName);
			return;
		}
		this.context = context;
		String url=null;
		final String newSongName=songName;
		if(lyricId!=null){//根据歌词id，获取歌词
			url = CommonUtils.getLrcSearchUrl(lyricId);
			
			OnlineMusicManager.getInstence().getSongUrlData(TianlApp.newInstance(),
					new OnDataPreparedListener<String>() {
				@Override
				public void onDataPrepared(String data) {
					if (data != null) {	
						String json = data;
						if(json!=null&&json.length()>0){
							String lrcUrl = parserLrcUrl(json);
							if (lrcUrl != null && lrcUrl.length() > 6) {
								getLrcFromNet(newSongName, lrcUrl, iLoadedImage);
							}
						}
					}else {
						Lg.d("getresultURL() == null");
						iLoadedImage.onError(new Exception());
						return;
					}
				}
			}, url,false);
		}else{
			//获取歌词列表
			url = getLrcListRequestUrl(songName, singerName);
			OnlineMusicManager.getInstence().getLrcUrlFromListData(TianlApp.newInstance(),
					new OnDataPreparedListener<String>() {
				@Override
				public void onDataPrepared(String data) {
					if (data != null) {	
						String lrcUrl = data;
						if (lrcUrl != null && lrcUrl.length() > 6) {
							getLrcFromNet(newSongName, lrcUrl, iLoadedImage);
						}
					}else {
						Lg.d("getresultURL() == null");
						iLoadedImage.onError(new Exception());
						return;
					}
				}
			}, url,false);
		}
	}

	/**
	 * {"url":
	 * "http://gc.lxzww.com:83/music/lrc/2012/08/31/02/0453_0001237_utf8.lrc?p=26104&esid=K_3FWajqssySjb9"
	 * } 解析歌词json数据
	 */
	private String parserLrcUrl(String json) {
		String lrcUrl = null;
		try {
			JSONObject jsonObject = new JSONObject(json);
			if (!jsonObject.isNull("url")) {
				lrcUrl = jsonObject.getString("url");
			}
		} catch (JSONException e) {
			//e.printStackTrace();
		}
		return lrcUrl;
	}

	/**
	 * 从网络获取歌词
	 */
	private void getLrcFromNet(final String songName, String lrcUrl,
			final ILoadedImage iLoadedImage) {
		OnlineMusicManager.getInstence().getLrcData(TianlApp.newInstance(),
				new OnDataPreparedListener<String>() {
					@Override
					public void onDataPrepared(String data) {
						if (data != null) {	
							String savePath = Constant.SdcardPath.LYRIC_SAVEPATH + "/" + songName
									+ ".lrc";
							FileOutputStream fos = null;
							try {
								fos = new FileOutputStream(new File(savePath));// 把歌词写入到sdcard里面去
								fos.write(data.getBytes());
								fos.flush();
							} catch (Exception e) {
								//e.printStackTrace();
							} finally {
								if (fos != null) {
									try {
										fos.close();
									} catch (IOException e) {
										//e.printStackTrace();
									}
								}
							}
							// 给出任务完成通知刷新
							iLoadedImage.onFinishLoadedLRC(savePath, songName);
						}else {
							Lg.d("getresultURL() == null");
							iLoadedImage.onError(new Exception());
							return;
						}
					}
				}, lrcUrl,false);
	}

	/**
	 * 访问获取图片
	 * 
	 * @param imageUrl
	 * @param iLoadedImage
	 * @param saveName
	 */
	private void getBitmapByUrl(String imageUrl, ILoadedImage iLoadedImage,
			String saveName) {
		EasouAsyncImageLoader.newInstance().loadImage(imageUrl, iLoadedImage,
				saveName);
	}
	/**
	 * 照片名字的新名字  不然 周华健  如果是大图片的话就位 周华健big 中图片的话就为周华健middle 用来区别图片清晰度
	 */
	private String newName;
	/**
	 * 解析图片json字符串
	 * 
	 * @param json
	 * @return
	 */
	private String parserImageUrl(String json,String name) {
		String url = null;
		try {
			JSONObject jsonObject = new JSONObject(json);
			if (!jsonObject.isNull("imgUrl400")) {
				url = jsonObject.getString("imgUrl400");
				newName=name+"_400";
				if (url == null || url.length() < 6) {
					if (!jsonObject.isNull("imgUrl250")) {
						url = jsonObject.getString("imgUrl250");
						newName=name+"_250";
						if (url == null || url.length() < 6) {
							if (!jsonObject.isNull("imgUrl80")) {
								url = jsonObject.getString("imgUrl80");
								newName=name+"_80";
							}
						}
					}
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		return url;
	}

	/**
	 * 获取要图片访问搜索接口的url
	 */
	private String getImageRequestUrl(int type, String name) {
		String url = WebServiceUrl.EASOU_SERVER;
		StringBuffer sb = new StringBuffer();
		if (type == SEARCH_ALBUM_URL) {// 获取专辑图片
			sb.append("type=cp");
			sb.append("&query=" + URLEncoder.encode(name + "") + "");
		} else if (type == SEARCH_SINGER_URL) {// 获取歌手图片
			if (name.equals("<unknown>")) {
				return null;
			}
			sb.append("type=sp");
			sb.append("&singer=" + URLEncoder.encode(name) + "");
		}
		sb.append("&ver=4");
		sb.append("&vs=2");
		sb.append("&esid=");
		sb.append("&encrypt=ENCRYPT_1_PARAMS_");
		sb.append("&im=" + CommonUtils.getIMEI(context));
		sb.append("&cid=" + Env.getChannel());
		url += "aai.e?" + sb.toString();
		Lg.d(url);
		return url;
	}

	/**
	 * 获取歌词请求地址的接口
	 * 
	 * @param songName
	 * @param singerName
	 * @return
	 */
	private String getLrcRequestUrl(String songName, String singerName) {
		if (songName == null) {
			return null;
		}
		String url = WebServiceUrl.EASOU_SERVER;
		StringBuffer sb = new StringBuffer();
		sb.append("type=l");// 请求类型，s:MP3检索；d:获取信息检索；l:歌词检索
		try {
			sb.append("&song=" + URLEncoder.encode(songName, "UTF-8"));// 歌名
			if (singerName.equals("<unknown>") || singerName.contains("未知歌手")) {
				singerName = "";
			}
			sb.append("&singer=" + URLEncoder.encode(singerName, "UTF-8"));// 歌手
		} catch (Exception e) {
			//e.printStackTrace();
		}

		sb.append("&ver=4");// 接口标识(不可变)
		sb.append("&vs=2");
		sb.append("&esid=");// 调用接口需要带上此参数（标识用户，带有用户身份信息）
		sb.append("&im=" + CommonUtils.getIMEI(context));// imei串号
		sb.append("&cid=" + Env.getChannel());// 分配的渠道号
		sb.append("&encrypt=ENCRYPT_1_PARAMS_");
		url = url + "aai.e?" + sb.toString();
		return url;
	}
	
	/**
	 * 获取歌词列表请求地址的接口
	 * 
	 * @param songName
	 * @param singerName
	 * @return
	 * @author Perry
	 */
	private String getLrcListRequestUrl(String songName, String singerName) {
		if (songName == null) {
			return null;
		}
		String url = WebServiceUrl.EASOU_SERVER;
		StringBuffer sb = new StringBuffer();
		sb.append("type=lm");// 请求类型，s:MP3检索；d:获取信息检索；l:歌词检索
		try {
			sb.append("&song=" + URLEncoder.encode(songName, "UTF-8"));// 歌名
			if (singerName.equals("<unknown>") || singerName.contains("未知歌手")) {
				singerName = "";
			}
			sb.append("&singer=" + URLEncoder.encode(singerName, "UTF-8"));// 歌手
		} catch (Exception e) {
			//e.printStackTrace();
		}

		sb.append("&ver=4");// 接口标识(不可变)
		sb.append("&page=1");
		sb.append("&size=6");
		sb.append("&esid=");// 调用接口需要带上此参数（标识用户，带有用户身份信息）
		sb.append("&im=" + CommonUtils.getIMEI(context));// imei串号
		sb.append("&cid=" + Env.getChannel());// 分配的渠道号
		sb.append("&vs=21");
		sb.append("&encrypt=ENCRYPT_1_PARAMS_");
		url = url + "aai.e?" + sb.toString();
		return url;
	}
}
