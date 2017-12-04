package com.tiantiankuyin.adapter;

import java.lang.ref.SoftReference;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tiantiankuyin.bean.CategoryItem;
import com.tiantiankuyin.net.EasouAsyncImageLoader;
import com.tiantiankuyin.net.EasouAsyncImageLoader.ILoadedImage;
import com.tiantiankuyin.utils.CommonUtils;
import com.tiantiankuyin.view.MyGridView;
import com.tiantiankuyin.R;

public class GridViewCategoryAdapter extends BaseAdapter {
	private List<CategoryItem> dataList;
	private Context context;
	private MyGridView gridView;
	private boolean isZero=false;
	
	public GridViewCategoryAdapter(List<CategoryItem> dataList,Context context) {
		this.dataList = dataList;
		this.context=context;
	}
	public void setGridView(MyGridView gridView) {
		this.gridView = gridView;
	}
	@Override
	public int getCount() {
		return dataList.size();
	}

	@Override
	public Object getItem(int position) {
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.category_expand_item_sub, null);
			holder.category_img = (ImageView) convertView
					.findViewById(R.id.category_img);
			holder.category_name = (TextView) convertView
					.findViewById(R.id.category_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.category_name.setText(dataList.get(position).getType());
		holder.category_img.setTag(CommonUtils.MD5(dataList.get(position).getImgUrl()));
		holder.category_name.setTag(dataList.get(position).getId()+"_"+dataList.get(position).getImgUrl());
		SoftReference<Drawable> softReference=EasouAsyncImageLoader.newInstance().getLocalImage(dataList.get(position).getImgUrl());
		boolean isNeedGetImage=true;
		if(softReference!=null){
			Drawable d=softReference.get();
			if(d!=null){
				isNeedGetImage=false;
				holder.category_img.setImageDrawable(d);
			}
		}
		if(isNeedGetImage){//需要去网上获取图片
			holder.category_img.setImageResource(R.drawable.default_img);
			ILoadedImage iLoadedImage = new ILoadedImage() {
				@Override
				public void onFinishLoadedLRC(String lrcPath, String songName) {
				}
				@Override
				public void onFinishLoaded(SoftReference<Drawable> drawable,String saveName) {
					//防止重复刷屏  不知道为什么getView 会一直执行 并且 position=0 这个地方是用来屏蔽掉重复执行的逻辑
					if(isZero&&(position==0)){
						return ;
					}
					if(position==0){
						isZero=true;
					}
					ImageView imageview = (ImageView) gridView
							.findViewWithTag(CommonUtils.MD5(saveName));
					if (drawable != null && imageview != null) {
						imageview.setImageDrawable(drawable.get());
					}
				}
				@Override
				public void onError(Exception e) {
				}
			};
			EasouAsyncImageLoader.newInstance().loadImage(dataList.get(position).getImgUrl(), iLoadedImage, dataList.get(position).getImgUrl());
		}
		return convertView;
	}
	class ViewHolder {
		ImageView category_img;
		TextView category_name;
	}
}
