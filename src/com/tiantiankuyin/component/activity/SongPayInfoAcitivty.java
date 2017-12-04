package com.tiantiankuyin.component.activity;


import com.tiantiankuyin.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class SongPayInfoAcitivty extends Activity {
private TextView songTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.songs_payinfo_dialog);
		songTextView=(TextView) findViewById(R.id.song_name_text);
		Intent intent=getIntent();
		String songName=intent.getStringExtra("songName");
		if(songName.length()>0){
			songTextView.setText(songName);
		}
		
	}

}
