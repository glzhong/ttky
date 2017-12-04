package com.tiantiankuyin.view;

import com.tiantiankuyin.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

public class SongPayInfoDialog extends Dialog {
	public SongPayInfoDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.songs_payinfo_dialog);
	}

}