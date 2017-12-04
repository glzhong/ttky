package com.tiantiankuyin.adapter;

import java.lang.ref.SoftReference;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.tiantiankuyin.bean.Banner;
import com.tiantiankuyin.net.EasouAsyncImageLoader;
import com.tiantiankuyin.net.EasouAsyncImageLoader.ILoadedImage;
import com.tiantiankuyin.utils.CommonUtils;
import com.tiantiankuyin.R;
import com.tiantiankuyin.TianlApp;

/**
 * 在线推荐页_Banner适配器
 * 
 * @author DK
 * 
 */
public class LocalScanBannerAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<Banner> mBanners;
	public static final int MSG_REFRESH = 1;
	/** 在xml中存储Banner数量 */
	public static final String BANNER_SP_KEY = "BANNER_COUNT";

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == MSG_REFRESH) {
				notifyDataSetChanged();
			}
		}
	};

	public LocalScanBannerAdapter(Context context) {
		super();
		mInflater = LayoutInflater.from(context);
	}

	public void setData(List<Banner> banners) {
		mBanners = banners;
	}

	public int getCount() {
		if (mBanners == null) {
			return 0;
		} else {
			return Integer.MAX_VALUE;
		}
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		BannerHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(
					R.layout.local_scan_banner_item, null);
			holder = new BannerHolder();
			convertView.setTag(holder);
		}
		holder.bannerImage = (ImageView) convertView.findViewById(R.id.banner);
		
//		int bannerCount = CommonUtils.readIntPreferences(mContext,
//				BANNER_SP_KEY);
		int bannerCount = mBanners.size();
		final Banner banner = mBanners.get(position % bannerCount);
		
		holder.bannerImage.setTag(banner);
		if (banner.getBannerImage() != null) {
			holder.bannerImage.setImageDrawable(banner.getBannerImage());
		} else if (!banner.isLoadingImg()) {
			banner.setLoadingImg(true);
			EasouAsyncImageLoader.newInstance().loadImage(banner.getImageURL(),
					new ILoadedImage() {
						@Override
						public void onFinishLoaded(SoftReference<Drawable> drawable,String saveName) {
							if(drawable == null)
								return;
							banner.setBannerImage(drawable.get());
							mHandler.sendEmptyMessage(MSG_REFRESH);
							banner.setLoadingImg(false);
						}

						@Override
						public void onError(Exception e) {
							banner.setLoadingImg(false);
						}

						@Override
						public void onFinishLoadedLRC(String lrcPath,String songName) {
							// TODO Auto-generated method stub
							
						}
						
					}, CommonUtils.getFileNameByUrl(banner.getImageURL()));
		}
		int[] metrics = CommonUtils.readWindowMetrics((WindowManager) TianlApp.newInstance().getSystemService(Context.WINDOW_SERVICE));
		Gallery.LayoutParams layoutParams = new Gallery.LayoutParams(metrics[1], -2);
		convertView.setLayoutParams(layoutParams);
		return convertView;
	}

	public class BannerHolder {
		public ImageView bannerImage;
	}
}
