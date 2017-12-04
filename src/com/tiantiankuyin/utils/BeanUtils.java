package com.tiantiankuyin.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.cmsc.cmmusic.common.data.MusicInfo;
import com.tiantiankuyin.bean.OlSongVO;

public class BeanUtils {

	public static List<OlSongVO> convenrtMusicInfoTOOlSongVO(
			List<MusicInfo> musics) {
		if(null!=musics && musics.size()>0){
			List<OlSongVO> result = new ArrayList<OlSongVO>();
			for (MusicInfo musicInfo : musics) {
				OlSongVO olSongVO = new OlSongVO();
				olSongVO.setGid(musicInfo.getMusicId());
				olSongVO.setSong(musicInfo.getSongName());
				olSongVO.setSinger(musicInfo.getSingerName());
				olSongVO.setCrbtListenDir(musicInfo.getCrbtListenDir());
				olSongVO.setRingListenDir(musicInfo.getRingListenDir());
				olSongVO.setSongListenDir(musicInfo.getSongListenDir());
				olSongVO.setHasDolby(musicInfo.getHasDolby());
				result.add(olSongVO);
			}
			return result;
		}else{
			return Collections.EMPTY_LIST;
		}
	}
}
