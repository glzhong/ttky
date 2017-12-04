package com.tiantiankuyin.adapter;

import java.lang.ref.SoftReference;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tiantiankuyin.bean.OlAlbumVO;
import com.tiantiankuyin.net.EasouAsyncImageLoader;
import com.tiantiankuyin.net.EasouAsyncImageLoader.ILoadedImage;
import com.tiantiankuyin.utils.CommonUtils;
import com.tiantiankuyin.R;

public class SimilarAlbumAdatper extends BaseAdapter {
	private Context context;
	private OlAlbumVO mOlAlbumVO;
	private ListView listView;
	
	public SimilarAlbumAdatper(Context context, OlAlbumVO mOlAlbumVO) {
		this.context = context;
		this.mOlAlbumVO = mOlAlbumVO;
	}

	public OlAlbumVO getmOlAlbumVO() {
		return mOlAlbumVO;
	}

	public void setmOlAlbumVO(OlAlbumVO mOlAlbumVO) {
		this.mOlAlbumVO = mOlAlbumVO;
	}

	@Override
	public int getCount() {
		return mOlAlbumVO.getDataList().size();
	}

	public void setListView(ListView listView) {
		this.listView = listView;
	}

	@Override
	public Object getItem(int position) {
		return mOlAlbumVO.getDataList().get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.online_similar_album_item, null);
			holder.img = (ImageView) convertView.findViewById(R.id.img);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.desc = (TextView) convertView.findViewById(R.id.desc);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		String numStr=null;
		if(mOlAlbumVO.getDataList().get(position).getMusicCount()>0){
			numStr="(" + mOlAlbumVO.getDataList().get(position).getMusicCount() + ")";
		}else{
			numStr="(" + 0 + ")";
		}
		holder.name.setText(mOlAlbumVO.getDataList().get(position).getName()+numStr);
		holder.desc.setText(mOlAlbumVO.getDataList().get(position).getIntro()
				+ "");
		holder.name.setTag(mOlAlbumVO.getDataList().get(position).getId());
		/** 动画淡隐效果 */
		final Animation animation = AnimationUtils.loadAnimation(context,
				R.anim.push_in);
		if (mOlAlbumVO.getDataList().get(position).getImgUrl() != null) {
			SoftReference<Drawable>  softReference= EasouAsyncImageLoader.newInstance()
				.getLocalImage(
						CommonUtils.MD5(mOlAlbumVO.getDataList()
								.get(position).getImgUrl()));
			Drawable drawable=null;
			if(softReference!=null){
				drawable =softReference.get();
			}
			if (drawable != null) {
				holder.img.setImageDrawable(drawable);
			} else {
				holder.img.setImageDrawable(context.getResources().getDrawable(
						R.drawable.default_img));
				ILoadedImage iLoadedImage = new ILoadedImage() {
					@Override
					public void onFinishLoadedLRC(String lrcPath,
							String songName) {
					}
					@Override
					public void onFinishLoaded(SoftReference<Drawable> drawable,
							String saveName) {
						if(listView==null)
							return ;
						for (int i = 0; i < listView.getAdapter().getCount(); i++) {
							View view = listView.getChildAt(i);
							if (view == null) {
								return;
							}
							ImageView imageview = (ImageView) view
									.findViewById(R.id.img);
							if (imageview != null
									&& imageview.getTag()!=null&&imageview.getTag().equals(saveName)) {
								if (drawable != null && imageview != null) {
									imageview.setImageDrawable(drawable.get());
									imageview.startAnimation(animation);
								}
								break;
							}
						}
					}

					@Override
					public void onError(Exception e) {
					}
				};
				EasouAsyncImageLoader.newInstance().loadImage(
						mOlAlbumVO.getDataList().get(position).getImgUrl(),
						iLoadedImage,
						CommonUtils.MD5(mOlAlbumVO.getDataList().get(position)
								.getImgUrl()));
			}
			holder.img.startAnimation(animation);
			holder.img.setTag(CommonUtils.MD5(mOlAlbumVO.getDataList()
					.get(position).getImgUrl()));
		}

		return convertView;
	}

	class ViewHolder {
		ImageView img;
		TextView name;
		TextView desc;
	}

}
