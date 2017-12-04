package com.tiantiankuyin.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.tiantiankuyin.utils.CommonUtils;
import com.tiantiankuyin.R;

/**
 * 弹出常规对话框
 * 
 * @author Erica
 * @note 使用 EasouDialog.Builder eb = new EasouDialog.Builder(this);
 *       eb.setTitle(“”) .setMessage(“”) .setCheckBox(true) .setCkBoxMessage(“”)
 *       .setPositiveButton(“”,new DialogInterface.OnClickListener())...
 * */
public class EasouDialog extends Dialog {

	/** 是否有checkbox */
	private static boolean hasCheckConfirm = false;
	/** 是否有EditBox输入框 */
	private static boolean hasEditBox = false;
	/** 是否有RadioButton */
	private static boolean hasRadioButton = false;
	/** EditBox的输入框字数长度 */
	private static int editMaxLength = 10;
	/** 是否有progressBar */
	private static boolean hasProgressBar = false;
	/** 中部编辑框对象 */
	private static EditText editBox;
	/** check选框 */
	private static CheckBox checkBox;
	/** RadioGroup选框 */
	private static RadioGroup radioGroup;
	/** RadioButton选框 */
	private static RadioButton high_radio;
	/** RadioButton选框 */
	private static RadioButton low_radio;
	/** RadioButton选框Msg */
	private static TextView first_intro_tv;
	/** RadioButton选框Msg */
	private static TextView second_intro_tv;
	/** check选框Msg */
	private static TextView checkBox_tv;
	/** 弹出对话框对象 */
	private static EasouDialog dialog = null;
	/** 用于存放临时对象 */
	// add by DK
	public Object tag;
	public final static int RENAME_SONGLIST_DELETE = 0;
	public static int type = -1;

	/**
	 * 构造函数
	 * 
	 * @author Erica
	 * @param context
	 *            Context 界面操作对象
	 * @param theme
	 *            int 界面弹出框style
	 * */
	public EasouDialog(Context context, int theme) {

		super(context, theme);
	}

	public EasouDialog(Context context, int theme,
			DialogInterface.OnDismissListener dismissListener) {
		super(context, theme);
		this.setOnDismissListener(dismissListener);
	}

	public static RadioGroup getRadioGroup() {
		return radioGroup;
	}

	/**
	 * 基本构造函数
	 * */
	public EasouDialog(Context context) {

		super(context, R.style.easouDialog);

	}

	/** 判断checkbox是否已勾选 */
	public static boolean getChecked() {
		return checkBox.isChecked();
	}

	/** 返回EditBox的内容 */
	public static String getEditValue() {
		if (editBox != null)
			return editBox.getText().toString();
		else
			return "";
	}

	public static EditText getEditText() {
		return editBox;
	}

	public void setParentDismissListener(
			DialogInterface.OnDismissListener dismissListener) {
		setOnDismissListener(dismissListener);
	}

	/**
	 * 对话框绑定对象
	 * 
	 * @author Erica
	 * @note 用于绑定对话框中所需内容
	 * */
	public static class Builder {
		/** 界面组件操作对象 */
		private Context context;
		/** 界面标题 */
		private String title;
		/** 弹出框显示提示字符串 */
		private String message;
		/** 确定按钮 */
		private String positiveButtonText;
		/** 取消按钮 */
		private String negativeButtonText;
		/** 更多按钮 */
		private String moreButtonText;
		/** RadioButton */
		private String radioFirstMessage;
		/** RadioButton */
		private String radioSecondMessage;
		/** RadioButton */
		private String firstIntroMessage;
		/** RadioButton */
		private String secondIntroMessage;
		/** ckBoxMessage */
		private String ckBoxMessage;

		/** 编辑框 */
		private String editTextTip;
		/** 等待框 */
		private String waitMsg;

		// private static DownedItem item;
		/**
		 * 当前EditText的输入格式是否为整型
		 */
		private boolean editTextIntegerMode;

		/**
		 * 当前EditText里的文字默认全选状态。
		 */
		private boolean editTextSelectAllOnFocus;

		/**
		 * 当前EditText默认输入信息
		 */
		private String editTextMessage;

		/**
		 * 当前EditText输入的文字居中
		 */
		private boolean editTextCenter;

		/**
		 * 当前Message的文字居中，大字体
		 */
		private boolean messageCenterAndBigSize;

		private DialogInterface.OnClickListener positiveButtonClickListener,
				negativeButtonClickListener, moreButtonClickListener;
		private RadioGroup.OnCheckedChangeListener radioListener;

		public Builder(Context context) {

			this.context = context;
			hasCheckConfirm = false;
			hasRadioButton = false;
			hasEditBox = false;
			hasProgressBar = false;
			editTextIntegerMode = false;
			editTextMessage = null;
			editTextCenter = false;

		}

		/** 设置check选中与否 */
		public Builder setCheckBox(boolean flag) {
			hasCheckConfirm = flag;
			return this;
		}

		/** 设置RadioButton选中与否 */
		public Builder setRadioButton(boolean flag) {
			hasRadioButton = flag;
			return this;
		}

		// public static DownedItem getItem() {
		// return item;
		// }

		/** 设置是否需要编辑框 */
		public Builder setEditBox(boolean flag) {
			hasEditBox = flag;
			return this;
		}

		/** 设置显示字符串 */
		public Builder setMessage(String message) {

			this.message = message;

			return this;

		}

		private DialogInterface.OnDismissListener dismissListener;

		public void setDismissListener(
				DialogInterface.OnDismissListener dismissListener) {
			this.dismissListener = dismissListener;
		}

		/** 设置显示字符串id */
		public Builder setMessage(int message) {

			this.message = (String) context.getText(message);

			return this;

		}

		/**
		 * 设置Message字体是否居中，大字体。
		 * 
		 * @param flag
		 * @return
		 * @author Erica
		 */
		public Builder setMessageCenterAndBigSize(boolean flag) {

			this.messageCenterAndBigSize = flag;

			return this;

		}

		/** 设置编辑框字符串 */
		public Builder setRadioFirstMessage(String msg) {
			this.radioFirstMessage = msg;
			return this;
		}

		/** 设置编辑框字符串 */
		public Builder setRadioSecondMessage(String msg) {
			this.radioSecondMessage = msg;
			return this;
		}

		/** 设置编辑框字符串 */
		public Builder setFirstIntroMessage(String msg) {
			this.firstIntroMessage = msg;
			return this;
		}

		/** 设置编辑框字符串 */
		public Builder setSecondIntroMessage(String msg) {
			this.secondIntroMessage = msg;
			return this;
		}

		/** 设置编辑框字符串 */
		public Builder setEditTextMessage(String msg) {
			this.editTextMessage = msg;
			return this;
		}

		/** 设置编辑框默认提示字符串 */
		public Builder setEditHitMessage(String tip) {
			this.editTextTip = tip;
			return this;
		}

		/** 设置编辑框最大输入长度 */
		public Builder setEditMaxLength(int length) {
			editMaxLength = length;
			return this;
		}

		/** 设置是否需要进度条 */
		public Builder setProgress(boolean flag) {
			hasProgressBar = flag;
			return this;
		}

		/** 设置等待字符串 */
		public Builder setWaitMsg(String msg) {
			waitMsg = msg;
			return this;
		}

		/** 获取checkbox后字符串内容 */
		public String getCkBoxMessage() {
			return ckBoxMessage;
		}

		/** 设置checkBox字符串内容 */
		public Builder setCkBoxMessage(String ckBoxMessage) {
			this.ckBoxMessage = ckBoxMessage;
			return this;
		}

		/** 设置标题 id */
		public Builder setTitle(int title) {

			this.title = (String) context.getText(title);

			return this;

		}

		/** 设置标题 字符串 */
		public Builder setTitle(String title) {

			this.title = title;

			return this;

		}

		/**
		 * 设置当前输入格式
		 * 
		 * @author Erica
		 */
		public Builder setEditTextIntegerMode(boolean flag) {

			this.editTextIntegerMode = flag;

			return this;
		}

		/**
		 * 设置当前EditText中的文字默认为全选
		 * 
		 * @param flag
		 * @return
		 * @author Erica
		 */
		public Builder setEditTextSelectAllOnFocus(boolean flag) {

			this.editTextSelectAllOnFocus = flag;

			return this;
		}

		/**
		 * 设置当前EditText中的文字是否居中
		 * 
		 * @param flag
		 * @return
		 * @author Erica
		 */
		public Builder setEditTextCenter(boolean flag) {

			this.editTextCenter = flag;

			return this;
		}

		/** 设置当前显示布局id */
		public Builder setLayout(int layout) {
			return this;
		}

		/** 设置确定按钮 */
		public Builder setPositiveButton(int positiveButtonText,
				DialogInterface.OnClickListener listener) {

			this.positiveButtonText = (String) context

			.getText(positiveButtonText);

			this.positiveButtonClickListener = listener;

			return this;

		}

		/** 设置确定按钮 */
		public Builder setPositiveButton(String positiveButtonText,

		DialogInterface.OnClickListener listener) {

			this.positiveButtonText = positiveButtonText;

			this.positiveButtonClickListener = listener;

			return this;

		}

		/** 设置取消按钮 */
		public Builder setNegativeButton(int negativeButtonText,

		DialogInterface.OnClickListener listener) {

			this.negativeButtonText = (String) context

			.getText(negativeButtonText);

			this.negativeButtonClickListener = listener;

			return this;

		}

		/** 设置取消按钮 */
		public Builder setNegativeButton(String negativeButtonText,

		DialogInterface.OnClickListener listener) {

			this.negativeButtonText = negativeButtonText;

			this.negativeButtonClickListener = listener;

			return this;

		}

		/** 设置更多按钮 */
		public Builder setMoreButton(int moreButtonText,

		DialogInterface.OnClickListener listener) {

			this.moreButtonText = (String) context

			.getText(moreButtonText);

			this.moreButtonClickListener = listener;

			return this;

		}

		/** 设置更多按钮 */
		public Builder setMoreButton(String moreButtonText,

		DialogInterface.OnClickListener listener) {

			this.moreButtonText = moreButtonText;

			this.moreButtonClickListener = listener;

			return this;

		}

		/** 设置更多按钮 */
		public Builder setSelectedID(int _id) {
			return this;
		}

		/** 设置更多按钮 */
		public Builder setRadioListener(
				RadioGroup.OnCheckedChangeListener listener) {

			this.radioListener = listener;
			radioGroup.setOnCheckedChangeListener(radioListener);
			return this;

		}

		/** 销毁弹出框 */
		public void dismiss() {
			dialog.dismiss();
		}

		/**
		 * 弹出框初始化
		 * 
		 * @return
		 * @author Erica
		 */
		public EasouDialog create() {
			LayoutInflater inflater = (LayoutInflater) context

			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (dismissListener != null) {
				dialog = new EasouDialog(context, R.style.easouDialog,
						dismissListener);
			} else {
				dialog = new EasouDialog(context, R.style.easouDialog);
			}
			// View layout = inflater.inflate(this.layout, null);
			View layout = inflater.inflate(R.layout.easou_dialog, null);
			// 如果设置了显示checkbox
			if (hasCheckConfirm) {
				layout.findViewById(R.id.contentTxt).setVisibility(View.GONE);
				checkBox = (CheckBox) layout.findViewById(R.id.ck_comfirm);
				LinearLayout check_btn_lay = (LinearLayout) layout
						.findViewById(R.id.check_btn_lay);
				check_btn_lay.setVisibility(View.VISIBLE);
				//2012-10-19 17:57:25   looming 将点击区域扩大到整个文字描述区域
				check_btn_lay.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						checkBox.setChecked(!checkBox.isChecked());
					}
				});

				checkBox_tv = (TextView) layout.findViewById(R.id.check_text);
				checkBox_tv.setText(ckBoxMessage);
			}

			// 如果设置了EditBox
			if (hasEditBox) {
				// 如果编辑框显示了，则消息框需要隐藏,如果设置了message，则显示message内容
				if (this.message != null && !"".equals(this.message))
					layout.findViewById(R.id.contentTxt).setVisibility(
							View.VISIBLE);
				else
					layout.findViewById(R.id.contentTxt).setVisibility(
							View.GONE);
				editBox = (EditText) layout.findViewById(R.id.edit_easoudlg);
				editBox.setVisibility(View.VISIBLE);
				// edittext 对字数的限制
				editBox.addTextChangedListener(new TextWatcher() {
					private CharSequence temp;
					private int selectionStart;
					private int selectionEnd;

					@Override
					public void beforeTextChanged(CharSequence s, int arg1,
							int arg2, int arg3) {
						temp = s;
					}

					@Override
					public void onTextChanged(CharSequence s, int arg1,
							int arg2, int arg3) {
					}

					@Override
					public void afterTextChanged(Editable s) {
						if (type == RENAME_SONGLIST_DELETE) {
							if (s.length() == 0) {
								editBox.setHint(context
										.getString(R.string.songlist_rename_null));
							}
						}
						selectionStart = editBox.getSelectionStart();
						selectionEnd = editBox.getSelectionEnd();
						if (temp.length() > editMaxLength) {
							// Toast.makeText(context,context.getString(R.string.grid_item_nick),Toast.LENGTH_SHORT).show();
							s.delete(selectionStart - 1, selectionEnd);
							int tempSelection = selectionStart;
							editBox.setText(s);
							editBox.setSelection(tempSelection);
						}
					}
				});
				editBox.setHint(this.editTextTip);

				// 设置当前EditText只能输入整数。
				if (editTextIntegerMode) {
					editBox.setKeyListener(new DigitsKeyListener(false, false));
				}

				// 设置当前EditText的输入值。
				if (!("").equals(editTextMessage)) {
					editBox.setText(editTextMessage);
				}

				// 设置当前EditText中的文字为全选状态。
				if (editTextSelectAllOnFocus) {
					editBox.setSelectAllOnFocus(true);
					editBox.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							editBox.clearFocus();
							editBox.requestFocus();
						}
					});
				}

				// 设置当前EditText中的文字居中
				if (editTextCenter) {
					editBox.setGravity(Gravity.CENTER);
				}

			} else
				// 如果编辑框不显示，则消息框需要显示
				layout.findViewById(R.id.contentTxt)
						.setVisibility(View.VISIBLE);

			if (hasRadioButton) {
				radioGroup = (RadioGroup) layout.findViewById(R.id.radioGroup);

				high_radio = (RadioButton) layout
						.findViewById(R.id.high_radioButton);
				low_radio = (RadioButton) layout
						.findViewById(R.id.low_radioButton);
				/*
				 * radio_first_tv =
				 * (TextView)layout.findViewById(R.id.radio_txt_first);
				 */
				/*
				 * radio_second_tv =
				 * (TextView)layout.findViewById(R.id.radio_txt_second);
				 */
				first_intro_tv = (TextView) layout
						.findViewById(R.id.first_intro);
				second_intro_tv = (TextView) layout
						.findViewById(R.id.second_intro);
				high_radio.setText(radioFirstMessage);
				low_radio.setText(radioSecondMessage);
				/* radio_first_tv.setText(radioFirstMessage); */
				/* radio_second_tv.setText(radioSecondMessage); */
				first_intro_tv.setText(firstIntroMessage);
				second_intro_tv.setText(secondIntroMessage);
				/*
				 * switch(radio_id){ case 0: high_radio.setChecked(true);
				 * low_radio.setChecked(false); break; case 1:
				 * high_radio.setChecked(false); low_radio.setChecked(true);
				 * break; }
				 */
				/*
				 * radioGroup.addView(high_radio, 0);
				 * radioGroup.addView(low_radio, 1);
				 */

				radioGroup.setOnCheckedChangeListener(radioListener);
				dialog.setCanceledOnTouchOutside(true);
				layout.findViewById(R.id.contentTxt).setVisibility(View.GONE);
				// 显示进度控件
				layout.findViewById(R.id.prg_container)
						.setVisibility(View.GONE);
				layout.findViewById(R.id.check_btn_lay)
						.setVisibility(View.GONE);
				layout.findViewById(R.id.okContainer).setVisibility(View.GONE);
				layout.findViewById(R.id.okAndCancelContainer).setVisibility(
						View.GONE);
				layout.findViewById(R.id.edit_easoudlg)
						.setVisibility(View.GONE);
				layout.findViewById(R.id.radioBtn_lay).setVisibility(
						View.VISIBLE);
			}
			// 如果设置了progressBar
			if (hasProgressBar) {
				// 如果等待消息不为空的话
				if (waitMsg != null && !"".equals(waitMsg))
					((TextView) layout.findViewById(R.id.prgb_txt))
							.setText(waitMsg);
				// 隐藏文本区域
				layout.findViewById(R.id.contentTxt).setVisibility(View.GONE);
				// 显示进度控件
				layout.findViewById(R.id.prg_container).setVisibility(
						View.VISIBLE);
				layout.findViewById(R.id.okContainer).setVisibility(View.GONE);
				layout.findViewById(R.id.okAndCancelContainer).setVisibility(
						View.GONE);
				layout.findViewById(R.id.edit_easoudlg)
						.setVisibility(View.GONE);

			}

			dialog.addContentView(layout, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

			// 设置标题
			((TextView) layout.findViewById(R.id.titleTxt)).setText(title);

			// 如果对话框只有一个“确定”按钮
			if (positiveButtonText != null && negativeButtonText == null
					&& moreButtonText == null) {
				layout.findViewById(R.id.okAndCancelContainer).setVisibility(
						View.GONE);
				((Button) layout.findViewById(R.id.sigleOkBtn))
						.setText(positiveButtonText);

				if (positiveButtonClickListener != null) {

					((Button) layout.findViewById(R.id.sigleOkBtn))
							.setOnClickListener(new View.OnClickListener() {

								public void onClick(View v) {

									positiveButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_POSITIVE);
								}
							});
				}
			}
			// 如果对话框包含“确定”和“取消”
			else if (positiveButtonText != null && negativeButtonText != null
					&& moreButtonText == null) {
				layout.findViewById(R.id.okContainer).setVisibility(View.GONE);
				((Button) layout.findViewById(R.id.okBtn))
						.setText(positiveButtonText);
				((Button) layout.findViewById(R.id.cancelBtn))
						.setText(negativeButtonText);
				if (positiveButtonClickListener != null) {

					((Button) layout.findViewById(R.id.okBtn))
							.setOnClickListener(new View.OnClickListener() {

								public void onClick(View v) {
									positiveButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_POSITIVE);
								}
							});
				}

				if (negativeButtonClickListener != null) {
					((Button) layout.findViewById(R.id.cancelBtn))
							.setOnClickListener(new View.OnClickListener() {

								public void onClick(View v) {

									negativeButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_NEGATIVE);
								}
							});
				}

				// 当前有3个按钮，包括“确定”、“更多”和“取消”按钮
			} else if (positiveButtonText != null && negativeButtonText != null
					&& moreButtonText != null) {
				layout.findViewById(R.id.okContainer).setVisibility(View.GONE);
				layout.findViewById(R.id.okAndCancelContainer).setVisibility(
						View.GONE);
				layout.findViewById(R.id.okAndMoreAndCancelContainer)
						.setVisibility(View.VISIBLE);
				((Button) layout.findViewById(R.id.moreOkBtn))
						.setText(positiveButtonText);
				((Button) layout.findViewById(R.id.moreCancelBtn))
						.setText(negativeButtonText);
				((Button) layout.findViewById(R.id.moreBtn))
						.setText(moreButtonText);
				if (positiveButtonClickListener != null) {

					((Button) layout.findViewById(R.id.moreOkBtn))
							.setOnClickListener(new View.OnClickListener() {

								public void onClick(View v) {
									positiveButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_POSITIVE);
								}
							});
				}

				if (negativeButtonClickListener != null) {
					((Button) layout.findViewById(R.id.moreCancelBtn))
							.setOnClickListener(new View.OnClickListener() {

								public void onClick(View v) {
									negativeButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_NEGATIVE);
								}
							});
				}

				if (moreButtonClickListener != null) {
					((Button) layout.findViewById(R.id.moreBtn))
							.setOnClickListener(new View.OnClickListener() {

								public void onClick(View v) {
									moreButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_NEUTRAL);
								}
							});
				}
			}

			if (message != null && !"".equals(this.message)) {
				((TextView) layout.findViewById(R.id.contentTxt))
						.setText(message);
			} else {
				((TextView) layout.findViewById(R.id.contentTxt))
						.setVisibility(View.GONE);
			}

			if (messageCenterAndBigSize) {
				((TextView) layout.findViewById(R.id.contentTxt))
						.setGravity(Gravity.CENTER);
				((TextView) layout.findViewById(R.id.contentTxt))
						.setTextSize(CommonUtils.dip2px(context, 18));
			}

			dialog.setContentView(layout);
			/** 做适配，根据屏幕宽度调整对话框的宽度 */
			Window dialogWindow = dialog.getWindow();
			WindowManager m = ((Activity) context).getWindowManager();
			Display d = m.getDefaultDisplay();
			WindowManager.LayoutParams p = dialogWindow.getAttributes();
			// p.height = (int) (d.getHeight() * 0.6);
			p.width = (int) (d.getWidth() * 0.8); // 宽度设置为屏幕的0.95//
			dialogWindow.setAttributes(p);
			/** 做适配end */
			return dialog;
		}

		public void setHighRadioButton() {
			if (high_radio != null && low_radio != null) {
				radioGroup.check(R.id.high_radioButton);
				/*
				 * high_radio.setChecked(true); low_radio.setChecked(false);
				 */
			}
		}

		public void setLowRadioButton() {
			if (high_radio != null && low_radio != null) {
				radioGroup.check(R.id.low_radioButton);
				/*
				 * high_radio.setChecked(false); low_radio.setChecked(true);
				 */
			}
		}
		// public void setItem(DownedItem item2) {
		// // TODO Auto-generated method stub
		// this.item = item;
		// }
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// if(EasouDialog.Builder.getItem()!=null){//吧状态值还原
			// EasouDialog.Builder.getItem().isShowDelDailog=false;
			// }
			// if(MediaButtonDialog.mDialog !=null){
			// MediaButtonDialog.mDialog.cb.dismiss();
			// MediaButtonDialog.mDialog.finish();
			// MediaButtonDialog.mDialog=null;
			// }
			// Contants.isUpdate=false;
		}
		return super.onKeyDown(keyCode, event);
	}

}