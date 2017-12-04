package com.tiantiankuyin.component.activity;

import com.tiantiankuyin.R;

import android.app.Activity;
import android.os.Bundle;

public class TestArt extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainlist);  
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

}
