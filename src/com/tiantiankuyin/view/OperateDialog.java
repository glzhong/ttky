/**
 * 
 */
package com.tiantiankuyin.view;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tiantiankuyin.adapter.MusicListAdapter;
import com.tiantiankuyin.bean.MusicInfo;
import com.tiantiankuyin.component.activity.DownloadedActivity;
import com.tiantiankuyin.database.bll.LocalMusicManager;
import com.tiantiankuyin.para.Env;
import com.tiantiankuyin.play.PlayLogicManager;
import com.tiantiankuyin.utils.MusicOperate;
import com.tiantiankuyin.R;
import com.tiantiankuyin.TianlApp;

/**
 * 歌曲条目操作弹出框
 * @author Erica
 * @note 实现   OperatDialog cr = new OperatDialog(this, R.style.easouDialog);
			  cr.setMusicInfo(musicInfo);
			  cr.show();
 */
public class OperateDialog extends Dialog {

	/** 界面操作对象 */
	private Context context;
	/** dialog里面的内容 */
	private ListView listView;
	/** 操作的当前歌曲 */
	private MusicInfo musicInfo;
	/** 操作队列显示列表 */
	private List<OprateItem> datas = null;

	/** 屏幕高*/
	private int screen_Height;
	
	private long songListId;
	

	public OperateDialog(Context context) {
		super(context);
		this.context = context;
		datas =new ArrayList<OprateItem>();
		OprateItem add = new OprateItem();
		add.setShowImage(context.getResources().getDrawable(R.drawable.dialog_add_icon_img));
		add.setShowText(context.getResources().getString(R.string.dialog_add));
		screen_Height = Env.getScreenHeight();
		OprateItem delete = new OprateItem();
		delete.setShowImage(context.getResources().getDrawable(R.drawable.dialog_delete_icon_img));
		delete.setShowText(context.getResources().getString(R.string.dialog_delete));
		OprateItem share = new OprateItem();
		share.setShowImage(context.getResources().getDrawable(R.drawable.dialog_share_icon_img));
		share.setShowText(context.getResources().getString(R.string.dialog_share));
		OprateItem fav = new OprateItem();
		fav.setShowImage(context.getResources().getDrawable(R.drawable.dialog_fav_icon_img));
		fav.setShowText(context.getResources().getString(R.string.dialog_fav));
		
		datas.add(fav);
		datas.add(add);
		datas.add(share);
		datas.add(delete);
		
		
		setCanceledOnTouchOutside(true);
		this.setOnDismissListener(dismissListener);
	}
	private DialogInterface.OnDismissListener dismissListener=new DialogInterface.OnDismissListener(){
		@Override
		public void onDismiss(DialogInterface dialog) {
			if(musicOperate!=null){
				musicOperate.setImageResource(R.drawable.list_item_more_btn_default);//设置 弹出item 收起
				LinearLayout layout=(LinearLayout)musicOperate.getParent();
				layout.setClickable(true);
			}
		}
	};
	/**
	 * 显示时候的事件
	 *//*
	private DialogInterface.OnShowListener onshowListener=new DialogInterface.OnShowListener() {
		
		@Override
		public void onShow(DialogInterface dialog) {
			if(musicOperate!=null){//防止连续点击两次的问题
				LinearLayout layout=(LinearLayout)musicOperate.getParent();
				layout.setClickable(false);
			}
		}
	};*/
	public OperateDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
	}
	
	public long getSongListId() {
		return songListId;
	}
	public void setSongListId(long songListId) {
		this.songListId = songListId;
	}
	/** 设置当前操作音乐对象
	 * @param music MusicInfo 传入当前操作音乐对象
	 *  */
	public void setMusicInfo(MusicInfo music){
		musicInfo=music;
	}
	/**
	 * 当前要删除歌曲的对象所属的集合
	 */
	private List<MusicInfo> musicInfos;
	/**
	 * 当前要删除歌曲的索引在 集合中的 
	 */
	private int index;
	public void setMusicInfos(List<MusicInfo> musicInfos){
		this.musicInfos=musicInfos;
	}
	public void setIndex(int index){
		this.index=index;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.easou_dialog);
		// 设置弹窗标题
		
		datas =new ArrayList<OprateItem>();
		OprateItem add = new OprateItem();
		add.setShowImage(context.getResources().getDrawable(R.drawable.dialog_add_icon_img));
		add.setShowText(context.getResources().getString(R.string.dialog_add));
		screen_Height = Env.getScreenHeight();
		
		OprateItem delete = new OprateItem();
		delete.setShowImage(context.getResources().getDrawable(R.drawable.dialog_delete_icon_img));
		delete.setShowText(context.getResources().getString(R.string.dialog_delete));
		OprateItem share = new OprateItem();
		share.setShowImage(context.getResources().getDrawable(R.drawable.dialog_share_icon_img));
		share.setShowText(context.getResources().getString(R.string.dialog_share));
		OprateItem fav = new OprateItem();
		if(musicInfo == null)
			return;
		if(musicInfo.getDateAddedFav()>0)
			fav.setShowImage(context.getResources().getDrawable(R.drawable.dialog_added_fav_icon_img));
		else
			fav.setShowImage(context.getResources().getDrawable(R.drawable.dialog_fav_icon_img));
		fav.setShowText(context.getResources().getString(R.string.dialog_fav));
		
		datas.add(fav);
		datas.add(add);
		datas.add(share);
		datas.add(delete);
		
		((TextView) findViewById(R.id.titleTxt)).setText(musicInfos.get(index).getTitle());
		((TextView) findViewById(R.id.titleTxt)).setSingleLine();
		listView = (ListView) findViewById(R.id.operate_lv);
		listView.setVisibility(View.VISIBLE);
		// 隐藏对话框正文内容
		findViewById(R.id.normal_container).setVisibility(View.GONE);
		findViewById(R.id.okContainer).setVisibility(View.GONE);
		findViewById(R.id.checklist_container).setVisibility(View.GONE);
		findViewById(R.id.okAndCancelContainer).setVisibility(View.GONE);
		findViewById(R.id.okAndMoreAndCancelContainer).setVisibility(View.GONE);
		
		OperateListAdapter operateAdapter = new OperateListAdapter(context,datas);
		
		listView.setAdapter(operateAdapter);	
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				//添加到列表
				case 1:
					AddToDialog.Builder cr = new AddToDialog.Builder(context, musicInfo);
					cr.setHideSongListId(songListId);//显示要隐藏的 歌单
					cr.create().show();
					dismiss();
					break;
				//删除歌曲
				case 3:
					dismiss();	
					EasouDialog.Builder cb = new EasouDialog.Builder(context);
			        cb.setTitle(context.getString(R.string.local_batch_edit_music_delete) + "\'" + musicInfo.getTitle() + "\'")
			        .setMessage("")
			        .setCheckBox(true)
			        .setCkBoxMessage(context.getString(R.string.delete_local_file))
			        .setLayout(R.layout.easou_dialog)
			        .setPositiveButton("确定", new DialogInterface.OnClickListener() 
			        {
			        	public void onClick(DialogInterface dialog, int which) {
			        		boolean isChecked = EasouDialog.getChecked();
			        		//如果是我的最爱下面的歌曲 要把关系也删除
							if(context.getString(R.string.fav).equals(TianlApp.currentPlayPath)){//我的最爱里面的删除的话 就是 取消搜藏
								MusicInfo info=musicInfos.get(index);
								boolean flag= MusicOperate.newInstance().removeMusicInFavlist(info, false);
								if(flag){
									Toast.makeText(context, context.getString(R.string.song_delete_succuess),
											Toast.LENGTH_SHORT).show();
								}
								if(isChecked){
									MusicOperate.newInstance().deleteMusicInLocal(musicInfos,new int[]{index}, true);
								}
								dialog.dismiss();
								return ;
							}
							if(isChecked)
							{
								MusicOperate.newInstance().deleteMusicInLocal(musicInfos,new int[]{index}, true);
							}
							else {
								//逻辑删除，根据歌曲id和歌单id来删除记录
								if(songListId != -1){
									MusicOperate.newInstance().deleteMusicInSonglist(songListId, musicInfos, new int[]{index}, false);
								}else{
									MusicOperate.newInstance().deleteMusicInLocal(musicInfos,new int[]{index}, false);
								}
							}
							Toast.makeText(context, context.getString(R.string.song_delete_succuess),
									Toast.LENGTH_SHORT).show();
							// 刷新界面
							//TODO Activity不能参与业务逻辑处理
							DownloadedActivity.delete(musicInfos.get(index));
							dialog.dismiss();
			        	}
			        }).setNegativeButton("取消", new DialogInterface.OnClickListener() { 
			        	@Override
			        	public void onClick(DialogInterface dialog, int which) {
			        		dialog.dismiss();
			        	}
			        });
			        Dialog dl = cb.create();
			        dl.show();
					break;
				//分享
				case 2:
					dismiss();
					EasouShareDialog easou_share_dialog = new EasouShareDialog(context, R.style.easouDialog,musicInfo.getArtist()+"  "+musicInfo.getTitle(),musicInfo.getLocalUrl());
					easou_share_dialog.show();			
					break;
				//我的最爱
				case 0:
					dismiss();
					List<MusicInfo> musics = LocalMusicManager.getInstence().getMusicListByMusicID(musicInfo.getId());
					if(musics!=null&&musics.size()>0){
						if(musics.get(0).getDateAddedFav()>0){//取消收藏
							musicInfo.setDateAddedFav(0);
							boolean flag=LocalMusicManager.getInstence().updateMusic(musicInfo);
							if(flag){
								Toast.makeText(context, R.string.cancel_love, Toast.LENGTH_SHORT).show();
							}
						}else{//收藏
							musicInfo.setDateAddedFav(System.currentTimeMillis());
							boolean flag=LocalMusicManager.getInstence().updateMusic(musicInfo);
							if(flag){
								Toast.makeText(context, R.string.love_success, Toast.LENGTH_SHORT).show();	
							}
						}
						if (musicInfo != null
								&& PlayLogicManager.newInstance().getmCurMusic() != null) {
							if (musicInfo.equals(PlayLogicManager.newInstance()
									.getmCurMusic())) {
								PlayLogicManager.newInstance().setmCurMusic(
										musicInfo);
							}
						}
					}
					// 如果在我的最爱里面点击 取消喜欢 就 要刷新 列表 Easou.currentPlayPath=getString(R.string.fav);
					//获取 adapater然后刷新
					if(context.getString(R.string.fav).equals(TianlApp.currentPlayPath)){//我的最爱里面
						musicInfos.remove(index);
						adapter.notifyDataSetChanged();
					}
					break;
				}
				
			}
		});
		//重定义界面布局
		Window dialogWindow = getWindow();
		WindowManager m = ((Activity) context).getWindowManager();
		Display d = m.getDefaultDisplay();
		WindowManager.LayoutParams p = dialogWindow.getAttributes();
//		dialogWindow.setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,   
//				p.FLAG_BLUR_BEHIND);  

		p.width = (int) (d.getWidth() * 0.7); // 宽度设置为屏幕的0.75
		dialogWindow.setAttributes(p);
		setCanceledOnTouchOutside(true);
		this.setOnDismissListener(dismissListener);

	}
	private MusicListAdapter adapter;
	
	public void setAdapter(MusicListAdapter adapter) {
		this.adapter = adapter;
	}
	/** 列表操作类对象适配器
	 * @author Erica
	 *  */
	public class OperateListAdapter extends BaseAdapter{

		private Context mContext;
		private List<OprateItem> datas;		
		public OperateListAdapter(Context context,List<OprateItem> datas)
		{
			mContext=context;
			this.datas = datas;
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return datas.size();
		}

		@Override
		public OprateItem getItem(int position) {
			// TODO Auto-generated method stub
			return datas.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			ItemView itemView = null;
			if(convertView == null)
			{
				
				convertView = LayoutInflater.from(mContext).inflate(R.layout.oprate_listitem, null);
				convertView.setLayoutParams(new ListView.LayoutParams(LayoutParams.FILL_PARENT,screen_Height/10));//重点行
				itemView = new ItemView();
				itemView.txtMenu = (TextView)convertView.findViewById(R.id.menuitem);
				itemView.imageMenu =  (ImageView)convertView.findViewById(R.id.menuitemimage);
				convertView.setTag(itemView);
			}
			else
			{
				itemView = (ItemView)convertView.getTag();
			}
			
			itemView.imageMenu.setBackgroundDrawable(datas.get(position).getShowImage());
			itemView.txtMenu.setText(datas.get(position).getShowText());
			
			
			return convertView;
		}
		
		private class ItemView 
		{
			private ImageView imageMenu;
			private TextView txtMenu;
		}
		
	}
	private ImageView musicOperate;//音乐item 的箭头框
	/**
	 * 
	 */
	public void setImageView(ImageView imageButton){
		this.musicOperate=imageButton;
	}
/*	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){//弹出dilaog按下返回键的时候 
			if(musicOperate!=null){
				musicOperate.setImageResource(R.drawable.list_item_more_btn_default);//设置 弹出item 收起
			}
		}
		return super.onKeyDown(keyCode, event);
	}*/
	
	
	public static class OprateItem{
		Drawable showImage;
		String showText;
		public Drawable getShowImage() {
			return showImage;
		}
		public void setShowImage(Drawable showImage) {
			this.showImage = showImage;
		}
		public String getShowText() {
			return showText;
		}
		public void setShowText(String showText) {
			this.showText = showText;
		}
		
	}
}
