package com.tiantiankuyin.net;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import android.content.Context;

import com.cmsc.cmmusic.common.CPManagerInterface;
import com.cmsc.cmmusic.common.MusicQueryInterface;
import com.cmsc.cmmusic.common.data.ChartInfo;
import com.cmsc.cmmusic.common.data.ChartListRsp;
import com.cmsc.cmmusic.common.data.MusicInfo;
import com.cmsc.cmmusic.common.data.MusicListRsp;
import com.cmsc.cmmusic.common.data.QueryResult;
import com.tiantiankuyin.bean.PagerBean;
import com.tiantiankuyin.component.activity.local.cache.MusicLocalCache;
import com.tiantiankuyin.component.activity.online.RecommendActivity;
import com.tiantiankuyin.component.activity.online.RecommendEExActivity;
import com.tiantiankuyin.component.activity.online.RecommendExActivity;
import com.tiantiankuyin.para.WebServiceUrl;
import com.google.gson.annotations.Until;

public class CmMusicSearch{
	
	private Map<String,List<String>> bdMapping = new HashMap<String,List<String>>();
	
	private Map<String,String> chartCurrentMapping = new HashMap<String,String>();
	
	private Map<String,PagerBean> bdPageMapping = new HashMap<String,PagerBean>();
	
	private Map<String,Boolean> bdLastPage = new HashMap<String,Boolean>();
	
	private Map<String,Boolean> bdHashNextChart = new HashMap<String,Boolean>();
	
	private int chartPageNumber=1;
	
	private int chartNumberPerPage=30;
	
	private int chartMaxRowCount = 0;
	
	private int chartMaxPageNum = 0;
	
	private static CmMusicSearch cmMusicSearch = new CmMusicSearch();
	
	private CmMusicSearch(){
		
	}

	public static CmMusicSearch getInstance(){
		return cmMusicSearch;
	}

	public int getChartPageNumber() {
		return chartPageNumber;
	}

	public int getChartNumberPerPage() {
		return chartNumberPerPage;
	}

	public boolean isLastPage(String menuName){
		if(null!=bdLastPage.get(menuName)){
			return bdLastPage.get(menuName).booleanValue();
		}
		return false;
	}
	//初始化需要使用到的榜单信息
	@SuppressWarnings("unchecked")
	private void initBDMapping(Context mContext){
		System.currentTimeMillis();
		try {
			bdMapping = (Map<String,List<String>>)NetCache.readCache(WebServiceUrl.CHART_MAPPING_CACHE_KEY);
			chartCurrentMapping = (Map<String,String>)NetCache.readCache(WebServiceUrl.CURRENT_CHART_MAPPING_CACHE_KEY);
			bdHashNextChart = (Map<String,Boolean>)NetCache.readCache(WebServiceUrl.HASH_NEXT_CHART_MAPPING_CACHE_KEY);
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			ChartListRsp chartListRsp = getChartInfo(mContext);
			if(null==chartListRsp || Integer.parseInt(chartListRsp.getResCounter())<1){
				return;
			}
			List<ChartInfo> chartInfos = chartListRsp.getChartInfos();
			int chartInfoSize = chartInfos.size();
			int size = chartInfoSize/3;
			List<String> bd = new ArrayList<String>();
			List<String> tj = new ArrayList<String>();
			List<String> jxj = new ArrayList<String>();
			for (int i=0;i<chartInfoSize;++i) {
				if(0<=(i+1) && (i+1) <= size){
					bd.add(chartInfos.get(i).getChartCode());
				}else if(size<(i+1) && (i+1)<=size*2){
					tj.add(chartInfos.get(i).getChartCode());
				}else if(size*2<(i+1) && (i+1)<=size*3){
					jxj.add(chartInfos.get(i).getChartCode());
				}
			}
			bdMapping.put("bd", bd);
			bdMapping.put("tj", tj);
			bdMapping.put("jxj", jxj);
			chartCurrentMapping.put("bd", bd.get(0));
			chartCurrentMapping.put("tj", tj.get(0));
			chartCurrentMapping.put("jxj", jxj.get(0));
			bdHashNextChart.put("bd", Boolean.valueOf(true));
			bdHashNextChart.put("tj", Boolean.valueOf(true));
			bdHashNextChart.put("jxj", Boolean.valueOf(true));			
			NetCache.saveCache(bdMapping, WebServiceUrl.CHART_MAPPING_CACHE_KEY);
			NetCache.saveCache(chartCurrentMapping,WebServiceUrl.CURRENT_CHART_MAPPING_CACHE_KEY);
			NetCache.saveCache(bdHashNextChart,WebServiceUrl.HASH_NEXT_CHART_MAPPING_CACHE_KEY);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 获取cmmusic榜单信息
	 * @param mContext
	 * @return
	 */
	private ChartListRsp getChartInfo(Context mContext){
		if(getChartPageNumber()>chartMaxPageNum && chartMaxPageNum>0){
			chartPageNumber = 1;
		}
		ChartListRsp chartListRsp = MusicQueryInterface.getChartInfo(mContext, getChartPageNumber(), getChartNumberPerPage());
		String resCounter = chartListRsp.getResCounter();
		if(StringUtils.isNotEmpty(resCounter) && chartMaxRowCount==0){
			chartMaxRowCount = Integer.parseInt(resCounter);
			chartMaxPageNum = (chartMaxRowCount%chartNumberPerPage)==0?chartMaxRowCount/chartNumberPerPage:(chartMaxRowCount/chartNumberPerPage)+1;
		}
		++chartPageNumber;
		return chartListRsp;
	}
	
	/**
	 * 通过榜单编号获取歌曲信息
	 * @param mContext
	 * @param chartCode
	 * @return
	 */
	private MusicListRsp getMusicsByChartId(Context mContext, String chartCode,int pageNumber,int numberPerPage){
		MusicListRsp musicListRsp = MusicQueryInterface.getMusicsByChartId(mContext, chartCode, pageNumber, numberPerPage);
		return musicListRsp;
	}
	
	/**
	 * 获取歌曲信息
	 * @param mContext
	 * @return
	 */
	public List<MusicInfo> getMusicInfos(Context mContext,String musicMenuName){
		try{
			//初始化榜单信息
			if(bdMapping.isEmpty()){
				initBDMapping(mContext);
			}
			
			if(StringUtils.isNotEmpty(chartCurrentMapping.get(musicMenuName))){
				//获取当前菜单对应的榜单编号
				String chartCode = chartCurrentMapping.get(musicMenuName);
				
				//获取榜单当前分页对象
				PagerBean page = bdPageMapping.get(chartCode);
				//如果是首次则初始化分页对象
				if(null==page){
					page = new PagerBean();
				}
				if(StringUtils.equals("tj", musicMenuName) && StringUtils.isNotEmpty(MusicLocalCache.tjPageData.getCountPage())){
					page.setCurrentPageNumber(Integer.parseInt(MusicLocalCache.tjPageData.getThisPage()));
					page.setCountPageNumber(Integer.parseInt(MusicLocalCache.tjPageData.getCountPage()));
				}else if(StringUtils.equals("jxj", musicMenuName) && StringUtils.isNotEmpty(MusicLocalCache.jxjPageData.getCountPage())){
					page.setCurrentPageNumber(Integer.parseInt(MusicLocalCache.jxjPageData.getThisPage()));
					page.setCountPageNumber(Integer.parseInt(MusicLocalCache.jxjPageData.getCountPage()));
				}else if(StringUtils.equals("bd", musicMenuName) && StringUtils.isNotEmpty(MusicLocalCache.bdPageData.getCountPage())){
					page.setCurrentPageNumber(Integer.parseInt(MusicLocalCache.bdPageData.getThisPage()));
					page.setCountPageNumber(Integer.parseInt(MusicLocalCache.bdPageData.getCountPage()));
				}			
				boolean hasNextChart = true;
				boolean isLastPage = false;
				//如果已经是当前榜单的最后一页则获取下一个榜单编码并重新创建分页对象
				if(page.getCountPageNumber()!=0 && page.getCountPageNumber()==page.getCurrentPageNumber()){
					List<String> chartList = bdMapping.get(musicMenuName);
					for(int i=0;i<chartList.size();++i){
						if(StringUtils.equalsIgnoreCase(chartList.get(i), chartCode)&&i<chartList.size()-1){
							chartCode = chartList.get(i+1);
							chartCurrentMapping.put(musicMenuName, chartCode);
							if((i+1)>=chartList.size()-1){
								bdHashNextChart.put(musicMenuName, Boolean.valueOf(false));
							}
							try {
								NetCache.saveCache(chartCurrentMapping,WebServiceUrl.CURRENT_CHART_MAPPING_CACHE_KEY);
								NetCache.saveCache(bdHashNextChart,WebServiceUrl.HASH_NEXT_CHART_MAPPING_CACHE_KEY);
							} catch (Exception e) {
								e.printStackTrace();
							}	
							break;
						}
					}
					page = new PagerBean();
				}
				page.setCurrentPageNumber(page.getCurrentPageNumber()+1);
				//分页查询对应榜单歌曲信息
				MusicListRsp musicListRsp = getMusicsByChartId(mContext,chartCode,page.getCurrentPageNumber(),page.getPageRowNumber());
				//设置榜单对应歌曲总数
				int musicsCounter = 0;
				if(null!=musicListRsp){
					try{
						musicsCounter = Integer.parseInt(musicListRsp.getResCounter());
					}catch(NumberFormatException ex){
						
					}
					
				}else{
					isLastPage = true;
				}
				//设置分页信息
				page.setCountRowNumber(musicsCounter);
				if(page.getCountPageNumber()==page.getCurrentPageNumber() && !bdHashNextChart.get(musicMenuName)){
					isLastPage = true;
				}
				//重新设置榜单分页信息
				bdPageMapping.put(chartCode, page);
				bdLastPage.put(musicMenuName, Boolean.valueOf(isLastPage));
				if(StringUtils.equals("tj", musicMenuName)){
					MusicLocalCache.tjPageData.setLastPage(isLastPage);
					MusicLocalCache.tjPageData.setCountPage(String.valueOf(page.getCountPageNumber()));
					MusicLocalCache.tjPageData.setThisPage(String.valueOf(page.getCurrentPageNumber()));
				}else if(StringUtils.equals("jxj", musicMenuName)){
					MusicLocalCache.jxjPageData.setLastPage(isLastPage);
					MusicLocalCache.jxjPageData.setCountPage(String.valueOf(page.getCountPageNumber()));
					MusicLocalCache.jxjPageData.setThisPage(String.valueOf(page.getCurrentPageNumber()));
				}else if(StringUtils.equals("bd", musicMenuName)){
					MusicLocalCache.bdPageData.setLastPage(isLastPage);
					MusicLocalCache.bdPageData.setCountPage(String.valueOf(page.getCountPageNumber()));
					MusicLocalCache.bdPageData.setThisPage(String.valueOf(page.getCurrentPageNumber()));
				}
				return musicListRsp.getMusics();
			}else{
				return Collections.EMPTY_LIST;
			}			
		}catch(Exception ex){
			return Collections.EMPTY_LIST;
		}
	}
	
	public static QueryResult queryCPMonth(Context mContext, String serviceId){
		return CPManagerInterface.queryCPMonth(mContext, serviceId);
	}	
}
