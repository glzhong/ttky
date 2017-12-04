package com.tiantiankuyin.adapter;

import java.lang.ref.SoftReference;
import java.util.List;

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

import com.tiantiankuyin.bean.HotSaleItem;
import com.tiantiankuyin.net.EasouAsyncImageLoader;
import com.tiantiankuyin.net.EasouAsyncImageLoader.ILoadedImage;
import com.tiantiankuyin.utils.CommonUtils;
import com.tiantiankuyin.R;

public class HotSaleAdapter extends BaseAdapter {
	private List<HotSaleItem> hotSaleItems;
	private Context context;
	private ListView listView;
	public  HotSaleAdapter(List<HotSaleItem> hotSaleItems,Context context){
		this.hotSaleItems=hotSaleItems;
		this.context=context;
	}
	public void setListView(ListView listView) {
		this.listView = listView;
	}
	@Override
	public int getCount() {
		return hotSaleItems.size();
	}

	@Override
	public Object getItem(int position) {
		return hotSaleItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView==null){
			holder=new ViewHolder();
			convertView=LayoutInflater.from(context).inflate(R.layout.online_hotsale_item, null);
			holder.hotsale_img=(ImageView)convertView.findViewById(R.id.hotsale_img);
			holder.title=(TextView)convertView.findViewById(R.id.title);
			holder.desc=(TextView)convertView.findViewById(R.id.desc);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder)convertView.getTag();
		}
		holder.title.setText(hotSaleItems.get(position).getName());
		holder.title.setTag(hotSaleItems.get(position).getId());//保存ID 点击事件用
		holder.desc.setText(hotSaleItems.get(position).getIntor());
		if(hotSaleItems.get(position).getImgUrl()!=null&&hotSaleItems.get(position).getImgUrl().length()>0){
			//看本地是否存在图片
			SoftReference<Drawable> softReference = EasouAsyncImageLoader.newInstance().getLocalImage(CommonUtils.MD5(hotSaleItems.get(position).getImgUrl()));
			Drawable d= softReference == null ? null : softReference.get();
			if(d!=null){
				holder.hotsale_img.setImageDrawable(d);
				Animation animation = AnimationUtils.loadAnimation(
						context, R.anim.push_in);
				holder.hotsale_img.startAnimation(animation);
			}else{
				d=context.getResources().getDrawable(R.drawable.default_img);//如果本地沒有图片 设置缺省的
				holder.hotsale_img.setImageDrawable(d);
				Animation animation = AnimationUtils.loadAnimation(
						context, R.anim.push_in);
				holder.hotsale_img.startAnimation(animation);
				ILoadedImage iLoadedImage = new ILoadedImage() {
					@Override
					public void onFinishLoadedLRC(String lrcPath, String songName) {
					}
					@Override
					public void onFinishLoaded(SoftReference<Drawable> drawable,String saveName) {
						for(int i=0;i<listView.getAdapter().getCount();i++){
							View view=listView.getChildAt(i);
							if(view==null){
								return ;
							}
							ImageView imageview =(ImageView)view.findViewById(R.id.hotsale_img);
							if(imageview!=null&&imageview.getTag()!=null&&imageview.getTag().equals(saveName)){
								if (drawable != null && imageview != null) {
									imageview.setImageDrawable(drawable.get());
									/** 动画淡隐效果 */
									Animation animation = AnimationUtils.loadAnimation(
											context, R.anim.push_in);
									if (animation != null) {
										imageview.startAnimation(animation);
									}
								}
								break;
							}
						}
					}
					@Override
					public void onError(Exception e) {
					}
				};
				EasouAsyncImageLoader.newInstance().loadImage(hotSaleItems.get(position).getImgUrl(), iLoadedImage, CommonUtils.MD5(hotSaleItems.get(position).getImgUrl()));
			}
			holder.hotsale_img.setTag(CommonUtils.MD5(hotSaleItems.get(position).getImgUrl()));
		}
		return convertView;
	}
	class ViewHolder{
		ImageView hotsale_img;
		TextView title;
		TextView desc;
	}
}
