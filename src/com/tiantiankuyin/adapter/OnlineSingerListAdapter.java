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

import com.tiantiankuyin.bean.SingerBean;
import com.tiantiankuyin.net.EasouAsyncImageLoader;
import com.tiantiankuyin.net.EasouAsyncImageLoader.ILoadedImage;
import com.tiantiankuyin.utils.CommonUtils;
import com.tiantiankuyin.R;
import com.tiantiankuyin.TianlApp;

public class OnlineSingerListAdapter extends BaseAdapter {

	/** 当前操作歌手列表对象 */
	private List<SingerBean>  mMusicList;
	/** 当前操作界面对象 */
	private Context context;
	/** 列表对象 */
	private ListView mFeaturedSetList;
	
	public OnlineSingerListAdapter(Context context,ListView _mFeaturedSetList){
		this.context=context;
		mFeaturedSetList =_mFeaturedSetList;
	}
	/** 设置当前操作歌手列表对象
	 *  */
	public void setDatas(List<SingerBean>  musics) {
		if(mMusicList ==null)
			mMusicList = musics;
		else{
			mMusicList .addAll(musics);
		}
	}

	public List<SingerBean> getmMusicList() {
		return mMusicList;
	}

	@Override
	public int getCount() {
		if (mMusicList != null)
			return mMusicList.size();
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (mMusicList != null)
			return mMusicList.get(position);
		return null;
	}

	@Override
	public long getItemId(int position) {
		if (mMusicList != null)
			return position;
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		SingerHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(TianlApp.newInstance()).inflate(
					R.layout.online_singer_list_item, null);
			holder = new SingerHolder();
			holder.singerName = (TextView) convertView
					.findViewById(R.id.online_singer_name);
			holder.downloadNum = (TextView) convertView
					.findViewById(R.id.online_donwnum);
			holder.singerAlum = (ImageView) convertView
					.findViewById(R.id.online_singer_image);
			
			holder.showlevel=(ImageView) convertView
					.findViewById(R.id.showlevel);
			convertView.setTag(holder);
		}
		holder = (SingerHolder) convertView.getTag();
		holder.singerName.setText(mMusicList.get(position).getAuthor());
		int downLoadNum = setMusicHot(mMusicList.get(position).getDownloadNum());
		holder.downloadNum.setText(downLoadNum+"");
//		holder.singerAlum.setBackgroundResource(R.drawable.list_item_artist_defalut_img);
		//有6个等级，150以下，150-300，300-450,450-600,600-750,750以上
		if(downLoadNum>750){
			holder.showlevel.setBackgroundResource(R.drawable.firstlevel);	
		}else if( downLoadNum <= 750 && downLoadNum > 600){
			holder.showlevel.setBackgroundResource(R.drawable.secondlevel);
		}else if( downLoadNum <= 600 && downLoadNum > 450){
			holder.showlevel.setBackgroundResource(R.drawable.thirdlevel);
		}else if( downLoadNum <= 450 && downLoadNum > 300){
			holder.showlevel.setBackgroundResource(R.drawable.fourthlevel);
		}else if( downLoadNum <= 300 && downLoadNum > 150){
			holder.showlevel.setBackgroundResource(R.drawable.fifthlevel);
		}else if( downLoadNum <= 150 ){
			holder.showlevel.setBackgroundResource(R.drawable.sixthlevel);
		}
		
		if(mMusicList.get(position).getImgUrl()!=null){
			SoftReference<Drawable> softReference = EasouAsyncImageLoader.newInstance().getLocalImage(CommonUtils.MD5(mMusicList.get(position).getImgUrl()));
			Drawable d=softReference == null ? null : softReference.get();
			if(d!=null){
				holder.singerAlum.setImageDrawable(d);
				Animation animation = AnimationUtils.loadAnimation(
						context, R.anim.push_in);
				holder.singerAlum.startAnimation(animation);
			}else{
				d=context.getResources().getDrawable(R.drawable.default_img);//如果本地沒有图片 设置缺省的
				holder.singerAlum.setImageDrawable(d);
				Animation animation = AnimationUtils.loadAnimation(
						context, R.anim.push_in);
				holder.singerAlum.startAnimation(animation);
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
							ImageView imageview =(ImageView)view.findViewById(R.id.online_singer_image);
							if(imageview!=null&& imageview.getTag() != null && imageview.getTag().equals(saveName)){
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
				EasouAsyncImageLoader.newInstance().loadImage(mMusicList.get(position).getImgUrl(), iLoadedImage, CommonUtils.MD5(mMusicList.get(position).getImgUrl()));
			}
			holder.singerAlum.setTag(CommonUtils.MD5(mMusicList.get(position).getImgUrl()));
		}
		return convertView;
	}
	
	/** 当前歌手适配控制对象
	 *  */
	public class SingerHolder {
		public TextView singerName;
		public TextView downloadNum;
		public ImageView singerAlum;
		public ImageView showlevel;
	}

	private int setMusicHot(String _download_num){
		int hot_num =0;
		try{
			int download_num = Integer.parseInt(_download_num);
			if(download_num>300){
				hot_num =download_num/300;
				if(hot_num>999){
					hot_num = 999;
				}
			}else{
				hot_num = 1;
			}
		}catch(Exception e){
//			e.printStackTrace();
		}
		return hot_num;
	}
	
}
