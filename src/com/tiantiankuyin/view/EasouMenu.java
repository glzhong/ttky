package com.tiantiankuyin.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tiantiankuyin.bean.Update;
import com.tiantiankuyin.component.activity.BaseActivity;
import com.tiantiankuyin.component.activity.UpdateActivity;
import com.tiantiankuyin.component.activity.local.AboutActivity;
import com.tiantiankuyin.component.activity.local.FeedBackActivity;
import com.tiantiankuyin.database.bll.OnDataPreparedListener;
import com.tiantiankuyin.database.bll.OnlineMusicManager;
import com.tiantiankuyin.para.Env;
import com.tiantiankuyin.para.IntentAction;
import com.tiantiankuyin.para.WebServiceUrl;
import com.tiantiankuyin.utils.Lg;
import com.tiantiankuyin.R;
import com.tiantiankuyin.TianlApp;

/**
 * 物理键弹出自定义菜单
 * 
 * @author Erica
 */
public class EasouMenu implements OnClickListener{
	private Context mContext;
	private PopupWindow popupMenu;
	private ViewGroup menuView;
	private View parentView;

	/**
	 * 菜单设置布局按钮
	 */
	// private RelativeLayout rl_menu_settings;
	/**
	 * 菜单睡眠定时布局按钮
	 */
	private RelativeLayout rl_menu_sleepTimer;
	/**
	 * 菜单意见反馈布局按钮
	 */
	private RelativeLayout rl_menu_feedBack;
	/**
	 * 更换皮肤
	 */
	private RelativeLayout rl_menu_updateSkin;
	/**
	 * 关于我们
	 */
	private RelativeLayout rl_menu_aboutUs;
	/**
	 * 菜单检查更新布局按钮
	 */
	private RelativeLayout rl_menu_checkUpdata;
	/**
	 * 菜单本地扫描布局按钮
	 */
	private RelativeLayout rl_menu_scan;
	/**
	 * 菜单选择布局按钮
	 */
	private RelativeLayout rl_menu_download;
	/**
	 * 菜单退出布局按钮
	 */
	private RelativeLayout rl_menu_exit;//

	/**
	 * 菜单的构造方法
	 * 
	 * @param mContext
	 * @param parent
	 */
	public EasouMenu(Context mContext, View parent) {
		this.mContext = mContext;
		this.parentView = parent;
		initPopupWindwos();
	}

	/**
	 * 在界面底部，弹出菜单
	 */
	public void showMenu() {
		if (popupMenu != null) {
			popupMenu.showAtLocation(this.parentView, Gravity.BOTTOM, 0, 0);
		}
	}

	/**
	 * 菜单是否弹出
	 * 
	 * @return isShowing 返回菜单弹出的状态
	 */
	public boolean isShowing() {
		return popupMenu.isShowing();
	}

	/**
	 * 收起菜单
	 */
	public void dismiss() {
		if (popupMenu.isShowing()) {
			popupMenu.dismiss();
		}
	}

	/**
	 * 初始化菜单
	 */
	private void initPopupWindwos() {

		/*
		 * 初始化菜单各个控件
		 */
		menuView = (ViewGroup) View.inflate(mContext, R.layout.bottom_menu,
				null);
		rl_menu_scan = (RelativeLayout) menuView
				.findViewById(R.id.RelativeLayout_Item1);
		rl_menu_scan.setOnClickListener(this);
		rl_menu_download = (RelativeLayout) menuView
				.findViewById(R.id.RelativeLayout_Item2);
		rl_menu_download.setOnClickListener(this);
		rl_menu_updateSkin = (RelativeLayout) menuView
				.findViewById(R.id.RelativeLayout_Item3);
		rl_menu_updateSkin.setOnClickListener(this);
		rl_menu_feedBack = (RelativeLayout) menuView
				.findViewById(R.id.RelativeLayout_Item4);
		rl_menu_feedBack.setOnClickListener(this);
		rl_menu_aboutUs = (RelativeLayout) menuView
				.findViewById(R.id.RelativeLayout_Item5);//
		rl_menu_aboutUs.setOnClickListener(this);
		rl_menu_checkUpdata = (RelativeLayout) menuView
				.findViewById(R.id.RelativeLayout_Item6);
		rl_menu_checkUpdata.setOnClickListener(this);
		rl_menu_sleepTimer = (RelativeLayout) menuView
				.findViewById(R.id.RelativeLayout_Item7);
		rl_menu_sleepTimer.setOnClickListener(this);
		rl_menu_exit = (RelativeLayout) menuView
				.findViewById(R.id.RelativeLayout_Item8);
		rl_menu_exit.setOnClickListener(this);

		// 初始化popupMenu
		popupMenu = new PopupWindow(menuView, LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);

		popupMenu.setFocusable(true);
		popupMenu.setAnimationStyle(R.style.menushow);
		popupMenu.update();

		// 设置菜单外的地方，点击即收起菜单
		menuView.setFocusableInTouchMode(true);
		menuView.setOnTouchListener(new android.view.View.OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				if (popupMenu.isShowing()) {
					popupMenu.dismiss();
					return true;
				}
				return false;
			}
		});

		// 设置菜单的监听事件。点击菜单键即收起菜单
		menuView.setOnKeyListener(new android.view.View.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((keyCode == KeyEvent.KEYCODE_MENU || keyCode == KeyEvent.KEYCODE_BACK)
						&& popupMenu.isShowing()) {
					popupMenu.dismiss();
					return true;
				}
				return false;
			}
		});
	}

	/*
	 * 菜单各个点击事件
	 */
	@Override
	public void onClick(View v) {
		// 设置
		if (v.getId() == rl_menu_updateSkin.getId()) {
			/*
			 * Toast.makeText(mContext,mContext.getString(R.string.menu_change_skin
			 * ) , Toast.LENGTH_SHORT).show();
			 */
			Intent intent = new Intent();
			intent.setAction(IntentAction.INTENT_LOCAL_SETTING_ACTIVITY);
			mContext.startActivity(intent);
		}
		// 睡眠定时
		else if (v.getId() == rl_menu_sleepTimer.getId()) {
			/*
			 * Toast.makeText(mContext,mContext.getString(R.string.menu_sleep_timer
			 * ) , Toast.LENGTH_SHORT).show();
			 */

			// 当前若设定的睡眠定时大于0，则弹出取消睡眠定时Dialog
			if (TianlApp.newInstance().getSleepTimer().getSleepTimerMinutes() > 0) {
				showCancelSleepTimerDialog();
			} else {// 否则弹出，设置睡眠定时Dialog
				showSetSleepTimerDialog();
			}
		}
		// 意见反馈
		else if (v.getId() == rl_menu_feedBack.getId()) {// 请填写您对悦听的建议。
			/*
			 * Toast.makeText(mContext,mContext.getString(R.string.menu_feedback)
			 * , Toast.LENGTH_SHORT).show();
			 */
			Intent intent = new Intent(mContext, FeedBackActivity.class);
			mContext.startActivity(intent);
		}
		// 检查更新
		else if (v.getId() == rl_menu_checkUpdata.getId()) {
			doUpdate();
		}
		// 本地扫描
		else if (v.getId() == rl_menu_scan.getId()) {
			Intent intent = new Intent();
			intent.setAction(IntentAction.INTENT_LOCAL_SCAN_ACTIVITY);
			intent.putExtra(BaseActivity.INTENT_ACTIVITY_NAME, "ScanActivity");
			mContext.startActivity(intent);
		}
		// 任务管理
		else if (v.getId() == rl_menu_download.getId()) {
			Intent intent = new Intent(IntentAction.INTENT_DOWNLOAD_ACTIVITY);
			mContext.startActivity(intent);
		}
		// 关于我们
		else if (v.getId() == rl_menu_aboutUs.getId()) {
			/*
			 * Toast.makeText(mContext,mContext.getString(R.string.menu_about_us)
			 * , Toast.LENGTH_SHORT).show();
			 */
			Intent intent = new Intent(mContext, AboutActivity.class);
			mContext.startActivity(intent);

		}
		// 退出
		else if (v.getId() == rl_menu_exit.getId()) {
			exitProgram();
		}
		popupMenu.dismiss();
		System.gc();
	}

	private void doUpdate() {
		if (UpdateActivity.isUpdate)
			return;
		OnlineMusicManager.getInstence().getUpdate(mContext,
				new OnDataPreparedListener<Update>() {
					@Override
					public void onDataPrepared(Update data) {
						if (data != null) {	
							if (data.getVersion() != null
									&& Env.getVersion() != null
									&& !data.getVersion().equalsIgnoreCase(
											Env.getVersion())) {
								Intent intent = new Intent(
										IntentAction.INTENT_UPDATE_ACTIVITY);
								intent.putExtra(UpdateActivity.UPDATE_BEAN, data);
								mContext.startActivity(intent);
							}else {//不需要升级
								Toast.makeText(mContext, R.string.disneed_update, 0).show();
								UpdateActivity.isUpdate = false;
							}
						}else {
							Lg.d("getSingerListData() == null");
							Toast.makeText(mContext, R.string.disneed_update, 0).show();
							UpdateActivity.isUpdate = false;
//							Looper.prepare();
//							Toast.makeText(mContext, R.string.error_update, 0).show();
//							Looper.loop();
							return;
						}
					}
				}, WebServiceUrl.UPDATE);
	}

	/**
	 * 显示设置睡眠定时的Dialog
	 * 
	 * @author Erica edit by perry 2012-09-25 14:29:13
	 */
	private void showSetSleepTimerDialog() {
		EasouDialog.Builder eb = new EasouDialog.Builder(mContext);
		eb.setTitle(R.string.menu_sleep_timer);
		eb.setEditBox(true).setCheckBox(false).setEditMaxLength(3)
				.setEditTextIntegerMode(true).setEditTextMessage("30")
				.setEditTextSelectAllOnFocus(true).setEditTextCenter(true);
		eb.setPositiveButton(R.string.dialog_ok,
				new DialogInterface.OnClickListener() {
 
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String inputMinutes = EasouDialog.getEditValue();

						int sleepMinutes = 0;
						if (!("").equals(inputMinutes.trim())) {
							sleepMinutes = Integer.valueOf(inputMinutes.trim());
						}
						if (sleepMinutes <= 0) {
							Toast.makeText(mContext,
									R.string.sleep_timer_wrong_msg,
									Toast.LENGTH_SHORT).show();
						} else {
							TianlApp.newInstance().getSleepTimer()
									.setTimer(sleepMinutes);
							TianlApp.newInstance()
									.showToast(
											TianlApp.newInstance()
													.getString(
															R.string.sleep_timer_toast_msg1)
													+ sleepMinutes
													+ TianlApp.newInstance()
															.getString(
																	R.string.sleep_timer_toast_msg2));
							dialog.dismiss();
						}
					}
				});
		eb.setNegativeButton(R.string.dialog_cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		eb.create().show();
	}

	/**
	 * 显示取消睡眠定时Dialog
	 * 
	 * @author Erica edit by perry 2012-09-25 14:29:13
	 */
	private void showCancelSleepTimerDialog() {
		EasouDialog.Builder eb = new EasouDialog.Builder(mContext);
		eb.setTitle(R.string.sleep_timer_cancel_title);
		eb.setMessage(
				TianlApp.newInstance().getSleepTimer().getSleepTimerMinutes()
						+ mContext.getString(R.string.sleep_timer_cancel_min))
				.setMessageCenterAndBigSize(true);
		eb.setPositiveButton(R.string.sleep_timer_cancel_btn,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						cancelSleepTimer();
						dialog.dismiss();
					}
				});
		eb.setNegativeButton(R.string.dialog_ok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		eb.create().show();
	}

	/**
	 * 取消睡眠定时
	 * 
	 * @author Erica edit by perry 2012-09-25 14:29:13
	 */
	private void cancelSleepTimer() {
		TianlApp.newInstance().getSleepTimer().cancelTimer();
		TianlApp.newInstance().showToast("取消睡眠定时");
	}

	public void exitProgram() {
		final EasouDialog.Builder eb = new EasouDialog.Builder(mContext);
		eb.setTitle("天天酷音");
		eb.setMessage("是否要退出？");
		eb.setCheckBox(false);
		eb.setEditBox(false);
		eb.setPositiveButton("退出", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {				
				TianlApp.newInstance().exit();
			}
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		eb.create().show();
	}

	/**
	 * 取消睡眠定时
	 * 
	 * @author Erica edit by perry 2012-09-25 14:29:13
	 */
	public void finishMusic() {
		Toast.makeText(mContext, "取消睡眠定时", Toast.LENGTH_SHORT).show();
	}
}
