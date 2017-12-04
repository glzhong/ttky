package com.tiantiankuyin.component.activity.local;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tiantiankuyin.adapter.BatchEditMusicAdapter;
import com.tiantiankuyin.adapter.BatchEditMusicAdapter.ViewHolder;
import com.tiantiankuyin.bean.MusicInfo;
import com.tiantiankuyin.para.IntentAction;
import com.tiantiankuyin.utils.CommonUtils;
import com.tiantiankuyin.utils.MusicOperate;
import com.tiantiankuyin.view.AddToDialog;
import com.tiantiankuyin.view.EasouDialog;
import com.tiantiankuyin.R;
import com.tiantiankuyin.TianlApp;

public class BatchEditMusicActivity extends Activity {

	public static final String KEY_SONG_LIST_ID = "songListID";// 批量操作传递的歌单ID
	public static final String KEY_TITLE = "title"; // 批量操作传递的列表名称
	public static final String KEY_IS_ADD_TO = "isAddTo";// 当前是否为添加到功能。
	public static final String KEY_IS_FAV_LIST = "isFavList";//当前是否为我的最爱列表。
	
	public final int MSG_SHOW_WAIT_DIALOG = 1;//显示等待框
	public final int MSG_DISMISS_WAIT_DIALOG = 2;//显示等待框
	
	// 外部传进来的Bundle
	private Bundle bundle;

	private ImageButton topBackIbtn;
	private TextView topTitleTv;

	// 头部全选框
	private CheckBox topCheckAllCb;

	private ListView listView;
	private BatchEditMusicAdapter adapter;

	private LinearLayout editBottonLayout; // 底部批量编辑布局
	private Button addToBtn;
	private Button deleteBtn;

	private LinearLayout addToBottonLayout;// 底部添加到布局
	private Button okBtn;
	private Button cancelBtn;

	/** 外部传进来的MusicInfos */ 
	private List<MusicInfo> musicInfos;

	/** 将要处理的歌单ID */ 
	private long songListID;

	/** 当前是否为添加到布局 */ 
	private boolean isAddToLayout = false;
	
	/** 当前列表是否为我的最爱 歌曲列表。 */
	private boolean isFavList = false;

	/** 当前界面的标题 */ 
	private String title;

	/** 头部视图 */ 
	private static View rootView;

	/** 等待框  */
	private Dialog waitDialog;


	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case MSG_SHOW_WAIT_DIALOG:
				//执行插入歌单之前，弹出等待框
				EasouDialog.Builder cb = new EasouDialog.Builder(BatchEditMusicActivity.this);
				cb.setTitle(R.string.addto_dialog_waiting).setLayout(R.layout.easou_dialog)
						.setProgress(true)
						.setTitle(R.string.addto_dialog_wait_title)
						.setWaitMsg(
								BatchEditMusicActivity.this.getString(R.string.addto_dialog_wait_message));
				waitDialog = cb.create();
				waitDialog.show();
				break;

			case MSG_DISMISS_WAIT_DIALOG:
				//执行插入歌单之后，收起等待框
				waitDialog.dismiss();
				//提示插入成功
				Toast.makeText(
						BatchEditMusicActivity.this,
						BatchEditMusicActivity.this.getResources().getString(
								R.string.add_to_songlist_success),
						Toast.LENGTH_SHORT).show();
				break;
			}

		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		rootView = LayoutInflater.from(this).inflate(
				R.layout.local_batch_edit_music, null);
		setContentView(rootView);

		findView();
		initDatas();
	}

	private void findView() {

		topBackIbtn = (ImageButton) rootView.findViewById(R.id.back_btn);
		topTitleTv = (TextView) findViewById(R.id.title_text);

		topCheckAllCb = (CheckBox) findViewById(R.id.local_batch_edit_music_checkAll);
		listView = (ListView) findViewById(R.id.local_batch_edit_music_listview);

		editBottonLayout = (LinearLayout) findViewById(R.id.local_batch_edit_layout);
		addToBtn = (Button) findViewById(R.id.local_batch_edit_music_addTo);
		deleteBtn = (Button) findViewById(R.id.local_batch_edit_music_delete);

		addToBottonLayout = (LinearLayout) findViewById(R.id.local_batch_addto_layout);
		okBtn = (Button) findViewById(R.id.local_batch_edit_music_ok);
		cancelBtn = (Button) findViewById(R.id.local_batch_edit_music_cancel);
	}

	private void initDatas() {
		bundle = getIntent().getExtras();
		title = bundle.getString(BatchEditMusicActivity.KEY_TITLE);// 标题
		songListID = bundle
				.getLong(BatchEditMusicActivity.KEY_SONG_LIST_ID, -1); // 默认为-1。若当前列表不是歌单，则为-1；否则为对应的歌单ID。
		isAddToLayout = bundle.getBoolean(BatchEditMusicActivity.KEY_IS_ADD_TO,
				false); // 默认为false。
		isFavList = bundle.getBoolean(BatchEditMusicActivity.KEY_IS_FAV_LIST, false);//默认为非 我的最爱歌单
		if (!isAddToLayout) {
			editBottonLayout.setVisibility(View.VISIBLE);
			addToBottonLayout.setVisibility(View.GONE);
		} else {
			editBottonLayout.setVisibility(View.GONE);
			addToBottonLayout.setVisibility(View.VISIBLE);
			TianlApp.newInstance().setCurrentSonglistID(songListID);
		}

		CommonUtils.setTitle(rootView, title, true, false);
		topTitleTv.setText(title);

		topCheckAllCb.setOnClickListener(listener);

		musicInfos = new ArrayList<MusicInfo>();
		musicInfos.addAll(TianlApp.newInstance().getBatchEditMusicInfos());
		adapter = new BatchEditMusicAdapter(this, musicInfos, topCheckAllCb,
				addToBtn, deleteBtn, okBtn);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(listItemClickListener);

		topBackIbtn.setOnClickListener(listener);

		addToBtn.setOnClickListener(listener);
		addToBtn.setEnabled(false);
		deleteBtn.setOnClickListener(listener);
		deleteBtn.setEnabled(false);

		okBtn.setOnClickListener(listener);
		okBtn.setEnabled(false);
		cancelBtn.setOnClickListener(listener);
		cancelBtn.setEnabled(true);
	}

	private OnItemClickListener listItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			ViewHolder holder = (ViewHolder) view.getTag();
			holder.checkBox.toggle();
			// 如果任何一个item没有勾选，且全部勾选的状态为勾选了，那么取消全部勾选的状态
			if (!holder.checkBox.isChecked()) {
				if (topCheckAllCb.isChecked())
					topCheckAllCb.setChecked(false);
			}

			BatchEditMusicAdapter.isSelected.put(position,
					holder.checkBox.isChecked());

			// 如果item都勾选了，全选checkbox应该勾选上
			if (isCheckedAll())
				topCheckAllCb.setChecked(true);

			if (isSelectedMusic()) {
				addToBtn.setEnabled(true);
				deleteBtn.setEnabled(true);
				okBtn.setEnabled(true);
			} else {
				addToBtn.setEnabled(false);
				deleteBtn.setEnabled(false);
				okBtn.setEnabled(false);
			}
		}
	};

	private View.OnClickListener listener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.local_batch_edit_music_addTo) {// 添加到
				if (musicInfos != null && musicInfos.size() > 0) {
					showAddToDialog();
				}
			} else if (v.getId() == R.id.local_batch_edit_music_delete) {// 删除
				if (musicInfos != null && musicInfos.size() > 0) {
					showDeleteConfirmDialog();
				}
			} else if (v.getId() == R.id.local_batch_edit_music_checkAll) {
				if (topCheckAllCb.isChecked()) {
					// 全选
					adapter.setCheckAll();
				} else {
					// 全不选
					adapter.setCheckNone();
				}
				if (isSelectedMusic()) {
					addToBtn.setEnabled(true);
					deleteBtn.setEnabled(true);
					okBtn.setEnabled(true);
				} else {
					addToBtn.setEnabled(false);
					deleteBtn.setEnabled(false);
					okBtn.setEnabled(false);
				}
			} else if (v.getId() == R.id.back_btn) {// 头部返回键
				BatchEditMusicActivity.this.finish();
			} else if (v.getId() == R.id.local_batch_edit_music_ok){//确定
				if (musicInfos != null && musicInfos.size() > 0) {
					new Thread(){
						@Override
						public void run() {
							handler.sendEmptyMessage(MSG_SHOW_WAIT_DIALOG);
							MusicOperate.newInstance().addMusicToSongList(songListID, getSelectedMusicList());
							BatchEditMusicActivity.this.sendBroadcast(new Intent(IntentAction.INTENT_BROADCAST_UPDATE_MUSIC_LIST));
							BatchEditMusicActivity.this.sendBroadcast(new Intent(IntentAction.INTENT_BROADCAST_UPDATE_LOCAL_ACTIVITY));
							BatchEditMusicActivity.this.sendBroadcast(new Intent(IntentAction.INTENT_BROADCAST_SHOW_CURRENT_SONGLIST));
							handler.sendEmptyMessage(MSG_DISMISS_WAIT_DIALOG);
							BatchEditMusicActivity.this.finish();
						};
					}.start();
				}
				
			}else if (v.getId() == R.id.local_batch_edit_music_cancel){//取消
				BatchEditMusicActivity.this.finish();
			}
		}
	};

	/** 展示添加到 弹窗 */
	private void showAddToDialog() {

		List<MusicInfo> selectedMusicInfos = getSelectedMusicList();
		AddToDialog.Builder builder = new AddToDialog.Builder(this,
				selectedMusicInfos);
		builder.setHideSongListId(songListID);
		builder.create().show();
	}
	/** 展示删除 弹窗 */ 
	private void showDeleteConfirmDialog() {
		final int[] index = getSelectedIndexs();
		EasouDialog.Builder builder = new EasouDialog.Builder(
				BatchEditMusicActivity.this);
		builder.setCheckBox(true);
		builder.setTitle("删除确认");
		builder.setMessage("");
		builder.setCkBoxMessage("同时删除源文件");
		builder.setPositiveButton("确定", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(isFavList){//当前的列表是 我的最爱列表
					if(EasouDialog.getChecked()){//物理删除歌曲
						MusicOperate.newInstance().deleteMusicInLocal(musicInfos,
								index, true);
					}else {//逻辑删除歌曲（即取消收藏状态。）
						MusicOperate.newInstance().removeMusicsInFavlist(
								musicInfos, index, false);
					}
				}else if (songListID < 0) {//删除非歌单 本地 全部歌曲 专辑等
					MusicOperate.newInstance().deleteMusicInLocal(musicInfos,
							index, EasouDialog.getChecked());
				} else {//删除歌单
					MusicOperate.newInstance().deleteMusicInSonglist(
							songListID, musicInfos, index,
							EasouDialog.getChecked());
				}
				// 刷新列表
				refreshList(getSelectedMusicList());
				
				// 取消全选
				topCheckAllCb.setChecked(false);

				// 若全部删除后，则改变当前按钮状态。
				if (isSelectedMusic()) {
					addToBtn.setEnabled(true);
					deleteBtn.setEnabled(true);
					okBtn.setEnabled(true);
				} else {
					addToBtn.setEnabled(false);
					deleteBtn.setEnabled(false);
					okBtn.setEnabled(false);
				}
				
				dialog.dismiss();
			}
		});

		builder.setNegativeButton("取消", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	/** 判断当前item项是否全部勾选了  */ 
	private boolean isCheckedAll() {
		boolean result = true;
		Map<Integer, Boolean> map = BatchEditMusicAdapter.isSelected;
		Iterator it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			if ((Boolean) entry.getValue() == false) {
				result = false;
				break;
			}
		}
		return result;
	}

	/** 检查是否已经勾选了 */ 
	private boolean isSelectedMusic() {
		boolean isSelected = false;
		Iterator it = BatchEditMusicAdapter.isSelected.entrySet().iterator();

		while (it.hasNext()) {
			Map.Entry<Integer, Boolean> entry = (Map.Entry<Integer, Boolean>) it
					.next();
			// 如果打钩了
			if (entry.getValue() == true) {
				isSelected = true;
				break;
			}
		}

		return isSelected;
	}

	/** 得到选择歌曲的bean集合 */ 
	private List<MusicInfo> getSelectedMusicList() {
		List<MusicInfo> resultList = new ArrayList<MusicInfo>();
		Iterator it = BatchEditMusicAdapter.isSelected.entrySet().iterator();

		while (it.hasNext()) {
			Map.Entry<Integer, Boolean> entry = (Map.Entry<Integer, Boolean>) it
					.next();
			// 如果打钩了
			if (entry.getValue() == true) {
				// 拿到打钩的pos
				int pos = entry.getKey();
				// 所选中歌曲的对象bean
				resultList.add(musicInfos.get(pos));
			}

		}
		return resultList;
	}

	/** 得到选择歌曲的bean集合 */ 
	private int[] getSelectedIndexs() {
		List<Integer> resultIndexList = new ArrayList<Integer>();
		Iterator it = BatchEditMusicAdapter.isSelected.entrySet().iterator();

		while (it.hasNext()) {
			Map.Entry<Integer, Boolean> entry = (Map.Entry<Integer, Boolean>) it
					.next();
			// 如果打钩了
			if (entry.getValue() == true) {
				// 拿到打钩的pos
				int pos = entry.getKey();
				// 所选中歌曲的对象bean
				resultIndexList.add(pos);
			}

		}

		int[] resultIndex = new int[resultIndexList.size()];
		for (int i = 0; i < resultIndexList.size(); i++) {
			resultIndex[i] = resultIndexList.get(i);
		}

		return resultIndex;
	}

	/**
	 * 执行删除操作后，刷新界面
	 * 
	 * @param removeList
	 *            传入要删除的列表。
	 * @author Perry
	 */
	private void refreshList(List<MusicInfo> removeList) {
		//
		adapter.removeSelected(removeList);
//		adapter = new BatchEditMusicAdapter(this, musicInfos, topCheckAllCb,
//				addToBtn, deleteBtn, okBtn);
		// 全不选
		adapter.setCheckNone();
		adapter.notifyDataSetChanged();
		//listView.setAdapter(adapter);
	}
	
	@Override
	public void onResume() {
	    super.onResume();
		//友盟统计 日志 add by perry 2012-10-23
	   // MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
	    super.onPause();
		//友盟统计 日志 add by perry 2012-10-23
	   // MobclickAgent.onPause(this);
	}
}
