package com.tiantiankuyin.component.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tiantiankuyin.para.Constant;
import com.tiantiankuyin.para.SPHelper;
import com.tiantiankuyin.utils.CommonUtils;
import com.tiantiankuyin.view.EasouDialog;
import com.tiantiankuyin.R;

/**
 * 设置页
 * 
 * @author andrew
 * 
 */
public class SettingActivity extends Activity implements OnClickListener {
	private View rootView;
	private ImageButton backBtn;// 返回框
	private ImageView qulity_btn;// 音质选择框 音乐品质 1 高品质 0 流畅版
	private ImageView network_playmodel;// 移动网络在线歌曲播放模式 模式 1 省流量 0 标准
	private CheckBox running_back_download;// 始终后台运行预约
	private CheckBox listen_download;// 歌曲边听边存
	private CheckBox lrc_screen_on;// 歌词页屏幕常亮
	private CheckBox auto_download_lrc;// 自动获取歌词
	private CheckBox auto_download_pic;// 自动获取歌手图
	private CheckBox ear_off_pause;// 拔出耳机自动暂停
	private LinearLayout running_back_download_lay;// 始终后台运行预约 外层layout
	private LinearLayout listen_download_lay;// 歌曲边听边存 外层layout
	private LinearLayout lrc_screen_on_lay;// 歌词页屏幕常亮 外层layout
	private LinearLayout auto_download_lrc_lay;// 自动获取歌词 外层layout
	private LinearLayout auto_download_pic_lay;// 自动获取歌手图 外层layout
	private LinearLayout ear_off_pause_lay;// 拔出耳机自动暂停 外层layout
	private TextView quality_tips;// 音质选择提示
	private TextView playmodel_tips;// 移动网络模式
	private final static int QUALITY = 0;// 音乐品质弹出框
	private final static int NETWORK_PLAYMODEL = 1;// 移动网络在线播放模式弹出框
	private int type = -1;// 当前是哪个 类型的
	private LinearLayout quality_layout;// 扩大触屏区域
	private LinearLayout playmodel_layout;// 扩大触屏区域
	private EasouDialog.Builder dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		rootView = LayoutInflater.from(this).inflate(R.layout.setting, null);
		setContentView(rootView);
		CommonUtils.setTitle(rootView, "设置", true, false);
		init();
		initSettingData();// 初始化以前保存的信息
	}

	/**
	 * 初始化选择框
	 */
	private void initSettingData() {
		running_back_download.setChecked(SPHelper.newInstance()
				.getAwaysRuningBackDownload());
		listen_download.setChecked(SPHelper.newInstance().getListenDownload());
		lrc_screen_on.setChecked(SPHelper.newInstance().getLrcScreenOn());
		auto_download_lrc.setChecked(SPHelper.newInstance()
				.getAutoDownloadLrc());
		auto_download_pic.setChecked(SPHelper.newInstance()
				.getAutoDownloadPic());
		ear_off_pause.setChecked(SPHelper.newInstance().getEarOffPause());
		if (SPHelper.newInstance().getQuality() == 1) {// 初始化提示
			quality_tips.setText(getString(R.string.wifi_download_high));// 初始化提示
		} else {
			quality_tips.setText(getString(R.string.wifi_download_fluent));// 初始化提示
		}
		if (SPHelper.newInstance().getNetWorkPlayModel() == 1) {// 初始化提示
			playmodel_tips
					.setText(getString(R.string.online_music_playmodel_traffic));// 初始化提示
		} else {
			playmodel_tips
					.setText(getString(R.string.online_music_playmodel_standrd));// 初始化提示
		}
	}

	private void init() {
		quality_layout = (LinearLayout) findViewById(R.id.quality_layout);
		playmodel_layout = (LinearLayout) findViewById(R.id.playmodel_layout);
		quality_layout.setOnClickListener(listener);
		playmodel_layout.setOnClickListener(listener);

		backBtn = (ImageButton) findViewById(R.id.back_btn);
		network_playmodel = (ImageView) findViewById(R.id.network_playmodel);
		qulity_btn = (ImageView) findViewById(R.id.qulity_btn);
		running_back_download = (CheckBox) findViewById(R.id.running_back_download);
		listen_download = (CheckBox) findViewById(R.id.listen_download);
		lrc_screen_on = (CheckBox) findViewById(R.id.lrc_screen_on);
		lrc_screen_on.setOnCheckedChangeListener(checkedChangeListener);
		auto_download_lrc = (CheckBox) findViewById(R.id.auto_download_lrc);
		auto_download_pic = (CheckBox) findViewById(R.id.auto_download_pic);
		ear_off_pause = (CheckBox) findViewById(R.id.ear_off_pause);

		running_back_download_lay = (LinearLayout) findViewById(R.id.running_back_download_lay);
		listen_download_lay = (LinearLayout) findViewById(R.id.listen_download_lay);
		lrc_screen_on_lay = (LinearLayout) findViewById(R.id.lrc_screen_on_lay);
		auto_download_lrc_lay = (LinearLayout) findViewById(R.id.auto_download_lrc_lay);
		auto_download_pic_lay = (LinearLayout) findViewById(R.id.auto_download_pic_lay);
		ear_off_pause_lay = (LinearLayout) findViewById(R.id.ear_off_pause_lay);
		
		running_back_download_lay.setOnClickListener(this);
		listen_download_lay.setOnClickListener(this);
		lrc_screen_on_lay.setOnClickListener(this);
		auto_download_lrc_lay.setOnClickListener(this);
		auto_download_pic_lay.setOnClickListener(this);
		ear_off_pause_lay.setOnClickListener(this);
		
		/* qulity_btn.setOnClickListener(listener); */
		backBtn.setOnClickListener(listener);
		/* network_playmodel.setOnClickListener(listener); */
		quality_tips = (TextView) findViewById(R.id.quality_tips);
		playmodel_tips = (TextView) findViewById(R.id.playmodel_tips);
	}

	private CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			SPHelper.newInstance().setLrcScreenOn(isChecked);
			Intent intent = new Intent(
					Constant.SETTING_CHANGE_BROADCAST); // 对应setAction()
			sendBroadcast(intent);
		}
	};
	private View.OnClickListener listener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.back_btn) {
				save();
			} else if (v.getId() == R.id.quality_layout) {
				qulity_btn
						.setImageResource(R.drawable.list_item_more_btn_press);// 设置箭头向上
				showSettingDialog(QUALITY);
			} else if (v.getId() == R.id.playmodel_layout) {
				network_playmodel
						.setImageResource(R.drawable.list_item_more_btn_press);// 设置箭头向上
				showSettingDialog(NETWORK_PLAYMODEL);
			}
		}
	};
	private RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			if (dialog != null) {
				dialog.dismiss();// 隐藏弹出框
			}
			if (checkedId == R.id.high_radioButton) {// 高品质
				if (type == QUALITY) {// 音乐品质的弹出框
					SPHelper.newInstance().setQuality(1);// 保存数据
					quality_tips
							.setText(getString(R.string.wifi_download_high));// 初始化提示
				} else {
					SPHelper.newInstance().setNetWorkPlayModel(1);// 保存数据
					playmodel_tips
							.setText(getString(R.string.online_music_playmodel_traffic));// 初始化提示
				}
			} else {// 流畅版
				if (type == QUALITY) {// 音乐品质的弹出框
					SPHelper.newInstance().setQuality(0);// 保存数据
					quality_tips
							.setText(getString(R.string.wifi_download_fluent));// 初始化提示
				} else {
					SPHelper.newInstance().setNetWorkPlayModel(0);// 保存数据
					playmodel_tips
							.setText(getString(R.string.online_music_playmodel_standrd));// 初始化提示
				}
			}
		}
	};
	private DialogInterface.OnDismissListener dismissListener = new DialogInterface.OnDismissListener() {
		@Override
		public void onDismiss(DialogInterface dialog) {
			qulity_btn.setImageResource(R.drawable.list_item_more_btn_default);// 设置箭头向下
			network_playmodel
					.setImageResource(R.drawable.list_item_more_btn_default);// 设置箭头向下
		}
	};

	private void showSettingDialog(int type) {
		this.type = type;
		dialog = new EasouDialog.Builder(this);
		dialog.setDismissListener(dismissListener);
		dialog.setRadioButton(true);
		if (type == QUALITY) {
			dialog.setTitle(R.string.wifi_download_title);
			dialog.setRadioFirstMessage(getString(R.string.wifi_download_high));
			dialog.setRadioSecondMessage(getString(R.string.wifi_download_fluent));
			dialog.setFirstIntroMessage(getString(R.string.wifi_download_high_value));
			dialog.setSecondIntroMessage(getString(R.string.wifi_download_fluent_value));
		} else {
			dialog.setTitle(R.string.online_music_playmodel_title);
			dialog.setRadioFirstMessage(getString(R.string.online_music_playmodel_traffic));
			dialog.setRadioSecondMessage(getString(R.string.online_music_playmodel_standrd));
			dialog.setFirstIntroMessage(getString(R.string.online_music_playmodel_traffic_desc));
			dialog.setSecondIntroMessage(getString(R.string.online_music_playmodel_standrd_desc));
		}
		dialog.create().show();
		if (type == QUALITY) {
			if (SPHelper.newInstance().getQuality() == 1) {
				if (EasouDialog.getRadioGroup() != null) {
					dialog.setHighRadioButton();
				}
			} else {
				if (EasouDialog.getRadioGroup() != null) {
					dialog.setLowRadioButton();
				}
			}
		} else {
			if (SPHelper.newInstance().getNetWorkPlayModel() == 1) {
				if (EasouDialog.getRadioGroup() != null) {
					dialog.setHighRadioButton();
				}
			} else {
				if (EasouDialog.getRadioGroup() != null) {
					dialog.setLowRadioButton();
				}
			}
		}
		dialog.setRadioListener(onCheckedChangeListener);
	}

	private void save() {
		SPHelper.newInstance().setAwaysRuningBackDownload(
				running_back_download.isChecked());
		SPHelper.newInstance().setListenDownload(listen_download.isChecked());
		SPHelper.newInstance()
				.setAutoDownloadLrc(auto_download_lrc.isChecked());
		SPHelper.newInstance()
				.setAutoDownloadPic(auto_download_pic.isChecked());
		SPHelper.newInstance().setEarOffPause(ear_off_pause.isChecked());
		Toast.makeText(this, "设置保存成功!", Toast.LENGTH_SHORT).show();
		finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			save();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.running_back_download_lay:
			running_back_download
					.setChecked(!running_back_download.isChecked());
			break;
		case R.id.listen_download_lay:
			listen_download.setChecked(!listen_download.isChecked());
			break;
		case R.id.lrc_screen_on_lay:
			lrc_screen_on.setChecked(!lrc_screen_on.isChecked());
			break;
		case R.id.auto_download_lrc_lay:
			auto_download_lrc.setChecked(!auto_download_lrc.isChecked());
			break;
		case R.id.auto_download_pic_lay:
			auto_download_pic.setChecked(!auto_download_pic.isChecked());
			break;
		case R.id.ear_off_pause_lay:
			ear_off_pause.setChecked(!ear_off_pause.isChecked());
			break;

		default:
			break;
		}

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
