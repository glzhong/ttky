package com.tiantiankuyin.net;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.tiantiankuyin.para.Constant;
import com.tiantiankuyin.utils.CommonUtils;

/**
 * 异步加载图片 使用方法：通过静态函数newInstance获得该类的实例，并调用loadImage函数异步加载图片。
 * 通过实现ILoadedImage并重写onFinishLoaded方法，即可得到图片资源 建议：
 * 在要显示的ImageView中定义以url数据指纹为Tag，
 * 并在在Adapter的getView方法中，以ListView.findViewWithTag方法得到ImageView控件之后再将其渲染到界面上。
 * 加载图片不建议在滚动ListView时使用，
 * 应当监听ListView的setOnScrollListener事件并在滚动行为结束时即OnScrollListener
 * .SCROLL_STATE_IDLE状态， 再根据Tag为控件加载图片
 * 
 * @author DK
 * 
 */
public class EasouAsyncImageLoader {

	private EasouAsyncImageLoader() {
		if (!CommonUtils.isExternalStorageAvailable())
			imageCache = new HashMap<String, SoftReference<Drawable>>();
	}

	private static EasouAsyncImageLoader imageLoader;

	/**
	 * 用于没有外部存储器或者外部存储器无法使用时的图片缓存
	 */
	private HashMap<String, SoftReference<Drawable>> imageCache;

	/**
	 * 获取异步加载图片实例
	 * 
	 * @return
	 */
	public static EasouAsyncImageLoader newInstance() {
		if (imageLoader == null) {
			synchronized (EasouAsyncImageLoader.class) {
				if (imageLoader == null)
					imageLoader = new EasouAsyncImageLoader();
			}
		}
		return imageLoader;
	}

	/**
	 * 异步加载图片
	 * 
	 * @param imageUrl
	 *            图片资源地址
	 * @param iLoadedImage
	 *            加载回调接口
	 * @param saveName
	 *            文件名，用于数据指纹，区分唯一图片
	 */
	public void loadImage(final String imageUrl,
			final ILoadedImage iLoadedImage, final String saveName) {
		EasouAsyncTask<Object, Object, SoftReference<Drawable>> easouAsyncTask = new EasouAsyncTask<Object, Object, SoftReference<Drawable>>() {

			private InputStream inputStream;

			@Override
			protected SoftReference<Drawable> doInBackground(Object... params)
					throws EasouHttpException {
				if (imageCache != null && saveName != null && imageCache.get(Constant.SdcardPath.IMAGE_SAVEPATH + "/"
						+ CommonUtils.MD5(saveName)) != null) { // 如果缓存中存在，则直接返回缓存
					return imageCache.get(Constant.SdcardPath.IMAGE_SAVEPATH + "/"
							+ CommonUtils.MD5(saveName));
				}
				SoftReference<Drawable> localImage = getLocalImage(saveName); // 如果本地图片存在，则读取本地
				if (localImage != null)
					return localImage;
				try {
					URL url = new URL(imageUrl); // 从网络读取图片
					inputStream = (InputStream) url.getContent();
					SoftReference<Drawable> drawable = new SoftReference<Drawable>(
							Drawable.createFromStream(inputStream,
									"ImageLoader"));
					if (!CommonUtils.isExternalStorageAvailable()) { // 如果此时外部存储不可用，则缓存
						if (imageCache == null)
							imageCache = new HashMap<String, SoftReference<Drawable>>();
						imageCache.put(Constant.SdcardPath.IMAGE_SAVEPATH + "/"
								+ CommonUtils.MD5(saveName), drawable);
					} else { // 储存在外部存储器
						saveImage(drawable, saveName);
						if (imageCache == null)
							imageCache = new HashMap<String, SoftReference<Drawable>>();
						imageCache.put(Constant.SdcardPath.IMAGE_SAVEPATH + "/"
								+ CommonUtils.MD5(saveName), drawable);
					}
					return drawable;

				} catch (MalformedURLException e) {
					iLoadedImage.onError(e);
				} catch (IOException e) {
					iLoadedImage.onError(e);
				} finally {
					if (inputStream != null) {
						try {
							inputStream.close();
						} catch (IOException e) {
						}
					}
				}
				return null;
			}

			@Override
			protected void onPostExecute(SoftReference<Drawable> result) {
				iLoadedImage.onFinishLoaded(result, saveName);
			}

		};
		easouAsyncTask.execute(new Object[] {});
	}

	/**
	 * 将图片保存在本地
	 * 
	 * @param drawable
	 * @param saveName
	 * @return
	 * @throws IOException
	 */
	private boolean saveImage(SoftReference<Drawable> drawableSoft,
			String saveName) throws IOException {
		if (drawableSoft == null || drawableSoft == null)
			return false;
		Drawable drawable = drawableSoft.get();
		if(drawable == null) 
			return false;
		BitmapDrawable bd = (BitmapDrawable) drawable;
		Bitmap bitmap = bd.getBitmap();
		
		File file = new File(Constant.SdcardPath.IMAGE_SAVEPATH);
		if (!file.exists()) {
			file.mkdirs();
		}
		File image = new File(Constant.SdcardPath.IMAGE_SAVEPATH + "/"
				+ CommonUtils.MD5(saveName));
		FileOutputStream fos = null;
		try {
			image.createNewFile();
			fos = new FileOutputStream(image);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
		} catch (IOException e) {
			throw new IOException();
		} finally {
			try {
				if (fos != null)
					fos.close();
				if (bitmap != null) {
					// bitmap.recycle();
				}
			} catch (IOException e) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 获取本地图片
	 * 
	 * @param saveName
	 * @return
	 */
	public SoftReference<Drawable> getLocalImage(String saveName) {
		String filePath = Constant.SdcardPath.IMAGE_SAVEPATH + "/"
				+ CommonUtils.MD5(saveName);
		if(imageCache != null){
			SoftReference<Drawable> softReference = imageCache.get(filePath);
			if(softReference != null && softReference.get() != null)
				return softReference;
		}
		File imageFile = new File(filePath);
		if (imageFile.exists()) {
			SoftReference<Drawable> softReference = new SoftReference<Drawable>(decodeFile(new File(filePath)));
			if(imageCache == null){
				imageCache = new HashMap<String, SoftReference<Drawable>>();
			}
			imageCache.put(filePath, softReference);
			return softReference;
		}
		return null;
	}
	
	private Drawable decodeFile(File file) {
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory
					.decodeStream(new FileInputStream(file), null, options);
			final int REQUIRED_SIZE = 400;
			int scale = 1;
			while (options.outWidth / scale / 2 >= REQUIRED_SIZE
					&& options.outHeight / scale / 2 >= REQUIRED_SIZE)
				scale *= 2;
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			Bitmap bitmap = BitmapFactory.decodeStream(
					new FileInputStream(file), null, o2);
			BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
			return bitmapDrawable;
		} catch (FileNotFoundException e) {

		}
		return null;
	}

	/**
	 * 异步加载图片回调接口
	 * 
	 * @author DK
	 * 
	 */
	public interface ILoadedImage {
		public abstract void onFinishLoaded(SoftReference<Drawable> drawable, String imageName);

		public abstract void onError(Exception e);

		// 歌词加载完成
		public abstract void onFinishLoadedLRC(String lrcPath, String songName);
	}
}