package com.tiantiankuyin.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tiantiankuyin.component.activity.local.PlayViewLrcActivity;
import com.tiantiankuyin.component.activity.local.PlayViewMainActivity;
import com.tiantiankuyin.para.SPHelper;

public class SettingChangeBroadcastReceiver extends BroadcastReceiver{
	@Override
	public void onReceive(Context context, Intent intent) {
		if(PlayViewLrcActivity.instance.mWakeLock==null){
			return ;
		}
		if(SPHelper.newInstance().getLrcScreenOn()){//根据设置需求设置歌词也常亮
			if(PlayViewMainActivity.instance.curPos==2){//在歌词页面才常亮
				PlayViewLrcActivity.instance.mWakeLock.setReferenceCounted(false);
				PlayViewLrcActivity.instance.mWakeLock.acquire();  
			}
		}else{
			if(PlayViewLrcActivity.instance.mWakeLock.isHeld()){
				PlayViewLrcActivity.instance.mWakeLock.release();
			}
		}
	}
}
