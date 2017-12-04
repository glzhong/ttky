package com.tiantiankuyin.view;

import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.tiantiankuyin.bean.MusicInfo;
import com.tiantiankuyin.bean.SongListInfo;
import com.tiantiankuyin.database.bll.LocalMusicManager;
import com.tiantiankuyin.para.Constant;
import com.tiantiankuyin.para.Env;
import com.tiantiankuyin.utils.CommonUtils;
import com.tiantiankuyin.utils.MusicOperate;
import com.tiantiankuyin.R;

/**
 * 添加到 弹窗
 * 
 * @author Erica
 * 
 */
public class AddToDialog extends Dialog {
	
	public AddToDialog(Context context, int theme) {

		super(context, theme);
	}

	public AddToDialog(Context context) {
		super(context);
	}
	
	public static class Builder {
		/** 界面操作对象 */
		private Context context;
		/** CheckBox集合的线性布局 */
		private LinearLayout checkList;
		/** 当前添加队列 */
		private List<MusicInfo> musicInfos;
		/** 当前音乐对象  */
		private MusicInfo musicInfo;

		/** 添加到弹出框 */
		private AddToDialog dialog;
		/** 等待框  */
		private Dialog waitDialog;
		
		/** 需要隐藏某个歌单的ID。默认值为-1*/
		private long hideId = -1;
		private int screen_Height = Env.getScreenHeight();

		private Handler handler = new Handler() {
			@Override
			public void handleMessage(android.os.Message msg) {

				switch (msg.what) {
				case 1:
					//执行插入歌单之前，弹出等待框
					EasouDialog.Builder cb = new EasouDialog.Builder(context);
					cb.setTitle(R.string.addto_dialog_waiting).setLayout(R.layout.easou_dialog)
							.setProgress(true)
							.setTitle(R.string.addto_dialog_wait_title)
							.setWaitMsg(
									context.getString(R.string.addto_dialog_wait_message));
					waitDialog = cb.create();
					waitDialog.show();
					break;

				case 2:
					//执行插入歌单之后，收起等待框
					waitDialog.dismiss();
					//提示插入成功
					Toast.makeText(
							context,
							context.getResources().getString(
									R.string.add_to_songlist_success),
							Toast.LENGTH_SHORT).show();
					break;
				}

			};
		};

		public Builder(Context context, MusicInfo musicInfo) {

			this.context = context;

			this.musicInfo = musicInfo;

		}

		public Builder(Context context, List<MusicInfo> musicInfos) {

			this.context = context;

			this.musicInfos = musicInfos;

		}
		
		/**
		 * 设置需要隐藏的歌单ID
		 * @param hideId
		 * @return
		 */
		public Builder setHideSongListId(long hideId) {
			this.hideId = hideId;
			return this;
		}
		
		/**
		 * 添加到弹出框
		 * @author Erica
		 */
		public AddToDialog create() {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			View layout = inflater.inflate(R.layout.easou_dialog, null);

			dialog = new AddToDialog(context, R.style.easouDialog);

			dialog.addContentView(layout, new LayoutParams(
					android.widget.LinearLayout.LayoutParams.FILL_PARENT,
					android.widget.LinearLayout.LayoutParams.WRAP_CONTENT));

			((TextView) layout.findViewById(R.id.titleTxt))
					.setText(R.string.add_to_songlist);

			// 隐藏对话框正文内容
			layout.findViewById(R.id.normal_container).setVisibility(View.GONE);
			layout.findViewById(R.id.okAndCancelContainer).setVisibility(View.GONE);
			layout.findViewById(R.id.okAndMoreAndCancelContainer).setVisibility(View.GONE);
			layout.findViewById(R.id.okContainer).setVisibility(View.GONE);
			// 显示check list 布局
			ScrollView scrollList = (ScrollView) layout
					.findViewById(R.id.checklist_container);
			scrollList.setVisibility(View.VISIBLE);

			checkList = new LinearLayout(context);
			checkList.setLayoutParams(new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.FILL_PARENT));
			checkList.setOrientation(1);
			/* 查询当前所有歌单，并按需隐藏某一歌单 */
			/* 查询当前所有歌单，并按需隐藏某一歌单 */
			String selection = null;
//			if(hideId != -1) {
//				selection =MusicColumn.SONGLIST_ID + " not in ( " + hideId + " )";
//			}
			
			List<SongListInfo> songlistInfos = LocalMusicManager.getInstence().getSongList();
			if(songlistInfos==null || songlistInfos.size() <= 0){
				layout.findViewById(R.id.normal_container).setVisibility(View.VISIBLE);
				TextView error_msg = (TextView)layout.findViewById(R.id.normal_container).findViewById(R.id.contentTxt);
				error_msg.setText("当前没有歌单！");
			}else{
				for (int i = 0; i < songlistInfos.size(); i++) {
					final Long songlistId = songlistInfos.get(i).getId();
					if((hideId!=-1&&songlistId == this.hideId)||songlistInfos.get(i).getName().equals(Constant.RECENT_NAME))
						continue;
					Button button = new Button(context);
					button.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,screen_Height/10));
					button.setText(songlistInfos.get(i).getName());
					button.setTextColor(context.getResources().getColor(
							R.color.dialog_item_text_tv_color));
					button.setTextSize(18);
					button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dialog_songlist_icon_img, 0, 0, 0);
					button.setBackgroundResource(R.drawable.dialog_item_btn_bg);
					button.setEllipsize(TextUtils.TruncateAt.END);
					button.setSingleLine(true);
					int paddingLeft = CommonUtils.dip2px(context, 40);
					int paddingRight = CommonUtils.dip2px(context, 20);
					int paddingTop = CommonUtils.dip2px(context, 10);
					int paddingBottom = CommonUtils.dip2px(context, 10);
					button.setPadding(paddingLeft, paddingTop, paddingRight,
							paddingBottom);
					button.setClickable(true);
					button.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								onClickSongList(songlistId);
							}
						});
				
					checkList.addView(button);
					checkList.addView(getSepartatView());
				}
			}
			Button button = new Button(context);
			button.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,screen_Height/10));
			button.setText(R.string.add_songlist);
			button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dialog_add_songlist_icon_img, 0, 0, 0);
			button.setBackgroundResource(R.drawable.dialog_item_btn_bg);
			button.setTextColor(context.getResources().getColor(
					R.color.dialog_item_text_tv_color));
			button.setTextSize(18);
			button.setSingleLine(true);
			button.setEllipsize(TextUtils.TruncateAt.END);
			int paddingLeft = CommonUtils.dip2px(context, 40);
			int paddingRight = CommonUtils.dip2px(context, 20);
			int paddingTop = CommonUtils.dip2px(context, 10);
			int paddingBottom = CommonUtils.dip2px(context, 10);
			button.setPadding(paddingLeft, paddingTop, paddingRight,
					paddingBottom);
			button.setOnClickListener(positiveButtonClickListener);
			checkList.addView(button);
	
			scrollList.addView(checkList);
			dialog.setContentView(layout);

			/** 做适配，根据屏幕宽度调整对话框的宽度 */
			Window dialogWindow = dialog.getWindow();
			WindowManager m = ((Activity) context).getWindowManager();
			Display d = m.getDefaultDisplay();
			WindowManager.LayoutParams p = dialogWindow.getAttributes();
			// p.height = (int) (d.getHeight() * 0.6);
			p.width = (int) (d.getWidth() * 0.7); // 宽度设置为屏幕的0.95//
			dialogWindow.setAttributes(p);
			/** 做适配end */
			dialog.setCanceledOnTouchOutside(true);
			return dialog;
		}
		
		/**
		 * 内部调用的 添加按钮间的分割线。
		 * 
		 * @return
		 * @author Perry
		 */
		private ImageView getSepartatView() {
			ImageView image_separt = new ImageView(context);
			image_separt.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, 1));
			image_separt.setBackgroundColor(context.getResources().getColor(
					R.color.dialog_item_divider_line_color));
			return image_separt;
		}
		
		/** 单击list条目触发的操作
		 * @author Erica
		 *  */
		public void onClickSongList(final long songListId){
			// 启动线程执行插入歌单
			new Thread() {
				@Override
				public void run() {
					// 执行插入前，弹出等待窗口
					handler.sendEmptyMessage(1);
					
					//修改添加到歌单逻辑 edit by perry 2012-09-29 17:04:55
					if (musicInfos == null) {// 只传单个musicInfo
						MusicOperate.newInstance().addMusicToSongList(songListId, musicInfo);
					} else {// 只传musicInfos列表
						MusicOperate.newInstance().addMusicToSongList(songListId, musicInfos);
					}
					
					//执行完插入后，收起等待窗口
					handler.sendEmptyMessage(2);
				}
			}.start();

			dialog.dismiss();
		}
		/**
		 * 获取歌单总数
		 * @return
		 */
		private int getSonglistCount(){
			int count=0;
			List<SongListInfo> songlists = LocalMusicManager.getInstence().getSongList();// 查询数据库查询已经创建的歌单
			if(songlists!=null){
				count=songlists.size();
			}
			return  count;
		}
		/**
		 * 确定按钮的响应事件 
		 * @author Erica
		 */
		private View.OnClickListener positiveButtonClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int count=getSonglistCount();
				if(count>=21){
					Toast.makeText(context,context.getString(R.string.songlist_count_exceed), Toast.LENGTH_SHORT).show();
					return ;
				}
				final EasouDialog.Builder eb = new EasouDialog.Builder(context);
				eb.setTitle(context.getString(R.string.add_songlist));
				eb.setEditBox(true)
				.setCheckBox(false)
				.setEditHitMessage(context.getString(R.string.add_songlist_hint));
				
				eb.setPositiveButton(context.getString(R.string.confirm), new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						String editValue = EasouDialog.getEditValue();
						if (editValue != null && editValue.length() > 0) {
							int result=MusicOperate.newInstance().createSonglist(editValue, null,false);
							showTips(result);
							if(result!=0){
								insertMusicbySongListName(editValue);
								eb.dismiss();
							}
							//创建完歌单后，添加歌曲到刚刚新建的歌单 add by perry 2012-09-29 17:04:27
						} else {
							// 歌单名称为空提示
							/*Toast.makeText(context, context.getString(R.string.songlist_null),
									Toast.LENGTH_SHORT).show();*/
							EditText editText=EasouDialog.getEditText();
							editText.setHintTextColor(Color.RED);
						}
						
					}
				})
				.setNegativeButton(context.getString(R.string.dialog_cancel), new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				eb.create().show();
				dialog.dismiss();
			}
		};
		private void showTips(int resultInt){
			String show_tip = "";
			switch(resultInt){
			case 0:
				show_tip = context.getString(R.string.songlist_exsit);
				break;
			case 1:
				show_tip = context.getString(R.string.rename_songlist_success);
				break;
			case 2:
				show_tip =context. getString(R.string.songlist_create_success);
				break;
			}
			// 提示操作
			Toast.makeText(context,show_tip, Toast.LENGTH_SHORT)
					.show();
			// 刷新屏幕
		}
		/**根据歌单名字，把歌曲添加到该歌单 add by perry 2012-09-29 17:03:48 */
		private void insertMusicbySongListName(String songListName) {
			List<SongListInfo> selectSongListDatas = LocalMusicManager.getInstence().getSongList();// 查询数据库查询已经创建的歌单
			for(SongListInfo songListInfo : selectSongListDatas) {
				if(songListName.equals(songListInfo.getName())){
					final long songListId = songListInfo.getId();
					// 启动线程执行插入歌单
					new Thread() {
						@Override
						public void run() {
							// 执行插入前，弹出等待窗口
							handler.sendEmptyMessage(1);
							if (musicInfos == null) {// 只传单个musicInfo
								MusicOperate.newInstance().addMusicToSongList(
										songListId, musicInfo);
							} else {// 只传musicInfos列表
								MusicOperate.newInstance().addMusicToSongList(
										songListId, musicInfos);
							}
							
							// 执行完插入后，收起等待窗口
							handler.sendEmptyMessage(2);
						}
					}.start();
				}
			}
		}
	}
	

}