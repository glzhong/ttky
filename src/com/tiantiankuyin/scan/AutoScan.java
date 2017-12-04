package com.tiantiankuyin.scan;

import java.util.List;

import android.content.Context;

import com.tiantiankuyin.bean.MusicInfo;
import com.tiantiankuyin.database.bll.LocalMusicManager;
import com.tiantiankuyin.utils.Lg;

/**
 * 自动扫描
 * 
 * @author Perry
 * 
 */
public class AutoScan {

	private Context context;

	
	public AutoScan(Context context) throws Exception {
		this.context = context;
	}
	
	
	public boolean isNeedInsert(){
		boolean isNeedInsert = true;
		
		List<MusicInfo> musicInfos = LocalMusicManager.getInstence().getAllMusic();
		
		if(musicInfos != null && musicInfos.size() > 0) {
			isNeedInsert = false;
		}
		
		return isNeedInsert;
	}

	/**
	 * 爬取系统媒体库中的信息，并插入到宜搜音乐数据库中。
	 * @return
	 * @author Perry	
	 */
	public boolean scanAndInsert() {
		boolean isSuccess = false;

		List<MusicInfo> musicInfos = ScanUtil.scanMediaStore(context);
		if (musicInfos == null) {
			return true;
		}
		Lg.d("test", "musicInfos.size =" + musicInfos.size());
		LocalMusicManager.getInstence().addMusic(musicInfos);
		Lg.d("test", "执行插入操作 isSucces" + isSuccess);
		
		return isSuccess;
	}

}
