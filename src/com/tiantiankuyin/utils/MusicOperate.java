package com.tiantiankuyin.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;

import com.tiantiankuyin.bean.MusicInfo;
import com.tiantiankuyin.bean.RelateInfo;
import com.tiantiankuyin.bean.SongListInfo;
import com.tiantiankuyin.component.activity.local.LocalActivity;
import com.tiantiankuyin.database.bll.LocalMusicManager;
import com.tiantiankuyin.para.Constant;
import com.tiantiankuyin.para.IntentAction;
import com.tiantiankuyin.para.UserData;
import com.tiantiankuyin.play.PlayLogicManager;
import com.tiantiankuyin.TianlApp;

/**
 * 专门操作歌曲的新增 与删除逻辑 类。
 * 
 * @author Perry
 * 
 */
//TODO 此类中的操作方法，可能涉及数据库或io,如在UI中调用相关方法，需单开线程
public class MusicOperate {

	private static MusicOperate musicOperate; // 歌曲操作的实例

	private Context context;

	private MusicOperate(Context context) {
		this.context = context;
	}

	public static MusicOperate newInstance() {
		if (musicOperate == null) {
			synchronized (MusicOperate.class) {
				if (musicOperate == null) {
					musicOperate = new MusicOperate(TianlApp.newInstance());
				}
			}
		}
		return musicOperate;
	}


	/**
	 * 对本地歌曲进行删除操作。删除操作可逻辑删除，也可物理删除。
	 * 
	 * @param musicList
	 *            当前操作歌曲列表
	 * @param index
	 *            要删除歌曲的索引
	 * @param isDeleteSourceFile
	 *            是否要进行物理删除。
	 * @return 操作是否成功。
	 * @author Perry
	 */
	public boolean deleteMusicInLocal(List<MusicInfo> musicList, int[] index,
			boolean isDeleteSourceFile) {
		boolean isSuccess = false;
		// 通知播放逻辑，调度播放逻辑，准备删除歌曲
		PlayLogicManager.newInstance().recieveDeleteDataChange(musicList, index);

		/* 进行数据库删除。 */

		// 将要删除的音乐列表
		List<MusicInfo> deleteMusicList = new ArrayList<MusicInfo>();
		// 将要删除的音乐ID集合
		List<String> deleteMusicIDList = new ArrayList<String>();
		// 获取将要删除的歌曲。
		for (int i : index) {
			deleteMusicList.add(musicList.get(i));
			deleteMusicIDList.add(String.valueOf(musicList.get(i).getId()));
		}

		// 根据将要删除的音乐列表，删除数据库中的数据。
		String[] deleteMusicIDs = deleteMusicIDList
				.toArray(new String[deleteMusicIDList.size()]);
		LocalMusicManager.getInstence().deleteMusic(deleteMusicIDs);

		// 根据isDeteSoruceFile 执行物理删除。
		if (isDeleteSourceFile) {
			for (MusicInfo musicInfo : deleteMusicList) {
				File file = new File(musicInfo.getLocalUrl());
				if (file.exists()) {
					file.delete();
				}
			}
		}

		// 通知播放逻辑，调度播放逻辑，删除逻辑完成。
		PlayLogicManager.newInstance().setCurMusicListInfoAfterDelete();

		// 发送广播，更新界面。
		context.sendBroadcast(new Intent(
				IntentAction.INTENT_BROADCAST_UPDATE_MUSIC_LIST));

		isSuccess = true;
		return isSuccess;
	}

	/**
	 * 添加歌曲到本地。
	 * 
	 * @param musicList
	 *            将要添加到宜搜音乐数据的歌曲信息集合
	 * @param size
	 *            歌曲数量
	 * @return 操作是否成功
	 * @author Perry
	 */
	public boolean addMusicToLocal(List<MusicInfo> musicList) {

		boolean isSuccess = false;
		
		// 调度播放逻辑，准备新增歌曲
		PlayLogicManager.newInstance().recieveAddDataChange();

		/* 添加歌曲到数据库中 */
		isSuccess = LocalMusicManager.getInstence().addMusic(musicList);


		// 调度播放逻辑，新增逻辑完成。
		PlayLogicManager.newInstance().setCurMusicListInfoAfterAdd();

		// 发送广播，更新界面。
		context.sendBroadcast(new Intent(
				IntentAction.INTENT_BROADCAST_UPDATE_MUSIC_LIST));

		// 更新最近添加的列表
		refreshRecentList();
		
		return isSuccess;
	}

	/**
	 * 添加歌曲到本地。
	 * 
	 * @param musicInfo
	 *            将要添加到宜搜音乐数据的歌曲信息
	 * @return 操作是否成功
	 * @author Perry
	 */
	public boolean addMusicToLocal(MusicInfo musicInfo) {

		boolean isSuccess = false;
		
		// 调度播放逻辑，准备新增歌曲
		PlayLogicManager.newInstance().recieveAddDataChange();

		/* 添加歌曲到数据库中 */
		isSuccess = LocalMusicManager.getInstence().addMusic(musicInfo);

		// 更新最近添加的列表
		refreshRecentList();

		// 调度播放逻辑，新增逻辑完成。
		PlayLogicManager.newInstance().setCurMusicListInfoAfterAdd();

		// 发送广播，更新界面。
		context.sendBroadcast(new Intent(
				IntentAction.INTENT_BROADCAST_UPDATE_MUSIC_LIST));

		return isSuccess;
	}

	/**
	 * 删除歌单中的歌曲
	 * 
	 * @param songListID
	 *            要操作的歌单ID
	 * @param musicList
	 *            当前操作歌曲列表
	 * @param index
	 *            要删除歌曲的索引
	 * @param isDeleteSourceFile
	 *            是否要进行物理删除。
	 * @return 操作是否成功
	 * @author Perry
	 */
	public boolean deleteMusicInSonglist(long songListID,
			List<MusicInfo> musicList, int[] index, boolean isDeleteSourceFile) {
		boolean isSuccess = false;

		// 调度播放逻辑，准备删除歌曲
		PlayLogicManager.newInstance().recieveDeleteDataChange(musicList, index);

		/* 进行数据库删除。 */

		if (isDeleteSourceFile) {
			// 将要删除的音乐列表
			List<MusicInfo> deleteMusicList = new ArrayList<MusicInfo>();
			// 将要删除的音乐ID集合
			List<String> deleteMusicIDList = new ArrayList<String>();
			// 获取将要删除的歌曲。
			for (int i : index) {
				deleteMusicList.add(musicList.get(i));
				deleteMusicIDList.add(String.valueOf(musicList.get(i).getId()));
			}

			// 根据将要删除的音乐列表，删除数据库中的数据。
			String[] deleteMusicIDs = deleteMusicIDList
					.toArray(new String[deleteMusicIDList.size()]);
			isSuccess=LocalMusicManager.getInstence().deleteMusic(deleteMusicIDs);

			// 根据isDeteSoruceFile 执行物理删除。
			for (MusicInfo musicInfo : deleteMusicList) {
				File file = new File(musicInfo.getLocalUrl());
				if (file.exists()) {
					file.delete();
				}
			}
		} else {
			List<RelateInfo> relateInfos = new ArrayList<RelateInfo>();
			for (int i : index) {
				RelateInfo relateInfo = new RelateInfo(0, songListID, musicList
						.get(i).getId(), 0);
				relateInfos.add(relateInfo);
			}
			isSuccess = LocalMusicManager.getInstence().deleteAllRelateDatas(relateInfos);
		}

		// 调度播放逻辑，删除逻辑完成。
		PlayLogicManager.newInstance().setCurMusicListInfoAfterDelete();

		// 发送广播，更新界面。
		context.sendBroadcast(new Intent(
				IntentAction.INTENT_BROADCAST_UPDATE_MUSIC_LIST));

		return isSuccess;

	}
	
	/**
	 * 取消单首歌曲的收藏状态
	 * 
	 * @param musicInfo
	 *            将要要取消收藏的歌曲MusicInfo
	 * @param isDeleteSourceFile
	 *            是否物理删除。
	 * @return
	 * @author Perry
	 */
	public boolean removeMusicInFavlist(MusicInfo musicInfo, boolean isDeleteSourceFile) {
		boolean isSuccess = false;

		List<MusicInfo> musicList = new ArrayList<MusicInfo>();
		musicList.add(musicInfo);
		int[] index = {0};
		isSuccess = removeMusicsInFavlist(musicList, index, isDeleteSourceFile);
		
		return isSuccess;
	}

	
	/**
	 * 批量取消歌曲的收藏状态
	 * @param musicList
	 *            当前操作歌曲列表
	 * @param index
	 *            要取消歌曲的索引
	 * @param isDeleteSourceFile
	 *            是否要进行物理删除。
	 * @return 操作是否成功
	 * @author Perry
	 */
	public boolean removeMusicsInFavlist(
			List<MusicInfo> musicList, int[] index, boolean isDeleteSourceFile) {
		boolean isSuccess = false;

		// 调度播放逻辑，准备删除歌曲
		PlayLogicManager.newInstance().recieveDeleteDataChange(musicList, index);

		/* 进行数据库删除。 */
		if (isDeleteSourceFile) {
			// 将要删除的音乐列表
			List<MusicInfo> deleteMusicList = new ArrayList<MusicInfo>();
			// 将要删除的音乐ID集合
			List<String> deleteMusicIDList = new ArrayList<String>();
			// 获取将要删除的歌曲。
			for (int i : index) {
				deleteMusicList.add(musicList.get(i));
				deleteMusicIDList.add(String.valueOf(musicList.get(i).getId()));
			}

			// 根据将要删除的音乐列表，删除数据库中的数据。
			String[] deleteMusicIDs = deleteMusicIDList
					.toArray(new String[deleteMusicIDList.size()]);
			isSuccess = LocalMusicManager.getInstence().deleteMusic(deleteMusicIDs);

			// 根据isDeteSoruceFile 执行物理删除。
			for (MusicInfo musicInfo : deleteMusicList) {
				File file = new File(musicInfo.getLocalUrl());
				if (file.exists()) {
					file.delete();
				}
			}
		} else {
			//批量取消收藏逻辑
			List<MusicInfo> removeFavInfos = new ArrayList<MusicInfo>();
			for(int i : index){
				MusicInfo info = musicList.get(i);
				info.setDateAddedFav(0);
				removeFavInfos.add(info);
			}
			isSuccess = LocalMusicManager.getInstence().updateMusic(removeFavInfos);
		}

		// 调度播放逻辑，删除逻辑完成。
		PlayLogicManager.newInstance().setCurMusicListInfoAfterDelete();

		// 发送广播，更新界面。
		context.sendBroadcast(new Intent(
				IntentAction.INTENT_BROADCAST_UPDATE_MUSIC_LIST));

		return isSuccess;

	}
	
	/**
	 * 新增个歌曲到 某歌单中。
	 * 
	 * @param songListID
	 *            要把歌曲添加到的 歌单 的ID
	 * @param musicList
	 *            将要添加到宜搜音乐数据的歌曲信息集合
	 * @return 操作是否成功
	 * @author Perry
	 */
	public boolean addMusicToSongList(long songListID,
			List<MusicInfo> musicList) {

		boolean isSuccess;
		
		// 调度播放逻辑，准备新增歌曲
		PlayLogicManager.newInstance().recieveAddDataChange();

		/* 添加关系到数据库中 */
		List<RelateInfo> relateInfos = new ArrayList<RelateInfo>();
		for (int i = 0; i < musicList.size(); i++) {
			RelateInfo relateInfo = new RelateInfo(0, songListID, musicList
					.get(i).getId(), 0);
			relateInfos.add(relateInfo);
		}
		isSuccess = LocalMusicManager.getInstence().addRelateDatas(relateInfos);

		// 调度播放逻辑，新增逻辑完成。
		PlayLogicManager.newInstance().setCurMusicListInfoAfterAdd();

		// 发送广播，更新界面。
		context.sendBroadcast(new Intent(
				IntentAction.INTENT_BROADCAST_UPDATE_MUSIC_LIST));

		return isSuccess;
	}

	/**
	 * 新增个歌曲到 某歌单中。
	 * 
	 * @param songListID
	 *            要把歌曲添加到的 歌单 的ID
	 * @param musicInfo
	 *            将要添加到宜搜音乐数据的歌曲信息
	 * @return 操作是否成功
	 * @author Perry
	 */
	public boolean addMusicToSongList(long songListID,
			MusicInfo musicInfo) {

		boolean isSuccess = false;
		
		// 调度播放逻辑，准备新增歌曲
		PlayLogicManager.newInstance().recieveAddDataChange();

		/* 添加关系到数据库中 */
		RelateInfo relateInfo = new RelateInfo(0, songListID, musicInfo.getId(), 0);
		isSuccess = LocalMusicManager.getInstence().addRelateData(relateInfo);

		// 调度播放逻辑，新增逻辑完成。
		PlayLogicManager.newInstance().setCurMusicListInfoAfterAdd();

		// 发送广播，更新界面。
		context.sendBroadcast(new Intent(
				IntentAction.INTENT_BROADCAST_UPDATE_MUSIC_LIST));

		return isSuccess;
	}
	
	/**
	 * 添加歌曲到我的最爱 
	 * @param musicInfo 将要添加到我的最爱的歌曲信息
	 * @return 操作是否成功
	 * @author Perry	
	 */
	public boolean addMusicToFavList(MusicInfo musicInfo) {
		boolean isSuccess = false;

		// 调度播放逻辑，准备新增歌曲
		PlayLogicManager.newInstance().recieveAddDataChange();

		/*修改歌曲的收藏时间*/
		musicInfo.setDateAddedFav(System.currentTimeMillis());
		isSuccess = LocalMusicManager.getInstence().updateMusic(musicInfo);
		
		// 调度播放逻辑，新增逻辑完成。
		PlayLogicManager.newInstance().setCurMusicListInfoAfterAdd();

		// 发送广播，更新界面。
		context.sendBroadcast(new Intent(
				IntentAction.INTENT_BROADCAST_UPDATE_MUSIC_LIST));

		return isSuccess;
	}
	
	/**
	 * 创建歌单包括一系列逻辑
	 * 
	 * @param songlistName
	 *            新建歌单名
	 * @param listInfo
	 *            重命名歌单名
	 * @return 0 已存在 1 重命名成功 2 添加成功
	 */
	public int createSonglist(String songlistName, SongListInfo listInfo,boolean isOpen) {
		// 判断该歌单名称是否已经创建
		List<SongListInfo> songListInfos = LocalMusicManager.getInstence().getSongList();// 查询数据库查询已经创建的歌单
		if (songListInfos != null && songListInfos.size() > 0) {
			for (SongListInfo songListInfo : songListInfos) {
				if (songlistName.equals(songListInfo.getName())) {
					return 0;
				}
			}
		}
		// 判断歌单名称是否 跟 全部歌曲 专辑 歌手 文件夹 等 一样
		if ("全部音乐".equals(songlistName) || "歌手".equals(songlistName)
				|| "专辑".equals(songlistName) || "文件夹".equals(songlistName)
				|| "我的最爱".equals(songlistName) || "最近添加".equals(songlistName)
				|| "新建歌单".equals(songlistName)) {
			return 0;
		}

		if (listInfo != null) {// 重命名歌单
			listInfo.setName(songlistName);
			LocalMusicManager.getInstence().updateSongList(listInfo);
			return 1;
		} else {// 新建歌单
				// 最后执行插入操作
			LocalMusicManager.getInstence().addSongList(songlistName);
			if(isOpen){
				SongListInfo currentSonglist=null;
				List<SongListInfo> songlists=LocalMusicManager.getInstence().getSongList();// 查询数据库查询已经创建的歌单
				if(songlists!=null&&songlists.size()>0){
					for(SongListInfo songlist:songlists){
						if(songlist.getName().equals(songlistName)){
							currentSonglist=songlist;
							break;
						}
					}
				}
				/*if(id>=0){
					openAddSongActivity(id);//打开到添加歌曲界面
				}*/
				//打开到新创建的歌单里面去
				if(LocalActivity.instance!=null){
					if(currentSonglist!=null){
						LocalActivity.instance.openSonglistSubItems(currentSonglist);//到该歌单下	
					}
				}
			}
			return 2;
		}
	}
	/**
	 * 刷新“最近添加”歌单的歌曲。
	 * 
	 * @author Perry
	 */
	private void refreshRecentList() {
		/* 添加歌曲信息到“最近添加”歌单中 */
		
		//获取“最近添加”歌单的ID
		long recentSongListID = LocalMusicManager.getInstence().getRecentSongListId();

		if (recentSongListID == -1) {
			return;
		}

		// 清空 “最近添加”的歌曲
//		String deleteSQL = MusicColumn.RELATE_LIST_LIST_ID + " =  ?";
//		String[] deleteArgs = new String[] { recentSongListID };
//		relateDao.deleteRelateDatasBySQL(deleteSQL, deleteArgs);
		
		List<MusicInfo> recentMusicInfos = LocalMusicManager.getInstence().getMusicListBySongListID(recentSongListID);
		if (recentMusicInfos != null && recentMusicInfos.size() > 0) {
			int[] index = new int[recentMusicInfos.size()];
			for (int i = 0; i < recentMusicInfos.size(); i++) {
				index[i] = i;
			}
			deleteMusicInSonglist(recentSongListID,
					recentMusicInfos, index, false);
		}

		long firstStartTime = UserData.getInstence().getFirstStartTime();
		
		long recentTime = System.currentTimeMillis() / 1000
				- Constant.RECENT_ADD_TIME;
		if (recentTime < firstStartTime) {
			recentTime = firstStartTime;
		}

		List<MusicInfo> musicInfos = LocalMusicManager.getInstence().getLatestAddedMusicList(firstStartTime);
		if (musicInfos == null || musicInfos.size() <= 0) {
			return;
		}

		/* 添加关系到数据库中 */
		List<RelateInfo> relateInfos = new ArrayList<RelateInfo>();
		for (int i = 0; i < musicInfos.size(); i++) {
			RelateInfo relateInfo = new RelateInfo(0,
					Long.valueOf(recentSongListID), musicInfos.get(i).getId(),
					musicInfos.get(i).getDateAdded());
			relateInfos.add(relateInfo);
		}
		
		addMusicToSongList(Long.valueOf(recentSongListID), musicInfos);
//		relateDao.insertRelateDatas(relateInfos);
	}

}
