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

import com.tiantiankuyin.bean.OlAlbumItem;
import com.tiantiankuyin.bean.OlAlbumVO;
import com.tiantiankuyin.net.EasouAsyncImageLoader;
import com.tiantiankuyin.net.EasouAsyncImageLoader.ILoadedImage;
import com.tiantiankuyin.para.Env;
import com.tiantiankuyin.utils.CommonUtils;
import com.tiantiankuyin.R;

/**
 * 在线曲库中，精选集列表的Adapter
 * @author Erica
 * 
 */
public class OnlineAlbumAdapter extends BaseAdapter{

	/** 当前操作适配对象Context */
	private Context context;
	/** 精选集数据  */
	private OlAlbumVO olAlbumVO;
	/** 精选集列表 */
	private List<OlAlbumItem> olAlbumItemList;
	/** 精选集列表对象ListView */
	private ListView mFeaturedSetList;
	private int screenWidth = Env.getScreenHeight();

	public OnlineAlbumAdapter(Context context,ListView _mFeaturedSetList) {
		this.context = context;		
		this.mFeaturedSetList = _mFeaturedSetList;
	}
	
	/** 设置精选集对象
	 * @author Erica
	 *  */
	public void setOlAlbumVO(OlAlbumVO olAlbumVO,boolean _isAppend){
		this.olAlbumVO = olAlbumVO;
		if(_isAppend){
			olAlbumItemList.addAll(olAlbumVO.getDataList());
		}
		else{
			olAlbumItemList = this.olAlbumVO.getDataList();
		}
	}

	public List<OlAlbumItem> getOlAlbumItemList() {
		return olAlbumItemList;
	}

	public void setOlAlbumItemList(List<OlAlbumItem> olAlbumItemList) {
		this.olAlbumItemList = olAlbumItemList;
	}

	@Override
	public int getCount() {
		if (olAlbumItemList != null)
			return olAlbumItemList.size();
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (olAlbumItemList != null)
			return olAlbumItemList.get(position);
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		AlbumItemHolder holder;
		if (convertView == null) {
			holder = new AlbumItemHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.online_featured_set_item, null);
			holder.image = (ImageView) convertView
					.findViewById(R.id.online_featured_set_item_image);
			holder.title = (TextView) convertView
					.findViewById(R.id.online_select_item_title);
			holder.num = (TextView) convertView
					.findViewById(R.id.online_featured_set_num);
			holder.content = (TextView) convertView
					.findViewById(R.id.online_featured_set_item_content);
			convertView.setTag(holder);
		} else {
			holder = (AlbumItemHolder) convertView.getTag();
		}
		// 设置item的信息
		final OlAlbumItem olAlbumItem = olAlbumItemList.get(position);
		String content;
		String title;
		if(olAlbumItem.getName()!=null){
			title = olAlbumItem.getName().trim();
			holder.title.setText(title);
		}
		if(olAlbumItem.getIntro()!=null){
			content = olAlbumItem.getIntro();
			content = CommonUtils.mestrStr(content, 12, 3, screenWidth/5*2);
			holder.content.setText(content);
		}
		if(olAlbumItem.getMusicCount()>0){
			holder.num.setText("(" + olAlbumItem.getMusicCount() + ")");
		}else{
			holder.num.setText("(" + 0 + ")");
		}
		// 设置当前adapter的图片View标签
		// 获取图片本地路径
		if(olAlbumItem.getImgUrl()!=null){
			SoftReference<Drawable> softReference = EasouAsyncImageLoader.newInstance().getLocalImage(CommonUtils.MD5(olAlbumItem.getImgUrl()));
			Drawable d= softReference == null ? null : softReference.get();
			if(d!=null){
				holder.image.setImageDrawable(d);
				Animation animation = AnimationUtils.loadAnimation(
						context, R.anim.push_in);
				holder.image.startAnimation(animation);
			}else{
				d=context.getResources().getDrawable(R.drawable.default_img);//如果本地沒有图片 设置缺省的
				holder.image.setImageDrawable(d);
				Animation animation = AnimationUtils.loadAnimation(
						context, R.anim.push_in);
				holder.image.startAnimation(animation);
				ILoadedImage iLoadedImage = new ILoadedImage() {
					@Override
					public void onFinishLoadedLRC(String lrcPath, String songName) {
					}
					@Override
					public void onFinishLoaded(SoftReference<Drawable> drawable,String saveName) {
						for(int i=0;i<mFeaturedSetList.getAdapter().getCount();i++){
							View view=mFeaturedSetList.getChildAt(i);
							if(view==null){
								return ;
							}
							ImageView imageview =(ImageView)view.findViewById(R.id.online_featured_set_item_image);
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
				EasouAsyncImageLoader.newInstance().loadImage(olAlbumItem.getImgUrl(), iLoadedImage, CommonUtils.MD5(olAlbumItem.getImgUrl()));
			}
			holder.image.setTag(CommonUtils.MD5(olAlbumItem.getImgUrl()));
		}
		
		return convertView;
	}
	
	/** 精选集界面适配器控制对象
	 *  */
	class AlbumItemHolder {
		public ImageView image;
		public TextView title;
		public TextView num;
		public TextView content;
	}
}
