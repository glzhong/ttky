package com.tiantiankuyin.component.activity;

 
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.tiantiankuyin.adapter.MusicListAdapter;
import com.tiantiankuyin.adapter.MusicListAdapter.MusicHolder;
import com.tiantiankuyin.bean.MusicInfo;
import com.tiantiankuyin.component.activity.local.BatchEditMusicActivity;
import com.tiantiankuyin.database.bll.LocalMusicManager;
import com.tiantiankuyin.database.bll.OnDataPreparedListener;
import com.tiantiankuyin.para.Constant;
import com.tiantiankuyin.para.Env;
import com.tiantiankuyin.para.IntentAction;
import com.tiantiankuyin.para.SPHelper;
import com.tiantiankuyin.play.PlayLogicManager;
import com.tiantiankuyin.utils.CommonUtils;
import com.tiantiankuyin.utils.Lg;
import com.tiantiankuyin.component.activity.BaseActivity;
import com.tiantiankuyin.R;
import com.tiantiankuyin.TianlApp;

/**
 * 音乐列表
 * 
 * @author DK
 * 
 */
public class MusicListActivity extends Activity implements OnClickListener,
		OnItemClickListener {
	public static final String KEY_TITLE = "TITLE_NAME";
	public static final String KEY_BACK_BTN = "BACK_BTN";
	public static final String KEY_EDIT_BTN = "EDIT_BTN";
	public static final String KEY_SQL = "SQL";
	public static final String KEY_ARGS = "ARGS";
	public static final String KEY_BACK_ACTION = "BACK_ACTION";
	public static final String KEY_ACTIVITY_NAME = "ACTIVITY_NAME";
	public static final String KEY_SONG_LIST_ID = "SONG_LIST_ID";//歌单ID add by perry 2012-09-24
	public static final int LOAD_COMPLETE = 0x1;
	public static final int POPUP_WINDOW_DISMISS = 0x2;
	private ListView musicLV;
	private static List<MusicInfo> musicList = new ArrayList<MusicInfo>();
	public static MusicListActivity instance;
	private ImageButton backBtn;
	private ImageButton editBtn;
	public static String sql;
	public static String send_sql;
	private String[] args;
	private MusicListAdapter mAdapter;
	//private MusicDaoImpl dao;
	private static Handler mHandler;
	private String backAction;
	private String activityName;
	private static View rootView;
	private static ImageView imageView;// 动画view
	private LinearLayout layout_title;
	private String titleName;
	private long songListID; //当前若不为歌单，则默认为-1；若为歌单则为歌单ID。 add by perry 2012-09-24 19:48:15
	private LinearLayout tips_layout ;//没歌单的时候的提示容器
	private Button addSong ;//添加歌单的button
	
	private MusicListBroadcast updateUIReceiver;//更新UI接受器
	public MusicListAdapter getmAdapter() {
		return mAdapter;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance=this;
		if (TianlApp.activityBundles == null)
			return;
		Bundle bundle = TianlApp.activityBundles
				.get(IntentAction.INTENT_MUSCI_LIST_ACTIVITY);
		boolean hasBackBtn = bundle.getBoolean(KEY_BACK_BTN);
		boolean hasEditBtn = bundle.getBoolean(KEY_EDIT_BTN);
		titleName = bundle.getString(KEY_TITLE);
		rootView = LayoutInflater.from(this).inflate(R.layout.music_list, null);
		setContentView(rootView);
		CommonUtils.setTitle(rootView, titleName, hasBackBtn, hasEditBtn);
		findView();
		 
	 
		 
	}
	
	public void getMusicList(){
		
		File file=new File(Environment.getExternalStorageDirectory()+"/HighMusic/music/");
		File [] files=file.listFiles();
		if(files!=null){
		for (int i = 0; i < files.length; i++) {
			MusicInfo musicInfo=new MusicInfo();
			musicInfo.setTitle(files[i].getName().substring(0,files[i].getName().indexOf("-")));
			musicInfo.setFolderUrl(Environment.getExternalStorageDirectory()+"/HighMusic/music/");
			musicInfo.setLocalUrl(Environment.getExternalStorageDirectory()+"/HighMusic/music/"+files[i].getName());
			musicInfo.setArtist(files[i].getName().substring(files[i].getName().indexOf("-")+1, files[i].getName().indexOf(".")));
			musicList.add(musicInfo);
		}
		}
		 
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(updateUIReceiver!=null&&updateUIReceiver.isRegister()){
			updateUIReceiver.setRegister(false);
			unregisterReceiver(updateUIReceiver);
		}
		// unregisterReceiver(updateUIReceiver);
	}
	//刚新建的空歌单提示
	private void initNullSonglistTips(){
		musicLV.setVisibility(View.GONE);
		tips_layout.setVisibility(View.VISIBLE);
	}
	private View.OnClickListener listener=new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			openAddSongActivity();
		}
	};
	/**
	 * 打开选歌的界面
	 * @param id
	 */
	private void openAddSongActivity(){
		List<MusicInfo> allMusicInfos = LocalMusicManager.getInstence().getAllMusic();
		if(allMusicInfos == null) {
			allMusicInfos = new ArrayList<MusicInfo>();
		}
		Bundle bundle = new Bundle();
		bundle.putBoolean(BatchEditMusicActivity.KEY_IS_ADD_TO, true);
		bundle.putLong(BatchEditMusicActivity.KEY_SONG_LIST_ID, songListID);
		bundle.putString(BatchEditMusicActivity.KEY_TITLE, "选择歌曲");
		TianlApp.newInstance().setBatchEditMusicInfos(allMusicInfos);
		Intent intent=new Intent();
		intent.setAction(IntentAction.INTENT_LOCAL_BATCH_EDIT_ACTIVITY);
		intent.putExtras(bundle);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
	private void hideSonglistTips(){
		musicLV.setVisibility(View.VISIBLE);
		tips_layout.setVisibility(View.GONE);
	}
	private void init() {
		imageView = (ImageView) findViewById(R.id.button);
		mAdapter = new MusicListAdapter(this);
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case LOAD_COMPLETE:
					if (musicLV != null) {
						if(musicList==null&&songListID>0&&!titleName.equals(Constant.RECENT_NAME)){//刚新建的歌单 过滤掉最近添加
							initNullSonglistTips();
						}else{
							hideSonglistTips();
							getMusicList();
							mAdapter.setDatas(musicList);
							mAdapter.setSongListId(songListID);
							mAdapter.notifyDataSetChanged();	
						}
					}
					break;
				case POPUP_WINDOW_DISMISS:
//					if(pop.isShowing())
//						pop.dismiss();
					break;
				}
			}
		};
		this.musicLV.setAdapter(mAdapter);
		musicLV.setOnItemClickListener(this);
		LocalMusicManager.getInstence().getMusicListBySql(sql, args,this,new OnDataPreparedListener<List<MusicInfo>>() {
			
			@Override
			public void onDataPrepared(List<MusicInfo> data) {		
				musicList.clear();
				if(data!=null)
				musicList.addAll(data);
				mHandler.sendEmptyMessage(LOAD_COMPLETE);
			}
		});

		editBtn.setOnClickListener(this);
		backBtn.setOnClickListener(this);
//		updateUIReceiver = new BroadcastReceiver() {
//			@Override
//			public void onReceive(Context context,final Intent intent) {
//				new Thread() {
//					public void run() {
//						Log.d("test", "UPDATE_MUSIC_LIST  UI");
//						musicList = dao.selectMusicDatasBySQL(sql, args);
//						mHandler.sendEmptyMessage(LOAD_COMPLETE);
//					};
//				}.start();
//			}
//		};
//		Easou.newInstance().registerReceiver(
//				updateUIReceiver,
//				new IntentFilter(
//						IntentAction.INTENT_BROADCAST_UPDATE_MUSIC_LIST));
	}
	@Override
	protected void onResume() {
		initData();
		
		if(updateUIReceiver == null) {
			updateUIReceiver = new MusicListBroadcast(this);
		}
		if (!updateUIReceiver.isRegister()) {
			updateUIReceiver.registBroadcastReceiver();
		}
		super.onResume();
		//友盟统计 日志 add by perry 2012-10-23
	    //MobclickAgent.onResume(this);
	}
	
	@Override
	public void onPause() {
	    super.onPause();
		//友盟统计 日志 add by perry 2012-10-23
	   // MobclickAgent.onPause(this);
	}
	
	private void initData(){
		send_sql = "";
		if (TianlApp.activityBundles == null)
			return;
		Bundle bundle = TianlApp.activityBundles
				.get(IntentAction.INTENT_MUSCI_LIST_ACTIVITY);
		titleName = bundle.getString(KEY_TITLE);
		sql = bundle.getString(KEY_SQL);
		args = bundle.getStringArray(KEY_ARGS);

		if(args!=null&&args.length>0){//修改保存sql语句  10.8
			send_sql = CommonUtils.putArgsIntoSqlString(sql, args);
		}
		if(send_sql == null||send_sql.length()==0){
			send_sql = sql;
		}
		backAction = bundle.getString(KEY_BACK_ACTION);
		activityName = bundle.getString(KEY_ACTIVITY_NAME);
		songListID = bundle.getLong(KEY_SONG_LIST_ID, -1); //获取歌单ID。若无对应值，则为-1；若有值，则为歌单ID 。 add by perry 2012-09-24 19:49:15
		if(songListID>0){
			args=new String[]{String.valueOf(songListID)};
		}
		init();
	}
	private void findView() {
		musicLV = (ListView) findViewById(R.id.musicList);
		musicLV.setVisibility(View.VISIBLE);
		tips_layout=(LinearLayout) findViewById(R.id.tips_layout);
		tips_layout.setVisibility(View.GONE);
		addSong=(Button) findViewById(R.id.addSong);
		addSong.setOnClickListener(listener);
		layout_title = (LinearLayout) findViewById(R.id.layout_title);// 用来撑开整个屏幕的当listview的数据很小的时候
																		// 高度很低
																		// 动画效果不明显
		int screen_W = Env.getScreenWidth();
		int screen_H = Env.getScreenHeight();
		FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) layout_title
				.getLayoutParams();
		params.width = screen_W;
		params.height = screen_H;
		layout_title.setLayoutParams(params);
		backBtn = (ImageButton) findViewById(R.id.back_btn);
		editBtn = (ImageButton) findViewById(R.id.edit_btn);
		musicLV.setFooterDividersEnabled(true);
	}

	/**
	 * 动画监听器 当动画完成的时候 关闭 动画button
	 */
	private static Animation.AnimationListener animationListener = new Animation.AnimationListener() {
		@Override
		public void onAnimationStart(Animation animation) {
		}

		@Override
		public void onAnimationEnd(Animation animation) {
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			imageView.setVisibility(View.INVISIBLE);
		}

	};
	
	private static PopupWindow pop;

	private static void itemClick(View view) {

		try {
				
				MusicHolder hodler = (MusicHolder) view.getTag();
				MusicInfo music = null;// 非空判断经常崩溃 add by andrew monkey
										// 测试这个地方经常崩溃
				if (hodler != null) {
					music = (MusicInfo) hodler.musicID.getTag();
				}
				if (music != null) {
					int indexOf = musicList.indexOf(music);
					
					PlayLogicManager.newInstance().setMusicInfoAndPlay(musicList,
							indexOf, send_sql);
				}
				SPHelper.newInstance().setFoldPath(TianlApp.currentPlayPath);
		} catch (IllegalStateException e) {
			//e.printStackTrace();
		} 
	}

	@Override
	public void onBackPressed() {
		back();
	}

	private void back() {
		Intent intent = new Intent(backAction);
		intent.putExtra(BaseActivity.INTENT_ACTIVITY_NAME, activityName);
		TianlApp.activityBundles.remove(IntentAction.INTENT_MUSCI_LIST_ACTIVITY);
		int level = TianlApp.newInstance().getPageLevel();
		BaseActivity.newInstance().showActivity(intent,
				level - 1 == 0 ? 1 : level - 1);
		if(TianlApp.currentPlayPath!=null&&TianlApp.currentPlayPath.contains("/")){
			int index=TianlApp.currentPlayPath.lastIndexOf("/");
			TianlApp.currentPlayPath=TianlApp.currentPlayPath.substring(0, index);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_btn:
			back();
			break;
		case R.id.edit_btn:
			if (musicList != null && musicList.size() > 0) {
				Lg.d("test", "startActivity");
				Intent intent = new Intent();
				intent.setAction(IntentAction.INTENT_LOCAL_BATCH_EDIT_ACTIVITY);
				TianlApp.newInstance().setBatchEditMusicInfos(musicList);
				intent.putExtra(BatchEditMusicActivity.KEY_TITLE, titleName);
				intent.putExtra(BatchEditMusicActivity.KEY_SONG_LIST_ID,
						songListID);
				if(titleName.equals("我的最爱")){
					intent.putExtra(BatchEditMusicActivity.KEY_IS_FAV_LIST, true);
				}
				startActivity(intent);
			} else {
				Toast.makeText(MusicListActivity.this, "当前没有歌曲",
						Toast.LENGTH_SHORT).show();
			}
			break;
		}
	}

//	/**
//	 * 拦截MENU事件
//	 * 
//	 * @author Erica
//	 */
//	@Override
//	public boolean onMenuOpened(int featureId, Menu menu) {
//		BaseActivity.newInstance().menuOpen(featureId, menu);
//		return false;
//	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		itemClick(view);
	}
	
	public class MusicListBroadcast extends BroadcastReceiver{
		private Context context;
		
		private boolean isRegister = false;
		
		public MusicListBroadcast(Context context){
			this.context = context;
		}
		
		public void registBroadcastReceiver(){
			IntentFilter filter = new IntentFilter();
			filter.addAction(IntentAction.INTENT_BROADCAST_UPDATE_MUSIC_LIST);
			setRegister(true);
			context.registerReceiver(this, filter);
		}
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(IntentAction.INTENT_BROADCAST_UPDATE_MUSIC_LIST.equals(action)){
				Lg.d("test", "UPDATE_MUSIC_LIST  UI" );
				LocalMusicManager.getInstence().getMusicListBySql(sql, args,MusicListActivity.this,new OnDataPreparedListener<List<MusicInfo>>() {
					
					@Override
					public void onDataPrepared(List<MusicInfo> data) {
						musicList.clear();
						if(data != null){
							musicList.addAll(data);
						}
						mHandler.sendEmptyMessage(LOAD_COMPLETE);
					}
				});
			}
		}

		public boolean isRegister() {
			return isRegister;
		}

		public void setRegister(boolean isRegister) {
			this.isRegister = isRegister;
		}
	}
}
