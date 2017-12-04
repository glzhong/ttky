package com.tiantiankuyin.view;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;





/*
import com.android.easou.epay.EpayInit;*/
import com.cmsc.cmmusic.common.CMMusicCallback;
import com.cmsc.cmmusic.common.CPManagerInterface;
import com.cmsc.cmmusic.common.FullSongManagerInterface;
import com.cmsc.cmmusic.common.MusicQueryInterface;
//import com.cmsc.cmmusic.common.RingbackManagerInterface;
import com.cmsc.cmmusic.common.VibrateRingManagerInterface;
import com.cmsc.cmmusic.common.data.DownloadResult;
import com.cmsc.cmmusic.common.data.MusicInfo;
import com.cmsc.cmmusic.common.data.MusicInfoResult;
import com.cmsc.cmmusic.common.data.OrderResult;
import com.cmsc.cmmusic.common.data.Result;
 
import com.tiantiankuyin.bean.OlSongVO;
import com.tiantiankuyin.database.bll.LocalMusicManager;
import com.tiantiankuyin.para.SPHelper;
import com.tiantiankuyin.para.UserData;
import com.tiantiankuyin.utils.CommonUtils;
import com.tiantiankuyin.utils.Lg;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tiantiankuyin.NetRequestHelp;
import com.tiantiankuyin.R;

public class EasouOnlineDialog extends Dialog {
	// 用户私钥
	private static String key = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAMzL/6ToykXf57zwSQLgrEBRy3qeXLuGw+Bt+LmnwOEYBT1C8P/aADYIUsYwA95VdY6QAVhU4ko0A0AuqH/CoYJnSlNhj5bAEre/Tly2AX++gMnKxjhpVCUAF0d/KCtkdJ20npqbWY7U8ton3cRoDSF1DgI7+qin8MayYK9M7iuJAgMBAAECgYAQIKyEJxTuxcAxH9cQE/NcqVRV1qzE08sPHHnu3OgSkdqvxVrHOzqzBprGIrfbRW2ZiXuhoiWj5E6xOjtovEINp8IwbOUpGGB/n+BI0DVwBDvc6/otwsbiJ3fk0MUO95NEg6bhsW9/eqyCDZIQAxolWO4GpKSwN450DVtDO0/LgQJBAPSqsrvffLKxcaE728UleH0+Av79raPI0gPYp/MIhVz0+b4QEBqNrAuI3pO53FcZHD781wv64g8j/fmH79ckNBECQQDWSIQSY13h2VAE+lAbdLHOPOprM6XB/ZAUGecwwvQ4sWM8R1gKlWBsD+rogkWPLzlKVgne7oVbaHUAgdCLahf5AkA0rw0Np3ISiGMPdPQ933OyhEfg4H8jZXrTmTORAaS1/4pHgu30ycziLva+mKb5mk6awZcM3VkQKY/my365tPIBAkASHScs9sFk6h3djdtftsmhCX03erI0Z97aFCZ69L/4WuZsngtPOblg6SeOaBTBOdi58/P5qGgVhgr98/tRDbLhAkBsJq5oG4ISnACBLCFnVipaQGj/H4kII5XzknsSEj8Vdgmp7j7+GjYkKhULDHRENd8R3JwmDZmZAcY0nXKr4prb";// 锟矫伙拷私钥
	// CpID，在我司后台申请
	private static String cpid = "983";
	// 计费ID，在我司后台申请
	private static String appfeeid = "301";
	// 计费金额以分为单位
	private static String feenum = "200";


	public static final String PATH = "/HighMusic/music";
	private static EasouOnlineDialog dialog = null;
	public static String url = null;
	//业务id
	public static String serviceId_one="600967020000006653";
	public static String serviceId_four="600967020000006653";
	public static String serviceId_six="600967020000006603";
	public static String serviceId_eight="600967020000006015";
	public static String serviceId_ten="600967020000006016";
	public EasouOnlineDialog(Context context) {
		super(context, R.style.easouDialog);
	}

	public EasouOnlineDialog(Context context, int theme) {
		super(context, theme);
	}

	public static class Builder {
		private String msg;
		/** 上下文 */
		private Context context;

		/** 在线传过来的Bean */
		private OlSongVO olSongVO;

		private List<View> buttonList;

		private LinearLayout containerLayout;

		// 弹窗取消的监听事件
		private DialogInterface.OnDismissListener dismissListener = null;

		public Builder(Context context, OlSongVO olSongVO) {
			this.context = context;
			this.olSongVO = olSongVO;
		}

		/**
		 * 设置弹窗Dismiss时的监听事件
		 * 
		 * @param dismissListener
		 * @return
		 * @author Perry
		 * @note 修改预约逻辑 10.8 Erica
		 */
		public Builder setOnDismissListener(
				DialogInterface.OnDismissListener dismissListener) {
			this.dismissListener = dismissListener;
			return this;
		}

		public boolean checkIsPay(Context context, String gid) {
			boolean isPay = SPHelper.newInstance().getPayInfo(gid);
			/*if (!isPay) {
				EpayInit.getInstance()
						.pay(context, cpid, appfeeid, feenum, key);
				EpayInit.getInstance().getFeeResult(context);
				System.out
						.println("EpayInit.getInstance().getFeeResult(context)"
								+ EpayInit.getInstance().getFeeResult(context));
				// Easou.toPay(OnlineActivity.mOnlineActivity, gid);
			}*/
			return isPay;
		}

		public EasouOnlineDialog create() {
			
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			dialog = new EasouOnlineDialog(context, R.style.easouDialog);

			View layout = inflater.inflate(R.layout.easou_online_dialog, null);

			dialog.addContentView(layout, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

			// 设置弹窗标题
			((TextView) layout.findViewById(R.id.titleTxt)).setText(olSongVO
					.getSong());

			containerLayout = (LinearLayout) layout
					.findViewById(R.id.online_dialog_container);
			/*MusicInfoResult  infoRes=null;
			if(olSongVO.getCrbtListenDir()==null){
				  infoRes=MusicQueryInterface.getMusicInfoByMusicId(context,olSongVO.getGid());
				
			}
			if(infoRes!=null){
				MusicInfo info=infoRes.getMusicInfo();
				if(info!=null){
					olSongVO.setRingListenDir(info.getRingListenDir());
					olSongVO.setHasDolby(info.getHasDolby());
					olSongVO.setCrbtListenDir(info.getCrbtListenDir());
					olSongVO.setSongListenDir(info.getSongListenDir());
				}
			}*/
			if (StringUtils.isNotEmpty(olSongVO.getServiceId())) {	
				     msg="CP专属包月(6元包)";
				if(olSongVO.getServiceId().equals(EasouOnlineDialog.serviceId_four)){
					 msg="CP专属包月(4元包)";
				}else if(olSongVO.getServiceId().equals(EasouOnlineDialog.serviceId_six)){
					 msg="CP专属包月(6元包)";
				}else if(olSongVO.getServiceId().equals(EasouOnlineDialog.serviceId_eight)){
					 msg="CP专属包月(8元包)";
				}else if(olSongVO.getServiceId().equals(EasouOnlineDialog.serviceId_ten)){
					 msg="CP专属包月(10元包)";
				}
				addButtonView(msg,R.drawable.dialog_download_icon_img,
						new View.OnClickListener() {
							@Override
							public void onClick(View v) {
//								CPManagerInterface.openCpMonth(context.getApplicationContext(),
//										                       olSongVO.getServiceId(),"",
//										                       new CMMusicCallback<OrderResult>() {
//																@Override
//																public void operationResult(
//																		OrderResult result) {
//																	if(null!=result){
//																		String resMsg = result.getResMsg();
//																		if(StringUtils.isEmpty(resMsg)){
//																			resMsg = "已取消";
//																		}
//																		Toast.makeText(
//																				context,
//																				msg+","+resMsg,
//																				Toast.LENGTH_LONG)
//																				.show();
//																	}
//																}
//								                               }
//						        );
								
								CPManagerInterface
								.openCPMonth(
										context.getApplicationContext(),
										olSongVO.getServiceId(),"",
										new CMMusicCallback<OrderResult>() {
											@Override
											public void operationResult(
													OrderResult result) {
												if (null != result) {
													String resMsg = result.getResMsg();
													if(StringUtils.isEmpty(resMsg)){
														resMsg = "已取消";
													}
													Toast.makeText(
															context,
															msg+","+resMsg,
															Toast.LENGTH_LONG)
															.show();
												}
												 
											}
										});
							}
						}, 1);
				

			}
			
			if(StringUtils.isNotEmpty(olSongVO.getGid())){
				addButtonView("手机振铃", 
							  R.drawable.dialog_download_icon_img,
							  new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										VibrateRingManagerInterface.queryVibrateRingDownloadUrl(context, 
												                                                olSongVO.getGid(), 
												                                                new CMMusicCallback<OrderResult>() {
											@Override
											public void operationResult(OrderResult downLoadResult) {
												System.out.println("OrderResult queryVibrateRingDownloadUrl=" + downLoadResult);
												if (null != downLoadResult) {
													url = downLoadResult
															.getDownUrl();
													// url="http://mp3a.9ku.com:1234/mp3/412/411603.mp3";

													if (url != null
															&& !url.equals("")) {
														NetRequestHelp
																.get(url,
																		new AsyncHttpResponseHandler() {

																			@Override
																			public void onSuccess(
																					int arg0,
																					Header[] arg1,
																					byte[] arg2) {

																				try {
																					Toast.makeText(
																							context,
																							"下载完成",
																							Toast.LENGTH_LONG)
																							.show();

																					if (Environment
																							.getExternalStorageState()
																							.equals(Environment.MEDIA_MOUNTED)) {

																						File songFile = new File(
																								Environment
																										.getExternalStorageDirectory()
																										+ PATH
																										+ "/"
																										+ olSongVO
																												.getSong()
																										+ "-"
																										+ olSongVO
																												.getSinger()
																										+ ".mp3");
																						// songFile.setFileType(DownloadType.DOWNLOAD_TYPE_MUSIC);
																						FileOutputStream out = new FileOutputStream(
																								songFile);
																						out.write(arg2);

																					}
																				} catch (Exception e) {
																					e.printStackTrace();
																				}

																			}

																			@Override
																			public void onFailure(
																					int arg0,
																					Header[] arg1,
																				 																byte[] arg2,
																					Throwable arg3) {

																				Toast.makeText(
																						context,
																						"下载失败"
																								+ arg0,
																						Toast.LENGTH_LONG)
																						.show();																					}
																		});

													}
												}
												
											}
										});
		//								VibrateRingManagerInterface
		//										.queryVibrateRingDownloadUrlBySms(
		//												context, olSongVO.getHighId(),
		//												"0", olSongVO.getSong(),
		//												olSongVO.getSinger());
									 
									}
							  }, 
							  4);

			}
		
			// 流畅版的按钮
//			if (olSongVO.getLowId() != null
//					&& olSongVO.getLowId().trim().length() > 0) {
			if (StringUtils.isNotEmpty(olSongVO.getGid()))
				 {
			addButtonView("歌曲下载",
						R.drawable.dialog_download_icon_img,
						new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								System.out.println(context+"================1");
								if(fileExists(olSongVO)){
									Toast.makeText(
											context,
											"本地已存在该歌曲...",
											Toast.LENGTH_SHORT)
											.show();
									dialog.dismiss();
									return;
								}
								
								FullSongManagerInterface
										.getFullSongDownloadUrl(
												context,
												olSongVO.getGid(),
												new CMMusicCallback<OrderResult>() {
													@Override
													public void operationResult(
															final OrderResult downloadResult) {
														System.out.println("OrderResult getFullSongDownloadUrl=" + downloadResult);
														if (null != downloadResult) {
															url = downloadResult
																	.getDownUrl();

															if (url != null
																	&& !url.equals("")) {
																downLoadMusic(url,context,olSongVO);
															}
														}
													}
												});
								
								dialog.dismiss();

							}
						}, 2);
			}

			// 铃声版的按钮
//			if (olSongVO.getRingId() != null
//					&& olSongVO.getRingId().trim().length() > 0) {
			if(StringUtils.isNotEmpty(olSongVO.getGid())){
				/**
				注释彩铃
				addButtonView("彩铃订购",
						R.drawable.dialog_download_icon_img,
						new View.OnClickListener() {

							@Override
							public void onClick(View v) {

								if (CommonUtils.isHasNetwork(context)) {
									final String ringId = olSongVO.getRingId();
									try {
										boolean result = hasSongInLocal(ringId);
										if (result) {
											// 本地已存在
											dialog.dismiss();
											Toast.makeText(context,
													R.string.task_exit,
													Toast.LENGTH_SHORT).show();
										} else {
											
											RingbackManagerInterface.buyRingBack(context,
																				 olSongVO.getGid(), 
													                             new CMMusicCallback<OrderResult>() {
																						@Override
																						public void operationResult(
																								OrderResult result) {
																							System.out.println("OrderResult buyRingBack=" + result);
																							 
																							if(null!=result){
																								String resMsg = result.getResMsg();
																								if(StringUtils.isEmpty(resMsg)){
																									resMsg = "已取消";
																								}
																								Toast.makeText(
																										context,
																										"彩铃订购,"+resMsg,
																										Toast.LENGTH_LONG)
																										.show();
//																								new AlertDialog.Builder(context)
//																								.setTitle("彩铃订购")
//																								.setMessage(result.toString())
//																								.setPositiveButton("确认", null)
//																								.show();
																							}
																						}
																				 } 
											);
											
//											RingbackManagerInterface.buyRingbackBySms(
//													context, ringId,
//													olSongVO.getSong(),
//													olSongVO.getSinger());
										}
									} catch (Exception e) {
										// e.printStackTrace();
									}
								} else {
									Toast.makeText(context,
											R.string.no_network,
											Toast.LENGTH_SHORT).show();
									dialog.dismiss();
								}
							}

						}, 3);
						
											  **/
			}

			// 增加“分享”按钮
			addButtonView("分享", R.drawable.dialog_share_icon_img,
					new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							// 无网络时提示
							boolean hasNetwork = CommonUtils
									.isHasNetwork(context);
							if (!hasNetwork) {
								Toast.makeText(context, R.string.no_network,
										Toast.LENGTH_SHORT).show();
								return;
							}

							EasouShareDialog easou_share_dialog = new EasouShareDialog(
									context, R.style.easouDialog, olSongVO
											.getSong(), null);
							easou_share_dialog.show();
							dialog.dismiss();
						}
					}, 5);

			if (buttonList != null) {
				for (View button : buttonList) {
					// 加按钮
					containerLayout.addView(button);
					// 加分割线
					containerLayout.addView(getSepartatView());
				}
			}
			// 去掉容器底部的分割线
			containerLayout.removeViewAt(containerLayout.getChildCount() - 1);

			dialog.setCanceledOnTouchOutside(true);

			// 设置Dialog Dismiss时的监听事件
			if (dismissListener != null) {
				dialog.setOnDismissListener(dismissListener);
			}

			/** 做适配，根据屏幕宽度调整对话框的宽度 */
			Window dialogWindow = dialog.getWindow();
			WindowManager m = ((Activity) context).getWindowManager();
			Display d = m.getDefaultDisplay();
			WindowManager.LayoutParams p = dialogWindow.getAttributes();
			p.width = (int) (d.getWidth() * 0.75); // 宽度设置为屏幕的0.75//
			dialogWindow.setAttributes(p);

			dialog.setContentView(layout, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			dialog.setOnShowListener(new OnShowListener() {

				@Override
				public void onShow(DialogInterface dialog) {
					if (!UserData.getInstence().isShowedWifiDownloadGuide()) {
						showWifiDownloadGuide();
						UserData.getInstence().setShowedWifiDownloadGuide(true);
					}
				}
			});
			return dialog;
		
		}

		private boolean hasSongInLocal(String fileID) {
			return LocalMusicManager.getInstence().existMusicByFileId(fileID);
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
			// image_separt.setBackgroundColor(context.getResources().getColor(
			// R.color.dialog_item_divider_line_color));
			image_separt
					.setBackgroundResource(R.drawable.list_divider_line_img);
			return image_separt;
		}

		/**
		 * 用于内部添加Button到 buttonList中。
		 * 
		 * @param buttonText
		 * @param listener
		 * @author Perry
		 */
		private void addButtonView(final String buttonText,
				int iconId, View.OnClickListener listener, int _tag) {
			if (buttonList == null) {
				buttonList = new ArrayList<View>();
			}

			View view = LayoutInflater.from(context).inflate(
					R.layout.easou_dilog_button, null);
			ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
			TextView textView = (TextView) view.findViewById(R.id.textView);
			TextView tvFrom = (TextView) view.findViewById(R.id.tvFrom);
			TextView btnFrom = (TextView) view.findViewById(R.id.btnFrom);

			imageView.setImageResource(iconId);
			textView.setText(buttonText);

			/*if (from != null && !from.equals("")) {
				tvFrom.setVisibility(View.VISIBLE);
				tvFrom.setText("来源:" + from);
				btnFrom.setVisibility(View.VISIBLE);
				// String htmlLinkText =
				// String.format("<a href=\"?\"><u>源站</u></a>", from);
				// btnFrom.setText(Html.fromHtml(htmlLinkText));
				// btnFrom.setMovementMethod(LinkMovementMethod.getInstance());
				btnFrom.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						Uri uri = Uri.parse(from);
						Intent intent = new Intent(Intent.ACTION_VIEW, uri);
						// //加下面这句话就是启动系统自带的浏览器打开上面的网址， 不加下面一句话，
						// 如果你有多个浏览器，就会弹出让你选择某一浏览器， 然后改浏览器就会打开该网址...............
						// intent.setClassName("com.android.browser",
						// "com.android.browser.BrowserActivity");
						context.startActivity(intent);
					}
				});
			} else {
				btnFrom.setVisibility(View.GONE);
				tvFrom.setVisibility(View.GONE);
			}*/
			if (listener != null)
				view.setOnClickListener(listener);
			buttonList.add(view);
		}

		private PopupWindow popup;

		private void showWifiDownloadGuide() {

			ViewGroup guideView = (ViewGroup) View.inflate(context,
					R.layout.online_dialog_wifidownload_guide_layout, null);
			// 获取图片的高度
			Drawable drawable = context.getResources().getDrawable(
					R.drawable.wifidownload_guide_msg);
			int intrinsicHeight = drawable.getIntrinsicHeight();
			int intrinsicWidth = drawable.getIntrinsicWidth();

			popup = new PopupWindow(guideView, LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			popup.setBackgroundDrawable(drawable);
			popup.setFocusable(true);
			popup.setBackgroundDrawable(new BitmapDrawable());

			// 设置点击后，收起引导页面
			popup.setTouchInterceptor(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					popup.dismiss();
					return false;
				}
			});
			// 设置宽度为屏幕宽度
			// popup.setWidth(displayWidth);
			popup.setWidth(intrinsicWidth);
			popup.setHeight(intrinsicHeight);
			popup.update();
			View button = null;
			for (View one : buttonList) {
				if (((TextView) one.findViewById(R.id.textView)).getText()
						.equals("预约任务")) {
					button = one;
				}
			}
			if (button != null) {
				// 弹出定位在“预约任务”按钮下。
				// popup.showAsDropDown(button, 0,
				// 0 - popup.getHeight() - button.getHeight() / 2);
				popup.showAsDropDown(button, 0, 0 - button.getHeight() / 2);
			}
		}

	}
	
	private static void downLoadMusic(final String url,final Context context,final OlSongVO olSongVO){
		Toast.makeText(
				context,
				"下载中...",
				Toast.LENGTH_LONG)
				.show();
		NetRequestHelp
		.get(url,
				new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(
							int arg0,
							Header[] arg1,
							byte[] arg2) {

						System.out
								.println(new String(
										arg2));

						try {
							Toast.makeText(
									context,
									"下载完成",
									Toast.LENGTH_LONG)
									.show();

							if (Environment
									.getExternalStorageState()
									.equals(Environment.MEDIA_MOUNTED)) {

								File songFile = new File(
										Environment
												.getExternalStorageDirectory()
												+ PATH
												+ "/"
												+ olSongVO
														.getSong()
												+ "-"
												+ olSongVO
														.getSinger()
												+ ".mp3");
								// songFile.setFileType(DownloadType.DOWNLOAD_TYPE_MUSIC);
								FileOutputStream out = new FileOutputStream(
										songFile);
								out.write(arg2);

							}
						} catch (Exception e) {
							e.printStackTrace();
						}

					}

					@Override
					public void onFailure(
							int arg0,
							Header[] arg1,
						 																byte[] arg2,
							Throwable arg3) {

						Toast.makeText(
								context,
								"下载失败"
										+ arg0,
								Toast.LENGTH_LONG)
								.show();																					}
				});
	}
	
	private static boolean fileExists(OlSongVO olSongVO){
		File songFile = new File(
				Environment
						.getExternalStorageDirectory()
						+ PATH
						+ "/"
						+ olSongVO
								.getSong()
						+ "-"
						+ olSongVO
								.getSinger()
						+ ".mp3");
		return songFile.exists();
	}

}
